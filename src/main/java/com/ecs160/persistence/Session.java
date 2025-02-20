package com.ecs160.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class Session {
    private static final Session instance = new Session();
    private static final Gson gson = new GsonBuilder().create();
    private final Jedis jedisSession;
    private final Map<String, Object> cache = new HashMap<>();  // 临时缓存

    // ✅ 私有构造函数（单例模式）
    private Session() {
        this.jedisSession = new Jedis("localhost", 6379);
    }

    // ✅ 获取单例实例
    public static Session getInstance() {
        return instance;
    }

    // ✅ 添加对象到缓存
    public void add(Object obj) {
        if (obj == null) {
            System.out.println("❌ Error: Cannot add null object.");
            return;
        }
        String key = obj.getClass().getSimpleName() + ":" + getObjectId(obj);
        cache.put(key, obj);
    }

    // ✅ 持久化所有对象
    public void persistAll() {
        for (Map.Entry<String, Object> entry : cache.entrySet()) {
            String key = entry.getKey();
            Object obj = entry.getValue();
            Map<String, String> redisData = new HashMap<>();

            for (Field field : obj.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(PersistableField.class) || field.isAnnotationPresent(PersistableId.class)) {
                    field.setAccessible(true);
                    try {
                        redisData.put(field.getName(), gson.toJson(field.get(obj)));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("❌ Error: Cannot access field " + field.getName(), e);
                    }
                } else if (field.isAnnotationPresent(PersistableListField.class)) {
                    field.setAccessible(true);
                    try {
                        List<?> list = (List<?>) field.get(obj);
                        List<String> ids = list.stream()
                                .map(this::getObjectId)
                                .collect(Collectors.toList());
                        redisData.put(field.getName(), gson.toJson(ids));
                    } catch (Exception e) {
                        throw new RuntimeException("❌ Error: Cannot process List field " + field.getName(), e);
                    }
                }
            }

            jedisSession.hmset(key, redisData);
            System.out.println("✅ Persisted object: " + key);
        }
        cache.clear();
    }

    // ✅ 读取对象
    public Object load(Class<?> clazz, String postId) {
        if (postId == null || clazz == null) {
            System.out.println("❌ Error: postId or class type is null.");
            return null;
        }

        String key = clazz.getSimpleName() + ":" + postId;
        Map<String, String> redisData = jedisSession.hgetAll(key);

        if (redisData.isEmpty()) {
            System.out.println("❌ Error: No data found for key: " + key);
            return null;
        }

        try {
            Object obj = clazz.getDeclaredConstructor().newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                if (redisData.containsKey(field.getName())) {
                    field.setAccessible(true);
                    Object value = gson.fromJson(redisData.get(field.getName()), field.getType());
                    field.set(obj, value);
                }
            }
            System.out.println("✅ Successfully loaded object: " + key);
            return obj;
        } catch (Exception e) {
            throw new RuntimeException("❌ Error: Failed to deserialize object", e);
        }
    }

    // ✅ 获取对象的唯一 ID
    private String getObjectId(Object obj) {
        try {
            for (Field field : obj.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(PersistableId.class)) {
                    field.setAccessible(true);
                    return (String) field.get(obj);
                }
            }
            throw new RuntimeException("❌ Error: No field marked with @PersistableId found.");
        } catch (Exception e) {
            throw new RuntimeException("❌ Error: Cannot access @PersistableId field", e);
        }
    }
}