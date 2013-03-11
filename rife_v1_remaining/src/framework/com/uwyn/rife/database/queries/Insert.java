/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Insert.java 3918 2008-04-14 17:35:35Z gbevin $
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
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Object representation of a SQL "INSERT" query.
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
public class Insert extends AbstractParametrizedQuery implements Cloneable
{
	private String						mHint = null;
	private String						mInto = null;
	private Map<String, List<Object>>	mFields = null;

	public Insert(Datasource datasource)
	{
		super(datasource);

		if (null == datasource)	throw new IllegalArgumentException("datasource can't be null.");

		clear();
	}

	public void clear()
	{
		super.clear();

		mHint = null;
		mInto = null;
		mFields = new LinkedHashMap<String, List<Object>>();

		assert 0 == mFields.size();
	}

	public String getHint()
	{
		return mHint;
	}

	public String getInto()
	{
		return mInto;
	}

	public Map<String, List<Object>> getFields()
	{
		return mFields;
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
			if (null == mInto)
			{
				throw new TableNameRequiredException("Insert");
			}
			else if (0 == mFields.size())
			{
				throw new FieldsRequiredException("Insert");
			}
			else
			{
				Template template = TemplateFactory.SQL.get("sql."+StringUtils.encodeClassname(mDatasource.getAliasedDriver())+".insert");
				
				if (mHint != null)
				{
					if (!template.hasValueId("HINT"))
					{
						throw new UnsupportedSqlFeatureException("HINT", mDatasource.getAliasedDriver());
					}
					template.setValue("EXPRESSION", mHint);
					template.setBlock("HINT", "HINT");
				}

				template.setValue("INTO", mInto);

				// obtain the maximum number of values that are present by counting those of each field
				int	maximum_number_of_value_rows = 0;
				for (List<Object> values : mFields.values())
				{
					if (values.size() > maximum_number_of_value_rows)
					{
						maximum_number_of_value_rows = values.size();
					}
				}

				// create the different rows that will be inserted into the database
				ArrayList<String>	value_rows = new ArrayList<String>();
				ArrayList<String>	value_row = null;
				Object[]			column_names = mFields.keySet().toArray();
				String				column_name = null;
				for (int current_value_row = 0; current_value_row < maximum_number_of_value_rows; current_value_row++)
				{
					value_row = new ArrayList<String>();
					for (int i = 0; i < column_names.length; i++)
					{
						column_name = (String)column_names[i];
						if (current_value_row <= mFields.get(column_name).size()-1)
						{
							value_row.add(mFields.get(column_name).get(current_value_row).toString());
						}
						else
						{
							value_row.add("NULL");
						}
					}
					template.setValue("VALUES", StringUtils.join(value_row, template.getBlock("SEPERATOR")));
					value_rows.add(template.getBlock("VALUE_ROW"));
				}

				// create the strings of the columns that values will be inserted into and which values they are
				template.setValue("COLUMNS", StringUtils.join(column_names, template.getBlock("SEPERATOR")));
				if (1 == value_rows.size())
				{
					template.setValue("DATA", value_rows.get(0));
				}
				else
				{
					if (template.hasValueId("VALUE_ROWS"))
					{
						template.setValue("VALUE_ROWS", StringUtils.join(value_rows, template.getBlock("SEPERATOR")));
					}

					String block = template.getBlock("VALUE_ROWS");
					if (0 == block.length())
					{
						throw new UnsupportedSqlFeatureException("MULTIPLE INSERT ROWS", mDatasource.getAliasedDriver());
					}
					template.setValue("DATA", block);
				}

				mSql =  template.getBlock("QUERY");

				assert mSql != null;
				assert mSql.length() > 0;
			}
		}

		return mSql;
	}

	public Insert hint(String hint)
	{
		clearGenerated();
		mHint = hint;

		return this;
	}

	public Insert into(String into)
	{
		if (null == into)		throw new IllegalArgumentException("into can't be null.");
		if (0 == into.length())	throw new IllegalArgumentException("into can't be empty.");

		clearGenerated();
		mInto = into;

		return this;
	}

	public Insert fieldSubselect(Select query)
	{
		_fieldSubselect(query);

		return this;
	}

	protected Insert _field(String field, Object value)
	{
		assert field != null;
		assert field.length() > 0;

		clearGenerated();
		if (!mFields.containsKey(field))
		{
			mFields.put(field, new ArrayList<Object>());
		}
		if (null == value)
		{
			mFields.get(field).add(SqlNull.NULL);
		}
		else
		{
			mFields.get(field).add(value);
		}

		return this;
	}

	public Insert fieldParameter(String field)
	{
		return fieldParameter(field, field);
	}

	public Insert fieldParameter(String field, String alias)
	{
		if (null == field)			throw new IllegalArgumentException("field can't be null.");
		if (0 == field.length())	throw new IllegalArgumentException("field can't be empty.");
		if (null == alias)			throw new IllegalArgumentException("alias can't be null.");
		if (0 == alias.length())	throw new IllegalArgumentException("alias can't be empty.");

		clearGenerated();

		addFieldParameter(alias);

		return _field(field, "?");
	}

	public Insert field(String field, char value)
	{
		return field(field, new Character(value));
	}

	public Insert field(String field, boolean value)
	{
		return field(field, Boolean.valueOf(value));
	}

	public Insert field(String field, byte value)
	{
		return field(field, new Byte(value));
	}

	public Insert field(String field, double value)
	{
		return field(field, new Double(value));
	}

	public Insert field(String field, float value)
	{
		return field(field, new Float(value));
	}

	public Insert field(String field, int value)
	{
		return field(field, new Integer(value));
	}

	public Insert field(String field, long value)
	{
		return field(field, new Long(value));
	}

	public Insert field(String field, short value)
	{
		return field(field, new Short(value));
	}

	public Insert field(String field, Select query)
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

	public Insert field(String field, Object value)
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

	public Insert fieldCustom(String field, String expression)
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

	public Insert fields(Object[] keyValues)
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

	public Insert fields(Object bean)
	throws DbQueryException
	{
		return fieldsFiltered(bean, null, null);
	}

	public Insert fieldsIncluded(Object bean, String[] includedFields)
	throws DbQueryException
	{
		return fieldsFiltered(bean, includedFields, null);
	}

	public Insert fieldsExcluded(Object bean, String[] excludedFields)
	throws DbQueryException
	{
		return fieldsFiltered(bean, null, excludedFields);
	}

	public Insert fieldsFiltered(Object bean, String[] includedFields, String[] excludedFields)
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

	public Insert fieldsParameters(Class beanClass)
	throws DbQueryException
	{
		return fieldsParametersExcluded(beanClass, null);
	}

	public Insert fieldsParametersExcluded(Class beanClass, String[] excludedFields)
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

	public Insert clone()
	{
        Insert new_instance = (Insert)super.clone();
		if (new_instance != null)
		{
			if (mFields != null)
			{
				new_instance.mFields = new LinkedHashMap<String, List<Object>>();

				List<Object>	values = null;

				for (String field : mFields.keySet())
				{
					values = mFields.get(field);
					if (values != null)
					{
						values = new ArrayList<Object>(values);
					}
					new_instance.mFields.put(field, values);
				}
			}
		}

		return new_instance;
	}
}

