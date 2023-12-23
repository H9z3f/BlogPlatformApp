package ru.blogplatform.client.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.blogplatform.client.App;
import ru.blogplatform.client.controllers.BlogController;
import ru.blogplatform.client.controllers.LoginController;
import ru.blogplatform.client.responses.UserResponseBody;
import ru.blogplatform.client.utilities.HTTPUtility;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginModel {
    private Stage stage;
    private LoginController loginController;
    private ObjectMapper objectMapper;

    public LoginModel() {
        stage = App.getStage();
        loginController = LoginController.getLoginController();
        objectMapper = new ObjectMapper();
    }

    public void changeWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("registration-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Registration");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public void submitForm() throws Exception {
        String URL = "/user/auth";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", loginController.getEmailField().getText());
        requestBody.put("password", loginController.getPasswordField().getText());
        String responseBody = HTTPUtility.sendPostRequest(null, URL, requestBody);
        UserResponseBody userResponseBody = objectMapper.readValue(responseBody, UserResponseBody.class);

        if (!userResponseBody.isSuccess()) {
            loginController.getError().setText(userResponseBody.getMessage());
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
