/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParameterIncookieConflictException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ParameterIncookieConflictException extends DeclarationConflictException
{
	private static final long serialVersionUID = 7950755358721085291L;

	private String	mSubmissionName = null;

	public ParameterIncookieConflictException(String declarationName, String conflictName, String submissionName)
	{
		super("The parameter '"+conflictName+"' in submission '"+submissionName+"' of element '"+declarationName+"' conflicts with an existing incookie.", declarationName, conflictName);
		
		mSubmissionName = submissionName;
	}
	
	public String getSubmissionName()
	{
		return mSubmissionName;
	}
}
