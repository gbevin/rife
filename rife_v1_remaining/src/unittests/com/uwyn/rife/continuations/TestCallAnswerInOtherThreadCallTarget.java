/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestCallAnswerInOtherThreadCallTarget.java 3941 2008-04-26 21:28:32Z gbevin $
 */
package com.uwyn.rife.continuations;

public class TestCallAnswerInOtherThreadCallTarget extends AbstractContinuableObject
{
	private boolean mDoAnswer = false;
	
	public void setDoAnswer(boolean doAnswer)
	{
		mDoAnswer = doAnswer;
	}
	
	public void execute()
	{
		if (mDoAnswer)
		{
			answer(Boolean.TRUE);
		}
	}
}