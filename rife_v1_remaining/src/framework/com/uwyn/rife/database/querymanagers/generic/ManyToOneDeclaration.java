/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ManyToOneDeclaration.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic;

public class ManyToOneDeclaration
{
	private boolean	mIsBasic = true;
	private Class	mAssociationType;
	private String	mAssociationTable;
	private String	mAssociationColumn;
	private GenericQueryManager	mAssociationManager;
	
	public void setIsBasic(boolean isBasic)
	{
		mIsBasic = isBasic;
	}
	
	public ManyToOneDeclaration isBasic(boolean isBasic)
	{
		setIsBasic(isBasic);
		
		return this;
	}
	
	public boolean isBasic()
	{
		return mIsBasic;
	}
	
	public void setAssociationType(Class type)
	{
		mAssociationType = type;
	}
	
	public ManyToOneDeclaration associationType(Class type)
	{
		setAssociationType(type);
		
		return this;
	}
	
	public Class getAssociationType()
	{
		return mAssociationType;
	}
	
	public void setAssociationTable(String associationTable)
	{
		mAssociationTable = associationTable;
	}
	
	public ManyToOneDeclaration associationTable(String associationTable)
	{
		setAssociationTable(associationTable);
		
		return this;
	}
	
	public String getAssociationTable()
	{
		return mAssociationTable;
	}
	
	public void setAssociationColumn(String associationColumn)
	{
		mAssociationColumn = associationColumn;
	}
	
	public ManyToOneDeclaration associationColumn(String associationColumn)
	{
		setAssociationColumn(associationColumn);
		
		return this;
	}
	
	public String getAssociationColumn()
	{
		return mAssociationColumn;
	}
	
	public void setAssociationManager(GenericQueryManager associationManager)
	{
		mAssociationManager = associationManager;
	}
	
	public ManyToOneDeclaration associationManager(GenericQueryManager associationManager)
	{
		setAssociationManager(associationManager);
		
		return this;
	}
	
	public GenericQueryManager getAssociationManager()
	{
		return mAssociationManager;
	}
}
