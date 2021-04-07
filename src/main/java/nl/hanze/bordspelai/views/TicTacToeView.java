package nl.hanze.bordspelai.views;

import nl.hanze.bordspelai.controllers.Controller;

public class TicTacToeView extends View {

    public TicTacToeView(Controller controller) {
        super("/views/game.fxml", controller);
    }
}
