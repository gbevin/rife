/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FrequencyException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.exceptions;

public class FrequencyException extends SchedulerException
{
	private static final long serialVersionUID = -3013172833353569656L;

	public FrequencyException(String message)
	{
		super(message);
	}

	public FrequencyException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public FrequencyException(Throwable cause)
	{
		super(cause);
	}
}
