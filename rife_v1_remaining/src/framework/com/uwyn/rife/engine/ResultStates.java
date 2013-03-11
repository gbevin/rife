/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ResultStates.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;
	
import com.uwyn.rife.tools.UniqueIDGenerator;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ResultStates implements Serializable, Cloneable
{
	private static final long serialVersionUID = 3452384106266423281L;
	
	static final String	ID_PREFIX = "resultstates\u0000";
	
	private String							mId;
	private Map<String, ElementResultState>	mResultStates = new LinkedHashMap<String, ElementResultState>();
	private long							mModified = System.currentTimeMillis();
	
	public ResultStates()
	{
		regenerateId();
	}
	
	public ResultStates(ResultStates states)
	{
		mId = states.getId();
		putAll(states);
	}
	
	public synchronized void regenerateId()
	{
		mId = ID_PREFIX+UniqueIDGenerator.generate().toString();
	}
	
	public synchronized void putAll(ResultStates states)
	{
		mResultStates.putAll(states.mResultStates);
		mModified = System.currentTimeMillis();
	}
	
	public synchronized void put(ElementResultState state)
	{
		mResultStates.put(state.getContextId(), state);
		mModified = System.currentTimeMillis();
	}
	
	public String getId()
	{
		return mId;
	}
	
	public long getModified()
	{
		return mModified;
	}
	
	public synchronized Set<Map.Entry<String, ElementResultState>> entrySet()
	{
		return mResultStates.entrySet();
	}
	
	public int size()
	{
		return mResultStates.size();
	}
	
	public ElementResultState get(String contextId)
	{
		return mResultStates.get(contextId);
	}
	
	public synchronized ElementResultState remove(String contextId)
	{
		return mResultStates.remove(contextId);
	}
	
	public ResultStates cloneForStateStore(StateStore store)
	{
		ResultStates new_resultstate = new ResultStates();
		
		// clone the result states
		new_resultstate.mResultStates = new LinkedHashMap<String, ElementResultState>();
		// get the type that the result states should be in for this state store
		Class result_state_type = store.getResultStateType();
		// iterate over all the result states of the original object and check if
		// they are in the correct type so that the state store can understand them
		for(ElementResultState result_state : mResultStates.values())
		{
			if (result_state_type.isAssignableFrom(result_state.getClass()))
			{
				new_resultstate.mResultStates.put(result_state.getContextId(), result_state);
			}
			// if the type isn't compatible, convert it to the correct one
			else
			{
				ElementResultState new_result_state = store.createNewResultState(result_state.getContextId());
				new_result_state.populate(result_state);
				new_resultstate.mResultStates.put(new_result_state.getContextId(), new_result_state);
			}
		}
		new_resultstate.mModified = mModified;
		new_resultstate.mId = mId;
		
		return new_resultstate;
	}
	
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		for (Map.Entry<String, ElementResultState> result_state : mResultStates.entrySet())
		{
			result.append(result_state.getKey());
			result.append(" : ");
			result.append("contid=");
			result.append(result_state.getValue().getContinuationId());
			result.append(", preservedinputs=");
			Map<String, String[]> preserved = result_state.getValue().getPreservedInputs();
			if (preserved != null)
			{
				result.append("{");
				boolean first_entry = true;
				for (Map.Entry<String, String[]> input : preserved.entrySet())
				{
					if (!first_entry)
					{
						result.append(",");
					}
					result.append(input.getKey());
					result.append("=");
					if (input.getValue() != null)
					{
						result.append("[");
						boolean first_value = true;
						for (String value : input.getValue())
						{
							if (!first_value)
							{
								result.append(",");
							}
							result.append(value);
							first_value = false;
						}
						result.append("]");
					}
					else
					{
						result.append("null");
					}
					first_entry = false;
				}
				result.append("}");
			}
			else
			{
				result.append("null");
			}
		}
		
		return result.toString();
	}
}
