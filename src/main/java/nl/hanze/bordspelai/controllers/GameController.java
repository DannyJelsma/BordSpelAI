package nl.hanze.bordspelai.controllers;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import nl.hanze.bordspelai.BordspelAI;
import nl.hanze.bordspelai.enums.Command;
import nl.hanze.bordspelai.enums.GameState;
import nl.hanze.bordspelai.events.NetEventListener;
import nl.hanze.bordspelai.games.Game;
import nl.hanze.bordspelai.managers.GameManager;
import nl.hanze.bordspelai.net.Server;
import nl.hanze.bordspelai.notifications.Notification;

import java.util.ArrayList;
import java.util.Map;

public class GameController implements Controller, NetEventListener {
    @FXML
    private GridPane grid;

    @FXML
    private HBox scoreBox;

    @FXML
    private Label xPlayer;

    @FXML
    private Label oPlayer;

    private final Server server = BordspelAI.getServer();
    private ArrayList<Button> boardButtons = new ArrayList<>();

    private final Game game;
    private final GameManager manager = GameManager.getInstance();

    public GameController(Game game) {
        this.game = game;
    }

    public Button getButton(int position) {
        return boardButtons.get(position);
    }

    @FXML
    public void initialize() {
        // bind score manage with visibility.
        scoreBox.managedProperty().bind(scoreBox.visibleProperty());

        // setup grid
        game.setupBoard(grid, scoreBox, xPlayer, oPlayer);

        if (manager.getState().equals(GameState.YOUR_TURN)) {
            doBestMove();
        }
    }

    public void sendMove(int move) {
        this.server.sendCommand(Command.MOVE, String.valueOf(move));
    }

    public void doBestMove() {
        int bestMove = game.doBestMove();
        this.server.sendCommand(Command.MOVE, String.valueOf(bestMove));
    }

    public Game getGame() {
        return game;
    }

    @Override
    public void update(Notification notification) {
        updateTurnState(notification);

        if (notification.getNotificationType().equals("MOVE")) {
            Map<String, String> data = notification.getDataMap();
            int position = Integer.parseInt(data.get("MOVE"));

            game.addMove(position);
        }

        if (notification.getNotificationType().equals("YOURTURN")) {
            doBestMove();
        }
    }

    private void updateTurnState(Notification notification) {
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
