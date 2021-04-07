package nl.hanze.bordspelai.games;

import nl.hanze.bordspelai.managers.GameManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class TicTacToe extends Game {

    private final GameManager manager = GameManager.getInstance();
    private char ownChar;
    private char opponentChar;
    private String startingPlayer;

    public TicTacToe(String startingPlayer) {
        super(3);

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

    public String getStartingPlayer() {
        return startingPlayer;
    }

    private char getCharByUsername(String username) {
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
        board.setPosition(position, this.getCharByUsername(manager.getCurrentPlayer()));

        System.out.println(Arrays.toString(board.getBoard()));
    }
}
