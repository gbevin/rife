/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalVarParameterConflictException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class GlobalVarParameterConflictException extends DeclarationConflictException
{
	private static final long serialVersionUID = -5614174621201613794L;

	private String	mSubmissionName = null;

	public GlobalVarParameterConflictException(String declarationName, String conflictName, String submissionName)
	{
		super("The global variable '"+conflictName+"' of element '"+declarationName+"' conflicts with an existing parameter of submission '"+submissionName+"'.", declarationName, conflictName);
		
		mSubmissionName = submissionName;
	}
	
	public String getSubmissionName()
	{
		return mSubmissionName;
	}
}
