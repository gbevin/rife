/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: OutputGlobalVarConflictException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class OutputGlobalVarConflictException extends DeclarationConflictException
{
	private static final long serialVersionUID = 2923391909278565913L;

	public OutputGlobalVarConflictException(String declarationName, String conflictName)
	{
		super("The output '"+conflictName+"' of element '"+declarationName+"' conflicts with an existing global variable.", declarationName, conflictName);
	}
}
