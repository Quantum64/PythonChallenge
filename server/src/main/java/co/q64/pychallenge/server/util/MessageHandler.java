package co.q64.pychallenge.server.util;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.json.JSONObject;

import co.q64.pychallenge.server.game.Game;
import co.q64.pychallenge.server.user.User;

@Singleton
public class MessageHandler {
	private @Inject Game game;

	public void handle(User user, String text) {
		JSONObject message = new JSONObject(text);
		switch (message.getString("type")) {
		case "username":
			user.setUsername(message.getString("username"));
			break;
		case "submit":
			user.setSubmission(message.getString("submission"));
		}
	}
}
