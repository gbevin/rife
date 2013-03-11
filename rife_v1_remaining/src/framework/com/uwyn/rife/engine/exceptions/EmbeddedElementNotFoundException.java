/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: EmbeddedElementNotFoundException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class EmbeddedElementNotFoundException extends EngineException
{
	private static final long serialVersionUID = -5773527910542954514L;

	private String	mId = null;

	public EmbeddedElementNotFoundException(String id)
	{
		super("The embedded element with id '"+id+"' couldn't be found in the template.");
		
		mId = id;
	}
	
	public String getId()
	{
		return mId;
	}
}
