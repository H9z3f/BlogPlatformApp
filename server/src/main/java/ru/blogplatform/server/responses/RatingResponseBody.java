package ru.blogplatform.server.responses;

import ru.blogplatform.server.entities.PostRating;

public class RatingResponseBody {
    private boolean success;
    private PostRating postRating;
    private String message;

    public RatingResponseBody(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public RatingResponseBody(boolean success, PostRating postRating) {
        this.success = success;
        this.postRating = postRating;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public PostRating getPostRating() {
        return postRating;
    }

    public void setPostRating(PostRating postRating) {
        this.postRating = postRating;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
