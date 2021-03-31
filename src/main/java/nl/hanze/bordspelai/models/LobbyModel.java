package nl.hanze.bordspelai.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class LobbyModel implements Model {

    private ObservableList<String> playerList;

    public LobbyModel() {
        this.playerList = FXCollections.emptyObservableList();
    }

    public ObservableList<String> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(ObservableList<String> playerList) {
        this.playerList = playerList;
    }

    public void setPlayerList(List<String> playerList) {
        this.playerList = FXCollections.observableList(playerList);
    }
}
