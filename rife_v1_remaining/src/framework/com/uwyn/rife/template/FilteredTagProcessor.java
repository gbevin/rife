/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FilteredTagProcessor.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.uwyn.rife.template.exceptions.ExpressionException;
import com.uwyn.rife.template.exceptions.ExpressionNotBooleanException;

public abstract class FilteredTagProcessor
{
	public abstract Object processExpression(Template template, Class rootType, String rootName, Object rootValue, String expression, Map<String, Object> context) throws Exception;
	public abstract String getLanguage();

	public void processTags(List<String> setValues, Template template, List<String[]> blocks, String id, Class rootType, String rootName, Object rootValue, Map<String, Object> map)
	{
		if (blocks.size() != 0)
		{
			for (String[] block : blocks)
			{
				if (id != null &&
					!id.equals(block[1]))
				{
					continue;
				}
			
				// if the value corresponding to the filtered block is already
				// set, don't process the block any further
				if (null == id &&
					template.isValueSet(block[1]))
				{
					continue;
				}
				
				// prepare the context map and integrate the template
				// expression vars
				Map<String, Object> context_map = null;
				if (null == template.getExpressionVars())
				{
					context_map = map;
				}
				else if (null == map)
				{
					context_map = template.getExpressionVars();
				}
				else
				{
					context_map = new HashMap<String, Object>();
					
					context_map.putAll(template.getExpressionVars());
					context_map.putAll(map);
				}
				
				// store the root variable in the context map
				if (rootName != null)
				{
					if (null == context_map)
					{
						context_map = new HashMap<String, Object>();
					}
					context_map.put(rootName, rootValue);
				}
				
				// extract the expression
				String	expression = block[block.length-1];
				
				// process the expression
				Object result = null;
				try
				{
					result = processExpression(template, rootType, rootName, rootValue, expression, context_map);
				}
				catch (Exception e)
				{
					throw new ExpressionException(getLanguage(), template.getClass().getName(), block[block.length-1], e);
				}
				
				// if the result is null, just return
				if (null == result)
				{
					continue;
				}
				
				// ensure that the result is boolean
				if (!(result instanceof Boolean))
				{
					throw new ExpressionNotBooleanException(getLanguage(), template.getClass().getName(), block[block.length-1], result.getClass());
				}
				
				// automatically set the block if the resulting boolean value is true
				if (((Boolean)result).booleanValue())
				{
					template.setBlock(block[1], block[0]);
					if (setValues != null)
					{
						setValues.add(block[1]);
					}
				}
			}
		}
	}
}

