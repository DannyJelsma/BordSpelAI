package nl.hanze.bordspelai.games;

import nl.hanze.bordspelai.managers.GameManager;

import java.util.ArrayList;
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
            this.ownChar = 'x';
            this.opponentChar = 'o';
        } else {
            this.ownChar = 'o';
            this.opponentChar = 'x';
        }
    }

    @Override
    public ArrayList<Integer> getAvailablePositions(Board board) {
        ArrayList<Integer> availablePositions = new ArrayList<>();
        for (int pos = 0; pos < this.getBoard().length; pos++) {
            if (this.isValidMove(pos)) {
                for (Direction dir : Direction.values()) {
                    int flipAmount = getFlipAmount(dir, pos);

                    if (flipAmount > 0) {
                        availablePositions.add(pos);
                        break;
                    }
                }
            }
        }

        return availablePositions;
    }

    @Override
    public int doBestMove() {
        ArrayList<Integer> availablePositions = getAvailablePositions(board);
        System.out.println(availablePositions);

        return availablePositions.get(ThreadLocalRandom.current().nextInt(0, availablePositions.size()));
    }


    private boolean isValidMove(int pos) {
        boolean result = false;

        //System.out.println("Pos = " + pos);

        if (this.board.getPosition(pos) == NO_CHIP) {

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

            if (checkChip(Direction.TOP, pos) == this.opponentChar) {
                result = true;
            } else if (checkChip(Direction.TOP_RIGHT, pos) == this.opponentChar) {
                result = true;
            } else if (checkChip(Direction.RIGHT, pos) == this.opponentChar) {
                result = true;
            } else if (checkChip(Direction.BOTTOM_RIGHT, pos) == this.opponentChar) {
                result = true;
            } else if (checkChip(Direction.BOTTOM, pos) == this.opponentChar) {
                result = true;
            } else if (checkChip(Direction.BOTTOM_LEFT, pos) == this.opponentChar) {
                result = true;
            } else if (checkChip(Direction.LEFT, pos) == this.opponentChar) {
                result = true;
            } else if (checkChip(Direction.TOP_LEFT, pos) == this.opponentChar) {
                result = true;
            }
        }

        return result;
    }

    public int getFlipAmount(Direction direction, int pos) {
        if (checkChip(direction, pos) != this.opponentChar) {
            return 0;
        }

        if (pos < 0 || pos > 63) {
            return 0;
        }

        int newPos = pos;
        int counter = 0;
        while (newPos > -1) {
            newPos = getChipPosition(direction, newPos);
            char chipChar = board.getPosition(newPos);

            if (chipChar != this.opponentChar) {
                if (chipChar == this.ownChar) {
                    return counter;
                } else {
                    return 0;
                }
            }

            if (newPos == -1) {
                break;
            }

            counter++;
        }

        return counter;
    }

    private enum Direction {
        TOP, TOP_RIGHT, RIGHT, BOTTOM_RIGHT, BOTTOM, BOTTOM_LEFT, LEFT, TOP_LEFT
    }

    private char checkChip(Direction direction, int pos) {
        int chipPos = getChipPosition(direction, pos);

        if (chipPos == -1) {
            return 0;
        }

        return board.getPosition(chipPos);
    }

    private int getChipPosition(Direction direction, int pos) {
        //System.out.println("param pos "+pos);
        char player = NO_CHIP;

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
        return player;
    }
}
