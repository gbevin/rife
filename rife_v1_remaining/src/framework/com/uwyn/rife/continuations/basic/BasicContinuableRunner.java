/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BasicContinuableRunner.java 3928 2008-04-22 16:25:18Z gbevin $
 */
package com.uwyn.rife.continuations.basic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.uwyn.rife.continuations.*;
import com.uwyn.rife.continuations.exceptions.AnswerException;
import com.uwyn.rife.continuations.exceptions.CallException;
import com.uwyn.rife.continuations.exceptions.PauseException;
import com.uwyn.rife.continuations.exceptions.StepBackException;

/**
 * Basic implementation of a 'continuable runner' that will execute the
 * continuable objects and correctly handle the continuations-related
 * exceptions that are triggered.
 * <p>This runner is probably only applicable to the most simple of use-cases,
 * but by reading its source it should be relatively easy to adapt of extend
 * it for purposes that don't fall inside its scope.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3928 $
 * @since 1.6
 */
public class BasicContinuableRunner
{
	private final ClassLoader						mClassLoader;
	private final ContinuationConfigInstrument		mConfigInstrument;
	private final ContinuationManager				mManager;
	private final ThreadLocal<ContinuableObject>	mCurrentContinuable = new ThreadLocal<ContinuableObject>();

	private volatile CallTargetRetriever			mCallTargetRetriever = new ClassCallTargetRetriever();
	private volatile boolean						mCloneContinuations = true;

	/**
	 * Create a new runner instance.
	 *
	 * @param configInstrument the instance of the instrumentation
	 * configuration that will be used for the transformation
	 * @since 1.6
	 */
	public BasicContinuableRunner(ContinuationConfigInstrument configInstrument)
	{
		this(configInstrument, null);
	}

	/**
	 * Create a new runner instance with a custom classloader.
	 *
	 * @param configInstrument the instance of the instrumentation
	 * configuration that will be used for the transformation
	 * @param classloader the classloader that will be used to load the
	 * continuable classes, this is for example an instance of
	 * {@link BasicContinuableClassLoader}
	 * @since 1.6
	 */
	public BasicContinuableRunner(ContinuationConfigInstrument configInstrument, ClassLoader classloader)
	{
		mManager = new ContinuationManager(new BasicConfigRuntime());
		mConfigInstrument = configInstrument;
		if (null == classloader)
		{
			classloader = getClass().getClassLoader();
		}
		mClassLoader = classloader;
	}

	/**
	 * Starts the execution of a new instance of the provided class.
	 *
	 * @param className the name of the class that will be executed
	 * @return the ID of the resulting paused continuation; or
	 * <p>{@code null} if no continuation was paused
	 * @throws Throwable when an error occurs
	 * @since 1.6
	 */
	public String start(String className)
	throws Throwable
	{
		return run(className, null, null, null);		
	}
	
	/**
	 * Resumes the execution of a paused continuation.
	 *
	 * @param continuationId the ID of the continuation that will be resumed
	 * @return the ID of the resulting paused continuation; or
	 * <p>{@code null} if no continuation was paused or if the provided ID
	 * couldn't be found
	 * @throws Throwable when an error occurs
	 * @since 1.6
	 */
	public String resume(String continuationId)
	throws Throwable
	{		
		return run(null, continuationId, null, null);		
	}
	
	/**
	 * Resumes the execution of a call continuation.
	 *
	 * @param continuationId the ID of the continuation that will be resumed
	 * @param callAnswer the call answer object
	 * @return the ID of the resulting paused continuation; or
	 * <p>{@code null} if no continuation was paused or if the provided ID
	 * couldn't be found
	 * @throws Throwable when an error occurs
	 * @since 1.6.1
	 */
	public String answer(String continuationId, Object callAnswer)
	throws Throwable
	{		
		return run(null, continuationId, null, callAnswer);		
	}

	/**
	 * Executes a continuation whether it's paused or not. This is supposed to
	 * only be used for answer continuations.
	 *
	 * @param continuationId the ID of the existing continuation context that
	 * will be executed
	 * @return the ID of the resulting paused continuation; or
	 * <p>{@code null} if no continuation was paused or if the provided ID
	 * couldn't be found
	 * @throws Throwable when an error occurs
	 * @since 1.6
	 */
	public String run(String continuationId)
	throws Throwable
	{		
		return run(null, null, continuationId, null);		
	}
	
