package nl.hanze.bordspelai.games;

public class Board {

    private final int size;
    private char[] board;

    public Board(int size) {
        this.size = size;
        this.board = new char[size * size];
    }

    public int getSize() {
        return size;
    }

    public char[] getBoard() {
        return board;
    }

    public void setPosition(int position, char character) {
        board[position] = character;
    }

    public char getPosition(int position) {
        return board[position];
    }

    public boolean isPositionAvailable(int position) {
        return position == 0;
    }

    public void reset() {
        this.board = new char[size * size];
    }
}
