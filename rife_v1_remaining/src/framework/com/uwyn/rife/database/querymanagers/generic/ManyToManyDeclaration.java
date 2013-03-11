/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ManyToManyDeclaration.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic;

public class ManyToManyDeclaration
{
	private Class	mAssociationType;
	private Class	mCollectionType;
	private boolean	mReversed;
	
	public void setAssociationType(Class type)
	{
		mAssociationType = type;
	}
	
	public ManyToManyDeclaration associationType(Class type)
	{
		setAssociationType(type);
		
		return this;
	}
	
	public Class getAssociationType()
	{
		return mAssociationType;
	}
	
	public void setCollectionType(Class type)
	{
		mCollectionType = type;
	}
	
	public ManyToManyDeclaration collectionType(Class type)
	{
		setCollectionType(type);
		
		return this;
	}
	
	public Class getCollectionType()
	{
		return mCollectionType;
	}
	
	public void setReversed(boolean reversed)
	{
		mReversed = reversed;
	}
	
	public ManyToManyDeclaration reversed(boolean reversed)
	{
		setReversed(reversed);
		
		return this;
	}
	
	public boolean isReversed()
	{
		return mReversed;
	}
}
