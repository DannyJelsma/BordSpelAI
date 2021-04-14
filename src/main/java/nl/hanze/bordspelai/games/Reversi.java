package nl.hanze.bordspelai.games;

import nl.hanze.bordspelai.enums.GameState;
import nl.hanze.bordspelai.managers.GameManager;
import nl.hanze.bordspelai.managers.SceneManager;
import nl.hanze.bordspelai.minimax.MinimaxCache;
import nl.hanze.bordspelai.minimax.MinimaxResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.Label;

public class Reversi extends Game {

    private static final char NO_CHIP = 0;

    private final char ownChar;
    private final char opponentChar;
    private final MinimaxCache minimaxCache = new MinimaxCache();
    private int cacheHits = 0;
    private int calculations = 0;
    private static final int MAX_THREADS = Runtime.getRuntime().availableProcessors() - 2;
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(MAX_THREADS, MAX_THREADS, 0L, TimeUnit.MILLISECONDS, queue);

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

    private static final List<Integer> CORNERS = List.of(0, 7, 56, 63);

    @Override
    public int doBestMove() {
        calculations = 0;
        cacheHits = 0;
        long endTime = System.currentTimeMillis() + 5000;
        List<Integer> availableMoves = getAvailablePositions(getBoard(), ownChar);

        for (int corner : CORNERS) {
            if (availableMoves.contains(corner)) {
                return corner;
            }
        }

        System.out.println("Available: " + availableMoves);
        int bestMove = parallelMinimax(getBoard(), endTime, false);

        System.out.println("Cache hits: " + cacheHits);
        System.out.println("Calculations: " + calculations);
        System.out.println("Cache hit ratio: " + ((float) cacheHits / (float) calculations * 100f));
        System.out.println("Cache Size: " + minimaxCache.getSize());

        // Failsafe
        List<Integer> avail = getAvailablePositions(getBoard(), ownChar);
        if (!getAvailablePositions(getBoard(), ownChar).contains(bestMove)) {
            System.out.println("ERROR: Tried to do an illegal move at " + bestMove + ".");

            return avail.get(ThreadLocalRandom.current().nextInt(0, avail.size()));
        }

        return bestMove;
    }

