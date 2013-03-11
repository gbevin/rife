/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FileParameterConflictException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class FileParameterConflictException extends DeclarationConflictException
{
	private static final long serialVersionUID = -4700031506601370070L;

	private String	mSubmissionName = null;

	public FileParameterConflictException(String declarationName, String conflictName, String submissionName)
	{
		super("The file '"+conflictName+"' in submission '"+submissionName+"' of element '"+declarationName+"' conflicts with an existing parameter.", declarationName, conflictName);
		
		mSubmissionName = submissionName;
	}
	
	public String getSubmissionName()
	{
		return mSubmissionName;
	}
}
