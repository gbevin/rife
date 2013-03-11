/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MultipartFileErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

import java.io.IOException;

public class MultipartFileErrorException extends MultipartRequestException
{
	private static final long serialVersionUID = 7862529899155331130L;

	private String	mFileName = null;

	public MultipartFileErrorException(String fileName, IOException e)
	{
		super("Unexpected error while saving the contents of file '"+fileName+"'.", e);
		
		mFileName = fileName;
	}
	
	public String getFileName()
	{
		return mFileName;
	}
}
