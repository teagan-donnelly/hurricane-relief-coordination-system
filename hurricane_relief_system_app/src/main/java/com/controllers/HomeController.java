package com.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import com.hurricane_relief_system.App;

/**
 * Controller for the primary screen.
 */
public class HomeController {

    /**
     * Switches to the secondary screen.
     * @throws IOException if the secondary screen cannot be loaded
     */
    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("login");
    }
}
