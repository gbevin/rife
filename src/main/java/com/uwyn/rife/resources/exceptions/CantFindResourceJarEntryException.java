/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CantFindResourceJarEntryException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.resources.exceptions;

public class CantFindResourceJarEntryException extends ResourceFinderErrorException
{
	private static final long serialVersionUID = 5184216632613011807L;
	
	private String	mJarFileName = null;
	private String	mEntryFileName = null;
	
	public CantFindResourceJarEntryException(String jarFileName, String entryFileName, Throwable cause)
	{
		super("The jar file '"+jarFileName+"' couldn't be found to read the '"+entryFileName+"' entry from.", cause);
		
		mJarFileName = jarFileName;
		mEntryFileName = entryFileName;
	}
	
	public String getJarFileName()
	{
		return mJarFileName;
	}
	
	public String getEntryFileName()
	{
		return mEntryFileName;
	}
}
