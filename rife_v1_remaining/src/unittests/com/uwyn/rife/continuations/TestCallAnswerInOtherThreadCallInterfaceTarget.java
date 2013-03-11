/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestCallAnswerInOtherThreadCallInterfaceTarget.java 3941 2008-04-26 21:28:32Z gbevin $
 */
package com.uwyn.rife.continuations;

public class TestCallAnswerInOtherThreadCallInterfaceTarget implements ContinuableObject, ContinuableSupportAware
{
	private ContinuableSupport mSupport;
	public void setContinuableSupport(ContinuableSupport support) { mSupport = support; }
	
	public Object clone()
	throws CloneNotSupportedException
	{
		return super.clone();
	}
	
	private boolean mDoAnswer = false;
	
	public void setDoAnswer(boolean doAnswer)
	{
		mDoAnswer = doAnswer;
	}
	
	public void execute()
	{
		if (mDoAnswer)
		{
			mSupport.answer(Boolean.TRUE);
		}
	}
}