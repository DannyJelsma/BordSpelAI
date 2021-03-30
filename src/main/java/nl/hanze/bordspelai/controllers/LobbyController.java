package nl.hanze.bordspelai.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import nl.hanze.bordspelai.BordspelAI;
import nl.hanze.bordspelai.listeners.PlayerListUpdateListener;
import nl.hanze.bordspelai.net.Command;
import nl.hanze.bordspelai.net.Server;

import java.util.List;

public class LobbyController implements Controller {

    @FXML
    public Button challengeButton;

    @FXML
    public Button refreshButton;

    @FXML
    public ListView<String> playerList;

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

        List<String> playerList = PlayerListUpdateListener.getPlayerList();
        if (playerList != null) {
            ObservableList<String> newPlayerList = FXCollections.observableArrayList(playerList);

            this.playerList.setItems(newPlayerList);
            this.playerList.refresh();
        }
    }
}
