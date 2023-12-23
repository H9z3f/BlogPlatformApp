package ru.blogplatform.client.entities;

public class Blog {
    private Long id;
    private User user;
    private String blogName;
    private String blogDescription;
    private String blogTheme;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBlogName() {
        return blogName;
    }

    public void setBlogName(String blogName) {
        this.blogName = blogName;
    }

    public String getBlogDescription() {
        return blogDescription;
    }

    public void setBlogDescription(String blogDescription) {
        this.blogDescription = blogDescription;
    }

    public String getBlogTheme() {
        return blogTheme;
    }

    public void setBlogTheme(String blogTheme) {
        this.blogTheme = blogTheme;
    }
}
