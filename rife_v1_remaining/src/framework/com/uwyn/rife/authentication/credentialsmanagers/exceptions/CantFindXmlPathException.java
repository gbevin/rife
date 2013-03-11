/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CantFindXmlPathException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;

public class CantFindXmlPathException extends CredentialsManagerException
{
	private static final long serialVersionUID = -3728335418850049034L;
	
	private String	mXmlPath = null;
	
	public CantFindXmlPathException(String xmlPath)
	{
		super("The xml path '"+xmlPath+"' can't be found.");
		
		mXmlPath = xmlPath;
	}
	
	public String getXmlPath()
	{
		return mXmlPath;
	}
}
