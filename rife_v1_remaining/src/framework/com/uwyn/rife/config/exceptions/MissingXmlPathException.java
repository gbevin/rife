/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MissingXmlPathException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.config.exceptions;

public class MissingXmlPathException extends ConfigErrorException
{
	private static final long serialVersionUID = 3832381866285443663L;

	public MissingXmlPathException()
	{
		super("The xml path is missing.");
	}
}
