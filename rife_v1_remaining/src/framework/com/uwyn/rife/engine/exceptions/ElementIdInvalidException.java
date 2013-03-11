/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementIdInvalidException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ElementIdInvalidException extends EngineException
{
	private static final long serialVersionUID = -4039966927506500947L;

	private String	mId = null;

	public ElementIdInvalidException(String id)
	{
		super("The element id '"+id+"' is not valid. It can't contain '..', '^', ':', be empty or have '.' at the start or the end.");

		mId = id;
	}

	public String getId()
	{
		return mId;
	}
}
