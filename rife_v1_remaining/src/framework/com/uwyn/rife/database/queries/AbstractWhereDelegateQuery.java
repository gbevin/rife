/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AbstractWhereDelegateQuery.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.exceptions.DbQueryException;
import com.uwyn.rife.database.queries.AbstractWhereQuery;
import com.uwyn.rife.database.queries.Select;
import com.uwyn.rife.database.queries.WhereGroupAnd;
import com.uwyn.rife.database.queries.WhereGroupOr;
import com.uwyn.rife.database.queries.WhereQuery;
import java.util.List;

public abstract class AbstractWhereDelegateQuery<QueryType extends AbstractWhereDelegateQuery, DelegateType extends AbstractWhereQuery> implements WhereQuery<QueryType>
{
	protected DelegateType mDelegate = null;
		
	protected AbstractWhereDelegateQuery(DelegateType delegate)
	{
		mDelegate = delegate;
	}
	
	public DelegateType getDelegate()
	{
		return mDelegate;
	}
	
	public Datasource getDatasource()
	{
		return mDelegate.getDatasource();
	}
	
	public WhereGroup<QueryType> startWhere()
	{
		return new WhereGroup<QueryType>(getDatasource(), this);
	}
	
	public WhereGroupAnd<QueryType> startWhereAnd()
	{
		return new WhereGroupAnd<QueryType>(getDatasource(), this);
	}

	public WhereGroupOr<QueryType> startWhereOr()
	{
		return new WhereGroupOr<QueryType>(getDatasource(), this);
	}

	public QueryType where(String where)
	{
		if (mDelegate.getWhere().length() > 0)
		{
			mDelegate.whereAnd(where);
		}
		else
		{
			mDelegate.where(where);
		}
		
		return (QueryType)this;
	}

	public QueryType where(String field, String operator, boolean value)
	{
		if (mDelegate.getWhere().length() > 0)
		{
			mDelegate.whereAnd(field, operator, value);
		}
		else
		{
			mDelegate.where(field, operator, value);
		}
		
		return (QueryType)this;
	}

	public QueryType where(String field, String operator, byte value)
	{
		if (mDelegate.getWhere().length() > 0)
		{
			mDelegate.whereAnd(field, operator, value);
		}
		else
		{
			mDelegate.where(field, operator, value);
		}
		
		return (QueryType)this;
	}

	public QueryType where(String field, String operator, char value)
	{
		if (mDelegate.getWhere().length() > 0)
		{
			mDelegate.whereAnd(field, operator, value);
		}
		else
		{
			mDelegate.where(field, operator, value);
		}
		
		return (QueryType)this;
	}

	public QueryType where(String field, String operator, double value)
	{
		if (mDelegate.getWhere().length() > 0)
		{
			mDelegate.whereAnd(field, operator, value);
		}
		else
		{
			mDelegate.where(field, operator, value);
		}
		
		return (QueryType)this;
	}

	public QueryType where(String field, String operator, float value)
	{
		if (mDelegate.getWhere().length() > 0)
		{
			mDelegate.whereAnd(field, operator, value);
		}
		else
		{
			mDelegate.where(field, operator, value);
		}
		
		return (QueryType)this;
	}

	public QueryType where(String field, String operator, int value)
	{
		if (mDelegate.getWhere().length() > 0)
		{
			mDelegate.whereAnd(field, operator, value);
		}
		else
		{
			mDelegate.where(field, operator, value);
		}
		
		return (QueryType)this;
	}

	public QueryType where(String field, String operator, long value)
	{
		if (mDelegate.getWhere().length() > 0)
		{
			mDelegate.whereAnd(field, operator, value);
		}
		else
		{
			mDelegate.where(field, operator, value);
		}
		
		return (QueryType)this;
	}

	public QueryType where(String field, String operator, Select query)
	{
		if (mDelegate.getWhere().length() > 0)
		{
			mDelegate.whereAnd(field, operator, query);
		}
		else
		{
			mDelegate.where(field, operator, query);
		}
		
		return (QueryType)this;
	}
	

	public QueryType where(String field, String operator, Object value)
	{
		if (mDelegate.getWhere().length() > 0)
		{
			mDelegate.whereAnd(field, operator, value);
		}
		else
		{
			mDelegate.where(field, operator, value);
		}
		
		return (QueryType)this;
	}

	public QueryType where(String field, String operator, short value)
	{
		if (mDelegate.getWhere().length() > 0)
		{
			mDelegate.whereAnd(field, operator, value);
		}
		else
		{
			mDelegate.where(field, operator, value);
		}
		
		return (QueryType)this;
	}

