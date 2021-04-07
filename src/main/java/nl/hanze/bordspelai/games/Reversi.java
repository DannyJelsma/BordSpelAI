package nl.hanze.bordspelai.games;

import java.util.ArrayList;

public class Reversi extends Game {

  public Reversi(String startingPlayer) {
    super(8, startingPlayer);
  }

  @Override
  public ArrayList<Integer> getAvailablePositions(Board board) {
    return null;
  }

  @Override
  public int doBestMove() {
    // todo: exeption when there are no valid moves

    return 0;
  }
}
