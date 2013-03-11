/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParameterParameterRegexpConflictException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ParameterParameterRegexpConflictException extends DeclarationConflictException
{
	private static final long serialVersionUID = -3074721538233843502L;

	private String	mSubmissionName = null;
	private String	mParameterRegexp = null;

	public ParameterParameterRegexpConflictException(String declarationName, String conflictName, String submissionName, String parameterRegexp)
	{
		super("The parameter '"+conflictName+"' in submission '"+submissionName+"' of element '"+declarationName+"' conflicts with the existing parameter regular expression '"+parameterRegexp+"'.", declarationName, conflictName);
		
		mSubmissionName = submissionName;
		mParameterRegexp = parameterRegexp;
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
