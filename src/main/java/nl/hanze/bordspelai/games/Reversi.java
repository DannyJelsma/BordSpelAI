package nl.hanze.bordspelai.games;

import javafx.scene.control.Button;
import nl.hanze.bordspelai.managers.GameManager;

import nl.hanze.bordspelai.controllers.GameController;

import java.util.ArrayList;

public class Reversi extends Game {

    private static final char NO_CHIP = ' ';

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
    public ArrayList<Integer> getAvailablePositions() {
        ArrayList<Integer> availablePositions = new ArrayList<>();
        for (int pos = 0; pos < this.getBoard().length; pos++) {
            if (this.isValidMove(pos)) {
                availablePositions.add(pos);
            }
        }
        return availablePositions;
    }

    @Override
    public int doBestMove() {
        // todo: exeption when there are no valid moves

        return 0;
    }


    private boolean isValidMove(int pos) {
        boolean result = false;

        if (this.board.getPosition(pos) != NO_CHIP) {

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

    private enum Direction {
        TOP, TOP_RIGHT, RIGHT, BOTTOM_RIGHT, BOTTOM, BOTTOM_LEFT, LEFT, TOP_LEFT
    }

    private char checkChip(Direction direction, int pos) {
        char player = NO_CHIP;

        boolean onTopColumn = pos < 9;
        boolean onRightRow = pos % 8 == 0;
        boolean onBottomColumn = pos > 56;
        boolean onLeftRow = pos % 8 == 1;

        switch (direction) {
            case TOP:
                if (!onTopColumn) {
                    player = this.board.getPosition(pos - 8);
                }
            case TOP_RIGHT:
                if (!onTopColumn && !onRightRow) {
                    player = this.board.getPosition(pos - 7);
                }
            case RIGHT:
                if (!onRightRow) {
                    player = this.board.getPosition(pos + 1);
                }
            case BOTTOM_RIGHT:
                if (!onRightRow && !onBottomColumn) {
                    player = this.board.getPosition(pos + 9);
                }
            case BOTTOM:
                if (!onBottomColumn) {
                    player = this.board.getPosition(pos + 8);
                }
            case BOTTOM_LEFT:
                if (!onBottomColumn && !onLeftRow) {
                    player = this.board.getPosition(pos + 7);
                }
            case LEFT:
                if (!onLeftRow) {
                    player = this.board.getPosition(pos - 1);
                }
            case TOP_LEFT:
                if (!onLeftRow && onTopColumn) {
                    player = this.board.getPosition(pos - 9);
                }
        }
        return player;
    }
}
