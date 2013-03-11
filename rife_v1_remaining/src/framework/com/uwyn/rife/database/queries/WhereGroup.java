/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: WhereGroup.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.capabilities.Capabilities;

public class WhereGroup<ParentType extends WhereQuery> extends AbstractWhereGroup<ParentType>
{
	public WhereGroup(Datasource datasource, WhereQuery parent)
	{
		super(datasource, parent);
	}
	
	public Capabilities getCapabilities()
	{
		return null;
	}
	
	public ParentType end()
	{
		StringBuilder where = new StringBuilder();

		where.append("(");
		where.append(getSql());
		where.append(")");
		
		mParent.where(where.toString());
		
		mParent.addWhereParameters(getWhereParameters());
		
		return (ParentType)mParent;
	}
}
