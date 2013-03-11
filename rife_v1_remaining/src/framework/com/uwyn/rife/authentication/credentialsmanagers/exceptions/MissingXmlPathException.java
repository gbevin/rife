/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MissingXmlPathException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;

public class MissingXmlPathException extends CredentialsManagerException
{
	private static final long serialVersionUID = -8531463268528849726L;

	public MissingXmlPathException()
	{
		super("The xml path is missing.");
	}
}
