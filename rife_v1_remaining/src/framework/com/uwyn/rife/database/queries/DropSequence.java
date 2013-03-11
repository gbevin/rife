/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DropSequence.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.capabilities.Capabilities;
import com.uwyn.rife.database.exceptions.DbQueryException;
import com.uwyn.rife.database.exceptions.SequenceNameRequiredException;
import com.uwyn.rife.database.exceptions.UnsupportedSqlFeatureException;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.template.TemplateFactory;
import com.uwyn.rife.tools.StringUtils;

/**
 * Object representation of a SQL "DROP SEQUENCE" query.
 * 
 * <p>This object may be used to dynamically construct a SQL statement in a
 * database-independent fashion. After it is finished, it may be executed using
 * {@link com.uwyn.rife.database.DbQueryManager#executeUpdate(Query)
 * DbQueryManager.executeUpdate()}.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @author Steven Grimm (koreth[remove] at midwinter dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public class DropSequence extends AbstractQuery implements Cloneable
{
	private String	mName = null;

	public DropSequence(Datasource datasource)
	{
		super(datasource);

		if (null == datasource)	throw new IllegalArgumentException("datasource can't be null.");

		clear();
	}

	public void clear()
	{
		super.clear();

		mName = null;
	}

	public String getName()
	{
		return mName;
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
				throw new SequenceNameRequiredException("DropSequence");
			}
			else
			{
				Template template = TemplateFactory.SQL.get("sql."+StringUtils.encodeClassname(mDatasource.getAliasedDriver())+".drop_sequence");

				if (template.hasValueId("NAME"))
				{
					template.setValue("NAME", mName);
				}

				mSql = template.getBlock("QUERY");
				if (0 == mSql.length())
				{
					throw new UnsupportedSqlFeatureException("DROP SEQUENCE", mDatasource.getAliasedDriver());
				}

				assert mSql != null;
				assert mSql.length() > 0;
			}
		}

		return mSql;
	}

	public DropSequence name(String name)
	{
		if (null == name)		throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty.");

		clearGenerated();
		mName = name;

		return this;
	}

	public DropSequence clone()
	{
        return (DropSequence)super.clone();
	}
}
