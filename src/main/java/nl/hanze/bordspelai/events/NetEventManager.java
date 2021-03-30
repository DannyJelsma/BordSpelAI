package nl.hanze.bordspelai.events;

import nl.hanze.bordspelai.net.GameNotification;

import java.util.ArrayList;
import java.util.List;

public class NetEventManager {

    private final List<NetEventListener> listeners = new ArrayList<>();

    public void register(NetEventListener listener) {
        listeners.add(listener);
    }

    public void unregister(NetEventListener listener) {
        listeners.remove(listener);
    }

    public void notify(GameNotification notification) {
        for (NetEventListener listener : listeners) {
            listener.update(notification);
        }
    }
}
