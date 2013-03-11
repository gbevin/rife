/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AbstractCallbacks.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic;

/**
 * Abstract convenience class that provides a postive no-op implementation of
 * all the methods of the {@link Callbacks} interface.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see com.uwyn.rife.database.querymanagers.generic.Callbacks
 * @since 1.0
 */
public abstract class AbstractCallbacks<BeanType> implements Callbacks<BeanType>
{
	public boolean beforeValidate(BeanType object)
	{
		return true;
	}
	
	public boolean beforeInsert(BeanType object)
	{
		return true;
	}
	
	public boolean beforeDelete(int objectId)
	{
		return true;
	}
	
	public boolean beforeSave(BeanType object)
	{
		return true;
	}
	
	public boolean beforeUpdate(BeanType object)
	{
		return true;
	}
	
	public boolean afterValidate(BeanType object)
	{
		return true;
	}
	
	public boolean afterInsert(BeanType object, boolean success)
	{
		return true;
	}
	
	public boolean afterDelete(int objectId, boolean success)
	{
		return true;
	}
	
	public boolean afterSave(BeanType object, boolean success)
	{
		return true;
	}
	
	public boolean afterUpdate(BeanType object, boolean success)
	{
		return true;
	}
	
	public boolean afterRestore(BeanType object)
	{
		return true;
	}
}
