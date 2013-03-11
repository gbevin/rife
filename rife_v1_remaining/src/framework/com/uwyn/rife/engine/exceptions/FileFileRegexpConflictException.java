/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FileFileRegexpConflictException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class FileFileRegexpConflictException extends DeclarationConflictException
{
	private static final long serialVersionUID = 5383774550976461676L;

	private String	mSubmissionName = null;
	private String	mFileRegexp = null;

	public FileFileRegexpConflictException(String declarationName, String conflictName, String submissionName, String fileRegexp)
	{
		super("The file '"+conflictName+"' in submission '"+submissionName+"' of element '"+declarationName+"' conflicts with the file regular expression '"+fileRegexp+"'.", declarationName, conflictName);
		
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
