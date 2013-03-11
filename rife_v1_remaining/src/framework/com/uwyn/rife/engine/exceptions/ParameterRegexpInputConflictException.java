/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParameterRegexpInputConflictException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ParameterRegexpInputConflictException extends DeclarationConflictException
{
	private static final long serialVersionUID = 6514507788301981936L;

	private String	mSubmissionName = null;
	private String	mInputName = null;

	public ParameterRegexpInputConflictException(String declarationName, String conflictName, String submissionName, String inputName)
	{
		super("The regexp parameter '"+conflictName+"' in submission '"+submissionName+"' of element '"+declarationName+"' conflicts with the existing input '"+inputName+"'.", declarationName, conflictName);
		
		mSubmissionName = submissionName;
		mInputName = inputName;
	}
	
	public String getSubmissionName()
	{
		return mSubmissionName;
	}
	
	public String getInputName()
	{
		return mInputName;
	}
}
