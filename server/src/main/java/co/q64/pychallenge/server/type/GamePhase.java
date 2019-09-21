package co.q64.pychallenge.server.type;

public enum GamePhase {
	WAITING, QUESTION, SCORE, RESULTS;

	public String getName() {
		return name().toLowerCase();
	}
}
