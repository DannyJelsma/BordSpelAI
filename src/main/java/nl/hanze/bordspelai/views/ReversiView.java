package nl.hanze.bordspelai.views;

import nl.hanze.bordspelai.controllers.GameController;

public class ReversiView extends View {
  // buttons

  public ReversiView(GameController controller) {
    super("/views/game.fxml", controller);
  }  
}
