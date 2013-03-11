/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AbstractHangmanElement.java 3918 2008-04-14 17:35:35Z gbevin $
 */
import com.uwyn.rife.engine.Element;
import javax.servlet.http.HttpSession;
import model.Game;
import model.WordGenerator;

public abstract class AbstractHangmanElement extends Element {
	public Game getGame() {
		HttpSession session = getHttpServletRequest().getSession();
		Game game = (Game)session.getAttribute("game");
		if (null == game) {
			game = new Game();
			game.newGame(5, new WordGenerator());
			session.setAttribute("game", game);
		}
		return game;
	}
	
	public boolean prohibitRawAccess() {
		return false;
	}
}
