module ukcrime {
    requires javafx.controls;
    requires javafx.fxml;
    requires jericho.html;
    requires org.slf4j; // was previously slf4j.api;
    requires org.apache.logging.log4j;

    requires police.api;

    opens com.singingbush.ukcrime.controllers to javafx.fxml;
    opens com.singingbush.ukcrime.ui to javafx.fxml;

    exports com.singingbush.ukcrime;
}
