/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParticipantDatasources.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.rep.participants;
import com.uwyn.rife.database.Datasources;
import com.uwyn.rife.database.exceptions.DatasourcesException;
import com.uwyn.rife.rep.BlockingParticipant;

public class ParticipantDatasources extends BlockingParticipant
{
	private Datasources	mDatasources = null;

	public ParticipantDatasources()
	{
		setInitializationMessage("Creating the datasources object ...");
		setCleanupMessage("Cleaning up the datasources object ...");
	}
	
	protected void initialize()
	{
		try
		{
			mDatasources = new Datasources(getParameter(), getResourceFinder());
		}
		catch (DatasourcesException e)
		{
			throw new RuntimeException("Fatal error during the initialization while creating the datasources object.", e);
		}
	}

	protected Object _getObject()
	{
		return mDatasources;
	}

	protected Object _getObject(Object key)
	{
		return mDatasources.getDatasource(String.valueOf(key));
	}

	protected void cleanup()
	{
		if (mDatasources != null)
		{
			mDatasources.cleanup();
		}
	}
}

