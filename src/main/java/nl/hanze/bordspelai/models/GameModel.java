package nl.hanze.bordspelai.models;

import java.util.ArrayList;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class GameModel implements Model {

    private int size;
    private char[] board;

    private final String ownUsername;
    private final String opponentUsername;

    private final char ownChar;
    private final char opponentChar;

    private ArrayList<Integer> availableMoves;

    public GameModel(int size, String ownUsername, String opponentUsername) {
        this.size = size;
        this.board = new char[size * size];

        this.ownUsername = ownUsername;
        this.opponentUsername = opponentUsername;

        this.ownChar = 'x';
        this.opponentChar = 'o';
    }

    public int getSize() {
        return size;
    }

    public char[] getBoard() {
        return board;
    }

    public void addMove(int position, String username) {
        // add own char to board
        if (this.ownUsername.equals(username)) {
            this.board[position] = this.ownChar;
        }
        // add opponent char to board
        else if (this.opponentUsername.equals(username)) {
          this.board[position] = this.opponentChar;
        }

        // 'x', 'o'
        // update the inserted char on the board
    }

    protected ArrayList<Integer> getAvailablePositions() {
        ArrayList<Integer> availablePositions = new ArrayList<>();
        for(int i=0;i<this.board.length;i++) {
            if (this.board[i] == 0) {
                availablePositions.add(i);
            }
        }
        return availablePositions;
    }

    // updates UI
    public void updateMove(Button btn, int position, char move) {
        int turn = 0;
        String source = turn == 0 ? "/images/circle.png" : "/images/cross.png";
        Image image = new Image(source, 40, 40, false, true);
        btn.setStyle("-fx-background-color: #" + (turn == 0 ? "73ff5e" : "ff695e") + ";  -fx-background-radius: 12px;");
        ImageView view = new ImageView(image);
        btn.setGraphic(view);
    }

    public abstract int doBestMove();
}
