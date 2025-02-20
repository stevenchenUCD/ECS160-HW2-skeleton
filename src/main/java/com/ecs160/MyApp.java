package com.ecs160;


import com.ecs160.persistence.Session;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.List;

public class MyApp {
    public static void main(String[] args) {
        try {
            // ✅ 读取 JSON 文件
            String filePath = "src/main/resources/input.json";
            List<Post> posts = JsonLoader.loadPosts(filePath);
            System.out.println("✅ 成功加载 " + posts.size() + " 个帖子！");

            // ✅ 遍历前 20 个帖子
            for (Post post : posts.subList(0, Math.min(posts.size(), 20))) {
                System.out.println("😎 " + post);

                // ✅ 遍历 replies，确保不会报错
                if (post.getReplies() != null && !post.getReplies().isEmpty()) {
                    for (Post.Thread reply : post.getReplies()) {  // ✅ 修正 `getReplies()` 调用
                        if (reply.getPost() != null) {
                            System.out.println("  ↳ Reply ID: " + reply.getPost().getCid());
                            System.out.println("  ↳ Reply Content: " +
                                    (reply.getPost().getRecord() != null ? reply.getPost().getRecord().getText() : "null"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("❌ 解析 JSON 失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
}
/*
public class MyApp {
    public static void main(String[] args) {
        Session session = Session.getInstance();

        // ✅ 创建 `Post`
        Post post = new Post();
        post.setPostId("123");
        post.setPostContent("Hello, world!");

        // ✅ 存入 Redis
        RedisDataBase.savePostToRedis(post);

        // ✅ 读取 `Post`
        Post loadedPost = RedisDataBase.loadPostFromRedis("123");
        System.out.println("📌 Loaded Post: " + loadedPost);
    }

 */
