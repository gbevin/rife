/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContentDataUser.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam;

/**
 * By extending this class it's possible to provide the logic that should be
 * executed by methods that allow interaction with content data.
 * <p>This class has both a default constructor and one that can take a data
 * object. This can be handy when using it as an extending anonymous inner
 * class when you need to use variables inside the inner class that are
 * cumbersome to change to <code>final</code> in the enclosing class.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import java.util.logging.Logger;

public abstract class ContentDataUser<ResultType, DataType> implements Cloneable
{
	protected DataType  mData = null;
	
	/**
	 * Creates a new instance.
	 * 
	 * @since 1.0
	 */
	public ContentDataUser()
	{
	}
	
	/**
	 * Creates a new instance with a data object.
	 * 
	 * @since 1.0
	 */
	public ContentDataUser(DataType data)
	{
		mData = data;
	}
	
	/**
	 * Retrieves the data object that was provided through the constructor.
	 * 
	 * @return this intance's data object
	 * @since 1.0
	 */
	public DataType getData()
	{
		return mData;
	}
	
	/**
	 * Calling this method makes it possible to throw a checked exception from
	 * within this class.
	 * <p>To catch it you should surround the using method with a
	 * <code>try-catch</code> block that catching
	 * <code>InnerClassException</code>. The original exception is then
	 * available through <code>getCause()</code> and can for example be
	 * rethrown.
	 * 
	 * @exception InnerClassException when a checked exception needs to be
	 * thrown from within this class and caught outside the caller.
	 * @since 1.0
	 */
	public void throwException(Exception exception)
	throws InnerClassException
	{
		throw new InnerClassException(exception);
	}
	
	/**
	 * Should be implemented by all extending classes.
	 * 
	 * @since 1.0
	 */
	public abstract ResultType useContentData(Object contentData) throws InnerClassException;

	/**
	 * Simply clones the instance with the default clone method since this
	 * class contains no member variables.
	 * 
	 * @since 1.0
	 */
	public ContentDataUser<ResultType, DataType> clone()
	{
		try
		{
			return (ContentDataUser<ResultType, DataType>)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			///CLOVER:OFF
			// this should never happen
			Logger.getLogger("com.uwyn.rife.cmf").severe(ExceptionUtils.getExceptionStackTrace(e));
			return null;
			///CLOVER:ON
		}
	}
}

