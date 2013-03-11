/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: IncookieParameterConflictException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class IncookieParameterConflictException extends DeclarationConflictException
{
	private static final long serialVersionUID = -3511161024149678733L;

	private String	mSubmissionName = null;

	public IncookieParameterConflictException(String declarationName, String conflictName, String submissionName)
	{
		super("The incookie '"+conflictName+"' of element '"+declarationName+"' conflicts with an existing parameter of submission '"+submissionName+"'.", declarationName, conflictName);
		
		mSubmissionName = submissionName;
	}
	
	public String getSubmissionName()
	{
		return mSubmissionName;
	}
}
