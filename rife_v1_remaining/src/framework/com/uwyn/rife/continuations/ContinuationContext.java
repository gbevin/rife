/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContinuationContext.java 3928 2008-04-22 16:25:18Z gbevin $
 */
package com.uwyn.rife.continuations;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.uwyn.rife.continuations.exceptions.ContinuableLocalVariableUncloneableException;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.JavaSpecificationUtils;
import com.uwyn.rife.tools.UniqueIDGenerator;

/**
 * Contains all contextual data of one particular continuation.
 * <p>It also provides some static retrieval methods to be able to access
 * active continuations.
 * <p>Active continuations are managed in a {@link ContinuationManager} so that
 * they can be easily retrieved.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3928 $
 * @see ContinuationManager
 * @since 1.6
 */
public class ContinuationContext<T extends ContinuableObject> implements Cloneable
{
	private final static transient ThreadLocal<ContinuationContext> 				ACTIVE_CONTEXT = new ThreadLocal<ContinuationContext>();
	private final static transient ThreadLocal<WeakReference<ContinuationContext>>	LAST_CONTEXT = new ThreadLocal<WeakReference<ContinuationContext>>();
	
	private transient ContinuationManager	mManager = null;
	
	private T					mContinuable = null;
	private CallState			mCreatedCallState = null;
	private CallState			mActiveCallState = null;
	private Object				mCallAnswer = null;
	private String				mId = null;
	private String				mParentId = null;
	private List<String>		mRelatedIds = null;
	private long				mStart = -1;
	
	private int					mLabel = -1;
	private boolean				mPaused = false;
	
	private ContinuationStack	mLocalVars = null;
	private ContinuationStack	mLocalStack = null;
	
	/**
	 * [PRIVATE AND UNSUPPORTED] Creates a new continuation context or resets
	 * its expiration time.
	 * <p>This method is used by the instrumented bytecode that provides
	 * continuations support, it's not intended for general use.
	 * 
	 * @return a new {@code ContinuationContext}, or the active one with
	 * its expiration time being reset
	 * @since 1.6
	 */
	public static ContinuationContext createOrResetContext(Object executingInstance)
	{
		ContinuationContext context = getActiveContext();
		if (null == context)
		{
			ContinuationConfigRuntime config = ContinuationConfigRuntime.getActiveConfigRuntime();
			ContinuableObject continuable = config.getAssociatedContinuableObject(executingInstance);
			context = new ContinuationContext(config.getContinuationManager(continuable), continuable);
			
			// check if the last continuation created a call continuation, in that case
			// pass the call state on to this new continuation
			ContinuationContext last_context = getLastContext();
			if (last_context != null)
			{
				CallState call_state = null;
				if (last_context.getCreatedCallState() != null)
				{
					call_state = last_context.getCreatedCallState();
				}
				else
				{
					call_state = last_context.getActiveCallState();
				}
				
				if (call_state != null)
				{
					context.setActiveCallState(call_state);
				}
			}
		}
		else
		{
			context.resetStart();
		}
		
		setActiveContext(context);
		
		// preserve a reference to the last executed continuation, so that it's
		// possible to detect call continuations
		LAST_CONTEXT.set(new WeakReference<ContinuationContext>(context));
		
		return context;
	}
	
	/**
	 * Clears the active currently continuation context for the executing thread.
	 * 
	 * @since 1.6
	 */
	public static void clearActiveContext()
	{
		if (JavaSpecificationUtils.isAtLeastJdk15()) {
			ACTIVE_CONTEXT.remove();
		} else {
			ACTIVE_CONTEXT.set(null);
		}
	}
	
	/**
	 * Retrieves the identifier of the currently active continuation for the
	 * current thread.
	 * 
	 * @return the identifier of the currently active continuation as a unique
	 * string; or
	 * <p>{@code null} if no continuation is currently active
	 * @see #getActiveContext
	 * @since 1.6
	 */
	public static String getActiveContextId()
	{
		ContinuationContext context = ACTIVE_CONTEXT.get();
		if (null == context)
		{
			return null;
		}
		return context.getId();
	}
	
