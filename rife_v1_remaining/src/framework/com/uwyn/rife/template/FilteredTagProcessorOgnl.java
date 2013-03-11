/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FilteredTagProcessorOgnl.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import ognl.*;

import java.util.Map;

public class FilteredTagProcessorOgnl extends FilteredTagProcessor
{
	private static final ClassResolver	RESOLVER = new RifeResolver();
	
	private static FilteredTagProcessor	sInstance = null;
	
	public static FilteredTagProcessor getInstance()
	{
		if (null == sInstance)
		{
			sInstance = new FilteredTagProcessorOgnl();
		}
		
		return sInstance;
	}
	
	public String getLanguage()
	{
		return "OGNL";
	}
	
	public Object processExpression(Template template, Class rootType, String rootName, Object rootValue, String expression, Map<String, Object> context)
	throws Exception
	{
		Node	tree = (Node)template.getCacheObject(expression);
		if (null == tree)
		{
			tree = (Node)Ognl.parseExpression(expression);
			template.cacheObject(expression, tree);
		}
		TypeConverter	converter = new DefaultTypeConverter();
		OgnlContext		ognl_context = new OgnlContext(RESOLVER, converter, new DefaultMemberAccess(false, false, false), context);
		
		return tree.getValue(ognl_context, rootValue);
	}
	
	public static class RifeResolver implements ClassResolver
	{
		RifeResolver()
		{
		}
		
		public Class classForName(String classname, Map context)
		throws ClassNotFoundException
		{
			return getClass().getClassLoader().loadClass(classname);
		}
	}
}

