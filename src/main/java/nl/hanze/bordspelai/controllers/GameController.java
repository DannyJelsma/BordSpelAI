package nl.hanze.bordspelai.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import nl.hanze.bordspelai.BordspelAI;
import nl.hanze.bordspelai.enums.Command;
import nl.hanze.bordspelai.enums.GameState;
import nl.hanze.bordspelai.events.NetEventListener;
import nl.hanze.bordspelai.managers.GameManager;
import nl.hanze.bordspelai.models.GameModel;
import nl.hanze.bordspelai.net.Server;
import nl.hanze.bordspelai.notifications.Notification;

import java.util.ArrayList;
import java.util.Map;

public class GameController implements Controller, NetEventListener {
    @FXML
    private GridPane grid;
    private final Server server = BordspelAI.getServer();
    private ArrayList<Button> boardButtons = new ArrayList<>();

    private final GameModel model;
    private final GameManager manager = GameManager.getInstance();

    public GameController(GameModel model) {
        this.model = model;
//    this.server.sendCommand(Command.PLAY, "TicTacToe"); todo
    }

    // public void update
    
    @FXML
    public void initialize() {
//        int board[] = model.getBoard();

        int size = model.getSize();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {

                Button btn = new Button("");
                grid.setHgap(6);
                grid.setVgap(6);
                btn.setStyle("-fx-background-color: #ECECEC; -fx-background-radius: 12px;");
                btn.setPrefSize(80, 80);
        
                int clicked = size * i + j;
                btn.setOnAction((event) -> {
                    this.sendMove(clicked);
                    //this.model.addMove(clicked, manager.getUsername());

                    System.out.println("click " + clicked);
                });
                grid.add(btn, j, i);

                boardButtons.add(btn);
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
        updateTurnState(notification);

        System.out.println(GameManager.getInstance().getState());

        if (notification.getNotificationType().equals("MOVE")) {
            Map<String, String> data = notification.getDataMap();

            int position = Integer.parseInt(data.get("MOVE")); 
            model.addMove(position, data.get("PLAYER"));
            
            // update ui from button index
            Button btn = boardButtons.get(position);
            model.updateMove(btn, position);
        }

        //if (manager.getMode().equals(Mode.MULTIPLAYER)) {
        if (notification.getNotificationType().equals("YOURTURN")) {
            doBestMove();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //} else {
        // TODO: Make AI move on the correct turn.
        //}
    }

    private void updateTurnState(Notification notification) {
        Map<String, String> data = notification.getDataMap();

        if (notification.getNotificationType().equals("MATCH")) {
            String toMove = data.get("PLAYERTOMOVE");

            if (toMove.equals(manager.getUsername())) {
                manager.setState(GameState.YOUR_TURN);
            } else {
                manager.setState(GameState.OPPONENT_TURN);
            }
        }

        if (notification.getNotificationType().equals("MOVE")) {
            if (data.get("PLAYER").equals(manager.getUsername())) {
                manager.setState(GameState.OPPONENT_TURN);
            } else if (data.get("PLAYER").equals(manager.getOpponent())) {
                manager.setState(GameState.YOUR_TURN);
            }
        }
    }
}
