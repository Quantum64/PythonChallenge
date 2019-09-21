package co.q64.pychallenge.server.question.questions;

import javax.inject.Singleton;

import co.q64.pychallenge.server.question.Question;
import lombok.Getter;

@Singleton
public class FibonacciQuestion implements Question {
	private final @Getter String description = "Write a single function named 'fib' taking one argument. Return the nth iteration of the Fibonacci Sequence.";
	private final @Getter int time = 300;
	private final @Getter String methodName = "fib";
	private final @Getter int arguments = 1;

	@Override
	public boolean test(int[] inputs, int output) {
		int a = 0, b = 1;
		for (int i = 0; i < inputs[0]; i++) {
			int temp = a;
			a = b;
			b = temp + b;
		}
		return b == output;
	}

	@Override
	public int[] generateTestValues() {
		return randomArray(1, 5, 30);
	}
}
