/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: generic.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.databasedrivers;

import com.uwyn.rife.database.queries.*;

import com.uwyn.rife.authentication.Credentials;
import com.uwyn.rife.authentication.credentialsmanagers.DatabaseUsers;
import com.uwyn.rife.authentication.credentialsmanagers.ListRoles;
import com.uwyn.rife.authentication.credentialsmanagers.ListUsers;
import com.uwyn.rife.authentication.credentialsmanagers.RoleUserAttributes;
import com.uwyn.rife.authentication.credentialsmanagers.exceptions.DuplicateLoginException;
import com.uwyn.rife.authentication.credentialsmanagers.exceptions.DuplicateRoleException;
import com.uwyn.rife.authentication.credentialsmanagers.exceptions.DuplicateUserIdException;
import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;

public class generic extends DatabaseUsers
{
	protected CreateSequence	mCreateSequenceRole = null;
	protected CreateTable		mCreateTableRole = null;
	protected CreateTable		mCreateTableUser = null;
	protected CreateTable		mCreateTableRoleLink = null;
	protected Select			mVerifyCredentialsNoRole = null;
	protected Select			mVerifyCredentialsRole = null;
	protected Select 			mGetRoleId = null;
	protected SequenceValue		mGetNewRoleId = null;
	protected Insert			mAddRole = null;
	protected Select			mContainsRole = null;
	protected Select			mCountRoles = null;
	protected Select			mListRoles = null;
	protected Insert			mAddUserWithId = null;
	protected Select			mGetFreeUserId = null;
	protected Insert			mAddRoleLink = null;
	protected Select			mGetAttributes = null;
	protected Select			mGetUserRoles = null;
	protected Select			mContainsUser = null;
	protected Select			mCountUsers = null;
	protected Select			mGetLogin = null;
	protected Select			mGetUserId = null;
	protected Select			mListUsers = null;
	protected Select			mListUsersRanged = null;
	protected Select			mIsUserInRole = null;
	protected Select			mListUsersInRole = null;
	protected Update			mUpdateUser = null;
	protected Delete			mRemoveRoleLinksByUserId = null;
	protected Delete			mRemoveUserByLogin = null;
	protected Delete			mRemoveUserByUserId = null;
	protected Delete			mRemoveRole = null;
	protected Delete			mClearUsers = null;
	protected Select 			mListUserRoles = null;
	protected DropSequence		mDropSequenceRole = null;
	protected DropTable			mDropTableRole = null;
	protected DropTable			mDropTableUser = null;
	protected DropTable			mDropTableRoleLink = null;
	
