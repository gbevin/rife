/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MultipartInputErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

import java.io.IOException;

public class MultipartInputErrorException extends MultipartRequestException
{
	private static final long serialVersionUID = 4019071444351340575L;

	public MultipartInputErrorException(IOException e)
	{
		super("Unexpected error during the input of the multipart request content.", e);
	}
}
