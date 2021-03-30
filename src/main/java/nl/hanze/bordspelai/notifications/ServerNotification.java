package nl.hanze.bordspelai.notifications;

public class ServerNotification extends Notification {

    private String notificationType;
    private String data;

    public ServerNotification(String message) {
        super(message);
    }

    @Override
    protected String getData() {
        return data;
    }

    @Override
    public void parse(String message) {
        String[] split = message.split(" ", 3);

        if (!split[0].equals("SVR") || split.length != 3 || split[1].equals("HELP")) {
            return;
        }

        for (int i = 0; i < split.length; i++) {
            if (split[i].equals("SVR")) {
                continue;
            }

            if (i == 1) {
                notificationType = split[i];
            } else {
                data = split[i];
            }
        }
    }

    @Override
    public String getNotificationType() {
        return notificationType;
    }
}
