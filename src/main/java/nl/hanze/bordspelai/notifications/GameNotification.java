package nl.hanze.bordspelai.notifications;

public class GameNotification extends Notification {

    private String notificationType;
    private String data;

    public GameNotification(String message) {
        super(message);
    }

    @Override
    protected String getData() {
        return data;
    }

    @Override
    public void parse(String message) {
        String[] split = message.split(" ", 4);

        if (!split[0].equals("SVR") || split.length < 4 || split[1].equals("HELP")) {
            return;
        }

        for (int i = 0; i < split.length; i++) {
            if (split[i].equals("SVR") || split[i].equals("GAME")) {
                continue;
            }

            if (i == 2) {
                notificationType = split[i];
            } else if (i == 3) {
                data = split[i];
            }
        }
    }

    @Override
    public String getNotificationType() {
        return notificationType;
    }
}
