/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DuplicateElementIdException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class DuplicateElementIdException extends EngineException
{
	private static final long serialVersionUID = 2534247746133985641L;

	private String	mId = null;

	public DuplicateElementIdException(String id)
	{
		super("The element id '"+id+"' is already present in the site.");
		
		mId = id;
	}
	
	public String getId()
	{
		return mId;
	}
}
