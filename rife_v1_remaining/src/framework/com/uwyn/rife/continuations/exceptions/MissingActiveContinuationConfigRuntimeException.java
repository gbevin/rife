package com.uwyn.rife.continuations.exceptions;

import com.uwyn.rife.tools.exceptions.ControlFlowRuntimeException;

/**
 * Thrown when the active continuation runtime configuration isn't set.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3813 $
 * @since 1.6
 */
public class MissingActiveContinuationConfigRuntimeException extends RuntimeException implements ControlFlowRuntimeException
{
	private static final long serialVersionUID = 7401871623085473212L;

	/**
	 * Instantiates a new exception.
	 *
	 * @since 1.6
	 */
	public MissingActiveContinuationConfigRuntimeException()
	{
		super("The active ContinuationConfigRuntime instance is not set, this is required for continuations to be able to execute. Always call ContinuationConfigRuntime.setActiveConfigRuntime(config) in your continuations runner before executing the ContinuableObject.");
	}
}
