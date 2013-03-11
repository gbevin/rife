/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RemoveContentErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentmanagers.exceptions;

import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.database.exceptions.DatabaseException;

public class RemoveContentErrorException extends ContentManagerException
{
	private static final long serialVersionUID = -2762295250254019078L;

	public RemoveContentErrorException(DatabaseException cause)
	{
		super("Can't remove the content database structure.", cause);
	}
}
