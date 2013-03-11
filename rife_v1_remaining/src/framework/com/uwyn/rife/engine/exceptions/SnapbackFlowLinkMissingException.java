/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SnapbackFlowLinkMissingException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class SnapbackFlowLinkMissingException extends EngineException
{
	private static final long serialVersionUID = 1368067530873292228L;

	private String	mDeclarationName = null;

	public SnapbackFlowLinkMissingException(String declarationName)
	{
		super("Impossible to create a snapback data link from element '"+declarationName+"' since no snapback flow link is available.");
		
		mDeclarationName = declarationName;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
}