    private int parallelMinimax(Board board, long endTime, boolean isBackgroundTask) {
        List<Future> runningTasks = new ArrayList<>();
        MinimaxResult result = new MinimaxResult();
        char charToCheck = isBackgroundTask ? opponentChar : ownChar;
        char otherChar = isBackgroundTask ? ownChar : opponentChar;
        List<Integer> availableMoves = getAvailablePositions(board, charToCheck);

        modifyAvailableMoves(availableMoves);

        for (int move : availableMoves) {
            Future future = executor.submit(() -> {
                try {
                    Board newBoard = new Board(board);

                    List<Integer> flippedChips = getAllFlippedChips(newBoard, move, charToCheck, otherChar);
                    newBoard.setPosition(move, charToCheck);

                    for (int chip : flippedChips) {
                        newBoard.setPosition(chip, charToCheck);
                    }

                    int score = minimax(newBoard, 0, false, Integer.MIN_VALUE, Integer.MAX_VALUE, endTime, isBackgroundTask);
                    int bestScore = result.getScore().get();

                    if (score > bestScore) {
                        result.setScore(score);
                        result.setMove(move);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            runningTasks.add(future);
        }

        while (runningTasks.size() > 0) {
            if (System.currentTimeMillis() > endTime + 1000) {
                queue.clear();

                for (Future task : runningTasks) {
                    task.cancel(true);
                }

                return result.getMove().get();
            }

            if (isBackgroundTask && manager.getState().equals(GameState.YOUR_TURN)) {
                queue.clear();

                for (Future task : runningTasks) {
                    task.cancel(true);
                }

                return -1;
            }

            runningTasks.removeIf(Future::isDone);
        }

        queue.clear();
        return result.getMove().get();
    }

    private void modifyAvailableMoves(List<Integer> availableMoves) {
        List<Integer> X_SQUARES = new ArrayList<>(List.of(9, 49, 14, 54));
        List<Integer> C_SQUARES = new ArrayList<>(List.of(8, 1, 6, 15, 48, 57, 55, 62));

        if (getBoard().getPosition(0) == ownChar) {
            X_SQUARES.remove((Integer) 9);
            C_SQUARES.remove((Integer) 8);
            C_SQUARES.remove((Integer) 1);
        }

        if (getBoard().getPosition(7) == ownChar) {
            X_SQUARES.remove((Integer) 14);
            C_SQUARES.remove((Integer) 6);
            C_SQUARES.remove((Integer) 15);
        }

        if (getBoard().getPosition(56) == ownChar) {
            X_SQUARES.remove((Integer) 49);
            C_SQUARES.remove((Integer) 48);
            C_SQUARES.remove((Integer) 57);
        }

        if (getBoard().getPosition(63) == ownChar) {
            X_SQUARES.remove((Integer) 54);
            C_SQUARES.remove((Integer) 55);
            C_SQUARES.remove((Integer) 62);
        }

        int counter = 0;
        for (int square : X_SQUARES) {
            if (availableMoves.contains(square)) {
                counter++;
            }
        }

        for (int square : C_SQUARES) {
            if (availableMoves.contains(square)) {
                counter++;
            }
        }

        if (availableMoves.size() > counter) {
            availableMoves.removeAll(X_SQUARES);
            availableMoves.removeAll(C_SQUARES);
        }
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

            return ourAmount - opponentAmount;
        }

        calculations++;
        if (maximize) {
            int bestScore = Integer.MIN_VALUE;
            List<Integer> availablePositions = getAvailablePositions(board, ownChar);

            if (availablePositions.size() == 0) {
                int opponentAmount = board.getAmount(opponentChar);
                int ourAmount = board.getAmount(ownChar);

                return Math.max(ourAmount - opponentAmount, bestScore);
            }

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

                BoardState state = new BoardState(board.hashCode(), depth, move);
                int size = board.getSize();
                int score;
                if (minimaxCache.containsState(state, size)) {
                    cacheHits++;
                    score = minimaxCache.getScoreForState(state, size);

                    if (score == Integer.MIN_VALUE) {
                        score = minimax(board, depth + 1, false, alpha, beta, endTime, isBackgroundTask);
                    }
                } else {
                    score = minimax(board, depth + 1, false, alpha, beta, endTime, isBackgroundTask);
                    minimaxCache.addBoardState(state, size, bestScore);
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

            if (availablePositions.size() == 0) {
                int opponentAmount = board.getAmount(opponentChar);
                int ourAmount = board.getAmount(ownChar);

                return Math.min(ourAmount - opponentAmount, bestScore);
            }

            for (int move : availablePositions) {
                if (System.currentTimeMillis() > endTime) {
                    int score = doHeuristics(board, move, false);

                    return Math.min(score, bestScore);
                }

                List<Integer> flippedChips = getAllFlippedChips(board, move, opponentChar, ownChar);
                board.setPosition(move, opponentChar);

                for (int chip : flippedChips) {
                    board.setPosition(chip, opponentChar);
                }

                BoardState state = new BoardState(board.hashCode(), depth, move);
                int size = board.getSize();
                int score;
                if (minimaxCache.containsState(state, size)) {
                    cacheHits++;
                    score = minimaxCache.getScoreForState(state, size);

                    if (score == Integer.MIN_VALUE) {
                        score = minimax(board, depth + 1, false, alpha, beta, endTime, isBackgroundTask);
                    }
                } else {
                    score = minimax(board, depth + 1, true, alpha, beta, endTime, isBackgroundTask);
                    minimaxCache.addBoardState(state, size, bestScore);
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

    // Weights taken from the follwing source (Chapter 5.2): https://courses.cs.washington.edu/courses/cse573/04au/Project/mini1/RUSSIA/Final_Paper.pdf
    private int[] getWeights(Board board, boolean maximize) {
        char charToCheck = maximize ? ownChar : opponentChar;
        int[] weights = new int[]
                {4, -3, 2, 2, 2, 2, -3, 4,
                        -3, -4, -1, -1, -1, -1, -4, -3,
                        2, -1, 1, 0, 0, 1, -1, 2,
                        2, -1, 0, 1, 1, 0, -1, 2,
                        2, -1, 0, 1, 1, 0, -1, 2,
                        2, -1, 1, 0, 0, 1, -1, 2,
                        -3, -4, -1, -1, -1, -1, -4, -3,
                        4, -3, 2, 2, 2, 2, -3, 4};

        // XSquares and CSquares become important when you are in the corner, as they become unflippable.
        if (board.getPosition(0) == charToCheck) {
            weights[1] = 2;
            weights[8] = 2;
            weights[9] = 1;
        }

        if (board.getPosition(7) == charToCheck) {
            weights[6] = 2;
            weights[14] = 1;
            weights[15] = 2;
        }

        if (board.getPosition(56) == charToCheck) {
            weights[48] = 2;
            weights[49] = 1;
            weights[57] = 2;
        }

        if (board.getPosition(63) == charToCheck) {
            weights[54] = 1;
            weights[55] = 2;
            weights[62] = 2;
        }

        return weights;
    }

    public List<Character> getAllDiscsOnRow(Board board, int move) {
        List<Character> chips = new ArrayList<>();

        if (move >= 0 && move <= 7) {
            for (int i = 0; i < 7; i++) {
                chips.add(board.getPosition(i));
            }
        } else if (move >= 8 && move <= 15) {
            for (int i = 8; i < 15; i++) {
                chips.add(board.getPosition(i));
            }
        } else if (move >= 16 && move <= 23) {
            for (int i = 16; i < 23; i++) {
                chips.add(board.getPosition(i));
            }
        } else if (move >= 24 && move <= 31) {
            for (int i = 24; i < 31; i++) {
                chips.add(board.getPosition(i));
            }
        } else if (move >= 32 && move <= 39) {
            for (int i = 32; i < 39; i++) {
                chips.add(board.getPosition(i));
            }
        } else if (move >= 40 && move <= 47) {
            for (int i = 40; i < 47; i++) {
                chips.add(board.getPosition(i));
            }
        } else if (move >= 48 && move <= 55) {
            for (int i = 48; i < 55; i++) {
                chips.add(board.getPosition(i));
            }
        } else if (move >= 56 && move <= 63) {
            for (int i = 56; i < 63; i++) {
                chips.add(board.getPosition(i));
            }
        }

        return chips;
    }

    private int doHeuristics(Board board, int move, boolean maximize) {
        int[] weights = getWeights(board, maximize);
        int score = 0;
        char charToCheck = maximize ? ownChar : opponentChar;
        char otherChar = maximize ? opponentChar : ownChar;

        if (isStable(board, move, maximize)) {
            if (maximize) {
                score += 50;
            } else {
                score -= 50;
            }
        }

        if (board.getAllUsedPositions().size() > 48) {
            int opponentAmount = getBoard().getAmount(opponentChar);
            int ourAmount = getBoard().getAmount(ownChar);

            if (maximize) {
                score += ourAmount - opponentAmount;
            } else {
                score -= opponentAmount - ourAmount;
            }
        }

        if (maximize) {
            score += 200 * weights[move];
        } else {
            score -= 200 * weights[move];
        }

        List<Integer> flippedChips = getAllFlippedChips(board, move, charToCheck, otherChar);

        for (int flipped : flippedChips) {
            if (maximize) {
                score += 50 * weights[flipped];
            } else {
                score -= 50 * weights[flipped];
            }

            if (isStable(board, flipped, maximize)) {
                if (maximize) {
                    score += 40;
                } else {
                    score -= 40;
                }
            }
        }

        return score;
    }

    private boolean isStable(Board board, int pos, boolean maximize) {
        boolean onTopColumn = pos <= 7;
        boolean onRightRow = pos % 8 == 7;
        boolean onBottomColumn = pos >= 56;
        boolean onLeftRow = pos % 8 == 0;
        char otherChar = maximize ? opponentChar : ownChar;

        boolean stable = true;

        for (Direction dir : Direction.values()) {
            int positionInDirection = getChipPosition(dir, pos);

            if (positionInDirection == -1) {
                continue;
            }

            List<Character> row = getAllDiscsOnRow(board, positionInDirection);
            if (row.contains(otherChar) || row.contains(NO_CHIP)) {
                if (onTopColumn || onRightRow || onBottomColumn || onLeftRow) {
                    stable = false;
                    break;
                }

                if (!isStable(board, positionInDirection, maximize)) {
                    stable = false;
                    break;
                }

                stable = false;
                break;
            }
        }

        return stable;
    }

    private boolean isValidMove(Board board, int pos, char opponent) {
        if (board.getPosition(pos) == NO_CHIP) {

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

        minimaxCache.removeRedundantEntries(getBoard().getSize());
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

    public void reset() {
        minimaxCache.reset();
        queue.clear();
        try {
            executor.awaitTermination(2500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        boolean onTopColumn = pos <= 7;
        boolean onRightRow = pos % 8 == 7;
        boolean onBottomColumn = pos >= 56;
        boolean onLeftRow = pos % 8 == 0;

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

        return -1;
    }
}
