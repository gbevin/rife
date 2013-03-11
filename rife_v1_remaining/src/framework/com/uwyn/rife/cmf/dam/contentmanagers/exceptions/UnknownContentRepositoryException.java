/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UnknownContentRepositoryException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentmanagers.exceptions;

import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;

public class UnknownContentRepositoryException extends ContentManagerException
{
	private static final long serialVersionUID = -7745441411433107874L;

	private String	mRepositoryName = null;
	
	public UnknownContentRepositoryException(String repositoryName)
	{
		super("The repository '"+repositoryName+"' doesn't exist.");
		
		mRepositoryName = repositoryName;
	}
	
	public String getRepositoryName()
	{
		return mRepositoryName;
	}
}
