/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AuthenticatedDeployer.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.elements;

import com.uwyn.rife.authentication.Credentials;
import com.uwyn.rife.authentication.SessionValidator;
import com.uwyn.rife.engine.ElementDeployer;

public abstract class AuthenticatedDeployer extends ElementDeployer
{
	private Class<? extends Credentials>		mCredentialsClass = null;
	private SessionValidator					mSessionValidator = null;

	protected void setCredentialsClass(Class<? extends Credentials> credentialsClass)
	{
		assert credentialsClass != null;

		mCredentialsClass = credentialsClass;
	}

	public Class<? extends Credentials> getCredentialsClass()
	{
		return mCredentialsClass;
	}

	protected void setSessionValidator(SessionValidator sessionValidator)
	{
		assert sessionValidator != null;

		mSessionValidator = sessionValidator;
	}

	public SessionValidator getSessionValidator()
	{
		return mSessionValidator;
	}
}


