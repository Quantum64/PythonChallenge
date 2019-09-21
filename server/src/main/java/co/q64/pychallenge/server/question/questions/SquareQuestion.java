package co.q64.pychallenge.server.question.questions;

import javax.inject.Singleton;

import co.q64.pychallenge.server.question.Question;
import lombok.Getter;

@Singleton
public class SquareQuestion implements Question {
	private final @Getter String description = "Write a single function named 'sqr' taking one argument. Return the squared value of a given integer.";
	private final @Getter int time = 20;
	private final @Getter String methodName = "sqr";
	private final @Getter int arguments = 1;

	@Override
	public boolean test(int[] inputs, int output) {
		int a = 0;
		a = inputs[0] * inputs[0];
		return a == output;
	}

	@Override
	public int[] generateTestValues() {
		return randomArray(1, 10, 100);
	}
}
