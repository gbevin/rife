/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Erroneous.java 3930 2008-04-24 11:10:22Z gbevin $
 */
package com.uwyn.rife.engine.testelements.engine;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.annotations.Elem;
import com.uwyn.rife.engine.annotations.InputProperty;
import com.uwyn.rife.engine.exceptions.EngineException;

@Elem
public class Erroneous extends Element
{
	private String mExceptionType;

	@InputProperty
	public void setExceptionType(String type)
	{
		mExceptionType = type;
	}

	public void processElement()
	{
		if (mExceptionType != null &&
			"EngineException".equals(mExceptionType))
		{
			throw new EngineException("This is an engine exception.");
		}
		else if (mExceptionType != null &&
			"nested EngineException".equals(mExceptionType))
		{
			throw new RuntimeException("This is a runtime exception with a nested engine exception.", new EngineException("This is an engine exception."));
		}
		else
		{
			throw new RuntimeException("This is a runtime exception.");
		}
	}
}