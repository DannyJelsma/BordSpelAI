package nl.hanze.bordspelai.events;

import nl.hanze.bordspelai.net.GameNotification;

import java.util.ArrayList;
import java.util.List;

public class NetEventManager {

    private static NetEventManager instance;
    private final List<NetEventListener> listeners = new ArrayList<>();

    private NetEventManager() {
    }

    public static NetEventManager getInstance() {
        if (instance == null) {
            instance = new NetEventManager();
        }

        return instance;
    }

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
