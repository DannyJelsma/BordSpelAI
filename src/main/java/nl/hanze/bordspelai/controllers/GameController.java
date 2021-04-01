package nl.hanze.bordspelai.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import nl.hanze.bordspelai.BordspelAI;
import nl.hanze.bordspelai.enums.Command;
import nl.hanze.bordspelai.managers.SceneManager;
import nl.hanze.bordspelai.models.GameModel;
import nl.hanze.bordspelai.net.Server;

public class GameController implements Controller {
    @FXML
    private GridPane grid;
    private final Server server = BordspelAI.getServer();

    private GameModel model;

    public GameController(GameModel model) {
        this.model = model;
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

                    String username = "test"; // todo
                    this.sendMove(clicked, username);
                    this.model.addMove(clicked, username);

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

    public void sendMove(int move, String username) {
        this.server.sendCommand(Command.MOVE, String.format("{PLAYER: \"%s\", MOVE: \"%d\", DETAILS: \"\"}", username, move));
    }

    public void doBestMove() {
        int bestMove = model.doBestMove();
        String username = "test"; // todo
        this.server.sendCommand(Command.MOVE, String.format("{PLAYER: \"%s\", MOVE: \"%d\", DETAILS: \"\"}", username, bestMove));
    }
}
