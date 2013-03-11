/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: StoreContentDataErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentstores.exceptions;

import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;

public class StoreContentDataErrorException extends ContentManagerException
{
	private static final long serialVersionUID = 2037221789702552879L;

	private int	mId = -1;
	
	public StoreContentDataErrorException(int id, Throwable cause)
	{
		super("Unexpected error while storing the content with the id '"+id+"'.", cause);
		
		mId = id;
	}
	
	public int getId()
	{
		return mId;
	}
}
