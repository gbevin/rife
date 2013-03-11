/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContinuationConfigInstrument.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations;

/**
 * This interface needs to be implemented to configure the bytecode
 * instrumentation that enables the continuations functionalities.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6

 */
public interface ContinuationConfigInstrument
{
	/**
	 * The name of the interface that will indicate that a class should be
	 * instrumented for continuations functionalities, for instance
	 * {@code ContinuableObject.class.getName()}.
	 *
	 * @return the name of the marker interface
	 * @since 1.6
	 */
    public String getContinuableMarkerInterfaceName();
	
	/**
	 * The class name of the support class that contains dummy implementations
	 * of the continuation methods that are configured below, for instance
	 * {@code ContinuableSupport.class.getName()}.
	 * <p>If you implement these methods in your continuable classes or extend
	 * these classes from a common base class with those methods that are then
	 * called locally, this configuration can return {@code null} since it
	 * will not be used. A class name only needs to be provided if your
	 * continuable classes only implement the marker interface, and you call
	 * the continuation methods on an instance of this support inside your
	 * continuations logic.
	 *
	 * @return the name of the continuable support class; or
	 * <p>{@code null} if such a support class isn't used
	 * @since 1.6
	 */
    public String getContinuableSupportClassName();
	
	/**
	 * The name of the entry method that will be invoked when a new instance
	 * of a continuable class is created and its execution is started, for
	 * instance {@code "execute"}.
	 *
	 * @return the name of the entry method
	 * @since 1.6
	 */
    public String getEntryMethodName();
	
	/**
	 * The return type of the entry method, for instance
	 * {@code void.class}.
	 * <p>This will solely be used to detect and lookup the method before
	 * instrumenting or calling it.
	 *
	 * @return the class of the entry method's return value
	 * @since 1.6
	 */
    public Class getEntryMethodReturnType();
	
	/**
	 * The array argument types that the entry method takes, for instance
	 * {@code null} if it takes none.
	 * <p>This will solely be used to detect and lookup the method before
	 * instrumenting or calling it.
	 *
	 * @return the array of argument types of the entry method; or
	 * {@code null} if there are none
	 * @since 1.6
	 */
    public Class[] getEntryMethodArgumentTypes();
	
	/**
	 * The name of the method that will trigger a pause continuation, for
	 * instance {@code "pause"}.
	 * <p>This method should have a {@code void} return type and take no
	 * arguments.
	 *
	 * @return the name of the pause method
	 * @since 1.6
	 */
    public String getPauseMethodName();
	
	/**
	 * The name of the method that will trigger a step-back continuation, for
	 * instance {@code "stepback"}.
	 * <p>This method should have a {@code void} return type and take no
	 * arguments.
	 *
	 * @return the name of the step-back method
	 * @since 1.6
	 */
    public String getStepbackMethodName();
	
	/**
	 * The name of the method that will trigger a call continuation, for
	 * instance {@code "call"}.
	 *
	 * @return the name of the call method
	 * @since 1.6
	 */
    public String getCallMethodName();
	
	/**
	 * The return type of the call method, for instance {@code Object.class}.
	 * <p>This needs to be an object, not a primitive and you have to be
	 * certain that it's compatible with the values that are sent through the
	 * answer to the call continuation. It's just recommended to keep this as
	 * generic as possible (hence {@code Object.class}).
	 *
	 * @return the type of the call method's return value
	 * @since 1.6
	 */
    public Class getCallMethodReturnType();
	
	/**
	 * The array argument types that the call method takes, for instance
	 * {@code new Class[] {String.class}}.
	 * <p>This needs to be a single object argument, not more or less than one,
	 * and not a primitive. You will use this yourself in the implementation
	 * of the runner that executes the continuations. If the
	 * {@link com.uwyn.rife.continuations.basic.BasicContinuableRunner} is
	 * used, {@link com.uwyn.rife.continuations.basic.CallTargetRetriever} will
	 * be used to resolve the target of the call continuation by using the
	 * what's provided as the argument of the method call.
	 *
	 * @return the array of argument types of the call method
	 * @since 1.6
	 */
    public Class[] getCallMethodArgumentTypes();
	
	/**
	 * The name of the method that will trigger the answer to a call
	 * continuation, for instance {@code "answer"}.
	 * <p>This method should have a {@code void} return type and take one
	 * argument with the type {@code java.lang.Object}.
	 *
	 * @return the name of the answer method
	 * @since 1.6
	 */
    public String getAnswerMethodName();
}

