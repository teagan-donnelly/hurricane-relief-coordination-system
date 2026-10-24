package com.hurricane_relief_system;

import java.io.IOException;
import javafx.fxml.FXML;

/**
 * Controller for the primary screen.
 */
public class PrimaryController {

    /**
     * Switches to the secondary screen.
     * @throws IOException if the secondary screen cannot be loaded
     */
    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
