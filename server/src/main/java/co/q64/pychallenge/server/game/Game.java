package co.q64.pychallenge.server.game;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import co.q64.pychallenge.server.user.User;

@Singleton
public class Game {
	private @Inject Provider<User> userProvider;
	
	private List<User> users = new ArrayList<>();
	
	public void userConnect(UUID id) {
		
	}

	public void userDisconnect(UUID id) {

	}
}
