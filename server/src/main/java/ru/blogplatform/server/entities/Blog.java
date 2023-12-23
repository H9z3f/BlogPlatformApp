package ru.blogplatform.server.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Blogs")
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "blog_name", nullable = false)
    private String blogName;

    @Column(name = "blog_description", nullable = false, columnDefinition = "TEXT")
    private String blogDescription;

    @Column(name = "blog_theme", nullable = false, length = 100)
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
