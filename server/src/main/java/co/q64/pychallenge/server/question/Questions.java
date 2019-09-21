package co.q64.pychallenge.server.question;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.q64.pychallenge.server.question.questions.AddQuestion;
import lombok.Getter;

@Singleton
public class Questions {
	private @Inject AddQuestion addQuestion;
	
	private @Getter List<Question> questions = new ArrayList<>();
	
	@Inject
	private void init() {
		questions.add(addQuestion);
	}
}
