package nl.hanze.bordspelai.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
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

    public void options() {
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Options");
        dialog.setHeaderText("Full in an ip-address and a port to connect to another server");

        // Set the icon (must be included in the project).

        // Set the button types.
        ButtonType connectButtonType = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(connectButtonType, ButtonType.CANCEL);

        // Create the labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField ipAddress = new TextField();
        ipAddress.setPromptText("IP-Address");
        TextField port = new TextField();
        port.setPromptText("Port");

        grid.add(new Label("IP-Address:"), 0, 0);
        grid.add(ipAddress, 1, 0);
        grid.add(new Label("Port:"), 0, 1);
        grid.add(port, 1, 1);

        // Enable/Disable connect button depending on whether a port was entered.
        Node connectButton = dialog.getDialogPane().lookupButton(connectButtonType);
        connectButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        port.textProperty().addListener((observable, oldValue, newValue) -> {
            connectButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> ipAddress.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == connectButtonType) {
                return new Pair<>(ipAddress.getText(), port.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(ipAddressAndPort -> {
            BordspelAI bordspelAI = new BordspelAI();

            try {
                bordspelAI.changeServer(ipAddressAndPort.getKey(), Integer.parseInt(ipAddressAndPort.getValue()));
                //refresh();
            } catch (NumberFormatException e) {
                AlertBuilder builder = new AlertBuilder(Alert.AlertType.ERROR);
                Alert alert = builder.setTitle("Oops!")
                        .setContent("Please full in a valid port")
                        .build();

                alert.show();
            } catch (IllegalStateException e) {
                AlertBuilder builder = new AlertBuilder(Alert.AlertType.ERROR);
                Alert alert = builder.setTitle("Oops!")
                        .setContent(e.getMessage())
                        .build();

                alert.show();
            }
        });
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
