package nl.hanze.bordspelai.listeners;

import nl.hanze.bordspelai.BordspelAI;
import nl.hanze.bordspelai.events.NetEventListener;
import nl.hanze.bordspelai.net.Command;
import nl.hanze.bordspelai.notifications.Notification;

import java.util.Map;

public class ChallengeReceiveListener implements NetEventListener {

    @Override
    public void update(Notification notification) {
        if (notification.getNotificationType().equals("CHALLENGE")) {
            if (notification.isMap()) {
                Map<String, String> data = notification.getDataMap();
                String challengeNumber = data.get("CHALLENGENUMBER");

                BordspelAI.getServer().sendCommand(Command.CHALLENGE_ACCEPT, challengeNumber);
            }
        }
    }
}
