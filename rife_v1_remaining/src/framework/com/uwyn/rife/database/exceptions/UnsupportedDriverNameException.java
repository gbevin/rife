/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UnsupportedDriverNameException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

public class UnsupportedDriverNameException extends DatabaseException
{
	static final long serialVersionUID = 6993103229317879655L;

	private String mName = null;

	public UnsupportedDriverNameException(String name)
	{
		super("Couldn't find a supported driver class for the driver name '"+name+"'.");
		mName = name;
	}

	public String getName()
	{
		return mName;
	}
}
