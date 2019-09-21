package co.q64.pychallenge.server;

import javax.inject.Singleton;

import spark.Spark;

@Singleton
public class Server {
	public void start() {
		
		
		Spark.port(12345);
	}
}
