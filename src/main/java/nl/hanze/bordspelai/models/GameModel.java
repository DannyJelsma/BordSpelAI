package nl.hanze.bordspelai.models;

import nl.hanze.bordspelai.managers.GameManager;

import java.util.ArrayList;

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

    public abstract void addMove(int move);

    protected ArrayList<Integer> getAvailablePositions() {
        ArrayList<Integer> availablePositions = new ArrayList<>();
        for(int i=0;i<this.board.length;i++) {
            if (this.board[i] == 0) {
                availablePositions.add(i);
            }
        }
        return availablePositions;
    }

    public abstract int doBestMove();
}
