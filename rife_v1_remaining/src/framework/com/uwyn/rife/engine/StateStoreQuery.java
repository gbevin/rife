/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: StateStoreQuery.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.tools.StringUtils;

import java.util.*;
import java.util.logging.Logger;

public class StateStoreQuery implements StateStore
{
	static final int	MAX_URL_LENGTH = 2048;
		
	public static final String	IDENTIFIER = "query";
	
	StateStoreQuery()
	{
	}
	
	public void init(Request request)
	throws EngineException
	{
	}
	
	public void exportQueryUrl(CharSequenceDeferred deferred, String url, FlowState state, ElementInfo source, String type, String name)
	throws EngineException
	{
		if (state.isEmpty())
		{
			deferred.setContent(url);
			return;
		}
		
		Map<String, String[]> parameters = processState(state);
		
		String result;
		
		StringBuilder	query_parameters = new StringBuilder("?");
		if (parameters != null &&
			parameters.size() > 0)
		{
			// process the exit url query parameters
			Iterator<String>	parameter_names_it = parameters.keySet().iterator();
			String				parameter_name = null;
			String[]			parameter_values = null;
			
			while (parameter_names_it.hasNext())
			{
				boolean added_seperator = false;
				
				parameter_name = parameter_names_it.next();
				parameter_values = parameters.get(parameter_name);
				if (null == parameter_values)
				{
					continue;
				}
				
				if (query_parameters.length() > 1 &&
					!added_seperator)
				{
					added_seperator = true;
					query_parameters.append("&");
				}

				for (int i = 0; i < parameter_values.length; i++)
				{
					query_parameters.append(StringUtils.encodeUrlValue(parameter_name));
					query_parameters.append("=");
					query_parameters.append(StringUtils.encodeUrlValue(parameter_values[i]));
					if (i+1 < parameter_values.length)
					{
						query_parameters.append("&");
					}
				}
			}
		}
		
		if (1 == query_parameters.length())
		{
			deferred.setContent(url);
			return;
		}
		
		result = url+query_parameters.toString();
		if (result.length() <= MAX_URL_LENGTH)
		{
			deferred.setContent(deferred.encode(result));
			return;
		}
		
		Logger.getLogger("com.uwyn.rife.engine").warning("The "+type+" '"+name+"' of element '"+source.getId()+"' generated an URL whose length of "+result.length()+" exceeds the maximum length of "+MAX_URL_LENGTH+" bytes, using session state store instead. The generated URL was '"+result+"'.");
		
		StateStoreFactory.getInstance(StateStoreSession.IDENTIFIER).exportQueryUrl(deferred, url, state, source, type, name);
	}
	
	public void exportFormState(CharSequenceDeferred deferred, FlowState state, FormStateType stateType)
	throws EngineException
	{
		if (state.isEmpty())
		{
			deferred.setContent("");
			return;
		}
		
		Map<String, String[]> parameters = processState(state);
		if (parameters != null &&
			parameters.size() > 0)
		{
			StringBuilder form_state = new StringBuilder();
			if (FormStateType.JAVASCRIPT == stateType)
			{
				StateStoreTools.appendJavascriptHeader(form_state);
			}

			String[] parameter_values = null;
			
			for (String parameter_name : parameters.keySet())
			{
				parameter_values = parameters.get(parameter_name);
				if (parameter_values != null)
				{
					for (String parameter_value :  parameter_values)
					{
						if (FormStateType.PARAMS == stateType)
						{
							StateStoreTools.appendHtmlHiddenParam(form_state, deferred, parameter_name, parameter_value);
						}
						else if (FormStateType.JAVASCRIPT == stateType)
						{
							StateStoreTools.appendJavascriptHiddenParam(form_state, parameter_name, parameter_value);
						}
					}
				}
			}
			
			if (FormStateType.JAVASCRIPT == stateType)
			{
				StateStoreTools.appendJavascriptFooter(form_state);
			}
			
			deferred.setContent(form_state.toString());
		}
	}
	
	public void exportFormUrl(CharSequenceDeferred deferred, String url)
	throws EngineException
	{
		deferred.setContent(url);
	}
	
