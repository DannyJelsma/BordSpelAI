package nl.hanze.bordspelai.listeners;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import nl.hanze.bordspelai.BordspelAI;
import nl.hanze.bordspelai.builder.AlertBuilder;
import nl.hanze.bordspelai.controllers.GameController;
import nl.hanze.bordspelai.enums.GameState;
import nl.hanze.bordspelai.events.NetEventListener;
import nl.hanze.bordspelai.events.NetEventManager;
import nl.hanze.bordspelai.games.Game;
import nl.hanze.bordspelai.games.Reversi;
import nl.hanze.bordspelai.games.TicTacToe;
import nl.hanze.bordspelai.managers.GameManager;
import nl.hanze.bordspelai.managers.SceneManager;
import nl.hanze.bordspelai.notifications.Notification;
import nl.hanze.bordspelai.views.ReversiView;
import nl.hanze.bordspelai.views.TicTacToeView;
import nl.hanze.bordspelai.views.View;

import java.util.Map;

public class MatchStartListener implements NetEventListener {

    @Override
    public void update(Notification notification) {
        if (notification.getNotificationType().equals("MATCH")) {
            BordspelAI.getThreadPool().submit(() -> Platform.runLater(() -> {
                Map<String, String> dataMap = notification.getDataMap();
                String gameType = dataMap.get("GAMETYPE");

                GameManager manager = GameManager.getInstance();
                String opponent = dataMap.get("OPPONENT");
                String toMove = dataMap.get("PLAYERTOMOVE");
                Alert alert = new AlertBuilder(Alert.AlertType.CONFIRMATION)
                        .setTitle("Match started")
                        .setContent("A match of " + gameType + " has started. Playing against: " + opponent)
                        .build();

                alert.show();
                manager.setOpponent(opponent);

                if (toMove.equalsIgnoreCase(opponent)) {
                    manager.setState(GameState.OPPONENT_TURN);
                } else {
                    manager.setState(GameState.YOUR_TURN);
                }

                Game game = null;
                GameController gameController = null;
                View view = null;

                if (gameType.equalsIgnoreCase("Tic-tac-toe")) {
                    game = new TicTacToe(dataMap.get("PLAYERTOMOVE"));
                    gameController = new GameController(game);
                    view = new TicTacToeView(gameController);
                } else if (gameType.equalsIgnoreCase("Reversi")) {
                    game = new Reversi(dataMap.get("PLAYERTOMOVE"));
                    gameController = new GameController(game);
                    view = new ReversiView(gameController);
                }
                
                if (game != null) {
                    NetEventManager.getInstance().register(gameController);
                    manager.setGameController(gameController);
                    SceneManager.switchScene(view);
                }
            }));
        }
    }
}
