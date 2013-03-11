/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DatabaseRemember.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.remembermanagers;

import com.uwyn.rife.authentication.remembermanagers.exceptions.*;

import com.uwyn.rife.authentication.RememberManager;
import com.uwyn.rife.authentication.exceptions.RememberManagerException;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.DbPreparedStatementHandler;
import com.uwyn.rife.database.DbQueryManager;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.CreateTable;
import com.uwyn.rife.database.queries.Delete;
import com.uwyn.rife.database.queries.DropTable;
import com.uwyn.rife.database.queries.Insert;
import com.uwyn.rife.database.queries.Select;
import com.uwyn.rife.tools.StringEncryptor;
import com.uwyn.rife.tools.UniqueID;
import com.uwyn.rife.tools.UniqueIDGenerator;
import java.security.NoSuchAlgorithmException;

public abstract class DatabaseRemember extends DbQueryManager implements RememberManager
{
	private long	mRememberDuration = RifeConfig.Authentication.getRememberDuration();
	
    protected DatabaseRemember(Datasource datasource)
    {
        super(datasource);
    }
	
	public long getRememberDuration()
	{
		return mRememberDuration;
	}
	
	public void setRememberDuration(long milliseconds)
	{
		mRememberDuration = milliseconds;
	}

	public abstract boolean install()
	throws RememberManagerException;
	
	public abstract boolean remove()
	throws RememberManagerException;
	
	protected boolean _install(CreateTable createRemember, String createRememberMomentIndex)
	{
		executeUpdate(createRemember);
		executeUpdate(createRememberMomentIndex);
		
		return true;
	}
	
	protected boolean _remove(DropTable removeRemember, String removeRememberMomentIndex)
	{
		executeUpdate(removeRememberMomentIndex);
		executeUpdate(removeRemember);
		
		return true;
	}
	
	protected String _createRememberId(Insert createRememberId, final long userId, String hostIp)
	throws RememberManagerException
	{
		assert createRememberId != null;
		
		if (userId < 0)
		{
			throw new CreateRememberIdErrorException(userId);
		}
		
		final UniqueID	remember_id = UniqueIDGenerator.generate(hostIp);
		final String	remember_id_string = remember_id.toString();
		
		try
		{
			if (0 == executeUpdate(createRememberId, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setString("rememberId", remember_id_string)
							.setLong("userId", userId)
							.setLong("moment", System.currentTimeMillis());
					}
				}))
			{
				throw new CreateRememberIdErrorException(userId);
			}
		}
		catch (DatabaseException e)
		{
			throw new CreateRememberIdErrorException(userId, e);
		}

		try
		{
			return StringEncryptor.SHA.encrypt(String.valueOf(userId))+"|"+remember_id_string;
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new CreateRememberIdErrorException(userId, e);
		}
	}
	
	protected boolean _eraseRememberId(Delete eraseRememberId, final String rememberId)
	throws RememberManagerException
	{
		assert eraseRememberId != null;
		
		if (null == rememberId ||
			0 == rememberId.length())
		{
			return false;
		}
		
		final int rememberid_slash = rememberId.indexOf("|");
		if (-1 == rememberid_slash)
		{
			return false;
		}

		boolean result = false;
		try
		{
			if (0 != executeUpdate(eraseRememberId, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setString("rememberId", rememberId.substring(rememberid_slash+1));
					}
				}))
			{
				result = true;
			}
		}
		catch (DatabaseException e)
		{
			throw new EraseRememberIdErrorException(rememberId, e);
		}

		return result;
	}

	protected boolean _eraseUserRememberIds(Delete eraseUserRememberIds, final long userId)
	throws RememberManagerException
	{
		assert eraseUserRememberIds != null;
		
		if (userId < 0)
		{
			return false;
		}
		
		boolean result = false;
		try
		{
			if (0 != executeUpdate(eraseUserRememberIds, new DbPreparedStatementHandler() {
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
			throw new EraseUserRememberIdsErrorException(userId, e);
		}

		return result;
	}
	
	protected void _eraseAllRememberIds(Delete eraseAllRememberIds)
	throws RememberManagerException
	{
		assert eraseAllRememberIds != null;
		
		try
		{
			executeUpdate(eraseAllRememberIds);
		}
		catch (DatabaseException e)
		{
			throw new EraseAllRememberIdsErrorException(e);
		}
	}
	
	protected long _getRememberedUserId(Select getRememberedUserId, final String rememberId)
	throws RememberManagerException
	{
		assert getRememberedUserId != null;
		
		if (null == rememberId ||
			0 == rememberId.length())
		{
			return -1;
		}
		
		final int rememberid_slash = rememberId.indexOf("|");
		if (-1 == rememberid_slash)
		{
			return -1;
		}
		
		final String encrypted_userid = rememberId.substring(0, rememberid_slash);
		final String real_rememberid = rememberId.substring(rememberid_slash+1);
		
		long result = -1;

		try
		{
			result = executeGetFirstLong(getRememberedUserId, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setString("rememberId", real_rememberid);
					}
				});
		}
		catch (DatabaseException e)
		{
			throw new GetRememberedUserIdErrorException(rememberId, e);
		}

		try
		{
			if (!encrypted_userid.equals(StringEncryptor.SHA.encrypt(String.valueOf(result))))
			{
				return -1;
			}
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new GetRememberedUserIdErrorException(rememberId, e);
		}
		
		return result;
	}
	
	protected void _purgeRememberIds(Delete purgeRememberIds)
	throws RememberManagerException
	{
		try
		{
			executeUpdate(purgeRememberIds, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement.setLong(1, System.currentTimeMillis()-getRememberDuration());
					}
				});
		}
		catch (DatabaseException e)
		{
			throw new PurgeRememberIdsErrorException(e);
		}
	}
}

