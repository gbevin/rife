/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParticlePropertyException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model.exceptions;

public class ParticlePropertyException extends GuiModelException
{
	private static final long serialVersionUID = -5438013259311818279L;

	public ParticlePropertyException(String message)
	{
		super(message);
	}

	public ParticlePropertyException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ParticlePropertyException(Throwable cause)
	{
		super(cause);
	}
}
