/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParameterFileRegexpConflictException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ParameterFileRegexpConflictException extends DeclarationConflictException
{
	private static final long serialVersionUID = 4593281345470673114L;
	
	private String	mSubmissionName = null;
	private String	mFileRegexp = null;

	public ParameterFileRegexpConflictException(String declarationName, String conflictName, String submissionName, String fileRegexp)
	{
		super("The parameter '"+conflictName+"' in submission '"+submissionName+"' of element '"+declarationName+"' conflicts with the existing file regular expression '"+fileRegexp+"'.", declarationName, conflictName);
		
		mSubmissionName = submissionName;
		mFileRegexp = fileRegexp;
	}
	
	public String getSubmissionName()
	{
		return mSubmissionName;
	}
	
	public String getFileRegexp()
	{
		return mFileRegexp;
	}
}
