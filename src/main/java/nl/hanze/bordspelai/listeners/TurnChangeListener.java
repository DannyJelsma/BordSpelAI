package nl.hanze.bordspelai.listeners;

import nl.hanze.bordspelai.enums.GameState;
import nl.hanze.bordspelai.events.NetEventListener;
import nl.hanze.bordspelai.games.Game;
import nl.hanze.bordspelai.managers.GameManager;
import nl.hanze.bordspelai.notifications.Notification;

import java.util.Map;

public class TurnChangeListener implements NetEventListener {

    @Override
    public void update(Notification notification) {
        GameManager manager = GameManager.getInstance();
        Game game = manager.getGameController().getGame();
        updateTurnState(notification);

        if (notification.getNotificationType().equals("MOVE")) {
            Map<String, String> data = notification.getDataMap();
            int position = Integer.parseInt(data.get("MOVE"));

            game.addMove(position);
        }

        if (notification.getNotificationType().equals("YOURTURN")) {
            game.doBestMove();
        }
    }

    private void updateTurnState(Notification notification) {
        GameManager manager = GameManager.getInstance();
        Map<String, String> data = notification.getDataMap();

        if (notification.getNotificationType().equals("MOVE")) {
            if (data.get("PLAYER").equals(manager.getUsername())) {
                manager.setState(GameState.OPPONENT_TURN);
            } else if (data.get("PLAYER").equals(manager.getOpponent())) {
                manager.setState(GameState.YOUR_TURN);
            }
        }
    }
}
