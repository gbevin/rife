/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementContextMissingException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ElementContextMissingException extends EngineException
{
	private static final long serialVersionUID = -7810994956872529521L;

	public ElementContextMissingException()
	{
		super("This element executed a method that requires a fully initialized element context. You are probably initializing member variables at construction while they should be initialized in the initialize() method.");
	}
}
