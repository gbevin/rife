/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CircularSubsitesException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class CircularSubsitesException extends EngineException
{
	private static final long serialVersionUID = 6007309009924917435L;

	private String	mFile = null;

	public CircularSubsitesException(String file)
	{
		super("The sub-site with file '"+file+"' is already present is the hierarchy.");
		
		mFile = file;
	}
	
	public String getFile()
	{
		return mFile;
	}
}
