/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PathInfoMappingSegment.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import java.util.regex.Pattern;

public class PathInfoMappingSegment
{
	private String	mValue = null;
	private Pattern	mPattern = null;
	private boolean	mRegexp = false;
	
	private PathInfoMappingSegment(String value)
	{
		mValue = value;
		mPattern = null;
		mRegexp = false;
	}
	
	private PathInfoMappingSegment(Pattern pattern)
	{
		mValue = null;
		mPattern = pattern;
		mRegexp = true;
	}
	
	static PathInfoMappingSegment createLiteralSegment(String value)
	{
		return new PathInfoMappingSegment(value);
	}
	
	static PathInfoMappingSegment createRegexpSegment(Pattern pattern)
	{
		return new PathInfoMappingSegment(pattern);
	}
	
	public String getValue()
	{
		return mValue;
	}
	
	public Pattern getPattern()
	{
		return mPattern;
	}
	
	public boolean isRegexp()
	{
		return mRegexp;
	}
	
}

