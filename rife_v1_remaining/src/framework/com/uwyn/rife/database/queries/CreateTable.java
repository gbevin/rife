/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CreateTable.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.exceptions.*;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.capabilities.Capabilities;
import com.uwyn.rife.datastructures.EnumClass;
import com.uwyn.rife.site.Constrained;
import com.uwyn.rife.site.ConstrainedBean;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.ConstrainedUtils;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.template.TemplateFactory;
import com.uwyn.rife.tools.ClassUtils;
import com.uwyn.rife.tools.StringUtils;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Object representation of a SQL "CREATE TABLE" query.
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
public class CreateTable extends AbstractQuery implements Cloneable
{
	private String						mTable = null;
	private boolean						mTemporary = false;
	private Map<String, Column>			mColumnMapping = null;
	private List<PrimaryKey>			mPrimaryKeys = null;
	private List<ForeignKey>			mForeignKeys = null;
	private List<UniqueConstraint>		mUniqueConstraints = null;
	private List<CheckConstraint>		mCheckConstraints = null;
	
	public static final Nullable NULL = new Nullable("NULL");
	public static final Nullable NOTNULL = new Nullable("NOTNULL");

	public static final ViolationAction NOACTION = new ViolationAction("NOACTION");
	public static final ViolationAction RESTRICT = new ViolationAction("RESTRICT");
	public static final ViolationAction CASCADE = new ViolationAction("CASCADE");
	public static final ViolationAction SETNULL = new ViolationAction("SETNULL");
	public static final ViolationAction SETDEFAULT = new ViolationAction("SETDEFAULT");

	public CreateTable(Datasource datasource)
	{
		super(datasource);


		if (null == datasource)	throw new IllegalArgumentException("datasource can't be null.");

		clear();
	}

	public void clear()
	{
		super.clear();

		mTable = null;
		mTemporary = false;
		mColumnMapping = new LinkedHashMap<String, Column>();
		mPrimaryKeys = new ArrayList<PrimaryKey>();
		mForeignKeys = new ArrayList<ForeignKey>();
		mUniqueConstraints = new ArrayList<UniqueConstraint>();
		mCheckConstraints = new ArrayList<CheckConstraint>();

		assert 0 == mColumnMapping.size();
		assert 0 == mPrimaryKeys.size();
		assert 0 == mForeignKeys.size();
		assert 0 == mUniqueConstraints.size();
		assert 0 == mCheckConstraints.size();
	}

	public Capabilities getCapabilities()
	{
		return null;
	}

	public String getTable()
	{
		return mTable;
	}

	public boolean isTemporary()
	{
		return mTemporary;
	}

	public Map<String, Column> getColumnMapping()
	{
		return mColumnMapping;
	}

	public List<PrimaryKey> getPrimaryKeys()
	{
		return mPrimaryKeys;
	}

	public List<ForeignKey> getForeignKeys()
	{
		return mForeignKeys;
	}

	public List<UniqueConstraint> getUniqueConstraints()
	{
		return mUniqueConstraints;
	}

	public List<CheckConstraint> getCheckConstraints()
	{
		return mCheckConstraints;
	}

