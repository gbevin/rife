/*
 * Copyright 2001-2008 Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id$
 */
package com.uwyn.rife.authentication.credentialsmanagers;

import com.uwyn.rife.authentication.CredentialsManager;
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.ioc.exceptions.PropertyValueException;

/**
 * Factory for {@link MemoryUsers} manager instances that basically retrieves
 * the current memory credential from the default repository participant.
 * 
 * @author Steven Grimm (koreth[remove] at midwinter dot com)
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: $
 * @since 1.6
 */
public class MemoryUsersFactory implements CredentialsManagerFactory
{
	public CredentialsManager getCredentialsManager(HierarchicalProperties properties)
	throws PropertyValueException
	{
		return MemoryUsers.getRepInstance();
	}
}
