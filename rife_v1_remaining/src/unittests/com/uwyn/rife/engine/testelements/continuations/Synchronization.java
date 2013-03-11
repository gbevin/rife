/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Synchronization.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.continuations;

import com.uwyn.rife.engine.Element;

public class Synchronization extends Element
{
	private Object			mMonitorMember = new Object();
	private static Object	sMonitorStatic = new Object();

	public void processElement()
	{
		synchronized (this)
		{
		}
		
		synchronized (this)
		{
			print("monitor this");
		}
		print("\n"+getContinuationId());
		
		pause();
		
		synchronized (mMonitorMember)
		{
			print("monitor member");
		}
		print("\n"+getContinuationId());
		
		pause();
		
		synchronized (sMonitorStatic)
		{
			print("monitor static");
		}
		print("\n"+getContinuationId());
		
		pause();
		
		print("done");
	}
}

