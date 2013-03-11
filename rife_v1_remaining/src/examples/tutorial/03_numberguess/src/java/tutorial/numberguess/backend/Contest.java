/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Contest.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package tutorial.numberguess.backend;

import com.uwyn.rife.tools.UniqueID;
import com.uwyn.rife.tools.UniqueIDGenerator;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is meant to never be instantiated. It keeps track of all active
 * games.
 * <p>
 * Ideally this functionality should be implemented through the storage in a
 * database or other persistant data storage medium. For simplicity's sake,
 * a simple in-memory structure is used and accessed in a thread-safe manner.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 */
public abstract class Contest {
    private static final Map    activeGames = new HashMap();
    
    /**
     * Starts a new game and registers it in the collection of active game.
     * A unique identifier is also generated that can be used to retrieve the
     * game afterwards.
     *
     * @return a <code>String</code> containing the unique id that corresponds
     * to the newly started game.
     */
    public static String startGame() {
        UniqueID gameid = UniqueIDGenerator.generate();
        Game game = new Game();
        
        synchronized (Contest.activeGames) {
            Contest.activeGames.put(gameid.toString(), game);
        }
        
        return gameid.toString();
    }
    
    /**
     * Stops an active game.
     *
     * @param gameid The unique identifier that corresponds to the game that
     * has to be stopped.
     *
     * @return the <code>Game</code> instance that has been stopped; or
     * <p>
     *<code>null</code> if no game could be found with the provided id.
     */
    public static Game stopGame(String gameid) {
        synchronized (Contest.activeGames) {
            return (Game)Contest.activeGames.remove(gameid);
        }
    }
    
    /**
     * Retrieves an active game.
     *
     * @param gameid The unique identifier that corresponds to the game that
     * has to be retrieved.
     *
     * @return the <code>Game</code> instance that corresponds to the provided
     * id; or
     * <p>
     * <code>null</code> if no game could be found with the provided id
     *
     */
    public static Game getGame(String gameid) {
        return (Game)Contest.activeGames.get(gameid);
    }
}
