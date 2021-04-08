package nl.hanze.bordspelai.games;

import java.util.Arrays;
import java.util.Objects;

public class Board {

    private final int size;
    private char[] board;

    public Board(int size) {
        this.size = size;
        this.board = new char[size * size];
    }

    public Board(int size, char[] board) {
        this.size = size;
        this.board = board;
    }

    public int getSize() {
        return size * size;
    }

    public int getWidth() {
        return size;
    }

    public char[] getBoard() {
        return board;
    }

    public void setBoard(char[] newBoard) {
        Objects.requireNonNull(newBoard);
        reset();
        System.arraycopy(newBoard, 0, board, 0, 64);
    }

    public void setPosition(int position, char character) {
        board[position] = character;
    }

    public void clearPosition(int position) {
        board[position] = 0;
    }

    public char getPosition(int position) {
        return board[position];
    }

    public boolean isPositionAvailable(int position) {
        return getPosition(position) == 0;
    }

    public void reset() {
        this.board = new char[size * size];
    }

    public int getAmount(char player) {
        int counter = 0;

        for (char character : board) {
            if (character == player) {
                counter++;
            }
        }

        return counter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board1 = (Board) o;
        return size == board1.size && Arrays.equals(board, board1.board);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(size);
        result = 31 * result + Arrays.hashCode(board);
        return result;
    }

    public Board clone() {
        return new Board(size, board.clone());
    }
}
