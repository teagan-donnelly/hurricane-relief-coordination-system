package com.hurricane_relief_system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    /**
     * Starts the JavaFX app and shows the primary screen.
     * @param stage Main window of the app
     * @throws IOException if the fxml file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Changes the screen that is currently being shown.
     * @param fxml Name of the fxml file to show
     * @throws IOException if the fxml file cannot be loaded
     */
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Loads an fxml file from the resources folder.
     * @param fxml Name of the fxml file without the extension
     * @return The loaded screen
     * @throws IOException if the fxml file cannot be loaded
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Launches the JavaFX app.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch();
    }

}