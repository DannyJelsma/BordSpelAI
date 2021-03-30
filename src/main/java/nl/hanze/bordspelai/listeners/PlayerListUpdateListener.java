package nl.hanze.bordspelai.listeners;

import nl.hanze.bordspelai.events.NetEventListener;
import nl.hanze.bordspelai.notifications.Notification;

import java.util.ArrayList;
import java.util.List;

public class PlayerListUpdateListener implements NetEventListener {

    private static List<String> playerList = new ArrayList<>();

    @Override
    public void update(Notification notification) {
        if (notification.getNotificationType().equals("PLAYERLIST")) {
            playerList = notification.getDataList();
        }
    }

    public static List<String> getPlayerList() {
        return playerList;
    }
}
