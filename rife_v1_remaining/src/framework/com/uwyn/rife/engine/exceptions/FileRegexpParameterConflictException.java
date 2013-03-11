/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FileRegexpParameterConflictException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class FileRegexpParameterConflictException extends DeclarationConflictException
{
	private static final long serialVersionUID = 5126419898716478831L;
	
	private String	mSubmissionName = null;
	private String	mParameterName = null;

	public FileRegexpParameterConflictException(String declarationName, String conflictName, String submissionName, String parameterName)
	{
		super("The regexp file '"+conflictName+"' in submission '"+submissionName+"' of element '"+declarationName+"' conflicts with the existing parameter '"+parameterName+"'.", declarationName, conflictName);
		
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
