/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestCallMultipleAnswersWithTryCatchSource.java 3941 2008-04-26 21:28:32Z gbevin $
 */
package com.uwyn.rife.continuations;

public class TestCallMultipleAnswersWithTryCatchSource extends AbstractContinuableObject
{
	private StringBuffer mResult;
	
	public void execute()
	{
		mResult = new StringBuffer("before call\n");
		String answer = (String)call(TestCallMultipleAnswersWithTryCatchTarget.class);
		mResult.append(answer);
		answer = (String)call(TestCallMultipleAnswersWithTryCatchTarget.class);
		mResult.append(answer);
		mResult.append("after call");
	}
	
	public String getResult()
	{
		return mResult.toString();
	}
}