	/**
	 * Retrieves the currently active continuation for the executing thread.
	 * 
	 * @return the currently active continuation; or
	 * <p>{@code null} if no continuation is currently active
	 * @see #getActiveContextId
	 * @since 1.6
	 */
	public static ContinuationContext getActiveContext()
	{
		return ACTIVE_CONTEXT.get();
	}
	
	/**
	 * Replaces the active continuation context for the executing thread.
	 * 
	 * @param context the new {@code ContinuationContext} that will be active; or
	 * {@code null} if no continuation context should be active
	 * @see #setActiveContext
	 * @since 1.6
	 */
	public static void setActiveContext(ContinuationContext context)
	{
		ACTIVE_CONTEXT.set(context);
	}
	
	/**
	 * Retrieves the last active continuation for the executing thread.
	 * 
	 * @return the last active continuation; or
	 * <p>{@code null} if no continuation was active
	 * @since 1.6
	 */
	public static ContinuationContext getLastContext()
	{
		WeakReference<ContinuationContext> reference = LAST_CONTEXT.get();
		if (reference != null)
		{
			return reference.get();
		}
		return null;
	}
	
	private ContinuationContext(ContinuationManager manager, T continuable)
	{
		mManager = manager;
		mContinuable = continuable;
			
		resetId();
		resetStart();

		mLabel = -1;

		mLocalVars = new ContinuationStack().initialize();
		mLocalStack = new ContinuationStack().initialize();
	}

	/**
	 * Retrieves the manager of this {@code ContinuationContext}.
	 *
	 * @return this continuation's manager instance
	 * @since 1.6
	 */
	public ContinuationManager getManager()
	{
		return mManager;
	}
	
	synchronized void setManager(ContinuationManager manager)
	{
		mManager = manager;
	}
	
	/**
	 * Registers this continuation in its manager, so that it can be retrieved later.
	 * @since 1.6
	 */
	public void registerContext()
	{
		synchronized (mManager)
		{
			mManager.addContext(this);
		}
	}
	
	/**
	 * Makes sure that this {@code ContinuationContext} is not the active
	 * one.
	 * 
	 * @since 1.6
	 */
	public void deactivate()
	{
		if (this == getActiveContext())
		{
			clearActiveContext();
		}
	}
	
	/**
	 * Removes this {@code ContinuationContext} instance from its {@link
	 * ContinuationManager}.
	 * 
	 * @since 1.6
	 */
	public synchronized void remove()
	{
		mManager.removeContext(mId);
		deactivate();
	}
	
	/**
	 * Removes the entire continuation tree that this
	 * {@code ContinuationContext} instance belongs to from its {@link
	 * ContinuationManager}.
	 * 
	 * @since 1.6
	 */
	public void removeContextTree()
	{
		synchronized (mManager)
		{
			mManager.removeContext(mId);
			
			if (mRelatedIds != null)
			{
				
				ContinuationContext child;
				for (String id : mRelatedIds)
				{
					child = mManager.getContext(id);
					if (child != null)
					{
						child.removeContextTree();
					}
				}
			}
			
			ContinuationContext parent = getParentContext();
			if (parent != null)
			{
				parent.removeContextTree();
			}
			
			deactivate();
		}
	}
	
	/**
	 * Retrieves the unique identifier of the parent continuation of this
	 * {@code ContinuationContext} instance.
	 * 
	 * @return the parent's identifier as a unique string; or
	 * <p>{@code null} if this {@code ContinuationContext} has no
	 * parent
	 * @see #getParentContext
	 * @since 1.6
	 */
	public String getParentContextId()
	{
		return mParentId;
	}
	
	/**
	 * Retrieves the parent {@code ContinuationContext} of this
	 * {@code ContinuationContext} instance.
	 * 
	 * @return the parent {@code ContinuationContext}; or
	 * <p>{@code null} if this {@code ContinuationContext} has no
	 * parent
	 * @see #getParentContextId
	 * @since 1.6
	 */
	public ContinuationContext getParentContext()
	{
		return mManager.getContext(getParentContextId());
	}
	
	/**
	 * Retrieves the answer that the call continuation stored in this context.
	 * 
	 * @return the call continuation's answer; or
	 * <p>{@code null} if no answer was provided or the corresponding
	 * continuation wasn't a call continuation
	 * @since 1.6
	 */
	public Object getCallAnswer()
	{
		return mCallAnswer;
	}
	
