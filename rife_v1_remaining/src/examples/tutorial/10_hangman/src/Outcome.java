/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Outcome.java 3918 2008-04-14 17:35:35Z gbevin $
 */
import com.uwyn.rife.engine.annotations.Elem;
import com.uwyn.rife.engine.annotations.Flowlink;
import com.uwyn.rife.engine.annotations.SubmissionHandler;
import com.uwyn.rife.template.Template;
import model.Game;

@Elem(
	flowlinks = {@Flowlink(srcExit = "guess", destClass = Guess.class, redirect=true)}
)
public class Outcome extends AbstractHangmanElement {
	@SubmissionHandler
	public void doPlayAgain() {
		getGame().newGame();
		exit("guess");
	}
	
	public void processElement() {
		Template t = getPropertyTyped("template", Template.class);
		
		Game game = getGame();
		if (t.hasValueId("guesses")) {
			t.setValue("guesses", game.getGuessesRemaining());
		}
		t.setValue("word", encodeHtml(game.getWord().asString()));
		
		print(t);
	}
}
