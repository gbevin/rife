/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParticipantSite.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.rep.participants;

import com.uwyn.rife.engine.SiteBuilder;
import com.uwyn.rife.rep.BlockingParticipant;
import com.uwyn.rife.tools.ExceptionUtils;
import java.util.logging.Logger;

public class ParticipantSite extends BlockingParticipant
{
	private Object		mSite = null;
	private Throwable	mException = null;

	public ParticipantSite()
	{
		setInitializationMessage("Creating web application's site structure ...");
		setCleanupMessage("Cleaning up web application's site structure ...");
	}
	
	public Throwable getException()
	{
		return mException;
	}
	
	protected void initialize()
	{
		try
		{
			SiteBuilder	builder = new SiteBuilder(getParameter(), getResourceFinder());
			mSite = builder.getSite();
		}
		catch (Throwable e)
		{
			mException = e;
			Logger.getLogger("com.uwyn.rife.rep").severe(ExceptionUtils.getExceptionStackTrace(e));
		}
	}
	
	protected Object _getObject(Object key)
	{
		if (null == mSite)
		{
			initialize();
		}
		
		return mSite;
	}
}

