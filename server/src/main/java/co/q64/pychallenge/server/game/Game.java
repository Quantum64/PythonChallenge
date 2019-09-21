package co.q64.pychallenge.server.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.json.JSONArray;
import org.json.JSONObject;
import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import co.q64.pychallenge.server.net.Socket;
import co.q64.pychallenge.server.question.Question;
import co.q64.pychallenge.server.question.Questions;
import co.q64.pychallenge.server.type.GamePhase;
import co.q64.pychallenge.server.user.User;

@Singleton
public class Game {
	private @Inject Provider<User> userProvider;
	private @Inject Socket socket;
	private @Inject Questions questions;
	private @Inject Logger logger;

	private ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
	//private Scanner scanner = new Scanner(System.in);

	private long cutoffTime = 0;
	private GamePhase phase = GamePhase.WAITING;
	private List<User> users = new ArrayList<>();
	private Question question;

	public void start() {
		question = questions.getQuestions().get(0);
		nextPhase();
	}

	public void nextPhase() {
		if (phase == GamePhase.WAITING) {
			awaitInput();
			/*try {
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			*/
			phase = GamePhase.QUESTION;
			broadcastPhase();
			printPhase();
			JSONObject payload = new JSONObject();
			payload.put("type", "question");
			payload.put("question", question.getDescription());
			payload.put("time", question.getTime());
			List<String> arguments = IntStream.rangeClosed(1, question.getArguments()).mapToObj(Game::charFromNum).map(String::toLowerCase).collect(Collectors.toList());
			StringBuilder sb = new StringBuilder("def " + question.getMethodName() + "(" + arguments.stream().collect(Collectors.joining(", ")) + "):\n\n");
			sb.append("# Test with print\n");
			int[] values = question.generateTestValues();
			sb.append("print(" + question.getMethodName() + "(" + Arrays.stream(values).mapToObj(String::valueOf).collect(Collectors.joining(", ")) + "))");
			payload.put("starter", sb.toString());
			socket.broadcast(payload);
			//awaitInput();
			try {
				Thread.sleep(question.getTime() * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			nextPhase();
		} else if (phase == GamePhase.QUESTION) {
			cutoffTime = System.currentTimeMillis();
			Queue<User> queue = new ConcurrentLinkedDeque<>(users);
			ExecutorService service = Executors.newFixedThreadPool(6);
			List<Future<?>> tasks = new ArrayList<>();
			while (!queue.isEmpty()) {
				User user = queue.poll();
				Future<?> task = service.submit(() -> {
					logger.info("Running submission for " + user.getId());
					user.setTest("No test case generated.");
					user.setResut("No output.");
					user.setLastScore(0);
					user.setFeedback("No submission.");
					JSONObject payload = new JSONObject();
					payload.put("score", 0);
					if (user.getSubmission().trim().isEmpty()) {
						logger.info("No submission for " + user.getId());
					} else {
						try {
							user.setFeedback("The program encountered an error.");
							PythonInterpreter interp = new PythonInterpreter();
							int[] arguments = question.generateTestValues();
							String call = question.getMethodName() + "(" + Arrays.stream(arguments).mapToObj(String::valueOf).collect(Collectors.joining(", ")) + ")";
							user.setTest(call);
							//PyCode code = interp.compile(user.getSubmission());
							interp.exec(user.getSubmission());
							PyObject[] args = new PyObject[arguments.length];
							for (int i = 0; i < arguments.length; i++) {
								args[i] = new PyInteger(arguments[i]);
							}
							//PyObject result = code.invoke(question.getMethodName(), args);
							PyObject result = interp.eval(call);
							interp.cleanup();
							interp.close();
							user.setResut("Non-integer output.");
							if (result instanceof PyInteger) {
								int raw = ((PyInteger) result).getValue();
								user.setResut(String.valueOf(raw));
								if (question.test(arguments, raw)) {
									int score = (int) (cutoffTime - user.getSubmissionTime());
									user.setFeedback("The test case was passed!");
									user.setScore(user.getScore() + score);
									payload.put("pass", true);
									payload.put("score", score);
									user.setLastScore(score);
								} else {
									user.setFeedback("The test case failed.");
									payload.put("pass", false);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						logger.info("Submission complete for " + user.getId());
					}
					payload.put("totalScore", user.getScore());
					payload.put("type", "score");
					payload.put("test", user.getTest());
					payload.put("result", user.getResut());
					payload.put("feedback", user.getFeedback());
					socket.send(user.getId(), payload);
				});
				tasks.add(task);
			}
			try {
				service.awaitTermination(5000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for (Future<?> task : tasks) {
				try {
					task.cancel(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			phase = GamePhase.SCORE;
			broadcastPhase();
			printPhase();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			logger.info("Waiting 10 seconds for score to be viewed.");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			nextPhase();

		} else if (phase == GamePhase.SCORE) {
			this.phase = GamePhase.RESULTS;

			JSONObject payload = new JSONObject();
			payload.put("type", "results");
			JSONArray scores = new JSONArray();
			Collections.sort(users, (o1, o2) -> Integer.compare(o2.getLastScore(), o1.getLastScore()));
			for (User user : users) {
				JSONObject obj = new JSONObject();
				obj.put("name", user.getUsername().isEmpty() ? Long.toString(user.getId().getLeastSignificantBits(), 1) : user.getUsername());
				obj.put("last", user.getLastScore());
				obj.put("total", user.getScore());
				scores.put(obj);
			}
			payload.put("results", scores);
			socket.broadcast(payload);

			broadcastPhase();
			printPhase();
			logger.info("Waiting 10 seconds to move on to the next question (results).");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			nextPhase();
		} else if (phase == GamePhase.RESULTS) {
			question = questions.getQuestions().get(ThreadLocalRandom.current().nextInt(questions.getQuestions().size()));
			this.phase = GamePhase.WAITING;
			broadcastPhase();
			printPhase();
			logger.info("Waiting 10 seconds to move on to the next question (results).");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			nextPhase();
		}
	}

	private void printPhase() {
		logger.info("GAME PHASE NOW AT " + phase.name());
	}

	private void broadcastPhase() {
		JSONObject payload = new JSONObject();
		payload.put("type", "phase");
		payload.put("phase", phase.getName());
		socket.broadcast(payload);
	}

	public void awaitInput() {
		System.out.print("Awaiting input: ");
		//scanner.nextLine();
	}

	/*
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
	*/

	public void userConnect(UUID id) {
		User user = userProvider.get();
		user.setId(id);
		users.add(user);
		timer.schedule(() -> {
			broadcastPhase();
		}, 1, TimeUnit.SECONDS);
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

	private static String charFromNum(int i) {
		return i > 0 && i < 27 ? String.valueOf((char) (i + 64)) : null;
	}
}