	/**
	 * [PRIVATE AND UNSUPPORTED] Sets whether the continuation if paused.
	 * <p>This method is used by the internals that provide continuations
	 * support, it's not intended for general use.
	 * 
	 * @param paused {@code true} if the continuation is paused; or
	 * <p>{@code false} otherwise
	 * @see #isPaused()
	 * @since 1.6
	 */
	public synchronized void setPaused(boolean paused)
	{
		mPaused = paused;
	}
	
	/**
	 * Indicates whether this continuation is actually paused and can be resumed.
	 * 
	 * @return {@code true} if the continuation is paused; or
	 * <p>{@code false} otherwise
	 * @since 1.6
	 */
	public boolean isPaused()
	{
		return mPaused;
	}
	
	/**
	 * [PRIVATE AND UNSUPPORTED] Set the number of the bytecode label where
	 * the continuation has to resume execution from.
	 * <p>This method is used by the instrumented bytecode that provides
	 * continuations support, it's not intended for general use.
	 * 
	 * @param label the number of the resumed bytecode label
	 * @since 1.6
	 */
	public synchronized void setLabel(int label)
	{
		mLabel = label;
	}

	/**
	 * [PRIVATE AND UNSUPPORTED] Set the number of the bytecode label where
	 * the continuation has to resume execution from.
	 * <p>This method is used by the instrumented bytecode that provides
	 * continuations support, it's not intended for general use.
	 *
	 * @since 1.6.2
	 */
	public void clearLabel()
	{
		setLabel(-1);
	}

	/**
	 * [PRIVATE AND UNSUPPORTED] Retrieves the number of the bytecode label
	 * where the continuation has to resume execution from.
	 * <p>This method is used by the instrumented bytecode that provides
	 * continuations support, it's not intended for general use.
	 * 
	 * @return the number of the resumed bytecode label; or
	 * <p>{@code -1} if no label number has been set
	 * @since 1.6
	 */
	public int getLabel()
	{
		return mLabel;
	}
	
	/**
	 * [PRIVATE AND UNSUPPORTED] Retrieves the local variable stack of this
	 * continuation.
	 * <p>This method is used by the instrumented bytecode that provides
	 * continuations support, it's not intended for general use.
	 * 
	 * @return this continuation's local variable stack
	 * @since 1.6
	 */
	public ContinuationStack getLocalVars()
	{
		return mLocalVars;
	}
	
	/**
	 * [PRIVATE AND UNSUPPORTED] Retrieves the local operand stack of this
	 * continuation.
	 * <p>This method is used by the instrumented bytecode that provides
	 * continuations support, it's not intended for general use.
	 * 
	 * @return this continuation's local operand stack
	 * @since 1.6
	 */
	public ContinuationStack getLocalStack()
	{
		return mLocalStack;
	}
	
	private synchronized void resetStart()
	{
		mStart = System.currentTimeMillis();
	}
	
	synchronized void resetId()
	{
		mId = UniqueIDGenerator.generate().toString();
	}

	/**
	 * Retrieves the continuation ID.
	 * <p>Note that this ID is not necessarily present in the manager and that
	 * trying to retrieve a continuation afterwards from its ID is never
	 * guaranteed to give a result.
	 *
	 * @return the unique ID of this continuation.
	 * @since 1.6
	 */
	public String getId()
	{
		return mId;
	}

	/**
	 * Retrieves the ID of this continuation's parent.
	 *
	 * @return the ID of this continuation's parent continuation; or
	 * <p>{@code null} if this continuation has no parent.
	 * @since 1.6
	 */
	public String getParentId()
	{
		return mParentId;
	}
	
	/**
	 * [PRIVATE AND UNSUPPORTED] Associates the ID of another continuation to
	 * this continuation.
	 * <p>This method is used by the instrumented bytecode that provides
	 * continuations support, it's not intended for general use.
	 *
	 * @param id the ID of another continuation that's related to this
	 * continuation
	 * @since 1.6
	 */
	public synchronized void addRelatedId(String id)
	{
		if (null == mRelatedIds)
		{
			mRelatedIds = new ArrayList<String>();
		}
		mRelatedIds.add(id);
	}
	
