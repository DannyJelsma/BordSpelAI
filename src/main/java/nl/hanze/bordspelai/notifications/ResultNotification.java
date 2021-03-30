package nl.hanze.bordspelai.notifications;

public class ResultNotification extends Notification {

    private String data;
    private String notificationType;

    public ResultNotification(String message) {
        super(message);
    }

    @Override
    protected String getData() {
        return data;
    }

    @Override
    protected void parse(String message) {
        String[] split = message.split(" ", 2);

        notificationType = split[0];

        if (split.length == 2) {
            data = "{MESSAGE: " + split[1] + "}";
        } else {
            data = "{MESSAGE: Command successfully executed}";
        }
    }

    @Override
    public String getNotificationType() {
        return notificationType;
    }
}
