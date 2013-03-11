/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParameterRegexpIncookieConflictException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ParameterRegexpIncookieConflictException extends DeclarationConflictException
{
	private static final long serialVersionUID = 2962445249477260632L;

	private String	mSubmissionName = null;
	private String	mIncookieName = null;

	public ParameterRegexpIncookieConflictException(String declarationName, String conflictName, String submissionName, String incookieName)
	{
		super("The regexp parameter '"+conflictName+"' in submission '"+submissionName+"' of element '"+declarationName+"' conflicts with the existing incookie '"+incookieName+"'.", declarationName, conflictName);
		
		mSubmissionName = submissionName;
		mIncookieName = incookieName;
	}
	
	public String getSubmissionName()
	{
		return mSubmissionName;
	}
	
	public String getIncookieName()
	{
		return mIncookieName;
	}
}
