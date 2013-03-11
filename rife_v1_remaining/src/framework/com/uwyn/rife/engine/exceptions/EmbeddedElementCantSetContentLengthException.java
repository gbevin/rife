/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: EmbeddedElementCantSetContentLengthException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class EmbeddedElementCantSetContentLengthException extends EngineException
{
	private static final long serialVersionUID = 5352023656476132948L;

	public EmbeddedElementCantSetContentLengthException()
	{
		super("An embedded element is not allowed to set the content length.");
	}
}
