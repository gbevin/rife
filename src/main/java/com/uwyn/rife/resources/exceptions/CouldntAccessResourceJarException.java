/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CouldntAccessResourceJarException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.resources.exceptions;

public class CouldntAccessResourceJarException extends ResourceFinderErrorException
{
	private static final long serialVersionUID = -7524303430166484247L;
	
	private String	mJarFileName = null;
	private String	mEntryFileName = null;
	
	public CouldntAccessResourceJarException(String jarFileName, String entryFileName)
	{
		super("The jar file '"+jarFileName+"' couldn't be found to read the '"+entryFileName+"' entry from.");
		
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
