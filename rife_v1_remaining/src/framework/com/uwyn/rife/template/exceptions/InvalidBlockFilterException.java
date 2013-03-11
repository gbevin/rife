/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InvalidBlockFilterException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template.exceptions;

public class InvalidBlockFilterException extends TemplateException
{
	private static final long serialVersionUID = -4317974249211817030L;
	
	private String mBlockFilter = null;

	public InvalidBlockFilterException(String blockFilter)
	{
		super("The block filter "+blockFilter+" is an invalid regular expression.");
		mBlockFilter = blockFilter;
	}

	public String getBlockFilter()
	{
		return mBlockFilter;
	}
}
