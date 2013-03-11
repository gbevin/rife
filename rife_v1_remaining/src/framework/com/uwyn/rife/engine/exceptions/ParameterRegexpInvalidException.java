/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParameterRegexpInvalidException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

import java.util.regex.PatternSyntaxException;

public class ParameterRegexpInvalidException extends EngineException
{
	private static final long serialVersionUID = 1744487303290975583L;

	private String	mDeclarationName = null;
	private String	mSubmissionName = null;
	private String	mParameterRegexp = null;

	public ParameterRegexpInvalidException(String declarationName, String parameterRegexp, String submissionName, PatternSyntaxException cause)
	{
		super("The parameter regular expression '"+parameterRegexp+"' in submission '"+submissionName+"' of element '"+declarationName+"' is invalid.", cause);
		
		mDeclarationName = declarationName;
		mSubmissionName = submissionName;
		mParameterRegexp = parameterRegexp;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getSubmissionName()
	{
		return mSubmissionName;
	}
	
	public String getParameterRegexp()
	{
		return mParameterRegexp;
	}
}
