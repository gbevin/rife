/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContinuableLocalVariableUncloneableException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations.exceptions;

/**
 * Thrown when a local variable in a {@link com.uwyn.rife.continuations.ContinuationStack}
 * couldn't be cloned when a continuation is resumed.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public class ContinuableLocalVariableUncloneableException extends CloneNotSupportedException
{
	private static final long serialVersionUID = -6226277931198774505L;
	
	private Class	mContinuableClass = null;
	private String	mLocalVarType = null;

	/**
	 * Instantiates a new exception.
	 *
	 * @param continuableClass the class of the continuable that contains an
	 * unclonable local variable
	 * @param localVarType the type of the local variable
	 * @param cause the cause of the retrieval failure; or
	 * <p>{@code null} if there was no exception cause
	 * @since 1.6
	 */
	public ContinuableLocalVariableUncloneableException(Class continuableClass, String localVarType, Throwable cause)
	{
		super("The continuable with class name '" + continuableClass.getName() + "' uses a local method variable of type '" + localVarType + "' which is not cloneable.");
		
		initCause(cause);
		
		mContinuableClass = continuableClass;
		mLocalVarType = localVarType;
	}

	/**
	 * Retrieves the class of the continuable that contains an unclonable
	 * local variable.
	 *
	 * @return the class of the continuable
	 * @since 1.6
	 */
	public Class getContinuableClass()
	{
		return mContinuableClass;
	}

	/**
	 * The type of the local variable that can't be cloned.
	 *
	 * @return the type of the local variable
	 * @since 1.6
	 */
	public String getLocalVarType()
	{
		return mLocalVarType;
	}
}
