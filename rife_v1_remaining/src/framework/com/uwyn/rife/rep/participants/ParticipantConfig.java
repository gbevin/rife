/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParticipantConfig.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.rep.participants;

import com.uwyn.rife.config.Config;
import com.uwyn.rife.config.exceptions.ConfigErrorException;
import com.uwyn.rife.rep.BlockingParticipant;

public class ParticipantConfig extends BlockingParticipant
{
	private Config	mConfig = null;

	public ParticipantConfig()
	{
		setInitializationMessage("Creating the config object ...");
		setCleanupMessage("Cleaning up the config object ...");
	}
	
	protected void initialize()
	{
		try
		{
			mConfig = new Config(this.getParameter(), getResourceFinder());
		}
		catch (ConfigErrorException e)
		{
			throw new RuntimeException("Fatal error during the initialization while creating the config object.", e);
		}
	}

	protected Object _getObject()
	{
		return mConfig;
	}

	protected Object _getObject(Object key)
	{
		String key_string = String.valueOf(key);
		Object result = mConfig.getString(key_string);
		if (result != null)
		{
			return result;
		}
		
		return mConfig.getStringItems(key_string);
	}
}

