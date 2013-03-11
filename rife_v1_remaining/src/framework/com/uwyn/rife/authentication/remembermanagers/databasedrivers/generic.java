/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: generic.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.remembermanagers.databasedrivers;

import com.uwyn.rife.authentication.exceptions.RememberManagerException;
import com.uwyn.rife.authentication.remembermanagers.DatabaseRemember;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.queries.CreateTable;
import com.uwyn.rife.database.queries.Delete;
import com.uwyn.rife.database.queries.DropTable;
import com.uwyn.rife.database.queries.Insert;
import com.uwyn.rife.database.queries.Select;

public class generic extends DatabaseRemember
{
	protected CreateTable	mCreateRemember = null;
	protected String		mCreateRememberMomentIndex = null;
	protected DropTable		mRemoveRemember = null;
	protected String		mRemoveRememberMomentIndex = null;
	protected Insert		mCreateRememberId = null;
	protected Delete		mEraseRememberId = null;
	protected Delete		mEraseUserRememberIds = null;
	protected Delete		mEraseAllRememberIds = null;
	protected Select		mGetRememberedUserId = null;
	protected Delete		mPurgeRememberIds = null;
	
	public generic(Datasource datasource)
	{
		super(datasource);

		mCreateRemember = new CreateTable(getDatasource())
			.table(RifeConfig.Authentication.getTableRemember())
			.column("rememberId", String.class, 32, CreateTable.NOTNULL)
			.column("userId", long.class, CreateTable.NOTNULL)
			.column("moment", long.class, CreateTable.NOTNULL)
			.primaryKey(RifeConfig.Authentication.getTableRemember().toUpperCase()+"_PK", "rememberId");

		mCreateRememberMomentIndex = "CREATE INDEX "+RifeConfig.Authentication.getTableRemember()+"_moment_IDX ON "+RifeConfig.Authentication.getTableRemember()+" (moment)";

		mRemoveRemember = new DropTable(getDatasource())
			.table(mCreateRemember.getTable());
		
		mRemoveRememberMomentIndex = "DROP INDEX "+RifeConfig.Authentication.getTableRemember()+"_moment_IDX";

		mCreateRememberId = new Insert(getDatasource())
			.into(mCreateRemember.getTable())
			.fieldParameter("rememberId")
			.fieldParameter("userId")
			.fieldParameter("moment");

		mEraseRememberId = new Delete(getDatasource())
			.from(mCreateRemember.getTable())
			.whereParameter("rememberId", "=");
		
		mEraseUserRememberIds = new Delete(getDatasource())
			.from(mCreateRemember.getTable())
			.whereParameter("userId", "=");

		mEraseAllRememberIds = new Delete(getDatasource())
			.from(mCreateRemember.getTable());

		mGetRememberedUserId = new Select(getDatasource())
			.field("userId")
			.from(mCreateRemember.getTable())
			.whereParameter("rememberId", "=");
		
		mPurgeRememberIds = new Delete(getDatasource())
			.from(mCreateRemember.getTable())
			.whereParameter("moment", "<=");
	}
	
	public boolean install()
	throws RememberManagerException
	{
		return _install(mCreateRemember, mCreateRememberMomentIndex);
	}
	
	public boolean remove()
	throws RememberManagerException
	{
		return _remove(mRemoveRemember, mRemoveRememberMomentIndex);
	}
	
	public String createRememberId(long userId, String hostIp)
	throws RememberManagerException
	{
		return _createRememberId(mCreateRememberId, userId, hostIp);
	}
	
	public boolean eraseRememberId(String rememberId)
	throws RememberManagerException
	{
		return _eraseRememberId(mEraseRememberId, rememberId);
	}

	public boolean eraseUserRememberIds(long userId)
	throws RememberManagerException
	{
		return _eraseUserRememberIds(mEraseUserRememberIds, userId);
	}
	
	public void eraseAllRememberIds()
	throws RememberManagerException
	{
		_eraseAllRememberIds(mEraseAllRememberIds);
	}
	
	public long getRememberedUserId(String rememberId)
	throws RememberManagerException
	{
		return _getRememberedUserId(mGetRememberedUserId, rememberId);
	}

	public void purgeRememberIds()
	throws RememberManagerException
	{
		_purgeRememberIds(mPurgeRememberIds);
	}
}
