package nl.hanze.bordspelai.listeners;

import nl.hanze.bordspelai.events.NetEventListener;
import nl.hanze.bordspelai.notifications.Notification;

public class ErrorListener implements NetEventListener {

    private static String lastType;
    private static String lastError;
    private static volatile boolean wait = false;

    @Override
    public void update(Notification notification) {
        if (notification.getNotificationType().equals("OK")) {
            lastType = notification.getNotificationType();
            System.out.println("< " + notification.getNotificationType());
            wait = false;
        } else if (notification.getNotificationType().equals("ERR")) {
            String message = notification.getDataMap().get("MESSAGE");
            lastType = notification.getNotificationType();
            lastError = message;
            System.out.println("< " + notification.getNotificationType() + " " + message);
            wait = false;
        }
    }

    public static String getLastType() {
        return lastType;
    }

    public static String getLastError() {
        return lastError;
    }
}
