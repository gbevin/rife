/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Move.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud.elements.admin;

import com.uwyn.rife.cmf.dam.OrdinalManager;
import com.uwyn.rife.crud.templates.AdminTemplateTransformer;
import com.uwyn.rife.site.Constrained;

public class Move extends CrudElement
{
	private int			mIdentityValue = -1;
	private Constrained	mBeanInstance = null;
	
	public AdminTemplateTransformer getTransformer()
	{
		return null;
	}
	
	public void initialize()
	{
		super.initialize();
		
		mIdentityValue = getIdentityValue();
		if (mIdentityValue >= 0)
		{
			mBeanInstance = getBeanInstance();
		}
		if (null == mBeanInstance)
		{
			exit(getCrudPrefix()+"-home");
		}
	}
	
	protected int getIdentityValue()
	{
		return getInputInt(getIdentityVarName(), -1);
	}
	
	protected Constrained getBeanInstance()
	{
		return (Constrained)getContentQueryManager().restore(mIdentityValue);
	}
	
	public void processElement()
	{
		moveEntity(mBeanInstance);
		exit("moved");
	}
	
	protected boolean moveEntity(Constrained entity)
	{
		return getContentQueryManager().move(entity, getPropertyString(getCrudPrefix() + "-ordinal_property_name"), OrdinalManager.Direction.getDirection(getInput("direction")));
	}
}
