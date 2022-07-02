module ukcrime {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.slf4j; // was previously slf4j.api;
    requires ch.qos.logback.core;
    requires ch.qos.logback.classic; //only runtime dependency
    requires java.xml; // needed by logback

    requires police.api;

    opens com.singingbush.ukcrime.controllers to javafx.fxml;
    opens com.singingbush.ukcrime.ui to javafx.fxml;

    exports com.singingbush.ukcrime;
}
