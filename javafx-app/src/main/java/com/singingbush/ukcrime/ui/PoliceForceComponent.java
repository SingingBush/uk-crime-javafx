package com.singingbush.ukcrime.ui;

import com.singingbush.ukcrime.model.PoliceForce;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.net.URL;

public class PoliceForceComponent extends VBox {

    @FXML
    private Label nameLabel;

    @FXML
    private WebView descriptionHtml;

    @FXML
    private Hyperlink phone;

    @FXML
    private Hyperlink url;

    public PoliceForceComponent() {
        final URL resource = this.getClass().getResource("/components/police_force.fxml"); // works running from IDEA
        final FXMLLoader fxmlLoader = new FXMLLoader(resource);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setPoliceForce(final PoliceForce policeForce) {
        this.nameLabel.setText(policeForce.getName());

        this.descriptionHtml.getEngine().loadContent(policeForce.getDescription());

        final String phoneUrl = policeForce.getTelephone() != null && !policeForce.getTelephone().isEmpty() ? String.format("tel:%s", policeForce.getTelephone().trim()) : null;
        phone.setText(phoneUrl);

        this.url.setText(policeForce.getUrl());
    }
}
