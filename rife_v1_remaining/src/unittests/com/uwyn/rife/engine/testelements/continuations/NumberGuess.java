/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: NumberGuess.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.continuations;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.Template;
import java.util.Calendar;
import java.util.Random;

public class NumberGuess extends Element
{
    private static Random sRandomNumbers = new Random();

	public void processElement()
	{
		Template template = getHtmlTemplate("engine_continuation_numberguess_guess");
		
		Calendar    start = Calendar.getInstance();
		int 		answer = 0;
		int 		guesses = 0;
		
        synchronized (this)
        {
            answer = sRandomNumbers.nextInt(101);
        }
		
		int guess = -1;
		while (guess != answer)
		{
			print(template);
			
			pause();
			
			guess = getParameterInt("guess", -1);
			
        	if (guess < 0 || guess > 100)
        	{
	            template.setBlock("warning", "invalid");
	            continue;
    	    }

	        guesses++;

	        if (answer < guess)
	        {
    	        template.setBlock("indication", "lower");
        	}
	        else if (answer > guess)
    	    {
        	    template.setBlock("indication", "higher");
	        }
		}
        
		Calendar now = Calendar.getInstance();
		long duration_milliseconds = now.getTime().getTime()-start.getTime().getTime();
		long duration_seconds = duration_milliseconds / 1000;
		
		// output the details of the played game
		template = getHtmlTemplate("engine_continuation_numberguess_success");
		
		template.setValue("answer", answer);
		template.setValue("guesses", guesses);
		template.setValue("duration", duration_seconds);
		
		print(template);
	}
}

