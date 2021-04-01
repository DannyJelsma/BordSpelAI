package nl.hanze.bordspelai.models;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import nl.hanze.bordspelai.enums.GameState;
import nl.hanze.bordspelai.managers.GameManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class TicTacToeModel extends GameModel {

    private GameManager manager = GameManager.getInstance();
    private char ownChar;
    private char opponentChar;

    public TicTacToeModel() {
        super(3);
        super.reset();
    }

    private char getCharByUsername(String username) {
        char playerChar;
        if (manager.getUsername().equals(username)) {
            playerChar = this.ownChar;
        } else {
            playerChar = this.opponentChar;
        }
        return playerChar;
    }

    @Override
    public int doBestMove() {
        ArrayList<Integer> availableMoves = this.getAvailablePositions();
        int bestMove = 0;

//        for (int move: availableMoves) {
        bestMove = availableMoves.get(new Random().nextInt(availableMoves.size())); // random (valid) move
//        }

        //this.board[bestMove] = this.getCharByUsername(manager.getCurrentPlayer());
        return bestMove;
    }

    @Override
    public void updatePlayerChars() {
        // set players char
        if (manager.getState() == GameState.YOUR_TURN) {
            this.ownChar = 'x';
            this.opponentChar = 'o';
        } else {
            this.ownChar = 'o';
            this.opponentChar = 'x';
        }
    }


    public void addMove(int position, String player) {
        this.board[position] = this.getCharByUsername(manager.getCurrentPlayer());

        System.out.println(Arrays.toString(this.board));
    }

    @Override
    public void updateMove(Button btn, int position) {
        Platform.runLater(() -> {
            char move = this.board[position];
            String source = move == 'o' ? "/images/circle.png" : "/images/cross.png";
            Image image = new Image(source, 40, 40, false, true);
            btn.setStyle("-fx-background-color: #" + (move == 'o' ? "73ff5e" : "ff695e") + ";  -fx-background-radius: 12px;");
            ImageView view = new ImageView(image);
            btn.setGraphic(view);
        });
    }
}
