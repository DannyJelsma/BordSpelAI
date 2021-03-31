package nl.hanze.bordspelai.listeners;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import nl.hanze.bordspelai.BordspelAI;
import nl.hanze.bordspelai.enums.Command;
import nl.hanze.bordspelai.enums.GameState;
import nl.hanze.bordspelai.events.NetEventListener;
import nl.hanze.bordspelai.managers.GameManager;
import nl.hanze.bordspelai.notifications.Notification;

import java.util.Map;
import java.util.Optional;

public class ChallengeReceiveListener implements NetEventListener {

    @Override
    public void update(Notification notification) {
        GameManager manager = GameManager.getInstance();

        if (manager.getState().equals(GameState.LOBBY) && notification.getNotificationType().equals("CHALLENGE")) {
            if (notification.isMap()) {
                BordspelAI.getThreadPool().submit(() -> Platform.runLater(() -> {
                    Map<String, String> data = notification.getDataMap();
                    String challenger = data.get("CHALLENGER");
                    String challengeNumber = data.get("CHALLENGENUMBER");
                    String gameType = data.get("GAMETYPE");

                    ButtonType yesButton = new ButtonType("Yes");
                    ButtonType noButton = new ButtonType("No");

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Challenge received.");
                    alert.setHeaderText(null);
                    alert.setContentText(challenger + " has challenged you for a game of " + gameType + ". Do you want to accept the challenge?");
                    alert.getButtonTypes().setAll(yesButton, noButton);
                    Optional<ButtonType> buttonResult = alert.showAndWait();

                    if (buttonResult.isPresent()) {
                        if (buttonResult.get().equals(yesButton)) {
                            if (!BordspelAI.getServer().sendCommand(Command.CHALLENGE_ACCEPT, challengeNumber)) {
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);

                                errorAlert.setContentText(BordspelAI.getServer().getLastError());
                                errorAlert.show();
                            }
                        }
                    }
                }));
            }
        }
    }
}
