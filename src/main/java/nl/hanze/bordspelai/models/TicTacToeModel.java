package nl.hanze.bordspelai.models;

import nl.hanze.bordspelai.BordspelAI;
import nl.hanze.bordspelai.enums.Command;
import nl.hanze.bordspelai.net.Server;

import java.util.ArrayList;
import java.util.Random;

public class TicTacToeModel extends GameModel {

    public TicTacToeModel(String ownUsername, String opponentUsername) {
        super(3, ownUsername, opponentUsername);
    }

    @Override
    public int doBestMove() {
        ArrayList<Integer> availableMoves = this.getAvailablePositions();
        int bestMove = 0;

        for (int move: availableMoves) {
            bestMove = new Random().nextInt(availableMoves.size()); // random (valid) move
        }

        return bestMove;
    }
}
