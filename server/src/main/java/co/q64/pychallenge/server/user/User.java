package co.q64.pychallenge.server.user;

import java.util.UUID;

import lombok.experimental.Accessors;

@Accessors
public class User {
	private String username;
	private int score;
	private UUID id;
	private String submission;
	private boolean active;
}
