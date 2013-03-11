/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CircularContructionException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template.exceptions;

public class CircularContructionException extends ProcessingException
{
	private static final long serialVersionUID = 5707444018880036132L;

	public CircularContructionException()
	{
		super("The value constructions reference themselves in a circular way. This is not allowed since it would cause a stack overflow.");
	}
}
