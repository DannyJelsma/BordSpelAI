package nl.hanze.bordspelai.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import nl.hanze.bordspelai.BordspelAI;
import nl.hanze.bordspelai.enums.Command;
import nl.hanze.bordspelai.enums.Game;
import nl.hanze.bordspelai.enums.GameState;
import nl.hanze.bordspelai.managers.GameManager;
import nl.hanze.bordspelai.models.LobbyModel;
import nl.hanze.bordspelai.net.Server;

import java.util.Optional;

public class LobbyController implements Controller {

    private final LobbyModel model;

    @FXML
    public Button challengeButton;

    @FXML
    public Button refreshButton;

    @FXML
    public ListView<String> playerList;

    public LobbyController(LobbyModel model) {
        this.model = model;
    }

    @FXML
    public void initialize() {
        GameManager manager = GameManager.getInstance();
        Server server = BordspelAI.getServer();

        server.sendCommand(Command.GET_PLAYERLIST);
        manager.setState(GameState.LOBBY);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        BordspelAI.getThreadPool().submit(() -> Platform.runLater(this::updatePlayerList));
    }

    @FXML
    public void refresh() {
        Server server = BordspelAI.getServer();

        server.sendCommand(Command.GET_PLAYERLIST);
        BordspelAI.getThreadPool().submit(() -> Platform.runLater(this::updatePlayerList));
    }

    @FXML
    public void challengePlayer() {
        GameManager manager = GameManager.getInstance();
        String playerToChallenge = playerList.getSelectionModel().getSelectedItem();

        BordspelAI.getThreadPool().submit(() -> Platform.runLater(() -> {
            if (playerToChallenge.equalsIgnoreCase(manager.getUsername() + " (You)")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);

                alert.setTitle("Oops!");
                alert.setHeaderText(null);
                alert.setContentText("You can't challenge yourself...");
                alert.show();
            } else {
                ButtonType reversiButton = new ButtonType("Othello/Reversi");
                ButtonType tttButton = new ButtonType("Tic-tac-toe");
                Alert gameSelectAlert = new Alert(Alert.AlertType.NONE);
                gameSelectAlert.setTitle("Game selection");
                gameSelectAlert.setHeaderText(null);
                gameSelectAlert.setContentText("What game do you want to play?");
                gameSelectAlert.getButtonTypes().setAll(reversiButton, tttButton);
                Optional<ButtonType> buttonResult = gameSelectAlert.showAndWait();

                if (buttonResult.isPresent()) {
                    Game game;

                    if (buttonResult.get().equals(reversiButton)) {
                        game = Game.REVERSI;
                    } else if (buttonResult.get().equals(tttButton)) {
                        game = Game.TIC_TAC_TOE;
                    } else {
                        return;
                    }

                    BordspelAI.getServer().sendCommand(Command.CHALLENGE, playerToChallenge, game.getGame());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

                    alert.setTitle("Challenge sent");
                    alert.setHeaderText(null);
                    alert.setContentText("A challenge to play " + game.getGame() + " has been sent to " + playerToChallenge + ".");
                    alert.show();
                }
            }
        }));
    }

    public void updatePlayerList() {
        if (model.getPlayerList() != null) {
            playerList.setItems(model.getPlayerList());
        }
    }
}
