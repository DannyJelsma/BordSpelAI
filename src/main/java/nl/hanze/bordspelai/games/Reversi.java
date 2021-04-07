package nl.hanze.bordspelai.games;

import nl.hanze.bordspelai.controllers.GameController;

public class Reversi extends Game {

  public Reversi(String startingPlayer) {
    super(8, startingPlayer);
  }

  @Override
  public int doBestMove() {
    // todo: exeption when there are no valid moves

    return 0;
  }
}
