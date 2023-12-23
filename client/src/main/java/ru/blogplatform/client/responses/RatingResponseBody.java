package ru.blogplatform.client.responses;

import ru.blogplatform.client.entities.PostRating;

public class RatingResponseBody {
    private boolean success;
    private PostRating postRating;
    private String message;

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
