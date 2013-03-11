/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CallTargetNotFoundException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations.exceptions;

import com.uwyn.rife.tools.exceptions.ControlFlowRuntimeException;

/**
 * Thrown when a call target couldn't be resolved to a proper
 * {@code ContinuableObject}.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public class CallTargetNotFoundException extends RuntimeException implements ControlFlowRuntimeException
{
	private static final long serialVersionUID = 8455201993543302381L;

	/**
	 * Instantiates a new exception.
	 *
	 * @param target the original call target
	 * @param cause the cause of the retrieval failure; or
	 * <p>{@code null} if there was no exception cause
	 * @since 1.6
	 */
	public CallTargetNotFoundException(Object target, Throwable cause)
	{
		super("The ContinuableObject that corresponds to the call target "+target+" couldn't be found.", cause);
	}
}
