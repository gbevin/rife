/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementIdNotFoundException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ElementIdNotFoundException extends EngineException
{
	private static final long serialVersionUID = 6454135080615137673L;

	private String	mId = null;

	public ElementIdNotFoundException(String id)
	{
		super("The element id '"+id+"' couldn't be found.");
		
		mId = id;
	}
	
	public String getId()
	{
		return mId;
	}
}
