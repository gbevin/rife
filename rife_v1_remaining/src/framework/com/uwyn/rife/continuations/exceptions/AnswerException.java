/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AnswerException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations.exceptions;

import com.uwyn.rife.continuations.ContinuationContext;
import com.uwyn.rife.tools.exceptions.ControlFlowRuntimeException;
import com.uwyn.rife.tools.exceptions.LightweightError;

/**
 * This exception will be thrown when an answer continuation is triggered.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public class AnswerException extends LightweightError implements ControlFlowRuntimeException
{
	private static final long serialVersionUID = 4501524247064256632L;

 	private ContinuationContext	mContext = null;
	private Object				mCallAnswer = null;

	/**
	 * [PRIVATE AND UNSUPPORTED] Instantiates a new answer exception.
	 * <p>This is used by the instrumented bytecode that provides
	 * continuations support, it's not intended for general use.
	 * @param context the active continuation context
	 * @param answer the answered value
	 * @since 1.6
	 */
	public AnswerException(ContinuationContext context, Object answer)
	{
		super();
		
		mContext = context;
		mCallAnswer = answer;
	}

	/**
	 * Retrieves the context of this answer continuation.
	 *
	 * @return this answer continuation's context
	 * @since 1.6
	 */
	public ContinuationContext getContext()
	{
		return mContext;
	}

	/**
	 * Retrieves the answered value.
	 *
	 * @return this answer continuation's anwered value; or
	 * <p>{@code null} if no answer was provided
	 * @since 1.6
	 */
	public Object getAnswer()
	{
		return mCallAnswer;
	}
}
