/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ResourceModificationCheckErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

import java.net.URL;

public class ResourceModificationCheckErrorException extends EngineException
{
	private static final long serialVersionUID = -727831719090843100L;

	private URL	mResource = null;

	public ResourceModificationCheckErrorException(URL resource, Throwable cause)
	{
		super("The modification time of resource '"+resource.toString()+"' could not be checked.", cause);
		
		mResource = resource;
	}
	
	public URL getResource()
	{
		return mResource;
	}
}
