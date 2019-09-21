package co.q64.pychallenge.server.util;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.json.JSONObject;

import co.q64.pychallenge.server.game.Game;
import co.q64.pychallenge.server.net.Socket;
import co.q64.pychallenge.server.user.User;

@Singleton
public class MessageHandler {
	private @Inject Game game;
	private @Inject Logger logger;

	public void handle(User user, String text) {
		if (Socket.DEBUG_SOCKET) {
			logger.info("RX: " + text);
		}
		JSONObject message = new JSONObject(text);
		switch (message.getString("type")) {
		case "username":
			String name = message.getString("username");
			if (name.length() > 25) {
				name = name.substring(0, 24);
			}
			user.setUsername(name);
			break;
		case "submit":
			if (message.getString("submission").toLowerCase().contains("while")) {
				return;
			}
			user.setSubmission(message.getString("submission"));
			user.setSubmissionTime(System.currentTimeMillis());
			break;
		}
	}
}
