/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalVarOutputConflictException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class GlobalVarOutputConflictException extends DeclarationConflictException
{
	private static final long serialVersionUID = -3847832563704417393L;

	public GlobalVarOutputConflictException(String declarationName, String conflictName)
	{
		super("The global variable '"+conflictName+"' of element '"+declarationName+"' conflicts with an existing output.", declarationName, conflictName);
	}
}
