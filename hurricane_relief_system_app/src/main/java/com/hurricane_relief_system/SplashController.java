package com.hurricane_relief_system;

import java.io.IOException;
import javafx.fxml.FXML;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class SplashController {

    @FXML
    private ImageView hurricaneImage;

    @FXML
    private StackPane viewport;

    @FXML
    private Pane designPane;

    @FXML
    private void initialize() {
        designPane.setClip(new Rectangle(600, 400));
        
        // Resize the entire banner proportionally
        NumberBinding scale = Bindings.min(
            viewport.widthProperty().divide(600.0),
            viewport.heightProperty().divide(400.0)
        );

        designPane.scaleXProperty().bind(scale);
        designPane.scaleYProperty().bind(scale);

        // Spin only the hurricane image
        RotateTransition spin =
            new RotateTransition(Duration.seconds(6), hurricaneImage);

        spin.setByAngle(-360);
        spin.setCycleCount(Animation.INDEFINITE);
        spin.setInterpolator(Interpolator.LINEAR);
        spin.play();
    }

}