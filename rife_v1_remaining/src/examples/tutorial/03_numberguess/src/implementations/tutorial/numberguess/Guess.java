/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Guess.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package tutorial.numberguess;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.Template;
import tutorial.numberguess.backend.Contest;
import tutorial.numberguess.backend.Game;

/**
 * This element handles guesses that are being made by participants in a game.
 * <p>
 * If an active game is detected, it is resumed. Otherwise, a new game is
 * started.
 * <p>
 * The visitor is able to submit a guess through a form. The element validates
 * the answer and keeps track of the number of guesses. The user receives an
 * indication about the relation of the correct answer with the last submitted
 * guess. If the guess was correct, the <code>success</code> exit is activated.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 */
public class Guess extends Element {
	private Game	game;
	
	private String	gameid;
	private int		guess = -1;
	
	public void setGameid(String gameid)	{ this.gameid = gameid; }
	public void setGuess(int guess)			{ this.guess = guess; }
	
	/**
	 * An instance of the template that's being used by the element instance.
	 */
    private Template template;
    
	/**
	 * The element's initialization.
	 */
    public void initialize() {
        // obtain the active game
        game = Contest.getGame(gameid);
        // if no game could be found, start a new one
        if (null == game) {
            exit("start");
        }
        
        // retrieve the html template
        template = getHtmlTemplate("guess");
    }
    
	/**
	 * The element's entry point.
	 */
    public void processElement() {
        // output the template
        print(template);
    }
    
	/**
	 * Processes a guess when the form has been submitted.
	 */
    public void doPerformGuess() {
        // validate the guess
        if (guess < 0 || guess > 100) {
            // if the guess was invalid, a warning is shown and the logic is
            // interrupted
            template.setBlock("warning", "invalid");
        } else {
			// increase the number of guess attempts
			game.increaseGuesses();
			
			// check the correctness of the guess in case of a successful match, the
			// success exit is triggered, otherwise an indication message is shown
			if (game.getAnswer() < guess) {
				template.setBlock("indication", "lower");
			} else if (game.getAnswer() > guess) {
				template.setBlock("indication", "higher");
			} else {
				setOutput("gameid", gameid);
				exit("success");
			}
		}
        
        // output the template
        print(template);
    }
}
