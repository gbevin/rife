/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Delete.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.capabilities.Capabilities;
import com.uwyn.rife.database.exceptions.TableNameRequiredException;
import com.uwyn.rife.database.exceptions.UnsupportedSqlFeatureException;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.template.TemplateFactory;
import com.uwyn.rife.tools.StringUtils;

/**
 * Object representation of a SQL "DELETE" query.
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
public class Delete extends AbstractWhereQuery<Delete> implements Cloneable
{
	private String	mHint = null;
	private String	mFrom = null;

	public Delete(Datasource datasource)
	{
		super(datasource);

		if (null == datasource)	throw new IllegalArgumentException("datasource can't be null.");
		
		clear();
	}

	public String getHint()
	{
		return mHint;
	}

	public void clear()
	{
		super.clear();

		mHint = null;
		mFrom = null;
	}

	public String getFrom()
	{
		return mFrom;
	}
	
	public Capabilities getCapabilities()
	{
		return null;
	}

	public String getSql()
	{
		if (null == mSql)
		{
			if (null == mFrom)
			{
				throw new TableNameRequiredException("Delete");
			}
			else
			{
				Template template = TemplateFactory.SQL.get("sql."+StringUtils.encodeClassname(mDatasource.getAliasedDriver())+".delete");

				if (mHint != null)
				{
					if (!template.hasValueId("HINT"))
					{
						throw new UnsupportedSqlFeatureException("HINT", mDatasource.getAliasedDriver());
					}
					template.setValue("EXPRESSION", mHint);
					template.setBlock("HINT", "HINT");
				}

				template.setValue("TABLE", mFrom);

				if (mWhere != null &&
					mWhere.length() > 0)
				{
					template.setValue("CONDITION", mWhere);
					template.setValue("WHERE", template.getBlock("WHERE"));
				}

				mSql = template.getBlock("QUERY");
				
				assert mSql != null;
				assert mSql.length() > 0;
			}
		}

		return mSql;
	}

	public Delete hint(String hint)
	{
		clearGenerated();
		mHint = hint;

		return this;
	}

	public Delete from(String from)
	{
		if (null == from)		throw new IllegalArgumentException("from can't be null.");
		if (0 == from.length())	throw new IllegalArgumentException("from can't be empty.");

		clearGenerated();
		mFrom = from;

		return this;
	}

	public Delete clone()
	{
        return super.clone();
	}
}
