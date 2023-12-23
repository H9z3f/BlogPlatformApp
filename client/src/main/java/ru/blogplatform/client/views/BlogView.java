package ru.blogplatform.client.views;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import ru.blogplatform.client.controllers.BlogController;
import ru.blogplatform.client.entities.Blog;
import ru.blogplatform.client.entities.User;
import ru.blogplatform.client.models.BlogModel;
import ru.blogplatform.client.responses.BlogResponseBody;
import ru.blogplatform.client.utilities.HTTPUtility;

import java.util.Objects;

public class BlogView extends AnchorPane {
    private BlogModel blogModel;
    private User user;
    private Blog blog;

    public BlogView(BlogModel blogModel, User user, Blog blog) {
        this.blogModel = blogModel;
        this.user = user;
        this.blog = blog;

        initialize();
    }

    private void initialize() {
        this.setPrefWidth(350);
        this.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-padding: 10px;");

        if (Objects.equals(user.getId(), blog.getUser().getId())) {
            Button closeButton = new Button("x");
            closeButton.setFont(Font.font("Arial", 14));
            closeButton.setOnAction(actionEvent -> {
                try {
                    deleteBlog();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            AnchorPane.setTopAnchor(closeButton, 0.0);
            AnchorPane.setRightAnchor(closeButton, 0.0);

            this.getChildren().add(closeButton);
        }

        Label authorLabel = new Label("Author: " + blog.getUser().getFullName());
        authorLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        authorLabel.setWrapText(true);
        authorLabel.setMaxWidth(330);
        AnchorPane.setTopAnchor(authorLabel, 0.0);
        AnchorPane.setLeftAnchor(authorLabel, 0.0);

        Label titleLabel = new Label("Title: " + blog.getBlogName());
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(330);
        AnchorPane.setTopAnchor(titleLabel, 15.0);
        AnchorPane.setLeftAnchor(titleLabel, 0.0);

        Label descriptionLabel = new Label("Description: " + blog.getBlogDescription());
        descriptionLabel.setFont(Font.font("Arial", 14));
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(330);
        AnchorPane.setTopAnchor(descriptionLabel, 30.0);
        AnchorPane.setLeftAnchor(descriptionLabel, 0.0);

        Label themeLabel = new Label("Theme: " + blog.getBlogTheme());
        themeLabel.setFont(Font.font("Arial", 14));
        themeLabel.setWrapText(true);
        themeLabel.setMaxWidth(330);
        AnchorPane.setTopAnchor(themeLabel, 45.0);
        AnchorPane.setLeftAnchor(themeLabel, 0.0);

        Hyperlink readMoreLink = new Hyperlink("Read more ->");
        readMoreLink.setFont(Font.font("Arial", 14));
        readMoreLink.setOnAction(actionEvent -> {
            try {
                readMore();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        AnchorPane.setTopAnchor(readMoreLink, 60.0);
        AnchorPane.setRightAnchor(readMoreLink, 0.0);

        this.getChildren().addAll(authorLabel, titleLabel, descriptionLabel, themeLabel, readMoreLink);
    }

    private void deleteBlog() throws Exception {
        String URL = "/blog/" + blog.getId();
        String responseBody = HTTPUtility.sendDeleteRequest(BlogController.getUserResponseBody().getJWT(), URL);
        ObjectMapper objectMapper = new ObjectMapper();
        BlogResponseBody blogResponseBody = objectMapper.readValue(responseBody, BlogResponseBody.class);

        if (!blogResponseBody.isSuccess()) {
            return;
        }

        BlogController.getUserResponseBody().getYourBlogs().remove(blog);

        blogModel.setSelectedBlog(null);
        blogModel.displayBlogs();
    }

    private void readMore() throws Exception {
        String URL = "/blog/" + blog.getId();
        String responseBody = HTTPUtility.sendGetRequest(BlogController.getUserResponseBody().getJWT(), URL);
        ObjectMapper objectMapper = new ObjectMapper();
        BlogResponseBody blogResponseBody = objectMapper.readValue(responseBody, BlogResponseBody.class);

        if (!blogResponseBody.isSuccess()) {
            return;
        }

        BlogController.setBlogResponseBody(blogResponseBody);

        blogModel.setSelectedBlog(blog);
        blogModel.displayPosts();
    }
}
