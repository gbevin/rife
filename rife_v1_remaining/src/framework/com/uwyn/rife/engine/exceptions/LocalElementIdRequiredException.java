/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: LocalElementIdRequiredException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class LocalElementIdRequiredException extends EngineException
{
	private static final long serialVersionUID = -6601137551446928023L;

	private String	mId = null;

	public LocalElementIdRequiredException(String id)
	{
		super("Only local element ids are supported in this definition, '"+id+"' isn't.");
		
		mId = id;
	}
	
	public String getClassname()
	{
		return mId;
	}
}
