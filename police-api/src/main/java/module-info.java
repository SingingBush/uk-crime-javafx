module police.api {

    requires javafx.fxml;
    requires java.net.http;
    requires org.slf4j; // was previously slf4j.api;
    requires tools.jackson.core; // needed for services.UkPoliceApi
    requires tools.jackson.databind; // needed for services.UkPoliceApi
    requires com.fasterxml.jackson.annotation; // for model

    requires static org.jetbrains.annotations;

    opens com.singingbush.ukcrime.model to javafx.fxml, tools.jackson.databind;

    exports com.singingbush.ukcrime.services;
    exports com.singingbush.ukcrime.model;
}
