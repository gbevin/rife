/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: QueryParameters.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import java.util.*;

import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.QueryParameterType;
import com.uwyn.rife.tools.ArrayUtils;

public class QueryParameters implements Cloneable
{
	private AbstractParametrizedQuery		mQuery = null;
	private Map<QueryParameterType, Object>	mParameters = null;

	private List<String>	mCombinedParameters = null;
	private String[]		mCombinedParametersArray = null;
	
	public QueryParameters(AbstractParametrizedQuery query)
	{
		if (null == query)	throw new IllegalArgumentException("query can't be null");

		mQuery = query;
	}
	
	public QueryParameters getNewInstance()
	{
		return new QueryParameters(mQuery);
	}
	
	public int getNumberOfTypes()
	{
		if (null == mParameters)
		{
			return 0;
		}
		
		return mParameters.size();
	}

	public void clear()
	{
		mParameters = null;
		mCombinedParameters = null;
		mCombinedParametersArray = null;
	}
	
	public boolean hasParameter(QueryParameterType type, String value)
	{
		if (null == type ||
			null == value ||
			null == mParameters)
		{
			return false;
		}
		
		if (!mParameters.containsKey(type))
		{
			return false;
		}
		
		if (type.isSingular())
		{
			return value.equals(mParameters.get(type));
		}
		else
		{
			List<String> list = (List<String>)mParameters.get(type);
			if (null == list)
			{
				return false;
			}
			
			return list.contains(value);
		}
	}
	
	public Set<String> getDistinctNames()
	{
		if (null == mParameters ||
			0 == mParameters.size())
		{
			return null;
		}
		
		HashSet<String> names = new HashSet<String>();
		for (Map.Entry<QueryParameterType, Object> entry : mParameters.entrySet())
		{
			if (entry.getKey().isSingular())
			{
				names.add((String)entry.getValue());
			}
			else
			{
				names.addAll((List<String>)entry.getValue());
			}
		}
		
		return names;
	}
	
	public List<String> getOrderedNames()
	{
		if (null == mCombinedParameters)
		{
			ArrayList<String> combined_parameters = null;

			if (mParameters != null &&
				mParameters.size() > 0)
			{
				if (mParameters.containsKey(QueryParameterType.FIELD))
				{
					combined_parameters = new ArrayList<String>();
					
					for (String parameter : (List<String>)mParameters.get(QueryParameterType.FIELD))
					{
						// add the parameter to the combined list
						combined_parameters.add(parameter);
					}
				}
				
				if (mParameters.containsKey(QueryParameterType.TABLE))
				{
					if (null == combined_parameters)
					{
						combined_parameters = new ArrayList<String>();
					}
					
					for (String parameter : (List<String>)mParameters.get(QueryParameterType.TABLE))
					{
						// add the parameter to the combined list
						combined_parameters.add(parameter);
					}
				}
				
				if (mParameters.containsKey(QueryParameterType.WHERE))
				{
					if (null == combined_parameters)
					{
						combined_parameters = new ArrayList<String>();
					}
					
					for (String parameter : (List<String>)mParameters.get(QueryParameterType.WHERE))
					{
						// add the parameter to the combined list
						combined_parameters.add(parameter);
					}
				}
				
				if (mParameters.containsKey(QueryParameterType.UNION))
				{
					if (null == combined_parameters)
					{
						combined_parameters = new ArrayList<String>();
					}
	
					for (String parameter : (List<String>)mParameters.get(QueryParameterType.UNION))
					{
						// add the parameter to the combined list
						combined_parameters.add(parameter);
					}
				}
				
				if (mParameters.containsKey(QueryParameterType.LIMIT) ||
					mParameters.containsKey(QueryParameterType.OFFSET))
				{
					if (mQuery.isLimitBeforeOffset())
					{
						if (mParameters.containsKey(QueryParameterType.LIMIT))
						{
							if (null == combined_parameters)
							{
								combined_parameters = new ArrayList<String>();
							}
							
							// get the parameter value
							String value = (String)mParameters.get(QueryParameterType.LIMIT);
							// add the parameter to the combined list
							combined_parameters.add(value);
						}
				
						if (mParameters.containsKey(QueryParameterType.OFFSET))
						{
							if (null == combined_parameters)
							{
								combined_parameters = new ArrayList<String>();
							}
							
							// get the parameter value
							String value = (String)mParameters.get(QueryParameterType.OFFSET);
							// add the parameter to the combined list
							combined_parameters.add(value);
						}
					}
					else
					{
						if (mParameters.containsKey(QueryParameterType.OFFSET))
						{
							if (null == combined_parameters)
							{
								combined_parameters = new ArrayList<String>();
							}
							
							// get the parameter value
							String value = (String)mParameters.get(QueryParameterType.OFFSET);
							// add the parameter to the combined list
							combined_parameters.add(value);
						}
	
						if (mParameters.containsKey(QueryParameterType.LIMIT))
						{
							if (null == combined_parameters)
							{
								combined_parameters = new ArrayList<String>();
							}
							
							// get the parameter value
							String value = (String)mParameters.get(QueryParameterType.LIMIT);
							// add the parameter to the combined list
							combined_parameters.add(value);
						}
					}
				}
			}
	
			mCombinedParameters = combined_parameters;
			mCombinedParametersArray = null;
		}
		
		return mCombinedParameters;
	}
	
