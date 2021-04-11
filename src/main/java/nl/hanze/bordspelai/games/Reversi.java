package nl.hanze.bordspelai.games;

import nl.hanze.bordspelai.enums.GameState;
import nl.hanze.bordspelai.managers.GameManager;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class Reversi extends Game {

    private static final char NO_CHIP = 0;

    private final char ownChar;
    private final char opponentChar;
    private final Map<Board, Integer> minimaxCache = new HashMap<>();
    private int cacheHits = 0;
    private int calculations = 0;
    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public Reversi(String startingPlayer) {
        super(8, startingPlayer);

        // set players char
        GameManager manager = GameManager.getInstance();
        if (startingPlayer.equals(manager.getUsername())) {
            this.ownChar = 'o';
            this.opponentChar = 'x';
        } else {
            this.ownChar = 'x';
            this.opponentChar = 'o';
        }
    }

    @Override
    public Set<Integer> getAvailablePositions(Board board, char playerToCheck) {
        Set<Integer> availablePositions = new HashSet<>();
        for (int pos = 0; pos < board.getBoard().length; pos++) {
            char otherChar = playerToCheck == ownChar ? opponentChar : ownChar;
            if (this.isValidMove(board, pos, otherChar)) {
                for (Direction dir : Direction.values()) {
                    List<Integer> flippedChips = getFlippedChips(board, dir, pos, playerToCheck, otherChar);

                    if (flippedChips != null) {
                        availablePositions.add(pos);
                    }
                }
            }
        }

        return availablePositions;
    }

    public List<Integer> getAllFlippedChips(Board board, int pos, char movingPlayer, char flippingPlayer) {
        List<Integer> toFlip = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            List<Integer> flippedChips = getFlippedChips(board, dir, pos, movingPlayer, flippingPlayer);

            if (flippedChips != null) {
                toFlip.addAll(flippedChips);
            }
        }

        return toFlip;
    }

    @Override
    public int doBestMove() {
        calculations = 0;
        cacheHits = 0;
        Board newBoard = getBoard().clone();
        long endTime = System.currentTimeMillis() + 6000;
        int highestScore = Integer.MIN_VALUE;
        int bestMove = Integer.MIN_VALUE;

        System.out.println("Available: " + getAvailablePositions(newBoard, ownChar));

        for (int move : getAvailablePositions(newBoard, ownChar)) {
            List<Integer> flippedChips = getAllFlippedChips(newBoard, move, ownChar, opponentChar);
            newBoard.setPosition(move, ownChar);

            for (int chip : flippedChips) {
                newBoard.setPosition(chip, ownChar);
            }

            int score = minimax(newBoard, 0, false, Integer.MIN_VALUE, Integer.MAX_VALUE, endTime, false);
            if (score > highestScore) {
                highestScore = score;
                bestMove = move;
            }

            newBoard.clearPosition(move);
            for (int chip : flippedChips) {
                newBoard.setPosition(chip, opponentChar);
            }
        }

        System.out.println("Cache hits: " + cacheHits);
        System.out.println("Calculations: " + calculations);
        System.out.println("Cache hit ratio: " + ((float) cacheHits / (float) calculations * 100f));
        System.out.println("Cache Size: " + minimaxCache.size());

        // Failsafe
        if (!getAvailablePositions(getBoard(), ownChar).contains(bestMove)) {
            Object[] avail = getAvailablePositions(getBoard(), ownChar).toArray();
            System.out.println("ERROR: Tried to do an illegal move at " + bestMove + ".");

            return (int) avail[ThreadLocalRandom.current().nextInt(0, avail.length)];
        }

        return bestMove;
    }

    public void doBackgroundCalculations() {
        calculations = 0;
        cacheHits = 0;
        long endTime = System.currentTimeMillis() + 7500;
        Board newBoard = getBoard().clone();

        // Fill cache while waiting for the opponent
        while (manager.getState().equals(GameState.OPPONENT_TURN)) {
            for (int move : getAvailablePositions(newBoard, opponentChar)) {
                List<Integer> flippedChips = getAllFlippedChips(newBoard, move, opponentChar, ownChar);
                newBoard.setPosition(move, opponentChar);

                for (int chip : flippedChips) {
                    newBoard.setPosition(chip, opponentChar);
                }

                minimax(newBoard, 0, true, Integer.MIN_VALUE, Integer.MAX_VALUE, endTime, true);
                newBoard.clearPosition(move);
                for (int chip : flippedChips) {
                    newBoard.setPosition(chip, opponentChar);
                }
            }
        }

        System.out.println("BG - Cache hits: " + cacheHits);
        System.out.println("BG - Calculations: " + calculations);
        System.out.println("BG - Cache hit ratio: " + ((float) cacheHits / (float) calculations * 100f));
        System.out.println("BG - Cache Size: " + minimaxCache.size());
    }

    private int minimax(Board board, int depth, boolean maximize, int alpha, int beta, long endTime, boolean isBackgroundTask) {
        if (hasGameEnded(board)) {
            char winner = getWinner(board);

            if (winner == ownChar) {
                return 100;
            } else if (winner == opponentChar) {
                return -100;
            } else if (winner == 1) {
                return 0;
            }
        }

        if (minimaxCache.containsKey(board)) {
            cacheHits++;
            return minimaxCache.get(board);
        }

        if (isBackgroundTask && !manager.getState().equals(GameState.OPPONENT_TURN)) {
            int opponentAmount = board.getAmount(opponentChar);
            int ourAmount = board.getAmount(ownChar);

            if (maximize) {
                return ourAmount - opponentAmount;
            } else {
                return opponentAmount - ourAmount;
            }
        }

        calculations++;

        int bestScore;
        if (maximize) {
            bestScore = Integer.MIN_VALUE;

            for (int move : getAvailablePositions(board, ownChar)) {
                if (System.currentTimeMillis() > endTime) {
                    int opponentAmount = board.getAmount(opponentChar);
                    int ourAmount = board.getAmount(ownChar);
                    int score = ourAmount - opponentAmount;

                    return Math.max(score, bestScore);
                }

                List<Integer> flippedChips = getAllFlippedChips(board, move, ownChar, opponentChar);
                board.setPosition(move, ownChar);

                for (int chip : flippedChips) {
                    board.setPosition(chip, ownChar);
                }

                //System.out.println(Arrays.toString(board.getBoard()) + " -> " + Arrays.toString(board.getBoard()));

                int score = minimax(board, depth + 1, false, alpha, beta, endTime, isBackgroundTask);
                bestScore = Math.max(score, bestScore);
                alpha = Math.max(alpha, bestScore);

                board.clearPosition(move);
                for (int chip : flippedChips) {
                    board.setPosition(chip, opponentChar);
                }

                if (beta <= alpha) break;
            }

        } else {
            bestScore = Integer.MAX_VALUE;

            for (int move : getAvailablePositions(board, opponentChar)) {
                if (System.currentTimeMillis() > endTime) {
                    int opponentAmount = board.getAmount(opponentChar);
                    int ourAmount = board.getAmount(ownChar);
                    int score = opponentAmount - ourAmount;

                    return Math.min(score, bestScore);
                }

                List<Integer> flippedChips = getAllFlippedChips(board, move, opponentChar, ownChar);
                board.setPosition(move, opponentChar);

                for (int chip : flippedChips) {
                    board.setPosition(chip, opponentChar);
                }

                //System.out.println(Arrays.toString(board.getBoard()) + " -> " + Arrays.toString(board.getBoard()));

                int score = minimax(board, depth + 1, true, alpha, beta, endTime, isBackgroundTask);
                bestScore = Math.min(score, bestScore);
                beta = Math.min(beta, bestScore);

                board.clearPosition(move);
                for (int chip : flippedChips) {
                    board.setPosition(chip, ownChar);
                }

                if (beta <= alpha) break;
            }

        }

        //if (bestScore == 100 || bestScore == -100 || bestScore == 0) {
        minimaxCache.put(board, bestScore);
        //}

        return bestScore;
    }

    // TODO: Finish heuristic. Search for more information about good and bad moves.
    // Positive
    private static final List<Integer> CORNERS = List.of(0, 7, 56, 63);

    // Negative
    private static final List<Integer> X_SQUARES = List.of(9, 49, 14, 54); // High negative weight for at least first 40 moves. Positive weight when OUR chip is in the adjacent corner
    private static final List<Integer> C_SQUARES = List.of(8, 1, 6, 15, 48, 57, 55, 62); // Low negative weight unless OUR disk in corner.

    private int doHeuristics(Board board, int move, boolean maximize) {
        if (maximize) {
            int opponentAmount = board.getAmount(opponentChar);
            int ourAmount = board.getAmount(ownChar);
            int score = ourAmount - opponentAmount;

            return score;
        } else {
            int opponentAmount = board.getAmount(opponentChar);
            int ourAmount = board.getAmount(ownChar);
            int score = opponentAmount - ourAmount;

            return score;
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
        List<Integer> flipped;
        if (charToMove == ownChar) {
            flipped = getAllFlippedChips(getBoard(), position, ownChar, opponentChar);
        } else {
            flipped = getAllFlippedChips(getBoard(), position, opponentChar, ownChar);
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

        if (charToMove == ownChar) {
            executor.submit(this::doBackgroundCalculations);
        }

        System.out.println("Board: " + Arrays.toString(getBoard().getBoard()));
    }

    public List<Integer> getFlippedChips(Board board, Direction direction, int pos, char movingPlayer, char playerToFlip) {
        boolean foundPlayerToFlip = false;

        int newPos = pos;
        List<Integer> toFlip = new ArrayList<>();
        while (newPos > -1) {
            newPos = getChipPosition(direction, newPos);

            if (newPos == -1) {
                return null;
            }

            char chipChar = board.getPosition(newPos);

            if (chipChar == playerToFlip) {
                toFlip.add(newPos);
                foundPlayerToFlip = true;
            } else {
                if (chipChar == movingPlayer && foundPlayerToFlip) {
                    return toFlip;
                } else {
                    return null;
                }
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
