package nl.hanze.bordspelai.enums;

public enum Game {
    TIC_TAC_TOE("Tic-tac-toe"),
    REVERSI("Reversi");

    String game;

    Game(String game) {
        this.game = game;
    }

    public String getGame() {
        return game;
    }
}
