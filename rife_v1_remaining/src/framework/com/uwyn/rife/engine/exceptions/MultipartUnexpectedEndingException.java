/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MultipartUnexpectedEndingException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class MultipartUnexpectedEndingException extends MultipartRequestException
{
	private static final long serialVersionUID = -9216259920842606514L;

	public MultipartUnexpectedEndingException()
	{
		super("Premature ending of the form data.");
	}
}
