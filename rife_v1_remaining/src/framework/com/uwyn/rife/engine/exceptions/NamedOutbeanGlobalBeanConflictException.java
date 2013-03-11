/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: NamedOutbeanGlobalBeanConflictException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class NamedOutbeanGlobalBeanConflictException extends DeclarationConflictException
{
	private static final long serialVersionUID = -3804064423249453638L;

	public NamedOutbeanGlobalBeanConflictException(String declarationName, String conflictName)
	{
		super("The named outbean '"+conflictName+"' of element '"+declarationName+"' conflicts with an existing named global bean.", declarationName, conflictName);
	}
}
