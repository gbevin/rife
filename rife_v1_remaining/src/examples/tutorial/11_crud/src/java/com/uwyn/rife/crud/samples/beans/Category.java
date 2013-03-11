/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Category.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud.samples.beans;

import com.uwyn.rife.site.AbstractTextualIdentifierGenerator;
import com.uwyn.rife.site.ConstrainedBean;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.MetaData;

public class Category extends MetaData
{
	private int 	mId = -1;
	private String 	mName = null;
	
	public void activateMetaData()
	{
		addConstraint(new ConstrainedProperty("id")
			.editable(false)
			.identifier(true));
		
		addConstraint(new ConstrainedProperty("name")
			.notEmpty(true)
			.notNull(true)
			.maxLength(255)
			.listed(true));
		
		addConstraint(new ConstrainedBean()
			.defaultOrder("name")
			.textualIdentifier(new AbstractTextualIdentifierGenerator<Category>() {
					public String generateIdentifier()
					{
						return getId()+" : "+getName();
					}
				}));
	}

	public Category	id(int id)		{ setId(id); return this; }
	public void		setId(int id)	{ mId = id; }
	public int		getId() 		{ return mId; }
	
	public Category	name(String name)		{ setName(name); return this; }
	public void		setName(String name)	{ mName = name; }
	public String	getName()				{ return mName; }
}

