package com.ecs160;

import com.ecs160.persistence.*;
import redis.clients.jedis.Jedis;
import java.util.List;
import java.util.stream.Collectors;

public class RedisDataBase {
    private static final Jedis redisClient = new Jedis("localhost", 6379);

    // ✅ 存储 `Post`
    public static void savePostToRedis(Post post) {
        if (post == null || post.getPostId() == null) {
            System.out.println("❌ Error: Post or postId is null.");
            return;
        }

        // ✅ 使用 `Session` 存储 `Post`
        Session session = Session.getInstance();
        session.add(post);
        session.persistAll();

        // ✅ 存储 `replies` 的 `postId`
        if (post.getReplies() != null && !post.getReplies().isEmpty()) {
            List<String> replyIds = post.getReplies().stream()
                    .map(thread -> thread.getPost().getCid())  // ✅ 通过 `PostData` 获取 `postId`
                    .collect(Collectors.toList());
            redisClient.set(post.getPostId() + ":replies", String.join(",", replyIds));
        }

        System.out.println("✅ Successfully saved post: " + post.getPostId());
    }

    // ✅ 从 Redis 读取 `Post`
    public static Post loadPostFromRedis(String postId) {
        if (postId == null) {
            System.out.println("❌ Error: postId is null.");
            return null;
        }

        // ✅ 使用 `Session` 读取 `Post`
        Session session = Session.getInstance();
        Post post = (Post) session.load(Post.class, postId);

        if (post == null) {
            System.out.println("❌ Error: Post not found in Redis.");
            return null;
        }

        // ✅ 读取 `replies`

        return post;
    }

}