package co.q64.pychallenge.server.user;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
	private String username;
	private String feedback = "None";
	private int score;
	private UUID id;
	private String submission = "", test = "", resut = "";
	private boolean active;
}
