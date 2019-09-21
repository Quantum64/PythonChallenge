package co.q64.pychallenge.server;

import com.google.inject.Guice;
import com.google.inject.Injector;

import co.q64.pychallenge.server.net.Server;

public class Main {
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new Module());
		Server server = injector.getInstance(Server.class);
		server.start();
	}
}
