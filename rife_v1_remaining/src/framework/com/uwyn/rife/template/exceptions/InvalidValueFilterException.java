/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InvalidValueFilterException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template.exceptions;

public class InvalidValueFilterException extends TemplateException
{
	private static final long serialVersionUID = 4060674518383871785L;
	
	private String mValueFilter = null;

	public InvalidValueFilterException(String valueFilter)
	{
		super("The value filter "+valueFilter+" is an invalid regular expression.");
		mValueFilter = valueFilter;
	}

	public String getValueFilter()
	{
		return mValueFilter;
	}
}
