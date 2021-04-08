package nl.hanze.bordspelai.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
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

        if (playerToChallenge != null) {
            BordspelAI.getThreadPool().submit(() -> Platform.runLater(() -> {
                if (playerToChallenge.equalsIgnoreCase(manager.getUsername() + " (You)")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);

                    alert.setTitle("Oops!");
                    alert.setHeaderText(null);
                    alert.setContentText("You can't challenge yourself...");
                    Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                    stage.setAlwaysOnTop(true);
                    alert.show();
                } else {
                    ButtonType reversiButton = new ButtonType("Othello/Reversi");
                    ButtonType tttButton = new ButtonType("Tic-tac-toe");
                    Alert gameSelectAlert = new Alert(Alert.AlertType.NONE);
                    gameSelectAlert.setTitle("Game selection");
                    gameSelectAlert.setHeaderText(null);
                    gameSelectAlert.setContentText("What game do you want to play?");
                    gameSelectAlert.getButtonTypes().setAll(reversiButton, tttButton);
                    gameSelectAlert.getDialogPane().getButtonTypes().add(new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE));
                    Stage stage = (Stage) gameSelectAlert.getDialogPane().getScene().getWindow();
                    stage.setAlwaysOnTop(true);
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

                        if (BordspelAI.getServer().sendCommand(Command.CHALLENGE, playerToChallenge, game.getGame())) {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

                            alert.setTitle("Challenge sent");
                            alert.setHeaderText(null);
                            alert.setContentText("A challenge to play " + game.getGame() + " has been sent to " + playerToChallenge + ".");
                            alert.getButtonTypes().remove(1);
                            Stage stage2 = (Stage) alert.getDialogPane().getScene().getWindow();
                            stage2.setAlwaysOnTop(true);
                            alert.show();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);

                            alert.setTitle("Failed to send challenge");
                            alert.setHeaderText(null);
                            alert.setContentText(BordspelAI.getServer().getLastError());
                            alert.getButtonTypes().remove(1);
                            Stage stage2 = (Stage) alert.getDialogPane().getScene().getWindow();
                            stage2.setAlwaysOnTop(true);
                            alert.show();
                        }
                    }
                }
            }));
        }
    }

    public void updatePlayerList() {
        if (model.getPlayerList() != null) {
            playerList.setItems(model.getPlayerList());
        }
    }
}
