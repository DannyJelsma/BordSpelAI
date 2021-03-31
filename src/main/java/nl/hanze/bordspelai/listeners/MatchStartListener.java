package nl.hanze.bordspelai.listeners;

import nl.hanze.bordspelai.events.NetEventListener;
import nl.hanze.bordspelai.notifications.Notification;

public class MatchStartListener implements NetEventListener {
    @Override
    public void update(Notification notification) {
        if (notification.getNotificationType().equals("MATCH")) {
            String gameType = notification.getDataMap().get("GAMETYPE");
            // TODO: Send to correct game screen.
        }
    }
}
