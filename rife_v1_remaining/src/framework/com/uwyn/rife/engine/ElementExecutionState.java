/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementExecutionState.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import java.util.*;

import com.uwyn.rife.tools.ExceptionUtils;
import java.util.logging.Logger;
import java.util.regex.Matcher;

class ElementExecutionState implements Cloneable
{
	private RequestState			mRequestState = null;
	
	private RequestMethod			mMethod = null;
	private String					mPathInfo = null;
	private Stack<String>			mInheritanceStack = null;
	private Map<String, String[]>	mRequestParameters = null;
	private Map<String, String[]>	mTriggerInputs = null;
	private Map<String, String[]>	mPreservedInputs = null;
	private Map<String, String[]>	mPathInfoInputs = null;
	private HashSet<String>			mNonRequestParameterInputs = null;
	private List<TriggerContext>	mTriggerList = null;
	private List<TriggerContext>	mTriggerListState = null;
	
	ElementExecutionState(RequestState requestState)
	{
		setRequestState(requestState);

		if (mRequestState.getRequest() != null)
		{
			setMethod(mRequestState.getRequest().getMethod());
		}
	}
	
	void setRequestState(RequestState request)
	{
		mRequestState = request;
	}
	
	void setPathInfo(String pathInfo)
	{
		mPathInfo = pathInfo;
		clearVirtualInputs();
	}
	
	String getPathInfo()
	{
		return mPathInfo;
	}
	
	void setMethod(RequestMethod method)
	{
		assert method != null;
		
		mMethod = method;
	}
	
	void setInheritanceStack(Stack<String> inheritanceStack)
	{
		mInheritanceStack = inheritanceStack;
	}
	
	Stack<String> getInheritanceStack()
	{
		return mInheritanceStack;
	}
	
	void setTriggerInputs(Map<String, String[]> inputs)
	{
		mTriggerInputs = inputs;
	}
	
	Map<String, String[]> getTriggerInputs()
	{
		return mTriggerInputs;
	}
	
	void setNonRequestParameterInputs(HashSet<String> set)
	{
		mNonRequestParameterInputs = set;
	}
	
	boolean isInheritanceTarget()
	{
		if (mInheritanceStack != null &&
			0 == mInheritanceStack.size())
		{
			return true;
		}
		return false;
	}
	
	boolean inInheritanceStructure()
	{
		if (mInheritanceStack != null &&
			mInheritanceStack.size() > 0)
		{
			return true;
		}
		return false;
	}
	
	private boolean isNonRequestParameterInput(String name)
	{
		if (null == mNonRequestParameterInputs)
		{
			return false;
		}
		
		return mNonRequestParameterInputs.contains(name);
	}
	
	void clearVirtualInputs()
	{
		mPathInfoInputs = null;
		mPreservedInputs = null;
	}
	
	private Map<String, String[]> getPreservedInputs()
	{
		if (mPreservedInputs != null)
		{
			return mPreservedInputs;
		}
		
		Map<String, String[]> result = null;
		
		// merges the global preserved inputs with the element's preserved inputs
		ResultStates preserved = mRequestState.getElementResultStatesRestored();
		if (preserved.size() > 0)
		{
			// get the global result state
			ElementResultState global_result_state = preserved.get("");
			if (global_result_state != null)
			{
				result = global_result_state.getPreservedInputs();
			}
			
			// merge the element's result state
			ElementResultState element_result_state = preserved.get(mRequestState.buildContextId());
			if (element_result_state != null)
			{
				Map<String, String[]> element_inputs_map = element_result_state.getPreservedInputs();
				if (null == result)
				{
					result = element_inputs_map;
				}
				else
				{
					result.putAll(element_inputs_map);
				}
			}
		}
		
		if (null == result)
		{
			result = Collections.EMPTY_MAP;
		}
		
		mPreservedInputs = result;
		
		return mPreservedInputs;
	}
	
