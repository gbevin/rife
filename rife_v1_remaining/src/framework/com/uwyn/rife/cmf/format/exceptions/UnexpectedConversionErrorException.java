/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UnexpectedConversionErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.format.exceptions;

public class UnexpectedConversionErrorException extends FormatException
{
	private static final long serialVersionUID = 4479156837469537886L;

	public UnexpectedConversionErrorException(Throwable cause)
	{
		super("An unexpected conversion error occurred.", cause);
	}
}
