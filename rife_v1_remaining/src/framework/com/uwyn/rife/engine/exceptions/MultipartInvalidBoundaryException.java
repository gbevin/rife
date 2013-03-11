/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MultipartInvalidBoundaryException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class MultipartInvalidBoundaryException extends MultipartRequestException
{
	private static final long serialVersionUID = -4310903799969602620L;

	private String	mBoundary = null;
	private String	mLine = null;

	public MultipartInvalidBoundaryException(String boundary, String line)
	{
		super("The boundary '"+boundary+"' wasn't found at the beginning of line '"+line+"'.");
		
		mBoundary = boundary;
		mLine = line;
	}
	
	public String getBoundary()
	{
		return mBoundary;
	}
	
	public String getLine()
	{
		return mLine;
	}
}
