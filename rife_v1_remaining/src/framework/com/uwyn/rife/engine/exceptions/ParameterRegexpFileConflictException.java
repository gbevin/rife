/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParameterRegexpFileConflictException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ParameterRegexpFileConflictException extends DeclarationConflictException
{
	private static final long serialVersionUID = -7085880908758305864L;

	private String	mSubmissionName = null;
	private String	mFileName = null;

	public ParameterRegexpFileConflictException(String declarationName, String conflictName, String submissionName, String fileName)
	{
		super("The regexp parameter '"+conflictName+"' in submission '"+submissionName+"' of element '"+declarationName+"' conflicts with the existing file '"+fileName+"'.", declarationName, conflictName);
		
		mSubmissionName = submissionName;
		mFileName = fileName;
	}
	
	public String getSubmissionName()
	{
		return mSubmissionName;
	}
	
	public String getFileName()
	{
		return mFileName;
	}
}
