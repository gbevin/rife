/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: OutcookieGlobalCookieConflictException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class OutcookieGlobalCookieConflictException extends DeclarationConflictException
{
	private static final long serialVersionUID = 4540660703532118060L;

	public OutcookieGlobalCookieConflictException(String declarationName, String conflictName)
	{
		super("The outcookie '"+conflictName+"' of element '"+declarationName+"' conflicts with an existing global cookie.", declarationName, conflictName);
	}
}
