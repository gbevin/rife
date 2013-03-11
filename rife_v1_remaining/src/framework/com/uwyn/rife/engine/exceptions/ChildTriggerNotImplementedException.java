/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ChildTriggerNotImplementedException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ChildTriggerNotImplementedException extends EngineException
{
	private static final long serialVersionUID = 4962432690529413246L;

	private String	mImplementation = null;
	private String	mChildTriggerName = null;

	public ChildTriggerNotImplementedException(String implementation, String childTriggerName)
	{
		super("The child trigger '"+childTriggerName+"' of element with implementation '"+implementation+"' has been triggered, but it's not implemented.");
		
		mImplementation = implementation;
		mChildTriggerName = childTriggerName;
	}
	
	public String getImplementation()
	{
		return mImplementation;
	}
	
	public String getChildTriggerName()
	{
		return mChildTriggerName;
	}
}
