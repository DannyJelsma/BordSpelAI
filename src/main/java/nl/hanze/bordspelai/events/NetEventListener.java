package nl.hanze.bordspelai.events;

import nl.hanze.bordspelai.notifications.Notification;

public interface NetEventListener {

    void update(Notification notification);

}
