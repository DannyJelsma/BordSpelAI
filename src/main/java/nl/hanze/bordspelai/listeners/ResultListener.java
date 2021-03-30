package nl.hanze.bordspelai.listeners;

import nl.hanze.bordspelai.events.NetEventListener;
import nl.hanze.bordspelai.notifications.Notification;
import nl.hanze.bordspelai.notifications.ResultNotification;

public class ResultListener implements NetEventListener {

    @Override
    public void update(Notification notification) {
        if (notification instanceof ResultNotification) {
            if (notification.getNotificationType().equals("ERR")) {
                System.out.println("ERROR: " + notification.getDataMap().get("MESSAGE"));
            }
        }
    }
}
