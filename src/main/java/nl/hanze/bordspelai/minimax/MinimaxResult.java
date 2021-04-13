package nl.hanze.bordspelai.minimax;

import java.util.concurrent.atomic.AtomicInteger;

public class MinimaxResult {

    private AtomicInteger move;
    private AtomicInteger score;

    public MinimaxResult() {
        this.move = new AtomicInteger(Integer.MIN_VALUE);
        this.score = new AtomicInteger(Integer.MIN_VALUE);
    }

    public AtomicInteger getMove() {
        return move;
    }

    public void setMove(int move) {
        this.move.set(move);
    }

    public void setMove(AtomicInteger move) {
        this.move = move;
    }

    public AtomicInteger getScore() {
        return score;
    }

    public void setScore(AtomicInteger score) {
        this.score = score;
    }

    public void setScore(int score) {
        this.score.set(score);
    }
}
