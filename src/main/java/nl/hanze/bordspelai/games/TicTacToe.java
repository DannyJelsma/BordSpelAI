package nl.hanze.bordspelai.games;

import nl.hanze.bordspelai.managers.GameManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class TicTacToe extends Game {
    public TicTacToe(String startingPlayer) {
        super(3, startingPlayer);
    }

    @Override
    public ArrayList<Integer> getAvailablePositions() {
        ArrayList<Integer> availablePositions = new ArrayList<>();
        for (int i = 0; i < this.board.getSize(); i++) {
            if (board.isPositionAvailable(i)) {
                availablePositions.add(i);
            }
        }

        System.out.println(availablePositions);

        if (availablePositions.size() == 0) {
            board.reset();

            return getAvailablePositions();
        }

        return availablePositions;
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
}
