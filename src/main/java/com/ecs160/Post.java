package com.ecs160;

import com.ecs160.persistence.*;
import com.google.gson.annotations.SerializedName;
import java.util.List;

@Persistable
public class Post {

    @PersistableField
    @SerializedName("thread")
    private Thread thread;

    public String getPostId() {
        return (thread != null && thread.post != null) ? thread.post.getCid() : null;
    }

    public String getPostContent() {
        if (thread != null && thread.post != null && thread.post.getRecord() != null) {
            return formatURL(thread.post.getRecord().getText()) ;
        }
        return null;
    }
    private String formatURL(String text) {if (text == null) return null;text = text.replaceAll("[\\n\\r]", " ");return text.replaceAll("(https?://\\S+|\\S+\\.\\S+)", "[link]");}
    // ✅ 直接返回 `thread.replies`，不涉及数据库、不做懒加载
    public List<Thread> getReplies() {
        return (thread != null) ? thread.getReplies() : null;
    }

    @Override
    public String toString() {
        return "Post{" +
                "postId=" + getPostId() +
                ", postContent='" + getPostContent() + '\'' +
                ", replies=" + (getReplies() != null ? getReplies().size() : 0) +
                '}';
    }


    public static class Thread {
        @PersistableField
        @SerializedName("post")
        private PostData post;

        @PersistableListField(className = "Post")
        @SerializedName("replies")
        private List<Thread> replies;

        public PostData getPost() {
            return post;
        }

        public List<Thread> getReplies() {
            return replies;
        }
    }


    public static class PostData {
        @SerializedName("cid")
        private String cid;

        @SerializedName("record")
        private Record record;

        public String getCid() {
            return cid;
        }

        public Record getRecord() {
            return record;
        }
    }

    public static class Record {
        @SerializedName("text")
        private String text;

        public String getText() {
            return text;
        }
    }
}