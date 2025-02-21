package com.ecs160;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class JsonLoader {
    private static final Gson gson = new Gson();

    public static List<Post> loadPosts(String filePath) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            JsonArray feedArray = jsonObject.getAsJsonArray("feed");
            Type listType = new TypeToken<List<Post>>() {}.getType();
            List<Post> posts = gson.fromJson(feedArray, listType);


            /*
            for (int i = 0; i < Math.min(posts.size(), 10); i++) {
                System.out.println("加载 Post ID: " + posts.get(i).getPostId());
            }
            */
            return posts;
        }
    }
}