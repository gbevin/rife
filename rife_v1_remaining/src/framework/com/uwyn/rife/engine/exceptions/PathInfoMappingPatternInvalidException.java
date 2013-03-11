/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PathInfoMappingPatternInvalidException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class PathInfoMappingPatternInvalidException extends EngineException
{
	static final long serialVersionUID = -7798386202044922044L;
	
	private String	mSpecification = null;
	private String	mInputName = null;
	private String	mInputPattern = null;
	
	public PathInfoMappingPatternInvalidException(String specification, String inputName, String inputPattern, Throwable cause)
	{
		super("The matching pattern '"+inputPattern+"' for input '"+inputName+"' in the pathinfo specification '"+specification+"' is not valid a valid regular expression.", cause);

		mSpecification = specification;
		mInputName = inputName;
		mInputPattern = inputPattern;
	}

	public String getSpecification()
	{
		return mSpecification;
	}
	
	public String getInputName()
	{
		return mInputName;
	}
	
	public String getInputPattern()
	{
		return mInputPattern;
	}
}