	public String getSql()
	throws DbQueryException
	{
		if (null == mSql)
		{
			if (null == getTable())
			{
				throw new TableNameRequiredException("CreateTable");
			}
			else if (0 == mColumnMapping.size())
			{
				throw new ColumnsRequiredException("CreateTable");
			}
			else
			{
				Template	template = TemplateFactory.SQL.get("sql."+StringUtils.encodeClassname(mDatasource.getAliasedDriver())+".create_table");
				String 		block = null;
				String		sql = null;

				String				columns = null;
				ArrayList<String>	column_list = new ArrayList<String>();
				for (Column column : mColumnMapping.values())
				{
					column_list.add(column.getSql(template));
				}
				columns = StringUtils.join(column_list, template.getBlock("SEPERATOR"));

				if (mTemporary)
				{
					block = template.getBlock("TEMPORARY");
					if (0 == block.length())
					{
						throw new UnsupportedSqlFeatureException("TEMPORARY", mDatasource.getAliasedDriver());
					}
					template.setValue("TEMPORARY", block);
				}

				String primary = "";
				if (mPrimaryKeys.size() > 0)
				{
					ArrayList<String>	constraints = new ArrayList<String>();
					for (PrimaryKey primary_key : mPrimaryKeys)
					{
						sql = primary_key.getSql(template);
						if (sql.length() > 0)
						{
							constraints.add(sql);
						}
					}
					if (constraints.size() > 0)
					{
						primary = template.getBlock("SEPERATOR")+StringUtils.join(constraints, template.getBlock("SEPERATOR"));
					}
				}
				
				String foreign = "";
				if (mForeignKeys.size() > 0)
				{
					ArrayList<String>	constraints = new ArrayList<String>();
					for (ForeignKey foreign_key : mForeignKeys)
					{
						sql = foreign_key.getSql(template);
						if (sql.length() > 0)
						{
							constraints.add(sql);
						}
					}
					if (constraints.size() > 0)
					{
						foreign = template.getBlock("SEPERATOR")+StringUtils.join(constraints, template.getBlock("SEPERATOR"));
					}
				}

				String unique = "";
				if (mUniqueConstraints.size() > 0)
				{
					ArrayList<String>	constraints = new ArrayList<String>();
					for (UniqueConstraint unique_constraint : mUniqueConstraints)
					{
						sql = unique_constraint.getSql(template);
						if (sql.length() > 0)
						{
							constraints.add(sql);
						}
					}
					if (constraints.size() > 0)
					{
						unique = template.getBlock("SEPERATOR")+StringUtils.join(constraints, template.getBlock("SEPERATOR"));
					}
				}

				String check = "";
				if (mCheckConstraints.size() > 0)
				{
					ArrayList<String>	constraints = new ArrayList<String>();
					for (CheckConstraint check_constraint : mCheckConstraints)
					{
						sql = check_constraint.getSql(template);
						if (sql.length() > 0)
						{
							constraints.add(sql);
						}
					}
					if (constraints.size() > 0)
					{
						check = template.getBlock("SEPERATOR")+StringUtils.join(constraints, template.getBlock("SEPERATOR"));
					}
				}

				template.setValue("TABLE", mTable);
				template.setValue("COLUMNS", columns);
				template.setValue("PRIMARY_KEYS", primary);
				template.setValue("FOREIGN_KEYS", foreign);
				template.setValue("UNIQUE_CONSTRAINTS", unique);
				template.setValue("CHECKS", check);

				mSql = template.getBlock("QUERY");

				assert mSql != null;
				assert mSql.length() > 0;
			}
		}

		return mSql;
	}

	public CreateTable table(String table)
	{
		if (null == table)			throw new IllegalArgumentException("table can't be null.");
		if (0 == table.length())	throw new IllegalArgumentException("table can't be empty.");

		mTable = table;
		clearGenerated();

		return this;
	}

	public CreateTable temporary(boolean temporary)
	{
		mTemporary = temporary;
		clearGenerated();

		return this;
	}

	public CreateTable column(String name, Class type)
	{
		return column(name, type, -1, -1, null, null);
	}

	public CreateTable column(String name, Class type, String typeAttribute)
	{
		return column(name, type, -1, -1, typeAttribute, null);
	}

	public CreateTable column(String name, Class type, int precision)
	{
		return column(name, type, precision, -1, null, null);
	}

	public CreateTable column(String name, Class type, int precision, String typeAttribute)
	{
		return column(name, type, precision, -1, typeAttribute, null);
	}

	public CreateTable column(String name, Class type, int precision, int scale)
	{
		return column(name, type, precision, scale, null, null);
	}

	public CreateTable column(String name, Class type, int precision, int scale, String typeAttribute)
	{
		return column(name, type, precision, scale, typeAttribute, null);
	}

	public CreateTable column(String name, Class type, Nullable nullable)
	{
		return column(name, type, -1, -1, null, nullable);
	}

	public CreateTable column(String name, Class type, String typeAttribute, Nullable nullable)
	{
		return column(name, type, -1, -1, typeAttribute, nullable);
	}

	public CreateTable column(String name, Class type, int precision, Nullable nullable)
	{
		return column(name, type, precision, -1, null, nullable);
	}

	public CreateTable column(String name, Class type, int precision, String typeAttribute, Nullable nullable)
	{
		return column(name, type, precision, -1, typeAttribute, nullable);
	}

	public CreateTable column(String name, Class type, int precision, int scale, Nullable nullable)
	{
		return column(name, type, precision, scale, null, nullable);
	}

	public CreateTable column(String name, Class type, int precision, int scale, String typeAttribute, Nullable nullable)
	{
		if (null == name)		throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty.");
		if (null == type)		throw new IllegalArgumentException("type can't be null.");

		mColumnMapping.put(name, new Column(name, type, precision, scale, typeAttribute, nullable));
		clearGenerated();
		return this;
	}

