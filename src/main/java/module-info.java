module ukcrime {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.net.http;

    requires org.apache.logging.log4j;
    requires com.fasterxml.jackson.databind; // services
    requires com.fasterxml.jackson.annotation; // for model

    requires slf4j.api;

    opens com.singingbush.ukcrime.controllers to javafx.fxml;
    opens com.singingbush.ukcrime.model to javafx.fxml, com.fasterxml.jackson.databind;

    exports com.singingbush.ukcrime;
}
