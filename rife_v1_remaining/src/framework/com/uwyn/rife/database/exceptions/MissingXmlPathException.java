/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MissingXmlPathException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

public class MissingXmlPathException extends DatasourcesException
{
	private static final long serialVersionUID = -8515965533448911965L;

	public MissingXmlPathException()
	{
		super("The xml path is missing.");
	}
}
