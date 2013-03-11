/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FileRegexpInvalidException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

import java.util.regex.PatternSyntaxException;

public class FileRegexpInvalidException extends EngineException
{
	private static final long serialVersionUID = -3117450649849316797L;

	private String	mDeclarationName = null;
	private String	mSubmissionName = null;
	private String	mFileRegexp = null;

	public FileRegexpInvalidException(String declarationName, String fileRegexp, String submissionName, PatternSyntaxException cause)
	{
		super("The file regular expression '"+fileRegexp+"' in submission '"+submissionName+"' of element '"+declarationName+"' is invalid.", cause);
		
		mDeclarationName = declarationName;
		mSubmissionName = submissionName;
		mFileRegexp = fileRegexp;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
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
