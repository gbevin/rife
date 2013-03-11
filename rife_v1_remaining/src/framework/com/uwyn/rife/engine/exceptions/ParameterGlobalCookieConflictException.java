/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParameterGlobalCookieConflictException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ParameterGlobalCookieConflictException extends DeclarationConflictException
{
	private static final long serialVersionUID = -6169497738920544658L;

	private String	mSubmissionName = null;

	public ParameterGlobalCookieConflictException(String declarationName, String conflictName, String submissionName)
	{
		super("The parameter '"+conflictName+"' in submission '"+submissionName+"' of element '"+declarationName+"' conflicts with an existing global cookie.", declarationName, conflictName);

		mSubmissionName = submissionName;
	}
	
	public String getSubmissionName()
	{
		return mSubmissionName;
	}
}
