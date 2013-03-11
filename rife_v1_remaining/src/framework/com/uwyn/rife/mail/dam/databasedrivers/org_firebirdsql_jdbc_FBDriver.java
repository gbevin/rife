/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: org_firebirdsql_jdbc_FBDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.mail.dam.databasedrivers;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.mail.dam.DatabaseMailQueue;
import com.uwyn.rife.mail.exceptions.InstallMailQueueErrorException;
import com.uwyn.rife.mail.exceptions.MailQueueManagerException;

public class org_firebirdsql_jdbc_FBDriver extends DatabaseMailQueue
{
    public org_firebirdsql_jdbc_FBDriver(Datasource datasource)
    {
		super(datasource);
	}
	
	public boolean install()
	throws MailQueueManagerException
	{
		try
		{
			mManager.install(mManager.getInstallTableQuery().precision("body", 8191));
		}
		catch (DatabaseException e)
		{
			throw new InstallMailQueueErrorException(e);
		}

		return true;
	}
}
