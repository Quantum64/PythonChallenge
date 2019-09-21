package co.q64.pychallenge.server.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.json.JSONObject;

import co.q64.pychallenge.server.net.Socket;
import co.q64.pychallenge.server.type.GamePhase;
import co.q64.pychallenge.server.user.User;

@Singleton
public class Game {
	private @Inject Provider<User> userProvider;
	private @Inject Socket socket;

	private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
	private GamePhase phase = GamePhase.WAITING;
	private List<User> users = new ArrayList<>();
	private String question, output;
	private int time;

	public void start() {
		promptQuestion();
		nextPhase();
	}
	
	public void nextPhase() {
		if (phase == GamePhase.WAITING) {
			phase = GamePhase.QUESTION;
			broadcastPhase();
			JSONObject payload = new JSONObject();
			payload.put("type", "question");
			payload.put("question", question);
			payload.put("time", time);
			socket.broadcast(payload);
		}
	}
	
	private void broadcastPhase() {
		JSONObject payload = new JSONObject();
		payload.put("type", "phase");
		payload.put("phase", phase.getName());
		socket.broadcast(payload);
	}

	public void promptQuestion() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("Please input the question: ");
			this.question = reader.readLine();
			System.out.print("Please input the expected output: ");
			this.output = reader.readLine();
			System.out.print("Please input the time limit: ");
			this.time = Integer.parseInt(reader.readLine().trim());
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void userConnect(UUID id) {
		User user = userProvider.get();
		users.add(user);
		broadcastPhase();
	}

	public void userDisconnect(UUID id) {
		for (Iterator<User> itr = users.iterator(); itr.hasNext();) {
			if (itr.next().getId().equals(id)) {
				itr.remove();
			}
		}
	}

	public Optional<User> getUser(UUID id) {
		for (User user : users) {
			if (user.getId().equals(id)) {
				return Optional.of(user);
			}
		}
		return Optional.empty();
	}
}
