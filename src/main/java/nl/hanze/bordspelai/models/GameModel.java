package nl.hanze.bordspelai.models;

import java.util.ArrayList;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class GameModel implements Model {

    private int size;
    protected char[] board;

//    private final String ownUsername;
//    private final String opponentUsername;
//
//    private final char ownChar;
//    private final char opponentChar;

    private ArrayList<Integer> availableMoves;

    public GameModel(int size) {
        this.size = size;
        this.board = new char[size * size];

//        GameManager manager = GameManager.getInstance();
//        this.ownUsername = manager.getUsername();
//        this.opponentUsername = manager.getOpponent();
//
//        this.ownChar = 'x';
//        this.opponentChar = 'o';
    }

    public int getSize() {
        return size;
    }

    public char[] getBoard() {
        return board;
    }

    public abstract void addMove(int move, String player);

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
        String source = move == 'o' ? "/images/circle.png" : "/images/cross.png";
        Image image = new Image(source, 40, 40, false, true);
        btn.setStyle("-fx-background-color: #" + (move == 'o' ? "73ff5e" : "ff695e") + ";  -fx-background-radius: 12px;");
        ImageView view = new ImageView(image);
        btn.setGraphic(view);
    }

    public abstract int doBestMove();
}
