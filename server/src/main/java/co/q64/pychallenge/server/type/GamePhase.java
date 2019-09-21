package co.q64.pychallenge.server.type;

public enum GamePhase {
	WAITING, QUESTION, SCORE;

	public String getName() {
		return name().toLowerCase();
	}
}
