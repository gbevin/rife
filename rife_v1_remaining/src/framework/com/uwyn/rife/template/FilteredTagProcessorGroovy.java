/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FilteredTagProcessorGroovy.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.Script;
import java.io.ByteArrayInputStream;
import java.util.Map;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.runtime.InvokerHelper;

public class FilteredTagProcessorGroovy extends FilteredTagProcessor
{
	private static FilteredTagProcessor	sInstance = null;
	
	private int					mScriptCounter = 1;
	private GroovyClassLoader	mLoader = new GroovyClassLoader(FilteredTagProcessorGroovy.class.getClassLoader(), new CompilerConfiguration());
	
	public static FilteredTagProcessor getInstance()
	{
		if (null == sInstance)
		{
			sInstance = new FilteredTagProcessorGroovy();
		}
		
		return sInstance;
	}
	
	public String getLanguage()
	{
		return "GROOVY";
	}
	
	public Object processExpression(Template template, Class rootType, String rootName, Object rootValue, String expression, Map<String, Object> context)
	throws Exception
	{
		Class script_class = (Class)template.getCacheObject(expression);
		if (null == script_class)
		{
			GroovyCodeSource code_source = new GroovyCodeSource(new ByteArrayInputStream(expression.getBytes()), "Script"+(mScriptCounter++)+".groovy", "/groovy/shell");
			script_class = mLoader.parseClass(code_source);
			template.cacheObject(expression, script_class);
		}
		
		Binding binding = new Binding(context);
		Script script = InvokerHelper.createScript(script_class, binding);
		return script.run();
	}
}

