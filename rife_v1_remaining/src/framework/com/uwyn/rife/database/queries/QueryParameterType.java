/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: QueryParameterType.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.datastructures.EnumClass;

public class QueryParameterType extends EnumClass<String>
{
	public static final QueryParameterType FIELD = new QueryParameterType("FIELD", false);
	public static final QueryParameterType TABLE = new QueryParameterType("TABLE", false);
	public static final QueryParameterType WHERE = new QueryParameterType("WHERE", false);
	public static final QueryParameterType UNION = new QueryParameterType("UNION", false);
	public static final QueryParameterType LIMIT = new QueryParameterType("LIMIT", true);
	public static final QueryParameterType OFFSET = new QueryParameterType("OFFSET", true);
	
	private boolean	mSingular = false;

	private QueryParameterType(String identifier, boolean singular)
	{
		super(identifier);
		
		mSingular = singular;
	}
	
	public boolean isSingular()
	{
		return mSingular;
	}
}

