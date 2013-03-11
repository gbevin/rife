/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestStepBackCounterInterface.java 3941 2008-04-26 21:28:32Z gbevin $
 */
package com.uwyn.rife.continuations;

public class TestStepBackCounterInterface implements ContinuableObject, ContinuableSupportAware
{
	private ContinuableSupport mSupport;
	public void setContinuableSupport(ContinuableSupport support) { mSupport = support; }

	public Object clone()
	throws CloneNotSupportedException
	{
		return super.clone();
	}

	private int mTotal = -5;
	public int getTotal() { return mTotal; }

	private boolean mStart = false;
	public void setStart(boolean start) { mStart = start; }

	private int mAnswer;
	public void setAnswer(int answer) { mAnswer = answer; }

	public void execute()
	{
		int number_of_resumes = 0;

		if (mTotal < 0)
		{
			mTotal++;
			mSupport.stepback();
		}

		mSupport.pause();
		number_of_resumes++;

		if (mStart)
		{
			mSupport.pause();
			number_of_resumes++;

			mTotal += mAnswer;

			if (mTotal < 50)
			{
				mSupport.stepback();
			}
		}
	}
}