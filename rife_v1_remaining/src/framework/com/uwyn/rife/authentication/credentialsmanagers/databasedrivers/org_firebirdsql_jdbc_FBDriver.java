/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: org_firebirdsql_jdbc_FBDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.databasedrivers;

import com.uwyn.rife.authentication.credentialsmanagers.RoleUserAttributes;
import com.uwyn.rife.authentication.credentialsmanagers.exceptions.GetAttributesErrorException;
import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.DbPreparedStatementHandler;
import com.uwyn.rife.database.DbRowProcessor;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.Select;
import java.sql.ResultSet;
import java.sql.SQLException;

public class org_firebirdsql_jdbc_FBDriver extends generic
{
	public org_firebirdsql_jdbc_FBDriver(Datasource datasource)
	{
		super(datasource);

		mGetAttributes = new Select(getDatasource())
			.field("userId")
			.field("passwd")
			.from(mCreateTableUser.getTable())
			.whereParameter("login", "=");
	}
	
	public boolean install()
	throws CredentialsManagerException
	{
		int poolsize = getDatasource().getPoolsize();
		
		getDatasource().setPoolsize(0);
		try
		{
			super.install();
		}
		finally
		{
			getDatasource().setPoolsize(poolsize);
		}
	
		return true;
	}
	
	public boolean remove()
	throws CredentialsManagerException
	{
		int poolsize = getDatasource().getPoolsize();
		
		getDatasource().setPoolsize(0);
		try
		{
			super.remove();
		}
		finally
		{
			getDatasource().setPoolsize(poolsize);
		}
		
		return true;
	}
	
	public RoleUserAttributes getAttributes(final String login)
	throws CredentialsManagerException
	{
		if (null == login ||
			0 == login.length())
		{
			return null;
		}
		
		final RoleUserAttributes attributes = new RoleUserAttributes();
		
		try
		{
			executeFetchFirst(mGetAttributes, new DbRowProcessor() {
					public boolean processRow(ResultSet resultSet)
					throws SQLException
					{
						attributes.setUserId(resultSet.getLong("userid"));
						attributes.setPassword(resultSet.getString("passwd"));
						return false;
					}
				},
				new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setString("login", login);
					}
				});

			if (attributes != null)
			{
				final long userid = attributes.getUserId();
				
				RoleFetcher	fetcher = new RoleFetcher(attributes);
				executeFetchAll(mGetUserRoles, fetcher, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setLong("userId", userid);
					}
				});
			}
		}
		catch (DatabaseException e)
		{
			throw new GetAttributesErrorException(login, e);
		}

		return attributes;
	}
}
