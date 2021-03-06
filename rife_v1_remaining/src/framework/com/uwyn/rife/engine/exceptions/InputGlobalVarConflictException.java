/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InputGlobalVarConflictException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class InputGlobalVarConflictException extends DeclarationConflictException
{
	private static final long serialVersionUID = 7395921964150205375L;

	public InputGlobalVarConflictException(String declarationName, String conflictName)
	{
		super("The input '"+conflictName+"' of element '"+declarationName+"' conflicts with an existing global variable.", declarationName, conflictName);
	}
}
