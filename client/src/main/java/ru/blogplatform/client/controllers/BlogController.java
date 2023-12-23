package ru.blogplatform.client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import ru.blogplatform.client.models.BlogModel;
import ru.blogplatform.client.responses.BlogResponseBody;
import ru.blogplatform.client.responses.UserResponseBody;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class BlogController {
    private static BlogController blogController;
    private static UserResponseBody userResponseBody;
    private static BlogResponseBody blogResponseBody;
    private BlogModel blogModel;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private VBox blogField;

    @FXML
    private Label blogNameField;

    @FXML
    private VBox blogsListField;

    @FXML
    private Label fullNameField;

    public static BlogController getBlogController() {
        return blogController;
    }

    public static UserResponseBody getUserResponseBody() {
        return userResponseBody;
    }

    public static void setUserResponseBody(UserResponseBody userResponseBody) {
        BlogController.userResponseBody = userResponseBody;
    }

    public static BlogResponseBody getBlogResponseBody() {
        return blogResponseBody;
    }

    public static void setBlogResponseBody(BlogResponseBody blogResponseBody) {
        BlogController.blogResponseBody = blogResponseBody;
    }

    @FXML
    void changeAccount(ActionEvent event) throws IOException {
        blogModel.changeUser();
    }

    @FXML
    void initialize() {
        BlogController.blogController = this;
        blogModel = new BlogModel();
        blogModel.initialize();
    }

    public ResourceBundle getResources() {
        return resources;
    }

    public void setResources(ResourceBundle resources) {
        this.resources = resources;
    }

    public URL getLocation() {
        return location;
    }

    public void setLocation(URL location) {
        this.location = location;
    }

    public VBox getBlogField() {
        return blogField;
    }

    public void setBlogField(VBox blogField) {
        this.blogField = blogField;
    }

    public Label getBlogNameField() {
        return blogNameField;
    }

    public void setBlogNameField(Label blogNameField) {
        this.blogNameField = blogNameField;
    }

    public VBox getBlogsListField() {
        return blogsListField;
    }

    public void setBlogsListField(VBox blogsListField) {
        this.blogsListField = blogsListField;
    }

    public Label getFullNameField() {
        return fullNameField;
    }

    public void setFullNameField(Label fullNameField) {
        this.fullNameField = fullNameField;
    }
}
