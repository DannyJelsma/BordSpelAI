package nl.hanze.bordspelai.listeners;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import nl.hanze.bordspelai.BordspelAI;
import nl.hanze.bordspelai.controllers.LobbyController;
import nl.hanze.bordspelai.enums.GameState;
import nl.hanze.bordspelai.events.NetEventListener;
import nl.hanze.bordspelai.managers.GameManager;
import nl.hanze.bordspelai.managers.SceneManager;
import nl.hanze.bordspelai.notifications.Notification;

public class WinLossListener implements NetEventListener {

    @Override
    public void update(Notification notification) {
        BordspelAI.getThreadPool().submit(() -> Platform.runLater(() -> {
            GameManager manager = GameManager.getInstance();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);

            // TODO: Stuur terug naar de lobby.
            switch (notification.getNotificationType()) {
                case "LOSS":
                    manager.setState(GameState.GAME_LOST);

                    alert.setTitle("You lost the game...");
                    alert.setHeaderText("You lost the game...");
                    alert.setContentText(notification.getDataMap().get("COMMENT"));
                    alert.showAndWait();
                    SceneManager.switchScene("/views/lobby.fxml", new LobbyController(SceneManager.getLobbyModel()));
                    break;
                case "TIE":
                    manager.setState(GameState.GAME_TIE);

                    alert.setTitle("It's a tie.");
                    alert.setHeaderText("It's a tie.");
                    alert.setContentText(notification.getDataMap().get("COMMENT"));
                    alert.showAndWait();
                    SceneManager.switchScene("/views/lobby.fxml", new LobbyController(SceneManager.getLobbyModel()));
                    break;
                case "WIN":
                    manager.setState(GameState.GAME_WON);

                    alert.setTitle("You won the game!");
                    alert.setHeaderText("You won the game!");
                    alert.setContentText(notification.getDataMap().get("COMMENT"));
                    alert.showAndWait();
                    SceneManager.switchScene("/views/lobby.fxml", new LobbyController(SceneManager.getLobbyModel()));
                    break;
            }
        }));
    }
}
