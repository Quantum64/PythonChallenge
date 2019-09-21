package co.q64.pychallenge.server.net;

import static spark.Spark.port;
import static spark.Spark.webSocket;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Server {
	private @Inject Socket socket;

	public void start() {

		webSocket("/socket", socket);
		port(12345);
	}
}
