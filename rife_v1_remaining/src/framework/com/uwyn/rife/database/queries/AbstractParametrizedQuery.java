/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AbstractParametrizedQuery.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.queries.QueryParameterType;
import java.util.List;

abstract class AbstractParametrizedQuery extends AbstractQuery implements Query, Cloneable
{
	private QueryParameters	mParameters = null;

	protected AbstractParametrizedQuery(Datasource datasource)
	{
		super(datasource);
	}

	public void clear()
	{
		super.clear();

		if (mParameters != null)
		{
			mParameters.clear();
		}
	}
	
	private void addTypedParameters(QueryParameterType type, QueryParameters parameters)
	{
		if (null == parameters)
		{
			return;
		}
		
		addTypedParameters(type, parameters.getOrderedNames());
	}
	
	private void addTypedParameters(QueryParameterType type, List<String> parameters)
	{
		if (null == mParameters)
		{
			mParameters = new QueryParameters(this);
		}
		
		mParameters.addTypedParameters(type, parameters);
	}
	
	private void addTypedParameter(QueryParameterType type, String parameter)
	{
		if (null == mParameters)
		{
			mParameters = new QueryParameters(this);
		}
		
		mParameters.addTypedParameter(type, parameter);
	}
	
	private <T> T getTypedParameters(QueryParameterType type)
	{
		if (null == mParameters)
		{
			return null;
		}
		
		return (T)mParameters.getTypedParameters(type);
	}
	
	private void clearTypedParameters(QueryParameterType type)
	{
		if (null == mParameters)
		{
			return;
		}
		
		mParameters.clearTypedParameters(type);
		if (0 == mParameters.getNumberOfTypes())
		{
			mParameters = null;
		}
	}
	
	protected void _fieldSubselect(Select query)
	{
		if (null == query)	throw new IllegalArgumentException("query can't be null.");

		addTypedParameters(QueryParameterType.FIELD, query.getParameters());
	}
	
	protected void _tableSubselect(Select query)
	{
		if (null == query)	throw new IllegalArgumentException("query can't be null.");

		addTypedParameters(QueryParameterType.TABLE, query.getParameters());
	}
	
	protected void _whereSubselect(Select query)
	{
		if (null == query)	throw new IllegalArgumentException("query can't be null.");

		addTypedParameters(QueryParameterType.WHERE, query.getParameters());
	}
	
	protected void _unionSubselect(Select query)
	{
		if (null == query)	throw new IllegalArgumentException("query can't be null.");

		addTypedParameters(QueryParameterType.UNION, query.getParameters());
	}
	
	public QueryParameters getParameters()
	{
		return mParameters;
	}
		
	protected void addFieldParameter(String field)
	{
		addTypedParameter(QueryParameterType.FIELD, field);
	}
	
	protected void clearWhereParameters()
	{
		clearTypedParameters(QueryParameterType.WHERE);
	}
	
	protected void addWhereParameter(String field)
	{
		addTypedParameter(QueryParameterType.WHERE, field);
	}
	
	protected List<String> getWhereParameters()
	{
		return getTypedParameters(QueryParameterType.WHERE);
	}
	
	public void addWhereParameters(List<String> parameters)
	{
		addTypedParameters(QueryParameterType.WHERE, parameters);
	}
	
	protected void setLimitParameter(String limitParameter)
	{
		addTypedParameter(QueryParameterType.LIMIT, limitParameter);
	}
	
	public String getLimitParameter()
	{
		return getTypedParameters(QueryParameterType.LIMIT);
	}
	
	protected void setOffsetParameter(String offsetParameter)
	{
		addTypedParameter(QueryParameterType.OFFSET, offsetParameter);
	}
	
	public String getOffsetParameter()
	{
		return getTypedParameters(QueryParameterType.OFFSET);
	}
	
	protected boolean isLimitBeforeOffset()
	{
		return true;
	}

	public AbstractParametrizedQuery clone()
	{
        AbstractParametrizedQuery new_instance = (AbstractParametrizedQuery)super.clone();
		
		if (new_instance != null &&
			mParameters != null)
		{
			new_instance.mParameters = mParameters.clone();
		}

		return new_instance;
	}
}
