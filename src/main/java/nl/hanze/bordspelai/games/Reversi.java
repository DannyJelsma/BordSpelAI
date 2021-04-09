package nl.hanze.bordspelai.games;

import nl.hanze.bordspelai.managers.GameManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
        for (int pos = 0; pos < getBoard().getBoard().length; pos++) {
            char otherChar = playerToCheck == ownChar ? opponentChar : ownChar;
            if (this.isValidMove(pos, otherChar)) {
                for (Direction dir : Direction.values()) {
                    List<Integer> flippedChips = getFlippedChips(dir, pos, playerToCheck, otherChar);

                    if (flippedChips != null) {
                        int flipAmount = flippedChips.size();

                        if (flipAmount > 0) {
                            availablePositions.add(pos);
                            break;
                        }
                    }
                }
            }
        }

        return availablePositions;
    }

    @Override
    public int doBestMove() {
        ArrayList<Integer> availablePositions = getAvailablePositions(getBoard(), ownChar);
        System.out.println("Available pos: " + availablePositions);

        return availablePositions.get(ThreadLocalRandom.current().nextInt(0, availablePositions.size()));
    }

    private boolean isValidMove(int pos, char opponent) {
        //System.out.println("Pos = " + pos);

        if (getBoard().getPosition(pos) != ownChar && getBoard().getPosition(pos) != opponentChar) {
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
                if (checkChip(dir, pos) == opponent) {
                    return true;
                }
            }

            return false;
        }

        return false;
    }

    public boolean hasGameEnded() {
        return getAvailablePositions(getBoard(), ownChar).size() == 0 && getAvailablePositions(getBoard(), opponentChar).size() == 0;
    }

    public char getWinner() {
        if (!hasGameEnded()) {
            return 0;
        }

        int ownChips = getBoard().getAmount(ownChar);
        int opponentChips = getBoard().getAmount(opponentChar);

        if (ownChips > opponentChips) {
            return ownChar;
        } else {
            return opponentChar;
        }
    }

    @Override
    public void addMove(int position, char charToMove) {
        for (Direction dir : Direction.values()) {
            List<Integer> flipped;
            if (charToMove == ownChar) {
                flipped = getFlippedChips(dir, position, ownChar, opponentChar);
            } else {
                flipped = getFlippedChips(dir, position, opponentChar, ownChar);
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

    public List<Integer> getFlippedChips(Direction direction, int pos, char movingPlayer, char playerToFlip) {
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

            char chipChar = getBoard().getPosition(newPos);

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

    private char checkChip(Direction direction, int pos) {
        int chipPos = getChipPosition(direction, pos);

        if (chipPos == -1) {
            return 0;
        }

        return getBoard().getPosition(chipPos);
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
