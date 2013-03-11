/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementDetector.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.instrument;

import com.uwyn.rife.engine.EngineClassLoader;
import com.uwyn.rife.instrument.ClassInterfaceDetector;

public class ElementDetector extends ClassInterfaceDetector
{
	private static final String	ELEMENT_NAME = "com.uwyn.rife.engine.Element";
	private static final String	ELEMENTAWARE_INTERNAL_NAME = "com/uwyn/rife/engine/ElementAware";
	private static final String	ELEMENTAWARE_NAME = "com.uwyn.rife.engine.ElementAware";
	
	public ElementDetector(EngineClassLoader classLoader)
	{
		super(classLoader, ELEMENTAWARE_INTERNAL_NAME);
	}
	
	public boolean detect(String classname, byte[] bytes, boolean doAutoReload)
	throws ClassNotFoundException
	{
		if (ELEMENT_NAME == classname ||
			ELEMENTAWARE_NAME == classname)
		{
			return false;
		}
		
		return super.detect(bytes, doAutoReload);
	}
}
