/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: org_hsqldb_jdbcDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.databasedrivers;

import com.uwyn.rife.authentication.credentialsmanagers.DatabaseUsers;
import com.uwyn.rife.authentication.credentialsmanagers.RoleUserAttributes;
import com.uwyn.rife.authentication.credentialsmanagers.exceptions.DuplicateLoginException;
import com.uwyn.rife.authentication.credentialsmanagers.exceptions.DuplicateUserIdException;
import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.database.Datasource;

public class org_hsqldb_jdbcDriver extends generic
{
	public org_hsqldb_jdbcDriver(Datasource datasource)
	{
		super(datasource);
	}

	public DatabaseUsers addUser(String login, RoleUserAttributes attributes)
	throws CredentialsManagerException
	{
		try
		{
			_addUser(mAddUserWithId, mGetFreeUserId, mGetRoleId, mAddRoleLink, login, attributes);
		}
		catch (CredentialsManagerException e)
		{
			if (null != e.getCause() &&
				null != e.getCause().getCause())
			{
				String message = e.getCause().getCause().getMessage().toUpperCase();
				if (-1 != message.indexOf(mCreateTableUser.getUniqueConstraints().get(0).getName()))
				{
					throw new DuplicateLoginException(login);
				}
				if (-1 != message.indexOf("UNIQUE"))
				{
					throw new DuplicateUserIdException(attributes.getUserId());
				}
			}
		
			throw e;
		}
		
		return this;
	}
}
