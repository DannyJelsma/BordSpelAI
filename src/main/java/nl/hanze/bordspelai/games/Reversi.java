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
    private final Map<BoardState, Integer> minimaxCache = new HashMap<>();
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

    public void clearCache() {
        minimaxCache.clear();
    }

    @Override
    public List<Integer> getAvailablePositions(Board board, char playerToCheck) {
        List<Integer> availablePositions = new ArrayList<>();
        for (int pos = 0; pos < board.getBoard().length; pos++) {
            char otherChar = playerToCheck == ownChar ? opponentChar : ownChar;
            if (this.isValidMove(board, pos, otherChar)) {
                for (Direction dir : Direction.values()) {
                    List<Integer> flippedChips = getFlippedChips(board, dir, pos, playerToCheck, otherChar);

                    if (flippedChips != null) {
                        if (!availablePositions.contains(pos)) {
                            availablePositions.add(pos);
                        }
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
        Board newBoard = new Board(getBoard());
        long endTime = System.currentTimeMillis() + 5000;
        int highestScore = Integer.MIN_VALUE;
        int bestMove = Integer.MIN_VALUE;
        List<Integer> availableMoves = getAvailablePositions(newBoard, ownChar);

        System.out.println("Available: " + availableMoves);
        availableMoves.sort(Comparator.comparingInt(move -> doHeuristics(newBoard, (int) move, false)).reversed());

        for (int move : availableMoves) {
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
        List<Integer> avail = getAvailablePositions(getBoard(), ownChar);
        if (!getAvailablePositions(getBoard(), ownChar).contains(bestMove)) {
            System.out.println("ERROR: Tried to do an illegal move at " + bestMove + ".");

            return avail.get(ThreadLocalRandom.current().nextInt(0, avail.size()));
        }

        return bestMove;
    }

    public void doBackgroundCalculations() {
        calculations = 0;
        cacheHits = 0;
        long endTime = System.currentTimeMillis() + 7500;
        Board newBoard = new Board(getBoard());

        // Fill cache while waiting for the opponent
        List<Integer> availablePositions = getAvailablePositions(newBoard, opponentChar);
        availablePositions.sort(Comparator.comparingInt(move -> doHeuristics(newBoard, move, true)));
        while (manager.getState().equals(GameState.OPPONENT_TURN)) {
            for (int move : availablePositions) {
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
                return 1000;
            } else if (winner == opponentChar) {
                return -1000;
            } else if (winner == 1) {
                return 0;
            }
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
        if (maximize) {
            int bestScore = Integer.MIN_VALUE;
            List<Integer> availablePositions = getAvailablePositions(board, ownChar);
            availablePositions.sort(Comparator.comparingInt(move -> doHeuristics(board, move, true)));

            for (int move : availablePositions) {
                if (System.currentTimeMillis() > endTime) {
                    int score = doHeuristics(board, move, true);

                    return Math.max(score, bestScore);
                }

                List<Integer> flippedChips = getAllFlippedChips(board, move, ownChar, opponentChar);
                board.setPosition(move, ownChar);

                for (int chip : flippedChips) {
                    board.setPosition(chip, ownChar);
                }

                //System.out.println(Arrays.toString(board.getBoard()) + " -> " + Arrays.toString(board.getBoard()));

                BoardState state = new BoardState(board.hashCode(), depth, move);
                int score;
                if (minimaxCache.containsKey(state)) {
                    cacheHits++;
                    score = minimaxCache.get(state);
                } else {
                    score = minimax(board, depth + 1, false, alpha, beta, endTime, isBackgroundTask);
                    minimaxCache.put(state, bestScore);
                }

                bestScore = Math.max(score, bestScore);
                alpha = Math.max(alpha, bestScore);

                board.clearPosition(move);
                for (int chip : flippedChips) {
                    board.setPosition(chip, opponentChar);
                }

                if (beta <= alpha) break;
            }

            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            List<Integer> availablePositions = getAvailablePositions(board, opponentChar);
            availablePositions.sort(Comparator.comparingInt(move -> doHeuristics(board, (int) move, false)).reversed());

            for (int move : availablePositions) {
                if (System.currentTimeMillis() > endTime) {
                    int score = doHeuristics(board, move, false);

                    return Math.max(score, bestScore);
                }

                List<Integer> flippedChips = getAllFlippedChips(board, move, opponentChar, ownChar);
                board.setPosition(move, opponentChar);

                for (int chip : flippedChips) {
                    board.setPosition(chip, opponentChar);
                }

                //System.out.println(Arrays.toString(board.getBoard()) + " -> " + Arrays.toString(board.getBoard()));

                BoardState state = new BoardState(board.hashCode(), depth, move);
                int score;
                if (minimaxCache.containsKey(state)) {
                    cacheHits++;
                    score = minimaxCache.get(state);
                } else {
                    score = minimax(board, depth + 1, true, alpha, beta, endTime, isBackgroundTask);
                    minimaxCache.put(state, bestScore);
                }

                bestScore = Math.min(score, bestScore);
                beta = Math.min(beta, bestScore);

                board.clearPosition(move);
                for (int chip : flippedChips) {
                    board.setPosition(chip, ownChar);
                }

                if (beta <= alpha) break;
            }

            return bestScore;
        }
    }

    // TODO: Finish heuristic. Search for more information about good and bad moves. Tweak values.
/*    // Positive
    private static final List<Integer> CORNERS = List.of(0, 7, 56, 63);

    // Negative
    private static final List<Integer> X_SQUARES = List.of(9, 49, 14, 54);
    private static final List<Integer> C_SQUARES = List.of(8, 1, 6, 15, 48, 57, 55, 62);*/

    // Weights taken from the follwing source (Chapter 5.2): https://courses.cs.washington.edu/courses/cse573/04au/Project/mini1/RUSSIA/Final_Paper.pdf
    private static final int[] WEIGHTS = new int[]
            {4, -3, 2, 2, 2, 2, -3, 4,
                    -3, -4, -1, -1, -1, -1, -4, -3,
                    2, -1, 1, 0, 0, 1, -1, 2,
                    2, -1, 0, 1, 1, 0, -1, 2,
                    2, -1, 0, 1, 1, 0, -1, 2,
                    2, -1, 1, 0, 0, 1, -1, 2,
                    -3, -4, -1, -1, -1, -1, -4, -3,
                    4, -3, 2, 2, 2, 2, -3, 4};

    private int doHeuristics(Board board, int move, boolean maximize) {
        int score = 0;
        char charToCheck = maximize ? ownChar : opponentChar;
        char otherChar = maximize ? opponentChar : ownChar;

        if (board.getAllUsedPositions().size() >= 35) {
            int opponentAmount = board.getAmount(opponentChar);
            int ourAmount = board.getAmount(ownChar);

            if (maximize) {
                score += ourAmount - opponentAmount;
            } else {
                score -= opponentAmount - ourAmount;
            }
        } else {
            score += getAvailablePositions(board, charToCheck).size();
            score -= getAvailablePositions(board, otherChar).size();
        }

        score += 10 * WEIGHTS[move];

        List<Integer> flippedChips = getAllFlippedChips(board, move, charToCheck, otherChar);

        for (int flipped : flippedChips) {
            score += 7.5 * WEIGHTS[flipped];
        }

        return score;
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
