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
            // 1️⃣ 解析 JSON 为 JsonObject
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

            // 2️⃣ 获取 "feed" 数组
            JsonArray feedArray = jsonObject.getAsJsonArray("feed");

            // 3️⃣ 解析 feed 数组为 List<Post>
            Type listType = new TypeToken<List<Post>>() {}.getType();
            return gson.fromJson(feedArray, listType);
        }
    }
}