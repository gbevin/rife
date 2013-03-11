/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Start.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package tutorial.numberguess;

import com.uwyn.rife.engine.Element;
import tutorial.numberguess.backend.Contest;

/**
 * This element starts a new game and activates the <code>started</code> exit
 * when that's done. Nothing is displayed.
 * <p>
 * If a game is already active for the participant that uses this element,
 * the game is replaced by the name one.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 */
public class Start extends Element {
	/**
	 * The element's entry point.
	 */
    public void processElement() {
		// if a game is already active, stop it
        Contest.stopGame(getInput("gameid"));
		
		// start the new game and remember it's unique id
        setOutput("gameid", Contest.startGame());
        
		// activate the started exit
        exit("started");
    }
}
