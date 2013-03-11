/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DeployerInstantiationException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class DeployerInstantiationException extends EngineException
{
	private static final long serialVersionUID = -496720681753283695L;

	private String	mDeclarationName = null;

	public DeployerInstantiationException(String declarationName, Throwable cause)
	{
		super("The deployer of element '"+declarationName+"' couldn't be instantiated.", cause);
		
		mDeclarationName = declarationName;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
}