	public generic(Datasource datasource)
	{
		super(datasource);

		mCreateSequenceRole = new CreateSequence(getDatasource())
			.name(RifeConfig.Authentication.getSequenceRole());

		mCreateTableRole = new CreateTable(getDatasource())
			.table(RifeConfig.Authentication.getTableRole())
			.column("roleId", int.class, CreateTable.NOTNULL)
			.column("name", String.class, RifeConfig.Authentication.getRoleNameMaximumLength(), CreateTable.NOTNULL)
			.primaryKey(RifeConfig.Authentication.getTableRole().toUpperCase()+"_PK", "roleId")
			.unique(RifeConfig.Authentication.getTableRole().toUpperCase()+"_NAME_UQ", "name");

		mCreateTableUser = new CreateTable(getDatasource())
			.table(RifeConfig.Authentication.getTableUser())
			.column("userId", long.class, CreateTable.NOTNULL)
			.column("login", String.class, RifeConfig.Authentication.getLoginMaximumLength(), CreateTable.NOTNULL)
			.column("passwd", String.class, RifeConfig.Authentication.getPasswordMaximumLength(), CreateTable.NOTNULL)
			.primaryKey(RifeConfig.Authentication.getTableUser().toUpperCase()+"_PK", "userId")
			.unique(RifeConfig.Authentication.getTableUser().toUpperCase()+"_LOGIN_UQ", "login");

		mCreateTableRoleLink = new CreateTable(getDatasource())
			.table(RifeConfig.Authentication.getTableRoleLink())
			.column("userId", long.class, CreateTable.NOTNULL)
			.column("roleId", int.class, CreateTable.NOTNULL)
			.primaryKey(RifeConfig.Authentication.getTableRoleLink().toUpperCase()+"_PK", new String[] {"userId", "roleId"})
			.foreignKey(RifeConfig.Authentication.getTableRoleLink().toUpperCase()+"_USERID_FK", mCreateTableUser.getTable(), "userId", "userId", null, CreateTable.CASCADE)
			.foreignKey(RifeConfig.Authentication.getTableRoleLink().toUpperCase()+"_ROLEID_FK", mCreateTableRole.getTable(), "roleId", "roleId", null, CreateTable.CASCADE);

		mVerifyCredentialsNoRole = new Select(getDatasource())
			.from(mCreateTableUser.getTable())
			.field("userId")
			.whereParameter("login", "=")
			.whereParameterAnd("passwd", "=");

		mVerifyCredentialsRole = new Select(getDatasource())
			.from(mCreateTableUser.getTable())
			.join(mCreateTableRoleLink.getTable())
			.join(mCreateTableRole.getTable())
			.field(mCreateTableUser.getTable()+".userId")
			.whereParameter("login", "=")
			.whereParameterAnd("passwd", "=")
			.whereAnd(mCreateTableUser.getTable()+".userId = "+mCreateTableRoleLink.getTable()+".userId")
			.whereParameterAnd("name", "role", "=")
			.whereAnd(mCreateTableRole.getTable()+".roleId = "+mCreateTableRoleLink.getTable()+".roleId");

		mGetRoleId = new Select(getDatasource())
			.from(mCreateTableRole.getTable())
			.field("roleId")
			.whereParameter("name", "=");

		mGetNewRoleId = new SequenceValue(getDatasource())
			.name(mCreateSequenceRole.getName())
			.next();

		mAddRole = new Insert(getDatasource())
			.into(mCreateTableRole.getTable())
			.fieldParameter("roleId")
			.fieldParameter("name");

		mContainsRole = new Select(getDatasource())
			.from(mCreateTableRole.getTable())
			.whereParameter("name", "=");

		mCountRoles = new Select(getDatasource())
			.field("count(*)")
			.from(mCreateTableRole.getTable());

		mListRoles = new Select(getDatasource())
			.from(mCreateTableRole.getTable())
			.orderBy("name");

		mAddUserWithId = new Insert(getDatasource())
			.into(mCreateTableUser.getTable())
			.fieldParameter("userId")
			.fieldParameter("login")
			.fieldParameter("passwd");

		mGetFreeUserId = new Select(getDatasource())
			.field("COALESCE(MAX(userId)+1, 0) as freeUserId")
			.from(mCreateTableUser.getTable());

		mAddRoleLink = new Insert(getDatasource())
			.into(mCreateTableRoleLink.getTable())
			.fieldParameter("userId")
			.fieldParameter("roleId");

		mGetAttributes = new Select(getDatasource())
			.field("userId")
			.field("passwd AS password")
			.from(mCreateTableUser.getTable())
			.whereParameter("login", "=");
		
		mGetUserRoles = new Select(getDatasource())
			.field("name")
			.from(mCreateTableRoleLink.getTable())
			.join(mCreateTableRole.getTable())
			.whereParameter("userId", "=")
			.whereAnd(mCreateTableRoleLink.getTable()+".roleId = "+mCreateTableRole.getTable()+".roleId")
			.orderBy("name");

		mContainsUser = new Select(getDatasource())
			.from(mCreateTableUser.getTable())
			.whereParameter("login", "=");

		mCountUsers = new Select(getDatasource())
			.field("count(*)")
			.from(mCreateTableUser.getTable());

		mGetLogin = new Select(getDatasource())
			.field("login")
			.from(mCreateTableUser.getTable())
			.whereParameter("userId", "=");

		mGetUserId = new Select(getDatasource())
			.field("userId")
			.from(mCreateTableUser.getTable())
			.whereParameter("login", "=");

		mListUsers = new Select(getDatasource())
			.from(mCreateTableUser.getTable())
			.orderBy("login");

		mListUsersRanged = new Select(getDatasource())
			.from(mCreateTableUser.getTable())
			.orderBy("login")
			.limitParameter("limit")
			.offsetParameter("offset");

		mIsUserInRole = new Select(getDatasource())
			.from(mCreateTableRoleLink.getTable())
			.join(mCreateTableRole.getTable())
			.field(mCreateTableRoleLink.getTable()+".userId")
			.whereParameter(mCreateTableRoleLink.getTable()+".userId", "=")
			.whereParameterAnd("name", "role", "=")
			.whereAnd(mCreateTableRole.getTable()+".roleId = "+mCreateTableRoleLink.getTable()+".roleId");

		mListUsersInRole = new Select(getDatasource())
			.field(mCreateTableUser.getTable()+".userId")
			.field("login")
			.field("passwd")
			.from(mCreateTableUser.getTable())
			.join(mCreateTableRoleLink.getTable())
			.join(mCreateTableRole.getTable())
			.where(mCreateTableUser.getTable()+".userId = "+mCreateTableRoleLink.getTable()+".userId")
			.whereAnd(mCreateTableRoleLink.getTable()+".roleId = "+mCreateTableRole.getTable()+".roleId")
			.whereParameterAnd(mCreateTableRole.getTable()+".name", "role", "=")
			.orderBy("login");

		mUpdateUser = new Update(getDatasource())
			.table(mCreateTableUser.getTable())
			.fieldParameter("passwd")
			.whereParameter("login", "=");

		mRemoveRoleLinksByUserId = new Delete(getDatasource())
			.from(mCreateTableRoleLink.getTable())
			.whereParameter("userId", "=");

		mRemoveUserByLogin = new Delete(getDatasource())
			.from(mCreateTableUser.getTable())
			.whereParameter("login", "=");

		mRemoveUserByUserId = new Delete(getDatasource())
			.from(mCreateTableUser.getTable())
			.whereParameter("userId", "=");

		mRemoveRole = new Delete(getDatasource())
			.from(mCreateTableRole.getTable())
			.whereParameter("name", "role", "=");

		mClearUsers = new Delete(getDatasource())
			.from(mCreateTableUser.getTable());

		mListUserRoles = new Select(getDatasource())
			.from(mCreateTableRole.getTable())
			.join(mCreateTableRoleLink.getTable())
			.join(mCreateTableUser.getTable())
			.field(mCreateTableRole.getTable()+".name")
			.where(mCreateTableRole.getTable()+".roleId = "+mCreateTableRoleLink.getTable()+".roleId")
			.whereParameterAnd(mCreateTableUser.getTable()+".login", "=")
			.whereAnd(mCreateTableUser.getTable()+".userId = "+mCreateTableRoleLink.getTable()+".userId");

		mDropSequenceRole = new DropSequence(getDatasource())
			.name(mCreateSequenceRole.getName());

		mDropTableRole = new DropTable(getDatasource())
			.table(mCreateTableRole.getTable());

		mDropTableUser = new DropTable(getDatasource())
			.table(mCreateTableUser.getTable());

		mDropTableRoleLink = new DropTable(getDatasource())
			.table(mCreateTableRoleLink.getTable());
	}
	
