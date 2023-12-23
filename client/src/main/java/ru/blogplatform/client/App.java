package ru.blogplatform.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    private static Stage stage;

    public static void main(String[] args) {
        launch();
    }

    public static Stage getStage() {
        return stage;
    }

    @Override
    public void start(Stage stage) throws IOException {
        App.stage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
