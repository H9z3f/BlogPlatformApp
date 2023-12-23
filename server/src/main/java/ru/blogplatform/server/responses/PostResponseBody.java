package ru.blogplatform.server.responses;

import ru.blogplatform.server.entities.Post;

public class PostResponseBody {
    private boolean success;
    private Post post;
    private String message;

    public PostResponseBody(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public PostResponseBody(boolean success, Post post) {
        this.success = success;
        this.post = post;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
