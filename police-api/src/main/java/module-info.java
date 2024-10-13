module police.api {

    requires javafx.fxml;
    requires java.net.http;
    requires org.slf4j; // was previously slf4j.api;
    requires com.fasterxml.jackson.annotation; // for model
    requires com.fasterxml.jackson.databind; // services

    requires static org.jetbrains.annotations;

    opens com.singingbush.ukcrime.model to javafx.fxml, com.fasterxml.jackson.databind;

    exports com.singingbush.ukcrime.services;
    exports com.singingbush.ukcrime.model;
}
