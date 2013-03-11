/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FileUnknownException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class FileUnknownException extends EngineException
{
	private static final long serialVersionUID = 6375554177354377456L;

	private String	mDeclarationName = null;
	private String	mSubmissionName = null;
	private String	mFileName = null;

	public FileUnknownException(String declarationName, String fileName)
	{
		super("The element '"+declarationName+"' doesn't contain file '"+fileName+"' in any submission.");
		
		mDeclarationName = declarationName;
		mFileName = fileName;
	}

	public FileUnknownException(String declarationName, String submissionName, String fileName)
	{
		super("The element '"+declarationName+"' doesn't contain file '"+fileName+"' in submission '"+submissionName+"'.");
		
		mDeclarationName = declarationName;
		mSubmissionName = submissionName;
		mFileName = fileName;
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
