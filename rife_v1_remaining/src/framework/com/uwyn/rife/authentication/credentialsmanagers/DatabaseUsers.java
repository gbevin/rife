/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DatabaseUsers.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers;

import com.uwyn.rife.authentication.credentialsmanagers.exceptions.*;
import com.uwyn.rife.database.*;
import com.uwyn.rife.database.queries.*;

import com.uwyn.rife.authentication.Credentials;
import com.uwyn.rife.authentication.CredentialsManager;
import com.uwyn.rife.authentication.PasswordEncrypting;
import com.uwyn.rife.authentication.credentials.RoleUserCredentials;
import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import com.uwyn.rife.tools.StringEncryptor;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class DatabaseUsers extends DbQueryManager implements CredentialsManager, RoleUsersManager, PasswordEncrypting
{
	protected StringEncryptor	mPasswordEncryptor = null;
	
    protected DatabaseUsers(Datasource datasource)
    {
        super(datasource);
    }

	public abstract boolean install()
	throws CredentialsManagerException;
	
	public abstract boolean remove()
	throws CredentialsManagerException;

	public StringEncryptor getPasswordEncryptor()
	{
		return mPasswordEncryptor;
	}
	
	public void setPasswordEncryptor(StringEncryptor passwordEncryptor)
	{
		mPasswordEncryptor = passwordEncryptor;
	}
	
	protected boolean _install(final CreateSequence createSequenceRole, final CreateTable createTableRole, final CreateTable createTableUser, final CreateTable createTableRoleLink)
	throws CredentialsManagerException
	{
		assert createSequenceRole != null;
		assert createTableRole != null;
		assert createTableUser != null;
		assert createTableRoleLink != null;
		
		try
		{
			executeUpdate(createSequenceRole);
			executeUpdate(createTableRole);
			executeUpdate(createTableUser);
			executeUpdate(createTableRoleLink);
		}
		catch (DatabaseException e)
		{
			throw new InstallCredentialsErrorException(e);
		}

		return true;
	}
	
	protected boolean _remove(final DropSequence dropSequenceRole, final DropTable dropTableRole, final DropTable dropTableUser, final DropTable dropTableRoleLink)
	throws CredentialsManagerException
	{
		assert dropSequenceRole != null;
		assert dropTableRole != null;
		assert dropTableUser != null;
		assert dropTableRoleLink != null;

		try
		{
			executeUpdate(dropTableRoleLink);
			executeUpdate(dropTableUser);
			executeUpdate(dropTableRole);
			executeUpdate(dropSequenceRole);
		}
		catch (DatabaseException e)
		{
			throw new RemoveCredentialsErrorException(e);
		}

		return true;
	}


	protected long _verifyCredentials(Select verifyCredentialsNoRole, Select verifyCredentialsRole, Credentials credentials)
	throws CredentialsManagerException
	{
		assert verifyCredentialsNoRole != null;
		assert verifyCredentialsRole != null;
		
		if (null == credentials)
		{
			return -1;
		}
		
		long result = -1;
		
		RoleUserCredentials role_user = null;
		if (credentials instanceof RoleUserCredentials)
		{
			role_user = (RoleUserCredentials)credentials;
		}
		else
		{
			throw new UnsupportedCredentialsTypeException(credentials);
		}

		try
		{
			Select query = null;
			// check if the role should be verified too and get the appropriate query
			if (null == role_user.getRole())
			{
				query = verifyCredentialsNoRole;
			}
			else
			{
				query = verifyCredentialsRole;
			}
			
			ProcessVerifyCredentials	process_verify_credentials = new ProcessVerifyCredentials();
			
			if (executeFetchFirst(query, process_verify_credentials, new DbPreparedStatementHandler<RoleUserCredentials>(role_user) {
					public void setParameters(DbPreparedStatement statement)
					{
						statement.setString("login", mData.getLogin());
								
						// handle automatic password encoding
						if (null == mPasswordEncryptor)
						{
							statement.setString("passwd", mData.getPassword());
						}
						else
						{
							try
							{
								statement.setString("passwd", mPasswordEncryptor.encrypt(mData.getPassword()));
							}
							catch (NoSuchAlgorithmException e)
							{
								throw new DatabaseException(e);
							}
						}
						
						// set the role if that's required
						if (mData.getRole() != null)
						{
							statement.setString("role", mData.getRole());
						}
					}
				}))
			{
				result = process_verify_credentials.getUserId();
			}
		}
		catch (DatabaseException e)
		{
			throw new VerifyCredentialsErrorException(credentials, e);
		}

		return result;
	}
	
	protected void _addRole(final SequenceValue getRoleId, final Insert addRole, final String role)
	throws CredentialsManagerException
	{
		assert getRoleId != null;
		assert addRole != null;
		
		if (null == role ||
			0 == role.length())
		{
			throw new AddRoleErrorException(role);
		}
		
		try
		{
			try
			{
				inTransaction(new DbTransactionUserWithoutResult() {
					public void useTransactionWithoutResult()
					throws InnerClassException
					{
						// get the id of the new role
						final int role_id = executeGetFirstInt(getRoleId);
						if (-1 == role_id)
						{
							throwException(new AddRoleErrorException(role));
						}
						
						// store the new role with the obtained id
						if (0 == executeUpdate(addRole, new DbPreparedStatementHandler() {
								public void setParameters(DbPreparedStatement statement)
								{
									statement
										.setInt("roleId", role_id)
										.setString("name", role);
								}
							}))
						{
							throwException(new AddRoleErrorException(role));
						}
					}});
			}
			catch (InnerClassException e)
			{
				throw (CredentialsManagerException)e.getCause();
			}
		}
		catch (DatabaseException e)
		{
			throw new AddRoleErrorException(role, e);
		}
	}
	
	protected boolean _containsRole(Select containsRole, final String role)
	throws CredentialsManagerException
	{
		assert containsRole != null;
		
		if (null == role ||
			0 == role.length())
		{
			return false;
		}
		
		boolean result = false;
		
		try
		{
			result = executeHasResultRows(containsRole, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setString("name", role);
					}
				});
		}
		catch (DatabaseException e)
		{
			throw new ContainsRoleErrorException(role, e);
		}

		return result;
	}
	
	protected long _countRoles(Select countRoles)
	throws CredentialsManagerException
	{
		assert countRoles != null;
		
		try
		{
			return executeGetFirstLong(countRoles);
		}
		catch (DatabaseException e)
		{
			throw new CountRolesErrorException(e);
		}
	}
	
	protected boolean _listRoles(Select listRolesQuery, ListRoles processor)
	throws CredentialsManagerException
	{
		assert listRolesQuery != null;
		
		if (null == processor)
		{
			return false;
		}

		boolean result = false;
		
		try
		{
			result = executeFetchAll(listRolesQuery, new ListDatabaseRoles(processor));
		}
		catch (DatabaseException e)
		{
			throw new ListRolesErrorException(e);
		}
		
		return result;
	}
	
	protected void _addUser(final Insert addUserWithId, final Select getFreeUserId, final Select getRoleId, final Insert addRoleLink, final String login, final RoleUserAttributes attributes)
	throws CredentialsManagerException
	{
		assert addUserWithId != null;
		assert getFreeUserId != null;
		assert getRoleId != null;
		assert addRoleLink != null;
		
		if (null == login ||
			0 == login.length() ||
			null == attributes)
		{
			throw new AddUserErrorException(login, attributes);
		}
		
		try
		{
			try
			{
				inTransaction(new DbTransactionUserWithoutResult() {
					public void useTransactionWithoutResult()
					throws InnerClassException
					{
						// ensure that the password is encoded if an encoder has been set
						String password = null;
						if (null == mPasswordEncryptor ||
							attributes.getPassword().startsWith(mPasswordEncryptor.toString()))
						{
							password = attributes.getPassword();
						}
						else
						{
							try
							{
								password = mPasswordEncryptor.encrypt(attributes.getPassword());
							}
							catch (NoSuchAlgorithmException e)
							{
								throwException(new AddUserErrorException(login, attributes, e));
							}
						}
						
						synchronized (getFreeUserId)
						{
							final String adapted_password = password;
							
							// get a new user id if it has not been provided
							if (attributes.getUserId() < 0)
							{
								attributes.setUserId(executeGetFirstLong(getFreeUserId));
							}
							
							if (0 == executeUpdate(addUserWithId, new DbPreparedStatementHandler() {
									public void setParameters(DbPreparedStatement statement)
									{
										statement
											.setLong("userId", attributes.getUserId())
											.setString("login", login)
											.setString("passwd", adapted_password);
									}
								}))
							{
								throwException(new AddUserErrorException(login, attributes));
							}
						}
						
						// assign the correct roles to the user
						if (attributes.getRoles() != null)
						{
							
							// insert the role link
							if (0 == executeUpdate(addRoleLink, new DbPreparedStatementHandler() {
									public int performUpdate(DbPreparedStatement statement)
									{
										for (String role : attributes.getRoles())
										{
											// obtain the role id
											int roleid = executeGetFirstInt(getRoleId, new DbPreparedStatementHandler<String>(role) {
													public void setParameters(DbPreparedStatement statement)
													{
														statement.setString("name", mData);
													}
												});
													
											if (-1 == roleid)
											{
												throw new UnknownRoleErrorException(role, login, attributes);
											}
											
											statement
												.setLong("userId", attributes.getUserId())
												.setInt("roleId", roleid);
											
											if (0 == statement.executeUpdate())
											{
												return 0;
											}
											
											statement.clearParameters();
										}
										
										return 1;
									}
								}))
							{
								throwException(new AddUserErrorException(login, attributes));
							}
						}
					}});
			}
			catch (InnerClassException e)
			{
				throw (CredentialsManagerException)e.getCause();
			}
		}
		catch (DatabaseException e)
		{
			throw new AddUserErrorException(login, attributes, e);
		}
	}
	
	protected RoleUserAttributes _getAttributes(Select getAttributes, Select getUserRoles, final String login)
	throws CredentialsManagerException
	{
		assert getAttributes != null;
		assert getUserRoles != null;
		
		if (null == login ||
			0 == login.length())
		{
			return null;
		}
		
		RoleUserAttributes attributes = null;
		
		try
		{
			attributes = executeFetchFirstBean(getAttributes, RoleUserAttributes.class, new DbPreparedStatementHandler() {
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
				executeFetchAll(getUserRoles, fetcher, new DbPreparedStatementHandler() {
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
	
	protected class RoleFetcher extends DbRowProcessor
	{
		private RoleUserAttributes	mAttributes = null;
		
		public RoleFetcher(RoleUserAttributes attributes)
		{
			mAttributes = attributes;
		}
		
		public boolean processRow(ResultSet resultSet)
		throws SQLException
		{
			mAttributes.addRole(resultSet.getString("name"));
			
			return true;
		}
	}
	
	protected boolean _containsUser(Select containsUser, final String login)
	throws CredentialsManagerException
	{
		assert containsUser != null;
		
		if (null == login ||
			0 == login.length())
		{
			return false;
		}
		
		boolean result = false;
		
		try
		{
			result = executeHasResultRows(containsUser, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setString("login", login);
					}
				});
		}
		catch (DatabaseException e)
		{
			throw new ContainsUserErrorException(login, e);
		}

		return result;
	}
	
	protected long _countUsers(Select countUsers)
	throws CredentialsManagerException
	{
		assert countUsers != null;

		long result = -1;
		
		try
		{
			result = executeGetFirstLong(countUsers);
		}
		catch (DatabaseException e)
		{
			throw new CountUsersErrorException(e);
		}
		
		return result;
	}

	protected String _getLogin(Select getLogin, final long userId)
	throws CredentialsManagerException
	{
		assert getLogin != null;

		if (userId < 0)
		{
			return null;
		}

		String result = null;
		
		try
		{
			result = executeGetFirstString(getLogin, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setLong("userId", userId);
					}
				});
		}
		catch (DatabaseException e)
		{
			throw new GetLoginErrorException(e, userId);
		}

		return result;
	}

	protected long _getUserId(Select getUserId, final String login)
	throws CredentialsManagerException
	{
		assert getUserId != null;

		if (null == login ||
			0 == login.length())
		{
			return -1;
		}

		long result = -1;
		
		try
		{
			result = executeGetFirstLong(getUserId, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setString("login", login);
					}
				});
		}
		catch (DatabaseException e)
		{
			throw new GetUserIdErrorException(e, login);
		}

		return result;
	}
	
	protected boolean _listUsers(Select listUsersQuery, ListUsers processor)
	throws CredentialsManagerException
	{
		assert listUsersQuery != null;
		
		if (null == processor)
		{
			return false;
		}

		boolean result = false;
		
		try
		{
			ListDatabaseUsers	row_processor = new ListDatabaseUsers(processor);
			result = executeFetchAll(listUsersQuery, row_processor);
		}
		catch (DatabaseException e)
		{
			throw new ListUsersErrorException(e);
		}
		
		return result;
	}
	
	protected boolean _listUsers(Select listUsersRangedQuery, ListUsers processor, final int limit, final int offset)
	throws CredentialsManagerException
	{
		assert listUsersRangedQuery != null;
		
		if (null == processor ||
			limit <= 0)
		{
			return false;
		}

		boolean result = false;
		
		try
		{
			ListDatabaseUsers	row_processor = new ListDatabaseUsers(processor);
			result = executeFetchAll(listUsersRangedQuery, row_processor, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setInt("limit", limit)
							.setInt("offset", offset);
					}
				});
		}
		catch (DatabaseException e)
		{
			throw new ListUsersErrorException(e);
		}
		
		return result;
	}
	
	protected boolean _isUserInRole(Select isUserInRole, final long userId, final String role)
	throws CredentialsManagerException
	{
		assert isUserInRole != null;
		
		if (userId < 0 ||
			null == role ||
			0 == role.length())
		{
			return false;
		}
		
		boolean result = false;
		
		try
		{
			result = executeHasResultRows(isUserInRole, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setLong("userId", userId)
							.setString("role", role);
					}
				});
		}
		catch (DatabaseException e)
		{
			throw new IsUserInRoleErrorException(userId, role, e);
		}

		return result;
	}
	
	protected boolean _listUsersInRole(Select listUsersInRole, ListUsers processor, final String role)
	throws CredentialsManagerException
	{
		assert listUsersInRole != null;
		
		if (null == processor)
		{
			return false;
		}

		if (null == role ||
			0 == role.length())
		{
			return false;
		}

		boolean result = false;
		
		try
		{
			ListDatabaseUsers	row_processor = new ListDatabaseUsers(processor);
			result = executeFetchAll(listUsersInRole, row_processor, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setString("role", role);
					}
				});
		}
		catch (DatabaseException e)
		{
			throw new ListUsersErrorException(e);
		}

		return result;
	}
	
	protected boolean _updateUser(final Update updateUser, final Delete removeRoleLinksByUser, final Select getRoleId, final Insert addRoleLink, final String login, final RoleUserAttributes attributes)
	throws CredentialsManagerException
	{
		assert updateUser != null;
		assert removeRoleLinksByUser != null;
		assert getRoleId != null;
		assert addRoleLink != null;
		
		if (null == login ||
			0 == login.length() ||
			null == attributes)
		{
			throw new UpdateUserErrorException(login, attributes);
		}
		
		boolean result = false;
		try
		{
			try
			{
				result = ((Boolean)inTransaction(new DbTransactionUser() {
					public Boolean useTransaction()
					throws InnerClassException
					{
						// obtain the user id
						try
						{
							final long userid = getUserId(login);
							
							if (userid < 0)
							{
								throwException(new UpdateUserErrorException(login, attributes));
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
										throwException(new UpdateUserErrorException(login, attributes, e));
									}
								}
								
								// update the user password
								final String adapted_password = password;
								if (0 == executeUpdate(updateUser, new DbPreparedStatementHandler() {
										public void setParameters(DbPreparedStatement statement)
										{
											statement
												.setString("passwd", adapted_password)
												.setString("login", login);
										}
									}))
								{
									// no update was performed
									return false;
								}
							}
							
							// remove the previous roles
							executeUpdate(removeRoleLinksByUser, new DbPreparedStatementHandler() {
									public void setParameters(DbPreparedStatement statement)
									{
										statement
											.setLong("userId", userid);
									}
								});
							
							// assign the correct roles to the user
							if (attributes.getRoles() != null)
							{
								if (0 == executeUpdate(addRoleLink, new DbPreparedStatementHandler() {
										public int performUpdate(DbPreparedStatement statement)
										{
											for (String role : attributes.getRoles())
											{
												// obtain the role id
												int roleid = executeGetFirstInt(getRoleId, new DbPreparedStatementHandler<String>(role) {
														public void setParameters(DbPreparedStatement statement)
														{
															statement.setString("name", mData);
														}
													});
														
												if (-1 == roleid)
												{
													throwException(new UnknownRoleErrorException(role, login, attributes));
												}
												
												statement
													.setLong("userId", userid)
													.setInt("roleId", roleid);
												
												if (0 == statement.executeUpdate())
												{
													return 0;
												}
												
												statement.clearParameters();
											}
											
											return 1;
										}
									}))
								{
									throwException(new UpdateUserErrorException(login, attributes));
								}
							}
						}
						catch (CredentialsManagerException e)
						{
							throwException(new UpdateUserErrorException(login, attributes, e));
						}
						
						// update was successful
						return true;
					}})).booleanValue();
			}
			catch (InnerClassException e)
			{
				throw (CredentialsManagerException)e.getCause();
			}
		}
		catch (DatabaseException e)
		{
			throw new UpdateUserErrorException(login, attributes, e);
		}

		return result;
	}

	protected boolean _removeUser(Delete removeUserByLogin, final String login)
	throws CredentialsManagerException
	{
		assert removeUserByLogin != null;
		
		if (null == login ||
			0 == login.length())
		{
			return false;
		}
		
		boolean result = false;
		
		try
		{
			if (0 != executeUpdate(removeUserByLogin, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setString("login", login);
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
	
	protected boolean _removeUser(Delete removeUserByUserId, final long userId)
	throws CredentialsManagerException
	{
		assert removeUserByUserId != null;
		
		if (userId < 0)
		{
			return false;
		}
		
		boolean result = false;
		
		try
		{
			if (0 != executeUpdate(removeUserByUserId, new DbPreparedStatementHandler() {
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
	
	protected boolean _removeRole(Delete removeRole, final String name)
	throws CredentialsManagerException
	{
		assert removeRole != null;
		
		if (null == name ||
			0 == name.length())
		{
			return false;
		}
		
		boolean result = false;
		
		try
		{
			if (0 != executeUpdate(removeRole, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setString("role", name);
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
	
	protected void _clearUsers(Delete clearUsers)
	throws CredentialsManagerException
	{
		assert clearUsers != null;
		
		try
		{
			executeUpdate(clearUsers);
		}
		catch (DatabaseException e)
		{
			throw new ClearUsersErrorException(e);
		}
	}
	
	protected boolean _listUserRoles(Select listUserRolesQuery, final String login, final ListRoles processor)
	throws CredentialsManagerException
	{
		assert listUserRolesQuery != null;
		
		if (null == processor)
		{
			return false;
		}

		boolean result = false;
		
		try
		{
			result = executeFetchAll(listUserRolesQuery, new ListDatabaseRoles(processor), new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setString("login", login);
					}
				});
		}
		catch (DatabaseException e)
		{
			throw new ListRolesErrorException(e);
		}

		return result;
	}

	protected class ProcessVerifyCredentials extends DbRowProcessor
	{
		private long mUserId = -1;

		public boolean processRow(ResultSet resultSet)
		throws SQLException
		{
			assert resultSet != null;
			
			mUserId = resultSet.getLong(1);

			return true;
		}

		public long getUserId()
		{
			return mUserId;
		}
	}
	
	protected static class ListDatabaseRoles extends DbRowProcessor
	{
		private ListRoles	mListRoles = null;
		
		public ListDatabaseRoles(ListRoles listRoles)
		{
			mListRoles = listRoles;
		}
		
		public boolean processRow(ResultSet resultSet)
		throws SQLException
		{
			return mListRoles.foundRole(resultSet.getString("name"));
		}
	}
	
	protected static class ListDatabaseUsers extends DbRowProcessor
	{
		private ListUsers	mListUsers = null;
		
		public ListDatabaseUsers(ListUsers listUsers)
		{
			mListUsers = listUsers;
		}
		
		public boolean processRow(ResultSet resultSet)
		throws SQLException
		{
			return mListUsers.foundUser(resultSet.getLong("userId"), resultSet.getString("login"), resultSet.getString("passwd"));
		}
	}
}

