package co.q64.pychallenge.server.util;

import javax.inject.Singleton;

@Singleton
public class Logger {
	public void info(String message) {
		System.out.println(message);
	}
}
