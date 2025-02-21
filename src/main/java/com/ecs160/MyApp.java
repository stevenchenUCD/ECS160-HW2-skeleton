package com.ecs160;


import com.ecs160.persistence.Session;
import java.util.Scanner;
import java.util.List;

public class MyApp {
    public static void main(String[] args) {


        Session.getredisSession().CleanDataBase();

        try {
            String filePath = "src/main/resources/input.json";
            List<Post> posts = JsonLoader.loadPosts(filePath);
            int count = 0;
            Session session = Session.getredisSession();
            for (Post post : posts) {

                session.add(post);
                //TestIfGotPostsFromJson(count,post);
            }
            session.persistAll();

            //PrintFist10Posts(posts);

            UserInput();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void UserInput() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("\nPlease Enter Post ID (or type 'exit' to quit): ");
            String inputPostId = scanner.nextLine().trim();

            if (inputPostId.equalsIgnoreCase("exit")) {
                System.out.println("Exiting UserInput mode...");
                break;
            }

            // 加载 Post
            Post post = (Post) Session.getredisSession().load(Post.class, inputPostId);

            if (post != null) {
                // 成功查询到 Post
                System.out.println("\nLoad Post Successfully...");
                System.out.println("Post Content: " + post.getPostContent());
                System.out.println("Reply IDs:    " + (post.getReplyIds() != null ? post.getReplyIds() : "[]"));
                List<String> replyTexts = post.getReplyTexts();
                if (!replyTexts.isEmpty()) {
                    System.out.println("  Replies:");
                    for (String replyText : replyTexts) {
                        System.out.println("            " + replyText);
                    }
                } else {
                    System.out.println("     No Replies Found.");
                }
            } else {
                System.out.println("Post Not Found. Please try again.");
            }
        }
        scanner.close();
    }

    public  static void TestIfGotPostsFromJson(int count, Post post) {
        if (count <=20) {
            System.out.println("\n========== DEBUG: Loading Posts =========="+ "    Number Of   "+ count);
            System.out.println("\uD83D\uDCCC" + post);
            System.out.println("✅ Debug: if have replies In the Posts：  " + post.getReplyIds());
        }
    }

    public static void PrintFist10Posts(List<Post> posts) {
        int J= 0;
        for (int i = 0; i < Math.min(posts.size(), 2); i++) {
            String postId = posts.get(i).getPostId();

            Post loadedPost = (Post) Session.getredisSession().load(Post.class, postId);
            if (loadedPost != null) {

                System.out.println("\n========== DEBUG: Loading Posts From Redis ==========");

                System.out.println("✅ Post ID:              " + loadedPost.getPostId());
                System.out.println("✅ Post Content:         " + loadedPost.getPostContent());
                System.out.println("✅ Reply IDs from Redis: " +
                        (loadedPost.getReplyIds() != null ? loadedPost.getReplyIds() : "[]"));


                //System.out.println("\uD83D\uDCCC" + loadedPost.getReplyTexts());
            }
        }
    }

}