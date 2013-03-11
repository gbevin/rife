/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UndefinedAuthenticationRememberManagerException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.elements.exceptions;

import com.uwyn.rife.engine.exceptions.EngineException;

public class UndefinedAuthenticationRememberManagerException extends EngineException
{
	private static final long serialVersionUID = 5888560019892891803L;

	public UndefinedAuthenticationRememberManagerException()
	{
		super("The RememberManager is null, maybe this authentication type doesn't support remember Remember Me functionalities.");
	}
}