	private String run(String className, String resumeId, String runId, Object callAnswer)
	throws Throwable
	{
		// retrieve the current context classloader
		ClassLoader previous_context_classloader = Thread.currentThread().getContextClassLoader();
		
		String result = null;
		try
		{
			// set the continuations classloader as the context classloader
			Thread.currentThread().setContextClassLoader(mClassLoader);
			
			ContinuableObject object = null;
			boolean stepback = false;
			boolean call = false;
			boolean answer = false;
			do
			{
				try
				{
					try
					{
						try
						{
							// create or retrieve a continuable object
							if (null == object)
							{
								// no active continuation, start a new one
								if (null == resumeId &&
									null == runId)
								{
									// load the continuable class through the provided classloader
									Class continuableClass = mClassLoader.loadClass(className);
									object = (ContinuableObject)continuableClass.newInstance();
									ContinuationContext.clearActiveContext();
								}
								else
								{
									ContinuationContext context = null;
									
									// resume an existing continuation
									if (resumeId != null)
									{
										context = mManager.resumeContext(resumeId);
									}
									// run an existing continuation
									else if (runId != null)
									{
										context = mManager.getContext(runId);
									}
									
									// setup the context
									if (context != null)
									{
										if (callAnswer != null)
										{
											context.setCallAnswer(callAnswer);
										}
										ContinuationContext.setActiveContext(context);
										object = context.getContinuable();
									}
								}
							}
							
							// reset state variables
							resumeId = null;
							runId = null;
							callAnswer = null;
							stepback = false;
							call = false;
							answer = false;
							
							// execute the continuable object
							result = null;
							
							// setup the required threadlocal vars
							mCurrentContinuable.set(object);
							ContinuationConfigRuntime.setActiveConfigRuntime(mManager.getConfigRuntime());
							
							executeContinuable(object);
	
							// clear out the continuable object
							object = null;
						}
						finally
						{
							ContinuationContext.clearActiveContext();
						}
					}
					catch (InvocationTargetException invocation_target_exception)
					{
						throw invocation_target_exception.getTargetException();
					}
				}
				catch (PauseException e)
				{
					// register context
					ContinuationContext context = e.getContext();
					mManager.addContext(context);
					
					// obtain continuation ID
					result = context.getId();				
				}
				catch (StepBackException e)
				{
					stepback = true;
					
					// register context
					ContinuationContext context = e.getContext();
					mManager.addContext(context);
					
					resumeId = e.lookupStepBackId();
					if (resumeId != null)
					{
						// clear the continuable object so that it's looked up from the
						// grand parent continuation
						object = null;
					}
				}
				catch (CallException e)
				{
					call = true;
					
					// register context
					ContinuationContext context = e.getContext();
					mManager.addContext(context);
					
					// create a new call state
					CallState call_state = new CallState(context.getId(), null);
					context.setCreatedCallState(call_state);
					
					// create the new target object
					object = mCallTargetRetriever.getCallTarget(e.getTarget(), call_state);
				}
				catch (AnswerException e)
				{						
					// obtain the context and the answer of the answering element
					ContinuationContext context = e.getContext();
					
					// handle the call state of the last processed element context
					if (context != null &&
						context.getActiveCallState() != null)
					{
						answer = true;
						
						CallState call_state = context.getActiveCallState();
						callAnswer = e.getAnswer();
						runId = call_state.getContinuationId();
					}
					
					object = null;
				}
			}
			while (stepback || (call && object != null) || answer);
		}
		finally
		{
			// restore the previous context classloader
			Thread.currentThread().setContextClassLoader(previous_context_classloader);
		}
		
		return result;
	}

	/**
	 * Executes the continuable object by looking up the entrance method and
	 * invoking it.
	 * <p>This method can be overridden in case the default behavior isn't
	 * approrpiate.
	 *
	 * @param object the continuable that will be executed
	 * @throws Throwable when an unexpected error occurs
	 * @since 1.6.1
	 */
	public void executeContinuable(ContinuableObject object) throws Throwable
	{
		// lookup the method that will be used to execute the entrance of the continuable object
		beforeExecuteEntryMethodHook(object);
		Method method = object.getClass().getMethod(mConfigInstrument.getEntryMethodName(), mConfigInstrument.getEntryMethodArgumentTypes());
		method.invoke(object, (Object[])null);
	}

