/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CallbacksSparseBean.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic.beans;

import com.uwyn.rife.database.querymanagers.generic.Callbacks;
import com.uwyn.rife.database.querymanagers.generic.beans.CallbacksSparseBean;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.Validation;
import java.util.ArrayList;
import java.util.List;

public class CallbacksSparseBean extends Validation implements Callbacks<CallbacksSparseBean>
{
	private int 	mId = -1;
	private String 	mTestString = null;
	
	private List<String>	mExecutedCallbacks = new ArrayList<String>();
	
	private boolean			mBeforeValidateReturn = true;
	private boolean			mBeforeInsertReturn = true;
	private boolean			mBeforeDeleteReturn = true;
	private boolean			mBeforeSaveReturn = true;
	private boolean			mBeforeUpdateReturn = true;
	private boolean			mAfterValidateReturn = true;
	private boolean			mAfterInsertReturn = true;
	private boolean			mAfterDeleteReturn = true;
	private boolean			mAfterSaveReturn = true;
	private boolean			mAfterUpdateReturn = true;
	private boolean			mAfterRestoreReturn = true;
	
	public void activateValidation()
	{
		addConstraint(new ConstrainedProperty("id").identifier(true).sparse(true));
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
	
	public List<String> getExecutedCallbacks()
	{
		return mExecutedCallbacks;
	}
	
	public void clearExecuteCallbacks()
	{
		mExecutedCallbacks = new ArrayList<String>();		
	}
	
	public void setBeforeValidateReturn(boolean beforeValidateReturn)
	{
		mBeforeValidateReturn = beforeValidateReturn;
	}
	
	public void setBeforeInsertReturn(boolean beforeInsertReturn)
	{
		mBeforeInsertReturn = beforeInsertReturn;
	}
	
	public void setBeforeDeleteReturn(boolean beforeDeleteReturn)
	{
		mBeforeDeleteReturn = beforeDeleteReturn;
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
	
	public void setAfterDeleteReturn(boolean afterDeleteReturn)
	{
		mAfterDeleteReturn = afterDeleteReturn;
	}
	
	public void setAfterSaveReturn(boolean afterSaveReturn)
	{
		mAfterSaveReturn = afterSaveReturn;
	}
	
	public void setAfterUpdateReturn(boolean afterUpdateReturn)
	{
		mAfterUpdateReturn = afterUpdateReturn;
	}
	
	public void setAfterRestoreReturn(boolean afterRestoreReturn)
	{
		mAfterRestoreReturn = afterRestoreReturn;
	}
	
	public boolean beforeValidate(CallbacksSparseBean object)
	{
		mExecutedCallbacks.add("beforeValidate " + object.toString());
		return mBeforeValidateReturn;
	}
	
	public boolean beforeInsert(CallbacksSparseBean object)
	{
		mExecutedCallbacks.add("beforeInsert " + object.toString());
		return mBeforeInsertReturn;
	}
	
	public boolean beforeDelete(int objectId)
	{
		mExecutedCallbacks.add("beforeDelete " + objectId);
		return mBeforeDeleteReturn;
	}
	
	public boolean beforeSave(CallbacksSparseBean object)
	{
		mExecutedCallbacks.add("beforeSave " + object.toString());
		return mBeforeSaveReturn;
	}
	
	public boolean beforeUpdate(CallbacksSparseBean object)
	{
		mExecutedCallbacks.add("beforeUpdate " + object.toString());
		return mBeforeUpdateReturn;
	}
	
	public boolean afterValidate(CallbacksSparseBean object)
	{
		mExecutedCallbacks.add("afterValidate " + object.toString());
		return mAfterValidateReturn;
	}
	
	public boolean afterInsert(CallbacksSparseBean object, boolean success)
	{
		mExecutedCallbacks.add("afterInsert " + success + " " + object.toString());
		return mAfterInsertReturn;
	}
	
	public boolean afterDelete(int objectId, boolean success)
	{
		mExecutedCallbacks.add("afterDelete " + success + " " + objectId);
		return mAfterDeleteReturn;
	}
	
	public boolean afterSave(CallbacksSparseBean object, boolean success)
	{
		mExecutedCallbacks.add("afterSave " + success + " " + object.toString());
		return mAfterSaveReturn;
	}
	
	public boolean afterUpdate(CallbacksSparseBean object, boolean success)
	{
		mExecutedCallbacks.add("afterUpdate " + success + " " + object.toString());
		return mAfterUpdateReturn;
	}
	
	public boolean afterRestore(CallbacksSparseBean object)
	{
		mExecutedCallbacks.add("afterRestore " + object.toString());
		return mAfterRestoreReturn;
	}
}