	private Map<String, String[]> getPathInfoInputs()
	{
		if (mPathInfoInputs != null)
		{
			return mPathInfoInputs;
		}
		
		if (mRequestState.getTarget().hasPathInfoMappings())
		{
			Map<String, String[]> inputs = new HashMap<String, String[]>();

			Matcher matcher;
			Iterator<String> input_names_it;
			for (PathInfoMapping mapping : mRequestState.getTarget().getPathInfoMappings())
			{
				matcher = mapping.getRegexp().matcher(mPathInfo);
				if (matcher.matches())
				{
					input_names_it = mapping.getInputs().iterator();
					for (int i = 1; i <= mapping.getInputs().size(); i++)
					{
						inputs.put(input_names_it.next(), new String[] {matcher.group(i)});
				}
					break;
				}
			}
			
			mPathInfoInputs = inputs;
		}
		else
		{
			mPathInfoInputs = Collections.EMPTY_MAP;
		}
		
		return mPathInfoInputs;
	}
	
	/*
	 * Inputs should only come from a HTTP request in a direct access.
	 *
	 * If inputs are provided through exits, the request is not checked.
	 *
	 * If inputs are provided through inheritance, the request is not checked,
	 * unless the element is the target element.
	 * In this case the original request is restored. However, in the meantime,
	 * values might have been modified higher in parent elements through the
	 * modification of global variables.
	 * Therefore, when inputs are requested in a target element of an inheritance
	 * stack, first the provided inputs are checked and then the original request.
	 */
	String[] getInputValues(String name)
	{
		return getInputValues(name, true);
	}
	
	String[] getInputValues(String name, boolean usePreservedInputs)
	{
		assert name != null;
		assert name.length() > 0;
		
		String[] input_values = null;
		
		if (isInheritanceTarget())
		{
			if (mTriggerInputs != null)
			{
				input_values = mTriggerInputs.get(name);
			}
			
			if (null == input_values &&
				getPreservedInputs() != null)
			{
				input_values = getPreservedInputs().get(name);
			}
			
			if (null == input_values &&
				!isNonRequestParameterInput(name))
			{
				input_values = getRequestParameterValues(name);
			}
			
			// if pathinfo inputs were provided, check them as a last resort
			if (null == input_values &&
				getPathInfoInputs().size() > 0)
			{
				input_values = getPathInfoInputs().get(name);
			}
		}
		else
		{
			// try to obtain the input from a previous element
			// (arrived at the current element through an exit)
			if (mTriggerInputs != null)
			{
				input_values = mTriggerInputs.get(name);
			}
			// if no inputs were provided through a previous element, obtain the
			// values from the request
			else
			{
				if (getPreservedInputs() != null)
				{
					input_values = getPreservedInputs().get(name);
				}
			
				if (null == input_values &&
					!isNonRequestParameterInput(name))
				{
					input_values = getRequestParameterValues(name);
				}
				
				// if pathinfo inputs were provided, check them as a last resort
				if (null == input_values &&
					getPathInfoInputs().size() > 0)
				{
					input_values = getPathInfoInputs().get(name);
				}
			}
		}
		
		return input_values;
	}
	
	boolean hasInputValue(String name)
	{
		return getInputValues(name) != null;
	}
	
	String getInput(String name)
	{
		String[] input_value = getInputValues(name);
		if (null == input_value)
		{
			return null;
		}
		
		return input_value[0];
	}
	
