package nl.hanze.bordspelai.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import nl.hanze.bordspelai.BordspelAI;
import nl.hanze.bordspelai.builder.AlertBuilder;
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
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(() -> Platform.runLater(this::updatePlayerList)).start();
    }

    @FXML
    public void refresh() {
        Server server = BordspelAI.getServer();
        server.sendCommand(Command.GET_PLAYERLIST);
        new Thread(() -> Platform.runLater(this::updatePlayerList)).start();
    }

    @FXML
    public void challengePlayer() {
        GameManager manager = GameManager.getInstance();
        String playerToChallenge = playerList.getSelectionModel().getSelectedItem();

        if (playerToChallenge != null) {
            Platform.runLater(() -> {
                if (playerToChallenge.equalsIgnoreCase(manager.getUsername() + " (You)")) {
                    AlertBuilder builder = new AlertBuilder(Alert.AlertType.ERROR);
                    Alert alert = builder.setTitle("Oops!")
                            .setContent("You can't challenge yourself...")
                            .build();

                    alert.show();
                } else {
                    ButtonType reversiButton = new ButtonType("Othello/Reversi");
                    ButtonType tttButton = new ButtonType("Tic-tac-toe");
                    ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                    AlertBuilder builder = new AlertBuilder(Alert.AlertType.NONE);
                    Alert gameSelectAlert = builder.setTitle("Game selection")
                            .setContent("What game do you want to play?")
                            .addButtons(reversiButton, tttButton, cancelButton)
                            .build();

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
                        Alert alert = new AlertBuilder(Alert.AlertType.CONFIRMATION)
                                .setTitle("Challenge sent")
                                .setContent("A challenge to play " + game.getGame() + " has been sent to " + playerToChallenge + ".")
                                .build();

                        alert.getButtonTypes().remove(1);
                        alert.show();
                    } else {
/*                            Alert alert = new AlertBuilder(Alert.AlertType.ERROR)
                                .setTitle("Failed to send challenge")
                                .setContent(BordspelAI.getServer().getLastError())
                                .build();

                        alert.getButtonTypes().remove(1);
                        alert.show();*/
                    }
                }
            });
        }
    }

    public void updatePlayerList() {
        if (model.getPlayerList() != null) {
            playerList.setItems(model.getPlayerList());
        }
    }
}
