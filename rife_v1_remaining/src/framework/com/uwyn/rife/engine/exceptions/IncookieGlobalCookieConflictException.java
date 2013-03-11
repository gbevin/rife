/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: IncookieGlobalCookieConflictException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class IncookieGlobalCookieConflictException extends DeclarationConflictException
{
	private static final long serialVersionUID = -8860780483338599691L;

	public IncookieGlobalCookieConflictException(String declarationName, String conflictName)
	{
		super("The incookie '"+conflictName+"' of element '"+declarationName+"' conflicts with an existing global cookie.", declarationName, conflictName);
	}
}