	public boolean install()
	throws CredentialsManagerException
	{
		return _install(mCreateSequenceRole, mCreateTableRole, mCreateTableUser, mCreateTableRoleLink);
	}
	
	public boolean remove()
	throws CredentialsManagerException
	{
		return _remove(mDropSequenceRole, mDropTableRole, mDropTableUser, mDropTableRoleLink);
	}

	public long verifyCredentials(Credentials credentials)
	throws CredentialsManagerException
	{
		return _verifyCredentials(mVerifyCredentialsNoRole, mVerifyCredentialsRole, credentials);
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
				if (-1 != message.indexOf(mCreateTableRole.getUniqueConstraints().get(0).getName()))
				{
					throw new DuplicateRoleException(role);
				}
			}
		
			throw e;
		}
		
		return this;
	}
	
	public boolean containsRole(String role)
	throws CredentialsManagerException
	{
		return _containsRole(mContainsRole, role);
	}

	public long countRoles()
	throws CredentialsManagerException
	{
		return _countRoles(mCountRoles);
	}

	public boolean listRoles(ListRoles processor)
	throws CredentialsManagerException
	{
		return _listRoles(mListRoles, processor);
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
				if (-1 != message.indexOf(mCreateTableUser.getPrimaryKeys().get(0).getName()))
				{
					throw new DuplicateUserIdException(attributes.getUserId());
				}
				if (-1 != message.indexOf(mCreateTableUser.getUniqueConstraints().get(0).getName()))
				{
					throw new DuplicateLoginException(login);
				}
			}
		
			throw e;
		}
		
		return this;
	}

	public RoleUserAttributes getAttributes(String login)
	throws CredentialsManagerException
	{
		return _getAttributes(mGetAttributes, mGetUserRoles, login);
	}
	
	public boolean containsUser(String login)
	throws CredentialsManagerException
	{
		return _containsUser(mContainsUser, login);
	}

	public long countUsers()
	throws CredentialsManagerException
	{
		return _countUsers(mCountUsers);
	}

	public String getLogin(long userId)
	throws CredentialsManagerException
	{
		return _getLogin(mGetLogin, userId);
	}

	public long getUserId(String login)
	throws CredentialsManagerException
	{
		return _getUserId(mGetUserId, login);
	}

	public boolean listUsers(ListUsers processor)
	throws CredentialsManagerException
	{
		return _listUsers(mListUsers, processor);
	}

	public boolean listUsers(ListUsers processor, int limit, int offset)
	throws CredentialsManagerException
	{
		return _listUsers(mListUsersRanged, processor, limit, offset);
	}

	public boolean isUserInRole(long userId, String role)
	throws CredentialsManagerException
	{
		return _isUserInRole(mIsUserInRole, userId, role);
	}

	public boolean listUsersInRole(ListUsers processor, String role)
	throws CredentialsManagerException
	{
		return _listUsersInRole(mListUsersInRole, processor, role);
	}

	public boolean updateUser(String login, RoleUserAttributes attributes)
	throws CredentialsManagerException
	{
		return _updateUser(mUpdateUser, mRemoveRoleLinksByUserId, mGetRoleId, mAddRoleLink, login, attributes);
	}

	public boolean removeUser(String login)
	throws CredentialsManagerException
	{
		return _removeUser(mRemoveUserByLogin, login);
	}
	
	public boolean removeUser(long userId)
	throws CredentialsManagerException
	{
		return _removeUser(mRemoveUserByUserId, userId);
	}
	
	public boolean removeRole(String name)
	throws CredentialsManagerException
	{
		return _removeRole(mRemoveRole, name);
	}

	public void clearUsers()
	throws CredentialsManagerException
	{
		_clearUsers(mClearUsers);
	}
		
	public boolean listUserRoles(String login, ListRoles processor)
	throws CredentialsManagerException
	{
		return _listUserRoles(mListUserRoles, login, processor);
	}
}



