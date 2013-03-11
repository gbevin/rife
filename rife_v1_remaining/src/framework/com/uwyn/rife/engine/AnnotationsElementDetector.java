/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AnnotationsElementDetector.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.annotations.Elem;
import com.uwyn.rife.tools.JavaSpecificationUtils;
import java.lang.reflect.Method;

abstract class AnnotationsElementDetector
{
	static boolean hasElementAnnotation(String implementation)
	{
		if (implementation != null && JavaSpecificationUtils.isAtLeastJdk15())
		{
			try
			{
				Class klass = Class.forName("com.uwyn.rife.engine.AnnotationsElementDetectorProcessor");
				Method detect = klass.getDeclaredMethod("detect", new Class[] {String.class});
				Boolean result = (Boolean)detect.invoke(null, new Object[] {implementation});
				return result.booleanValue();
			}
			catch (Exception e)
			{
				return false;
			}
		}

		return false;
	}
}

abstract class AnnotationsElementDetectorProcessor
{
	public static boolean detect(String implementation)
	{
		try
		{
			Class klass = Class.forName(implementation);
			if (klass.isAnnotationPresent(Elem.class))
			{
				return true;
			}
		}
		catch (ClassNotFoundException e)
		{
			// do nothing, the implementation is not a valid Java class
		}
		
		return false;
	}
}