	/**
	 * Hook method that will be executed right before executing the entry
	 * method of a continuable object, when the default implementation of
	 * {@link #executeContinuable} is used.
	 * <p>This can for example be used to inject a continuable support object
	 * in case the main continuable class only implements the marker interface
	 * without having any of the support methods (see {@link ContinuationConfigInstrument#getContinuableSupportClassName()}).
	 *
	 * @param object the continuable object that will be executed
	 * @see #executeContinuable
	 * @since 1.6
	 */
	public void beforeExecuteEntryMethodHook(ContinuableObject object)
	{
	}

	/**
	 * Retrieves the instrumentation configuration that is used by this runner.
	 *
	 * @return this runner's instrumentation configuration
	 * @since 1.6
	 */
	public ContinuationConfigInstrument getConfigInstrumentation()
	{
		return mConfigInstrument;
	}

	/**
	 * Retrieves the classloader that is used by this runner.
	 *
	 * @return this runner's classloader
	 * @since 1.6
	 */
	public ClassLoader getClassLoader()
	{
		return mClassLoader;
	}

	/**
	 * Configures the runner to clone continuations or not.
	 *
	 * @param cloneContinuations {@code true} if continuations should be
	 * cloned when they are resumed; or
	 * <p>{@code false} if they should not be cloned
	 * @return this runner instance
	 * @see #setCloneContinuations
	 * @see #getCloneContinuations
	 * @since 1.6
	 */
	public BasicContinuableRunner cloneContinuations(boolean cloneContinuations)
	{
		setCloneContinuations(cloneContinuations);
		return this;
	}

	/**
	 * Configures the runner to clone continuations or not.
	 *
	 * @param cloneContinuations {@code true} if continuations should be
	 * cloned when they are resumed; or
	 * <p>{@code false} if they should not be cloned
	 * @see #cloneContinuations
	 * @see #getCloneContinuations
	 * @since 1.6
	 */
	public void setCloneContinuations(boolean cloneContinuations)
	{
		mCloneContinuations = cloneContinuations;
	}

	/**
	 * Indicates whether continuations should be cloned when they are resumed.
	 *
	 * @return {@code true} if continuations should be cloned when they are
	 * resumed; or
	 * <p>{@code false} if they should not be cloned
	 * @see #cloneContinuations
	 * @see #setCloneContinuations
	 * @since 1.6
	 */
	public boolean getCloneContinuations()
	{
		return mCloneContinuations;
	}
	
	/**
	 * Sets the call target retriever that will be used when a call
	 * continuation is triggered.
	 *
	 * @param callTargetRetriever the call target retriever that will be used
	 * @return this runner instance
	 * @see #setCallTargetRetriever
	 * @see #getCallTargetRetriever
	 * @since 1.6
	 */
	public BasicContinuableRunner callTargetRetriever(CallTargetRetriever callTargetRetriever)
	{
		setCallTargetRetriever(callTargetRetriever);
		return this;
	}

	/**
	 * Sets the call target retriever that will be used when a call
	 * continuation is triggered.
	 *
	 * @param callTargetRetriever the call target retriever that will be used
	 * @see #callTargetRetriever
	 * @see #getCallTargetRetriever
	 * @since 1.6
	 */
	public void setCallTargetRetriever(CallTargetRetriever callTargetRetriever)
	{
		mCallTargetRetriever = callTargetRetriever;
	}
	
	/**
	 * Retrieves the call target retriever that will be used when a call
	 * continuation is triggered.
	 *
	 * @return this runner's call target retriever
	 * @see #callTargetRetriever
	 * @see #setCallTargetRetriever
	 * @since 1.6
	 */
	public CallTargetRetriever getCallTargetRetriever()
	{
		return mCallTargetRetriever;
	}

	/**
	 * Retrieves the continuable that is active for the executing thread.
	 *
	 * @return this thread's continuable; or
	 * <p>{@code null} if there's no current continuable
	 * @since 1.6
	 */
	public ContinuableObject getCurrentContinuable()
	{
		return mCurrentContinuable.get();
	}

	/**
	 * Retrieves the manager that is used by the continuation runner.
	 *
	 * @return this runner's continuation manager
	 * @since 1.6
	 */
	public ContinuationManager getManager()
	{
		return mManager;
	}
	
	private class BasicConfigRuntime extends ContinuationConfigRuntime
	{
		public ContinuableObject getAssociatedContinuableObject(Object executingInstance)
		{
			return mCurrentContinuable.get();
		}
		
		public ContinuationManager getContinuationManager(ContinuableObject executingContinuable)
		{
			return mManager;
		}		
		
		public boolean cloneContinuations(ContinuableObject executingContinuable)
		{
			return mCloneContinuations;
		}
	}
}
