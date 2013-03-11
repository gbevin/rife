/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FileRegexpGlobalCookieConflictException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class FileRegexpGlobalCookieConflictException extends DeclarationConflictException
{
	private static final long serialVersionUID = -4580684571902250328L;

	private String	mSubmissionName = null;
	private String	mGlobalCookieName = null;

	public FileRegexpGlobalCookieConflictException(String declarationName, String conflictName, String submissionName, String globalCookieName)
	{
		super("The regexp file '"+conflictName+"' in submission '"+submissionName+"' of element '"+declarationName+"' conflicts with the existing global cookie '"+globalCookieName+"'.", declarationName, conflictName);
		
		mSubmissionName = submissionName;
		mGlobalCookieName = globalCookieName;
	}
	
	public String getSubmissionName()
	{
		return mSubmissionName;
	}
	
	public String getGlobalCookieName()
	{
		return mGlobalCookieName;
	}
}
