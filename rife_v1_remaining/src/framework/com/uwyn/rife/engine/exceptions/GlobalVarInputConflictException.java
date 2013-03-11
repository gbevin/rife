/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalVarInputConflictException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class GlobalVarInputConflictException extends DeclarationConflictException
{
	private static final long serialVersionUID = -6496879841250387554L;

	public GlobalVarInputConflictException(String declarationName, String conflictName)
	{
		super("The global variable '"+conflictName+"' of element '"+declarationName+"' conflicts with an existing input.", declarationName, conflictName);
	}
}
