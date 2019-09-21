package co.q64.pychallenge.server.question.questions;

import javax.inject.Singleton;

import co.q64.pychallenge.server.question.Question;
import lombok.Getter;

@Singleton
public class BothNegativeQuestion implements Question {
	private final @Getter String description = "Write a single function named 'bothNegative' taking two arguments. Return 1 if true and 0 if false.";
	private final @Getter int time = 60;
	private final @Getter String methodName = "bothNegative";
	private final @Getter int arguments = 2;

	@Override
	public boolean test(int[] inputs, int output) {
		int answer;
		if ((inputs[0] < 0) && (inputs[1] < 0)) {
			answer = 1;
		} else {
			answer = 0;
		}
		return answer == output;
	}

	@Override
	public int[] generateTestValues() {
		return randomArray(2, -1000, 1000);
	}
}
