/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Select.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import java.util.*;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.capabilities.Capabilities;
import com.uwyn.rife.database.capabilities.Capability;
import com.uwyn.rife.database.exceptions.DbQueryException;
import com.uwyn.rife.database.exceptions.TableNameOrFieldsRequiredException;
import com.uwyn.rife.database.exceptions.UnsupportedSqlFeatureException;
import com.uwyn.rife.datastructures.EnumClass;
import com.uwyn.rife.site.Constrained;
import com.uwyn.rife.site.ConstrainedBean;
import com.uwyn.rife.site.ConstrainedUtils;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.template.TemplateFactory;
import com.uwyn.rife.tools.StringUtils;

/**
 * Object representation of a SQL "SELECT" query.
 * 
 * <p>This object may be used to dynamically construct a SQL statement in a
 * database-independent fashion. After it is finished, it may be executed using
 * one of the query methods on {@link com.uwyn.rife.database.DbQueryManager
 * DbQueryManager}.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @author Steven Grimm (koreth[remove] at midwinter dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public class Select extends AbstractWhereQuery<Select> implements Cloneable, ReadQuery
{
	private String			mHint = null;
	private List<String>	mFields = null;
	private String			mFrom = null;
	private List<Join>		mJoins = null;
	private List<String>	mGroupBy = null;
	private List<String>	mHaving = null;
	private boolean			mDistinct = false;
	private List<String>	mDistinctOn = null;
	private List<Union>		mUnions = null;
	private List<OrderBy>	mOrderBy = null;
	private int				mLimit = -1;
	private int				mOffset = -1;

	private Capabilities	mCapabilities = null;

	private Class			mConstrainedClass = null;

	public static final JoinCondition NATURAL = new JoinCondition("NATURAL");
	public static final JoinCondition ON = new JoinCondition("ON");
	public static final JoinCondition USING = new JoinCondition("USING");

	public static final JoinType LEFT = new JoinType("LEFT");
	public static final JoinType RIGHT = new JoinType("RIGHT");
	public static final JoinType FULL = new JoinType("FULL");

	public static final OrderByDirection ASC = new OrderByDirection("ASC");
	public static final OrderByDirection DESC = new OrderByDirection("DESC");

	public Select(Datasource datasource)
	{
		this(datasource, null);
	}

	public Select(Datasource datasource, Class constrainedClass)
	{
		super(datasource);

		if (null == datasource)	throw new IllegalArgumentException("datasource can't be null.");

		mConstrainedClass = constrainedClass;

		clear();
	}

	public void clear()
	{
		super.clear();

		mHint = null;
		mFields = new ArrayList<String>();
		mFrom = null;
		mJoins = new ArrayList<Join>();
		mGroupBy = new ArrayList<String>();
		mHaving = new ArrayList<String>();
		mDistinct = false;
		mDistinctOn = new ArrayList<String>();
		mUnions = new ArrayList<Union>();
		mOrderBy = new ArrayList<OrderBy>();
		mLimit = -1;
		mOffset = -1;
		mCapabilities = null;

		assert 0 == mFields.size();
		assert 0 == mJoins.size();
		assert 0 == mGroupBy.size();
		assert 0 == mHaving.size();
		assert 0 == mDistinctOn.size();
		assert 0 == mOrderBy.size();
	}

	public void clearGenerated()
	{
		super.clearGenerated();

		mCapabilities = null;
	}

	public String getHint()
	{
		return mHint;
	}

	public Collection<String> getFields()
	{
		return mFields;
	}

	public boolean isDistinct()
	{
		return mDistinct;
	}

	public Collection<String> getDistinctOn()
	{
		return mDistinctOn;
	}

	public String getFrom()
	{
		return mFrom;
	}

	public Collection<Join> getJoins()
	{
		return mJoins;
	}

	public Collection<String> getGroupBy()
	{
		return mGroupBy;
	}

	public Collection<String> getHaving()
	{
		return mHaving;
	}

	public Collection<Union> getUnions()
	{
		return mUnions;
	}

	public Collection<OrderBy> getOrderBy()
	{
		return mOrderBy;
	}

	public int getLimit()
	{
		return mLimit;
	}

	public int getOffset()
	{
		return mOffset;
	}

	protected Template getTemplate()
	{
		return TemplateFactory.SQL.get("sql."+StringUtils.encodeClassname(mDatasource.getAliasedDriver())+".select");
	}

	public Capabilities getCapabilities()
	{
		if (null == mCapabilities)
		{
			Capabilities capabilities = null;

			if (getLimit() != -1)
			{
				if (null == capabilities)
				{
					capabilities = new Capabilities();
				}

				capabilities.put(Capability.LIMIT, getLimit());
			}

			if (getLimitParameter() != null)
			{
				if (null == capabilities)
				{
					capabilities = new Capabilities();
				}

				capabilities.put(Capability.LIMIT_PARAMETER, getLimitParameter());
			}

			if (getOffset() != -1)
			{
				if (null == capabilities)
				{
					capabilities = new Capabilities();
				}

				capabilities.put(Capability.OFFSET, getOffset());
			}

			if (getOffsetParameter() != null)
			{
				if (null == capabilities)
				{
					capabilities = new Capabilities();
				}

				capabilities.put(Capability.OFFSET_PARAMETER, getOffsetParameter());
			}

			mCapabilities = capabilities;
		}

		return mCapabilities;
	}

	public String getSql()
	throws DbQueryException
	{
		Constrained constrained = ConstrainedUtils.getConstrainedInstance(mConstrainedClass);

		// handle constrained beans meta-data that needs to be handled after all the
		// rest
		if (constrained != null)
		{
			ConstrainedBean constrained_bean = constrained.getConstrainedBean();
			if (constrained_bean != null)
			{
				// handle default ordering if no order statements have been
				// defined yet
				if (constrained_bean.hasDefaultOrdering() &&
					0 == mOrderBy.size())
				{
					Iterator<ConstrainedBean.Order> ordering_it = constrained_bean.getDefaultOrdering().iterator();
					ConstrainedBean.Order order = null;
					while (ordering_it.hasNext())
					{
						order = ordering_it.next();
						orderBy(order.getPropertyName(), OrderByDirection.getDirection(order.getDirection().toString()));
					}
				}
			}
		}

		if (null == mFrom &&
			0 == mFields.size())
		{
			throw new TableNameOrFieldsRequiredException("Select");
		}
		else
		{
			if (null == mSql)
			{
				Template	template = getTemplate();
				String		block = null;
				
				if (mHint != null)
				{
					if (!template.hasValueId("HINT"))
					{
						throw new UnsupportedSqlFeatureException("HINT", mDatasource.getAliasedDriver());
					}
					template.setValue("EXPRESSION", mHint);
					template.setBlock("HINT", "HINT");
				}

				if (mDistinct)
				{
					if (0 == mDistinctOn.size())
					{
						block = template.getBlock("DISTINCT");
						if (0 == block.length())
						{
							throw new UnsupportedSqlFeatureException("DISTINCT", mDatasource.getAliasedDriver());
						}
						template.setValue("DISTINCT", block);
					}
					else
					{
						if (template.hasValueId("COLUMNS"))
						{
							template.setValue("COLUMNS", StringUtils.join(mDistinctOn, template.getBlock("SEPERATOR")));
						}
						block = template.getBlock("DISTINCTON");
						if (0 == block.length())
						{
							throw new UnsupportedSqlFeatureException("DISTINCT ON", mDatasource.getAliasedDriver());
						}
						template.setValue("DISTINCT", block);
					}
				}

				if (0 == mFields.size())
				{
					template.setValue("FIELDS", template.getBlock("ALLFIELDS"));
				}
				else
				{
					template.setValue("FIELDS", StringUtils.join(mFields, template.getBlock("SEPERATOR")));
				}

				if (null != mFrom)
				{
					template.setValue("TABLE", mFrom);
					block = template.getBlock("FROM");
					if (0 == block.length())
					{
						throw new UnsupportedSqlFeatureException("FROM", mDatasource.getAliasedDriver());
					}
					template.setValue("FROM", block);
				}

				if (mJoins.size() > 0)
				{
					ArrayList<String>	join_list = new ArrayList<String>();
					for (Join join : mJoins)
					{
						join_list.add(join.getSql(template));
					}
					template.setValue("JOINS", StringUtils.join(join_list, ""));
				}

				if (mWhere.length() > 0)
				{
					template.setValue("CONDITION", mWhere);
					block = template.getBlock("WHERE");
					if (0 == block.length())
					{
						throw new UnsupportedSqlFeatureException("WHERE", mDatasource.getAliasedDriver());
					}
					template.setValue("WHERE", block);
				}

				if (mGroupBy.size() > 0)
				{
					template.setValue("EXPRESSION", StringUtils.join(mGroupBy, template.getBlock("SEPERATOR")));
					block = template.getBlock("GROUPBY");
					if (0 == block.length())
					{
						throw new UnsupportedSqlFeatureException("GROUP BY", mDatasource.getAliasedDriver());
					}
					template.setValue("GROUPBY", block);
				}

				if (mHaving.size() > 0)
				{
					template.setValue("EXPRESSION", StringUtils.join(mHaving, template.getBlock("SEPERATOR")));
					block = template.getBlock("HAVING");
					if (0 == block.length())
					{
						throw new UnsupportedSqlFeatureException("HAVING", mDatasource.getAliasedDriver());
					}
					template.setValue("HAVING", block);
				}

				if (mUnions != null)
				{
					for (Union union : mUnions)
					{
						template.setValue("EXPRESSION", union.getExpression());
						if (union.isAll())
						{
							block = template.getBlock("UNION_ALL");
							if (0 == block.length())
							{
								throw new UnsupportedSqlFeatureException("UNION_ALL", mDatasource.getAliasedDriver());
							}
							template.appendBlock("UNION", "UNION_ALL");
						}
						else
						{
							block = template.getBlock("UNION");
							if (0 == block.length())
							{
								throw new UnsupportedSqlFeatureException("UNION", mDatasource.getAliasedDriver());
							}
							template.appendBlock("UNION", "UNION");
						}
					}
				}

				if (mOrderBy.size() > 0)
				{
					ArrayList<String>	orderby_list = new ArrayList<String>();
					for (OrderBy order_by : mOrderBy)
					{
						orderby_list.add(order_by.getSql(template));
					}
					template.setValue("ORDERBY_PARTS", StringUtils.join(orderby_list, template.getBlock("SEPERATOR")));
					block = template.getBlock("ORDERBY");
					if (0 == block.length())
					{
						throw new UnsupportedSqlFeatureException("ORDER BY", mDatasource.getAliasedDriver());
					}
					template.setValue("ORDERBY", block);
				}

				if (mLimit != -1 ||
					getLimitParameter() != null)
				{
					// integrate a default value for offset if that has been provided
					// by the template
					if (-1 == mOffset &&
						template.hasValueId("OFFSET_VALUE"))
					{
						String offset_value = template.getValue("OFFSET_VALUE");
						if (offset_value != null &&
							offset_value.trim().length() > 0)
						{
							mOffset = Integer.parseInt(offset_value);
						}
					}

					if (mOffset > -1 ||
						getOffsetParameter() != null)
					{
						if (template.hasValueId("OFFSET_VALUE"))
						{
							if (getOffsetParameter() != null)
							{
								template.setValue("OFFSET_VALUE", "?");
							}
							else
							{
								template.setValue("OFFSET_VALUE", mOffset);
							}
						}

						block = template.getBlock("OFFSET");
						if (0 == block.length())
						{
							if (!mExcludeUnsupportedCapabilities)
							{
								throw new UnsupportedSqlFeatureException("OFFSET", mDatasource.getAliasedDriver());
							}
						}
						else
						{
							template.setValue("OFFSET", block);
						}
					}

					if (template.hasValueId("LIMIT_VALUE"))
					{
						if (getLimitParameter() != null)
						{
							template.setValue("LIMIT_VALUE", "?");
						}
						else
						{
							template.setValue("LIMIT_VALUE", mLimit);
						}
					}

					block = template.getBlock("LIMIT");
					if (0 == block.length())
					{
						if (!mExcludeUnsupportedCapabilities)
						{
							throw new UnsupportedSqlFeatureException("LIMIT", mDatasource.getAliasedDriver());
						}
					}
					else
					{
						template.setValue("LIMIT", block);
					}
				}

				mSql = template.getBlock("QUERY");

				assert mSql != null;
				assert mSql.length() > 0;
			}
		}

		return mSql;
	}

	public Select hint(String hint)
	{
		clearGenerated();
		mHint = hint;

		return this;
	}

	public Select field(String field)
	{
		if (null == field)			throw new IllegalArgumentException("field can't be null.");
		if (0 == field.length())	throw new IllegalArgumentException("field can't be empty.");

		clearGenerated();
		mFields.add(field);

		return this;
	}

	public Select field(String alias, Select query)
	{
		if (null == alias)			throw new IllegalArgumentException("alias can't be null.");
		if (0 == alias.length())	throw new IllegalArgumentException("alias can't be empty.");
		if (null == query)			throw new IllegalArgumentException("query can't be null.");

		StringBuilder buffer = new StringBuilder();

		buffer.append("(");
		buffer.append(query.toString());
		buffer.append(") AS ");
		buffer.append(alias);

		field(buffer.toString());

		fieldSubselect(query);

		return this;
	}

	public Select fields(Class beanClass)
	throws DbQueryException
	{
		return fieldsExcluded(null, beanClass, (String[])null);
	}

	public Select fieldsExcluded(Class beanClass, String... excludedFields)
	throws DbQueryException
	{
		return fieldsExcluded(null, beanClass, excludedFields);
	}

	public Select fields(String table, Class beanClass)
	throws DbQueryException
	{
		return fieldsExcluded(table, beanClass, (String[])null);
	}

	public Select fieldsExcluded(String table, Class beanClass, String... excludedFields)
	throws DbQueryException
	{
		if (null == beanClass)	throw new IllegalArgumentException("beanClass can't be null.");

		Set<String>	property_names = QueryHelper.getBeanPropertyNames(beanClass, excludedFields);

		Constrained constrained = ConstrainedUtils.getConstrainedInstance(beanClass);

		// handle the properties
		for (String property_name : property_names)
		{
			if (!ConstrainedUtils.persistConstrainedProperty(constrained, property_name, null))
			{
				continue;
			}

			if (null == table)
			{
				field(property_name);
			}
			else
			{
				field(table+"."+property_name);
			}
		}

		return this;
	}

	public Select fields(String... fields)
	{
		if (null == fields)			throw new IllegalArgumentException("fields can't be null.");

		if (fields.length > 0)
		{
			clearGenerated();
			mFields.addAll(Arrays.asList(fields));
		}

		return this;
	}

	public Select distinct()
	{
		clearGenerated();
		mDistinct = true;

		return this;
	}

	public Select distinctOn(String column)
	{
		if (null == column)			throw new IllegalArgumentException("column can't be null.");
		if (0 == column.length())	throw new IllegalArgumentException("column can't be empty.");

		clearGenerated();
		mDistinct = true;
		mDistinctOn.add(column);

		return this;
	}

	public Select distinctOn(String... columns)
	{
		if (null == columns)	throw new IllegalArgumentException("columns can't be null.");

		if (columns.length > 0)
		{
			clearGenerated();
			mDistinct = true;
			mDistinctOn.addAll(Arrays.asList(columns));
		}

		return this;
	}

	public Select from(String from)
	{
		if (null == from)		throw new IllegalArgumentException("from can't be null.");
		if (0 == from.length())	throw new IllegalArgumentException("from can't be empty.");

		clearGenerated();
		mFrom = from;

		return this;
	}

	public Select from(Select query)
	{
		if (null == query)	throw new IllegalArgumentException("query can't be null.");

		StringBuilder buffer = new StringBuilder();

		buffer.append("(");
		buffer.append(query.toString());
		buffer.append(")");

		from(buffer.toString());

		_tableSubselect(query);

		return this;
	}

	public Select from(String alias, Select query)
	{
		if (null == alias)			throw new IllegalArgumentException("alias can't be null.");
		if (0 == alias.length())	throw new IllegalArgumentException("alias can't be empty.");
		if (null == query)			throw new IllegalArgumentException("query can't be null.");

		StringBuilder buffer = new StringBuilder();

		buffer.append("(");
		buffer.append(query.toString());
		buffer.append(") ");
		buffer.append(alias);

		from(buffer.toString());

		_tableSubselect(query);

		return this;
	}

	public Select join(String table)
	{
		if (null == table)			throw new IllegalArgumentException("table can't be null.");
		if (0 == table.length())	throw new IllegalArgumentException("table can't be empty.");

		clearGenerated();
		mJoins.add(new JoinDefault(table));

		return this;
	}

	public Select join(String alias, Select query)
	{
		if (null == alias)			throw new IllegalArgumentException("alias can't be null.");
		if (0 == alias.length())	throw new IllegalArgumentException("alias can't be empty.");
		if (null == query)			throw new IllegalArgumentException("query can't be null.");

		StringBuilder buffer = new StringBuilder();
		buffer.append("(");
		buffer.append(query.toString());
		buffer.append(") ");
		buffer.append(alias);

		join(buffer.toString());

		tableSubselect(query);

		return this;
	}

	public Select joinCustom(String customJoin)
	{
		if (null == customJoin)			throw new IllegalArgumentException("customJoin can't be null.");
		if (0 == customJoin.length())	throw new IllegalArgumentException("customJoin can't be empty.");

		clearGenerated();
		mJoins.add(new JoinCustom(customJoin));

		return this;
	}

	public Select joinCross(String table)
	{
		if (null == table)			throw new IllegalArgumentException("table can't be null.");
		if (0 == table.length())	throw new IllegalArgumentException("table can't be empty.");

		clearGenerated();
		mJoins.add(new JoinCross(table));

		return this;
	}

	public Select joinInner(String table, JoinCondition condition, String conditionExpression)
	{
		if (null == table)						throw new IllegalArgumentException("table can't be null.");
		if (0 == table.length())				throw new IllegalArgumentException("table can't be empty.");
		if (null == condition)					throw new IllegalArgumentException("condition can't be null.");
		if (NATURAL == condition &&
			conditionExpression != null)		throw new IllegalArgumentException("a NATURAL join condition can't have a join expression.");
		if (NATURAL != condition &&
			null == conditionExpression)		throw new IllegalArgumentException("conditionExpression can't be null.");
		if (NATURAL != condition &&
			0 == conditionExpression.length())	throw new IllegalArgumentException("conditionExpression can't be empty.");

		clearGenerated();
		mJoins.add(new JoinInner(table, condition, conditionExpression));

		return this;
	}

	public Select joinOuter(String table, JoinType type, JoinCondition condition, String conditionExpression)
	{
		if (null == table)						throw new IllegalArgumentException("table can't be null.");
		if (0 == table.length())				throw new IllegalArgumentException("table can't be empty.");
		if (null == type)						throw new IllegalArgumentException("type can't be null.");
		if (null == condition)					throw new IllegalArgumentException("condition can't be null.");
		if (NATURAL == condition &&
			conditionExpression != null)		throw new IllegalArgumentException("a NATURAL join condition can't have a join expression.");
		if (NATURAL != condition &&
			null == conditionExpression)		throw new IllegalArgumentException("conditionExpression can't be null.");
		if (NATURAL != condition &&
			0 == conditionExpression.length())	throw new IllegalArgumentException("conditionExpression can't be empty.");

		clearGenerated();
		mJoins.add(new JoinOuter(table, type, condition, conditionExpression));

		return this;
	}

	public Select fieldSubselect(Select query)
	{
		_fieldSubselect(query);

		return this;
	}

	public Select tableSubselect(Select query)
	{
		_tableSubselect(query);

		return this;
	}

	public Select groupBy(String groupBy)
	{
		if (null == groupBy)		throw new IllegalArgumentException("groupBy can't be null.");
		if (0 == groupBy.length())	throw new IllegalArgumentException("groupBy can't be empty.");

		clearGenerated();
		mGroupBy.add(groupBy);

		return this;
	}

	public Select groupBy(Class beanClass)
	throws DbQueryException
	{
		return groupByExcluded(beanClass, (String[])null);
	}

	public Select groupByExcluded(Class beanClass, String... excludedFields)
	throws DbQueryException
	{
		if (null == beanClass)	throw new IllegalArgumentException("beanClass can't be null.");

		Set<String>	property_names = QueryHelper.getBeanPropertyNames(beanClass, excludedFields);

		clearGenerated();

		for (String property_name : property_names)
		{
			mGroupBy.add(property_name);
		}

		return this;
	}

	public Select having(String having)
	{
		if (null == having)			throw new IllegalArgumentException("having can't be null.");
		if (0 == having.length())	throw new IllegalArgumentException("having can't be empty.");

		clearGenerated();
		mHaving.add(having);

		return this;
	}

	public Select union(String union)
	{
		if (null == union)			throw new IllegalArgumentException("union can't be null.");
		if (0 == union.length())	throw new IllegalArgumentException("union can't be empty.");

		clearGenerated();
		mUnions.add(new Union(union, false));

		return this;
	}

	public Select union(Select union)
	throws DbQueryException
	{
		if (null == union)	throw new IllegalArgumentException("union can't be null.");

		union(union.getSql());
		_unionSubselect(union);

		return this;
	}

	public Select unionAll(String union)
	{
		if (null == union)			throw new IllegalArgumentException("union can't be null.");
		if (0 == union.length())	throw new IllegalArgumentException("union can't be empty.");

		clearGenerated();
		mUnions.add(new Union(union, true));

		return this;
	}

	public Select unionAll(Select union)
	throws DbQueryException
	{
		if (null == union)	throw new IllegalArgumentException("union can't be null.");

		unionAll(union.getSql());
		_unionSubselect(union);

		return this;
	}

	public Select orderBy(String column)
	{
		clearGenerated();
		return orderBy(column, ASC);
	}

	public Select orderBy(String column, OrderByDirection direction)
	{
		if (null == column)			throw new IllegalArgumentException("column can't be null.");
		if (0 == column.length())	throw new IllegalArgumentException("column can't be empty.");
		if (null == direction)		throw new IllegalArgumentException("direction can't be null.");

		OrderBy orderby = new OrderBy(column, direction);
		clearGenerated();
		mOrderBy.add(orderby);

		return this;
	}

	public Select limit(int limit)
	{
		if (limit < 1)	throw new IllegalArgumentException("limit must be at least 1.");

		clearGenerated();
		mLimit = limit;
		setLimitParameter(null);

		return this;
	}

	public Select limitParameter(String name)
	{
		if (null == name)		throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty.");

		clearGenerated();
		mLimit = -1;
		setLimitParameter(name);

		return this;
	}

	public Select offset(int offset)
	{
		if (offset < 0)	throw new IllegalArgumentException("offset must be at least 0.");

		clearGenerated();
		mOffset = offset;
		setOffsetParameter(null);

		return this;
	}

	protected boolean isLimitBeforeOffset()
	{
		Template template = getTemplate();
		if (!template.hasValueId("OFFSET") ||
			!template.hasValueId("LIMIT_VALUE")
		   )
		{
			return super.isLimitBeforeOffset();
		}

		String offset = template.getValue("OFFSET");
		String limit_value = template.getValue("LIMIT_VALUE");
		template.setValue("OFFSET", "offset");
		template.setValue("LIMIT_VALUE", "limit");
		String limit = template.getBlock("LIMIT");

		template.setValue("OFFSET", offset);
		template.setValue("LIMIT_VALUE", limit_value);

		return limit.indexOf("offset") >= limit.indexOf("limit");
	}

	public Select offsetParameter(String name)
	{
		if (null == name)		throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty.");

		clearGenerated();
		mOffset = -1;
		setOffsetParameter(name);

		return this;
	}

	public Select clone()
	{
		Select new_instance = super.clone();
		if (new_instance != null)
		{
			if (mFields != null)
			{
				new_instance.mFields = new ArrayList<String>();
				new_instance.mFields.addAll(mFields);
			}

			if (mJoins != null)
			{
				new_instance.mJoins = new ArrayList<Join>();

				for (Join join : mJoins)
				{
					new_instance.mJoins.add(join.clone());
				}
			}

			if (mUnions != null)
			{
				new_instance.mUnions = new ArrayList<Union>();
				for (Union union : mUnions)
				{
					new_instance.mUnions.add(union.clone());
				}
			}

			if (mGroupBy != null)
			{
				new_instance.mGroupBy = new ArrayList<String>();
				new_instance.mGroupBy.addAll(mGroupBy);
			}

			if (mHaving != null)
			{
				new_instance.mHaving = new ArrayList<String>();
				new_instance.mHaving.addAll(mHaving);
			}

			if (mDistinctOn != null)
			{
				new_instance.mDistinctOn = new ArrayList<String>();
				new_instance.mDistinctOn.addAll(mDistinctOn);
			}

			if (mOrderBy != null)
			{
				new_instance.mOrderBy = new ArrayList<OrderBy>();
				for (OrderBy order_by : mOrderBy)
				{
					new_instance.mOrderBy.add(order_by.clone());
				}
			}
		}

		return new_instance;
	}

	public static class JoinCondition extends EnumClass<String>
	{
		JoinCondition(String identifier)
		{
			super(identifier);
		}
	}

	public static class JoinType extends EnumClass<String>
	{
		JoinType(String identifier)
		{
			super(identifier);
		}
	}

	public static class OrderByDirection extends EnumClass<String>
	{
		OrderByDirection(String identifier)
		{
			super(identifier);
		}

		public static OrderByDirection getDirection(String identifier)
		{
			return getMember(OrderByDirection.class, identifier);
		}
	}

	public class JoinCustom extends Join
	{
		JoinCustom(String customJoin)
		{
			super(customJoin);
		}

		String getSql(Template template)
		{
			return " "+getData();
		}
	}

	public class JoinDefault extends Join implements Cloneable
	{
		JoinDefault(String table)
		{
			super(table);
		}

		String getSql(Template template)
		{
			assert template != null;

			String result = null;

			template.setValue("TABLE", getData());
			result = template.getBlock("JOIN_DEFAULT");
			template.removeValue("TABLE");

			assert result != null;
			assert result.length() > 0;

			return result;
		}

		public JoinDefault clone()
		{
			return (JoinDefault)super.clone();
		}
	}

	public class JoinCross extends Join implements Cloneable
	{
		JoinCross(String table)
		{
			super(table);
		}

		String getSql(Template template)
		throws DbQueryException
		{
			assert template != null;

			String result = null;

			template.setValue("TABLE", getData());
			result = template.getBlock("JOIN_CROSS");
			if (0 == result.length())
			{
				throw new UnsupportedSqlFeatureException("CROSS JOIN", mDatasource.getAliasedDriver());
			}
			template.removeValue("TABLE");

			assert result != null;
			assert result.length() > 0;

			return result;
		}

		public JoinCross clone()
		{
			return (JoinCross)super.clone();
		}
	}

	public class JoinInner extends Join implements Cloneable
	{
		private JoinCondition	mCondition = null;
		private String			mExpression = null;

		JoinInner(String table, JoinCondition condition, String expression)
		{
			super(table);

			assert condition != null;
			assert condition == Select.NATURAL || (expression != null && expression.length() > 0);

			setCondition(condition);
			setExpression(expression);
		}

		String getSql(Template template)
		throws DbQueryException
		{
			assert template != null;

			String	condition = null;
			String	result = null;

			template.setValue("TABLE", getData());
			if (getExpression() != null)
			{
				template.setValue("EXPRESSION", getExpression());
			}
			condition = template.getBlock("JOIN_INNER_"+getCondition().toString());
			if (0 == condition.length())
			{
				throw new UnsupportedSqlFeatureException(getCondition().toString()+" for INNER JOIN", mDatasource.getAliasedDriver());
			}
			template.setValue("JOIN_INNER_"+getCondition().toString(), condition);
			result = template.getBlock("JOIN_INNER");
			if (0 == result.length())
			{
				throw new UnsupportedSqlFeatureException("INNER JOIN", mDatasource.getAliasedDriver());
			}
			template.removeValue("TABLE");
			template.removeValue("EXPRESSION");
			template.removeValue("JOIN_INNER_"+getCondition().toString());

			assert result != null;
			assert result.length() > 0;

			return result;
		}

		public JoinCondition getCondition()
		{
			return mCondition;
		}

		void setCondition(JoinCondition condition)
		{
			assert condition != null;

			mCondition = condition;
		}

		public String getExpression()
		{
			return mExpression;
		}

		void setExpression(String expression)
		{
			mExpression = expression;
		}

		public JoinInner clone()
		{
			return (JoinInner)super.clone();
		}
	}

	public class JoinOuter extends Join implements Cloneable
	{
		private JoinType		mType = null;
		private JoinCondition	mCondition = null;
		private String			mExpression = null;

		JoinOuter(String table, JoinType type, JoinCondition condition, String expression)
		{
			super(table);

			assert type != null;
			assert condition != null;
			assert condition == Select.NATURAL || (expression != null && expression.length() > 0);

			setType(type);
			setCondition(condition);
			setExpression(expression);
		}

		String getSql(Template template)
		throws DbQueryException
		{
			assert template != null;

			String	type = null;
			String	condition = null;
			String	result = null;

			template.setValue("TABLE", getData());
			if (getType() != null)
			{
				type = template.getBlock("JOIN_OUTER_"+getType().toString());
				if (0 == type.length())
				{
					throw new UnsupportedSqlFeatureException(getType().toString()+" for OUTER JOIN", mDatasource.getAliasedDriver());
				}
				template.setValue("JOIN_OUTER_TYPE", type);
			}
			if (getExpression() != null)
			{
				template.setValue("EXPRESSION", getExpression());
			}
			condition = template.getBlock("JOIN_OUTER_"+getCondition().toString());
			if (0 == condition.length())
			{
				throw new UnsupportedSqlFeatureException(getCondition().toString()+" for OUTER JOIN", mDatasource.getAliasedDriver());
			}
			template.setValue("JOIN_OUTER_"+getCondition().toString(), condition);
			result = template.getBlock("JOIN_OUTER");
			if (0 == result.length())
			{
				throw new UnsupportedSqlFeatureException("OUTER JOIN", mDatasource.getAliasedDriver());
			}
			template.removeValue("TABLE");
			template.removeValue("EXPRESSION");
			template.removeValue("JOIN_OUTER_"+getCondition().toString());
			template.removeValue("JOIN_OUTER_TYPE");

			assert result != null;
			assert result.length() > 0;

			return result;
		}

		public JoinType getType()
		{
			return mType;
		}

		void setType(JoinType type)
		{
			assert type != null;

			mType = type;
		}

		public JoinCondition getCondition()
		{
			return mCondition;
		}

		void setCondition(JoinCondition condition)
		{
			assert condition != null;

			mCondition = condition;
		}

		public String getExpression()
		{
			return mExpression;
		}

		void setExpression(String expression)
		{
			mExpression = expression;
		}

		public JoinOuter clone()
		{
			return (JoinOuter)super.clone();
		}
	}

	public abstract class Join implements Cloneable
	{
		private String mData = null;

		Join(String data)
		{
			assert data != null;
			assert data.length() > 0;

			setData(data);
		}

		abstract String getSql(Template template) throws DbQueryException;

		public String getData()
		{
			return mData;
		}

		void setData(String data)
		{
			assert data != null;
			assert data.length() > 0;

			mData = data;
		}

		public Join clone()
		{
			Join new_instance = null;
			try
			{
				new_instance = (Join)super.clone();
			}
			catch (CloneNotSupportedException e)
			{
				new_instance = null;
			}

			return new_instance;
		}
	}

	public class OrderBy implements Cloneable
	{
		private String				mColumn = null;
		private OrderByDirection	mDirection = null;

		OrderBy(String column, OrderByDirection direction)
		{
			assert column != null;
			assert column.length() > 0;
			assert direction != null;

			setColumn(column);
			setDirection(direction);
		}

		String getSql(Template template)
		{
			assert template != null;

			String result = null;

			template.setValue("COLUMN", getColumn());
			template.setValue("DIRECTION", template.getBlock("ORDERBY_"+getDirection().toString()));
			result = template.getBlock("ORDERBY_PART");
			template.removeValue("COLUMN");
			template.removeValue("DIRECTION");

			assert result != null;
			assert result.length() > 0;

			return result;
		}

		public String getColumn()
		{
			return mColumn;
		}

		void setColumn(String column)
		{
			assert column != null;
			assert column.length() > 0;

			mColumn = column;
		}

		public OrderByDirection getDirection()
		{
			return mDirection;
		}

		void setDirection(OrderByDirection direction)
		{
			assert direction != null;

			mDirection = direction;
		}

		public OrderBy clone()
		{
			OrderBy new_instance = null;
			try
			{
				new_instance = (OrderBy)super.clone();
			}
			catch (CloneNotSupportedException e)
			{
				new_instance = null;
			}

			return new_instance;
		}
	}

	public class Union implements Cloneable
	{
		private String	mExpression = null;
		private boolean	mAll = false;

		Union(String expression, boolean all)
		{
			setExpression(expression);
			setAll(all);
		}

		void setExpression(String expression)
		{
			assert expression != null;
			assert expression.length() > 0;

			mExpression = expression;
		}
		
		public String getExpression()
		{
			return mExpression;
		}
		
		void setAll(boolean all)
		{
			mAll = all;
		}
		
		public boolean isAll()
		{
			return mAll;
		}

		public Union clone()
		{
			Union new_instance = null;
			try
			{
				new_instance = (Union)super.clone();
			}
			catch (CloneNotSupportedException e)
			{
				new_instance = null;
			}

			return new_instance;
		}
	}
}