	/**
	 * [PRIVATE AND UNSUPPORTED] Set the ID of this continuation's parent.
	 * <p>This method is used by the instrumented bytecode that provides
	 * continuations support, it's not intended for general use.
	 *
	 * @param id the ID of this continuation's parent
	 * @see #getParentId()
	 * @since 1.6
	 */
	public synchronized void setParentId(String id)
	{
		mParentId = id;
	}

	/**
	 * Returns the object instance in which this continuation was executing.
	 *
	 * @return this continuation's active object
	 * @since 1.6
	 */
	public T getContinuable()
	{
		return mContinuable;
	}

	/**
	 * Sets the call continuation's state when a new call continuation is
	 * created.
	 * <p>This state initiates a call continuation and should be set when
	 * a new call happens, after that it should never be changed.
	 *
	 * @param createdCallState this call continuation's creation state
	 * @see #getCreatedCallState()
	 * @since 1.6
	 */
	public synchronized void setCreatedCallState(CallState createdCallState)
	{
		mCreatedCallState = createdCallState;
	}

	/**
	 * Retrieves this continuation's call continuation creation state.
	 * <p>If this returns a non-null value, you can detect from it that this
	 * was a call continuation.
	 *
	 * @return this continuation
	 * @see #setCreatedCallState(CallState)
	 * @since 1.6
	 */
	public CallState getCreatedCallState()
	{
		return mCreatedCallState;
	}

	/**
	 * Sets the active call state for this continuation.
	 * <p>This mainly passes on the call state that was created during a call
	 * continuation. It allows quick retrieval of the active call state when
	 * an answer occurs.
	 *
	 * @param callState the active call state
	 * @see #setCreatedCallState(CallState) 
	 * @since 1.6
	 */
	public synchronized void setActiveCallState(CallState callState)
	{
		mActiveCallState = callState;
	}

	/**
	 * Retrieves the call state that is active during this continuation.
	 *
	 * @return the active {@code CallState}; or
	 * <p>{@code null} if no call state was active for this continuation
	 */
	public CallState getActiveCallState()
	{
		return mActiveCallState;
	}
	
	long getStart()
	{
		return mStart;
	}
	
	/**
	 * Set the answer to a call continuation.
	 * 
	 * @param answer the object that will be the call continuation's answer; or
	 * {@code null} if there was no answer
	 * @since 1.6
	 */
	public synchronized void setCallAnswer(Object answer)
	{
		mCallAnswer = answer;
	}
	
	/**
	 * [PRIVATE AND UNSUPPORTED] Creates a cloned instance of this
	 * continuation context, this clone is not a perfect copy but is intended
	 * to be a child continuation and all context data is setup for that.
	 * <p>This method is used by the instrumented bytecode that provides
	 * continuations support, it's not intended for general use.
	 * 
	 * @return a clone of this continuation for use as a child continuation
	 * @since 1.6
	 */
	public ContinuationContext clone()
	throws CloneNotSupportedException
	{
		ContinuationContext new_continuationcontext = null;
		try
		{
			new_continuationcontext = (ContinuationContext)super.clone();
		}
		///CLOVER:OFF
		catch (CloneNotSupportedException e)
		{
			// this should never happen
			Logger.getLogger("com.uwyn.rife.continuations").severe(ExceptionUtils.getExceptionStackTrace(e));
		}
		///CLOVER:ON
		
		new_continuationcontext.mContinuable = (ContinuableObject)mContinuable.clone();
		new_continuationcontext.mCallAnswer = null;
		
		new_continuationcontext.mId = UniqueIDGenerator.generate().toString();
		new_continuationcontext.mParentId = mId;
		new_continuationcontext.mPaused = false;
		addRelatedId(new_continuationcontext.mId);
		
		try
		{
			new_continuationcontext.mLocalVars = mLocalVars.clone(new_continuationcontext.mContinuable);
			new_continuationcontext.mLocalStack = mLocalStack.clone(new_continuationcontext.mContinuable);
		}
		catch (CloneNotSupportedException e)
		{
			throw new ContinuableLocalVariableUncloneableException(mContinuable.getClass(), e.getMessage(), e);
		}

		return new_continuationcontext;
	}
}
