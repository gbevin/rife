/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Guess.java 3918 2008-04-14 17:35:35Z gbevin $
 */
import com.uwyn.rife.engine.annotations.Elem;
import com.uwyn.rife.template.Template;
import model.Game;
import model.Letter;

@Elem
public class Guess extends AbstractHangmanElement {
	public void processElement() {
		Template t = getHtmlTemplate("guess");
		
		Game game = getGame();
		for (Letter letter : game.getLetters()) {
			processEmbeddedElement(t, "LetterImage", letter.asString(), letter);
			t.appendBlock("letters", "letter");
		}
		
		t.setValue("guesses", game.getGuessesRemaining());
		t.setValue("word", encodeHtml(game.getWord().asString(true)));
		
		print(t);
	}
}
