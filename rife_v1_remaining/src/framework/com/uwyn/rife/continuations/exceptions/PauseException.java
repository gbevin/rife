/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PauseException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations.exceptions;

import com.uwyn.rife.continuations.ContinuationContext;
import com.uwyn.rife.tools.exceptions.ControlFlowRuntimeException;
import com.uwyn.rife.tools.exceptions.LightweightError;

/**
 * This exception will be thrown when a pause continuation is triggered.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public class PauseException extends LightweightError implements ControlFlowRuntimeException
{
	private static final long serialVersionUID = 181863837154043654L;

	private ContinuationContext	mContext = null;

	/**
	 * [PRIVATE AND UNSUPPORTED] Instantiates a new pause exception.
	 * <p>This is used by the instrumented bytecode that provides
	 * continuations support, it's not intended for general use.
	 * @param context the active continuation context
	 * @since 1.6
	 */
	public PauseException(ContinuationContext context)
	{
		context.setPaused(true);

		mContext = context;
	}
	
	/**
	 * Retrieves the context of this pause continuation.
	 *
	 * @return this pause continuation's context
	 * @since 1.6
	 */
	public ContinuationContext getContext()
	{
		return mContext;
	}
}

