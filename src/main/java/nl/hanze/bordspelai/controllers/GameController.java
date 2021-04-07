package nl.hanze.bordspelai.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
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
    private final Server server = BordspelAI.getServer();
    private ArrayList<Button> boardButtons = new ArrayList<>();

    private final Game game;
    private final GameManager manager = GameManager.getInstance();

    public GameController(Game game) {
        this.game = game;
//    this.server.sendCommand(Command.PLAY, "TicTacToe"); todo
    }

    // public void update
    
    public Button getButton(int position) {
        return boardButtons.get(position);
    }

    // initialize is called upon scene switch
    @FXML
    public void initialize() {
        // setup grid
        game.setupBoard(grid);

        System.out.println(manager.getState());
        if (manager.getState().equals(GameState.YOUR_TURN)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            doBestMove();
        }

        // for(int i = 0; i < board.length; i++) {
        //   int row = (int) Math.ceil(i / model.getSize());
        //   int column = i - model.getSize() * row;

        //   System.out.println("X " + row + ", Y " + column );

        //   grid.setGridLinesVisible(true);

        //   Button btn = new Button(row + " " + column);
        //   btn.setMinSize(Double.MAX_VALUE, Double.MAX_VALUE);
        //   grid.add(btn, row, column);
        // }
    }

    public void sendMove(int move) {
        this.server.sendCommand(Command.MOVE, String.valueOf(move));
    }

    public void doBestMove() {
        int bestMove = game.doBestMove();
        this.server.sendCommand(Command.MOVE, String.valueOf(bestMove));
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
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
