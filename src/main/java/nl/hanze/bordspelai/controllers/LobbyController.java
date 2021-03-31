package nl.hanze.bordspelai.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import nl.hanze.bordspelai.BordspelAI;
import nl.hanze.bordspelai.enums.Command;
import nl.hanze.bordspelai.models.LobbyModel;
import nl.hanze.bordspelai.net.Server;

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
        Server server = BordspelAI.getServer();

        server.sendCommand(Command.GET_PLAYERLIST);
        BordspelAI.getThreadPool().submit(() -> Platform.runLater(this::updatePlayerList));
    }

    @FXML
    public void refresh() {
        Server server = BordspelAI.getServer();

        server.sendCommand(Command.GET_PLAYERLIST);
        BordspelAI.getThreadPool().submit(() -> Platform.runLater(this::updatePlayerList));
    }

    public void updatePlayerList() {
        if (model.getPlayerList() != null) {
            playerList.setItems(model.getPlayerList());
        }
    }
}
