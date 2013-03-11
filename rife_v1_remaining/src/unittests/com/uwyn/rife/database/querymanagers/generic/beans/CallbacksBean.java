/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CallbacksBean.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic.beans;

import com.uwyn.rife.database.querymanagers.generic.Callbacks;
import com.uwyn.rife.database.querymanagers.generic.beans.CallbacksBean;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.Validation;
import java.util.ArrayList;
import java.util.List;

public class CallbacksBean extends Validation implements Callbacks<CallbacksBean>
{
	private int 	mId = -1;
	private String 	mTestString = null;
	
	static private List<String>	sExecutedCallbacks = new ArrayList<String>();
	
	private boolean			mBeforeValidateReturn = true;
	private boolean			mBeforeInsertReturn = true;
	private static boolean	sBeforeDeleteReturn = true;
	private boolean			mBeforeSaveReturn = true;
	private boolean			mBeforeUpdateReturn = true;
	private boolean			mAfterValidateReturn = true;
	private boolean			mAfterInsertReturn = true;
	private static boolean	sAfterDeleteReturn = true;
	private boolean			mAfterSaveReturn = true;
	private boolean			mAfterUpdateReturn = true;
	private static boolean	sAfterRestoreReturn = true;
	
	public CallbacksBean()
	{
	}
	
	protected void activateValidation()
	{
		addConstraint(new ConstrainedProperty("id").identifier(true).notNull(true));
		addConstraint(new ConstrainedProperty("testString").notNull(true));
	}
	
	public void setId(int id)
	{
		mId = id;
	}
	
	public int getId()
	{
		return mId;
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
		return sExecutedCallbacks;
	}
	
	public static void clearExecuteCallbacks()
	{
		sExecutedCallbacks = new ArrayList<String>();		
	}
	
	public void setBeforeValidateReturn(boolean beforeValidateReturn)
	{
		mBeforeValidateReturn = beforeValidateReturn;
	}
	
	public void setBeforeInsertReturn(boolean beforeInsertReturn)
	{
		mBeforeInsertReturn = beforeInsertReturn;
	}
	
	public static void setBeforeDeleteReturn(boolean beforeDeleteReturn)
	{
		sBeforeDeleteReturn = beforeDeleteReturn;
	}
	
	public void setBeforeSaveReturn(boolean beforeSaveReturn)
	{
		mBeforeSaveReturn = beforeSaveReturn;
	}
	
	public void setBeforeUpdateReturn(boolean beforeUpdateReturn)
	{
		mBeforeUpdateReturn = beforeUpdateReturn;
	}
	
	public void setAfterValidateReturn(boolean afterValidateReturn)
	{
		mAfterValidateReturn = afterValidateReturn;
	}
	
	public void setAfterInsertReturn(boolean afterInsertReturn)
	{
		mAfterInsertReturn = afterInsertReturn;
	}
	
	public static void setAfterDeleteReturn(boolean afterDeleteReturn)
	{
		sAfterDeleteReturn = afterDeleteReturn;
	}
	
	public void setAfterSaveReturn(boolean afterSaveReturn)
	{
		mAfterSaveReturn = afterSaveReturn;
	}
	
	public void setAfterUpdateReturn(boolean afterUpdateReturn)
	{
		mAfterUpdateReturn = afterUpdateReturn;
	}
	
	public static void setAfterRestoreReturn(boolean afterRestoreReturn)
	{
		sAfterRestoreReturn = afterRestoreReturn;
	}
	
	public boolean beforeValidate(CallbacksBean object)
	{
		sExecutedCallbacks.add("beforeValidate " + object.toString());
		return mBeforeValidateReturn;
	}
	
	public boolean beforeInsert(CallbacksBean object)
	{
		sExecutedCallbacks.add("beforeInsert " + object.toString());
		return mBeforeInsertReturn;
	}
	
	public boolean beforeDelete(int objectId)
	{
		sExecutedCallbacks.add("beforeDelete " + objectId);
		return sBeforeDeleteReturn;
	}
	
	public boolean beforeSave(CallbacksBean object)
	{
		sExecutedCallbacks.add("beforeSave " + object.toString());
		return mBeforeSaveReturn;
	}
	
	public boolean beforeUpdate(CallbacksBean object)
	{
		sExecutedCallbacks.add("beforeUpdate " + object.toString());
		return mBeforeUpdateReturn;
	}
	
	public boolean afterValidate(CallbacksBean object)
	{
		sExecutedCallbacks.add("afterValidate " + object.toString());
		return mAfterValidateReturn;
	}
	
	public boolean afterInsert(CallbacksBean object, boolean success)
	{
		sExecutedCallbacks.add("afterInsert " + success + " " + object.toString());
		return mAfterInsertReturn;
	}
	
	public boolean afterDelete(int objectId, boolean success)
	{
		sExecutedCallbacks.add("afterDelete " + success + " " + objectId);
		return sAfterDeleteReturn;
	}
	
	public boolean afterSave(CallbacksBean object, boolean success)
	{
		sExecutedCallbacks.add("afterSave " + success + " " + object.toString());
		return mAfterSaveReturn;
	}
	
	public boolean afterUpdate(CallbacksBean object, boolean success)
	{
		sExecutedCallbacks.add("afterUpdate " + success + " " + object.toString());
		return mAfterUpdateReturn;
	}
	
	public boolean afterRestore(CallbacksBean object)
	{
		sExecutedCallbacks.add("afterRestore " + object.toString());
		return sAfterRestoreReturn;
	}
}

