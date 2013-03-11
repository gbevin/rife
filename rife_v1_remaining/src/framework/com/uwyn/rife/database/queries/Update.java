/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Update.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.capabilities.Capabilities;
import com.uwyn.rife.database.exceptions.DbQueryException;
import com.uwyn.rife.database.exceptions.FieldsRequiredException;
import com.uwyn.rife.database.exceptions.TableNameRequiredException;
import com.uwyn.rife.database.exceptions.UnsupportedSqlFeatureException;
import com.uwyn.rife.database.types.SqlNull;
import com.uwyn.rife.site.Constrained;
import com.uwyn.rife.site.ConstrainedUtils;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.template.TemplateFactory;
import com.uwyn.rife.tools.StringUtils;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Object representation of a SQL "UPDATE" query.
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
public class Update extends AbstractWhereQuery<Update> implements Cloneable
{
	private String				mHint = null;
	private String				mTable = null;
	private Map<String, Object>	mFields = null;

	public Update(Datasource datasource)
	{
		super(datasource);

		if (null == datasource)	throw new IllegalArgumentException("datasource can't be null.");

		clear();
	}

	public void clear()
	{
		super.clear();

		mHint = null;
		mFields = new LinkedHashMap<String, Object>();
		mTable = null;

		assert 0 == mFields.size();
	}

	public String getHint()
	{
		return mHint;
	}

	public String getTable()
	{
		return mTable;
	}

	public Map<String, Object> getFields()
	{
		return mFields;
	}

	public Capabilities getCapabilities()
	{
		return null;
	}

	public String getSql()
	{
		if (null == mSql)
		{
			if (null == mTable)
			{
				throw new TableNameRequiredException("Update");
			}
			else if (0 == mFields.size())
			{
				throw new FieldsRequiredException("Update");
			}
			else
			{
				Template template = TemplateFactory.SQL.get("sql."+StringUtils.encodeClassname(mDatasource.getAliasedDriver())+".update");

				if (mHint != null)
				{
					if (!template.hasValueId("HINT"))
					{
						throw new UnsupportedSqlFeatureException("HINT", mDatasource.getAliasedDriver());
					}
					template.setValue("EXPRESSION", mHint);
					template.setBlock("HINT", "HINT");
				}

				template.setValue("TABLE", mTable);

				if (mFields.size() > 0)
				{
					ArrayList<String>	set_list = new ArrayList<String>();

					for (String field : mFields.keySet())
					{
						template.setValue("NAME", field);
						template.setValue("V", mFields.get(field).toString());
						set_list.add(template.getBlock("SET"));
					}
					template.setValue("SET", StringUtils.join(set_list, template.getBlock("SEPERATOR")));
				}

				if (mWhere != null  &&
					mWhere.length() > 0)
				{
					template.setValue("CONDITION", mWhere);
					template.setValue("WHERE", template.getBlock("WHERE"));
				}

				mSql =  template.getBlock("QUERY");

				assert mSql != null;
				assert mSql.length() > 0;
			}
		}

		return mSql;
	}

	public Update hint(String hint)
	{
		clearGenerated();
		mHint = hint;

		return this;
	}

	public Update table(String table)
	{
		if (null == table)			throw new IllegalArgumentException("table can't be null.");
		if (0 == table.length())	throw new IllegalArgumentException("table can't be empty.");

		clearGenerated();
		mTable = table;

		return this;
	}

	public Update fieldSubselect(Select query)
	{
		_fieldSubselect(query);

		return this;
	}

	protected Update _field(String field, Object value)
	{
		assert field != null;
		assert field.length() > 0;

		clearGenerated();
		if (null == value)
		{
			mFields.put(field, SqlNull.NULL);
		}
		else
		{
			mFields.put(field, value);
		}

		return this;
	}

	public Update fieldParameter(String field)
	{
		return fieldParameter(field, field);
	}

	public Update fieldParameter(String field, String alias)
	{
		if (null == field)			throw new IllegalArgumentException("field can't be null.");
		if (0 == field.length())	throw new IllegalArgumentException("field can't be empty.");
		if (null == alias)			throw new IllegalArgumentException("alias can't be null.");
		if (0 == alias.length())	throw new IllegalArgumentException("alias can't be empty.");

		clearGenerated();

		addFieldParameter(alias);

		return _field(field, "?");
	}

