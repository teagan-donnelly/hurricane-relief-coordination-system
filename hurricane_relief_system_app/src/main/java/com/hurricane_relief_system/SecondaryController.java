package com.hurricane_relief_system;

import java.io.IOException;
import javafx.fxml.FXML;

/**
 * Controller for the secondary screen.
 */
public class SecondaryController {

    /**
     * Switches back to the primary screen.
     * @throws IOException if the primary screen cannot be loaded
     */
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}