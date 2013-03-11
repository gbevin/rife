/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BeanClassType.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud;

import com.uwyn.rife.datastructures.EnumClass;

public class BeanClassType extends EnumClass<String>
{
	public static final BeanClassType	JAVA = new BeanClassType("java");
	public static final BeanClassType	SCRIPT = new BeanClassType("scripted");
	
	public static final String		SCRIPT_EXT_GROOVY = ".groovy";
	public static final String		SCRIPT_EXT_JANINO = ".janino";
	public static final String[]	SCRIPT_EXTENSIONS = new String[] {SCRIPT_EXT_GROOVY, SCRIPT_EXT_JANINO};
	
	BeanClassType(String identifier)
	{
		super(identifier);
	}
	
	public static BeanClassType getBeanType(final String implementation)
	{
		if (null == implementation)	return null;
		
		// if there are path seperators, it's a script
		if (implementation.indexOf("/") != -1)
		{
			return BeanClassType.SCRIPT;
		}
		
		// check if the implementation name ends with a script file extension
		for (String ext : SCRIPT_EXTENSIONS)
		{
			if (implementation.endsWith(ext))
			{
				return BeanClassType.SCRIPT;
			}
		}
		
		// it's thus a java class name
		return BeanClassType.JAVA;
	}
}

