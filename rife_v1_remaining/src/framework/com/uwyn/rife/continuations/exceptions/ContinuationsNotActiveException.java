/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContinuationsNotActiveException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations.exceptions;

import com.uwyn.rife.tools.exceptions.ControlFlowRuntimeException;

/**
 * Thrown when a continuations method has not been instrumented.
 * <p>This is typically the sole method body of the methods that are present
 * in a continuable support object.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public class ContinuationsNotActiveException extends RuntimeException implements ControlFlowRuntimeException
{
	private static final long serialVersionUID = -7358516398081778097L;

	/**
	 * Instantiates a new exception.
	 * 
	 * @since 1.6
	 */
	public ContinuationsNotActiveException()
	{
		super("Continuations are not active for this class method. This class hasn't been instrumented or is has been reloaded through hot-swap.");
	}
}
