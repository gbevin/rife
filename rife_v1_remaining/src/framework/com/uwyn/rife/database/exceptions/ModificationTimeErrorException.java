/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ModificationTimeErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

import com.uwyn.rife.resources.exceptions.ResourceFinderErrorException;

public class ModificationTimeErrorException extends DatasourcesException
{
	private static final long serialVersionUID = 6275761113282946034L;

	private String	mXmlPath = null;

	public ModificationTimeErrorException(String xmlPath, ResourceFinderErrorException cause)
	{
		super("Error while retrieving the modification time of '"+xmlPath+"'", cause);
		
		mXmlPath = xmlPath;
	}
	
	public String getXmlPath()
	{
		return mXmlPath;
	}
}
