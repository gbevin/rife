/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: NamedInbeanGlobalBeanConflictException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class NamedInbeanGlobalBeanConflictException extends DeclarationConflictException
{
	private static final long serialVersionUID = -8880707725100346849L;

	public NamedInbeanGlobalBeanConflictException(String declarationName, String conflictName)
	{
		super("The named inbean '"+conflictName+"' of element '"+declarationName+"' conflicts with an existing named global bean.", declarationName, conflictName);
	}
}
