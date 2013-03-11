/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: com_mysql_jdbc_Driver.java 3918 2008-04-14 17:35:35Z gbevin $
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
import com.uwyn.rife.database.queries.Delete;
import com.uwyn.rife.database.queries.Insert;
import com.uwyn.rife.database.queries.Select;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class com_mysql_jdbc_Driver extends generic
{
	private Delete	mRemoveRoleLinksByRoleId = null;
	
	public com_mysql_jdbc_Driver(Datasource datasource)
	{
		super(datasource);

		mCreateTableRole = new CreateTable(getDatasource())
			.table(RifeConfig.Authentication.getTableRole())
			.column("roleId", int.class, CreateTable.NOTNULL)
			.column("name", String.class, RifeConfig.Authentication.getRoleNameMaximumLength(), CreateTable.NOTNULL)
			.customAttribute("roleId", "AUTO_INCREMENT")
			.primaryKey(RifeConfig.Authentication.getTableRole().toUpperCase()+"_PK", "roleId")
			.unique(RifeConfig.Authentication.getTableRole().toUpperCase()+"_NAME_UQ", "name");
			
		mCreateTableRoleLink = new CreateTable(getDatasource())
			.table(RifeConfig.Authentication.getTableRoleLink())
			.column("userId", long.class, CreateTable.NOTNULL)
			.column("roleId", int.class, CreateTable.NOTNULL)
			.primaryKey(RifeConfig.Authentication.getTableRoleLink().toUpperCase()+"_PK", new String[] {"userId", "roleId"});
		
		mAddRole = new Insert(getDatasource())
			.into(mCreateTableRole.getTable())
			.fieldParameter("name");

		mGetFreeUserId = new Select(getDatasource())
			.field("MAX(userId)+1 as freeUserId")
			.from(mCreateTableUser.getTable());
		
		mGetRoleId = new Select(getDatasource())
			.from(mCreateTableRole.getTable())
			.field("roleId")
			.whereParameter("name", "role", "=");
		
		mRemoveRoleLinksByRoleId = new Delete(getDatasource())
			.from(mCreateTableRoleLink.getTable())
			.whereParameter("roleId", "=");
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
				if (-1 != message.indexOf("DUPLICATE") &&
					-1 != message.indexOf("FOR KEY 2"))
				{
					throw new DuplicateRoleException(role);
				}
			}
		
			throw new AddRoleErrorException(role, e);
		}

		return this;
	}

	public DatabaseUsers addUser(final String login, final RoleUserAttributes attributes)
	throws CredentialsManagerException
	{
		if (null == login ||
			0 == login.length() ||
			null == attributes)
		{
			throw new AddUserErrorException(login, attributes);
		}
		
		try
		{
			// ensure that the password is encoded if an encoder has been set
			String password = null;
			if (null == mPasswordEncryptor ||
				attributes.getPassword().startsWith(mPasswordEncryptor.toString()))
			{
				password =  attributes.getPassword();
			}
			else
			{
				try
				{
					password = mPasswordEncryptor.encrypt(attributes.getPassword());
				}
				catch (NoSuchAlgorithmException e)
				{
					throw new AddUserErrorException(login, attributes, e);
				}
			}
			
			HashMap<String, Integer> roleids = null;
			// get the role ids
			if (attributes.getRoles() != null)
			{
				roleids = new HashMap<String, Integer>();
				
				DbPreparedStatement ps_get_roleid = getConnection().getPreparedStatement(mGetRoleId);
				int					roleid = -1;
				try
				{
					for (String role : attributes.getRoles())
					{
						ps_get_roleid.setString(1, role);
						ps_get_roleid.executeQuery();
						if (ps_get_roleid.getResultSet().hasResultRows())
						{
							roleid = ps_get_roleid.getResultSet().getFirstInt();
						}
						
						if (-1 == roleid)
						{
							throw new UnknownRoleErrorException(role, login, attributes);
						}
						
						roleids.put(role, roleid);
					}
				}
				finally
				{
					ps_get_roleid.close();
				}
			}

			synchronized (mGetFreeUserId)
			{
				final String adapted_password = password;
				
				// get a new user id if it has not been provided
				if (attributes.getUserId() < 0)
				{
					attributes.setUserId(executeGetFirstLong(mGetFreeUserId));
				}
				
				if (0 == executeUpdate(mAddUserWithId, new DbPreparedStatementHandler() {
						public void setParameters(DbPreparedStatement statement)
						{
							statement
								.setLong("userId", attributes.getUserId())
								.setString("login", login)
								.setString("passwd", adapted_password);
						}
					}))
				{
					throw new AddUserErrorException(login, attributes);
				}
			}
			
			// ensure that the correct roles are assigned to the user
			if (attributes.getRoles() != null)
			{
				DbPreparedStatement ps_add_rolelink = getConnection().getPreparedStatement(mAddRoleLink);
				try
				{
					for (String role : attributes.getRoles())
					{
						ps_add_rolelink.setLong(1, attributes.getUserId());
						ps_add_rolelink.setInt(2, roleids.get(role));
						if (0 == ps_add_rolelink.executeUpdate())
						{
							throw new AddUserErrorException(login, attributes);
						}
						ps_add_rolelink.clearParameters();
					}
				}
				finally
				{
					ps_add_rolelink.close();
				}
			}
		}
		catch (DatabaseException e)
		{
			if (null != e.getCause())
			{
				String message = e.getCause().getMessage().toUpperCase();
				if (-1 != message.indexOf("DUPLICATE"))
				{
					if (-1 != message.indexOf("FOR KEY 1"))
					{
						throw new DuplicateUserIdException(attributes.getUserId());
					}
					if (-1 != message.indexOf("FOR KEY 2"))
					{
						throw new DuplicateLoginException(login);
					}
				}
			}
		
			throw new AddUserErrorException(login, attributes, e);
		}

		return this;
	}
	
	public boolean updateUser(final String login, RoleUserAttributes attributes)
	throws CredentialsManagerException
	{
		if (null == login ||
			0 == login.length() ||
			null == attributes)
		{
			throw new UpdateUserErrorException(login, attributes);
		}
		
		try
		{
			HashMap<String, Integer> roleids = null;
			// get the role ids
			if (attributes.getRoles() != null)
			{
				roleids = new HashMap<String, Integer>();
				
				DbPreparedStatement ps_get_roleid = getConnection().getPreparedStatement(mGetRoleId);
				int					roleid = -1;
				try
				{
					for (String role : attributes.getRoles())
					{
						ps_get_roleid.setString(1, role);
						ps_get_roleid.executeQuery();
						if (ps_get_roleid.getResultSet().hasResultRows())
						{
							roleid = ps_get_roleid.getResultSet().getFirstInt();
						}
						
						if (-1 == roleid)
						{
							throw new UnknownRoleErrorException(role, login, attributes);
						}
						
						roleids.put(role, roleid);
					}
				}
				finally
				{
					ps_get_roleid.close();
				}
			}

			// obtain the user id
			final long userid = getUserId(login);
			if (userid < 0)
			{
				throw new UpdateUserErrorException(login, attributes);
			}
			
			// only handle the password if it has been provided
			if (attributes.getPassword() != null)
			{
				// ensure that the password is encoded if an encoder has been set
				String password = null;
				if (null == mPasswordEncryptor ||
					attributes.getPassword().startsWith(mPasswordEncryptor.toString()))
				{
					password =  attributes.getPassword();
				}
				else
				{
					try
					{
						password = mPasswordEncryptor.encrypt(attributes.getPassword());
					}
					catch (NoSuchAlgorithmException e)
					{
						throw new UpdateUserErrorException(login, attributes, e);
					}
				}
				
				// update the user password
				final String adapted_password = password;
				if (0 == executeUpdate(mUpdateUser, new DbPreparedStatementHandler() {
						public void setParameters(DbPreparedStatement statement)
						{
							statement
								.setString("passwd", adapted_password)
								.setString("login", login);
						}
					}))
				{
					return false;
				}
			}
			
			// remove the previous roles
			executeUpdate(mRemoveRoleLinksByUserId, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setLong("userId", userid);
					}
				});
			
			// assign the correct roles to the user
			if (attributes.getRoles() != null)
			{
				// add the provided roles
				DbPreparedStatement ps_add_rolelink = getConnection().getPreparedStatement(mAddRoleLink);
				try
				{
					for (String role : attributes.getRoles())
					{
						ps_add_rolelink.setLong(1, userid);
						ps_add_rolelink.setInt(2, roleids.get(role));
						if (0 == ps_add_rolelink.executeUpdate())
						{
							throw new AddUserErrorException(login, attributes);
						}
						ps_add_rolelink.clearParameters();
					}
				}
				finally
				{
					ps_add_rolelink.close();
				}
			}
		}
		catch (DatabaseException e)
		{
			throw new UpdateUserErrorException(login, attributes, e);
		}

		return true;
	}

	public boolean removeUser(final String login)
	throws CredentialsManagerException
	{
		if (null == login ||
			0 == login.length())
		{
			return false;
		}
		
		boolean result = false;
		
		try
		{
			final long userid = executeGetFirstLong(mGetUserId, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setString("login", login);
					}
				});

			if (0 != executeUpdate(mRemoveUserByLogin, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setString("login", login);
					}
				}))
			{
				result = true;
			}
			
			if (0 != executeUpdate(mRemoveRoleLinksByUserId, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setLong("userId", userid);
					}
				}))
			{
				result = true;
			}
		}
		catch (DatabaseException e)
		{
			throw new RemoveUserErrorException(login, e);
		}

		return result;
	}
	
	public boolean removeUser(final long userId)
	throws CredentialsManagerException
	{
		if (userId < 0)
		{
			return false;
		}
		
		boolean result = false;
		
		try
		{
			if (0 != executeUpdate(mRemoveUserByUserId, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setLong("userId", userId);
					}
				}))
			{
				result = true;
			}
			
			if (0 != executeUpdate(mRemoveRoleLinksByUserId, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setLong("userId", userId);
					}
				}))
			{
				result = true;
			}
		}
		catch (DatabaseException e)
		{
			throw new RemoveUserErrorException(userId, e);
		}

		return result;
	}
	
	public boolean removeRole(final String name)
	throws CredentialsManagerException
	{
		if (null == name ||
			0 == name.length())
		{
			return false;
		}
		
		boolean result = false;
		
		try
		{
			final int roleid = executeGetFirstInt(mGetRoleId, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setString("role", name);
					}
				});

			if (0 != executeUpdate(mRemoveRole, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setString("role", name);
					}
				}))
			{
				result = true;
			}

			if (0 != executeUpdate(mRemoveRoleLinksByRoleId, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setInt("roleId", roleid);
					}
				}))
			{
				result = true;
			}
		}
		catch (DatabaseException e)
		{
			throw new RemoveRoleErrorException(name, e);
		}

		return result;
	}
}
