package nl.hanze.bordspelai.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import nl.hanze.bordspelai.BordspelAI;
import nl.hanze.bordspelai.enums.Command;
import nl.hanze.bordspelai.listeners.PlayerListUpdateListener;
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
        BordspelAI.getThreadPool().submit(() -> Platform.runLater(this::updatePlayerList));
    }

    @FXML
    public void refresh() {
        BordspelAI.getThreadPool().submit(() -> Platform.runLater(this::updatePlayerList));
    }

    public void updatePlayerList() {
        Server server = BordspelAI.getServer();
        server.sendCommand(Command.GET_PLAYERLIST);

        ObservableList<String> newPlayerList = FXCollections.observableArrayList(PlayerListUpdateListener.getPlayerList());
        model.setPlayerList(newPlayerList);

        if (model.getPlayerList() != null) {
            this.playerList.setItems(model.getPlayerList());
        }
    }
}
