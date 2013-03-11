/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: org_h2_Driver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.databasedrivers;

import com.uwyn.rife.authentication.credentialsmanagers.DatabaseUsers;
import com.uwyn.rife.authentication.credentialsmanagers.RoleUserAttributes;
import com.uwyn.rife.authentication.credentialsmanagers.exceptions.DuplicateLoginException;
import com.uwyn.rife.authentication.credentialsmanagers.exceptions.DuplicateRoleException;
import com.uwyn.rife.authentication.credentialsmanagers.exceptions.DuplicateUserIdException;
import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.database.Datasource;

public class org_h2_Driver extends generic
{
	public org_h2_Driver(Datasource datasource)
	{
		super(datasource);
	}

	public DatabaseUsers addRole(String role)
	throws CredentialsManagerException
	{
		try
		{
			_addRole(mGetNewRoleId, mAddRole, role);
		}
		catch (CredentialsManagerException e)
		{
			if (null != e.getCause() &&
				null != e.getCause().getCause())
			{
				String message = e.getCause().getCause().getMessage().toUpperCase();
				if (-1 != message.indexOf("AUTHROLE_NAME_UQ_INDEX"))
				{
					throw new DuplicateRoleException(role);
				}
			}
			
			throw e;
		}
		
		return this;
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
				if (-1 != message.indexOf("PRIMARY_KEY"))
				{
					throw new DuplicateUserIdException(attributes.getUserId());
				}
				if (-1 != message.indexOf("AUTHUSER_LOGIN_UQ_INDEX"))
				{
					throw new DuplicateLoginException(login);
				}
			}
		
			throw e;
		}
		
		return this;
	}
}
