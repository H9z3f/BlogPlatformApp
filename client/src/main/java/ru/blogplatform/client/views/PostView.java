package ru.blogplatform.client.views;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import ru.blogplatform.client.controllers.BlogController;
import ru.blogplatform.client.entities.Post;
import ru.blogplatform.client.entities.PostRating;
import ru.blogplatform.client.entities.User;
import ru.blogplatform.client.models.BlogModel;
import ru.blogplatform.client.responses.PostResponseBody;
import ru.blogplatform.client.responses.RatingResponseBody;
import ru.blogplatform.client.utilities.HTTPUtility;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PostView extends AnchorPane {
    private BlogModel blogModel;
    private User user;
    private Post post;
    private PostRating yourPostRating;
    private Button toggleRatingButton;

    public PostView(BlogModel blogModel, User user, Post post) {
        this.blogModel = blogModel;
        this.user = user;
        this.post = post;

        initialize();
    }

    private void initialize() {
        this.setPrefWidth(350);
        this.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-padding: 10px;");

        for (PostRating postRating : post.getPostRatings()) {
            if (Objects.equals(user.getId(), postRating.getUser().getId())) {
                yourPostRating = postRating;
                break;
            }
        }

        if (Objects.equals(user.getId(), blogModel.getSelectedBlog().getUser().getId())) {
            Button closeButton = new Button("x");
            closeButton.setFont(Font.font("Arial", 14));
            closeButton.setOnAction(actionEvent -> {
                try {
                    deletePost();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            AnchorPane.setTopAnchor(closeButton, 0.0);
            AnchorPane.setRightAnchor(closeButton, 50.0);

            this.getChildren().add(closeButton);
        }

        Label titleLabel = new Label("Title: " + post.getPostTitle());
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(330);
        AnchorPane.setTopAnchor(titleLabel, 5.0);
        AnchorPane.setLeftAnchor(titleLabel, 0.0);

        Label textLabel = new Label(post.getPostText());
        textLabel.setFont(Font.font("Arial", 14));
        textLabel.setWrapText(true);
        textLabel.setMaxWidth(330);
        AnchorPane.setTopAnchor(textLabel, 30.0);
        AnchorPane.setLeftAnchor(textLabel, 0.0);

        toggleRatingButton = new Button("❤ " + post.getPostRatings().size());
        toggleRatingButton.setFont(Font.font("Arial", 14));
        if (yourPostRating != null) {
            toggleRatingButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        }
        toggleRatingButton.setOnAction(actionEvent -> {
            try {
                toggleRating();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        AnchorPane.setTopAnchor(toggleRatingButton, 0.0);
        AnchorPane.setRightAnchor(toggleRatingButton, 0.0);

        this.getChildren().addAll(titleLabel, textLabel, toggleRatingButton);
    }

    private void deletePost() throws Exception {
        String URL = "/post/" + post.getId();
        String responseBody = HTTPUtility.sendDeleteRequest(BlogController.getUserResponseBody().getJWT(), URL);
        ObjectMapper objectMapper = new ObjectMapper();
        PostResponseBody postResponseBody = objectMapper.readValue(responseBody, PostResponseBody.class);

        if (!postResponseBody.isSuccess()) {
            return;
        }

        BlogController.getBlogResponseBody().getPosts().remove(post);

        blogModel.displayPosts();
    }

    private void toggleRating() throws Exception {
        String responseBody;
        if (yourPostRating == null) {
            String URL = "/rating";
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("post_id", String.valueOf(post.getId()));
            responseBody = HTTPUtility.sendPostRequest(BlogController.getUserResponseBody().getJWT(), URL, requestBody);
        } else {
            String URL = "/rating/" + yourPostRating.getId();
            responseBody = HTTPUtility.sendDeleteRequest(BlogController.getUserResponseBody().getJWT(), URL);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        RatingResponseBody ratingResponseBody = objectMapper.readValue(responseBody, RatingResponseBody.class);

        if (!ratingResponseBody.isSuccess()) {
            return;
        }

        if (yourPostRating == null) {
            post.getPostRatings().add(ratingResponseBody.getPostRating());

            yourPostRating = ratingResponseBody.getPostRating();
            toggleRatingButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            toggleRatingButton.setText("❤ " + post.getPostRatings().size());
        } else {
            post.getPostRatings().remove(yourPostRating);

            yourPostRating = null;
            toggleRatingButton.setFont(Font.font("Arial", 14));
            toggleRatingButton.setText("❤ " + post.getPostRatings().size());
        }
    }
}
