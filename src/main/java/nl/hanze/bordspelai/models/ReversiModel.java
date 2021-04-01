package nl.hanze.bordspelai.models;

import javafx.scene.control.Button;

public class ReversiModel extends GameModel {

  public ReversiModel() {
    super(8);
  }

  @Override
  public void addMove(int move, String player) {

  }

  @Override
  public int doBestMove() {
    // todo: exeption when there are no valid moves

    return 0;
  }

  @Override
  public void updateMove(Button btn, int position) {
    // TODO Auto-generated method stub

  }


}
