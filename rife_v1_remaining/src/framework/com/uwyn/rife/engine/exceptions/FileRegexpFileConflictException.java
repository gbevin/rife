/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FileRegexpFileConflictException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class FileRegexpFileConflictException extends DeclarationConflictException
{
	private static final long serialVersionUID = -2978853055472876042L;

	private String	mSubmissionName = null;
	private String	mFileName = null;

	public FileRegexpFileConflictException(String declarationName, String conflictName, String submissionName, String fileName)
	{
		super("The regexp file '"+conflictName+"' in submission '"+submissionName+"' of element '"+declarationName+"' conflicts with the existing file '"+fileName+"'.", declarationName, conflictName);
		
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
