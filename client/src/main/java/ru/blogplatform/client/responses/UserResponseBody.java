package ru.blogplatform.client.responses;

import ru.blogplatform.client.entities.Blog;
import ru.blogplatform.client.entities.User;

import java.util.List;

public class UserResponseBody {
    private boolean success;
    private User user;
    private String JWT;
    private String message;
    private List<Blog> yourBlogs;
    private List<Blog> blogs;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getJWT() {
        return JWT;
    }

    public void setJWT(String JWT) {
        this.JWT = JWT;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Blog> getYourBlogs() {
        return yourBlogs;
    }

    public void setYourBlogs(List<Blog> yourBlogs) {
        this.yourBlogs = yourBlogs;
    }

    public List<Blog> getBlogs() {
        return blogs;
    }

    public void setBlogs(List<Blog> blogs) {
        this.blogs = blogs;
    }
}
