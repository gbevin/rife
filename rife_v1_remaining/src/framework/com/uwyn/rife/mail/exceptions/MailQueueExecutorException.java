/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MailQueueExecutorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.mail.exceptions;

public class MailQueueExecutorException extends RuntimeException
{
	private static final long serialVersionUID = -6122809637245784149L;

	public MailQueueExecutorException(String message)
	{
		super(message);
	}

	public MailQueueExecutorException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public MailQueueExecutorException(Throwable cause)
	{
		super(cause);
	}
}
