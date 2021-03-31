package nl.hanze.bordspelai.listeners;

import nl.hanze.bordspelai.events.NetEventListener;
import nl.hanze.bordspelai.models.LobbyModel;
import nl.hanze.bordspelai.notifications.Notification;

import java.util.List;

public class PlayerListUpdateListener implements NetEventListener {

    private LobbyModel model;

    public PlayerListUpdateListener(LobbyModel model) {
        this.model = model;
    }

    @Override
    public void update(Notification notification) {
        if (notification.getNotificationType().equals("PLAYERLIST")) {
            List<String> playerList = notification.getDataList();

            model.setPlayerList(playerList);
        }
    }
}
