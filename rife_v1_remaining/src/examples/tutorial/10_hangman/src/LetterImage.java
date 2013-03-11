/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: LetterImage.java 3918 2008-04-14 17:35:35Z gbevin $
 */
import com.uwyn.rife.engine.annotations.Elem;
import com.uwyn.rife.engine.annotations.Flowlink;
import com.uwyn.rife.engine.annotations.SubmissionHandler;
import com.uwyn.rife.template.Template;
import model.Game;
import model.Letter;

@Elem(
	url="",
	flowlinks = {
		@Flowlink(srcExit="win", destId="Win", embedding=Flowlink.Embedding.CANCEL),
		@Flowlink(srcExit="lose", destId="Lose", embedding=Flowlink.Embedding.CANCEL)
	}
)
public class LetterImage extends AbstractHangmanElement {
	private Letter getLetter() {
		return (Letter)getEmbedData();
	}
	
	@SubmissionHandler
	public void doGuess() {
		Game game = getGame();
		game.guess(getLetter());
		
		// Is the game over?
		if (game.isWon()) {
			exit("win");
		} else if (game.isLost()) {
			exit("lose");
		}
		
		processElement();
	}
	
	public void processElement() {
		Template t = getHtmlTemplate("letter");
		t.setValue("letter", getLetter().asString());
		if (getLetter().isGuessed()) {
			t.setBlock("output", "enabled");
		} else {
			t.setBlock("output", "disabled");
		}
		print(t);
	}
}
