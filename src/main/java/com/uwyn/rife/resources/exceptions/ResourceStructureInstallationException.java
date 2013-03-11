/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ResourceStructureInstallationException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.resources.exceptions;

public class ResourceStructureInstallationException extends ResourceWriterErrorException
{
	private static final long serialVersionUID = -4700139708043242285L;

	public ResourceStructureInstallationException(Throwable e)
	{
		super("Error while installing the resource structure.", e);
	}
}
