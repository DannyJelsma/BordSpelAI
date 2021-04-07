package nl.hanze.bordspelai.games;

import java.util.ArrayList;

public class TicTacToe extends Game {
    public TicTacToe(String startingPlayer) {
        super(3, startingPlayer);
    }

/*    @Override
    public int doBestMove() {
        ArrayList<Integer> availableMoves = this.getAvailablePositions();
        int bestMove = 0;

//        for (int move: availableMoves) {
        bestMove = availableMoves.get(new Random().nextInt(availableMoves.size())); // random (valid) move
//        }

        //this.board.getPosition(bestMove] = this.getCharByUsername(manager.getCurrentPlayer());
        return bestMove;
    }*/

    @Override
    public int doBestMove() {
        int highestScore = Integer.MIN_VALUE;
        int bestMove = 0;

        for (int move : getAvailablePositions(this.board)) {
            Board newBoard = board.clone();
            newBoard.setPosition(move, getCharByUsername(manager.getUsername()));
            int score = minimax(newBoard, false);

            if (score > highestScore) {
                highestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private int minimax(Board board, boolean maximize) {
        char winner = getWinner(board);
        char ourChar = getCharByUsername(manager.getUsername());
        char opponentChar = getCharByUsername(manager.getOpponent());

        if (winner == ourChar) {
            return 1;
        } else if (winner == opponentChar) {
            return -1;
        } else if (winner == 'd') {
            return 0;
        }

        int bestScore;
        if (maximize) {
            bestScore = Integer.MIN_VALUE;
            Board newBoard = board.clone();

            for (int move : getAvailablePositions(newBoard)) {
                newBoard.setPosition(move, ourChar);
                int score = minimax(board, false);
                bestScore = Math.max(score, bestScore);
            }
        } else {
            bestScore = Integer.MAX_VALUE;
            Board newBoard = board.clone();

            for (int move : getAvailablePositions(newBoard)) {
                newBoard.setPosition(move, opponentChar);
                int score = minimax(board, true);
                bestScore = Math.min(score, bestScore);
            }

        }

        return bestScore;
    }

    private char getWinner(Board board) {
        for (int a = 0; a < 8; a++) {
            StringBuilder sb = new StringBuilder();

            switch (a) {
                case 0:
                    sb.append(board.getPosition(0)).append(board.getPosition(1)).append(board.getPosition(2));
                    break;
                case 1:
                    sb.append(board.getPosition(3)).append(board.getPosition(4)).append(board.getPosition(5));
                    break;
                case 2:
                    sb.append(board.getPosition(6)).append(board.getPosition(7)).append(board.getPosition(8));
                    break;
                case 3:
                    sb.append(board.getPosition(0)).append(board.getPosition(3)).append(board.getPosition(6));
                    break;
                case 4:
                    sb.append(board.getPosition(1)).append(board.getPosition(4)).append(board.getPosition(7));
                    break;
                case 5:
                    sb.append(board.getPosition(2)).append(board.getPosition(5)).append(board.getPosition(8));
                    break;
                case 6:
                    sb.append(board.getPosition(0)).append(board.getPosition(4)).append(board.getPosition(8));
                    break;
                case 7:
                    sb.append(board.getPosition(2)).append(board.getPosition(4)).append(board.getPosition(6));
                    break;
            }

            if (sb.toString().equals("xxx")) {
                return 'x';
            } else if (sb.toString().equals("ooo")) {
                return 'o';
            }
        }

        for (int a = 0; a < 9; a++) {
            for (int i = 0; i < board.getBoard().length; i++) {
                if (board.getPosition(i) == 0) {
                    break;
                }
            }

            if (a == 8) {
                return 'd';
            }
        }

        return 'n';
    }

    @Override
    protected ArrayList<Integer> getAvailablePositions(Board board) {
        ArrayList<Integer> availablePositions = new ArrayList<>();
        for (int i = 0; i < board.getSize(); i++) {
            if (board.isPositionAvailable(i)) {
                availablePositions.add(i);
            }
        }

        if (availablePositions.size() == 0) {
            board.reset();

            return getAvailablePositions(board);
        }

        return availablePositions;
    }
}
