package nl.hanze.bordspelai.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import nl.hanze.bordspelai.models.GameModel;

public class GameController implements Controller {
  @FXML
  private GridPane grid;

  private GameModel model;
  public GameController(GameModel model) {
    this.model = model;
  }

  @FXML
  public void initialize() {
    int board[] = model.getBoard();
    for(int i = 0; i < board.length; i++) {
      int row = (int) Math.ceil(i / model.getSize());
      int column = i - model.getSize() * row;

      System.out.println("X " + row + ", Y " + column );

      Button btn = new Button(row + " " + column);
      btn.setAlignment(Pos.CENTER);
      btn.maxWidth(Double.MAX_VALUE);
      btn.maxHeight(Double.MAX_VALUE);
      grid.add(btn, row, column);
    }
  }
}
