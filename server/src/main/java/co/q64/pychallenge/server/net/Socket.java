package co.q64.pychallenge.server.net;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;

import co.q64.pychallenge.server.game.Game;
import co.q64.pychallenge.server.util.MessageHandler;

@WebSocket
@Singleton
public class Socket {
	public static final boolean DEBUG_SOCKET = false;

	private @Inject Logger logger;
	private @Inject Game game;
	private @Inject MessageHandler messageHandler;

	private Map<Session, UUID> users = new HashMap<>();

	@OnWebSocketConnect
	public void onConnect(Session user) throws Exception {
		UUID id = UUID.randomUUID();
		if (DEBUG_SOCKET) {
			logger.info("User connected: " + id);
		}
		users.put(user, id);
		game.userConnect(id);
	}

	@OnWebSocketClose
	public void onClose(Session user, int statusCode, String reason) {
		game.userDisconnect(users.get(user));
		users.remove(user);
		if (DEBUG_SOCKET) {
			logger.info("User connected: " + users.get(user));
		}
	}

	@OnWebSocketMessage
	public void onMessage(Session user, String message) {
		try {
			game.getUser(users.get(user)).ifPresent(u -> {
				messageHandler.handle(u, message);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void broadcast(JSONObject message) {
		if (DEBUG_SOCKET) {
			logger.info("Broadcasting: " + message.toString());
		}
		users.keySet().forEach(user -> {
			try {
				user.getRemote().sendString(message.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public void send(UUID id, JSONObject message) {
		for (Entry<Session, UUID> e : users.entrySet()) {
			if (e.getValue().equals(id)) {
				try {
					e.getKey().getRemote().sendString(message.toString());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}