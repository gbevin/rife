/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContinuationConfigRuntime.java 3984 2009-01-09 17:01:37Z gbevin $
 */
package com.uwyn.rife.continuations;

import com.uwyn.rife.continuations.exceptions.MissingActiveContinuationConfigRuntimeException;

/**
 * Configures the runtime behavior of the continuations engine.
 * <p>The active runtime configuration always has to be available through
 * {@link #getActiveConfigRuntime()} when a {@link ContinuableObject} is
 * executed. Therefore, it's best to always call
 * {@link #setActiveConfigRuntime} before the executing. The
 * {@link com.uwyn.rife.continuations.basic.BasicContinuableRunner} does
 * this by default. If you create your own runner, you have to ensure that
 * this is respected.
 * <p>By default the lifetime duration and purging of {@link ContinuableObject}
 * instances is set to a sensible default, so this only needs tuning in
 * specific case.
 * <p>This class has to be extended though to provide information that suits
 * your continuations usage and to indicate whether continuations should be
 * cloned when they are resumed.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3984 $
 * @since 1.6
 */
public abstract class ContinuationConfigRuntime<T extends ContinuableObject> implements ContinuationConfigRuntimeDefaults
{
	private static transient ThreadLocal<ContinuationConfigRuntime> sActiveConfigRuntime = new ThreadLocal<ContinuationConfigRuntime>();

	/**
	 * Sets the active runtime configuration for the executing thread.
	 *
	 * @param config the active runtime configuration for the executing thread
	 * @since 1.6
	 */
	public static void setActiveConfigRuntime(ContinuationConfigRuntime config)
	{
		sActiveConfigRuntime.set(config);
	}

	/**
	 * Retrieves the active runtime configuration for the executing thread.
	 * 
	 * @return the active runtime configuration
	 * @throws MissingActiveContinuationConfigRuntimeException when the active
	 * runtime configuration isn't set
	 * @since 1.6
	 */
	public static ContinuationConfigRuntime getActiveConfigRuntime()
	throws MissingActiveContinuationConfigRuntimeException
	{
		ContinuationConfigRuntime config = sActiveConfigRuntime.get();
		if (null == config)
		{
			throw new MissingActiveContinuationConfigRuntimeException();
		}
		return config;
	}

	/**
	 * The duration, in milliseconds, by which a continuation stays valid.
	 * <p>When this period is exceeded, a continuation can not be retrieved
	 * anymore and it will be removed from the manager during the next purge.
	 *
	 * @return the validity duration of a continuation in milliseconds
	 * @since 1.6
	 */
	public long getContinuationDuration()
	{
        return DEFAULT_CONTINUATION_DURATION;
    }

	/**
	 * The frequency by which the continuations purging will run in the
	 * {@link ContinuationManager}.
	 * <p>This works together with the scale that is configured through
	 * {@link #getContinuationPurgeScale}. The frequency divided by the scale
	 * makes how often the purging will happen. For instance, a frequency of 20
	 * and a scale of 1000 means that purging will happen 1/50th of the time.
	 *
	 * @return the continuation purge frequency
	 * @see #getContinuationPurgeScale
	 * @since 1.6
	 */
	public int getContinuationPurgeFrequency()
	{
        return DEFAULT_CONTINUATION_PURGE_FREQUENCY;
    }
	
	/**
	 * The scale that will be used to determine how often continuations purging
	 * will run in the {@link ContinuationManager}.
	 * <p>See {@link #getContinuationPurgeScale} for more info.
	 *
	 * @return the continuation purge scale
	 * @see #getContinuationPurgeFrequency
	 * @since 1.6
	 */
    public int getContinuationPurgeScale()
	{
        return DEFAULT_CONTINUATION_PURGE_SCALE;
    }
	
	/**
	 * Retrieves the {@code ContinuableObject} that corresponds to the currently
	 * executing object instance.
	 * <p> If you don't work with a seperate continuable support class
	 * ({@link ContinuationConfigInstrument#getContinuableSupportClassName see here})
	 * and don't allow people to just implement a marker interface without having
	 * to extend a base class, the associated continuable object is the same as
	 * the executing instance.
	 * <p>However, if there is a separate continuable support class, you'll need
	 * to return the appropriate continuable object here.
	 * 
	 * @param executingInstance the currently executing object instance
	 * @return the executing {@code ContinuableObject}
	 * @see ContinuationConfigInstrument#getContinuableSupportClassName
	 * @since 1.6
	 */
	public abstract T getAssociatedContinuableObject(Object executingInstance);
	
	/**
	 * Retrieves the manager that is responsible for the
	 * {@code ContinuableObject} that is currently executing.
	 *
	 * @param executingContinuable the currently executing continuable
	 * @return the corresponding manager
	 * @since 1.6
	 */
	public abstract ContinuationManager getContinuationManager(T executingContinuable);

	/**
	 * Indicates whether a continuable should be cloned before resuming the
	 * execution.
	 *
	 * @param executingContinuable the currently executing continuable
	 * @return {@code true} is the continuation should be cloned; or
	 * <p>{@code false} otherwise
	 * @since 1.6
	 */
	public abstract boolean cloneContinuations(T executingContinuable);
}

