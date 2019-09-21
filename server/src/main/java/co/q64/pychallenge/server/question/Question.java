package co.q64.pychallenge.server.question;

import java.util.concurrent.ThreadLocalRandom;

public interface Question {
	public String getDescription();

	public int getTime();

	public String getMethodName();

	public int getArguments();

	public int[] generateTestValues();

	public boolean test(int[] inputs, int output);

	public default int[] randomArray(int length, int min, int max) {
		int[] result = new int[length];
		for (int i = 0; i < result.length; i++) {
			result[i] = ThreadLocalRandom.current().nextInt(min, max);
		}
		return result;
	}
}
