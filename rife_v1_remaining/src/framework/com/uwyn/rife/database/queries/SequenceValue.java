/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SequenceValue.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.capabilities.Capabilities;
import com.uwyn.rife.database.exceptions.DbQueryException;
import com.uwyn.rife.database.exceptions.SequenceNameRequiredException;
import com.uwyn.rife.database.exceptions.SequenceOperationRequiredException;
import com.uwyn.rife.database.exceptions.UnsupportedSqlFeatureException;
import com.uwyn.rife.datastructures.EnumClass;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.template.TemplateFactory;
import com.uwyn.rife.tools.StringUtils;

public class SequenceValue extends AbstractQuery implements Cloneable, ReadQuery
{
	private String		mName = null;
	private Operation	mOperation = null;

	public static final Operation NEXT = new Operation("NEXT");
	public static final Operation CURRENT = new Operation("CURRENT");

	public SequenceValue(Datasource datasource)
	{
		super(datasource);

		if (null == datasource)	throw new IllegalArgumentException("datasource can't be null.");

		clear();
	}

	public void clear()
	{
		super.clear();

		mName = null;
		mOperation = null;
	}

	public String getName()
	{
		return mName;
	}

	public Operation getOperation()
	{
		return mOperation;
	}

	public Capabilities getCapabilities()
	{
		return null;
	}

	public String getSql()
	throws DbQueryException
	{
		if (null == mSql)
		{
			if (null == mName)
			{
				throw new SequenceNameRequiredException("SequenceValue");
			}
			else if (null == mOperation)
			{
				throw new SequenceOperationRequiredException("SequenceValue");
			}
			else
			{
				Template template = TemplateFactory.SQL.get("sql."+StringUtils.encodeClassname(mDatasource.getAliasedDriver())+".sequence_value");

				if (template.hasValueId("NAME"))
				{
					template.setValue("NAME", mName);
				}

				mSql = template.getBlock("OPERATION_"+mOperation);
				if (0 == mSql.length())
				{
					throw new UnsupportedSqlFeatureException("SEQUENCE VALUE "+mOperation, mDatasource.getAliasedDriver());
				}

				assert mSql != null;
				assert mSql.length() > 0;
			}
		}

		return mSql;
	}

	public SequenceValue name(String name)
	{
		if (null == name)		throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty.");

		clearGenerated();
		mName = name;

		return this;
	}

	public SequenceValue operation(Operation operation)
	{
		clearGenerated();
		mOperation = operation;

		return this;
	}

	public SequenceValue next()
	{
		return operation(NEXT);
	}

	public SequenceValue current()
	{
		return operation(CURRENT);
	}

	public SequenceValue clone()
	{
		return (SequenceValue)super.clone();
	}

	public static class Operation extends EnumClass<String>
	{
		Operation(String identifier)
		{
			super(identifier);
		}
	}
}
