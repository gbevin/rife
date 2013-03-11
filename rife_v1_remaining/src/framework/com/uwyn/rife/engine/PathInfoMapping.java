/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PathInfoMapping.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.engine.exceptions.PathInfoMappingPatternInvalidException;
import com.uwyn.rife.tools.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class PathInfoMapping
{
	public final static Pattern PATHINFO_SPEC_PATTERN = Pattern.compile("(?<!\\\\)\\$\\{?(\\w+)\\}?(?>\\(([^()]*)\\))?"); // (?<!\\)\$\{?(\w+)\}?(?>\(([^()]*)\))?
	
	private String							mSpecification = null;
	private Pattern							mRegexp = null;
	private List<String>					mInputs = null;
	private List<PathInfoMappingSegment>	mSegments = null;
	
	private PathInfoMapping(String specification)
	{
		mSpecification = specification;
		mInputs = new ArrayList<String>();
		mSegments = new ArrayList<PathInfoMappingSegment>();
	}
	
	public String getSpecification()
	{
		return mSpecification;
	}
	
	public Pattern getRegexp()
	{
		return mRegexp;
	}
	
	public List<String> getInputs()
	{
		return mInputs;
	}
	
	public List<PathInfoMappingSegment> getSegments()
	{
		return mSegments;
	}
	
	static PathInfoMapping create(String specification)
	throws EngineException
	{
		PathInfoMapping mapping = new PathInfoMapping(specification);
		
		if (null == specification)
		{
			return null;
		}
		
		if (!specification.startsWith("/"))
		{
			specification = "/"+specification;
		}
		
		Matcher matcher = PATHINFO_SPEC_PATTERN.matcher(specification);
		if (!matcher.find())
		{
			mapping.mRegexp = Pattern.compile(StringUtils.encodeRegexp(specification));
			mapping.mSegments.add(PathInfoMappingSegment.createLiteralSegment(specification));
			return mapping;
		}
		
		int last_group_end = 0;
		
		StringBuilder mapping_regexp = new StringBuilder();
		while (2 == matcher.groupCount())
		{
			// retrieve the text literal between the previous match and the current one
			String literal = specification.substring(last_group_end, matcher.start());
			if (literal.length() > 0)
			{
				mapping_regexp.append(StringUtils.encodeRegexp(literal));
				mapping.mSegments.add(PathInfoMappingSegment.createLiteralSegment(literal));
			}
			
			// obtain the input name
			String input_name = matcher.group(1);
			mapping.mInputs.add(input_name);
			
			// obtain the input matcher pattern
			String input_pattern = matcher.group(2);
			
			// use the default pattern if none is provided
			if (null == input_pattern ||
				0 == input_pattern.length())
			{
				input_pattern = "\\w+";
			}
			
			// check if the pattern syntax is valid
			Pattern pattern;
			try
			{
				pattern = Pattern.compile(input_pattern);
			}
			catch (PatternSyntaxException e)
			{
				throw new PathInfoMappingPatternInvalidException(specification, input_name, input_pattern, e);
			}
			
			mapping_regexp.append("(");
			mapping_regexp.append(input_pattern);
			mapping_regexp.append(")");
			mapping.mSegments.add(PathInfoMappingSegment.createRegexpSegment(pattern));
			
			// remember the end in this match
			last_group_end = matcher.end();
			
			// check if there are any more matches
			if (!matcher.find())
			{
				break;
			}
		}
		
		// retrieve the text literal at the end
		String literal = specification.substring(last_group_end);
		if (literal.length() > 0)
		{
			mapping_regexp.append(StringUtils.encodeRegexp(literal));
			mapping.mSegments.add(PathInfoMappingSegment.createLiteralSegment(literal));
		}
		
		mapping.mRegexp = Pattern.compile(mapping_regexp.toString());
		
		return mapping;
	}
}