	public Update field(String field, char value)
	{
		return field(field, new Character(value));
	}

	public Update field(String field, boolean value)
	{
		return field(field, Boolean.valueOf(value));
	}

	public Update field(String field, byte value)
	{
		return field(field, new Byte(value));
	}

	public Update field(String field, double value)
	{
		return field(field, new Double(value));
	}

	public Update field(String field, float value)
	{
		return field(field, new Float(value));
	}

	public Update field(String field, int value)
	{
		return field(field, new Integer(value));
	}

	public Update field(String field, long value)
	{
		return field(field, new Long(value));
	}

	public Update field(String field, short value)
	{
		return field(field, new Short(value));
	}

	public Update field(String field, Select query)
	{
		if (null == query)	throw new IllegalArgumentException("query can't be null.");

		StringBuilder buffer = new StringBuilder();

		buffer.append("(");
		buffer.append(query.toString());
		buffer.append(")");

		fieldCustom(field, buffer.toString());

		_fieldSubselect(query);

		return this;
	}

	public Update fieldCustom(String field, String expression)
	{
		if (null == field)			throw new IllegalArgumentException("field can't be null.");
		if (0 == field.length())	throw new IllegalArgumentException("field can't be empty.");

		if (null == expression)
		{
			return _field(field, null);
		}
		else
		{
			return _field(field, expression);
		}
	}

	public Update field(String field, Object value)
	{
		if (null == field)			throw new IllegalArgumentException("field can't be null.");
		if (0 == field.length())	throw new IllegalArgumentException("field can't be empty.");

		if (null == value)
		{
			return _field(field, null);
		}
		else
		{
			return _field(field, mDatasource.getSqlConversion().getSqlValue(value));
		}
	}

	public Update fields(Object[] keyValues)
	{
		if (null == keyValues)		throw new IllegalArgumentException("keyValues can't be null.");
		if (0 == keyValues.length)	throw new IllegalArgumentException("keyValues can't be empty.");

		for (int i = 0; i < keyValues.length; i+=2)
		{
			if (null != keyValues[i])
			{
				field(keyValues[i].toString(), keyValues[i+1]);
			}
		}

		return this;
	}

	public Update fields(Object bean)
	throws DbQueryException
	{
		return fieldsFiltered(bean, null, null);
	}

	public Update fieldsIncluded(Object bean, String[] includedFields)
	throws DbQueryException
	{
		return fieldsFiltered(bean, includedFields, null);
	}

	public Update fieldsExcluded(Object bean, String[] excludedFields)
	throws DbQueryException
	{
		return fieldsFiltered(bean, null, excludedFields);
	}

	public Update fieldsFiltered(Object bean, String[] includedFields, String[] excludedFields)
	throws DbQueryException
	{
		if (null == bean)	throw new IllegalArgumentException("bean can't be null.");

		Constrained			constrained = ConstrainedUtils.makeConstrainedInstance(bean);
		Map<String, String>	property_values = QueryHelper.getBeanPropertyValues(bean, includedFields, excludedFields, getDatasource());

		for (String property_name : property_values.keySet())
		{
			if (!ConstrainedUtils.saveConstrainedProperty(constrained, property_name, null))
			{
				continue;
			}

			_field(property_name, property_values.get(property_name));
		}

		return this;
	}

	public Update fieldsParameters(Class beanClass)
	throws DbQueryException
	{
		return fieldsParametersExcluded(beanClass, null);
	}

	public Update fieldsParametersExcluded(Class beanClass, String[] excludedFields)
	throws DbQueryException
	{
		if (null == beanClass)	throw new IllegalArgumentException("beanClass can't be null.");

		clearGenerated();

		Constrained	constrained = ConstrainedUtils.getConstrainedInstance(beanClass);
		Set<String>	property_names = QueryHelper.getBeanPropertyNames(beanClass, excludedFields);

		for (String property_name : property_names)
		{
			if (!ConstrainedUtils.saveConstrainedProperty(constrained, property_name, null))
			{
				continue;
			}

			addFieldParameter(property_name);
			_field(property_name, "?");
		}

		return this;
	}

	public Update clone()
	{
        Update new_instance = super.clone();
		if (new_instance != null)
		{
			if (mFields != null)
			{
				new_instance.mFields = new LinkedHashMap<String, Object>();
				new_instance.mFields.putAll(mFields);
			}
		}

		return new_instance;
	}
}
