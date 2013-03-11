/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GetRememberedUserIdErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.remembermanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.RememberManagerException;

public class GetRememberedUserIdErrorException extends RememberManagerException
{
	private static final long serialVersionUID = -8795043125113898699L;

	private String	mRememberId = null;

	public GetRememberedUserIdErrorException(String rememberId)
	{
		this(rememberId, null);
	}

	public GetRememberedUserIdErrorException(String rememberId, Throwable cause)
	{
		super("Unable to get the user id for remember id '"+rememberId+"'.", cause);
		mRememberId = rememberId;
	}

	public String getRememberId()
	{
		return mRememberId;
	}
}
