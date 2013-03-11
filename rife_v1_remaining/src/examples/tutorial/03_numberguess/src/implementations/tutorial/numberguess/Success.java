/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Success.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package tutorial.numberguess;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.Template;
import tutorial.numberguess.backend.Contest;
import tutorial.numberguess.backend.Game;

/**
 * This element outputs the results of a played game when the number was
 * successfully guessed. The active game is stopped.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 */
public class Success extends Element {
	/**
	 * The element's entry point.
	 */
    public void processElement() {
		// stop the active game and retrieve its details
        Game game = Contest.stopGame(getInput("gameid"));
		// if no game was active, start a new one
        if (null == game) {
            exit("start");
        }
        
		// output the details of the played game
        Template template = getHtmlTemplate("success");
        
        template.setValue("answer", game.getAnswer());
        template.setValue("guesses", game.getGuesses());
        template.setValue("duration", game.getDuration());
        
        print(template);
    }
}
