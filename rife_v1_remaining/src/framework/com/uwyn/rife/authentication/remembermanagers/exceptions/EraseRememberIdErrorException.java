/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: EraseRememberIdErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.remembermanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.RememberManagerException;
import com.uwyn.rife.database.exceptions.DatabaseException;

public class EraseRememberIdErrorException extends RememberManagerException
{
	private static final long serialVersionUID = 300560529136885181L;

	private String	mRememberId = null;

	public EraseRememberIdErrorException(String rememberId)
	{
		this(rememberId, null);
	}
	
	public EraseRememberIdErrorException(String rememberId, DatabaseException cause)
	{
		super("Unable to erase the remember id '"+rememberId+"'.", cause);
		mRememberId = rememberId;
	}

	public String getRememberId()
	{
		return mRememberId;
	}
}
