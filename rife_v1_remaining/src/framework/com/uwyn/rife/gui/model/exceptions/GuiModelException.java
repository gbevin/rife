/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GuiModelException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model.exceptions;

public class GuiModelException extends Exception
{
	private static final long serialVersionUID = -807626143506096330L;

	public GuiModelException(String message)
	{
		super(message);
	}

	public GuiModelException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public GuiModelException(Throwable cause)
	{
		super(cause);
	}
}
