/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AbstractWhereGroup.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.Datasource;

public abstract class AbstractWhereGroup<ParentType extends WhereQuery>
	extends AbstractWhereQuery<AbstractWhereGroup<ParentType>>
	implements Cloneable
{
	protected WhereQuery	mParent = null;
	
	protected AbstractWhereGroup(Datasource datasource, WhereQuery parent)
	{
		super(datasource);

		mParent = parent;
	}
	
	public ParentType end()
	{
		mParent.whereAnd("("+getSql()+")");
		mParent.addWhereParameters(getWhereParameters());
		
		return (ParentType)mParent;
	}
 
	public String getSql()
	{
		return mWhere.toString();
	}

	public AbstractWhereGroup<ParentType> clone()
	{
		return (AbstractWhereGroup<ParentType>)super.clone();
	}
}
