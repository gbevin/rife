/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SubmissionUrlMissingException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class SubmissionUrlMissingException extends EngineException
{
	private static final long serialVersionUID = -4467130691873815751L;
	
	private String	mDeclarationName = null;

	public SubmissionUrlMissingException(String declarationName)
	{
		super("The element '"+declarationName+"' tries to generate a submission url, but the element is not mapped to any url in the site structure.");
		
		mDeclarationName = declarationName;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
}
