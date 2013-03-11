/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: LimitOffsetCompensator.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.capabilities;

import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.DbResultSet;
import com.uwyn.rife.database.VirtualParameters;
import com.uwyn.rife.database.VirtualParametersHandler;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.Query;
import com.uwyn.rife.database.queries.QueryParameterType;
import com.uwyn.rife.database.queries.QueryParameters;
import java.sql.SQLException;

public class LimitOffsetCompensator implements VirtualParametersHandler
{
	private boolean	mUseRelativeForScrolling = false;
	
	public void setUseRelativeForScrolling(boolean useRelativeForCursors)
	{
		mUseRelativeForScrolling = useRelativeForCursors;
	}
	
	public void handleCapablePreparedStatement(DbPreparedStatement statement)
	throws DatabaseException
	{
		Query query = statement.getQuery();
		if (query != null)
		{
			// obtain capabilities
			Capabilities capabilities = query.getCapabilities();
			if (capabilities != null)
			{
				// handle limit and offset capabilities
				if (capabilities.containsKey(Capability.LIMIT))
				{
					// limit the fetch size of the resultset
					int max_rows = 0;
					int limit = (Integer)capabilities.get(Capability.LIMIT);
					statement.setFetchSize(limit);
					max_rows += limit;
		
					// limit the maximum number of rows
					if (capabilities.containsKey(Capability.OFFSET))
					{
						max_rows += (Integer)capabilities.get(Capability.OFFSET);
					}
					if (max_rows != 0)
					{
						statement.setMaxRows(max_rows);
					}
				}
				// handle limit and offset parameter capabilities
				else if (capabilities.containsKey(Capability.LIMIT_PARAMETER))
				{
					QueryParameters parameters = query.getParameters();
					if (parameters != null)
					{
						QueryParameters virtual_query_parameters = parameters.getNewInstance();
						
						virtual_query_parameters.addTypedParameter(QueryParameterType.LIMIT, (String)capabilities.get(Capability.LIMIT_PARAMETER));
						if (capabilities.containsKey(Capability.OFFSET_PARAMETER))
						{
							virtual_query_parameters.addTypedParameter(QueryParameterType.OFFSET, (String)capabilities.get(Capability.OFFSET_PARAMETER));
						}
						
						VirtualParameters virtual_parameters = new VirtualParameters(virtual_query_parameters, this);
						statement.setVirtualParameters(virtual_parameters);
					}
				}
			}
		}
	}
	
	public void handleValues(DbPreparedStatement statement)
	throws DatabaseException
	{
		Query query = statement.getQuery();
		if (query != null)
		{
			// obtain capabilities
			Capabilities capabilities = query.getCapabilities();
			if (capabilities != null)
			{
				// handle limit and offset capabilities
				if (capabilities.containsKey(Capability.LIMIT_PARAMETER))
				{
					// limit the fetch size of the resultset
					int max_rows = 0;
					String limit_parameter_name = (String)capabilities.get(Capability.LIMIT_PARAMETER);
					int limit = Integer.parseInt(String.valueOf(statement.getVirtualParameterValue(limit_parameter_name)));
					statement.setFetchSize(limit);
					max_rows += limit;
		
					// limit the maximum number of rows
					if (capabilities.containsKey(Capability.OFFSET_PARAMETER))
					{
						String offset_parameter_name = (String)capabilities.get(Capability.OFFSET_PARAMETER);
						int offset = Integer.parseInt(String.valueOf(statement.getVirtualParameterValue(offset_parameter_name)));
						max_rows += offset;
					}
					if (max_rows != 0)
					{
						statement.setMaxRows(max_rows);
					}
				}
			}
		}
	}
	
	public void handleCapableResultSet(DbPreparedStatement statement)
	throws DatabaseException
	{
		DbResultSet resultset = statement.getResultSet();
		
		// obtain capabilities
		Capabilities capabilities = statement.getQuery().getCapabilities();
		if (capabilities != null &&
			(capabilities.containsKey(Capability.LIMIT) ||
			 capabilities.containsKey(Capability.LIMIT_PARAMETER)))
		{
			int	offset = -1;
			
			// handle limit and offset capabilities
			if (capabilities.containsKey(Capability.OFFSET))
			{
				offset = (Integer)capabilities.get(Capability.OFFSET);
			}
			else if (capabilities.containsKey(Capability.OFFSET_PARAMETER))
			{
				String parameter_name = (String)capabilities.get(Capability.OFFSET_PARAMETER);
				offset = Integer.parseInt(String.valueOf(statement.getVirtualParameterValue(parameter_name)));
			}

			// apply the offset
			if (offset > 0)
			{
				try
				{
					if (mUseRelativeForScrolling)
					{
						resultset.relative(offset);
					}
					else
					{
						while (offset > 0)
						{
							resultset.next();
							offset--;
						}
					}
				}
				catch (SQLException e)
				{
					throw new DatabaseException(e);
				}
			}
		}
	}
}

