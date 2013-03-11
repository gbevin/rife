/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestCallAnswerInOtherThreadCallSource.java 3941 2008-04-26 21:28:32Z gbevin $
 */
package com.uwyn.rife.continuations;

public class TestCallAnswerInOtherThreadCallSource extends AbstractContinuableObject
{
	private StringBuffer mResult;
	
	public void execute()
	{
		mResult = new StringBuffer("before call\n");
		Boolean answer = (Boolean)call(TestCallAnswerInOtherThreadCallTarget.class);
		mResult.append(answer);
		mResult.append("\nafter call");
	}
	
	public String getResult()
	{
		return mResult.toString();
	}
}