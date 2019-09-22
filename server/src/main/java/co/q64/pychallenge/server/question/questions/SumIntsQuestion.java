package co.q64.pychallenge.server.question.questions;

import javax.inject.Singleton;

import co.q64.pychallenge.server.question.Question;
import lombok.Getter;

@Singleton
public class SumIntsQuestion implements Question {
	private final @Getter String description = "Write a single function named 'sumInts' taking one argument. Return the sum of all integers from n to 0.";
	private final @Getter int time = 30;
	private final @Getter String methodName = "sumInts";
	private final @Getter int arguments = 1;

	@Override
	public boolean test(int[] inputs, int output) {
		int a = 0;
		for (int i = 0; i <= inputs[0]; i++) {
			a = a + i;
		}
		return a == output;
	}

	@Override
	public int[] generateTestValues() {
		return randomArray(1, 10, 50);
	}
}
