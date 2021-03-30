package nl.hanze.bordspelai.listeners;

import nl.hanze.bordspelai.events.NetEventListener;
import nl.hanze.bordspelai.notifications.Notification;

public class NetMessageListener implements NetEventListener {

    @Override
    public void update(Notification notification) {
        // Debugging
        if (notification.isList()) {
            System.out.println(notification.getNotificationType() + ": " + notification.getDataList());
        }

        if (notification.isMap()) {
            System.out.println(notification.getNotificationType() + ": " + notification.getDataMap());
        }
    }
}
