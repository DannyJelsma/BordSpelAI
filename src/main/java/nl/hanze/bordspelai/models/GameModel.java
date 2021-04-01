package nl.hanze.bordspelai.models;

import java.util.ArrayList;

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

    public abstract int doBestMove();
}
