/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SiteException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model.exceptions;

public class SiteException extends GuiModelException
{
	private static final long serialVersionUID = -237778385780146434L;

	public SiteException(String message)
	{
		super(message);
	}

	public SiteException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public SiteException(Throwable cause)
	{
		super(cause);
	}
}
