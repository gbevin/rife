/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestCallMultipleAnswersWithTryCatchTarget.java 3941 2008-04-26 21:28:32Z gbevin $
 */
package com.uwyn.rife.continuations;

public class TestCallMultipleAnswersWithTryCatchTarget extends AbstractContinuableObject
{
	private static int mWhichAnswer = 1;
	
	public void execute()
	{
		if (1 == mWhichAnswer)
		{
			mWhichAnswer = 2;
			
			try
			{
				String answer = "during call target 1\n";
				answer(answer);				
			}
			catch (Error e)
			{
				throw new RuntimeException(e);
			}
		}
		else if (2 == mWhichAnswer)
		{
			try
			{
				String answer = "during call target 2\n";
				answer(answer);
			}
			catch (Error e)
			{
				throw new RuntimeException(e);
			}
		}
	}
}