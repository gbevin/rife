/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: NotRoleUsersManagedException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.exceptions;

import com.uwyn.rife.engine.exceptions.EngineException;

public class NotRoleUsersManagedException extends EngineException
{
	private static final long serialVersionUID = -4637783446076743042L;

	private String	mElementId = null;
	
	public NotRoleUsersManagedException(String elementId)
	{
		super("The credentials of the element '"+elementId+"' are not managed by a RoleUsersManager");
		
		mElementId = elementId;
	}
	
	public String getElementId()
	{
		return mElementId;
	}
}

