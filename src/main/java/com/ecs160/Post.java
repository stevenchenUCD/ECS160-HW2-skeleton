package com.ecs160;

import com.ecs160.persistence.*;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Persistable
public class Post {

    @PersistableField
    @SerializedName("thread")
    private Thread thread;

    @PersistableField
    @PersistableListField(className = "Post")
    private List<String> replyIds;

    @PersistableId
   private String postId;


    public String getPostId() {
        if (postId == null && thread != null && thread.post != null) {
            postId = thread.post.getCid();
        }
        return postId;
    }

    public String getPostContent() {
        if (thread != null && thread.post != null && thread.post.getRecord() != null) {
            return formatURL(thread.post.getRecord().getText()) ;
        }
        return null;
    }

    public List<Thread> getReplies() {
        if (thread != null && thread.getReplies() != null) {
            return thread.getReplies();
        }
        return List.of();  // 返回空列表，避免 null
    }

    public List<String> getReplyIds() {
        if (getReplies() != null) {
            this.replyIds = getReplies().stream()
                    .map(t -> t.getPost().getCid())
                    .toList();
        } else {
            this.replyIds = List.of();
        }
        return replyIds;
    }

    public void setReplyIds(List<String> replyIds) {
        System.out.println("✅ Debug: setReplyIds 被调用, replyIds = " + replyIds);

        this.replyIds = replyIds;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    private String formatURL(String text) {if (text == null) return null;text = text.replaceAll("[\\n\\r]", " ");return text.replaceAll("(https?://\\S+|\\S+\\.\\S+)", "[link]");}


    @Override
    public String toString() {
        return "Post{" +
                "postId=" + getPostId() +
                ", postContent='" + getPostContent() + '\'' +
                ", replies=" + (getReplies() != null ?
                getReplies().stream().map(t -> t.getPost().getCid()).toList() : "[]")  +
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
        public void setReplies(List<Thread> replies) {
            this.replies = replies;
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