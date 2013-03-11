/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Identified.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.elements;

import com.uwyn.rife.authentication.SessionManager;
import com.uwyn.rife.authentication.SessionValidator;
import com.uwyn.rife.authentication.credentialsmanagers.IdentifiableUsersManager;
import com.uwyn.rife.authentication.credentialsmanagers.RoleUserAttributes;
import com.uwyn.rife.authentication.credentialsmanagers.RoleUserIdentity;
import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.authentication.exceptions.SessionManagerException;
import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.ElementInfo;
import com.uwyn.rife.engine.annotations.Elem;
import com.uwyn.rife.engine.exceptions.EngineException;

@Elem
public class Identified extends Element
{
	public final static String	IDENTITY_ATTRIBUTE_NAME = RoleUserIdentity.class.getName();

	public String getAuthElementId()
	{
		return getPropertyString("authElementId");
	}

	public ElementInfo getAuthElement()
	{
		return getSite().resolveId(getAuthElementId(), getElementInfo());
	}

	public void processElement()
	{
		child();
	}

	public void setIdentityAttribute(String name, String[] values)
	{
		RoleUserIdentity identity = getIdentity(name, values);
		if (identity != null)
		{
			setRequestAttribute(IDENTITY_ATTRIBUTE_NAME, identity);
		}
	}

	public RoleUserIdentity getIdentity(String name, String[] values)
	{
		if (null == values ||
			0 == values.length)
		{
			return null;
		}

		String value = values[0];

		String				login = null;
		RoleUserAttributes	attributes = null;
		try
		{
			ElementInfo authenticated = getAuthElement();
			if (authenticated != null &&
				authenticated.getDeployer() != null &&
				authenticated.containsProperty("authvar_name") &&
				authenticated.containsProperty("authvar_type") &&
				authenticated.getDeployer() instanceof AuthenticatedDeployer)
			{
				AuthenticatedDeployer	deployer = (AuthenticatedDeployer)authenticated.getDeployer();
				SessionValidator		validator = deployer.getSessionValidator();
				if (validator.getCredentialsManager() instanceof IdentifiableUsersManager)
				{
					String authentication_request_attribute = Authenticated.createAuthenticationRequestAttributeName(getElementInfo(), name, values[0]);
					
					if (hasRequestAttribute(authentication_request_attribute) ||
						validator.getSessionManager().isSessionValid(value, getRemoteAddr()))
					{
						IdentifiableUsersManager	credentials = ((IdentifiableUsersManager)validator.getCredentialsManager());
						SessionManager				sessions = validator.getSessionManager();
						long						userid = -1;
						String						authvar_name = authenticated.getPropertyString("authvar_name");
						if (authvar_name.equals(name))
						{
							userid = sessions.getSessionUserId(value);

							if (userid > -1)
							{
								login = credentials.getLogin(userid);
								if (!hasRequestAttribute(authentication_request_attribute))
								{
									sessions.continueSession(value);
								}
							}

							if (login != null)
							{
								attributes = credentials.getAttributes(login);
							}
						}
					}
				}
			}
		}
		catch (SessionManagerException e)
		{
			throw new EngineException(e);
		}
		catch (CredentialsManagerException e)
		{
			throw new EngineException(e);
		}

		if (login != null &&
			attributes != null)
		{
			return new RoleUserIdentity(login, attributes);
		}

		return null;
	}

	public boolean childTriggered(String name, String[] values)
	{
		if (!hasRequestAttribute(IDENTITY_ATTRIBUTE_NAME))
		{
			setIdentityAttribute(name, values);
		}

		return true;
	}
}
