package com.singingbush.ukcrime;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class AppMain extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        final Scene scene = new Scene(loadFXML("primary"), 900, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private Parent loadFXML(final String fxml) throws IOException {
        final URL resource = this.getClass().getResource("/layouts/" + fxml + ".fxml");
        //final URL resource = getClass().getModule().getClassLoader().getResource("/layouts/" + fxml + ".fxml");
        //final URL resource = getClass().getClassLoader().getResource("/layouts/" + fxml + ".fxml");
        final FXMLLoader fxmlLoader = new FXMLLoader(resource);
        return fxmlLoader.load();
    }

}
