/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: XmlErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.xml.exceptions;

public class XmlErrorException extends RuntimeException
{
	private static final long serialVersionUID = -5984252179915986432L;

	public XmlErrorException(String message)
	{
		super(message);
	}
	
	public XmlErrorException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public XmlErrorException(Throwable cause)
	{
		super(cause);
	}
}
