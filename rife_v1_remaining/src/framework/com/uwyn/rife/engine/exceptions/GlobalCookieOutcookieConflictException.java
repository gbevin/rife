/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalCookieOutcookieConflictException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class GlobalCookieOutcookieConflictException extends DeclarationConflictException
{
	private static final long serialVersionUID = 5468343396741898416L;

	public GlobalCookieOutcookieConflictException(String declarationName, String conflictName)
	{
		super("The global cookie '"+conflictName+"' of element '"+declarationName+"' conflicts with an existing outcookie.", declarationName, conflictName);
	}
}
