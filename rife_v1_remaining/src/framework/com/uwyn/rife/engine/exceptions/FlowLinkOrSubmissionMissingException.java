/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FlowLinkOrSubmissionMissingException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class FlowLinkOrSubmissionMissingException extends EngineException
{
	static final long serialVersionUID = 4060645573665624449L;
	
	private String	mDeclarationName = null;

	public FlowLinkOrSubmissionMissingException(String DeclarationName)
	{
		super("Impossible to create a reflexive data link on element '"+DeclarationName+"' since no reflexive flow link nor any submission are available.");
		
		mDeclarationName = DeclarationName;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
}
