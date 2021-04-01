package nl.hanze.bordspelai.models;

public class ReversiModel extends GameModel {

  public ReversiModel(String ownUsername, String opponentUsername) {
    super(8, ownUsername, opponentUsername);
  }

  @Override
  public int doBestMove() {
    // todo: exeption when there are no valid moves

    return 0;
  }
}
