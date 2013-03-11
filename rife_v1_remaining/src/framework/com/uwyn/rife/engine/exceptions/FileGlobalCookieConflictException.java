/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FileGlobalCookieConflictException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class FileGlobalCookieConflictException extends DeclarationConflictException
{
	private static final long serialVersionUID = -6395920874951420518L;

	private String	mSubmissionName = null;

	public FileGlobalCookieConflictException(String declarationName, String conflictName, String submissionName)
	{
		super("The file '"+conflictName+"' in submission '"+submissionName+"' of element '"+declarationName+"' conflicts with an existing global cookie.", declarationName, conflictName);

		mSubmissionName = submissionName;
	}
	
	public String getSubmissionName()
	{
		return mSubmissionName;
	}
}
