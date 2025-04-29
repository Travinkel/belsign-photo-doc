package presentation.controller;


import com.belman.belsign.application.viewmodel.SplashViewModel;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class SplashController {
    private final SplashViewModel splashViewModel;

    @FXML
    private StackPane splashRoot;

    @FXML
    private Label messageLabel;

    public SplashController(SplashViewModel splashViewModel) {
        this.splashViewModel = splashViewModel;
    }

    @FXML
    public void initialize() {
        messageLabel.textProperty().bind(splashViewModel.messageProperty());

        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(event -> splashViewModel.onShow());
        delay.play();
    }
}
