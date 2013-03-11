/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContinuationStack.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations;

import com.uwyn.rife.continuations.instrument.ContinuationDebug;
import com.uwyn.rife.tools.ObjectUtils;
import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * [PRIVATE AND UNSUPPORTED] Contains the local state of a continuation.
 * <p>This needs to be publicly accessible for the instrumented code to be
 * able to interact with it, but it's not supposed to be used directly.
 *
 * @since 1.6
 */
public class ContinuationStack
{
	static final int	NONE = 0;
	static final int	INTEGER = 1;
	static final int	LONG = 2;
	static final int	FLOAT = 3;
	static final int	DOUBLE = 4;
	static final int	REFERENCE = 5;
	
	private int[]	mPositionMapping = null;
	private int[]	mTypeMapping = null;
	private int		mStackHeight = 0;
	
	private int[]		mIntStack = null;
	private long[]		mLongStack = null;
	private float[]		mFloatStack = null;
	private double[]	mDoubleStack = null;
	private Object[]	mReferenceStack = null;
	
	private int	mIntTop = 0;
	private int	mLongTop = 0;
	private int	mDoubleTop = 0;
	private int	mFloatTop = 0;
	private int	mReferenceTop = 0;
	
	ContinuationStack()
	{
	}
	
	ContinuationStack initialize()
	{
		mIntStack = new int[10];
		mLongStack = new long[5];
		mFloatStack = new float[5];
		mDoubleStack = new double[5];
		mReferenceStack = new Object[5];
		
		mPositionMapping = new int[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		mTypeMapping = new int[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		
		return this;
	}
	
	public synchronized int getType(int index)
	{
		if (index <= mTypeMapping.length-1)
		{
			return mTypeMapping[index];
		}
		return NONE;	
	}
	
	public synchronized int popInt()
	{
		return getInt(--mStackHeight);
	}
	
	public synchronized long popLong()
	{
		return getLong(--mStackHeight);
	}
	
	public synchronized float popFloat()
	{
		return getFloat(--mStackHeight);
	}
	
	public synchronized double popDouble()
	{
		return getDouble(--mStackHeight);
	}
	
	public synchronized Object popReference()
	{
		return getReference(--mStackHeight);
	}
	
	public synchronized int getInt(int index)
	{
		int position = mPositionMapping[index];
		if (-1 == position ||
			position >= mIntStack.length)
		{
			return 0;
		}
		return mIntStack[position];
	}
	
	public synchronized long getLong(int index)
	{
		int position = mPositionMapping[index];
		if (-1 == position ||
			position >= mLongStack.length)
		{
			return 0L;
		}
		return mLongStack[position];
	}
	
	public synchronized float getFloat(int index)
	{
		int position = mPositionMapping[index];
		if (-1 == position ||
			position >= mFloatStack.length)
		{
			return 0f;
		}
		return mFloatStack[position];
	}
	
	public synchronized double getDouble(int index)
	{
		int position = mPositionMapping[index];
		if (-1 == position ||
			position >= mDoubleStack.length)
		{
			return 0d;
		}
		return mDoubleStack[position];
	}
	
	public synchronized Object getReference(int index)
	{
		int position = mPositionMapping[index];
		if (-1 == position ||
			position >= mReferenceStack.length)
		{
			return null;
		}
		return mReferenceStack[position];
	}
	
	private synchronized void storeIndex(int index, int position, int type)
	{
		if (index > mPositionMapping.length-1)
		{
			int		size = (((index+1) /10)+1)*10;
			int[]	new_positionmapping = new int[size];
			int[]	new_typemapping = new int[size];
			Arrays.fill(new_positionmapping, mPositionMapping.length, new_positionmapping.length, -1);
			Arrays.fill(new_typemapping, mTypeMapping.length, new_typemapping.length, -1);
			System.arraycopy(mPositionMapping, 0, new_positionmapping, 0, mPositionMapping.length);
			System.arraycopy(mTypeMapping, 0, new_typemapping, 0, mTypeMapping.length);
			mPositionMapping = new_positionmapping;
			mTypeMapping = new_typemapping;
		}
		mPositionMapping[index] = position;
		mTypeMapping[index] = type;
	}
	
	public synchronized void incrementInt(int index, int increment)
	{
		int position = -1;
		
		position = mPositionMapping[index];
		mIntStack[position] += increment;
	}
	
	public synchronized void pushInt(int value)
	{
		storeInt(mStackHeight++, value);
	}
	
	public synchronized void pushLong(long value)
	{
		storeLong(mStackHeight++, value);
	}
	
	public synchronized void pushFloat(float value)
	{
		storeFloat(mStackHeight++, value);
	}
	
	public synchronized void pushDouble(double value)
	{
		storeDouble(mStackHeight++, value);
	}
	
	public synchronized void pushReference(Object value)
	{
		storeReference(mStackHeight++, value);
	}
	
	public synchronized void storeInt(int index, int value)
	{
		int position = -1;
		
		if (getType(index) != INTEGER)
		{
			position = mIntTop++;

			storeIndex(index, position, INTEGER);
			
			if (position > mIntStack.length-1)
			{
				int		size = (((position+1) /10)+1)*10;
				int[]	new_stack = new int[size];
				System.arraycopy(mIntStack, 0, new_stack, 0, mIntStack.length);
				mIntStack = new_stack;
			}
		}
		else
		{
			position = mPositionMapping[index];
		}
		
		mIntStack[position] = value;
	}
	
	public synchronized void storeLong(int index, long value)
	{
		int position = -1;
		
		if (getType(index) != LONG)
		{
			position = mLongTop++;

			storeIndex(index, position, LONG);
			
			if (position > mLongStack.length-1)
			{
				int		size = (((position+1) /10)+1)*10;
				long[]	new_stack = new long[size];
				System.arraycopy(mLongStack, 0, new_stack, 0, mLongStack.length);
				mLongStack = new_stack;
			}
		}
		else
		{
			position = mPositionMapping[index];
		}
		
		mLongStack[position] = value;
	}
	
	public synchronized void storeFloat(int index, float value)
	{
		int position = -1;
		
		if (getType(index) != FLOAT)
		{
			position = mFloatTop++;

			storeIndex(index, position, FLOAT);
			
			if (position > mFloatStack.length-1)
			{
				int		size = (((position+1) /10)+1)*10;
				float[]	new_stack = new float[size];
				System.arraycopy(mFloatStack, 0, new_stack, 0, mFloatStack.length);
				mFloatStack = new_stack;
			}
		}
		else
		{
			position = mPositionMapping[index];
		}
		
		mFloatStack[position] = value;
	}
	
	public synchronized void storeDouble(int index, double value)
	{
		int position = -1;
		
		if (getType(index) != DOUBLE)
		{
			position = mDoubleTop++;

			storeIndex(index, position, DOUBLE);
			
			if (position > mDoubleStack.length-1)
			{
				int		size = (((position+1) /10)+1)*10;
				double[]	new_stack = new double[size];
				System.arraycopy(mDoubleStack, 0, new_stack, 0, mDoubleStack.length);
				mDoubleStack = new_stack;
			}
		}
		else
		{
			position = mPositionMapping[index];
		}
		
		mDoubleStack[position] = value;
	}
	
	public synchronized void storeReference(int index, Object value)
	{
		int position = -1;
		
		if (getType(index) != REFERENCE)
		{
			position = mReferenceTop++;

			storeIndex(index, position, REFERENCE);
			
			if (position > mReferenceStack.length-1)
			{
				int			size = (((position+1) /10)+1)*10;
				Object[]	new_stack = new Object[size];
				System.arraycopy(mReferenceStack, 0, new_stack, 0, mReferenceStack.length);
				mReferenceStack = new_stack;
			}
		}
		else
		{
			position = mPositionMapping[index];
		}
		
		mReferenceStack[position] = value;
	}
	
	///CLOVER:OFF
	public synchronized void outputState()
	{
		ContinuationDebug.LOGGER.finest("");
		ContinuationDebug.LOGGER.finest("STACK : "+this);
		ContinuationDebug.LOGGER.finest("mPositionMapping["+mPositionMapping.length+"] = "+join(mPositionMapping, ","));
		ContinuationDebug.LOGGER.finest("mTypeMapping["+mTypeMapping.length+"]     = "+join(mTypeMapping, ","));
		ContinuationDebug.LOGGER.finest("mIntStack["+mIntStack.length+"]        = "+join(mIntStack, ","));
		ContinuationDebug.LOGGER.finest("mLongStack["+mLongStack.length+"]        = "+join(mLongStack, ","));
		ContinuationDebug.LOGGER.finest("mFloatStack["+mFloatStack.length+"]       = "+join(mFloatStack, ","));
		ContinuationDebug.LOGGER.finest("mDoubleStack["+mDoubleStack.length+"]      = "+join(mDoubleStack, ","));
		ContinuationDebug.LOGGER.finest("mReferenceStack["+mReferenceStack.length+"]   = "+join(mReferenceStack, ","));
	}
	
	// adding a join method here to remove a viral dependency on the StringUtils class
	private static String join(Object array, String separator)
	{
		if (null == array)
		{
			return "";
		}
		
		if (!array.getClass().isArray())
		{
			return String.valueOf(array);
		}
		
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < Array.getLength(array); i++)
		{
			if (result.length() > 0)
			{
				result.append(separator);
			}
			
			result.append(Array.get(array, i));
		}
		
		return result.toString();
	}
	///CLOVER:ON
	
	public synchronized ContinuationStack clone(Object elementInstance)
	throws CloneNotSupportedException
	{
        ContinuationStack new_stack = new ContinuationStack();
		
		new_stack.mPositionMapping = mPositionMapping.clone();
		new_stack.mTypeMapping = mTypeMapping.clone();
		new_stack.mStackHeight = mStackHeight;
		
		new_stack.mIntStack = mIntStack.clone();
		new_stack.mLongStack = mLongStack.clone();
		new_stack.mFloatStack = mFloatStack.clone();
		new_stack.mDoubleStack = mDoubleStack.clone();
		new_stack.mReferenceStack = new Object[mReferenceStack.length];
		for (int i = 0; i < mReferenceStack.length; i++)
		{
			if (mReferenceStack[i] != null &&
				mReferenceStack[i].getClass() == elementInstance.getClass())
			{
				new_stack.mReferenceStack[i] = elementInstance;
			}
			else
			{
				new_stack.mReferenceStack[i] = ObjectUtils.deepClone(mReferenceStack[i]);
			}
		}
		
		new_stack.mIntTop = mIntTop;
		new_stack.mLongTop = mLongTop;
		new_stack.mDoubleTop = mDoubleTop;
		new_stack.mFloatTop = mFloatTop;
		new_stack.mReferenceTop = mReferenceTop;
		
		return new_stack;
	}
}
