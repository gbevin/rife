/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ManyToOneAssociationDeclaration.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic;

public class ManyToOneAssociationDeclaration
{
	private Class					mMainType;
	private	String					mMainProperty;
	private Class					mCollectionType;
	private ManyToOneDeclaration	mMainDeclaration;
	
	public void setMainType(Class type)
	{
		mMainType = type;
	}
	
	public ManyToOneAssociationDeclaration mainType(Class type)
	{
		setMainType(type);
		
		return this;
	}
	
	public Class getMainType()
	{
		return mMainType;
	}
	
	public void setMainProperty(String mainProperty)
	{
		mMainProperty = mainProperty;
	}
	
	public ManyToOneAssociationDeclaration mainProperty(String mainProperty)
	{
		setMainProperty(mainProperty);
		return this;
	}
	
	public String getMainProperty()
	{
		return mMainProperty;
	}
	
	public void setCollectionType(Class type)
	{
		mCollectionType = type;
	}
	
	public ManyToOneAssociationDeclaration collectionType(Class type)
	{
		setCollectionType(type);
		
		return this;
	}
	
	public Class getCollectionType()
	{
		return mCollectionType;
	}
	
	public void setMainDeclaration(ManyToOneDeclaration mainDeclaration)
	{
		mMainDeclaration = mainDeclaration;
	}
	
	public ManyToOneAssociationDeclaration mainDeclaration(ManyToOneDeclaration mainDeclaration)
	{
		setMainDeclaration(mainDeclaration);
		
		return this;
	}
	
	public ManyToOneDeclaration getMainDeclaration()
	{
		return mMainDeclaration;
	}
}
