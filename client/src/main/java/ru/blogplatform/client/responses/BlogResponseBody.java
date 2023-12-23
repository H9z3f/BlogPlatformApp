package ru.blogplatform.client.responses;

import ru.blogplatform.client.entities.Blog;
import ru.blogplatform.client.entities.Post;

import java.util.List;

public class BlogResponseBody {
    private boolean success;
    private Blog blog;
    private String message;
    private List<Post> posts;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Blog getBlog() {
        return blog;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
