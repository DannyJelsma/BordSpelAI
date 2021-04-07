package nl.hanze.bordspelai.views;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import nl.hanze.bordspelai.controllers.GameController;

public class ReversiView extends View {
  // buttons

  public ReversiView(GameController controller) {
    super("/views/game.fxml", controller);
  }  
}
