/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CallException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations.exceptions;

import com.uwyn.rife.continuations.ContinuationContext;
import com.uwyn.rife.tools.exceptions.ControlFlowRuntimeException;
import com.uwyn.rife.tools.exceptions.LightweightError;

/**
 * This exception will be thrown when a call continuation is triggered.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public class CallException extends LightweightError implements ControlFlowRuntimeException
{
	private static final long serialVersionUID = 4288971544223559163L;

	private ContinuationContext mContext = null;
	private Object				mTarget = null;

	/**
	 * [PRIVATE AND UNSUPPORTED] Instantiates a new call exception.
	 * <p>This is used by the instrumented bytecode that provides
	 * continuations support, it's not intended for general use.
	 * @param context the active continuation context
	 * @param target the call target
	 * @since 1.6
	 */
	public CallException(ContinuationContext context, Object target)
	{
		super();
		
		context.setPaused(true);
		
		mContext = context;
		mTarget = target;
	}
	
	/**
	 * Retrieves the context of this call continuation.
	 *
	 * @return this call continuation's context
	 * @since 1.6
	 */
	public ContinuationContext getContext()
	{
		return mContext;
	}
	
	/**
	 * Retrieves the target of this call continuation.
	 *
	 * @return this call continuation's target
	 * @since 1.6
	 */
	public Object getTarget()
	{
		return mTarget;
	}
}