	public QueryType whereAnd(String where)
	{
		mDelegate.whereAnd(where);
		
		return (QueryType)this;
	}

	public QueryType whereAnd(String field, String operator, boolean value)
	{
		mDelegate.whereAnd(field, operator, value);
		
		return (QueryType)this;
	}

	public QueryType whereAnd(String field, String operator, byte value)
	{
		mDelegate.whereAnd(field, operator, value);
		
		return (QueryType)this;
	}

	public QueryType whereAnd(String field, String operator, char value)
	{
		mDelegate.whereAnd(field, operator, value);
		
		return (QueryType)this;
	}

	public QueryType whereAnd(String field, String operator, double value)
	{
		mDelegate.whereAnd(field, operator, value);
		
		return (QueryType)this;
	}

	public QueryType whereAnd(String field, String operator, float value)
	{
		mDelegate.whereAnd(field, operator, value);
		
		return (QueryType)this;
	}

	public QueryType whereAnd(String field, String operator, int value)
	{
		mDelegate.whereAnd(field, operator, value);
		
		return (QueryType)this;
	}

	public QueryType whereAnd(String field, String operator, long value)
	{
		mDelegate.whereAnd(field, operator, value);
		
		return (QueryType)this;
	}

	public QueryType whereAnd(String field, String operator, Select query)
	{
		mDelegate.whereAnd(field, operator, query);
		
		return (QueryType)this;
	}

	public QueryType whereAnd(String field, String operator, Object value)
	{
		mDelegate.whereAnd(field, operator, value);
		
		return (QueryType)this;
	}

	public QueryType whereAnd(String field, String operator, short value)
	{
		mDelegate.whereAnd(field, operator, value);
		
		return (QueryType)this;
	}

	public QueryType whereOr(String where)
	{
		mDelegate.whereOr(where);
		
		return (QueryType)this;
	}

	public QueryType whereOr(String field, String operator, boolean value)
	{
		mDelegate.whereOr(field, operator, value);
		
		return (QueryType)this;
	}

	public QueryType whereOr(String field, String operator, byte value)
	{
		mDelegate.whereOr(field, operator, value);
		
		return (QueryType)this;
	}

	public QueryType whereOr(String field, String operator, char value)
	{
		mDelegate.whereOr(field, operator, value);
		
		return (QueryType)this;
	}

	public QueryType whereOr(String field, String operator, double value)
	{
		mDelegate.whereOr(field, operator, value);
		
		return (QueryType)this;
	}

	public QueryType whereOr(String field, String operator, float value)
	{
		mDelegate.whereOr(field, operator, value);
		
		return (QueryType)this;
	}

	public QueryType whereOr(String field, String operator, int value)
	{
		mDelegate.whereOr(field, operator, value);
		
		return (QueryType)this;
	}

	public QueryType whereOr(String field, String operator, long value)
	{
		mDelegate.whereOr(field, operator, value);
		
		return (QueryType)this;
	}

	public QueryType whereOr(String field, String operator, Select query)
	{
		mDelegate.whereOr(field, operator, query);
		
		return (QueryType)this;
	}

	public QueryType whereOr(String field, String operator, Object value)
	{
		mDelegate.whereOr(field, operator, value);
		
		return (QueryType)this;
	}

	public QueryType whereOr(String field, String operator, short value)
	{
		mDelegate.whereOr(field, operator, value);
		
		return (QueryType)this;
	}

	public QueryType whereSubselect(Select query)
	{
		mDelegate.whereSubselect(query);
		
		return (QueryType)this;
	}
	
	public QueryType where(Object bean)
	throws DbQueryException
	{
		mDelegate.where(bean);
		
		return (QueryType)this;
	}
	
	public QueryType whereIncluded(Object bean, String[] includedFields) throws DbQueryException
	{
		mDelegate.whereIncluded(bean, includedFields);
		
		return (QueryType)this;
	}
	
	public QueryType whereExcluded(Object bean, String[] excludedFields) throws DbQueryException
	{
		mDelegate.whereExcluded(bean, excludedFields);
		
		return (QueryType)this;
	}
	
	public QueryType whereFiltered(Object bean, String[] includedFields, String[] excludedFields) throws DbQueryException
	{
		mDelegate.whereFiltered(bean, includedFields, excludedFields);
		
		return (QueryType)this;
	}

	public void addWhereParameters(List<String> parameters)
	{
		mDelegate.addWhereParameters(parameters);
	}
}
