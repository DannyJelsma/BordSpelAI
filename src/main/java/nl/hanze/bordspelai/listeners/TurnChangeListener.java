package nl.hanze.bordspelai.listeners;

import nl.hanze.bordspelai.enums.GameState;
import nl.hanze.bordspelai.events.NetEventListener;
import nl.hanze.bordspelai.managers.GameManager;
import nl.hanze.bordspelai.notifications.Notification;

import java.util.Map;

public class TurnChangeListener implements NetEventListener {

    @Override
    public void update(Notification notification) {
        GameManager manager = GameManager.getInstance();

        if (notification.getNotificationType().equals("MATCH")) {
            Map<String, String> data = notification.getDataMap();
            String toMove = data.get("PLAYERTOMOVE");

            if (toMove.equals(manager.getUsername())) {
                manager.setState(GameState.YOUR_TURN);
            } else {
                manager.setState(GameState.OPPONENT_TURN);
            }
        }

        if (notification.getNotificationType().equals("YOURTURN")) {
            manager.setState(GameState.YOUR_TURN);
        } else if (notification.getNotificationType().equals("MOVE") && !manager.getState().equals(GameState.OPPONENT_TURN)) {
            manager.setState(GameState.OPPONENT_TURN);
        }
    }
}
