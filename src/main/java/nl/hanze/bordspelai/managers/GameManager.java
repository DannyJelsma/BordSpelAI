package nl.hanze.bordspelai.managers;

public class GameManager {

    public static GameManager instance;

    private String username;

    private GameManager() {
    }

    public static GameManager getInstance() {
        if (instance != null) {
            instance = new GameManager();
        }

        return instance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
