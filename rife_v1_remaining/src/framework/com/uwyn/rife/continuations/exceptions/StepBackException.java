/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: StepBackException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations.exceptions;

import com.uwyn.rife.continuations.ContinuationContext;
import com.uwyn.rife.continuations.ContinuationManager;
import com.uwyn.rife.tools.exceptions.ControlFlowRuntimeException;
import com.uwyn.rife.tools.exceptions.LightweightError;

/**
 * This exception will be thrown when a stepback continuation is triggered.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public class StepBackException extends LightweightError implements ControlFlowRuntimeException
{
	private static final long serialVersionUID = -5005676849123442618L;
	
	private ContinuationContext mContext = null;

	/**
	 * [PRIVATE AND UNSUPPORTED] Instantiates a new stepback exception.
	 * <p>This is used by the instrumented bytecode that provides
	 * continuations support, it's not intended for general use.
	 * @param context the active continuation context
	 * @since 1.6
	 */
	public StepBackException(ContinuationContext context)
	{
		context.setPaused(true);
		
		mContext = context;
	}
	
	/**
	 * Retrieves the context of this stepback continuation.
	 *
	 * @return this stepback continuation's context
	 * @since 1.6
	 */
	public ContinuationContext getContext()
	{
		return mContext;
	}

	/**
	 * Looks up the ID of the target continuation of the stepback.
	 *
	 * @return the target continuation ID of the stepback; or
	 * <p>{@code null} if the target continuation couldn't be found
	 * @since 1.6
	 */
	public String lookupStepBackId()
	{
		ContinuationManager manager = mContext.getManager();
		
		// try to obtain the label of the previous continuation,
		// if there is no previous continuation, simply start from the beginning again				
		String parent_id = mContext.getParentId();
		ContinuationContext parent_context = manager.getContext(parent_id);
		if (parent_context != null)
		{
			String grandparent_id = parent_context.getParentId();
			ContinuationContext grandparent_context = manager.getContext(grandparent_id);
			
			// if the parent context exists, set up this context to resume execution
			// where the parent context resumed it
			if (grandparent_context != null)
			{
				mContext.setLabel(grandparent_context.getLabel());
				mContext.setParentId(grandparent_context.getParentId());
				mContext.addRelatedId(parent_id);
				return mContext.getId();
			}
		}
		
		return null;
	}
}

