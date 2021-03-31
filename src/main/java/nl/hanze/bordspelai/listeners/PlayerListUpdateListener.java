package nl.hanze.bordspelai.listeners;

import nl.hanze.bordspelai.events.NetEventListener;
import nl.hanze.bordspelai.managers.GameManager;
import nl.hanze.bordspelai.models.LobbyModel;
import nl.hanze.bordspelai.notifications.Notification;

import java.util.Collections;
import java.util.List;

public class PlayerListUpdateListener implements NetEventListener {

    private final LobbyModel model;

    public PlayerListUpdateListener(LobbyModel model) {
        this.model = model;
    }

    @Override
    public void update(Notification notification) {
        if (notification.getNotificationType().equals("PLAYERLIST")) {
            GameManager manager = GameManager.getInstance();
            List<String> playerList = notification.getDataList();

            for (String player : playerList) {
                if (player.equalsIgnoreCase(manager.getUsername())) {
                    playerList.remove(player);
                    playerList.add(player + " (You)");
                }
            }

            Collections.sort(playerList);
            model.setPlayerList(playerList);
        }
    }
}
