/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: StartAuthSession.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.elements.testelements.authenticationutils;

import com.uwyn.rife.authentication.AuthenticationUtils;
import com.uwyn.rife.authentication.exceptions.SessionManagerException;
import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.exceptions.EngineException;

public class StartAuthSession extends Element
{
	public void processElement()
	{
		try
		{
			print(AuthenticationUtils.startAuthenticationSession(getSite(), ".INPUT.MEMORY_AUTHENTICATED_BASIC", null, 1, getRemoteAddr(), false));
		}
		catch (SessionManagerException e)
		{
			throw new EngineException(e);
		}
	}
}