	public CreateTable columns(Object[] keyValues)
	{
		if (null == keyValues)	throw new IllegalArgumentException("keyValues can't be null.");

		for (int i = 0; i < keyValues.length; i+=2)
		{
			if (null != keyValues[i])
			{
				column(keyValues[i].toString(), (Class)keyValues[i+1]);
			}
		}

		return this;
	}

	public CreateTable precision(String name, int precision)
	{
		return precision(name, precision, -1);
	}

	public CreateTable precision(String name, int precision, int scale)
	{
		if (null == name)						throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())					throw new IllegalArgumentException("name can't be empty.");
		if (!mColumnMapping.containsKey(name))	throw new IllegalArgumentException("the '"+name+"' column hasn't been defined.");

		Column column = mColumnMapping.get(name);
		column.setPrecision(precision);
		column.setScale(scale);

		return this;
	}

	public CreateTable nullable(String name, Nullable nullable)
	{
		if (null == name)						throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())					throw new IllegalArgumentException("name can't be empty.");
		if (!mColumnMapping.containsKey(name))	throw new IllegalArgumentException("the '"+name+"' column hasn't been defined.");

		Column column = mColumnMapping.get(name);
		column.setNullable(nullable);

		return this;
	}

	public CreateTable defaultValue(String name, char value)
	{
		return defaultValue(name, new Character(value));
	}

	public CreateTable defaultValue(String name, boolean value)
	{
		return defaultValue(name, Boolean.valueOf(value));
	}

	public CreateTable defaultValue(String name, byte value)
	{
		return defaultValue(name, new Byte(value));
	}

	public CreateTable defaultValue(String name, double value)
	{
		return defaultValue(name, new Double(value));
	}

	public CreateTable defaultValue(String name, float value)
	{
		return defaultValue(name, new Float(value));
	}

	public CreateTable defaultValue(String name, int value)
	{
		return defaultValue(name, new Integer(value));
	}

	public CreateTable defaultValue(String name, long value)
	{
		return defaultValue(name, new Long(value));
	}

	public CreateTable defaultValue(String name, short value)
	{
		return defaultValue(name, new Short(value));
	}

	public CreateTable defaultValue(String name, Object value)
	{
		if (null == name)						throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())					throw new IllegalArgumentException("name can't be empty.");
		if (!mColumnMapping.containsKey(name))	throw new IllegalArgumentException("the '"+name+"' column hasn't been defined.");

		Column column = mColumnMapping.get(name);
		column.setDefault(mDatasource.getSqlConversion().getSqlValue(value));

		return this;
	}

	public CreateTable defaultFunction(String name, String defaultFunction)
	{
		if (null == name)						throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())					throw new IllegalArgumentException("name can't be empty.");
		if (!mColumnMapping.containsKey(name))	throw new IllegalArgumentException("the '"+name+"' column hasn't been defined.");
		if (null == defaultFunction)			throw new IllegalArgumentException("defaultFunction can't be null.");
		if (0 == defaultFunction.length())		throw new IllegalArgumentException("defaultFunction can't be empty.");

		Column column = mColumnMapping.get(name);
		column.setDefault(defaultFunction);

		return this;
	}

	public CreateTable customAttribute(String name, String attribute)
	{
		if (null == name)						throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())					throw new IllegalArgumentException("name can't be empty.");
		if (!mColumnMapping.containsKey(name))	throw new IllegalArgumentException("the '"+name+"' column hasn't been defined.");

		Column column = mColumnMapping.get(name);
		column.addCustomAttribute(attribute);

		return this;
	}

	public CreateTable columns(Class beanClass)
	throws DbQueryException
	{
		return columnsFiltered(beanClass, null, null);
	}

	public CreateTable columnsIncluded(Class beanClass, String[] includedFields)
	throws DbQueryException
	{
		return columnsFiltered(beanClass, includedFields, null);
	}

	public CreateTable columnsExcluded(Class beanClass, String[] excludedFields)
	throws DbQueryException
	{
		return columnsFiltered(beanClass, null, excludedFields);
	}

	public CreateTable columnsFiltered(Class beanClass, String[] includedFields, String[] excludedFields)
	throws DbQueryException
	{
		if (null == beanClass)	throw new IllegalArgumentException("beanClass can't be null.");

		Constrained constrained = ConstrainedUtils.getConstrainedInstance(beanClass);

		// handle constrained bean
		if (constrained != null)
		{
			ConstrainedBean constrained_bean = constrained.getConstrainedBean();
			if (constrained_bean != null)
			{
				// handle multi-column uniques
				if (constrained_bean.hasUniques())
				{
					for (String[] o : (List<String[]>)constrained_bean.getUniques())
					{
						unique(o);
					}
				}
			}
		}

		// handle properties
		ConstrainedProperty	constrained_property = null;
		Map<String, Class>	column_types = QueryHelper.getBeanPropertyTypes(beanClass, includedFields, excludedFields);
		Class				column_type = null;
		Column				column = null;
		for (String column_name : column_types.keySet())
		{
			if (!ConstrainedUtils.persistConstrainedProperty(constrained, column_name, null))
			{
				continue;
			}

			column_type = column_types.get(column_name);
			column = new Column(column_name, column_type);
			mColumnMapping.put(column_name, column);

			String[] in_list_values = null;

			in_list_values = ClassUtils.getEnumClassValues(column_type);

			if (constrained != null)
			{
				constrained_property = constrained.getConstrainedProperty(column_name);
				if (constrained_property != null)
				{
					if (constrained_property.isNotNull())
					{
						nullable(column_name, NOTNULL);
					}

					if (constrained_property.isIdentifier())
					{
						primaryKey(column_name);
					}

					if (constrained_property.isUnique())
					{
						unique(column_name);
					}

					if (constrained_property.isNotEmpty())
					{
						if (ClassUtils.isNumeric(column_type))
						{
							check(column_name+" != 0");
						}
						else if (ClassUtils.isText(column_type))
						{
							check(column_name+" != ''");
						}
					}

					if (constrained_property.isNotEqual())
					{
						if (ClassUtils.isNumeric(column_type))
						{
							check(column_name+" != "+constrained_property.getNotEqual());
						}
						else if (ClassUtils.isText(column_type))
						{
							check(column_name+" != '"+StringUtils.encodeSql(constrained_property.getNotEqual().toString())+"'");
						}
					}

					if (constrained_property.hasPrecision())
					{
						if (constrained_property.hasScale())
						{
							precision(column_name, constrained_property.getPrecision(), constrained_property.getScale());
						}
						else
						{
							precision(column_name, constrained_property.getPrecision());
						}
					}

					if (constrained_property.isInList())
					{
						in_list_values = constrained_property.getInList().clone();
					}

					if (constrained_property.hasDefaultValue())
					{
						defaultValue(column_name, constrained_property.getDefaultValue());
					}

					if (constrained_property.hasManyToOne() &&
						ClassUtils.isBasic(column_type))
					{
						ConstrainedProperty.ManyToOne many_to_one = constrained_property.getManyToOne();

						if (null == many_to_one.getDerivedTable())
						{
							throw new MissingManyToOneTableException(beanClass, constrained_property.getPropertyName());
						}

						if (null == many_to_one.getColumn())
						{
							throw new MissingManyToOneColumnException(beanClass, constrained_property.getPropertyName());
						}

						foreignKey(many_to_one.getDerivedTable(), constrained_property.getPropertyName(), many_to_one.getColumn(), many_to_one.getOnUpdate(), many_to_one.getOnDelete());
					}
				}
			}

			// handle in list constraints
			if (in_list_values != null)
			{
				for (int i = 0; i < in_list_values.length; i++)
				{
					in_list_values[i] = StringUtils.encodeSql(in_list_values[i]);
				}

				StringBuilder check_constraint = new StringBuilder();
				String seperator = "";
				if (ClassUtils.isText(column_type) || column_type.isEnum())
				{
					seperator = "'";
				}
				check_constraint.append(column_name);
				check_constraint.append(" IS NULL OR ");
				check_constraint.append(column_name);
				check_constraint.append(" IN (");
				check_constraint.append(seperator);
				check_constraint.append(StringUtils.join(in_list_values, seperator+","+seperator));
				check_constraint.append(seperator);
				check_constraint.append(")");
				check(check_constraint.toString());
			}
		}
		clearGenerated();

		return this;
	}

	public CreateTable primaryKey(String column)
	{
		return primaryKey(null, column);
	}

	public CreateTable primaryKey(String[] columns)
	{
		return primaryKey(null, columns);
	}

	public CreateTable primaryKey(String name, String column)
	{
		return primaryKey(name, new String[] {column});
	}

	public CreateTable primaryKey(String name, String[] columns)
	{
		if (name != null && 0 == name.length())	throw new IllegalArgumentException("name can't be empty.");
		if (null == columns)					throw new IllegalArgumentException("columns array can't be null.");
		if (0 == columns.length)				throw new IllegalArgumentException("columns array can't be empty.");

		for (String column : columns)
		{
			nullable(column, CreateTable.NOTNULL);
		}
		mPrimaryKeys.add(new PrimaryKey(name, columns));
		clearGenerated();

		return this;
	}

	public CreateTable foreignKey(String foreignTable, String localColumn, String foreignColumn)
	{
		return foreignKey(null, foreignTable, localColumn, foreignColumn, null, null);
	}

	public CreateTable foreignKey(String foreignTable, String localColumn, String foreignColumn, ViolationAction onUpdate, ViolationAction onDelete)
	{
		return foreignKey(null, foreignTable, new String[] {localColumn, foreignColumn}, onUpdate, onDelete);
	}

	public CreateTable foreignKey(String foreignTable, String[] columnsMapping)
	{
		return foreignKey(null, foreignTable, columnsMapping, null, null);
	}

	public CreateTable foreignKey(String foreignTable, String[] columnsMapping, ViolationAction onUpdate, ViolationAction onDelete)
	{
		return foreignKey(null, foreignTable, columnsMapping, onUpdate, onDelete);
	}

	public CreateTable foreignKey(String name, String foreignTable, String localColumn, String foreignColumn)
	{
		return foreignKey(name, foreignTable, localColumn, foreignColumn, null, null);
	}

	public CreateTable foreignKey(String name, String foreignTable, String localColumn, String foreignColumn, ViolationAction onUpdate, ViolationAction onDelete)
	{
		return foreignKey(name, foreignTable, new String[] {localColumn, foreignColumn}, onUpdate, onDelete);
	}

	public CreateTable foreignKey(String name, String foreignTable, String[] columnsMapping)
	{
		return foreignKey(name, foreignTable, columnsMapping, null, null);
	}

	public CreateTable foreignKey(String name, String foreignTable, String[] columnsMapping, ViolationAction onUpdate, ViolationAction onDelete)
	{
		if (name != null && 0 == name.length())	throw new IllegalArgumentException("name can't be empty.");
		if (null == foreignTable)				throw new IllegalArgumentException("foreignTable can't be null.");
		if (0 == foreignTable.length())			throw new IllegalArgumentException("foreignTable can't be empty.");
		if (null == columnsMapping)				throw new IllegalArgumentException("columnsMapping array can't be null.");
		if (0 == columnsMapping.length)			throw new IllegalArgumentException("columnsMapping array can't be empty.");
		if (columnsMapping.length%2 != 0)		throw new IllegalArgumentException("columnsMapping array isn't valid, each local column should be mapped to a foreign one.");

		mForeignKeys.add(new ForeignKey(name, foreignTable, columnsMapping, onUpdate, onDelete));

		clearGenerated();
		return this;
	}

	public CreateTable unique(String column)
	{
		return unique(null, column);
	}

	public CreateTable unique(String[] columns)
	{
		return unique(null, columns);
	}

	public CreateTable unique(String name, String column)
	{
		return unique(name, new String[] {column});
	}

	public CreateTable unique(String name, String[] columns)
	{
		if (name != null && 0 == name.length())	throw new IllegalArgumentException("name can't be empty.");
		if (null == columns)					throw new IllegalArgumentException("columns array can't be null.");
		if (0 == columns.length)				throw new IllegalArgumentException("columns array can't be empty.");

		mUniqueConstraints.add(new UniqueConstraint(name, columns));

		clearGenerated();
		return this;
	}

	public CreateTable check(String expression)
	{
		return check(null, expression);
	}

	public CreateTable check(String name, String expression)
	{
		if (name != null && 0 == name.length())	throw new IllegalArgumentException("name can't be empty.");
		if (null == expression)					throw new IllegalArgumentException("expression can't be null.");
		if (0 == expression.length())			throw new IllegalArgumentException("expression can't be empty.");

		mCheckConstraints.add(new CheckConstraint(name, expression));

		clearGenerated();
		return this;
	}

	public CreateTable clone()
	{
		CreateTable new_instance = (CreateTable)super.clone();
		if (new_instance != null)
		{
			if (mColumnMapping != null)
			{
				new_instance.mColumnMapping = new LinkedHashMap<String, Column>();

				Column	column = null;
				for (String name : mColumnMapping.keySet())
				{
					column = mColumnMapping.get(name);
					new_instance.mColumnMapping.put(name, column.clone());
				}
			}

			if (mPrimaryKeys != null)
			{
				new_instance.mPrimaryKeys = new ArrayList<PrimaryKey>();

				for (PrimaryKey primary_key : mPrimaryKeys)
				{
					new_instance.mPrimaryKeys.add(primary_key.clone());
				}
			}

			if (mForeignKeys != null)
			{
				new_instance.mForeignKeys = new ArrayList<ForeignKey>();

				for (ForeignKey foreign_key : mForeignKeys)
				{
					new_instance.mForeignKeys.add(foreign_key.clone());
				}
			}

			if (mUniqueConstraints != null)
			{
				new_instance.mUniqueConstraints = new ArrayList<UniqueConstraint>();

				for (UniqueConstraint unique_constraint : mUniqueConstraints)
				{
					new_instance.mUniqueConstraints.add(unique_constraint.clone());
				}
			}

			if (mCheckConstraints != null)
			{
				new_instance.mCheckConstraints = new ArrayList<CheckConstraint>();

				for (CheckConstraint check_constraint : mCheckConstraints)
				{
					new_instance.mCheckConstraints.add(check_constraint.clone());
				}
			}
		}

		return new_instance;
	}

	public class PrimaryKey extends ColumnsConstraint implements Cloneable
	{
		PrimaryKey(String name, String[] columns)
		{
			super(name, columns);
		}

		String getSql(Template template)
		throws DbQueryException
		{
			assert template != null;

			String result = null;

			if (getName() != null &&
				template.hasValueId("PRIMARY_KEY_NAME"))
			{
				template.setValue("NAME", getName());
				template.setValue("PRIMARY_KEY_NAME", template.getBlock("PRIMARY_KEY_NAME"));
			}

			template.setValue("COLUMN_NAMES", StringUtils.join(getColumns(), template.getBlock("SEPERATOR")));

			result = template.getBlock("PRIMARY_KEY");
			if (0 == result.length())
			{
				throw new UnsupportedSqlFeatureException("PRIMARY KEY", mDatasource.getAliasedDriver());
			}

			assert result != null;

			return result;
		}

		public PrimaryKey clone()
		{
			return (PrimaryKey)super.clone();
		}
	}

	public class ForeignKey extends ColumnsConstraint implements Cloneable
	{
		private String			mForeignTable = null;
		private ViolationAction	mOnUpdate = null;
		private ViolationAction	mOnDelete = null;

		ForeignKey(String name, String foreignTable, String[] columnsMapping, ViolationAction onUpdate, ViolationAction onDelete)
		{
			super(name, columnsMapping);
			setForeignTable(foreignTable);
			setOnUpdate(onUpdate);
			setOnDelete(onDelete);
		}

		String getSql(Template template)
		throws DbQueryException
		{
			assert template != null;

			String	block = null;
			String	result = null;

			if (getName() != null)
			{
				template.setValue("NAME", getName());
				template.setValue("FOREIGN_KEY_NAME", template.getBlock("FOREIGN_KEY_NAME"));
			}

			template.setValue("FOREIGN_TABLE", getForeignTable());

			String violations_actions = "";
			if (getOnUpdate() != null)
			{
				block = template.getBlock("ON_UPDATE_"+getOnUpdate().toString());
				if (0 == block.length())
				{
					throw new UnsupportedSqlFeatureException("ON UPDATE "+getOnUpdate().toString(), mDatasource.getAliasedDriver());
				}
				template.setValue("ON_UPDATE_ACTION", block);
				block = template.getBlock("ON_UPDATE");
				if (0 == block.length())
				{
					throw new UnsupportedSqlFeatureException("ON UPDATE", mDatasource.getAliasedDriver());
				}
				violations_actions += block;
			}

			if (getOnDelete() != null)
			{
				block = template.getBlock("ON_DELETE_"+getOnDelete().toString());
				if (0 == block.length())
				{
					throw new UnsupportedSqlFeatureException("ON DELETE "+getOnDelete().toString(), mDatasource.getAliasedDriver());
				}
				template.setValue("ON_DELETE_ACTION", block);
				block = template.getBlock("ON_DELETE");
				if (0 == block.length())
				{
					throw new UnsupportedSqlFeatureException("ON DELETE", mDatasource.getAliasedDriver());
				}
				violations_actions += block;
			}
			template.setValue("VIOLATION_ACTIONS", violations_actions);

			String[] local_columns = new String[getColumns().length/2];
			String[] foreign_columns = new String[getColumns().length/2];
			for (int i = 0; i < getColumns().length; i+=2)
			{
				local_columns[i/2] = getColumns()[i];
				foreign_columns[i/2] = getColumns()[i+1];
			}
			template.setValue("LOCAL_COLUMN_NAMES", StringUtils.join(local_columns, template.getBlock("SEPERATOR")));
			template.setValue("FOREIGN_COLUMN_NAMES", StringUtils.join(foreign_columns, template.getBlock("SEPERATOR")));

			result = template.getBlock("FOREIGN_KEY");
			if (0 == result.length())
			{
				throw new UnsupportedSqlFeatureException("FOREIGN KEY", mDatasource.getAliasedDriver());
			}

			assert result != null;

			return result;
		}

		public String getForeignTable()
		{
			return mForeignTable;
		}

		void setForeignTable(String foreignTable)
		{
			assert foreignTable != null;
			assert foreignTable.length() > 0;

			mForeignTable = foreignTable;
		}

		public ViolationAction getOnUpdate()
		{
			return mOnUpdate;
		}

		void setOnUpdate(ViolationAction onUpdate)
		{
			mOnUpdate = onUpdate;
		}

		public ViolationAction getOnDelete()
		{
			return mOnDelete;
		}

		void setOnDelete(ViolationAction onDelete)
		{
			mOnDelete = onDelete;
		}

		public ForeignKey clone()
		{
			return (ForeignKey)super.clone();
		}
	}

	public class UniqueConstraint extends ColumnsConstraint implements Cloneable
	{
		UniqueConstraint(String name, String[] columns)
		{
			super(name, columns);
		}

		String getSql(Template template)
		throws DbQueryException
		{
			assert template != null;

			String result = null;

			if (getName() != null)
			{
				template.setValue("NAME", getName());
				template.setValue("UNIQUE_CONSTRAINT_NAME", template.getBlock("UNIQUE_CONSTRAINT_NAME"));
			}

			template.setValue("COLUMN_NAMES", StringUtils.join(getColumns(), template.getBlock("SEPERATOR")));

			result = template.getBlock("UNIQUE_CONSTRAINT");
			if (0 == result.length())
			{
				throw new UnsupportedSqlFeatureException("UNIQUE", mDatasource.getAliasedDriver());
			}

			return result;
		}

		public UniqueConstraint clone()
		{
			return (UniqueConstraint)super.clone();
		}
	}

	public class CheckConstraint extends Constraint implements Cloneable
	{
		private String mExpression = null;

		CheckConstraint(String name, String expression)
		{
			super(name);
			setExpression(expression);
		}

		String getSql(Template template)
		throws DbQueryException
		{
			assert template != null;

			String result = null;

			if (getName() != null &&
				template.hasValueId("CHECK_NAME"))
			{
				template.setValue("NAME", getName());
				template.setValue("CHECK_NAME", template.getBlock("CHECK_NAME"));
			}

			if (template.hasValueId("EXPRESSION"))
			{
				template.setValue("EXPRESSION", getExpression());
			}

			result = template.getBlock("CHECK");
			if (0 == result.length())
			{
				throw new UnsupportedSqlFeatureException("CHECK", mDatasource.getAliasedDriver());
			}

			return result;
		}

		public String getExpression()
		{
			return mExpression;
		}

		void setExpression(String expression)
		{
			mExpression = expression;
		}

		public CheckConstraint clone()
		{
			return (CheckConstraint)super.clone();
		}
	}

	public abstract class ColumnsConstraint extends Constraint implements Cloneable
	{
		private String[] 	mColumns = null;

		ColumnsConstraint(String name, String[] columns)
		{
			super(name);

			setColumns(columns);
		}

		public String[] getColumns()
		{
			return mColumns;
		}

		void setColumns(String[] columns)
		{
			assert columns != null;
			assert columns.length > 0;

			mColumns = columns;
		}

		public ColumnsConstraint clone()
		{
			return (ColumnsConstraint)super.clone();
		}
	}

	public abstract class Constraint implements Cloneable
	{
		private String	mName = null;

		Constraint(String name)
		{
			setName(name);
		}

		abstract String getSql(Template template) throws DbQueryException;

		public String getName()
		{
			return mName;
		}

		void setName(String name)
		{
			assert null == name || name.length() > 0;

			mName = name;
		}

		public Constraint clone()
		{
			Constraint new_instance = null;
			try
			{
				new_instance = (Constraint)super.clone();
			}
			catch (CloneNotSupportedException e)
			{
				new_instance = null;
			}

			return new_instance;
		}
	}

	public class Column implements Cloneable
	{
		private String				mName = null;
		private Class				mType = null;
		private int					mPrecision = -1;
		private int					mScale = -1;
		private String				mTypeAttribute = null;
		private Nullable			mNullable = null;
		private String				mDefault = null;
		private ArrayList<String>	mCustomAttributes = new ArrayList<String>();

		Column(String name, Class type)
		{
			setName(name);
			setType(type);
		}

		Column(String name, Class type, int precision, int scale, String typeAttribute, Nullable nullable)
		{
			setName(name);
			setType(type);
			setPrecision(precision);
			setScale(scale);
			setTypeAttribute(typeAttribute);
			setNullable(nullable);
		}

		String getSql(Template template)
		throws DbQueryException
		{
			assert template != null;

			String	block = null;
			String	result = null;

			template.setValue("NAME", getName());
			template.setValue("TYPE", mDatasource.getSqlConversion().getSqlType(getType(), getPrecision(), getScale()));
			if (mTypeAttribute != null)
			{
				template.appendValue("TYPE", " ");
				template.appendValue("TYPE", mTypeAttribute);
			}

			if (getNullable() != null)
			{
				block = template.getBlock(getNullable().toString());
				if (0 == block.length())
				{
					throw new UnsupportedSqlFeatureException("NULLABLE "+getNullable().toString(), mDatasource.getAliasedDriver());
				}
				template.setValue("NULLABLE", block);
			}
			if (getDefault() != null)
			{
				template.setValue("V", getDefault());
				block = template.getBlock("DEFAULT");
				if (0 == block.length())
				{
					throw new UnsupportedSqlFeatureException("DEFAULT", mDatasource.getAliasedDriver());
				}
				template.setValue("DEFAULT", block);
				template.removeValue("V");
			}
			if (getCustomAttributes().size() > 0)
			{
				template.setValue("V", StringUtils.join(getCustomAttributes(), " "));
				block = template.getBlock("CUSTOM_ATTRIBUTES");
				if (0 == block.length())
				{
					throw new UnsupportedSqlFeatureException("CUSTOM_ATTRIBUTES", mDatasource.getAliasedDriver());
				}
				template.setValue("CUSTOM_ATTRIBUTES", block);
				template.removeValue("V");
			}
			result = template.getBlock("COLUMN");
			if (0 == result.length())
			{
				throw new UnsupportedSqlFeatureException("COLUMN", mDatasource.getAliasedDriver());
			}
			template.removeValue("NAME");
			template.removeValue("TYPE");
			template.removeValue("NULLABLE");
			template.removeValue("DEFAULT");
			template.removeValue("CUSTOM_ATTRIBUTES");

			assert result.length() > 0;

			return result;
		}

		public String getName()
		{
			return mName;
		}

		void setName(String name)
		{
			assert name != null;
			assert name.length() > 0;

			mName = name;
		}

		public Class getType()
		{
			return mType;
		}

		void setType(Class type)
		{
			assert type != null;

			mType = type;
		}

		public int getPrecision()
		{
			return mPrecision;
		}

		void setPrecision(int precision)
		{
			assert precision >= -1;

			mPrecision = precision;
		}

		public int getScale()
		{
			return mScale;
		}

		void setScale(int scale)
		{
			assert scale >= -1;

			mScale = scale;
		}

		public String getTypeAttribute()
		{
			return mTypeAttribute;
		}

		void setTypeAttribute(String typeAttribute)
		{
			mTypeAttribute = typeAttribute;
		}

		public Nullable getNullable()
		{
			return mNullable;
		}

		void setNullable(Nullable nullable)
		{
			mNullable = nullable;
		}

		public String getDefault()
		{
			return mDefault;
		}

		void setDefault(String defaultStatement)
		{
			mDefault = defaultStatement;
		}

		void addCustomAttribute(String attribute)
		{
			assert attribute != null;
			assert attribute.length() > 0;

			mCustomAttributes.add(attribute);
		}

		public ArrayList<String> getCustomAttributes()
		{
			return mCustomAttributes;
		}

		public Column clone()
		{
			Column new_instance = null;
			try
			{
				new_instance = (Column)super.clone();

				if (mCustomAttributes != null)
				{
					new_instance.mCustomAttributes = new ArrayList<String>();
					new_instance.mCustomAttributes.addAll(mCustomAttributes);
				}
			}
			catch (CloneNotSupportedException e)
			{
				new_instance = null;
			}

			return new_instance;
		}
	}

	public static class ViolationAction extends EnumClass<String>
	{
		ViolationAction(String identifier)
		{
			super(identifier);
		}
	}

	public static class Nullable extends EnumClass<String>
	{
		Nullable(String identifier)
		{
			super(identifier);
		}
	}
}
