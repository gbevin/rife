/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: generic.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.sessionmanagers.databasedrivers;

import com.uwyn.rife.database.queries.*;

import com.uwyn.rife.authentication.ListSessions;
import com.uwyn.rife.authentication.exceptions.SessionManagerException;
import com.uwyn.rife.authentication.sessionmanagers.DatabaseSessions;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;

public class generic extends DatabaseSessions
{
	protected CreateTable	mCreateAuthentication = null;
	protected String		mCreateAuthenticationSessStartIndex = null;
	protected Delete		mPurgeSessions = null;
	protected Insert		mStartSession = null;
	protected Select		mIsSessionValid = null;
	protected Select		mIsSessionValidRestrictHostIp = null;
	protected Update		mContinueSession = null;
	protected Delete		mEraseSession = null;
	protected Select		mWasRemembered = null;
	protected Delete		mEraseAllSessions = null;
	protected Delete		mEraseUserSessions = null;
	protected DropTable		mRemoveAuthentication = null;
	protected String		mRemoveAuthenticationSessStartIndex = null;
	protected Select		mCountSessions = null;
	protected Select		mGetSessionUserId = null;
	protected Select		mListSessions = null;
	
	public generic(Datasource datasource)
	{
		super(datasource);

		mCreateAuthentication = new CreateTable(getDatasource())
			.table(RifeConfig.Authentication.getTableAuthentication())
			.column("authId", String.class, 32, CreateTable.NOTNULL)
			.column("userId", long.class, CreateTable.NOTNULL)
			.column("hostIp", String.class, 40, CreateTable.NOTNULL)
			.column("sessStart", long.class, CreateTable.NOTNULL)
			.column("remembered", boolean.class, CreateTable.NOTNULL)
			.defaultValue("remembered", false)
			.primaryKey(RifeConfig.Authentication.getTableAuthentication().toUpperCase()+"_PK", "authId");

		mCreateAuthenticationSessStartIndex = "CREATE INDEX "+RifeConfig.Authentication.getTableAuthentication()+"_IDX ON "+RifeConfig.Authentication.getTableAuthentication()+" (sessStart)";

		mPurgeSessions = new Delete(getDatasource())
			.from(mCreateAuthentication.getTable())
			.whereParameter("sessStart", "<=");
		
		mStartSession = new Insert(getDatasource())
			.into(mCreateAuthentication.getTable())
			.fieldParameter("authId")
			.fieldParameter("userId")
			.fieldParameter("hostIp")
			.fieldParameter("sessStart")
			.fieldParameter("remembered");

		mIsSessionValid = new Select(getDatasource())
			.from(mCreateAuthentication.getTable())
			.whereParameter("authId", "=")
			.whereParameterAnd("sessStart", ">");

		mIsSessionValidRestrictHostIp = new Select(getDatasource())
			.from(mCreateAuthentication.getTable())
			.whereParameter("authId", "=")
			.whereParameterAnd("hostIp", "=")
			.whereParameterAnd("sessStart", ">");

		mContinueSession = new Update(getDatasource())
			.table(mCreateAuthentication.getTable())
			.fieldParameter("sessStart")
			.whereParameter("authId", "=");

		mEraseSession = new Delete(getDatasource())
			.from(mCreateAuthentication.getTable())
			.whereParameter("authId", "=");

		mWasRemembered = new Select(getDatasource())
			.from(mCreateAuthentication.getTable())
			.field("remembered")
			.whereParameter("authId", "=");

		mEraseAllSessions = new Delete(getDatasource())
			.from(mCreateAuthentication.getTable());
		
		mEraseUserSessions = new Delete(getDatasource())
			.from(mCreateAuthentication.getTable())
			.whereParameter("userId", "=");

		mRemoveAuthentication = new DropTable(getDatasource())
			.table(mCreateAuthentication.getTable());
		
		mRemoveAuthenticationSessStartIndex = "DROP INDEX "+RifeConfig.Authentication.getTableAuthentication()+"_IDX";

		mCountSessions = new Select(getDatasource())
			.field("count(*)")
			.from(mCreateAuthentication.getTable())
			.whereParameter("sessStart", ">");
		
		mGetSessionUserId = new Select(getDatasource())
			.field("userId")
			.from(mCreateAuthentication.getTable())
			.whereParameter("authId", "=");

		mListSessions = new Select(getDatasource())
			.from(mCreateAuthentication.getTable())
			.whereParameter("sessStart", ">");
	}
	
	public boolean install()
	throws SessionManagerException
	{
		return _install(mCreateAuthentication, mCreateAuthenticationSessStartIndex);
	}
	
	public boolean remove()
	throws SessionManagerException
	{
		return _remove(mRemoveAuthentication, mRemoveAuthenticationSessStartIndex);
	}

	public void purgeSessions()
	throws SessionManagerException
	{
		_purgeSessions(mPurgeSessions);
	}
	
	public String startSession(long userId, String hostIp, boolean remembered)
	throws SessionManagerException
	{
		return _startSession(mStartSession, userId, hostIp, remembered);
	}
	
	public boolean isSessionValid(String authId, String hostIp)
	throws SessionManagerException
	{
		return _isSessionValid(mIsSessionValid, mIsSessionValidRestrictHostIp, authId, hostIp);
	}
	
	public boolean continueSession(String authId)
	throws SessionManagerException
	{
		return _continueSession(mContinueSession, authId);
	}
	
	public boolean eraseSession(String authId)
	throws SessionManagerException
	{
		return _eraseSession(mEraseSession, authId);
	}
	
	public boolean wasRemembered(String authId)
	throws SessionManagerException
	{
		return _wasRemembered(mWasRemembered, authId);
	}
	
	public void eraseAllSessions()
	throws SessionManagerException
	{
		_eraseAllSessions(mEraseAllSessions);
	}

	public boolean eraseUserSessions(long userId)
	throws SessionManagerException
	{
		return _eraseUserSessions(mEraseUserSessions, userId);
	}
	
	public long countSessions()
	throws SessionManagerException
	{
		return _countSessions(mCountSessions);
	}
	
	public long getSessionUserId(String authId)
	throws SessionManagerException
	{
		return _getSessionUserId(mGetSessionUserId, authId);
	}
	
	public boolean listSessions(ListSessions processor)
	throws SessionManagerException
	{
		return _listSessions(mListSessions, processor);
	}
}
