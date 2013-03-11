/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Game.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package tutorial.numberguess.backend;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * The lifecycle of a number guess game is represented by this class.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 */
public class Game {
    private final static Random randomNumbers = new Random();
	
    private Date	start;
    private int		answer;
    private int		guesses;
    
    /**
     * When a new instance of the class is created, it's regarded as started
     * a new game. The answer is randomly generated, the start time is
     * registered and the number of guesses is initialized to zero.
     */
    public Game() {
        registerStart();
        generateAnswer();
    }
    
    /**
     * Registers the start of the game
     */
    private synchronized void registerStart() {
        start = Calendar.getInstance().getTime();
    }
    
    /**
     * Generates the number that has to be guessed in this game.
     */
    private void generateAnswer() {
        synchronized (randomNumbers) {
            answer = randomNumbers.nextInt(101);
        }
    }
    
    /**
     * Calculates how much seconds that have elapsed since the start of the
     * game.
     *
     * @return A <code>long</code> with the number of elapsed seconds.
     */
    public long getDuration() {
        Date now = Calendar.getInstance().getTime();
        long duration_milliseconds = now.getTime() - start.getTime();
        long duration_seconds = duration_milliseconds / 1000;
        
        return duration_seconds;
    }
    
    /**
     * Retrieves the correct answer.
     *
     * @return An <code>int</code> with the value of the correct answer.
     */
    public int getAnswer() {
        return answer;
    }
    
    /**
     * Increases the number of guesses that have been made.
     */
    public synchronized void increaseGuesses() {
        guesses++;
    }
    
    /**
     * Retrieves the number of guesses that have already been made.
     *
     * @return An <code>int</code> with the number of guesses.
     */
    public int getGuesses() {
        return guesses;
    }
}
