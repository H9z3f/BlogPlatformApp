module ru.blogplatform.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;


    opens ru.blogplatform.client to javafx.fxml;
    exports ru.blogplatform.client;
    exports ru.blogplatform.client.controllers;
    opens ru.blogplatform.client.controllers to javafx.fxml;
    exports ru.blogplatform.client.responses to com.fasterxml.jackson.databind;
    exports ru.blogplatform.client.entities to com.fasterxml.jackson.databind;
}
