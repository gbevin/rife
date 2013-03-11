/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SubsiteIdInvalidException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class SubsiteIdInvalidException extends EngineException
{
	private static final long serialVersionUID = -86542568632724895L;
	
	private String	mId = null;

	public SubsiteIdInvalidException(String id)
	{
		super("The sub-site id '"+id+"' is not valid. It can't contain the '.' or '^' character, or be empty.");
		
		mId = id;
	}
	
	public String getId()
	{
		return mId;
	}
}
