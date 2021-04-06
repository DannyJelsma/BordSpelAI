package nl.hanze.bordspelai.managers;

import nl.hanze.bordspelai.enums.GameState;
import nl.hanze.bordspelai.enums.Mode;
import nl.hanze.bordspelai.games.Game;

public class GameManager {

    public static GameManager instance;

    private String username;
    private String opponent;
    private GameState state;
    private Mode mode;
    private Game game;

    private GameManager() {
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }

        return instance;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        System.out.println("Set state to " + state);
        this.state = state;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        System.out.println("Set username to " + username);
        this.username = username;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        System.out.println("Set opponent to " + opponent);
        this.opponent = opponent;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        System.out.println("Set mode to " + mode);
        this.mode = mode;
    }

    public String getCurrentPlayer() {
        String player = null;

        if (this.state == GameState.YOUR_TURN) {
            player = this.username;
        } else if (this.state == GameState.OPPONENT_TURN) {
            player = this.opponent;
        }
        return player;
    }
}
