/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FilteredTagProcessorMvel.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import java.io.Serializable;
import java.util.Map;
import org.mvel.MVEL;

public class FilteredTagProcessorMvel extends FilteredTagProcessor
{
	private static FilteredTagProcessor	sInstance = null;
	
	public static FilteredTagProcessor getInstance()
	{
		if (null == sInstance)
		{
			sInstance = new FilteredTagProcessorMvel();
		}
		
		return sInstance;
	}
	
	public String getLanguage()
	{
		return "MVEL";
	}
	
	public Object processExpression(Template template, Class rootType, String rootName, Object rootValue, String expression, Map<String, Object> context)
	throws Exception
	{
		Serializable compiled = (Serializable)template.getCacheObject(expression);
		if (null == compiled)
		{
			compiled = MVEL.compileExpression(expression);
			template.cacheObject(expression, compiled);
		}
		
		return MVEL.executeExpression(compiled, rootValue, context, Boolean.class);
	}
}

