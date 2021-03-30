package nl.hanze.bordspelai.listeners;

import nl.hanze.bordspelai.events.NetEventListener;
import nl.hanze.bordspelai.net.GameNotification;

public class NetMessageListener implements NetEventListener {

    @Override
    public void update(GameNotification notification) {
        // Debugging
        if (notification.isList()) {
            System.out.println(notification.getNotificationType() + ": " + notification.getDataList());
        }

        if (notification.isMap()) {
            System.out.println(notification.getNotificationType() + ": " + notification.getDataMap());
        }
    }
}
