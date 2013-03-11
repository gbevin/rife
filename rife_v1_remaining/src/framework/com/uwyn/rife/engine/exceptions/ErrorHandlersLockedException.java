/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ErrorHandlersLockedException.java 3928 2008-04-22 16:25:18Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ErrorHandlersLockedException extends EngineException
{
	private static final long serialVersionUID = -5370945622869808619L;

	private String	mDeclarationName = null;
	private String	mDestId = null;

	public ErrorHandlersLockedException(String declarationName, String destId)
	{
		super("The site '"+declarationName+"' couldn't add the error handler towards '"+destId+"' since the error handlers of the declaring group are locked.");

		mDeclarationName = declarationName;
		mDestId = destId;
	}

	public String getDeclarationName()
	{
		return mDeclarationName;
	}

	public String getDestId()
	{
		return mDestId;
	}
}