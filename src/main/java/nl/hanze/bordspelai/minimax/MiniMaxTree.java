package nl.hanze.bordspelai.minimax;

import nl.hanze.bordspelai.games.Board;
import nl.hanze.bordspelai.games.Reversi;

import java.util.Set;

public class MiniMaxTree {

    private MiniMaxNode root;
    private final Reversi reversi;
    private int turns = 0;

    public MiniMaxTree(Reversi reversi, MiniMaxNode root) {
        this.reversi = reversi;
        this.root = root;
    }

    public MiniMaxNode getRootNode() {
        return root;
    }

    public void addNode(MiniMaxNode node) {
        addNode(root, node);
    }

    public MiniMaxNode getBestMove() {
        return root.findBestChild();
    }

    private void addNode(MiniMaxNode parentNode, MiniMaxNode node) {
        Set<MiniMaxNode> children = parentNode.getChildren();
        Board board = node.getBoard();

        if (children.size() > 0) {
            for (MiniMaxNode childNode : children) {
                Board targetBoard = childNode.getBoard();
                Set<Integer> usedTargetPositions = targetBoard.getAllUsedPositions();
                Set<Integer> usedPositions = board.getAllUsedPositions();

                if ((usedPositions.size() - usedTargetPositions.size()) == 1 && usedPositions.containsAll(usedTargetPositions)) {
                    childNode.addChild(node);
                    break;
                }

                if (childNode.getChildren().size() > 0) {
                    addNode(childNode, node);
                }
            }
        }
    }

    public void nextTurn(Board finalBoard) {
        pruneRedundantMoves(finalBoard);
        turns++;
    }

    private void pruneRedundantMoves(Board finalBoard) {
        Set<MiniMaxNode> children = root.getChildren();

        for (MiniMaxNode child : children) {
            if (child.getBoard().equals(finalBoard)) {
                root = child;
                break;
            }
        }
    }

    public void removeNode(MiniMaxNode node) {
        removeNode(root, node);
    }

    private void removeNode(MiniMaxNode parentNode, MiniMaxNode node) {
        Set<MiniMaxNode> children = parentNode.getChildren();

        if (children.size() > 0) {
            for (MiniMaxNode childNode : children) {
                if (childNode.equals(node)) {
                    childNode.removeChild(node);
                    break;
                }

                if (childNode.getChildren().size() > 0) {
                    removeNode(childNode, node);
                }
            }
        }
    }
}
