package nl.hanze.bordspelai.models;

public abstract class GameModel implements Model {
  private int size;
  private int[] board;
  public GameModel(int size) {
    this.size = size;
    board = new int[size * size];
  }

  public int getSize() {
    return size;
  }

  public int[] getBoard() {
    return board;
  }

  // public abstract int getBestMove();
  // private abstract int[] getValidMoves();
}
