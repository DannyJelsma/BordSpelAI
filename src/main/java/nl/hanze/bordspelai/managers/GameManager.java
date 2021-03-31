package nl.hanze.bordspelai.managers;

import nl.hanze.bordspelai.enums.GameState;

public class GameManager {

    public static GameManager instance;

    private String username;
    private GameState state;

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
        this.state = state;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
