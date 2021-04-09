package nl.hanze.bordspelai.games;

import nl.hanze.bordspelai.managers.GameManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reversi extends Game {

    private static final char NO_CHIP = 0;

    private final char ownChar;
    private final char opponentChar;
    private final GameManager manager = GameManager.getInstance();
    private final String startingPlayer;

    public Reversi(String startingPlayer) {
        super(8, startingPlayer);

        this.startingPlayer = startingPlayer;

        // set players char
        if (startingPlayer.equals(manager.getUsername())) {
            this.ownChar = 'o';
            this.opponentChar = 'x';
        } else {
            this.ownChar = 'x';
            this.opponentChar = 'o';
        }
    }

    @Override
    public ArrayList<Integer> getAvailablePositions(Board board, char playerToCheck) {
        ArrayList<Integer> availablePositions = new ArrayList<>();
        for (int pos = 0; pos < board.getBoard().length; pos++) {
            char otherChar = playerToCheck == ownChar ? opponentChar : ownChar;
            if (this.isValidMove(board, pos, playerToCheck)) {
                List<Integer> flippedChips = getAllFlippedChips(board, pos, playerToCheck, otherChar);

                if (flippedChips != null) {
                    int flipAmount = flippedChips.size();

                    if (flipAmount > 0) {
                        availablePositions.add(pos);
                        break;
                    }
                }
            }
        }

        return availablePositions;
    }

    public List<Integer> getAllFlippedChips(Board board, int pos, char playerToCheck, char otherChar) {
        List<Integer> toFlip = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            List<Integer> flippedChips = getFlippedChips(board, dir, pos, playerToCheck, otherChar);

            if (flippedChips != null) {
                toFlip.addAll(flippedChips);
            }
        }

        return toFlip;
    }

    @Override
    public int doBestMove() {
        int highestScore = Integer.MIN_VALUE;
        int bestMove = 0;

        System.out.println("Available: " + getAvailablePositions(getBoard(), getCharByUsername(manager.getUsername())));

        for (int move : getAvailablePositions(getBoard(), getCharByUsername(manager.getUsername()))) {
            Board newBoard = getBoard().clone();
            List<Integer> flippedChips = getAllFlippedChips(getBoard(), move, ownChar, opponentChar);
            newBoard.setPosition(move, ownChar);

            for (int chip : flippedChips) {
                newBoard.setPosition(chip, ownChar);
            }

            int score = minimax(newBoard, 0, false);
            if (score > highestScore) {
                highestScore = score;
                bestMove = move;
            }
        }
        return bestMove;
    }
    private int minimax(Board board, int depth, boolean maximize) {
        char winner = getWinner(board);
        char ourChar = getCharByUsername(manager.getUsername());
        char opponentChar = getCharByUsername(manager.getOpponent());

        if (hasGameEnded(board)) {
            if (winner == ourChar) {
                return 10;
            } else if (winner == opponentChar) {
                return -10;
            } else if (winner == 1) {
                return 0;
            }
        }

        if (maximize) {
            int bestScore = Integer.MIN_VALUE;

            for (int move : getAvailablePositions(board, ourChar)) {
                Board newBoard = board.clone();
                List<Integer> flippedChips = getAllFlippedChips(board, move, ourChar, opponentChar);
                newBoard.setPosition(move, ourChar);

                for (int chip : flippedChips) {
                    newBoard.setPosition(chip, ourChar);
                }

                System.out.println(Arrays.toString(board.getBoard()) + " -> " + Arrays.toString(newBoard.getBoard()));

                int score = minimax(newBoard, depth + 1, false);
                bestScore = Math.max(score, bestScore);
            }

            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;

            for (int move : getAvailablePositions(board, getCharByUsername(manager.getOpponent()))) {
                Board newBoard = board.clone();
                List<Integer> flippedChips = getAllFlippedChips(board, move, opponentChar, ourChar);
                newBoard.setPosition(move, opponentChar);

                for (int chip : flippedChips) {
                    newBoard.setPosition(chip, opponentChar);
                }

                int score = minimax(newBoard, depth + 1, true);
                bestScore = Math.min(score, bestScore);
            }
            return bestScore;
        }
    }

    private boolean isValidMove(Board board, int pos, char opponent) {
        //System.out.println("Pos = " + pos);

        if (board.getPosition(pos) == NO_CHIP) {
            //System.out.println(pos);

            /*
             *    |   |   |   |   |   |   |
             *  --+---+---+---+---+---+---+--
             *    |   |   |   |   |   |   |
             *  --+---+---+---+---+---+---+--
             *    |   |   |   |   |   |   |
             *  --+---+---+---+---+---+---+--
             *    |   |   | x | o |   |   |
             *  --+---+---+---+---+---+---+--
             *    |   |   | o | x |   |   |
             *  --+---+---+---+---+---+---+--
             *    |   |   |   |   |   |   |
             *  --+---+---+---+---+---+---+--
             *    |   |   |   |   |   |   |
             *  --+---+---+---+---+---+---+--
             *    |   |   |   |   |   |   |
             */

            for (Direction dir : Direction.values()) {
                //System.out.println(dir + ": " + checkChip(dir, pos));
                if (checkChip(board, dir, pos) == opponent) {
                    return true;
                }
            }

            return false;
        }

        return false;
    }

    public boolean hasGameEnded(Board board) {
        return getAvailablePositions(board, ownChar).size() == 0 && getAvailablePositions(board, opponentChar).size() == 0;
    }

    // Returns 0 when game hasn't ended.
    // Returns 1 when draw.
    public char getWinner(Board board) {
        if (!hasGameEnded(board)) {
            return 0;
        }

        int ownChips = board.getAmount(ownChar);
        int opponentChips = board.getAmount(opponentChar);

        if (ownChips > opponentChips) {
            return ownChar;
        } else if (opponentChips > ownChips) {
            return opponentChar;
        } else {
            return 1;
        }
    }

    @Override
    public void addMove(int position, char charToMove) {
        for (Direction dir : Direction.values()) {
            List<Integer> flipped;
            if (charToMove == ownChar) {
                flipped = getFlippedChips(getBoard(), dir, position, ownChar, opponentChar);
            } else {
                flipped = getFlippedChips(getBoard(), dir, position, opponentChar, ownChar);
            }

            getBoard().setPosition(position, charToMove);
            updateMove(position);

            if (flipped != null) {
                System.out.println("Flipped: " + flipped);
                for (int pos : flipped) {
                    getBoard().setPosition(pos, charToMove);
                    updateMove(pos);
                }
            }
        }

        System.out.println("Board: " + Arrays.toString(getBoard().getBoard()));
    }

    public List<Integer> getFlippedChips(Board board, Direction direction, int pos, char movingPlayer, char playerToFlip) {
        boolean foundPlayerToFlip = false;

        if (pos < 0 || pos > 63) {
            return null;
        }

        int newPos = pos;
        List<Integer> toFlip = new ArrayList<>();
        while (newPos > -1) {
            newPos = getChipPosition(direction, newPos);

            if (newPos == -1) {
                return null;
            }

            char chipChar = board.getPosition(newPos);

            if (chipChar != playerToFlip) {
                if (chipChar == movingPlayer && foundPlayerToFlip) {
                    return toFlip;
                } else {
                    return null;
                }
            } else {
                toFlip.add(newPos);
                foundPlayerToFlip = true;
            }
        }

        return null;
    }

    private enum Direction {
        TOP, TOP_RIGHT, RIGHT, BOTTOM_RIGHT, BOTTOM, BOTTOM_LEFT, LEFT, TOP_LEFT
    }

    private char checkChip(Board board, Direction direction, int pos) {
        int chipPos = getChipPosition(direction, pos);

        if (chipPos == -1) {
            return 0;
        }

        return board.getPosition(chipPos);
    }

    private int getChipPosition(Direction direction, int pos) {
        //System.out.println("param pos "+pos);

        boolean onTopColumn = pos < 9;
        boolean onRightRow = pos % 8 == 7;
        boolean onBottomColumn = pos >= 56;
        boolean onLeftRow = pos % 8 == 0;

        //System.out.println("onTopColumn "+onTopColumn);
        //System.out.println("onRightRow "+onRightRow);
        //System.out.println("onBottomColumn "+onBottomColumn);
        //System.out.println("onLeftRow "+onLeftRow);

        switch (direction) {
            case TOP:
                if (onTopColumn) {
                    return -1;
                }

                return pos - 8;
            case TOP_RIGHT:
                if (onTopColumn || onRightRow) {
                    return -1;
                }

                return pos - 7;
            case RIGHT:
                if (onRightRow) {
                    return -1;
                }

                return pos + 1;
            case BOTTOM_RIGHT:
                if (onRightRow || onBottomColumn) {
                    return -1;
                }

                return pos + 9;
            case BOTTOM:
                if (onBottomColumn) {
                    return -1;
                }

                return pos + 8;
            case BOTTOM_LEFT:
                if (onBottomColumn || onLeftRow) {
                    return -1;
                }

                return pos + 7;
            case LEFT:
                if (onLeftRow) {
                    return -1;
                }

                return pos - 1;
            case TOP_LEFT:
                if (onLeftRow || onTopColumn) {
                    return -1;
                }

                return pos - 9;
        }

        //System.out.println("player "+player);
        return -1;
    }
}
