package com.ecs160;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Persistable
public class Post {

    @PersistableId
    @SerializedName("cid")
    private String postId;

    @PersistableField
    @SerializedName("post")
    private PostData post;  // ✅ `record` 现在在 `post` 里面

    @PersistableField
    @SerializedName("thread")
    private Thread thread;  // ✅ `replies` 仍然在 `thread` 里

    @LazyLoad
    @PersistableListField(className = "Post")
    @SerializedName("replyIds")
    private List<String> replyIds;

    private transient List<Post> replies;

    public String getPostId() {
        return (post != null) ? post.getCid() : null;
    }

    // ✅ `record` 现在在 `post` 里
    public String getPostContent() {
        if (post != null && post.getRecord() != null) {
            return maskUrls(post.getRecord().getText());
        }
        return null;
    }

    private String maskUrls(String text) {
        if (text == null) return null;
        return text.replaceAll("(https?://\\S+|\\S+\\.\\S+)", "[link]");
    }

    // ✅ 解析 `thread.replies`
    public List<Post> getReplies() {
        if (replies == null && thread != null && thread.getReplies() != null) {
            replies = thread.getReplies();
        }
        return replies;
    }

    public void setReplies(List<Post> replies) {
        if (replies != null) {
            this.replies = replies;
            this.replyIds = replies.stream().map(Post::getPostId).toList();
        }
    }

    @Override
    public String toString() {
        return "Post{" +
                "postId=" + getPostId() +
                ", postContent='" + getPostContent() + '\'' +
                ", replies=" + (replyIds != null ? replyIds : "[]") +
                '}';
    }

    // ✅ `Thread` 仍然保留，因为 `replies` 在 `thread` 里
    public static class Thread {
        @SerializedName("post")
        private PostData post;

        @SerializedName("replies")
        private List<Post> replies; // ✅ `replies` 仍然在 `thread` 里

        public PostData getPost() {
            return post;
        }

        public List<Post> getReplies() {
            return replies;
        }
    }

    public static class PostData {
        @SerializedName("cid")
        private String cid;

        @SerializedName("record")
        private Record record; // ✅ `record` 现在在 `post` 里

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