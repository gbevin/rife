/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParameterRegexpGlobalVarConflictException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ParameterRegexpGlobalVarConflictException extends DeclarationConflictException
{
	private static final long serialVersionUID = 996515132568622989L;

	private String	mSubmissionName = null;
	private String	mGlobalVarName = null;

	public ParameterRegexpGlobalVarConflictException(String declarationName, String conflictName, String submissionName, String globalVarName)
	{
		super("The regexp parameter '"+conflictName+"' in submission '"+submissionName+"' of element '"+declarationName+"' conflicts with the existing global variable '"+globalVarName+"'.", declarationName, conflictName);
		
		mSubmissionName = submissionName;
		mGlobalVarName = globalVarName;
	}
	
	public String getSubmissionName()
	{
		return mSubmissionName;
	}
	
	public String getGlobalVarName()
	{
		return mGlobalVarName;
	}
}