	public Map<String, String[]> restoreParameters(Request request)
	throws EngineException
	{
		// check if the state hasn't been stored in the session due to its
		// length being too long
		if (request.getParameters().containsKey(ReservedParameters.STATEID))
		{
			return StateStoreFactory.getInstance(StateStoreSession.IDENTIFIER).restoreParameters(request);
		}
		else
		{
			// do nothing, what the request provides is exactly the state that has
			// been stored in http
			return null;
		}
	}
	
	public ElementResultState createNewResultState(String contextId)
	throws EngineException
	{
		return new ElementResultStateQuery(contextId);
	}
	
	public Class getResultStateType()
	throws EngineException
	{
		return ElementResultStateQuery.class;
	}
	
	public ResultStates restoreResultStates(Request request)
	throws EngineException
	{
		ResultStates result_states = new ResultStates();
		extractResultStates(request.getParameters().get(ReservedParameters.CTXT), result_states);
		extractResultStates(request.getParameters().get(ReservedParameters.INPUTS), result_states);
		
		return result_states;
	}
	
	private Map<String, String[]> processState(FlowState state)
	{
		Map<String, String[]> parameters = state.getParameters();
		if (null == parameters)
		{
			parameters = new LinkedHashMap<String, String[]>();
		}
		
		if (state.getSubmissionContextId() != null)
		{
			StringBuilder inputs_parameter_builder = new StringBuilder();
			
			// add the global inputs
			if (state.hasSubmissionGlobalInputs())
			{
				inputs_parameter_builder.append(ParameterMapEncoder.encodeToBase64String(state.getSubmissionGlobalInputs()));
			}
			
			// remember this element's inputs together with the global inputs
			if (state.hasSubmissionElementInputs())
			{
				if (inputs_parameter_builder.length() > 0)
				{
					inputs_parameter_builder.append('_');
				}
				inputs_parameter_builder.append(ParameterMapEncoder.encodeToBase64String(state.getSubmissionElementInputs(), state.getSubmissionContextId()));
			}
			
			if (inputs_parameter_builder.length() > 0)
			{
				parameters.put(ReservedParameters.INPUTS, new String[] {inputs_parameter_builder.toString()});
			}
			
			// If the parameters contain a context inputs parameter, those values should be populated dynamically
			// from the preserved inputs. The context id of the submission is stored in the parameter values.
			// This will be used to ensure that the inputs of the submission that these context inputs
			// belong to aren't added a second time.
			ResultStates states = RequestState.getActiveRequestState().getElementResultStatesObtained();
			
			// add the preserved inputs of the other embedded elements from previous requests
			inputs_parameter_builder = new StringBuilder();
			for (Map.Entry<String, ElementResultState> entry : states.entrySet())
			{
				if (entry.getKey().length() > 0 &&
					!entry.getKey().equals(state.getSubmissionContextId()))
				{
					if (inputs_parameter_builder.length() > 0)
					{
						inputs_parameter_builder.append('_');
					}
					inputs_parameter_builder.append(((ElementResultState)entry.getValue()).getBase64EncodedState());
				}
			}
			
			// Set the context parameters if the value is not empty.
			if (inputs_parameter_builder.length() > 0)
			{
				parameters.put(ReservedParameters.CTXT, new String[] {inputs_parameter_builder.toString()});
			}
		}
		
		return parameters;
	}
	
	private void extractResultStates(String[] encodedState, ResultStates states)
	{
		if (encodedState != null &&
			encodedState.length > 0 &&
			encodedState[0] != null &&
			encodedState[0].length() > 0)
		{
			List<String> inputs_targeted = StringUtils.split(encodedState[0], "_");
			if (inputs_targeted.size() > 0)
			{
				for (String inputs : inputs_targeted)
				{
					String[] seperated = ParameterMapEncoder.seperateBase64ContextString(inputs);
					if (seperated != null)
					{
						ElementResultStateQuery state = new ElementResultStateQuery(seperated[0]);
						state.setEncodedState(seperated[1], inputs);
						states.put(state);
					}
				}
			}
		}
	}
}
