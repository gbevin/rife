/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ResourceAdditionErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.resources.exceptions;

public class ResourceAdditionErrorException extends ResourceWriterErrorException
{
	private static final long serialVersionUID = 5975240130712585022L;
	
	private String	mName = null;
	private String	mContent = null;
	
	public ResourceAdditionErrorException(String name, String content, Throwable e)
	{
		super("Error while adding the resource with name '"+name+"' and content '"+content+"'.", e);
		
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