	Set<Map.Entry<String, String[]>> getInputEntries()
	{
		Set<Map.Entry<String, String[]>>	input_entries = null;
		
		if (isInheritanceTarget())
		{
			HashMap<String, String[]>	inputs_merge = new HashMap<String, String[]>();
			
			// put all the request parameters in the map
			inputs_merge.putAll(getRequestParameters());
			
			// remove all the request parameters that don't qualify as inputs
			if (mNonRequestParameterInputs != null)
			{
				for (String non_input_param : mNonRequestParameterInputs)
				{
					inputs_merge.remove(non_input_param);
				}
			}
			
			// put and override the map entries with the element inputs
			inputs_merge.putAll(mTriggerInputs);
			
			// add the preserved inputs
			if (getPreservedInputs() != null)
			{
				inputs_merge.putAll(getPreservedInputs());
			}
			
			// if pathinfo inputs were provided, add them as a last resort
			// without overriding the already existing inputs
			if (getPathInfoInputs().size() > 0)
			{
				for (Map.Entry<String, String[]> entry : getPathInfoInputs().entrySet())
				{
					if (!inputs_merge.containsKey(entry.getKey()))
					{
						inputs_merge.put(entry.getKey(), entry.getValue());
					}
				}
			}
			
			// get the entries of the map
			input_entries = inputs_merge.entrySet();
		}
		else
		{
			// try to obtain the input entries from a previous element
			// (arrived at the current element through an exit)
			if (mTriggerInputs != null)
			{
				input_entries = mTriggerInputs.entrySet();
			}
			// if no inputs were provided through a previous element, obtain the
			// values from the request
			else
			{
				// check if there are parameters that don't qualify as inputs or
				// if inputs should be added from the pathinfo
				if ((mNonRequestParameterInputs != null &&
					 mNonRequestParameterInputs.size() > 0) ||
					getPreservedInputs().size() > 0 ||
					getPathInfoInputs().size() > 0)
				{
					HashMap<String, String[]>	parameters = new HashMap<String, String[]>(getRequestParameters());
					
					// remove the non parameter inputs from the map of request parameters
					if (mNonRequestParameterInputs != null &&
						mNonRequestParameterInputs.size() > 0)
					{
						for (String non_input_param : mNonRequestParameterInputs)
						{
							parameters.remove(non_input_param);
						}
					}
					
					// add the preserved inputs
					if (getPreservedInputs() != null)
					{
						parameters.putAll(getPreservedInputs());
					}
					
					// if pathinfo inputs were provided, add them as a last resort
					// without overriding the already existing inputs
					if (getPathInfoInputs().size() > 0)
					{
						for (Map.Entry<String, String[]> entry : getPathInfoInputs().entrySet())
						{
							if (!parameters.containsKey(entry.getKey()))
							{
								parameters.put(entry.getKey(), entry.getValue());
							}
						}
					}
					
					input_entries = parameters.entrySet();
				}
				// just return all the request parameters
				else
				{
					input_entries = getRequestParameterEntries();
				}
			}
		}
		
		return input_entries;
	}
	
	boolean isNextTrigger(ElementInfo elementInfo)
	{
		assert elementInfo != null;
		
		if (mTriggerListState != null &&
			mTriggerListState.size() > 0 &&
			mTriggerListState.get(0).getDeclarationName().equals(elementInfo.getDeclarationName()))
		{
			return true;
		}
		
		return false;
	}
	
	boolean isNextChildTrigger(ElementInfo elementInfo, String childName)
	{
		assert elementInfo != null;
		
		if (null == childName)
		{
			return false;
		}
		
		if (isNextTrigger(elementInfo) &&
			mTriggerListState.get(0).getType() == TriggerContext.TRIGGER_CHILD &&
			mTriggerListState.get(0).getTriggerName().equals(childName))
		{
			return true;
		}
		
		return false;
	}
	
	boolean isNextExitTrigger(ElementInfo elementInfo, String exitName)
	{
		assert elementInfo != null;
		
		if (null == exitName)
		{
			return false;
		}
		
		if (isNextTrigger(elementInfo) &&
			mTriggerListState.get(0).getType() == TriggerContext.TRIGGER_EXIT &&
			mTriggerListState.get(0).getTriggerName().equals(exitName))
		{
			return true;
		}
		
		return false;
	}
	
	String getNextTriggerName()
	{
		return mTriggerListState.get(0).getTriggerName();
	}
	
	int getNextTriggerType()
	{
		return mTriggerListState.get(0).getType();
	}
	
	String[] getNextTriggerValues()
	{
		return mTriggerListState.get(0).getTriggerValues();
	}
	
	boolean hasTriggerList()
	{
		return mTriggerList != null;
	}
	
