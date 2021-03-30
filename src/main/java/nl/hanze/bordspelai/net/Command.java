package nl.hanze.bordspelai.net;

public enum Command {

    LOGIN("login"),
    LOGOUT("logout"),
    GET_PLAYERLIST("get playerlist"),
    GET_GAMELIST("get gamelist"),
    SUBSCRIBE("subscribe"),
    MOVE("move"),
    CHALLENGE("challenge"),
    CHALLENGE_ACCEPT("challenge accept"),
    FORFEIT("forfeit");

    private final String command;

    Command(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
