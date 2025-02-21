package com.ecs160.persistence;

import com.ecs160.Post;
import com.google.gson.Gson;
import redis.clients.jedis.Jedis;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class Session {
    private static Session redisSession;
    private final Jedis jedisSession;
    private final Gson gson;
    private final Map<String, Object> cache;

    private Session() {
        this.jedisSession = new Jedis("localhost", 6379);
        this.gson = new Gson();
        this.cache = new HashMap<>();
    }

    public static  Session getredisSession() {
        if (redisSession == null) {
            redisSession = new Session();
        }

        return redisSession;

    }
    public void add(Object obj) {
        if (obj instanceof Post) {
            ((Post) obj).getPostId();
        }

        try {
            Field idField = getPersistableIdField(obj);
            if (idField == null) {
                return;
            }
            idField.setAccessible(true);
            String postId = (String) idField.get(obj);

            if (postId == null || postId.isEmpty()) {
                return;
            }
            String key = obj.getClass().getSimpleName() + ":" + postId;
            cache.put(key, obj);
            //System.out.println("DEBUG: Cache after adding post -> " + cache);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void persistAll() {

        for (Map.Entry<String, Object> entry : cache.entrySet()) {
            String key = entry.getKey();
            Object obj = entry.getValue();

            if (obj instanceof Post p) {p.getReplyIds();} //Nor sure if this is allowed....
                                                          // but I really spent over 10 hours to findi hits  solution


            jedisSession.hset(key, "data", gson.toJson(obj));

            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(PersistableListField.class)) {
                    try {
                            //System.out.println("This is field.get(obj)" + field.get(obj));
                            //System.out.println("This is replyIds    " + replyIds);
                            //System.out.println("This is ReplyIds       " + replyIds);

                             List<String> replyIdsList = (List<String>) field.get(obj);
                             String replyIds = String.join(",", replyIdsList);

                             jedisSession.hset(key, field.getName(), replyIds);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }

    }



    public Object load(Class<?> clazz, String postId) {

        if (postId == null || clazz == null) {
            return null;
        }

        String key = clazz.getSimpleName() + ":" + postId;
        Map<String, String> redisData = jedisSession.hgetAll(key);
        if (redisData.isEmpty()) {
            return null;
        }

        try {
            return gson.fromJson(redisData.get("data"), clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Field getPersistableIdField(Object obj) {
        for (Field field : obj.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(PersistableId.class)) {
                return field;
            }
        }
        return null;
    }
    private String getObjectId(Object obj) {
        if (obj == null) {
            return "null";
        }
        try {
            Field idField = getPersistableIdField(obj);
            if (idField != null) {
                idField.setAccessible(true);
                String id = (String) idField.get(obj);
                return (id != null) ? id : "null";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "null";
    }


    public void CleanDataBase() {
        jedisSession.flushDB();
    }
}