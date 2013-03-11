/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FilteredTagProcessorJanino.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import java.util.Map;
import org.codehaus.janino.ExpressionEvaluator;

public class FilteredTagProcessorJanino extends FilteredTagProcessor
{
	private static FilteredTagProcessor	sInstance = null;
	
	public static FilteredTagProcessor getInstance()
	{
		if (null == sInstance)
		{
			sInstance = new FilteredTagProcessorJanino();
		}
		
		return sInstance;
	}
	
	public String getLanguage()
	{
		return "JANINO";
	}
	
	public Object processExpression(Template template, Class rootType, String rootName, Object rootValue, String expression, Map<String, Object> context)
	throws Exception
	{
		ExpressionEvaluator evaluator = (ExpressionEvaluator)template.getCacheObject(rootName+expression);
		
		if (null == evaluator)
		{
			evaluator = new ExpressionEvaluator(
				expression,
				Boolean.TYPE,
				new String[] {"context", rootName},
				new Class[] {Map.class, rootType},
				new Class[] {Exception.class},
				getClass().getClassLoader());
			template.cacheObject(rootName+expression, evaluator);
		}
		
		Boolean result = (Boolean)evaluator.evaluate(new Object[] {context, rootValue});
		
		return null != result && result.booleanValue();
	}
}

