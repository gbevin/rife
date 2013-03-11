/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SiteElementAlreadyPresentException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model.exceptions;

public class SiteElementAlreadyPresentException extends SiteException
{
	private static final long serialVersionUID = -4700659981436940708L;

	public SiteElementAlreadyPresentException(Throwable cause)
	{
		super(cause);
	}
}
