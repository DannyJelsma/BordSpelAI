package nl.hanze.bordspelai.games;

import java.util.Objects;

public class BoardState {

    private final int hashCode;
    private final int depth;
    private final int move;

    public BoardState(int hashCode, int depth, int move) {
        this.hashCode = hashCode;
        this.depth = depth;
        this.move = move;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardState that = (BoardState) o;
        return hashCode == that.hashCode && depth == that.depth && move == that.move;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hashCode, depth, move);
    }
}
