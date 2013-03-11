/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: VisitInterruptionException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.instrument.exceptions;

/**
 * This is an internal exception that is used whenever the bytecode of a
 * class is being visited and the analysis has to be interrupted in the
 * middle.
 * <p>NOTE: this exception is not intended to be caught or to be used inside
 * your own code
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public class VisitInterruptionException extends Error
{
	private static final long serialVersionUID = -1193837716595477767L;

	public VisitInterruptionException()
	{
		super("handled visit");
	}
	
	public Throwable fillInStackTrace()
	{
		return null;
	}
	
	public StackTraceElement[] getStackTrace()
	{
		return new StackTraceElement[0];
	}
}
