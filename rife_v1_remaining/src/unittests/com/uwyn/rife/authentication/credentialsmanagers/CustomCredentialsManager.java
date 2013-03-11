/*
 * Copyright 2001-2008 Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id$
 */
package com.uwyn.rife.authentication.credentialsmanagers;

import com.uwyn.rife.authentication.Credentials;
import com.uwyn.rife.authentication.CredentialsManager;

/**
 * Dummy credentials manager used to verify that we can use our own
 * credentials manager.
 */
public class CustomCredentialsManager implements CredentialsManager
{
	private String mId;
	
	public CustomCredentialsManager() { }
	public CustomCredentialsManager(String id)
	{
		mId = id;
	}
	
	public String getId()
	{
		return mId;
	}
	
	public long verifyCredentials(Credentials credentials) { return 0; }
}
