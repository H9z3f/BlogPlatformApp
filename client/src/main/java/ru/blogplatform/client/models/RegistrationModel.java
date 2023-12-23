package ru.blogplatform.client.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.blogplatform.client.App;
import ru.blogplatform.client.controllers.BlogController;
import ru.blogplatform.client.controllers.RegistrationController;
import ru.blogplatform.client.responses.UserResponseBody;
import ru.blogplatform.client.utilities.HTTPUtility;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegistrationModel {
    private Stage stage;
    private RegistrationController registrationController;
    private ObjectMapper objectMapper;

    public RegistrationModel() {
        stage = App.getStage();
        registrationController = RegistrationController.getRegistrationController();
        objectMapper = new ObjectMapper();
    }

    public void changeWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public void submitForm() throws Exception {
        String URL = "/user/register";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("full_name", registrationController.getFullNameField().getText());
        requestBody.put("email", registrationController.getEmailField().getText());
        requestBody.put("password", registrationController.getPasswordField().getText());
        String responseBody = HTTPUtility.sendPostRequest(null, URL, requestBody);
        UserResponseBody userResponseBody = objectMapper.readValue(responseBody, UserResponseBody.class);

        if (!userResponseBody.isSuccess()) {
            registrationController.getError().setText(userResponseBody.getMessage());
            return;
        }

        BlogController.setUserResponseBody(userResponseBody);

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("blog-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Blog");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
