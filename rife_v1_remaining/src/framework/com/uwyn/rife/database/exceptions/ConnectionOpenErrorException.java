/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ConnectionOpenErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

public class ConnectionOpenErrorException extends DatabaseException
{
	private static final long serialVersionUID = -8963881858111262119L;

	private String mUrl = null;
	private String mUser = null;
	private String mPassword = null;

	public ConnectionOpenErrorException(String url, Throwable cause)
	{
		super("Couldn't connect to the database with connection url '"+url+"'.", cause);
		mUrl = url;
	}

	public ConnectionOpenErrorException(String url, String user, String password, Throwable cause)
	{
		super("Couldn't connect to the database with connection url '"+url+"'.", cause);
		mUrl = url;
		mUser = user;
		mPassword = password;
	}

	public String getUrl()
	{
		return mUrl;
	}

	public String getUser()
	{
		return mUser;
	}

	public String getPassword()
	{
		return mPassword;
	}
}
