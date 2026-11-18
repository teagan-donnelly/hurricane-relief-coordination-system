package com.controllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import com.hurricane_relief_system.App;
import com.model.HurricaneReliefApplication;

/**
 * Controller for the login screen.
 */
public class LoginController {

    @FXML
    private TextField txt_username;

    @FXML
    private PasswordField txt_password;

    @FXML
    private TextField txt_password_visible;

    @FXML
    private CheckBox chk_remember_me;

    @FXML
    private CheckBox chk_show_password;

    @FXML
    private Label lbl_error;

    /**
     * Runs when the screen is loaded.
     * Links the hidden and visible password boxes so they always have the same text.
     */
    @FXML
    private void initialize() {
        txt_password_visible.textProperty().bindBidirectional(txt_password.textProperty());
    }

    /**
     * Tries to log the user in when the Sign In button is clicked.
     * @param event The button click
     * @throws IOException if the user home screen cannot be loaded
     */
    @FXML
    private void btnLoginClicked(ActionEvent event) throws IOException {
        String username = txt_username.getText().trim();
        String password = txt_password.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter your username and password.");
            return;
        }

        HurricaneReliefApplication hra = HurricaneReliefApplication.getInstance();

        if (!hra.login(username, password)) {
            showError("Invalid login credentials");
            return;
        }

        App.setRoot("user_home");
    }

    /**
     * Shows or hides the password depending on the show password checkbox.
     */
    @FXML
    private void togglePassword() {
        boolean show = chk_show_password.isSelected();

        txt_password_visible.setVisible(show);
        txt_password_visible.setManaged(show);
        txt_password.setVisible(!show);
        txt_password.setManaged(!show);
    }

    /**
     * Switches back to the home screen.
     * @throws IOException if the home screen cannot be loaded
     */
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("home");
    }

    /**
     * Switches to the resources screen.
     * @throws IOException if the resources screen cannot be loaded
     */
    @FXML
    private void switchToResources() throws IOException {
        App.setRoot("resources");
    }

    /**
     * Goes to the create account screen.
     * @throws IOException if the create account screen cannot be loaded
     */
    @FXML
    private void goToCreateAccount() throws IOException {
        // TODO: uncomment once the create account screen is made
        // App.setRoot("signup");
        System.out.println("Create account screen not made yet.");
    }

    /**
     * Shows an error message above the Sign In button.
     * @param message The message to show
     */
    private void showError(String message) {
        lbl_error.setText(message);
        lbl_error.setVisible(true);
        lbl_error.setManaged(true);
    }
}
