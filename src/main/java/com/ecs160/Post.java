package com.ecs160;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Post {

    @SerializedName("thread")
    private Thread thread;  // 解析 thread 结构
    @SerializedName("replies")
    private List<Post> replies;  // 存储回复帖子

    public String getPostId() {return (thread != null && thread.getPost() != null) ? thread.getPost().getCid() : null;}
    public String getPostContent() {return (thread != null && thread.post.getRecord() != null) ? formatURL(thread.post.record.getText()): null;}
    private String formatURL(String text) {if (text == null) return null;text = text.replaceAll("[\\n\\r]", " ");return text.replaceAll("(https?://\\S+|\\S+\\.\\S+)", "[link]");}


    public List<Post> getReplies() {return replies;}
    public void setReplies(List<Post> replies) {this.replies = replies;}

    @Override
    public String toString() {
        return "Post{" +
                "postId=" + getPostId() +
                ", postContent='" + getPostContent() + '\'' +
                ", replies=" + (replies != null ? replies.size() : 0) +
                '}';
    }

    // ✅ 静态内部类 Thread
    public static class Thread {
        @SerializedName("post")
        private PostData post;
        @SerializedName("replies")
        private List<Post> replies;

        public PostData getPost() {return post;}
        public List<Post> getReplies() {return replies;}
    }

    // ✅ 静态内部类 PostData（解析 post.cid）
    public static class PostData {
        @SerializedName("cid")
        private String cid;
        @SerializedName("record")
        private Record record;
        public String getCid() {return cid;}
        public Record getRecord() {return record;}
    }

    // ✅ 静态内部类 Record（解析 record.text）
    public static class Record {
        @SerializedName("text")
        private String text;
        public String getText() {return text;}
    }
}