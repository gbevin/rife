/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InitializationErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;

public class InitializationErrorException extends CredentialsManagerException
{
	private static final long serialVersionUID = 5656697814756806921L;

	private String	mXmlPath = null;
	
	public InitializationErrorException(String xmlPath, Throwable cause)
	{
		super("Error while initializing in-memory users from xml document '"+xmlPath+"'.", cause);
		
		mXmlPath = xmlPath;
	}
	
	public String getXmlPath()
	{
		return mXmlPath;
	}
}
