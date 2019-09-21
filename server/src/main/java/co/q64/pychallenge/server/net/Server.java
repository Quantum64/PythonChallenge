package co.q64.pychallenge.server.net;

import static spark.Spark.port;
import static spark.Spark.webSocket;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.q64.pychallenge.server.game.Game;
import spark.Spark;

@Singleton
public class Server {
	private @Inject Socket socket;
	private @Inject Game game;

	public void start() {

		Spark.staticFileLocation("build");
		webSocket("/socket", socket);
		port(12345);
		Spark.init();
		
		game.start();
	}
}
