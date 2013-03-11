/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MailQueueManagerException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.mail.exceptions;

public class MailQueueManagerException extends RuntimeException
{
	private static final long serialVersionUID = 5820465598058245534L;

	public MailQueueManagerException(String message)
	{
		super(message);
	}

	public MailQueueManagerException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public MailQueueManagerException(Throwable cause)
	{
		super(cause);
	}
}