	private void addVirtualIndexMapping(QueryParameters virtualParameters, Map<Integer, Integer> map, int[] parameterIndex, int[] realIndex, QueryParameterType type, String parameter)
	{
		if (virtualParameters.hasParameter(type, parameter))
		{
			map.put(parameterIndex[0], -1);
		}
		else
		{
			map.put(parameterIndex[0], realIndex[0]);
			realIndex[0]++;
		}
		
		parameterIndex[0]++;
	}
	
	public Map<Integer, Integer> getVirtualIndexMapping(QueryParameters virtualParameters)
	{
		Map<Integer, Integer> map = null;

		if (mParameters != null &&
			mParameters.size() > 0 &&
			virtualParameters != null &&
			virtualParameters.getNumberOfTypes() > 0)
		{
			map = new HashMap<Integer, Integer>();
			
			int[]	parameter_index = new int[] {1};
			int[]	real_index = new int[] {1};
			
			if (mParameters.containsKey(QueryParameterType.FIELD))
			{
				for (String parameter : (List<String>)mParameters.get(QueryParameterType.FIELD))
				{
					addVirtualIndexMapping(virtualParameters, map, parameter_index, real_index, QueryParameterType.FIELD, parameter);
				}
			}
			
			if (mParameters.containsKey(QueryParameterType.TABLE))
			{
				for (String parameter : (List<String>)mParameters.get(QueryParameterType.TABLE))
				{
					addVirtualIndexMapping(virtualParameters, map, parameter_index, real_index, QueryParameterType.TABLE, parameter);
				}
			}
			
			if (mParameters.containsKey(QueryParameterType.WHERE))
			{
				for (String parameter : (List<String>)mParameters.get(QueryParameterType.WHERE))
				{
					addVirtualIndexMapping(virtualParameters, map, parameter_index, real_index, QueryParameterType.WHERE, parameter);
				}
			}
			
			if (mParameters.containsKey(QueryParameterType.UNION))
			{
				for (String parameter : (List<String>)mParameters.get(QueryParameterType.UNION))
				{
					addVirtualIndexMapping(virtualParameters, map, parameter_index, real_index, QueryParameterType.UNION, parameter);
				}
			}
			
			if (mParameters.containsKey(QueryParameterType.LIMIT) ||
				mParameters.containsKey(QueryParameterType.OFFSET))
			{
				if (mQuery.isLimitBeforeOffset())
				{
					if (mParameters.containsKey(QueryParameterType.LIMIT))
					{
						String parameter = (String)mParameters.get(QueryParameterType.LIMIT);
						addVirtualIndexMapping(virtualParameters, map, parameter_index, real_index, QueryParameterType.LIMIT, parameter);
					}
			
					if (mParameters.containsKey(QueryParameterType.OFFSET))
					{
						String parameter = (String)mParameters.get(QueryParameterType.OFFSET);
						addVirtualIndexMapping(virtualParameters, map, parameter_index, real_index, QueryParameterType.OFFSET, parameter);
					}
				}
				else
				{
					if (mParameters.containsKey(QueryParameterType.OFFSET))
					{
						String parameter = (String)mParameters.get(QueryParameterType.OFFSET);
						addVirtualIndexMapping(virtualParameters, map, parameter_index, real_index, QueryParameterType.OFFSET, parameter);
					}

					if (mParameters.containsKey(QueryParameterType.LIMIT))
					{
						String parameter = (String)mParameters.get(QueryParameterType.LIMIT);
						addVirtualIndexMapping(virtualParameters, map, parameter_index, real_index, QueryParameterType.LIMIT, parameter);
					}
				}
			}
		}
		
		return map;
	}
		
