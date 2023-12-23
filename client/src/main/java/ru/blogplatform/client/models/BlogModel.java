package ru.blogplatform.client.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.blogplatform.client.App;
import ru.blogplatform.client.controllers.BlogController;
import ru.blogplatform.client.entities.Blog;
import ru.blogplatform.client.entities.Post;
import ru.blogplatform.client.responses.BlogResponseBody;
import ru.blogplatform.client.responses.PostResponseBody;
import ru.blogplatform.client.utilities.HTTPUtility;
import ru.blogplatform.client.views.BlogView;
import ru.blogplatform.client.views.PostView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BlogModel {
    private Stage stage;
    private Stage auxiliaryStage;
    private BlogController blogController;
    private Blog selectedBlog;

    public BlogModel() {
        stage = App.getStage();
        auxiliaryStage = new Stage();
        blogController = BlogController.getBlogController();
    }

    public void initialize() {
        blogController.getFullNameField().setText(BlogController.getUserResponseBody().getUser().getFullName());

        displayBlogs();
    }

    public void changeUser() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void clearBlogs() {
        blogController.getBlogsListField().getChildren().clear();

        Button button = new Button("Create a new blog");
        button.setPrefWidth(350);
        button.setFont(Font.font("Arial", 14));
        button.setOnAction(actionEvent -> {
            createBlogCreationForm();
        });

        blogController.getBlogsListField().getChildren().add(button);
    }

    public void displayBlogs() {
        selectedBlog = null;

        clearBlogs();
        clearPosts(false);

        Label yourBlogs = new Label("Your blogs: " + BlogController.getUserResponseBody().getYourBlogs().size());
        yourBlogs.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        blogController.getBlogsListField().getChildren().add(yourBlogs);

        for (Blog blog : BlogController.getUserResponseBody().getYourBlogs()) {
            blogController.getBlogsListField().getChildren().add(new BlogView(this, BlogController.getUserResponseBody().getUser(), blog));
        }

        Label otherBlogs = new Label("Other blogs: " + BlogController.getUserResponseBody().getBlogs().size());
        otherBlogs.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        blogController.getBlogsListField().getChildren().add(otherBlogs);

        for (Blog blog : BlogController.getUserResponseBody().getBlogs()) {
            blogController.getBlogsListField().getChildren().add(new BlogView(this, BlogController.getUserResponseBody().getUser(), blog));
        }
    }

    private void clearPosts(boolean personal) {
        blogController.getBlogField().getChildren().clear();
        blogController.getBlogNameField().setText("Select blog");

        if (!personal) {
            return;
        }

        Button button = new Button("Create a new post");
        button.setPrefWidth(350);
        button.setFont(Font.font("Arial", 14));
        button.setOnAction(actionEvent -> {
            createPostCreationForm();
        });

        blogController.getBlogField().getChildren().add(button);
    }

    public void displayPosts() {
        clearPosts(Objects.equals(BlogController.getUserResponseBody().getUser().getId(), selectedBlog.getUser().getId()));

        blogController.getBlogNameField().setText(selectedBlog.getBlogName());

        Label yourBlogs = new Label("Posts: " + BlogController.getBlogResponseBody().getPosts().size());
        yourBlogs.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        blogController.getBlogField().getChildren().add(yourBlogs);

        for (Post post : BlogController.getBlogResponseBody().getPosts()) {
            blogController.getBlogField().getChildren().add(new PostView(this, BlogController.getUserResponseBody().getUser(), post));
        }
    }

    private void createBlogCreationForm() {
        auxiliaryStage = new Stage();
        auxiliaryStage.initModality(Modality.APPLICATION_MODAL);
        auxiliaryStage.initOwner(stage);
        auxiliaryStage.setTitle("Fill out the form");

        Label titleLabel = new Label("Enter your blog title:");
        titleLabel.setFont(Font.font("Arial", 14));

        TextField titleTextField = new TextField();
        titleTextField.setPrefWidth(300);
        titleTextField.setFont(Font.font("Arial", 14));

        Label descriptionLabel = new Label("Enter your blog description:");
        descriptionLabel.setFont(Font.font("Arial", 14));

        TextField descriptionTextField = new TextField();
        descriptionTextField.setPrefWidth(300);
        descriptionTextField.setFont(Font.font("Arial", 14));

        Label themeLabel = new Label("Enter your blog theme:");
        themeLabel.setFont(Font.font("Arial", 14));

        TextField themeTextField = new TextField();
        themeTextField.setPrefWidth(300);
        themeTextField.setFont(Font.font("Arial", 14));

        Label errorLabel = new Label();
        errorLabel.setFont(Font.font("Arial", 14));
        errorLabel.setTextFill(Color.RED);

        Button submitButton = new Button("Create blog");
        submitButton.setPrefWidth(300);
        submitButton.setFont(Font.font("Arial", 14));
        submitButton.setOnAction(actionEvent -> {
            String URL = "/blog";
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("blog_name", titleTextField.getText());
            requestBody.put("blog_description", descriptionTextField.getText());
            requestBody.put("blog_theme", themeTextField.getText());
            String responseBody;
            try {
                responseBody = HTTPUtility.sendPostRequest(BlogController.getUserResponseBody().getJWT(), URL, requestBody);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            BlogResponseBody blogResponseBody;
            try {
                blogResponseBody = objectMapper.readValue(responseBody, BlogResponseBody.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            if (!blogResponseBody.isSuccess()) {
                errorLabel.setText(blogResponseBody.getMessage());
                return;
            }

            destroyCreationForm();
            BlogController.getUserResponseBody().getYourBlogs().add(blogResponseBody.getBlog());
            displayBlogs();
        });

        VBox dialogVbox = new VBox(10);
        dialogVbox.getChildren().addAll(titleLabel, titleTextField, descriptionLabel, descriptionTextField, themeLabel, themeTextField, errorLabel, submitButton);
        dialogVbox.setPadding(new Insets(20));

        Scene dialogScene = new Scene(dialogVbox);

        auxiliaryStage.setScene(dialogScene);
        auxiliaryStage.showAndWait();
    }

    private void createPostCreationForm() {
        auxiliaryStage = new Stage();
        auxiliaryStage.initModality(Modality.APPLICATION_MODAL);
        auxiliaryStage.initOwner(stage);
        auxiliaryStage.setTitle("Fill out the form");

        Label titleLabel = new Label("Enter your post title:");
        titleLabel.setFont(Font.font("Arial", 14));

        TextField titleTextField = new TextField();
        titleTextField.setPrefWidth(300);
        titleTextField.setFont(Font.font("Arial", 14));

        Label textLabel = new Label("Enter your post text:");
        textLabel.setFont(Font.font("Arial", 14));

        TextField textTextField = new TextField();
        textTextField.setPrefWidth(300);
        textTextField.setFont(Font.font("Arial", 14));

        Label errorLabel = new Label();
        errorLabel.setFont(Font.font("Arial", 14));
        errorLabel.setTextFill(Color.RED);

        Button submitButton = new Button("Create post");
        submitButton.setPrefWidth(300);
        submitButton.setFont(Font.font("Arial", 14));
        submitButton.setOnAction(actionEvent -> {
            String URL = "/post";
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("blog_id", String.valueOf(selectedBlog.getId()));
            requestBody.put("post_title", titleTextField.getText());
            requestBody.put("post_text", textTextField.getText());
            String responseBody;
            try {
                responseBody = HTTPUtility.sendPostRequest(BlogController.getUserResponseBody().getJWT(), URL, requestBody);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            PostResponseBody postResponseBody;
            try {
                postResponseBody = objectMapper.readValue(responseBody, PostResponseBody.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            if (!postResponseBody.isSuccess()) {
                errorLabel.setText(postResponseBody.getMessage());
                return;
            }

            destroyCreationForm();
            BlogController.getBlogResponseBody().getPosts().add(postResponseBody.getPost());
            displayPosts();
        });

        VBox dialogVbox = new VBox(10);
        dialogVbox.getChildren().addAll(titleLabel, titleTextField, textLabel, textTextField, errorLabel, submitButton);
        dialogVbox.setPadding(new Insets(20));

        Scene dialogScene = new Scene(dialogVbox);

        auxiliaryStage.setScene(dialogScene);
        auxiliaryStage.showAndWait();
    }

    private void destroyCreationForm() {
        auxiliaryStage.close();
    }

    public Blog getSelectedBlog() {
        return selectedBlog;
    }

    public void setSelectedBlog(Blog selectedBlog) {
        this.selectedBlog = selectedBlog;
    }
}
