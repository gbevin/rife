/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: VirtualParameters.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database;

import java.util.HashMap;
import java.util.Map;

import com.uwyn.rife.database.queries.Query;
import com.uwyn.rife.database.queries.QueryParameters;

/**
 * Internal class to handle virtual parameters of a
 * <code>DbPreparedStatement</code>.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public class VirtualParameters
{
	private QueryParameters				mParameters = null;
	private Map<Integer, Integer>		mIndexMapping = null;
	private Map<Integer, Object>		mValues = null;
	private VirtualParametersHandler	mHandler = null;

	/**
	 * Creates a new <code>VirtualParameters</code> instance.
	 *
	 * @param parameters the actual parameters that are virtual.
	 * @param handler the <code>VirtualParametersHandler</code> that will
	 * be used by the {@link #callHandler(DbPreparedStatement)} method.
	 * @since 1.0
	 */
	public VirtualParameters(QueryParameters parameters, VirtualParametersHandler handler)
	{
		if (null == parameters) throw new IllegalArgumentException("parameters can't be null.");
		if (null == handler)    throw new IllegalArgumentException("handler can't be null.");

		mParameters = parameters;
		mHandler = handler;
	}
	
	/**
	 * Calls the registered <code>VirtualParametersHandler</code>. This is
	 * typically called when all virtual parameters have been defined in a
	 * prepared statement and the statement is ready to be executed.
	 *
	 * @param statement the prepared statement that has all the virtual
	 * parameters defined.
	 * @since 1.0
	 */
	public void callHandler(DbPreparedStatement statement)
	{
		mHandler.handleValues(statement);
	}
	
	void setup(Query query)
	{
		mIndexMapping = query.getParameters().getVirtualIndexMapping(mParameters);
	}
	
	Object getValue(int index)
	{
		if (null == mValues)
		{
			return null;
		}
		
		return mValues.get(index);
	}
	
	boolean hasValue(int index)
	{
		if (null == mValues)
		{
			return false;
		}
		
		return mValues.containsKey(index);
	}
	
	boolean hasParameter(int index)
	{
		if (null == mIndexMapping)
		{
			return false;
		}
		
		return mIndexMapping.containsKey(index);
	}
	
	int getRealIndex(int index)
	{
		if (null == mIndexMapping)
		{
			return -1;
		}
		
		return mIndexMapping.get(index);
	}
	
	void putValue(int index, Object value)
	{
		if (null == mValues)
		{
			mValues = new HashMap<Integer, Object>();
		}
		
		mValues.put(index, value);
	}
}
