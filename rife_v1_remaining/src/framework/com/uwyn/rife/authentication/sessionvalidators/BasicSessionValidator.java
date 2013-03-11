/*
 * Copyright 2001-2008 Steven Grimm <koreth[remove] at midwinter dot com> and
 * Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id$
 */
package com.uwyn.rife.authentication.sessionvalidators;

import com.uwyn.rife.authentication.SessionAttributes;
import com.uwyn.rife.authentication.SessionManager;
import com.uwyn.rife.authentication.credentialsmanagers.RoleUsersManager;
import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.authentication.exceptions.SessionManagerException;
import com.uwyn.rife.authentication.exceptions.SessionValidatorException;
import com.uwyn.rife.authentication.sessionvalidators.exceptions.RoleCheckErrorException;
import com.uwyn.rife.authentication.sessionvalidators.exceptions.SessionValidityCheckErrorException;

/**
 * Non-optimized session validator. This is a naive implementation of the
 * {@link com.uwyn.rife.authentication.SessionValidator} interface, suitable for cases where there is no
 * need for optimization of session validity checking. For example, it is
 * used as the session validator for RIFE's built-in "mixed" and "memory"
 * authentication elements.
 * 
 * @author Steven Grimm (koreth[remove] at midwinter dot com)
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: $
 * @see com.uwyn.rife.authentication.SessionValidator
 * @since 1.6
 */
public class BasicSessionValidator extends AbstractSessionValidator
{
	public boolean isAccessAuthorized(int id)
	{
		return SESSION_VALID == id;
	}

	public int validateSession(String authId, String hostIp, SessionAttributes attributes)
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

		SessionManager	sessions = getSessionManager();
		
		try
		{
			if (!sessions.isSessionValid(authId, hostIp))
			{
				return SESSION_INVALID;
			}
		}
		catch (SessionManagerException e)
		{
			throw new SessionValidityCheckErrorException(authId, hostIp, e);
		}

		if (attributes.hasAttribute("role"))
		{
			long	user_id = -1;
			String	role = attributes.getAttribute("role");
			
			try
			{
				user_id = sessions.getSessionUserId(authId);
			}
			catch (SessionManagerException e)
			{
				user_id = -1;
			}
			
			if (-1 == user_id)
			{
				return SESSION_INVALID;
			}
			
			try
			{
				if (mCredentialsManager instanceof RoleUsersManager &&
				    !((RoleUsersManager) mCredentialsManager).isUserInRole(user_id, attributes.getAttribute("role")))
				{
					return SESSION_INVALID;
				}
			}
			catch (CredentialsManagerException e)
			{
				throw new RoleCheckErrorException(authId, hostIp, role, e);
			}
			
			return SESSION_VALID;
		}
		
		return SESSION_VALID;
	}

}
