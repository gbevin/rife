/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GenericTypeDetector.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

abstract class GenericTypeDetector
{
	static Class detectAssociatedClass(Method readMethod)
	{
		Class associated_class = null;
		Type generic_return_type = readMethod.getGenericReturnType();
		if (generic_return_type instanceof ParameterizedType)
		{
			Type[] type_args = ((ParameterizedType)generic_return_type).getActualTypeArguments();
			associated_class = (Class)type_args[0];
		}
		
		return associated_class;
	}
}
