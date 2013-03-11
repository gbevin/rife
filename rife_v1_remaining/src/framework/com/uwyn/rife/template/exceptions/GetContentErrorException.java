/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GetContentErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template.exceptions;

public class GetContentErrorException extends ProcessingException
{
	private static final long serialVersionUID = 6372141337279582707L;
	
	private String mPath = null;

	public GetContentErrorException(String path, Throwable cause)
	{
		super("Error while obtaining the content of resource '"+path+"'.", cause);
		
		mPath = path;
	}

	public String getPath()
	{
		return mPath;
	}
}
