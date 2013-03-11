/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementMemberFieldUncloneableException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ElementMemberFieldUncloneableException extends CloneNotSupportedException
{
	private static final long serialVersionUID = -5459045970180439274L;

	private String	mImplementation = null;
	private String	mField = null;

	public ElementMemberFieldUncloneableException(String implementation, String field, Throwable cause)
	{
		super("The implementation '"+implementation+"' of element has the member field '"+field+"' which can't be cloned.");
		if (cause != null)
		{
			initCause(cause);
		}
		
		mImplementation = implementation;
		mField = field;
	}
	
	public String getImplementation()
	{
		return mImplementation;
	}
	
	public String getField()
	{
		return mField;
	}
}
