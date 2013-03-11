/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CallbacksProviderBean.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic.beans;

import java.util.ArrayList;
import java.util.List;

import com.uwyn.rife.database.querymanagers.generic.Callbacks;
import com.uwyn.rife.database.querymanagers.generic.CallbacksProvider;

public class CallbacksProviderBean implements CallbacksProvider<CallbacksProviderBean>
{
	private int 	mId = -1;
	private String 	mTestString = null;
	
	static TheCallbacks mCallbacks = new TheCallbacks();
	
	public CallbacksProviderBean()
	{
	}
	
	public void setId(int id)
	{
		mId = id;
	}
	
	public int getId()
	{
		return mId;
	}
	
	public Callbacks<CallbacksProviderBean> getCallbacks()
	{
		return mCallbacks;
	}
	
	public void setTestString(String testString)
	{
		this.mTestString = testString;
	}
	
	public String getTestString()
	{
		return mTestString;
	}
	
	public String toString()
	{
		return mId + ";" + mTestString;
	}
	
	public static List<String> getExecutedCallbacks()
	{
		return mCallbacks.getExecutedCallbacks();
	}
	
	public static void clearExecuteCallbacks()
	{
		mCallbacks.clearExecuteCallbacks();
	}
	
	public static class TheCallbacks implements Callbacks<CallbacksProviderBean>
	{
		private List<String>	mExecutedCallbacks = new ArrayList<String>();
		
		public List<String> getExecutedCallbacks()
		{
			return mExecutedCallbacks;
		}
		
		public void clearExecuteCallbacks()
		{
			mExecutedCallbacks = new ArrayList<String>();		
		}
		
		public boolean beforeValidate(CallbacksProviderBean object)
		{
			mExecutedCallbacks.add("beforeValidate " + object.toString());
			return true;
		}
		
		public boolean beforeInsert(CallbacksProviderBean object)
		{
			mExecutedCallbacks.add("beforeInsert " + object.toString());
			return true;
		}
		
		public boolean beforeDelete(int objectId)
		{
			mExecutedCallbacks.add("beforeDelete " + objectId);
			return true;
		}
		
		public boolean beforeSave(CallbacksProviderBean object)
		{
			mExecutedCallbacks.add("beforeSave " + object.toString());
			return true;
		}
		
		public boolean beforeUpdate(CallbacksProviderBean object)
		{
			mExecutedCallbacks.add("beforeUpdate " + object.toString());
			return true;
		}
	
		public boolean afterValidate(CallbacksProviderBean object)
		{
			mExecutedCallbacks.add("afterValidate " + object.toString());
			return true;
		}
		
		public boolean afterInsert(CallbacksProviderBean object, boolean success)
		{
			mExecutedCallbacks.add("afterInsert " + success + " " + object.toString());
			return true;
		}
		
		public boolean afterDelete(int objectId, boolean success)
		{
			mExecutedCallbacks.add("afterDelete " + success + " " + objectId);
			return true;
		}
		
		public boolean afterSave(CallbacksProviderBean object, boolean success)
		{
			mExecutedCallbacks.add("afterSave " + success + " " + object.toString());
			return true;
		}
		
		public boolean afterUpdate(CallbacksProviderBean object, boolean success)
		{
			mExecutedCallbacks.add("afterUpdate " + success + " " + object.toString());
			return true;
		}
		
		public boolean afterRestore(CallbacksProviderBean object)
		{
			mExecutedCallbacks.add("afterRestore " + object.toString());
			return true;
		}
	}
}

