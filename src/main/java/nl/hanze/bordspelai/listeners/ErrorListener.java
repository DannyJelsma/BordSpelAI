package nl.hanze.bordspelai.listeners;

import nl.hanze.bordspelai.events.NetEventListener;
import nl.hanze.bordspelai.notifications.Notification;

public class ErrorListener implements NetEventListener {

    @Override
    public void update(Notification notification) {
        if (notification.getNotificationType().equals("OK")) {
            System.out.println("< " + notification.getNotificationType());
        } else if (notification.getNotificationType().equals("ERR")) {
            String message = notification.getDataMap().get("MESSAGE");
            System.out.println("< " + notification.getNotificationType() + " " + message);
        }
    }
}
