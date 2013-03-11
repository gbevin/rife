/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestPauseInWhileInterface.java 3941 2008-04-26 21:28:32Z gbevin $
 */
package com.uwyn.rife.continuations;

public class TestPauseInWhileInterface implements ContinuableObject, ContinuableSupportAware
{
	private ContinuableSupport mSupport;
	public void setContinuableSupport(ContinuableSupport support) { mSupport = support; }

	public Object clone()
	throws CloneNotSupportedException
	{
		return super.clone();
	}

	public void execute()
	{
		int count = 5;

		while (count > 0)
		{
			mSupport.pause();
			count--;
		}
	}
}