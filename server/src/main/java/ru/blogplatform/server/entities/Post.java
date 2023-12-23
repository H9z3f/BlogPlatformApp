package ru.blogplatform.server.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id", nullable = false)
    @JsonIgnore
    private Blog blog;

    @Column(name = "post_title", nullable = false)
    private String postTitle;

    @Column(name = "post_text", nullable = false, columnDefinition = "TEXT")
    private String postText;

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER)
    private List<PostRating> postRatings;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Blog getBlog() {
        return blog;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public List<PostRating> getPostRatings() {
        return postRatings;
    }

    public void setPostRatings(List<PostRating> postRatings) {
        this.postRatings = postRatings;
    }
}
