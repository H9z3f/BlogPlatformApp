package ru.blogplatform.client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.blogplatform.client.models.RegistrationModel;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegistrationController {
    private static RegistrationController registrationController;
    private RegistrationModel registrationModel;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField emailField;

    @FXML
    private Label error;

    @FXML
    private TextField fullNameField;

    @FXML
    private PasswordField passwordField;

    public static RegistrationController getRegistrationController() {
        return registrationController;
    }

    @FXML
    void changeWindow(ActionEvent event) throws IOException {
        registrationModel.changeWindow();
    }

    @FXML
    void submitForm(ActionEvent event) throws Exception {
        registrationModel.submitForm();
    }

    @FXML
    void initialize() {
        RegistrationController.registrationController = this;
        registrationModel = new RegistrationModel();
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

    public TextField getEmailField() {
        return emailField;
    }

    public void setEmailField(TextField emailField) {
        this.emailField = emailField;
    }

    public Label getError() {
        return error;
    }

    public void setError(Label error) {
        this.error = error;
    }

    public TextField getFullNameField() {
        return fullNameField;
    }

    public void setFullNameField(TextField fullNameField) {
        this.fullNameField = fullNameField;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public void setPasswordField(PasswordField passwordField) {
        this.passwordField = passwordField;
    }
}
