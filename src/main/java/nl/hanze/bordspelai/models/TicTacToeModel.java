package nl.hanze.bordspelai.models;

import nl.hanze.bordspelai.enums.GameState;
import nl.hanze.bordspelai.managers.GameManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class TicTacToeModel extends GameModel {

    private final char ownChar;
    private final char opponentChar;

    public TicTacToeModel() {
        super(3);
        super.reset();

        GameManager manager = GameManager.getInstance();

        // set players char
        if (manager.getState() == GameState.YOUR_TURN) {
            this.ownChar = 'x';
            this.opponentChar = 'o';
        } else {
            this.ownChar = 'o';
            this.opponentChar = 'x';
        }
    }

    private char getCharByUsername(String username) {
        GameManager manager = GameManager.getInstance();
        char playerChar;
        if (manager.getUsername().equals(username)) {
            playerChar = this.ownChar;
        } else {
            playerChar = this.opponentChar;
        }
        return playerChar;
    }

    @Override
    public int doBestMove() {
        ArrayList<Integer> availableMoves = this.getAvailablePositions();
        int bestMove = 0;

//        for (int move: availableMoves) {
        bestMove = availableMoves.get(new Random().nextInt(availableMoves.size())); // random (valid) move
//        }

        //this.board[bestMove] = this.getCharByUsername(manager.getCurrentPlayer());
        return bestMove;
    }

    public void addMove(int position, String player) {
        GameManager manager = GameManager.getInstance();
        this.board[position] = this.getCharByUsername(manager.getCurrentPlayer());

        System.out.println(Arrays.toString(this.board));
    }
}
