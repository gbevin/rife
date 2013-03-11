/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DatabaseSessionValidator.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.sessionvalidators;

import com.uwyn.rife.authentication.CredentialsManager;
import com.uwyn.rife.authentication.RememberManager;
import com.uwyn.rife.authentication.SessionAttributes;
import com.uwyn.rife.authentication.SessionManager;
import com.uwyn.rife.authentication.SessionValidator;
import com.uwyn.rife.authentication.exceptions.SessionValidatorException;
import com.uwyn.rife.authentication.sessionvalidators.exceptions.SessionValidityCheckErrorException;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.DbPreparedStatementHandler;
import com.uwyn.rife.database.DbQueryManager;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.Select;

public abstract class DatabaseSessionValidator extends DbQueryManager implements SessionValidator
{
	public static final int		SESSION_INVALID = 0;
	public static final int		SESSION_VALID = 1;

	protected CredentialsManager	mCredentialsManager = null;
	protected SessionManager		mSessionManager = null;
	protected RememberManager		mRememberManager = null;
	
    protected DatabaseSessionValidator(Datasource datasource)
    {
        super(datasource);
    }
	
	public void setCredentialsManager(CredentialsManager credentialsManager)
	{
		assert credentialsManager != null;

		mCredentialsManager = credentialsManager;
	}
	
	public CredentialsManager getCredentialsManager()
	{
		return mCredentialsManager;
	}
	
	public void setSessionManager(SessionManager sessionManager)
	{
		assert sessionManager != null;

		mSessionManager = sessionManager;
	}
	
	public void setRememberManager(RememberManager rememberManager)
	{
		assert rememberManager != null;

		mRememberManager = rememberManager;
	}
	
	public RememberManager getRememberManager()
	{
		return mRememberManager;
	}
	
	public SessionManager getSessionManager()
	{
		return mSessionManager;
	}
	
	public boolean isAccessAuthorized(int id)
	{
		return SESSION_VALID == id;
	}
	
	protected int _validateSession(Select sessionValidityNoRole, Select sessionValidityNoRoleRestrictHostIp, Select sessionValidityRole, Select sessionValidityRoleRestrictHostIp, ProcessSessionValidity processSessionValidity, final String authId, final String hostIp, final SessionAttributes attributes)
	throws SessionValidatorException
	{
		if (null == authId ||
			0 == authId.length() ||
			null == hostIp ||
			0 == hostIp.length() ||
			null == attributes)
		{
			return SESSION_INVALID;
		}

		int	result = SESSION_INVALID;
		
		Select query = null;
		
		// select which query to use according to the role attribute
		if (attributes.hasAttribute("role"))
		{
			if (mSessionManager.getRestrictHostIp())
			{
				query = sessionValidityRoleRestrictHostIp;
			}
			else
			{
				query = sessionValidityRole;
			}
		}
		else
		{
			if (mSessionManager.getRestrictHostIp())
			{
				query = sessionValidityNoRoleRestrictHostIp;
			}
			else
			{
				query = sessionValidityNoRole;
			}
		}
		
		// role has been specified, use optimized validity check to limit the amount of db queries
		try
		{
			executeFetchFirst(query, processSessionValidity, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setString("authId", authId)
							.setLong("sessStart", System.currentTimeMillis()-mSessionManager.getSessionDuration());
							
						if (attributes.hasAttribute("role"))
						{
							statement
								.setString("role", attributes.getAttribute("role"));
						}
						if (mSessionManager.getRestrictHostIp())
						{
							statement
							.setString("hostIp", hostIp);
						}
					}
				});
			result = processSessionValidity.getValidity();
		}
		catch (DatabaseException e)
		{
			throw new SessionValidityCheckErrorException(authId, hostIp, e);
		}

		return result;
	}
}



