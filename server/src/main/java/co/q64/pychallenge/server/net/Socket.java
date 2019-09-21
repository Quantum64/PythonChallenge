package co.q64.pychallenge.server.net;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import co.q64.pychallenge.server.game.Game;

@WebSocket
@Singleton
public class Socket {
	private @Inject Game game;
	
	private Map<Session, UUID> users = new HashMap<>();

	@OnWebSocketConnect
	public void onConnect(Session user) throws Exception {
		UUID id = UUID.randomUUID();
		users.put(user, id);
		game.userConnect(id);
	}

	@OnWebSocketClose
	public void onClose(Session user, int statusCode, String reason) {
		String username = Chat.userUsernameMap.get(user);
		Chat.userUsernameMap.remove(user);
		Chat.broadcastMessage(sender = "Server", msg = (username + " left the chat"));
	}

	@OnWebSocketMessage
	public void onMessage(Session user, String message) {
		Chat.broadcastMessage(sender = Chat.userUsernameMap.get(user), msg = message);
	}

	
}