package nl.hanze.bordspelai.listeners;

import nl.hanze.bordspelai.events.NetEventListener;
import nl.hanze.bordspelai.notifications.Notification;

public class TurnChangeListener implements NetEventListener {

    @Override
    public void update(Notification notification) {
/*        GameManager manager = GameManager.getInstance();
        Map<String, String> data = notification.getDataMap();

        if (notification.getNotificationType().equals("MATCH")) {
            String toMove = data.get("PLAYERTOMOVE");

            if (toMove.equals(manager.getUsername())) {
                manager.setState(GameState.YOUR_TURN);
            } else {
                manager.setState(GameState.OPPONENT_TURN);
            }
        }

        if (notification.getNotificationType().equals("YOURTURN")) {
            manager.setState(GameState.YOUR_TURN);
        } else if (notification.getNotificationType().equals("MOVE") && data.get("PLAYER").equals(manager.getUsername())) {
            manager.setState(GameState.OPPONENT_TURN);
        }*/
    }
}
