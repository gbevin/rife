/*
 * Copyright 2001-2008 Geert Bevin <gbevin@uwyn.com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Uncached.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.elements;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.annotations.Elem;
import com.uwyn.rife.engine.exceptions.EngineException;

@Elem
public class Uncached extends Element
{
	public void processElement()
	throws EngineException
	{
		preventCaching();
	}
}

