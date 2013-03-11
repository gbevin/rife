/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ClassCallTargetRetriever.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations.basic;

import com.uwyn.rife.continuations.CallState;
import com.uwyn.rife.continuations.ContinuableObject;
import com.uwyn.rife.continuations.exceptions.CallTargetNotFoundException;

/**
 * Retrieves the continuable for a call continuation where the call target is a
 * class.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public class ClassCallTargetRetriever implements CallTargetRetriever
{
	/**
	 * Retrieves the continuable for a call continuation.
	 * 
	 * @param target the call target object that will be used to retrieve the
	 * continuable
	 * @param state the call state
	 * @return the call continuable; or
	 * <p>{@code null} if no continuable should be executed immediately in
	 * response to this call
	 * @since 1.6
     */ 
	public ContinuableObject getCallTarget(Object target, CallState state)
	{
		try
		{
			Class target_class = (Class)target;
			Object target_instance = target_class.newInstance();
			return (ContinuableObject)target_instance;
		}
		catch (Throwable e)
		{
			throw new CallTargetNotFoundException(target, e);
		}
	}
}
