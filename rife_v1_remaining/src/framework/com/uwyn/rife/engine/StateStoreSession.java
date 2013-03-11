/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: StateStoreSession.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.tools.UniqueID;
import com.uwyn.rife.tools.UniqueIDGenerator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;

public class StateStoreSession implements StateStore
{
	public static final String	IDENTIFIER = "session";
	
	private static final String ATTRIBUTE_STATES_SUFFIX = "\u0000states";
	
	StateStoreSession()
	{
	}
	
	public void init(Request request)
	throws EngineException
	{
		request.getSession();
	}
	
	public void exportQueryUrl(CharSequenceDeferred deferred, String url, FlowState state, ElementInfo source, String type, String name) throws EngineException
	{
		Request		request = RequestState.getActiveRequestState().getRequest();
		Response	response = RequestState.getActiveRequestState().getResponse();
		HttpSession	session = request.getSession();
		
		if (state.isEmpty())
		{
			deferred.setContent(response.encodeURL(url));
			return;
		}
		
		UniqueID id = UniqueIDGenerator.generate(request.getRemoteAddr());
		storeStates(id, session);
		session.setAttribute(id.toString(), state);
		
		StringBuilder	query_parameters = new StringBuilder("?");
		query_parameters.append(ReservedParameters.STATEID);
		query_parameters.append("=");
		query_parameters.append(id.toString());
		
		deferred.setContent(response.encodeURL(url+query_parameters.toString()));
	}
	
	public void exportFormState(CharSequenceDeferred deferred, FlowState state, FormStateType stateType)
	throws EngineException
	{
		Request request = RequestState.getActiveRequestState().getRequest();

		HttpSession	session = request.getSession();

		if (state.isEmpty())
		{
			deferred.setContent("");
			return;
		}
		
		UniqueID id = UniqueIDGenerator.generate(request.getRemoteAddr());
		storeStates(id, session);
		session.setAttribute(id.toString(), state);
		
		StringBuilder form_state = new StringBuilder();
		if (FormStateType.PARAMS == stateType)
		{
			StateStoreTools.appendHtmlHiddenParam(form_state, deferred, ReservedParameters.STATEID, id.toString());
		}
		else if (FormStateType.JAVASCRIPT == stateType)
		{
			StateStoreTools.appendJavascriptHeader(form_state);
			StateStoreTools.appendJavascriptHiddenParam(form_state, ReservedParameters.STATEID, id.toString());
			StateStoreTools.appendJavascriptFooter(form_state);
		}
		
		deferred.setContent(form_state.toString());
	}
	
	public void exportFormUrl(CharSequenceDeferred deferred, String url) throws EngineException
	{
		Response	response = RequestState.getActiveRequestState().getResponse();
		deferred.setContent(response.encodeURL(url));
	}
	
	private FlowState getSessionFlowState(Request request)
	{
		String[]	value = request.getParameters().get(ReservedParameters.STATEID);
		if (null == value ||
			0 == value.length)
		{
			return null;
		}
		
		String		state_id = value[0];
		HttpSession	session = request.getSession(false);
		if (null == session)
		{
			return null;
		}
		
		return (FlowState)session.getAttribute(state_id);
	}
	
	public Map<String, String[]> restoreParameters(Request request)
	throws EngineException
	{
		FlowState state = getSessionFlowState(request);
		if (null == state)
		{
			return null;
		}
		
		Map<String, String[]> result = new LinkedHashMap<String, String[]>();
		if (state.hasParameters())
		{
			result.putAll(state.getParameters());
		}
		
		String[] values = null;
		for (Map.Entry<String, String[]> entry : request.getParameters().entrySet())
		{
			values = entry.getValue();
			
			// only override with non empty values
			if (values != null &&
				(values.length > 1 ||
				(values[0] != null && values[0].trim().length() > 0)))
			{
				result.put(entry.getKey(), values);
			}
		}
		return result;
	}
	
	public ResultStates restoreResultStates(Request request)
	throws EngineException
	{
		ResultStates result_states = null;
		
		String[] value = request.getParameters().get(ReservedParameters.STATEID);
		if (value != null &&
			value.length > 0)
		{
			String		state_id = value[0];
			HttpSession	session = request.getSession(false);
			if (session != null)
			{
				String states_id = (String)session.getAttribute(state_id+ATTRIBUTE_STATES_SUFFIX);
				if (states_id != null &&
					states_id.startsWith(ResultStates.ID_PREFIX))
				{
					result_states = (ResultStates)session.getAttribute(states_id);
					if (RifeConfig.Engine.getSessionStateStoreCloning())
					{
						result_states = result_states.cloneForStateStore(this);
						result_states.regenerateId();
					}
					else
					{
						session.removeAttribute(states_id);
					}
				}
			}
		}
		
		if (null == result_states)
		{
			result_states = new ResultStates();
		}
		
		FlowState flowstate = getSessionFlowState(request);
		if (flowstate != null)
		{
			if (flowstate.hasSubmissionGlobalInputs())
			{
				ElementResultStateSession result_state = new ElementResultStateSession("");
				result_state.setPreservedInputs(flowstate.getSubmissionGlobalInputs());
				result_states.put(result_state);
			}

			if (flowstate.hasSubmissionElementInputs() &&
				flowstate.getSubmissionContextId() != null)
			{
				ElementResultStateSession result_state = new ElementResultStateSession(flowstate.getSubmissionContextId());
				result_state.setPreservedInputs(flowstate.getSubmissionElementInputs());
				result_states.put(result_state);
			}
		}
		
		return result_states;
	}
	
	public ElementResultState createNewResultState(String contextId)
	throws EngineException
	{
		return new ElementResultStateSession(contextId);
	}
	
	public Class getResultStateType()
	throws EngineException
	{
		return ElementResultStateSession.class;
	}
	
	protected void storeStates(UniqueID stateId, HttpSession session)
	{
		ResultStates states = RequestState.getActiveRequestState().getElementResultStatesObtained();
		if (null == session.getAttribute(states.getId()))
		{
			session.setAttribute(states.getId(), states);
		}
		session.setAttribute(stateId+ATTRIBUTE_STATES_SUFFIX, states.getId());
	}
}
