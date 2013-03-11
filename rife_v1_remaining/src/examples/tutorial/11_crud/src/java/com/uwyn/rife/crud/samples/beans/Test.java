/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Test.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud.samples.beans;

import com.uwyn.rife.site.ConstrainedBean;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.MetaData;

public class Test extends MetaData
{
	private int 	mId = -1;
	private boolean mActive = false;
	private String	mActiveString = null;
	private String 	mName = null;
	
	public void activateMetaData()
	{
		addConstraint(new ConstrainedProperty("id")
			.editable(false)
			.identifier(true));
		
		addConstraint(new ConstrainedProperty("active")
			.listed(false));
		
		addConstraint(new ConstrainedProperty("activeString")
			.persistent(false)
			.editable(false)
			.listed(true));
		
		addConstraint(new ConstrainedProperty("name")
			.notEmpty(true)
			.notNull(true)
			.maxLength(80)
			.listed(true));
		
		addConstraint(new ConstrainedBean()
			.defaultOrder("active", ConstrainedBean.DESC)
			.defaultOrder("name")
			.associations(TestQuestion.class, TestResult.class));
	}

	public Test		id(int id)		{ setId(id); return this; }
	public void		setId(int id)	{ mId = id; }
	public int		getId() 		{ return mId; }
	
	public Test		active(boolean active)		{ setActive(active); return this; }
	public void		setActive(boolean active)	{ mActive = active; if (mActive) mActiveString = "x"; }
	public boolean	getActive()					{ return mActive; }
	
	public void		setActiveString(String activestring)	{ }
	public String	getActiveString()						{ return mActiveString; }
	
	public Test		name(String name)		{ setName(name); return this; }
	public void		setName(String name)	{ mName = name; }
	public String	getName()				{ return mName; }
}

