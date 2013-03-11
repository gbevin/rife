/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: org_apache_derby_jdbc_EmbeddedDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.databasedrivers;

import com.uwyn.rife.authentication.credentialsmanagers.exceptions.*;

import com.uwyn.rife.authentication.credentialsmanagers.DatabaseUsers;
import com.uwyn.rife.authentication.credentialsmanagers.RoleUserAttributes;
import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.DbPreparedStatementHandler;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.CreateTable;
import com.uwyn.rife.database.queries.Insert;
import com.uwyn.rife.database.queries.Select;

public class org_apache_derby_jdbc_EmbeddedDriver extends generic
{
	public org_apache_derby_jdbc_EmbeddedDriver(Datasource datasource)
	{
		super(datasource);

		mCreateTableRole
			.customAttribute("roleId", "GENERATED ALWAYS AS IDENTITY");

		mCreateTableRoleLink = new CreateTable(getDatasource())
			.table(RifeConfig.Authentication.getTableRoleLink())
			.column("userId", long.class, CreateTable.NOTNULL)
			.column("roleId", int.class, CreateTable.NOTNULL)
			.primaryKey(RifeConfig.Authentication.getTableRoleLink().toUpperCase()+"_PK", new String[] {"userId", "roleId"})
			.foreignKey(RifeConfig.Authentication.getTableRoleLink().toUpperCase()+"_UI_FK", mCreateTableUser.getTable(), "userId", "userId", null, CreateTable.CASCADE)
			.foreignKey(RifeConfig.Authentication.getTableRoleLink().toUpperCase()+"_RI_FK", mCreateTableRole.getTable(), "roleId", "roleId", null, CreateTable.CASCADE);
		
		mAddRole = new Insert(getDatasource())
			.into(mCreateTableRole.getTable())
			.fieldParameter("name");

		mGetFreeUserId = new Select(getDatasource())
			.field("CASE WHEN MAX(userId) IS NULL THEN 0 ELSE MAX(userId)+1 END AS freeUserId")
			.from(mCreateTableUser.getTable());
	}
	
	public boolean install()
	throws CredentialsManagerException
	{
		try
		{
			executeUpdate(mCreateTableRole);
			executeUpdate(mCreateTableUser);
			executeUpdate(mCreateTableRoleLink);
		}
		catch (DatabaseException e)
		{
			throw new InstallCredentialsErrorException(e);
		}
	
		return true;
	}
	
	public boolean remove()
	throws CredentialsManagerException
	{
		try
		{
			executeUpdate(mDropTableRoleLink);
			executeUpdate(mDropTableUser);
			executeUpdate(mDropTableRole);
		}
		catch (DatabaseException e)
		{
			throw new RemoveCredentialsErrorException(e);
		}
		
		return true;
	}

	public DatabaseUsers addRole(final String role)
	throws CredentialsManagerException
	{
		if (null == role ||
			0 == role.length())
		{
			throw new AddRoleErrorException(role);
		}
		
		try
		{
			if (0 == executeUpdate(mAddRole, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setString("name", role);
					}
				}))
			{
				throw new AddRoleErrorException(role);
			}
		}
		catch (DatabaseException e)
		{
			if (null != e.getCause())
			{
				String message = e.getCause().getMessage().toUpperCase();
				if (-1 != message.indexOf("AUTHROLE_NAME_UQ"))
				{
					throw new DuplicateRoleException(role);
				}
			}
		
			throw new AddRoleErrorException(role, e);
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
				if (-1 != message.indexOf("AUTHUSER_LOGIN_UQ"))
				{
					throw new DuplicateLoginException(login);
				}
				if (-1 != message.indexOf("AUTHUSER_PK"))
				{
					throw new DuplicateUserIdException(attributes.getUserId());
				}
			}
		
			throw e;
		}
		
		return this;
	}
}
