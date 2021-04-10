package nl.hanze.bordspelai.minimax;

import nl.hanze.bordspelai.games.Board;
import nl.hanze.bordspelai.games.Reversi;
import nl.hanze.bordspelai.managers.GameManager;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MiniMaxNode {

    private final boolean isMaximizer;
    private final Set<MiniMaxNode> children;
    private final Board board;
    private final Reversi reversi;

    public MiniMaxNode(Reversi reversi, boolean isMaximizer, Board board) {
        this.reversi = reversi;
        this.isMaximizer = isMaximizer;
        this.board = board;
        this.children = new HashSet<>();
    }

    public void addChild(MiniMaxNode node) {
        children.add(node);
    }

    public void removeChild(MiniMaxNode node) {
        children.remove(node);
    }

    public int calculateScore() {
        if (children.size() == 0) {
            GameManager manager = GameManager.getInstance();
            char ownChar = reversi.getCharByUsername(manager.getUsername());
            char opponentChar = reversi.getCharByUsername(manager.getOpponent());

            if (reversi.hasGameEnded(board)) {
                char winner = reversi.getWinner(board);

                if (winner == ownChar) {
                    return 100;
                } else if (winner == opponentChar) {
                    return -100;
                } else if (winner == 1) {
                    return 0;
                }
            } else {
                int opponentAmount = board.getAmount(opponentChar);
                int ourAmount = board.getAmount(ownChar);

                if (isMaximizer) {
                    return ourAmount - opponentAmount;
                } else {
                    return opponentAmount;
                }
            }
        } else {
            MiniMaxNode bestChild = findBestChild();

            return bestChild.calculateScore();
        }

        return -100;
    }

    public MiniMaxNode findBestChild() {
        int bestScore;
        MiniMaxNode bestChild = null;

        if (isMaximizer()) {
            bestScore = Integer.MIN_VALUE;
        } else {
            bestScore = Integer.MAX_VALUE;
        }

        for (MiniMaxNode node : children) {
            int score = node.calculateScore();

            if (isMaximizer()) {
                if (score > bestScore) {
                    bestScore = score;
                    bestChild = node;
                }
            } else {
                if (score < bestScore) {
                    bestScore = score;
                    bestChild = node;
                }
            }
        }

        return bestChild;
    }

    public boolean isMaximizer() {
        return isMaximizer;
    }

    public Set<MiniMaxNode> getChildren() {
        return children;
    }

    public Board getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MiniMaxNode that = (MiniMaxNode) o;
        return isMaximizer == that.isMaximizer && Objects.equals(children, that.children) && Objects.equals(board, that.board) && Objects.equals(reversi, that.reversi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isMaximizer, children, board);
    }
}
