/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AbstractLogout.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.elements;

import com.uwyn.rife.authentication.RememberManager;
import com.uwyn.rife.authentication.SessionManager;
import com.uwyn.rife.authentication.elements.exceptions.UndefinedLogoutRememberManagerException;
import com.uwyn.rife.authentication.exceptions.RememberManagerException;
import com.uwyn.rife.authentication.exceptions.SessionManagerException;
import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.engine.exceptions.PropertyRequiredException;
import javax.servlet.http.Cookie;

public abstract class AbstractLogout extends Element
{
	protected SessionManager	mSessionManager = null;
	protected RememberManager	mRememberManager = null;
	
	protected AbstractLogout()
	{
	}
	
	protected void setSessionManager(SessionManager sessionValidator)
	{
		assert sessionValidator != null;

		mSessionManager = sessionValidator;
	}
	
	public SessionManager getSessionManager()
	{
		return mSessionManager;
	}
	
	public void setRememberManager(RememberManager rememberManager)
	{
		mRememberManager = rememberManager;
	}
	
	public RememberManager getRememberManager()
	{
		return mRememberManager;
	}
	
	protected void performLogout()
	{
		if (!hasProperty("authvar_name"))
		{
			throw new PropertyRequiredException(getDeclarationName(), "authvar_name");
		}
		if (!hasProperty("remembervar_name"))
		{
			throw new PropertyRequiredException(getDeclarationName(), "remembervar_name");
		}

		String authvar_name = getPropertyString("authvar_name");
		boolean has_authvar_input = getElementInfo().containsInputPossibility(authvar_name);
		boolean has_authvar_cookie = getElementInfo().containsIncookiePossibility(authvar_name);
		
		String authid = null;
		if (has_authvar_cookie)
		{
			Cookie cookie = getCookie(authvar_name);
			if (cookie != null)
			{
				authid = cookie.getValue();
			}
		}
		if (has_authvar_input && (null == authid || 0 == authid.length()))
		{
			authid = getInput(authvar_name);
		}
			
		if (authid != null)
		{
			try
			{
				mSessionManager.eraseSession(authid);
			}
			catch (SessionManagerException e)
			{
				throw new EngineException(e);
			}
			
			// clear remember id cookie for the user
			String remembervar_name = getPropertyString("remembervar_name");
			if (getElementInfo().containsIncookiePossibility(remembervar_name) &&
				hasCookie(remembervar_name))
			{
				if (null == mRememberManager)
				{
					throw new UndefinedLogoutRememberManagerException();
				}
				
				try
				{
					mRememberManager.eraseRememberId(getCookieValue(remembervar_name));
				}
				catch (RememberManagerException e)
				{
					throw new EngineException(e);
				}
				
				Cookie cookie = getCookie(remembervar_name);
				cookie.setMaxAge(-1);
				cookie.setPath("/");
				cookie.setValue("");
				setCookie(cookie);
			}
			
			// clear child trigger value
			if (has_authvar_cookie &&
				hasCookie(authvar_name))
			{
				Cookie cookie = getCookie(authvar_name);
				cookie.setMaxAge(-1);
				cookie.setPath("/");
				cookie.setValue("");
				setCookie(cookie);
			}
			if (has_authvar_input)
			{
				clearOutput(authvar_name);
			}
		}
		
		removeRequestAttribute(Identified.IDENTITY_ATTRIBUTE_NAME);
	}
}
