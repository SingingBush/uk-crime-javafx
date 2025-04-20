module ukcrime {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.slf4j; // was previously slf4j.api;
    requires ch.qos.logback.core;
    requires ch.qos.logback.classic; //only runtime dependency
    requires java.xml; // needed by logback

    requires com.gluonhq.maps;
    requires com.gluonhq.attach.position;
    requires com.gluonhq.attach.lifecycle;
    requires com.gluonhq.attach.util;

    requires police.api;
//    requires java.desktop;
    requires org.jetbrains.annotations;

    opens com.singingbush.ukcrime.controllers to javafx.fxml;
    opens com.singingbush.ukcrime.ui to javafx.fxml;

    exports com.singingbush.ukcrime;
}
