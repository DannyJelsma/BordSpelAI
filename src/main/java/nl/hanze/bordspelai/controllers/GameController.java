package nl.hanze.bordspelai.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import nl.hanze.bordspelai.BordspelAI;
import nl.hanze.bordspelai.enums.Command;
import nl.hanze.bordspelai.enums.Mode;
import nl.hanze.bordspelai.events.NetEventListener;
import nl.hanze.bordspelai.events.NetEventManager;
import nl.hanze.bordspelai.managers.GameManager;
import nl.hanze.bordspelai.models.GameModel;
import nl.hanze.bordspelai.net.Server;
import nl.hanze.bordspelai.notifications.Notification;

import java.util.Map;

public class GameController implements Controller, NetEventListener {
    @FXML
    private GridPane grid;
    private final Server server = BordspelAI.getServer();

    private final GameModel model;
    private final GameManager manager = GameManager.getInstance();

    public GameController(GameModel model) {
        this.model = model;
        NetEventManager.getInstance().register(this);
//    this.server.sendCommand(Command.PLAY, "TicTacToe"); todo
    }

    @FXML
    public void initialize() {
//        int board[] = model.getBoard();
        int size = model.getSize();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Button btn = new Button(i + " " + j);

                int clicked = size * i + j;
                btn.setOnAction((event) -> {

                    this.sendMove(clicked);
                    this.model.addMove(clicked);

                    System.out.println("click " + clicked);
                });

                grid.add(btn, j, i);
            }
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
        int bestMove = model.doBestMove();
        this.server.sendCommand(Command.MOVE, String.valueOf(bestMove));
    }

    @Override
    public void update(Notification notification) {
        if (notification.getNotificationType().equals("MOVE")) {
            Map<String, String> data = notification.getDataMap();

            model.addMove(Integer.parseInt(data.get("MOVE")));
        }

        if (manager.getMode().equals(Mode.MULTIPLAYER) && notification.getNotificationType().equals("YOURTURN")) {
            doBestMove();
        } else {
            // TODO: Make AI move on the correct turn.

        }
    }
}