	void setTriggerList(List<TriggerContext> triggerList)
	{
		mTriggerList = triggerList;
		// make a copy of the list to be able to keep a seperate state the trace
		// the advancement of automatic trigger execution according
		// to the location and match against the trigger list
		mTriggerListState = new ArrayList<TriggerContext>(mTriggerList);
	}
	
	void addTrigger(TriggerContext triggerContext)
	{
		assert triggerContext != null;
		
		mTriggerList.add(triggerContext);
	}
	
	TriggerContext nextTrigger()
	{
		TriggerContext next_trigger = mTriggerListState.get(0);
		mTriggerListState.remove(0);
		
		return next_trigger;
	}
	
	String encodeTriggerList()
	{
		return TriggerListEncoder.encode(mTriggerList);
	}
	
	List<TriggerContext> cloneTriggerList()
	{
		return new ArrayList<TriggerContext>(mTriggerList);
	}
	
	RequestMethod getMethod()
	{
		return mMethod;
	}
	
	void setRequestParameters(Map<String, String[]> parameters)
	{
		mRequestParameters = parameters;
		mRequestState.setupContinuations();
		clearVirtualInputs();
	}
	
	Map<String, String[]> getRequestParameters()
	{
		if (null == mRequestParameters)
		{
			return mRequestState.getRequest().getParameters();
		}
		
		return mRequestParameters;
	}
	
	Collection<String> getRequestParameterNames()
	{
		return getRequestParameters().keySet();
	}
	
	boolean hasRequestParameterValue(String name)
	{
		assert name != null;
		assert name.length() > 0;
		
		return getRequestParameters().containsKey(name);
	}
	
	String getRequestParameter(String name)
	{
		assert name != null;
		assert name.length() > 0;
		
		String[] parameters = getRequestParameters().get(name);
		if (null == parameters)
		{
			return null;
		}
		return parameters[0];
	}
	
	String[] getRequestParameterValues(String name)
	{
		assert name != null;
		assert name.length() > 0;
		
		return getRequestParameters().get(name);
	}
	
	Set<Map.Entry<String, String[]>> getRequestParameterEntries()
	{
		return getRequestParameters().entrySet();
	}
	
	public ElementExecutionState clone()
	{
		ElementExecutionState new_elementstate = null;
		try
		{
			new_elementstate =  (ElementExecutionState)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			///CLOVER:OFF
			// this should never happen
			Logger.getLogger("com.uwyn.rife.site").severe(ExceptionUtils.getExceptionStackTrace(e));
			return null;
			///CLOVER:ON
		}
		
		new_elementstate.mRequestState = null;
		
		if (mInheritanceStack != null)
		{
			new_elementstate.mInheritanceStack = new Stack<String>();
			new_elementstate.mInheritanceStack.addAll(mInheritanceStack);
		}
		
		if (null == mRequestParameters)
		{
			new_elementstate.mRequestParameters = new HashMap<String, String[]>(mRequestState.getRequest().getParameters());
		}
		else
		{
			new_elementstate.mRequestParameters = new HashMap<String, String[]>(mRequestParameters);
		}
		
		if (mTriggerInputs != null)
		{
			new_elementstate.mTriggerInputs = new HashMap<String, String[]>(mTriggerInputs);
		}
		
		if (mPreservedInputs != null)
		{
			new_elementstate.mPreservedInputs = new HashMap<String, String[]>(mPreservedInputs);
		}
		
		if (mPathInfoInputs != null)
		{
			new_elementstate.mPathInfoInputs = new HashMap<String, String[]>(mPathInfoInputs);
		}
		
		if (mNonRequestParameterInputs != null)
		{
			new_elementstate.mNonRequestParameterInputs = new HashSet<String>(mNonRequestParameterInputs);
		}
		
		if (mTriggerList != null)
		{
			new_elementstate.mTriggerList = new ArrayList<TriggerContext>(mTriggerList);
		}
		
		if (mTriggerListState != null)
		{
			new_elementstate.mTriggerListState = new ArrayList<TriggerContext>(mTriggerListState);
		}
		
		return new_elementstate;
	}
}

