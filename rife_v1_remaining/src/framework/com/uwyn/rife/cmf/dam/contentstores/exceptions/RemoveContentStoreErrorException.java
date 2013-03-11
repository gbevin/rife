/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RemoveContentStoreErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentstores.exceptions;

import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.database.exceptions.DatabaseException;

public class RemoveContentStoreErrorException extends ContentManagerException
{
	private static final long serialVersionUID = 5377418013372733953L;

	public RemoveContentStoreErrorException(DatabaseException cause)
	{
		super("Can't remove the content store database structure.", cause);
	}
}
