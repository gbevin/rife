/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FileRegexpInputConflictException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class FileRegexpInputConflictException extends DeclarationConflictException
{
	private static final long serialVersionUID = 1998513396560809049L;

	private String	mSubmissionName = null;
	private String	mInputName = null;

	public FileRegexpInputConflictException(String declarationName, String conflictName, String submissionName, String inputName)
	{
		super("The regexp file '"+conflictName+"' in submission '"+submissionName+"' of element '"+declarationName+"' conflicts with the existing input '"+inputName+"'.", declarationName, conflictName);
		
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
