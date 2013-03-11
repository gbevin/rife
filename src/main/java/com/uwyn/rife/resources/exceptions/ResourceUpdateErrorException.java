/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ResourceUpdateErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.resources.exceptions;

public class ResourceUpdateErrorException extends ResourceWriterErrorException
{
	private static final long serialVersionUID = -4098480729483331152L;
	
	private String	mName = null;
	private String	mContent = null;
	
	public ResourceUpdateErrorException(String name, String content, Throwable e)
	{
		super("Error while updating the resource with the name '"+name+"' to content '"+content+"'.", e);
		
		mName = name;
		mContent = content;
	}
	
	public String getName()
	{
		return mName;
	}
	
	public String getContent()
	{
		return mContent;
	}
}
