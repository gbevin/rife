/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParameterRegexpParameterConflictException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ParameterRegexpParameterConflictException extends DeclarationConflictException
{
	private static final long serialVersionUID = -7843973346127155864L;
	
	private String	mSubmissionName = null;
	private String	mParameterName = null;

	public ParameterRegexpParameterConflictException(String declarationName, String conflictName, String submissionName, String parameterName)
	{
		super("The regexp parameter '"+conflictName+"' in submission '"+submissionName+"' of element '"+declarationName+"' conflicts with the existing parameter '"+parameterName+"'.", declarationName, conflictName);
		
		mSubmissionName = submissionName;
		mParameterName = parameterName;
	}
	
	public String getSubmissionName()
	{
		return mSubmissionName;
	}
	
	public String getParameterName()
	{
		return mParameterName;
	}
}
