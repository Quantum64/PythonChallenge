package co.q64.pychallenge.server.question.questions;

import javax.inject.Singleton;

import co.q64.pychallenge.server.question.Question;
import lombok.Getter;

@Singleton
public class AddQuestion implements Question {
	private final @Getter String description = "Write a single function named 'add' taking two arguments. Return the sum of the arguments.";
	private final @Getter int time = 30;
	private final @Getter String methodName = "add";
	private final @Getter int arguments = 2;

	@Override
	public boolean test(int[] inputs, int output) {
		return inputs[0] + inputs[1] == output;
	}

	@Override
	public int[] generateTestValues() {
		return randomArray(2, -1000, 1000);
	}
}
