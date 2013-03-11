/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParticipantMemoryUsers.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.rep.participants;

import com.uwyn.rife.authentication.credentialsmanagers.MemoryUsers;
import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.rep.BlockingParticipant;

public class ParticipantMemoryUsers extends BlockingParticipant
{
	private MemoryUsers	mUsers = null;

	public ParticipantMemoryUsers()
	{
		setInitializationMessage("Creating in-memory user credentials ...");
		setCleanupMessage("Cleaning up in-memory user credentials ...");
	}
	
	protected void initialize()
	{
		try
		{
			mUsers = new MemoryUsers(this.getParameter(), getResourceFinder());
		}
		catch (CredentialsManagerException e)
		{
			throw new RuntimeException("Fatal error during the initialization while creating the memory users object.", e);
		}
	}

	protected Object _getObject(Object key)
	{
		return mUsers;
	}
}

