/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContinuationManager.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations;

import java.util.*;

import com.uwyn.rife.tools.TerracottaUtils;

/**
 * Manages a collection of {@code ContinuationContext} instances.
 * <p>A {@code ContinuationManager} instance is typically associated with
 * a specific context, like for example a {@link com.uwyn.rife.engine.Site}
 * for RIFE's web engine. It's up to you to provide an API to your users if
 * you want them to be able to interact with the appropriate continuations
 * manager. For instance, in RIFE, to gain access to the
 * {@code ContinuationManager} of an active
 * <code>{@link com.uwyn.rife.engine.ElementSupport}</code> instance, the
 * following code can be used: {@code getElementInfo().getSite().getContinuationManager()}.
 * Your application or library will have to provide its own.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see ContinuationManager
 * @since 1.6
 */
public class ContinuationManager<T extends ContinuableObject>
{
	private final Map<String, ContinuationContext<T>>	mContexts;
	private final Random  								mRandom = new Random();
	private final ContinuationConfigRuntime				mConfig;
	
	/**
	 * Instantiates a new continuation manager and uses the default values for
	 * the continuations duration and purging.
	 *
	 * @param config the runtime configuration that will be used be this
	 * manager
	 * @since 1.6
	 */
	public ContinuationManager(ContinuationConfigRuntime config)
	{
		mConfig = config;
		
		if (TerracottaUtils.isTcPresent())
		{
			mContexts = new HashMap<String, ContinuationContext<T>>();			
		}
		else
		{
			mContexts = new WeakHashMap<String, ContinuationContext<T>>();
		}
	}

	/**
	 * Retrieves the runtime configuration that was provided to the manager
	 * at instantiation.
	 *
	 * @return this manager's runtime configuration
	 * @since 1.6
	 */
	public ContinuationConfigRuntime getConfigRuntime()
	{
		return mConfig;
	}
	
	/**
	 * Checks if a particular continuation context is expired.
	 *
	 * @param context the context that needs to be verified
	 * @return {@code true} if the continuation context is expired; and
	 * <p>{@code false} otherwise
	 * @see com.uwyn.rife.config.RifeConfig.Engine#getContinuationDuration
	 * @since 1.6
	 */
	public boolean isExpired(ContinuationContext<T> context)
	{
		if (context.getStart() <= System.currentTimeMillis() - mConfig.getContinuationDuration())
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Adds a particular {@code ContinuationContext} to this manager.
	 *
	 * @param context the context that will be added
	 * @since 1.6
	 */
	public void addContext(ContinuationContext<T> context)
	{
		if (null == context)
		{
			return;
		}
		
		synchronized (mContexts)
		{
			mContexts.put(context.getId(), context);
		}
	}
	
	/**
	 * Removes a {@link ContinuationContext} instance from this continuation
	 * manager.
	 * 
	 * @param id the unique string that identifies the
	 * {@code ContinuationContext} instance that will be removed
	 * @see #getContext
	 * @since 1.6
	 */
	public void removeContext(String id)
	{
		if (null == id)
		{
			return;
		}
		
		synchronized (mContexts)
		{
			mContexts.remove(id);
		}
	}
	
	/**
	 * Creates a new {@code ContinuationContext} from an existing one so that
	 * the execution can be resumed.
	 * <p>If the existing continuation context couldn't be found, no new one
	 * can be created. However, if it could be found, the result of
	 * {@link ContinuationConfigRuntime#cloneContinuations} will determine
	 * whether the existing continuation context will be cloned to create
	 * the new one, or if its state will be reused.
	 * <p>The new continuation context will have its own unique ID.
	 *
	 * @param id the ID of the existing continuation context
	 * @return the new {@code ContinuationContext}; or
	 * <p>{@code null} if the existing continuation context couldn't be found
	 * @throws CloneNotSupportedException when the continuable couldn't be cloned
	 * @since 1.6
	 */
	public ContinuationContext<T> resumeContext(String id)
	throws CloneNotSupportedException
	{
		synchronized (mContexts)
		{
			ContinuationContext<T> result = null;
			
			purgeContinuations();
			
			ContinuationContext<T> context = getContext(id);
			if (context != null &&
				context.isPaused())
			{
				if (mConfig.cloneContinuations(context.getContinuable()))
				{
					result = cloneContext(context);
				}
				else
				{
					result = reuseContext(context);
				}
			}
			
			return result;
		}
	}
	
	/**
	 * Retrieves a {@link ContinuationContext} instance from this continuation
	 * manager.
	 * 
	 * @param id the unique string that identifies the
	 * {@code ContinuationContext} instance that has to be retrieved
	 * @return the {@code ContinuationContext} instance that corresponds
	 * to the provided identifier; or
	 * <p>{@code null} if the identifier isn't known by the continuation
	 * manager.
	 * @see #removeContext
	 * @since 1.6
	 */
	public ContinuationContext<T> getContext(String id)
	{
		ContinuationContext<T> context = mContexts.get(id);
		if (context != null)
		{
			if (isExpired(context))
			{
				context = null;
				removeContext(id);
			}
			else
			{
				// always set the manager of the continuation context, it could have been
				// cleared if the context was pulled in through Terracotta from another
				// node
				context.setManager(this);
			}
		}
		return context;
	}
	
	private ContinuationContext<T> reuseContext(ContinuationContext<T> context)
	{
		mContexts.remove(context.getId());
		context.resetId();
		addContext(context);
		
		return context;
	}
	
	private ContinuationContext<T> cloneContext(ContinuationContext<T> context)
	throws CloneNotSupportedException
	{
		ContinuationContext<T> new_context = context.clone();
		new_context.resetId();
		addContext(new_context);
		
		return new_context;
	}
	
	private void purgeContinuations()
	{
		int purge_decision = mRandom.nextInt(mConfig.getContinuationPurgeScale());
		if (purge_decision <= mConfig.getContinuationPurgeFrequency())
		{
			new PurgeContinuations().start();
		}
	}
	
	private class PurgeContinuations extends Thread
	{
		public void run()
		{
			purge();
		}
		
		private void purge()
		{
			ArrayList<String>   stale_continuations = new ArrayList<String>();
			try
			{
				ContinuationContext<T> context = null;
				for (ContinuationContext<T> reference : mContexts.values())
				{
					if (reference != null)
					{
						context = reference;
						if (context != null &&
							isExpired(context))
						{
							stale_continuations.add(context.getId());							
						}
					}
				}
			}
			catch (ConcurrentModificationException e)
			{
				// Oops something changed while we were looking.
				// Lock the context and try again.
				// Set our priority high while we have the sessions locked
				int old_priority = Thread.currentThread().getPriority();
				Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
				try
				{
					synchronized (mContexts)
					{
						stale_continuations = null;
						purge();
					}
				}
				finally
				{
					Thread.currentThread().setPriority(old_priority);
				}
			}
			
			if (stale_continuations != null)
			{
				synchronized (mContexts)
				{
					for (String id : stale_continuations)
					{
						mContexts.remove(id);
					}
				}
			}
		}
	}
}
