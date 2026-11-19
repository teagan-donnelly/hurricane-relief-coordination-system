package com.hurricane_relief_system;

// package com.hurricane_relief_system;
// import javafx.animation.PauseTransition;
// import javafx.application.HostServices;
// import javafx.util.Duration;
// package com.movement;
// import com.location.Address;
// import java.io.IOException;
// import java.io.IOException;
// import com.controllers.MapController;

import com.controllers.MapController;
import com.location.Address;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/** map display test */
public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                App.class.getResource("map_view.fxml")
        );
        Parent root = loader.load();

        Address userAddress = new Address(
                "1 Vendue Range", "Charleston", "SC", "29401"
        );
        Address shelterAddress = new Address(
                "2950 Carner Avenue", "North Charleston", "SC", "29405"
        );

        stage.setScene(new Scene(root, 900, 650));
        stage.setTitle("Hurricane Relief Map");
        stage.show();

        MapController controller = loader.getController();
        controller.showRouteOnMap(userAddress, shelterAddress);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
/** commented application startup */
/*
private static Scene scene;
    private static HostServices hostServices;

    /**
     * Starts the JavaFX app and shows the primary screen.
     * @param stage Main window of the app
     * @throws IOException if the fxml file cannot be loaded
     *\/
    @Override
    public void start(Stage stage) throws IOException {
        hostServices = getHostServices();
        scene = new Scene(loadFXML("splash"), 1440, 900);
        stage.setScene(scene);
        stage.show();

        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished(event -> {
            try {
                setRoot("login");
            } catch (IOException e){
                e.printStackTrace();
            }
        });

        delay.play();
    }

    /**
     * Changes the screen that is currently being shown.
     * @param fxml Name of the fxml file to show
     * @throws IOException if the fxml file cannot be loaded
     *\/
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Opens a web page in the user's default browser.
     * @param url Address of the page to open
     *\/
    public static void openWebPage(String url) {
        if (hostServices != null) {
            hostServices.showDocument(url);
        }
    }

    /**
     * Loads an fxml file from the resources folder.
     * @param fxml Name of the fxml file without the extension
     * @return The loaded screen
     * @throws IOException if the fxml file cannot be loaded
     *\/
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Launches the JavaFX app.
     * @param args Command line arguments
     *\/
    public static void main(String[] args) {
        launch();
    }
    */
