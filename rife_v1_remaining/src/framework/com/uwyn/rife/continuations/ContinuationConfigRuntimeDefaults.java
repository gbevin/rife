/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContinuationConfigRuntimeDefaults.java 3984 2009-01-09 17:01:37Z gbevin $
 */
package com.uwyn.rife.continuations;

/**
 * Default values for {@link ContinuationConfigRuntime}.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3984 $
 * @since 1.7
 */
public interface ContinuationConfigRuntimeDefaults
{
	/**
	 * The default duration is 20 minutes.
	 * @since 1.6
	 */
	public static final long	DEFAULT_CONTINUATION_DURATION = 1200000;

	/**
	 * The default frequency is every 20 times out of the scale, with the
	 * default scale of 1000 this means, 1/50th of the time.
	 * @since 1.6
	 */
	public static final int		DEFAULT_CONTINUATION_PURGE_FREQUENCY = 20;

	/**
	 * The default purge scale is 1000.
	 * @since 1.6
	 */
    public static final int		DEFAULT_CONTINUATION_PURGE_SCALE = 1000;
}