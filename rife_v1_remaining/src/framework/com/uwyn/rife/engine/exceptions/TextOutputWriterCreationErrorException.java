/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TextOutputWriterCreationErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class TextOutputWriterCreationErrorException extends EngineException
{
	private static final long serialVersionUID = 2801872175281235891L;

	public TextOutputWriterCreationErrorException(Throwable cause)
	{
		super("The text output writer couldn't be obtained.", cause);
	}
}
