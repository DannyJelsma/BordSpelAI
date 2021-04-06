package nl.hanze.bordspelai.models;

import nl.hanze.bordspelai.games.Game;

public class GameModel implements Model {

    private final Game game;

    public GameModel(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }
}
