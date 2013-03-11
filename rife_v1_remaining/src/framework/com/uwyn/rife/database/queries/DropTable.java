/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DropTable.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.capabilities.Capabilities;
import com.uwyn.rife.database.exceptions.DbQueryException;
import com.uwyn.rife.database.exceptions.TableNameRequiredException;
import com.uwyn.rife.database.exceptions.UnsupportedSqlFeatureException;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.template.TemplateFactory;
import com.uwyn.rife.tools.StringUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Object representation of a SQL "DROP TABLE" query.
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
public class DropTable extends AbstractQuery implements Cloneable
{
	private List<String>	mTables = null;

	public DropTable(Datasource datasource)
	{
		super(datasource);

		if (null == datasource)	throw new IllegalArgumentException("datasource can't be null.");

		clear();
	}

	public List<String> getTables()
	{
		return mTables;
	}

	public void clear()
	{
		super.clear();

		mTables = new ArrayList<String>();

		assert 0 == mTables.size();
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
			if (0 == mTables.size())
			{
				throw new TableNameRequiredException("DropTable");
			}
			else
			{
				Template template = TemplateFactory.SQL.get("sql."+StringUtils.encodeClassname(mDatasource.getAliasedDriver())+".drop_table");

				if (1 == mTables.size())
				{
					template.setValue("EXPRESSION", mTables.get(0));
				}
				else
				{
					if (template.hasValueId("TABLES"))
					{
						template.setValue("TABLES", StringUtils.join(mTables, template.getBlock("SEPERATOR")));
					}

					String block = template.getBlock("TABLES");
					if (0 == block.length())
					{
						throw new UnsupportedSqlFeatureException("MULTIPLE TABLE DROP", mDatasource.getAliasedDriver());
					}

					template.setValue("EXPRESSION", block);
				}

				mSql = template.getBlock("QUERY");

				if (template.hasValueId("TABLES"))
				{
					template.removeValue("TABLES");
				}
				template.removeValue("EXPRESSION");

				assert mSql != null;
				assert mSql.length() > 0;
			}
		}

		return mSql;
	}

	public DropTable table(String table)
	{
		if (null == table)			throw new IllegalArgumentException("table can't be null.");
		if (0 == table.length())	throw new IllegalArgumentException("table can't be empty.");

		mTables.add(table);
		clearGenerated();

		return this;
	}

	public DropTable clone()
	{
        DropTable new_instance = (DropTable)super.clone();
		if (new_instance != null)
		{
			if (mTables != null)
			{
				new_instance.mTables = new ArrayList<String>();
				new_instance.mTables.addAll(mTables);
			}
		}

		return new_instance;
	}
}
