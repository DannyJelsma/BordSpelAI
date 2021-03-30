package nl.hanze.bordspelai.events;

import nl.hanze.bordspelai.net.GameNotification;

public interface NetEventListener {

    void update(GameNotification notification);

}
