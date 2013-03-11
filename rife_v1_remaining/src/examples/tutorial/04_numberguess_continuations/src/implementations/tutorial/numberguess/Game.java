/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Game.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package tutorial.numberguess;

import com.uwyn.rife.continuations.ContinuationContext;
import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.Template;

import java.util.Random;

/**
 * This element handles guesses that are being made by participants in a game.
 * <p>
 * If a continuation is found, it is resumed and all local variables that
 * define an active game are restored, otherwise a new game is started.
 * <p>
 * The visitor is able to submit a guess through a form. The element validates
 * the answer and keeps track of the number of guesses. The user receives an
 * indication about the relation of the correct answer with the last submitted
 * guess. If the guess was correct, the <code>success</code> exit is activated.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 */
public class Game extends Element {
	private static Random randomNumbers = new Random();
	public void processElement() {
		
		Template template = getHtmlTemplate("game");
		int answer = 0, guesses = 0, guess = -1;
		
		answer = randomNumbers.nextInt(101);
		while (guess != answer) {
			print(template);
			
			pause();
			
			template.clear();
			
			guess = getParameterInt("guess", -1);
			if (guess < 0 || guess > 100) {
				template.setBlock("warning", "invalid");
				continue;
			}
			guesses++;
			
			if (answer < guess)      template.setBlock("msg", "lower");
			else if (answer > guess) template.setBlock("msg", "higher");
		}
		
		ContinuationContext.getActiveContext().removeContextTree();
		
		template = getHtmlTemplate("success");
		template.setValue("answer", answer);
		template.setValue("guesses", guesses);
		print(template);
	}
}
