/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ResourceStructureRemovalException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.resources.exceptions;

public class ResourceStructureRemovalException extends ResourceWriterErrorException
{
	private static final long serialVersionUID = 8328850970373591003L;

	public ResourceStructureRemovalException(Throwable e)
	{
		super("Error while removing the resource structure.", e);
	}
}
