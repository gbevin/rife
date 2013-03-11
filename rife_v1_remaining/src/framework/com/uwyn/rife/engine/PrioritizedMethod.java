/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PrioritizedMethod.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import java.lang.reflect.Method;
import java.util.Arrays;

class PrioritizedMethod implements Comparable
{
	private Method	mMethod = null;
	private int[]	mPriority = null;
	
	PrioritizedMethod(Method method, int[] priority)
	{
		assert method != null;
		
		mMethod = method;
		mPriority = priority;
	}
	
	public Method getMethod()
	{
		return mMethod;
	}
	
	public int[] getPriority()
	{
		return mPriority;
	}
	
	public int compareTo(Object other)
	{
		if (null == other ||
			!(other instanceof PrioritizedMethod))
		{
			return 0;
		}
		
		PrioritizedMethod other_method = (PrioritizedMethod)other;
		int[] other_priority = other_method.mPriority;
		if (null == other_priority && mPriority != null)
		{
			return 1;
		}
		else if (null == mPriority && other_priority != null)
		{
			return -1;
		}
		else if (null == mPriority && null == other_priority)
		{
			return mMethod.getName().compareTo(other_method.getMethod().getName());
		}
		else
		{
			int position = 0;
			while (position < mPriority.length ||
				   position < other_priority.length)
			{
				if (position >= mPriority.length &&
					position < other_priority.length)
				{
					return -1;
				}
				else if (position < mPriority.length &&
						 position >= other_priority.length)
				{
					return 1;
				}
				else
				{
					if (mPriority[position] < other_priority[position])
					{
						return -1;
					}
					else if (mPriority[position] > other_priority[position])
					{
						return 1;
					}
				}
				position++;
			}
			
			return mMethod.getName().compareTo(other_method.getMethod().getName());
		}
	}
	
	public boolean equals(Object other)
	{
		if (null == other ||
			!(other instanceof PrioritizedMethod))
		{
			return false;
		}
		
		if (other == this)
		{
			return true;
		}
		
		PrioritizedMethod other_method = (PrioritizedMethod)other;
		int[] other_priority = other_method.mPriority;
		if (null == other_priority && mPriority != null ||
			null == mPriority && other_priority != null)
		{
			return false;
		}
		else if (null == other_priority && null == mPriority)
		{
			return mMethod.getName().equals(other_method.getMethod().getName());
		}
		else
		{
			return Arrays.equals(mPriority, other_priority);
		}
	}

	public int hashCode()
	{
		int result = 0;

		if (mMethod != null)
		{
			result = mMethod.hashCode();
		}

		if (mPriority != null)
		{
			result = mPriority.hashCode();
		}

		return result;
	}
}

