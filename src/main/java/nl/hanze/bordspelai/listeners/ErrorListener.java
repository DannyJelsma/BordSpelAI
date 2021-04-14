package nl.hanze.bordspelai.listeners;

import nl.hanze.bordspelai.events.NetEventListener;
import nl.hanze.bordspelai.notifications.Notification;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class ErrorListener implements NetEventListener {

    private static final List<BiConsumer<Boolean, String>> callbacks = new ArrayList<>();

    @Override
    public void update(Notification notification) {
        boolean isResult = notification.getNotificationType().equals("OK") || notification.getNotificationType().equals("ERR");

        if (isResult) {
            String message = notification.getDataMap().get("MESSAGE");
            boolean isOk = notification.getNotificationType().equals("OK");

            if (!callbacks.isEmpty()) {
                for (BiConsumer<Boolean, String> callback : callbacks) {
                    callback.accept(isOk, message);
                }

                callbacks.clear();
            }

            if (isOk) {
                System.out.println("< " + notification.getNotificationType());
            } else {
                System.out.println("< " + notification.getNotificationType() + " " + message);
            }
        }
    }

    public static void waitForConfirmation(BiConsumer<Boolean, String> callback) {
        callbacks.add(callback);
    }
}
