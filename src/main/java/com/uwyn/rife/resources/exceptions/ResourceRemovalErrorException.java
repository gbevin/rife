/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ResourceRemovalErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.resources.exceptions;

public class ResourceRemovalErrorException extends ResourceWriterErrorException
{
	private static final long serialVersionUID = -2804786350448231032L;
	
	private String	mName = null;
	
	public ResourceRemovalErrorException(String name, Throwable e)
	{
		super("Error while removing the resource with the name '"+name+"'.", e);
		
		mName = name;
	}
	
	public String getName()
	{
		return mName;
	}
}