	public String[] getOrderedNamesArray()
	{
		if (null == mParameters ||
			0 == mParameters.size())
		{
			return null;
		}
		
		if (null == mCombinedParametersArray)
		{
			String[] array = new String[0];
			
			for (String parameter_name : getOrderedNames())
			{
				array = ArrayUtils.join(array, parameter_name);
			}
			
			mCombinedParametersArray = array;
		}
		
		return mCombinedParametersArray;
	}
	
	private void clearCombinedParameters()
	{
		mCombinedParameters = null;
		mCombinedParametersArray = null;
	}

	public void clearTypedParameters(QueryParameterType type)
	{
		if (null == type)		throw new IllegalArgumentException("the parameter type can't be null");

		if (null == mParameters)
		{
			return;
		}
		
		mParameters.remove(type);
		clearCombinedParameters();
	}
	
	public <T> T getTypedParameters(QueryParameterType type)
	{
		if (null == type)		throw new IllegalArgumentException("the parameter type can't be null");

		if (null == mParameters)
		{
			return null;
		}
		
		return (T)mParameters.get(type);
	}
	
	public void addTypedParameters(QueryParameterType type, List<String> parameters)
	{
		if (null == type)		throw new IllegalArgumentException("the parameter type can't be null");
		if (type.isSingular())	throw new IllegalArgumentException("the parameter type '"+type+"' only supports a singular value");
		
		// don't add empty parameters
		if (null == parameters ||
			0 == parameters.size())
		{
			return;
		}
		
		// obtain the existing typed parameters
		List<String> typed_parameters = null;
		if (null == mParameters)
		{
			mParameters = new HashMap<QueryParameterType, Object>();
		}
		else
		{
			typed_parameters = (List<String>)mParameters.get(type);
		}
		
		// initialize the typed parameters collection if it didn't exist before
		boolean new_collection = false;
		if (null == typed_parameters)
		{
			typed_parameters = new ArrayList<String>();
			new_collection = true;
		}
		
		// add the new parameters
		typed_parameters.addAll(parameters);
		
		if (new_collection)
		{
			mParameters.put(type, typed_parameters);
		}
		
		// clear the already calculated combined parameters
		clearCombinedParameters();
	}
	
	public void addTypedParameter(QueryParameterType type, String value)
	{
		if (null == type)		throw new IllegalArgumentException("the parameter type can't be null");
		
		if (value != null)
		{
			// initialize the parameters map if it doesn't exist yet
			if (null == mParameters)
			{
				mParameters = new HashMap<QueryParameterType, Object>();
			}
	
			// remove the table-field separator dot
			if (value.indexOf(".") != -1)
			{
				value = value.substring(value.lastIndexOf(".")+1);
			}
		}
		
		// check if the parameter is singular
		if (type.isSingular())
		{
			// empty singular parameters clear out the key
			if (null == value)
			{
				if (null == mParameters)
				{
					return;
				}
				
				mParameters.remove(type);
			}
			// store the singular parameter
			else
			{
				mParameters.put(type, value);
			}
		}
		else
		{
			// don't add empty parameters
			if (null == value)
			{
				return;
			}
			
			// obtain the existing typed parameters
			List<String> typed_parameters = (List<String>)mParameters.get(type);
			
			// initialize the typed parameters collection if it didn't exist before
			boolean new_collection = false;
			if (null == typed_parameters)
			{
				typed_parameters = new ArrayList<String>();
				new_collection = true;
			}
			
			// add the new parameters
			typed_parameters.add(value);
			
			// store the new collection if it has been allocated
			if (new_collection)
			{
				mParameters.put(type, typed_parameters);
			}
		}
		
		// clear the already calculated combined parameters
		clearCombinedParameters();
	}

	public QueryParameters clone()
	{
		QueryParameters new_instance = null;
		try
		{
			new_instance = (QueryParameters)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new DatabaseException(e);
		}
		
		if (new_instance != null)
		{
			if (mCombinedParameters != null)
			{
				new_instance.mCombinedParameters = new ArrayList<String>();
				new_instance.mCombinedParameters.addAll(mCombinedParameters);
			}
			
			if (mParameters != null)
			{
				new_instance.mParameters = new HashMap<QueryParameterType, Object>();
				for (Map.Entry<QueryParameterType, Object> entry : mParameters.entrySet())
				{
					if (entry.getKey().isSingular())
					{
						new_instance.mParameters.put(entry.getKey(), entry.getValue());
					}
					else
					{
						List<String> values = new ArrayList<String>();
						values.addAll((List<String>)entry.getValue());
						new_instance.mParameters.put(entry.getKey(), values);
					}
				}
			}
		}

		return new_instance;
	}
}
