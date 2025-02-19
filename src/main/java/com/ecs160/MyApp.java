package com.ecs160;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.List;

public class MyApp {
    public static void main(String[] args) {
        try {
            String filePath = "src/main/resources/input.json";
            List<Post> posts = JsonLoader.loadPosts(filePath);
            System.out.println("✅ 成功加载 " + posts.size() + " 个帖子！");

            for (Post post : posts.subList(0, Math.min(posts.size(), 20))) {
                System.out.println("😎 " + post);
                if (post.getReplies() != null) {
                    for (Post reply : post.getReplies()) {
                        System.out.println("  ↳ Reply: " + reply);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

