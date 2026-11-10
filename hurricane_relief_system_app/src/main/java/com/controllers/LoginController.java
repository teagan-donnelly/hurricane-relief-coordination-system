package com.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import com.hurricane_relief_system.App;

/**
 * Controller for the secondary screen.
 */
public class LoginController {

    /**
     * Switches back to the primary screen.
     * @throws IOException if the primary screen cannot be loaded
     */
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("home");
    }
}