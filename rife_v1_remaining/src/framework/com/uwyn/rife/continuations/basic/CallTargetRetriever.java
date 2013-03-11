/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CallTargetRetriever.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations.basic;

import com.uwyn.rife.continuations.CallState;
import com.uwyn.rife.continuations.ContinuableObject;

/**
 * Retrieves the target continuable for a call continuation.
 * <p>This is used by the call/answer implementation in the
 * {@link BasicContinuableRunner}, but if you use a similar approach in case
 * you need to implement your own runner, then you can benefit from this
 * interface too.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public interface CallTargetRetriever
{
	/**
	 * Retrieves the continuable for a particular call continuation target and
	 * state.
	 *
	 * @param target the call continuation target that was provided as an
	 * argument to the method call (see {@link com.uwyn.rife.continuations.ContinuationConfigInstrument#getCallMethodArgumentTypes()})
	 * @param state the state of the call continuation
	 * @return the continuable that corresponds to the provided call target
	 * and state
	 * @since 1.6
	 */
	public ContinuableObject getCallTarget(Object target, CallState state);
}
