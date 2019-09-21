package co.q64.pychallenge.server.question;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.q64.pychallenge.server.question.questions.AddQuestion;
import co.q64.pychallenge.server.question.questions.BothNegativeQuestion;
import co.q64.pychallenge.server.question.questions.FibonacciQuestion;
import co.q64.pychallenge.server.question.questions.SquareQuestion;
import co.q64.pychallenge.server.question.questions.SumIntsQuestion;
import lombok.Getter;

@Singleton
public class Questions {
	private @Inject AddQuestion addQuestion;
	private @Inject BothNegativeQuestion bothNegativeQuestion;
	private @Inject FibonacciQuestion fibonacciQuestion;
	private @Inject SquareQuestion squareQuestion;
	private @Inject SumIntsQuestion sumIntsQuestion;
	
	private @Getter List<Question> questions = new ArrayList<>();
	
	@Inject
	private void init() {
		questions.add(addQuestion);
		questions.add(bothNegativeQuestion);
		questions.add(fibonacciQuestion);
		questions.add(squareQuestion);
		questions.add(sumIntsQuestion);
	}
}
