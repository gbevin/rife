/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FileExistsException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class FileExistsException extends EngineException
{
	private static final long serialVersionUID = -2880596505795627595L;

	private String	mDeclarationName = null;
	private String	mSubmissionName = null;
	private String	mFileName = null;

	public FileExistsException(String declarationName, String parameterName, String submissionName)
	{
		super("The submission '"+submissionName+"' of element '"+declarationName+"' already contains file '"+parameterName+"'.");
		
		mDeclarationName = declarationName;
		mSubmissionName = submissionName;
		mFileName = parameterName;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
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
