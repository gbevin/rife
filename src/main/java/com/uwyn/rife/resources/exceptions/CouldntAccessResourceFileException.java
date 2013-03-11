/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CouldntAccessResourceFileException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.resources.exceptions;

public class CouldntAccessResourceFileException extends ResourceFinderErrorException
{
	private static final long serialVersionUID = -5803514478814762581L;
	
	private String	mFileName = null;
	
	public CouldntAccessResourceFileException(String fileName)
	{
		super("The resource file '"+fileName+"' couldn't be found.");
		
		mFileName = fileName;
	}
	
	public String getFileName()
	{
		return mFileName;
	}
}
