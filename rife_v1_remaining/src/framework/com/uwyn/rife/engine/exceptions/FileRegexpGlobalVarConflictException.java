/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FileRegexpGlobalVarConflictException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class FileRegexpGlobalVarConflictException extends DeclarationConflictException
{
	private static final long serialVersionUID = 8063847197444748898L;

	private String	mSubmissionName = null;
	private String	mGlobalVarName = null;

	public FileRegexpGlobalVarConflictException(String declarationName, String conflictName, String submissionName, String globalVarName)
	{
		super("The regexp file '"+conflictName+"' in submission '"+submissionName+"' of element '"+declarationName+"' conflicts with the existing global variable '"+globalVarName+"'.", declarationName, conflictName);
		
		mSubmissionName = submissionName;
		mGlobalVarName = globalVarName;
	}
	
	public String getSubmissionName()
	{
		return mSubmissionName;
	}
	
	public String getGlobalVarName()
	{
		return mGlobalVarName;
	}
}
