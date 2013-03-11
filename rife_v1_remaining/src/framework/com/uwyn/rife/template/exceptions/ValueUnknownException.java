/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ValueUnknownException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template.exceptions;

public class ValueUnknownException extends ProcessingException
{
	private static final long serialVersionUID = -236961439795474690L;
	
	private String mId = null;

	public ValueUnknownException(String id)
	{
		super("The template doesn't contain a value with id "+(null == id ? "null" : "'"+id+"'")+".");
		mId = id;
	}

	public String getId()
	{
		return mId;
	}
}
