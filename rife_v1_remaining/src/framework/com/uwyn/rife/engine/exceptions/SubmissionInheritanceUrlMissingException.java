/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SubmissionInheritanceUrlMissingException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class SubmissionInheritanceUrlMissingException extends EngineException
{
	private static final long serialVersionUID = -2296897793632006355L;

	private String	mDeclarationName = null;
	private String	mTargetDeclarationName = null;

	public SubmissionInheritanceUrlMissingException(String declarationName, String targetDeclarationName)
	{
		super("The element '"+declarationName+"' tries to generate a submission url in an inheritance structure, but the parent element '"+targetDeclarationName+"' is not mapped to any url in the site structure.");
		
		mDeclarationName = declarationName;
		mTargetDeclarationName = targetDeclarationName;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getTargetDeclarationName()
	{
		return mTargetDeclarationName;
	}
}
