package com.controllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import com.hurricane_relief_system.App;
import com.model.HurricaneReliefApplication;

//import model.*;

/**
 * Controller for the secondary screen.
 */
public class LoginController {

    @FXML 
    private TextField txt_username;

    @FXML 
    private TextField txt_password;

    @FXML 
    private Label lbl_error;

    @FXML 
    private void btnLoginClicked(ActionEvent event) throws IOException {
        String username = txt_username.getText();
        String password = txt_password.getText();

        HurricaneReliefApplication hra = HurricaneReliefApplication.getInstance();

        if (!hra.login(username, password)) {
            lbl_error.setText("Invalid login credentials");
            return;
        }

        App.setRoot("user_home");
    }

    /**
     * Switches back to the primary screen.
     * @throws IOException if the primary screen cannot be loaded
     */
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("home");
    }

    @FXML 
    void login(ActionEvent event) {
        String username = txt_username.getText();
        String password = txt_password.getText();
        System.out.println("Your name is " + username + " pass " + password);

    }
}