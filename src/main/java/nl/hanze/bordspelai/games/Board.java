package nl.hanze.bordspelai.games;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

    public Set<Integer> getAllUsedPositions() {
        Set<Integer> usedPositions = new HashSet<>();

        for (int i = 0; i < getBoard().length; i++) {
            if (getPosition(i) != 0) {
                usedPositions.add(i);
            }
        }

        return usedPositions;
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

        return new org.apache.commons.lang3.builder.EqualsBuilder().append(board, board1.board).isEquals();
    }

    @Override
    public int hashCode() {
        return new org.apache.commons.lang3.builder.HashCodeBuilder(17, 37).append(board).toHashCode();
    }

    public Board clone() {
        return new Board(size, board.clone());
    }
}
