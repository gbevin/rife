/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementContext.java 3928 2008-04-22 16:25:18Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.continuations.CallState;
import com.uwyn.rife.continuations.ContinuationConfigRuntime;
import com.uwyn.rife.continuations.ContinuationContext;
import com.uwyn.rife.continuations.ContinuationManager;
import com.uwyn.rife.continuations.exceptions.AnswerException;
import com.uwyn.rife.continuations.exceptions.CallException;
import com.uwyn.rife.continuations.exceptions.PauseException;
import com.uwyn.rife.continuations.exceptions.StepBackException;
import com.uwyn.rife.engine.exceptions.*;
import com.uwyn.rife.site.Constrained;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.ConstrainedUtils;
import com.uwyn.rife.template.*;
import com.uwyn.rife.template.exceptions.TemplateException;
import com.uwyn.rife.tools.*;
import com.uwyn.rife.tools.exceptions.BeanUtilsException;
import com.uwyn.rife.tools.exceptions.LightweightError;
import com.uwyn.rife.tools.exceptions.SerializationUtilsErrorException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ElementContext
{
	public static final String	SUFFIX_QUERY = "QUERY:";
	public static final String	SUFFIX_FORM = "FORM:";
	public static final String	SUFFIX_PARAMS = "PARAMS:";
	public static final String	SUFFIX_PARAMSJS = "PARAMSJS:";

	public static final String	PREFIX_EXIT = "EXIT:";
	public static final String	PREFIX_EXIT_QUERY = PREFIX_EXIT+SUFFIX_QUERY;
	public static final String	PREFIX_EXIT_FORM = PREFIX_EXIT+SUFFIX_FORM;
	public static final String	PREFIX_EXIT_PARAMS = PREFIX_EXIT+SUFFIX_PARAMS;
	public static final String	PREFIX_EXIT_PARAMSJS = PREFIX_EXIT+SUFFIX_PARAMSJS;
	public static final String	PREFIX_SUBMISSION = "SUBMISSION:";
	public static final String	PREFIX_SUBMISSION_QUERY = PREFIX_SUBMISSION+SUFFIX_QUERY;
	public static final String	PREFIX_SUBMISSION_FORM = PREFIX_SUBMISSION+SUFFIX_FORM;
	public static final String	PREFIX_SUBMISSION_PARAMS = PREFIX_SUBMISSION+SUFFIX_PARAMS;
	public static final String	PREFIX_SUBMISSION_PARAMSJS = PREFIX_SUBMISSION+SUFFIX_PARAMSJS;

	public static final String	PREFIX_PARAM = "PARAM:";
	public static final String	PREFIX_INPUT = "INPUT:";
	public static final String	PREFIX_OUTPUT = "OUTPUT:";
	public static final String	PREFIX_INCOOKIE = "INCOOKIE:";
	public static final String	PREFIX_OUTCOOKIE = "OUTCOOKIE:";
	public static final String	PREFIX_ELEMENT = "ELEMENT:";
	public static final String	PREFIX_PROPERTY = "PROPERTY:";
	public static final String	PREFIX_OGNL_ROLEUSER = "OGNL:ROLEUSER:";
	public static final String	PREFIX_MVEL_ROLEUSER = "MVEL:ROLEUSER:";
	public static final String	PREFIX_GROOVY_ROLEUSER = "GROOVY:ROLEUSER:";
	public static final String	PREFIX_JANINO_ROLEUSER = "JANINO:ROLEUSER:";

	public static final String	ID_WEBAPP_ROOTURL = "WEBAPP:ROOTURL";
	public static final String	ID_SERVER_ROOTURL = "SERVER:ROOTURL";
	
	public static final String	TAG_PROPERTY = "^"+PREFIX_PROPERTY+"\\s*(.*?)\\s*$";
	public static final String	TAG_ELEMENT = "^("+PREFIX_ELEMENT+"\\s*([\\-\\+]?)\\s*(.*?)\\s*)(?::([^:]*))?$";
	public static final String	TAG_EXITFIELD = "^"+PREFIX_EXIT+"("+SUFFIX_QUERY+"|"+SUFFIX_FORM+"|"+SUFFIX_PARAMS+"|"+SUFFIX_PARAMSJS+")\\s*@([\\w\\.]*?)\\s*$";
	public static final String	TAG_SUBMISSIONFIELD = "^"+PREFIX_SUBMISSION+"("+SUFFIX_QUERY+"|"+SUFFIX_FORM+"|"+SUFFIX_PARAMS+"|"+SUFFIX_PARAMSJS+")\\s*@([\\w\\.]*?)\\s*$";
	public static final String	TAG_OGNL_ROLEUSER = "(?s)^("+PREFIX_OGNL_ROLEUSER+".*):\\s*\\[\\[\\s*+(.*?)\\s*+\\]\\]\\s*$";
	public static final String	TAG_MVEL_ROLEUSER = "(?s)^("+PREFIX_MVEL_ROLEUSER+".*):\\s*\\[\\[\\s*+(.*?)\\s*+\\]\\]\\s*$";
	public static final String	TAG_JANINO_ROLEUSER = "(?s)^("+PREFIX_JANINO_ROLEUSER+".*):\\s*\\[\\[\\s*+(.*?)\\s*+\\]\\]\\s*$";
	public static final String	TAG_GROOVY_ROLEUSER = "(?s)^("+PREFIX_GROOVY_ROLEUSER+".*):\\s*\\[\\[\\s*+(.*?)\\s*+\\]\\]\\s*$";
	
	private static final ThreadLocal<ElementSupport> ACTIVE_ELEMENT_SUPPORT = new ThreadLocal<ElementSupport>();
	
	private final ElementSupport	mElement;
	private final ElementInfo		mElementInfo;
	private final RequestState		mRequestState;
	private final OutputValues		mOutputs;
	private final OutcookieValues	mOutcookies;

	private Response							mResponse = null;
	private ArrayList<OutputListener>			mOutputListeners = null;
	private ArrayList<OutcookieListener>		mOutcookieListeners = null;
	private String								mContextId = null;
	private String								mSubmission = null;
	private boolean								mSteppedBack = false;
	
	public static ElementSupport getActiveElementSupport()
	{
		return ACTIVE_ELEMENT_SUPPORT.get();
	}
	
	ElementContext(ElementSupport element, RequestState state, Response response)
	throws EngineException
	{
		assert element != null;
		assert state != null;
		assert response != null;

		mElement = element;
		synchronized (mElement)
		{
			mElement.setElementContext(this);
			
			mElementInfo = mElement.getElementInfo();
			
			mRequestState = state;
			mResponse = response;
			mOutputs = new OutputValues(this);
			mOutcookies = new OutcookieValues(this);
			
			// register the EmbeddingListener of the current state so that the
			// output values are kept in sync with global vars and the outcookies
			// are kept in sync with the global cookies
			if (state.isEmbedded())
			{
				RequestState.EmbeddingListener listener = state.getEmbeddingListener();
				addOutputListener(listener);
				addOutcookieListener(listener);
			}
			
			// register the PrecedenceListener of the current state so that the
			// output values are kept in sync with global vars and the outcookies
			// are kept in sync with the global cookies
			if (state.isPreceeding())
			{
				RequestState.PrecedenceListener listener = state.getPrecedenceListener();
				addOutputListener(listener);
				addOutcookieListener(listener);
			}
	
			ElementExecutionState element_state = getElementState();

			// if the child element has been reached, replace the request parameters with the
			// original child request parameters
			if (element_state.isInheritanceTarget() &&
				element_state.hasRequestParameterValue(ReservedParameters.CHILDREQUEST))
			{
				ChildRequestEncoder.decode(element.getElementInfo(), mRequestState);
			}
			
			// automatically set an output value if the input value has been provided
			// and it's a global variable
			String[]	output_values = null;
			for (String globalvar_name : mElementInfo.getGlobalVarNames())
			{
				if (element_state.hasInputValue(globalvar_name))
				{
					output_values = element_state.getInputValues(globalvar_name);
					setAutomatedOutputValues(globalvar_name, output_values);
				}
			}
		}
	}
	
	private void handleChildTriggerVariablesPre()
	throws EngineException
	{
		ElementExecutionState element_state = getElementState();
		
		// verify if no value are present that will automatically launch a child trigger
		if (mElementInfo.getChildTriggerNames().size() > 0)
		{
			// check each child trigger for a corresponding value
			// if it was found, launch the trigger
			for (String child_trigger_name : mElementInfo.getChildTriggerNames())
			{
				// check if a provided input value is acceptable for this
				// element as input or global variable
				if (element_state.hasInputValue(child_trigger_name))
				{
					if (mElementInfo.containsGlobalVar(child_trigger_name) ||
						mElementInfo.containsInput(child_trigger_name))
					{
						triggerChild(child_trigger_name, getInputValues(child_trigger_name));
					}
				}
				// check if a provided cookie is acceptable for this
				// element as incookie
				else if (mRequestState.getCookie(child_trigger_name) != null)
				{
					if (mElementInfo.containsGlobalCookie(child_trigger_name) ||
						mElementInfo.containsIncookie(child_trigger_name))
					{
						triggerChild(child_trigger_name, new String[] {getCookie(child_trigger_name).getValue()});
					}
				}
				// check if a default input value is present that can trigger the child
				else if (mElementInfo.hasInputDefaultValues(child_trigger_name))
				{
					triggerChild(child_trigger_name, mElementInfo.getInputDefaultValues(child_trigger_name));
				}
				// check if a default cookie value is present that can trigger the child
				else if (mElementInfo.hasIncookieDefaultValue(child_trigger_name))
				{
					triggerChild(child_trigger_name, new String[] {mElementInfo.getIncookieDefaultValue(child_trigger_name)});
				}
				// check if a global var default is present for the trigger name
				else if (mElementInfo.containsGlobalVar(child_trigger_name) &&
						 mElementInfo.hasGlobalVarDefaultValues(child_trigger_name))
				{
					triggerChild(child_trigger_name, mElementInfo.getGlobalVarDefaultValues(child_trigger_name));
				}
				// check if a global cookie default is present for the trigger name
				else if (mElementInfo.containsGlobalCookie(child_trigger_name) &&
						 mElementInfo.hasGlobalCookieDefaultValue(child_trigger_name))
				{
					triggerChild(child_trigger_name, new String[] {mElementInfo.getGlobalCookieDefaultValue(child_trigger_name)});
				}
			}
		}
	}
	
	private void handleChildTriggerVariablesPost()
	throws EngineException
	{
		// verify if no value are present that will automatically launch a child trigger
		if (mElementInfo.getChildTriggerNames().size() > 0)
		{
			// check each child trigger for a corresponding value
			// if it was found, launch the trigger
			for (String child_trigger_name : mElementInfo.getChildTriggerNames())
			{
				// check if an output default value is present
				if (mElementInfo.hasOutputDefaultValues(child_trigger_name))
				{
					String[]	output_defaultvalues = mElementInfo.getOutputDefaultValues(child_trigger_name);
					triggerChild(child_trigger_name, output_defaultvalues);
				}
			}
		}
	}
	
	ElementContext processContext()
	throws EngineException
	{
		long start = mElementInfo.startTrace();
		boolean clear_request = false;
		
		ElementContext result = null;
		
		ElementExecutionState element_state = getElementState();
		
		try
		{
			ElementSupport previous_active_element;
			
			synchronized (mElement)
			{
				// handle the inheritance structure
				if (element_state.inInheritanceStructure())
				{
					// try to see if the top of the trigger list matches the current element
					if (element_state.isNextTrigger(mElementInfo))
					{
						// if it does, and it's a child trigger, use the values to launch a child trigger
						if (TriggerContext.TRIGGER_CHILD == element_state.getNextTriggerType() &&
							mElementInfo.containsChildTrigger(element_state.getNextTriggerName()))
						{
							triggerChild(element_state.getNextTriggerName(), element_state.getNextTriggerValues());
						}
						else if (TriggerContext.TRIGGER_EXIT == element_state.getNextTriggerType() &&
								 mElementInfo.containsExit(element_state.getNextTriggerName()))
						{
							exit(element_state.getNextTriggerName());
						}
					}
					
					handleChildTriggerVariablesPre();
				}
				
				// handle precedence
				mRequestState.handlePrecedence(mResponse, getElementInfo());
				
				// set the response content type if this has been specified by the element
				if (mElementInfo.getContentType() != null &&
					mElementInfo.getContentType().length() > 0)
				{
					mResponse.setContentType(mElementInfo.getContentType());
				}

				// obtain the continuation context
				ContinuationContext continuation_context = mRequestState.getContinuationContext(mElementInfo);
				ContinuationContext.setActiveContext(continuation_context);
				ContinuationConfigRuntime.setActiveConfigRuntime(EngineContinuationConfigRuntimeSingleton.INSTANCE);
				
				// register the element as the active one in this thread and remember
				// which one that was previously active
				previous_active_element = ACTIVE_ELEMENT_SUPPORT.get();

				ACTIVE_ELEMENT_SUPPORT.set(mElement);
			}
			
			try
			{
				final ElementAware element_aware;
				Method submission_handler = null;
				
				synchronized (mElement)
				{
					// get the element aware interface
					element_aware = mElement.getElementAware();
					
					// check if there's are submissions
					if (element_state.hasRequestParameterValue(ReservedParameters.SUBMISSION))
					{
						String[]		submission_names = element_state.getRequestParameterValues(ReservedParameters.SUBMISSION);
						String[]		submission_contexts = element_state.getRequestParameterValues(ReservedParameters.SUBMISSIONCONTEXT);
						String			submission_context = null;
						String			submission_context_id = null;
						String			submission_target = null;
						HashSet<String>	non_requestparameter_inputs = null;
						int				counter = 0;
						for (String submission_name : submission_names)
						{
							// get the submission context
							if (null == submission_contexts ||
								counter >= submission_contexts.length)
							{
								submission_context = getContextId();
							}
							else
							{
								try
								{
									byte[] decoded = Base64.decode(submission_contexts[counter].getBytes("UTF-8"));
									if (decoded != null &&
										decoded.length > 0)
									{
										submission_context = new String(decoded);
									}
									else
									{
										submission_context = getContextId();
									}
								}
								catch (UnsupportedEncodingException e)
								{
									// should never happen
								}
							}
							
							// split up in the submission context id and the submission target
							int seperator_index = submission_context.indexOf("^");
							if (-1 == seperator_index)
							{
								submission_context_id = submission_context;
								submission_target = submission_context;
							}
							else
							{
								submission_context_id = submission_context.substring(0, seperator_index);
								submission_target = submission_context.substring(seperator_index+1);
							}
							
							// check if the submission target corresponds to the context id
							if (null == mSubmission &&
								getContextId().equals(submission_context_id) &&
								mElementInfo.hasSubmission(submission_name))
							{
								mSubmission = submission_name;
							}
							// if it doesn't, use the submission target element id to
							// retrieve the submission declaration, and the parameters
							// that have to be disabled as inputs for this element
							else
							{
								// get the submission target
								if (0 == submission_target.length())
								{
									submission_target = getElementInfo().getId();
								}
	
								// obtain the element info
								ElementInfo submission_target_element = mElementInfo.getSite().resolveId(submission_target);
								if (submission_target_element != null)
								{
									// get the submission declaration
									Submission submission = submission_target_element.getSubmission(submission_name);
									if (submission != null)
									{
										// get the submission parameter names
										Collection<String> names = submission.getParameterNames();
										if (names != null &&
											names.size() > 0)
										{
											if (null == non_requestparameter_inputs)
											{
												non_requestparameter_inputs = new HashSet<String>();
											}
											
											// add the submission parameters to the collection
											// of inputs that shouldn't retrieve their values
											// from the request parameters
											non_requestparameter_inputs.addAll(names);
										}
									}
								}
							}
		
							counter++;
						}
						
						// register the non parameter inputs
						element_state.setNonRequestParameterInputs(non_requestparameter_inputs);
					}
					
					// check if there's a submission handler
					
					// check if a submission is present and a corresponding do*() method is
					// available and retrieve the method
					final String submission_name = getSubmission();
					if (submission_name != null)
					{
						String submission_handler_name = "do"+StringUtils.capitalize(submission_name);
						try
						{
							submission_handler = element_aware.getClass().getMethod(submission_handler_name, (Class[])null);
	                        submission_handler.setAccessible(true);
	                    }
						catch (NoSuchMethodException e)
						{
							submission_handler = null;
						}
						catch (SecurityException e)
						{
							throw new EngineException(e);
						}
					}
					
					// inject the properties request values into the element instance
					new ElementInjector(this).performInjection(submission_name);
	
					// update the target element in the response
					mResponse.setLastElement(mElement);
					
					// initialize the element in a fully setup context
					mElement.initialize();
				}
				
				try
				{
					try
					{
						synchronized (mElement)
						{
							// call the processElement() method of the element itself if no handler could be found
							if (null == submission_handler)
							{
								element_aware.processElement();
							}
							// call the submission handler if it's available
							else
							{
								try
								{
									submission_handler.invoke(element_aware, (Object[])null);
								}
								catch (InvocationTargetException e)
								{
									if (e.getCause() instanceof LightweightError)
									{
										throw (LightweightError)e.getCause();
									}
									else if (e.getCause() instanceof EngineException)
									{
										throw (EngineException)e.getCause();
									}
									else
									{
										throw new EngineException(e.getCause());
									}
								}
								catch (IllegalArgumentException e)
								{
									throw new EngineException(e);
								}
								catch (IllegalAccessException e)
								{
									throw new EngineException(e);
								}
							}
						}
					}
					catch (CallException e)
					{
						ContinuationContext context = e.getContext();
						
						// register context
						ContinuationManager	manager = mElementInfo.getSite().getContinuationManager();
						manager.addContext(context);
						
						synchronized (mElement)
						{
							String exit = (String)e.getTarget();
							mElementInfo.validateExitName(exit);
							if (null == mElementInfo.getFlowLink(exit))
							{
								throw new ExitNotAttachedException(mElementInfo.getDeclarationName(), exit);
							}
						
							// create a new call state
							CallState call_state = new CallState(context.getId(), element_state.clone());
							context.setCreatedCallState(call_state);
							
							// setup the exit
							result = setupExitContext(exit);
						}
					}
				}
				finally
				{
					synchronized (mElement)
					{
						String context_id = getContextId();
						
						ElementResultState result_state = null;
	
						// extract the preserved inputs of this element
						Set<Map.Entry<String, String[]>>	output_value_entries = mOutputs.aggregateValues().entrySet();
						Map<String, String[]>				preserved_inputs = collectPreservedInputs(output_value_entries);
						
						// if a call continuation is active, also preserve the previous preserved inputs without overriding the new ones
						ContinuationContext active_context = ContinuationContext.getActiveContext();
						if (active_context != null &&
							active_context.getActiveCallState() != null)
						{
							ElementResultState previous_result_state = mRequestState.getElementResultStatesRestored().get(context_id);
							if (previous_result_state != null)
							{
								Map<String, String[]> previous_preserved_inputs = previous_result_state.getPreservedInputs();
								if (previous_preserved_inputs != null)
								{
									if (null == preserved_inputs)
									{
										preserved_inputs = previous_preserved_inputs;
									}
									else
									{
										for (Map.Entry<String, String[]> entry : previous_preserved_inputs.entrySet())
										{
											if (!preserved_inputs.containsKey(entry.getKey()))
											{
												preserved_inputs.put(entry.getKey(), entry.getValue());
											}
										}
									}
								}
							}
							
							// preserve the continuation ID of this element
							if (null == result_state)
							{
								result_state = mElementInfo.getStateStore().createNewResultState(context_id);
							}
							result_state.setContinuationId(active_context.getActiveCallState().getContinuationId());
						}
						
						// add the preserved inputs to the result state
						if (preserved_inputs != null &&
							preserved_inputs.size() > 0)
						{
							if (null == result_state)
							{
								result_state = mElementInfo.getStateStore().createNewResultState(context_id);
							}
							
							result_state.setPreservedInputs(preserved_inputs);
						}
						
						// state the resulting element state
						if (result_state != null)
						{
							mRequestState.getElementResultStatesObtained().put(result_state);
						}
						
						// handle the setting of the getter outcookies
						mOutcookies.processGetters();
	
						// handle the firing of listener methods for getters outjection
						mOutputs.processGetters();
					}
					
					// handle the outcookie and output childtriggers from the getters outjection
					mOutcookies.processGetterChildTriggers();
					mOutputs.processGetterChildTriggers();
				}
			}
			catch (AnswerException e)
			{
				synchronized (mElement)
				{
					// only answer a call if a call state is available
					// otherwise the answer will function as a regular return
					if (e.getContext().getActiveCallState() != null)
					{					
						throw e;
					}
				}
			}
			finally
			{
				synchronized (mElement)
				{
					// restore the previously active element
					ACTIVE_ELEMENT_SUPPORT.set(previous_active_element);
				}
			}
			
			synchronized (mElement)
			{
				// handle the child trigger values that could have an effect after
				// the actual element logic
				handleChildTriggerVariablesPost();
				
				// handle the default outcookies
				if (mElementInfo.hasOutcookieDefaults())
				{
					Cookie	default_cookie = null;
					
					for (Map.Entry<String, String> default_outcookie_entry : mElementInfo.getDefaultOutcookies().entrySet())
					{
						if (!mOutcookies.contains(default_outcookie_entry.getKey()))
						{
							default_cookie = new Cookie(default_outcookie_entry.getKey(), default_outcookie_entry.getValue());
							default_cookie.setPath("");
							setCookie(default_cookie);
						}
					}
				}
				
				// handle the default global cookies
				if (mElementInfo.hasGlobalCookieDefaults())
				{
					Cookie	default_cookie = null;
					
					for (Map.Entry<String, String> default_globalcookie_entry : mElementInfo.getDefaultGlobalCookies().entrySet())
					{
						if (!mOutcookies.contains(default_globalcookie_entry.getKey()))
						{
							default_cookie = new Cookie(default_globalcookie_entry.getKey(), default_globalcookie_entry.getValue());
							default_cookie.setPath("");
							setCookie(default_cookie);
						}
					}
				}
			}
		}
		catch (PauseException e)
		{
			ContinuationContext context = e.getContext();
			
			// register context
			ContinuationManager	manager = mElementInfo.getSite().getContinuationManager();
			manager.addContext(context);

			synchronized (mElement)
			{
				// preserve the continuation ID of this element
				String				context_id = getContextId();
				ElementResultState	result_state = mRequestState.getElementResultStatesObtained().get(context_id);
				
				if (null == result_state)
				{
					result_state = mElementInfo.getStateStore().createNewResultState(context_id);
					mRequestState.getElementResultStatesObtained().put(result_state);
				}
				result_state.setContinuationId(context.getId());
				
				clear_request = true;
				try
				{
					mResponse.flush();
				}
				catch (EngineException e2)
				{
					// if errors occurred during flushing it means that the client has
					// disconnected, disregard them
				}
				mResponse = null;
			}
		}
		catch (ChildTriggeredException e)
		{
			synchronized (mElement)
			{
				result = setupChildtriggerContext(e.getChildTriggerName(), e.getChildTriggerValues());
			}
		}
		catch (ExitTriggeredException e)
		{
			synchronized (mElement)
			{
				result = handleExitTrigger(e.getExitName());
			}
		}
		catch (StepBackException e)
		{
			ContinuationContext	context = e.getContext();
			
			// register context
			ContinuationManager manager = mElementInfo.getSite().getContinuationManager();
			manager.addContext(context);
			
			synchronized (mElement)
			{
				// preserve the continuation ID of this element
				String				context_id = getContextId();
				ElementResultState	result_state = mRequestState.getElementResultStatesObtained().get(context_id);
				if (null == result_state)
				{
					result_state = mElementInfo.getStateStore().createNewResultState(context_id);
					mRequestState.getElementResultStatesObtained().put(result_state);
				}
				result_state.setContinuationId(context.getId());
				
				// try to obtain the id of the previous continuation
				String stepbackid = e.lookupStepBackId();
	
				// there is no previous continuation, so start from the beginning again
				if (null == stepbackid)
				{
					ContinuationContext.clearActiveContext();
					mRequestState.setContinuationId(null);
				}
				else
				{
					mRequestState.setContinuationId(stepbackid);
				}
				
				result = new ElementContext(mElement, mRequestState, mResponse);
				result.mSteppedBack = true;
			}
		}
		catch (ForwardException e)
		{
			synchronized (mElement)
			{
				handleForward(e.getUrl());
			}
		}
		catch (RedirectException e)
		{
			synchronized (mElement)
			{
				getResponse().sendRedirect(e.getUrl());
			}
		}
		finally
		{
			synchronized (mElement)
			{
				try
				{
					// flush the output buffer
					if (mResponse != null)
					{
						mResponse.flush();
					}
					
					// trace element activity
					mElementInfo.outputTrace(start, mRequestState);
				}
				catch (EngineException e)
				{
					// if errors occurred during flushing it means that the client has
					// disconnected, disregard them
				}
				finally
				{
					// clear the request if this is needed, this typically happens
					// for continuations, to prevent the request to be cloned when
					// element instances are cloned
					if (clear_request)
					{
						mRequestState.clearRequest();
					}
				}
			}
		}

		return result;
	}

	private ElementContext setupChildtriggerContext(String childtrigger, String[] values)
	throws EngineException
	{
		ElementContext			element_context = null;
		Map<String, String[]>	child_inputs = null;
		
		ElementExecutionState element_state = getElementState();		
		if (element_state.inInheritanceStructure())
		{
			// construct the child element
			String child_element_id = element_state.getInheritanceStack().pop();
			ElementInfo child_element_info = mRequestState.getSite().resolveId(child_element_id);
			
			// verify if the trigger wasn't raised automatically, if this is the
			// case, the first trigger context has to be removed from the trigger
			// list since it has been used by this element
			if (element_state.isNextChildTrigger(mElementInfo, childtrigger))
			{
				// obtain the stored exit inputs
				child_inputs = element_state.nextTrigger().getParameters();
			}
			// since the current element deferred the logical flow to its child,
			// record the child trigger that caused this
			else
			{
				child_inputs = collectChildInputValues(child_element_info);
				
				// don't store an child trigger which isn't dependent on watched values in the
				// trigger list, the whole element will be executed each time for those triggers
				if (childtrigger != null)
				{
					element_state.addTrigger(TriggerContext.generateChildTrigger(mElementInfo, childtrigger, values, child_inputs));
				}
			}
			
			// construct the child element's context
			element_state.setTriggerInputs(child_inputs);
			element_context = mRequestState.getElementContext(child_element_info, mResponse);
		}
		
		return element_context;
	}
	
	private ElementContext handleExitTrigger(String exit)
	throws EngineException
	{
		// get the the flowlink to check first if it's a redirection
		FlowLink	flowlink = mElementInfo.getFlowLink(exit);
		if (null == flowlink)
		{
			throw new ExitNotAttachedException(mElementInfo.getDeclarationName(), exit);
		}

		if (flowlink.isRedirect())
		{
			getResponse().sendRedirect(ElementContextFlowGeneration.generateExitQueryUrl(this, flowlink, null, mOutputs.aggregateValues(), null).toString());
			return null;
		}
		else
		{
			return setupExitContext(exit);
		}
	}

	private ElementContext setupExitContext(String exit)
	throws EngineException
	{
		// get the element info of the exit target
		FlowLink flowlink = mElementInfo.getFlowLink(exit);
		if (null == flowlink)
		{
			throw new ExitNotAttachedException(mElementInfo.getDeclarationName(), exit);
		}
		ElementInfo target = flowlink.getExitTarget(mRequestState);
		
		// handle embedding cancellation
		if (mRequestState.isEmbedded() &&
			flowlink.cancelEmbedding())
		{
			// this flag is set in the embedding context to indicate that the
			// embedding needs to be cancelled, it will be checked by the
			// processEmbeddedElement method of the embedding element
			mRequestState.getEmbeddingContext().setCancelEmbedding(true);
			
			// clear all current output buffers
			RequestState request_state = mRequestState;
			while (request_state.isEmbedded())
			{
				request_state.getEmbeddingContext().getElementContext().getResponse().clearBuffer();
				request_state = request_state.getEmbeddingContext().getElementContext().getRequestState();
			}
		}
		
		Map<String, String[]>	exit_request_params = null;
		Map<String, String[]>	exit_inputs = null;
		Stack<String>			inheritance_stack = null;
		
		Set<Map.Entry<String, String[]>>	output_entries = mOutputs.aggregateValues().entrySet();
		ElementExecutionState				element_state = getElementState();
		if (element_state.inInheritanceStructure() &&
			!flowlink.cancelInheritance())
		{
			// verify if the trigger wasn't raised automatically, if this is the
			// case, the first trigger context has to be removed from the trigger
			// list since it has been used by this element
			if (element_state.isNextExitTrigger(mElementInfo, exit))
			{
				exit_request_params = element_state.getRequestParameters();
				// obtain the stored exit inputs
				exit_inputs = element_state.nextTrigger().getParameters();
			}
			// since the current element deferred the logical flow to an exit,
			// record this action in the trigger list
			else
			{
				exit_request_params = new HashMap<String, String[]>();
				exit_request_params.put(ReservedParameters.CHILDREQUEST, new String[] {getEncodedChildRequest()});
				
				exit_request_params.put(ReservedParameters.TRIGGERLIST, element_state.getRequestParameterValues(ReservedParameters.TRIGGERLIST));
				
				exit_inputs = collectExitInputValues(flowlink, output_entries, target, flowlink.isSnapback());
				
				element_state.addTrigger(TriggerContext.generateExitTrigger(mElementInfo, exit, exit_inputs));
			}
			
			inheritance_stack = element_state.getInheritanceStack();
			
			// check for successive inheritance stacks
			Stack<ElementInfo>	dest_inheritance_stack = target.getInheritanceStack();
			if (dest_inheritance_stack != null)
			{
				for (ElementInfo element_info : dest_inheritance_stack)
				{
					target = element_info;
					inheritance_stack.add(element_info.getId());
				}
				inheritance_stack.pop();
			}
		}
		else
		{
			exit_request_params = new HashMap<String, String[]>();
			exit_inputs = collectExitInputValues(flowlink, output_entries, target, flowlink.isSnapback());
			
			mRequestState.setTarget(target);
			
			// obtain the inheritance stack and if it exists the top parent
			// this isn't done if an inheritance structure is already present
			Stack<ElementInfo>	dest_inheritance_stack = target.getInheritanceStack();
			if (dest_inheritance_stack != null)
			{
				inheritance_stack = new Stack<String>();
				for (ElementInfo element_info : dest_inheritance_stack)
				{
					target = element_info;
					inheritance_stack.add(element_info.getId());
				}
				inheritance_stack.pop();
			}
		}
		
		// the request method isn't get or post anymore since all parameters have
		// been removed
		element_state.setMethod(RequestMethod.EXIT);
		
		// construct the target element's context
		element_state.setRequestParameters(exit_request_params);
		element_state.setInheritanceStack(inheritance_stack);
		element_state.setTriggerInputs(exit_inputs);
		
		// return the new element context
		return mRequestState.getElementContext(target, mResponse);
	}
	
	private void handleForward(String url)
	{
		boolean is_absolute_url = true;
		if (-1 == url.indexOf(":/"))
		{
			is_absolute_url = false;
			
			StringBuilder absolute_url = new StringBuilder();
			absolute_url.append(mRequestState.getWebappRootUrl(RifeConfig.Engine.getLocalForwardPort()));
			if (url.startsWith("/"))
			{
				absolute_url.append(url.substring(1));
			}
			else
			{
				absolute_url.append(url);
			}
			url = absolute_url.toString();
		}
		
		try
		{
			Map<String, String> request_header_map = new HashMap<String, String>();
			
			Request	request = mRequestState.getRequest();
			Enumeration request_header_names = request.getHeaderNames();
			
			// convert the headers to a map
			if (request_header_names.hasMoreElements())
			{
				String	header_name = null;
				String	header_name_lowercase = null;
				String	header_value = null;
				do
				{
					header_name = (String)request_header_names.nextElement();
					if (null != header_name)
					{
						header_name_lowercase = header_name.toLowerCase();
						header_value = request.getHeader(header_name);
						if (is_absolute_url &&
							("host".equals(header_name_lowercase) ||
							 "connection".equals(header_name_lowercase) ||
							 "keep-alive".equals(header_name_lowercase) ||
							 "content-type".equals(header_name_lowercase) ||
							 "content-length".equals(header_name_lowercase)))
						{
							continue;
						}
						request_header_map.put(header_name, header_value);
					}
				}
				while (request_header_names.hasMoreElements());
			}
			
			// retrieve the page
			HttpUtils.Page page = new HttpUtils.Request(url).headers(request_header_map).retrieve();
			
			// incorporate the page data in the current request
			if ((page.getResponseCode() / 100) != 2)
			{
				// report errors
				mResponse.sendError(page.getResponseCode(), page.getResponseMessage());
			}
			else
			{
				// preserve the status code
				mResponse.setStatus(page.getResponseCode());
			}

			if (page.getContent() != null)
			{
				mResponse.print(page.getContent());
			}
			
			for (Map.Entry<String, List<String>> header : page.getHeaders().entrySet())
			{
				if (header.getKey() != null)
				{
					String key = header.getKey().toLowerCase();
					
					// strip out several duplicate headers
					if (key.equals("accept-encoding") ||
						key.equals("content-encoding") ||
						key.equals("content-length") ||
						key.equals("date") ||
						key.equals("host") ||
						key.equals("server") ||
						key.equals("transfer-encoding"))
					{
						continue;
					}
					
					// handle the content type differently
					if (key.equals("content-type"))
					{
						mResponse.setContentType(HttpUtils.extractMimeTypeFromContentType(page.getContentType()));
						continue;
					}
					
					for (String header_value : header.getValue())
					{
						mResponse.addHeader(header.getKey(), header_value);
					}
				}
			}
		}
		catch (IOException e)
		{
			mResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		
		RequestDispatcher dispatcher = mRequestState.getRequest().getRequestDispatcher(url);
		if (null == dispatcher)
		{
			mResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
	
	RequestState getRequestState()
	{
		return mRequestState;
	}
	
	ElementInfo getElementInfo()
	{
		return mElementInfo;
	}
	
	ElementSupport getElementSupport()
	{
		return mElement;
	}
	
	ElementExecutionState getElementState()
	{
		return getRequestState().getElementState();
	}
	
	Response getResponse()
	{
		return mResponse;
	}
	
	void setResponse(Response response)
	{
		mResponse = response;
	}

	void print(Template template)
	throws TemplateException, EngineException
	{
		List<String> set_values = new EngineTemplateProcessor(this, template).processTemplate();
		
		// set the content type
		if (!mResponse.isContentTypeSet())
		{
			String content_type = template.getDefaultContentType();
			if (null == content_type)
			{
				content_type = RifeConfig.Engine.getDefaultContentType();
			}
			
			mResponse.setContentType(content_type);
		}
		
		// print the element contents with the auto-generated values
		mResponse.print(template);

		// clean up the values that were set
		template.removeValues(set_values);
	}

	void triggerChild(String childTriggerName, String[] childTriggerValues)
	throws EngineException
	{
		mElement.enableRequestAccess(false);
		try
		{
			if (mElement.childTriggered(childTriggerName, childTriggerValues))
			{
				throw new ChildTriggeredException(childTriggerName, childTriggerValues);
			}
		}
		finally
		{
			mElement.enableRequestAccess(true);
		}
	}
	
	boolean hasSubmission()
	{
		return null != getSubmission();
	}
	
	boolean hasSubmission(String name)
	{
		String submission = getSubmission();
		return submission != null &&
			   submission.equals(name);

	}
	
	String getSubmission()
	{
		if (null == mSubmission)
		{
			return null;
		}
		
		return mSubmission;
	}
	
	private void validateParameter(String parameterName)
	throws EngineException
	{
		assert parameterName != null;
		assert parameterName.length() > 0;
		
		boolean found = false;
		for (Submission submission : mElementInfo.getSubmissions())
		{
			if (submission.containsParameter(parameterName))
			{
				found = true;
				break;
			}
		}
		
		if (!found)
		{
			throw new ParameterUnknownException(mElementInfo.getDeclarationName(), parameterName);
		}
	}
	
	private void validateFile(String fileName)
	throws EngineException
	{
		assert fileName != null;
		assert fileName.length() > 0;
		
		boolean found = false;
		for (Submission submission : mElementInfo.getSubmissions())
		{
			if (submission.containsFile(fileName))
			{
				found = true;
				break;
			}
		}
		
		if (!found)
		{
			throw new FileUnknownException(mElementInfo.getDeclarationName(), fileName);
		}
	}
	
	boolean isInputEmpty(String name)
	throws EngineException
	{
		String input = getInput(name);

		return null == input ||
			   input.trim().equals("");
	}
	
	String getInput(String name)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;
		
		mElementInfo.validateInputName(name);
		
		String input = getElementState().getInput(name);
		
		if (null == input &&
			mElementInfo.hasInputDefaults())
		{
			String[] default_values = mElementInfo.getInputDefaultValues(name);
			if (default_values != null)
			{
				input = default_values[0];
			}
		}

		if (null == input &&
			mElementInfo.hasGlobalVarDefaults())
		{
			String[] default_values = mElementInfo.getGlobalVarDefaultValues(name);
			if (default_values != null)
			{
				input = default_values[0];
			}
		}

		return input;
	}
	
	String[] getInputValues(String name)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;
		
		mElementInfo.validateInputName(name);
		
		String[] input_values = getElementState().getInputValues(name);
		
		if (null == input_values &&
			mElementInfo.hasInputDefaults())
		{
			input_values = mElementInfo.getInputDefaultValues(name);
		}
		
		if (null == input_values &&
			mElementInfo.hasGlobalVarDefaults())
		{
			input_values = mElementInfo.getGlobalVarDefaultValues(name);
		}
		
		return input_values;
	}
	
	boolean hasInputValue(String name)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;
		
		mElementInfo.validateInputName(name);
		
		return getElementState().hasInputValue(name) || mElementInfo.hasInputDefaultValues(name) || mElementInfo.hasGlobalVarDefaultValues(name);
	}

	OutcookieValues getOutcookies() {
		return mOutcookies;
	}
	
	OutputValues getOutputs()
	{
		return mOutputs;
	}
	
	void setOutput(String name, String value)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;
		assert value != null;
		
		mElementInfo.validateOutputName(name);
		
		// create a string array
		String[] value_array = new String[]{value};

		// store the value
		setOutputValues(name, value_array);
		
		if (getElementState().inInheritanceStructure() &&
			mElementInfo.containsChildTrigger(name))
		{
			triggerChild(name, value_array);
		}
	}
	
	void setOutput(String name, String[] values)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;
		assert values != null;
		assert values.length > 0;
		
		mElementInfo.validateOutputName(name);
		
		setOutputValues(name, values);
		
		if (getElementState().inInheritanceStructure() &&
			mElementInfo.containsChildTrigger(name))
		{
			triggerChild(name, values);
		}
	}
	
	void setOutput(String name, Object value, ConstrainedProperty constrainedProperty)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;
		assert value != null;
		
		String[] value_array = getOutputObjectValues(name, value, constrainedProperty);
		
		// store the value
		setOutputValues(name, value_array);
		
		if (getElementState().inInheritanceStructure() &&
			mElementInfo.containsChildTrigger(name))
		{
			triggerChild(name, value_array);
		}
	}

	private String[] getOutputObjectValues(String name, Object value, ConstrainedProperty constrainedProperty)
	throws EngineException
	{
		mElementInfo.validateOutputName(name);
		
		// create a string array
		String[] value_array;
		
		// convert the value to a string representation
		Class	value_type = value.getClass();
		if (value_type.isArray() ||
			!(value instanceof Serializable) ||
			ClassUtils.isBasic(value_type))
		{
			value_array = ArrayUtils.createStringArray(value, constrainedProperty);
		}
		else
		{
			try
			{
				value_array = new String[]{SerializationUtils.serializeToString((Serializable)value)};
			}
			catch (SerializationUtilsErrorException e)
			{
				throw new UnserializableOutputValueException(mElementInfo.getDeclarationName(), name, value, e);
			}
		}

		return value_array;
	}

	void addOutputValue(String name, String value)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;
		assert value != null;
		
		mElementInfo.validateOutputName(name);
		
		String[] value_array = null;
		
		String[] existing_values = mOutputs.get(name);
		if (existing_values != null)
		{
			value_array = ArrayUtils.join(existing_values, value);
		}
		else
		{
			value_array = new String[]{value};
		}
		
		setOutputValues(name, value_array);

		if (getElementState().inInheritanceStructure() &&
			mElementInfo.containsChildTrigger(name))
		{
			triggerChild(name, value_array);
		}
	}
	
	void addOutputValues(String name, String[] values)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;
		assert values != null;
		assert values.length > 0;
		
		mElementInfo.validateOutputName(name);
		
		String[] value_array = null;

		String[] existing_values = mOutputs.get(name);
		if (existing_values != null)
		{
			value_array = ArrayUtils.join(existing_values, values);
		}
		else
		{
			value_array = values;
		}
		
		setOutputValues(name, value_array);
		
		if (getElementState().inInheritanceStructure() &&
			mElementInfo.containsChildTrigger(name))
		{
			triggerChild(name, value_array);
		}
	}
	
	void addOutputValue(String name, Object value)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;
		assert value != null;
		
		mElementInfo.validateOutputName(name);

		// convert the value to a string representation
		String	value_text = null;
		Class	value_type = value.getClass();
		if (!(value instanceof Serializable) ||
			ClassUtils.isBasic(value_type))
		{
			value_text = String.valueOf(value);
		}
		else
		{
			try
			{
				value_text = SerializationUtils.serializeToString((Serializable)value);
			}
			catch (SerializationUtilsErrorException e)
			{
				throw new UnserializableOutputValueException(mElementInfo.getDeclarationName(), name, value, e);
			}
		}
		
		addOutputValue(name, value_text);
	}
	
	void clearOutput(String name)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;
		
		mElementInfo.validateOutputName(name);
		
		clearOutputValue(name);
	}

	void clearNamedOutputBean(String name)
	throws EngineException
	{
		assert name != null;

		mElementInfo.validateOutbeanName(name);

		BeanDeclaration	output_bean = mElementInfo.getNamedOutbeanInfo(name);
		if (null == output_bean)
		{
			output_bean = mElementInfo.getNamedGlobalBeanInfo(name);
		}

		Class	output_bean_class = null;
		try
		{
			output_bean_class = Class.forName(output_bean.getClassname());
		}
		catch (ClassNotFoundException e)
		{
			throw new NamedOutbeanClassnameErrorException(mElementInfo.getDeclarationName(),name, output_bean.getClassname());
		}
		
		clearOutputBean(output_bean_class, output_bean.getPrefix());
	}

	void clearOutputBean(Class beanClass, String prefix)
	throws EngineException
	{
		if (null == beanClass)		throw new IllegalArgumentException("beanClass can't be null.");
		
		try
		{
			// handle outputs
			Collection<String>	output_names = mElementInfo.getOutputNames();
			String[]			output_names_array = new String[output_names.size()];
			output_names.toArray(output_names_array);
			
			// handle globals
			Collection<String>	globalvar_names = mElementInfo.getGlobalVarNames();
			String[]			globalvar_names_array = new String[globalvar_names.size()];
			globalvar_names.toArray(globalvar_names_array);
			
			// merge the both arrays
			String[]	merged_names_array = ArrayUtils.join(output_names_array, globalvar_names_array);
			
			// process all the possible output names
			Set<String>	property_names = BeanUtils.getPropertyNames(beanClass, merged_names_array, null, prefix);
			for (String property_name : property_names)
			{
				clearOutput(property_name);
			}
		}
		catch (BeanUtilsException e)
		{
			throw new BeanClassNamesErrorException(beanClass, e);
		}
	}
	
	String[] getOutput(String name)
	throws EngineException
	{
		mElementInfo.validateOutputName(name);
		
		return mOutputs.aggregateValues().get(name);
	}

	Set<Map.Entry<String, String>> getIncookieEntries()
	{
		return collectCookieValues().entrySet();
	}
	
	boolean hasCookie(String name)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;
		
		mElementInfo.validateIncookieName(name);
		
		return mRequestState.hasCookie(name) || mElementInfo.hasIncookieDefaultValue(name) || mElementInfo.hasGlobalCookieDefaultValue(name);
	}
	
	Cookie getCookie(String name)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;
		
		mElementInfo.validateIncookieName(name);
		
		Cookie cookie = mRequestState.getCookie(name);
		if (null == cookie )
		{
			if (mElementInfo.hasIncookieDefaultValue(name))
			{
				cookie = new Cookie(name, mElementInfo.getIncookieDefaultValue(name));
			}
		
			if (mElementInfo.hasGlobalCookieDefaultValue(name))
			{
				cookie = new Cookie(name, mElementInfo.getGlobalCookieDefaultValue(name));
			}
		}
		
		return cookie;
	}
	
	String getCookieValue(String name)
	throws EngineException
	{
		Cookie	cookie = getCookie(name);
		String	value = null;
		if (cookie != null)
		{
			value = cookie.getValue();
		}
		
		return value;
	}
	
	void setCookie(Cookie cookie)
	throws EngineException
	{
		assert cookie != null;
		assert cookie.getName() != null;
		
		mElementInfo.validateOutcookieName(cookie.getName());
		
		mOutcookies.put(cookie.getName(), cookie.getValue());
		setCookieRaw(cookie);
		
		if (getElementState().inInheritanceStructure() &&
			mElementInfo.containsChildTrigger(cookie.getName()))
		{
			triggerChild(cookie.getName(), new String[] {cookie.getValue()});
		}
	}

	void setCookieRaw(Cookie cookie)
	{
		mResponse.addCookie(cookie);
		mRequestState.setStateCookie(cookie);
		fireOutcookieSet(cookie);
	}
	
	HashMap<String, String> collectCookieValues()
	throws EngineException
	{
		HashMap<String, String> result = new HashMap<String, String>();
		
		for (Map.Entry<String, String> entry : mElementInfo.getDefaultIncookies().entrySet())
		{
			result.put(entry.getKey(), entry.getValue());
		}
		
		for (Map.Entry<String, String> entry : mElementInfo.getDefaultGlobalCookies().entrySet())
		{
			result.put(entry.getKey(), entry.getValue());
		}
		
		for (Map.Entry<String, Cookie> entry : mRequestState.getCookies().entrySet())
		{
			result.put(entry.getKey(), entry.getValue().getValue());
		}
		
		return result;
	}
	
	boolean hasParameterValue(String name)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;
		
		validateParameter(name);

		String submission = getSubmission();
		if (null == submission)
		{
			return false;
		}

		return getElementState().hasRequestParameterValue(name) || mElementInfo.hasParameterDefaultValues(submission, name);
	}
		
	boolean isParameterEmpty(String name)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;
		
		String parameter = getParameter(name);
		if (null == parameter ||
			parameter.trim().equals(""))
		{
			return true;
		}
		return false;
	}
	
	String getParameter(String name)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;
		
		validateParameter(name);

		String submission = getSubmission();
		if (null == submission)
		{
			return null;
		}

		String parameter = getElementState().getRequestParameter(name);
		
		if (null == parameter &&
			mElementInfo.hasParameterDefaults(submission))
		{
			String[] default_values = mElementInfo.getParameterDefaultValues(submission, name);
			if (default_values != null)
			{
				return default_values[0];
			}
		}

		return parameter;
	}
	
	ArrayList<String> getParameterNames(String regexp)
	throws EngineException
	{
		Pattern	pattern = null;
		if (regexp != null)
		{
			pattern = Pattern.compile("^"+regexp+"$");
		}
		
		ElementExecutionState element_state = getElementState();

		ArrayList<String>	result = new ArrayList<String>();
		String				submission_name = getSubmission();
		if (null == submission_name)
		{
			return result;
		}

		Submission	submission = mElementInfo.getSubmission(submission_name);
		if (null == submission)
		{
			return result;
		}
		
		Collection<String>	parameter_names = null;
		
		// add all default parameters that match
		parameter_names = submission.getParameterDefaultNames();
		for (String parameter_name : parameter_names)
		{
			if (null == pattern ||
				pattern.matcher(parameter_name).matches())
			{
				result.add(parameter_name);
			}
		}
		
		// go over the possible parameters and check if they have values in the request
		parameter_names = submission.getParameterNames();
		for (String parameter_name : parameter_names)
		{
			if (element_state.hasRequestParameterValue(parameter_name) &&
				!result.contains(parameter_name) &&
				(null == pattern || pattern.matcher(parameter_name).matches()))
			{
				result.add(parameter_name);
			}
		}
		
		// go over all the parameter regexps and find those that match with the parameters in the request
		Matcher	matcher = null;
		for (Pattern parameter_regexp : submission.getParameterRegexps())
		{
			for (String parameter_name : element_state.getRequestParameterNames())
			{
				matcher = parameter_regexp.matcher(parameter_name);
				if (matcher.matches())
				{
					if (element_state.hasRequestParameterValue(parameter_name) &&
						!result.contains(parameter_name) &&
						(null == pattern || pattern.matcher(parameter_name).matches()))
					{
						result.add(parameter_name);
					}
				}
			}
			
		}
		
		return result;
	}
	
	String[] getParameterValues(String name)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;
		
		validateParameter(name);

		String submission = getSubmission();
		if (null == submission)
		{
			return null;
		}
		
		String[] parameter_values = getElementState().getRequestParameterValues(name);
		if (null == parameter_values &&
			mElementInfo.hasParameterDefaults(submission))
		{
			return mElementInfo.getParameterDefaultValues(submission, name);
		}
		
		return parameter_values;
	}
	
	ArrayList<String> getUploadedFileNames(String regexp)
	throws EngineException
	{
		Pattern	pattern = null;
		if (regexp != null)
		{
			pattern = Pattern.compile("^"+regexp+"$");
		}
		
		ArrayList<String>	result = new ArrayList<String>();
		String				submission_name = getSubmission();
		if (null == submission_name)
		{
			return result;
		}
		
		Submission	submission = mElementInfo.getSubmission(submission_name);
		if (null == submission)
		{
			return result;
		}
		
		Collection<String>	file_names = null;
		
		// go over the possible files and check if they have values in the request
		file_names = submission.getFileNames();
		for (String file_name : file_names)
		{
			if (mRequestState.hasUploadedFile(file_name) &&
				!result.contains(file_name) &&
				(null == pattern || pattern.matcher(file_name).matches()))
			{
				result.add(file_name);
			}
		}
		
		// go over all the file regexps and find those that match with the files in the request
		Matcher	matcher = null;
		for (Pattern file_regexp : submission.getFileRegexps())
		{
			for (String file_name : mRequestState.getUploadedFileNames())
			{
				matcher = file_regexp.matcher(file_name);
				if (matcher.matches())
				{
					if (mRequestState.hasUploadedFile(file_name) &&
						!result.contains(file_name) &&
						(null == pattern || pattern.matcher(file_name).matches()))
					{
						result.add(file_name);
					}
				}
			}
			
		}
		
		return result;
	}
	
	ArrayList<String> getUploadedFileNames()
	throws EngineException
	{
		ArrayList<String>	result = new ArrayList<String>();
		String				submission_name = getSubmission();
		if (null == submission_name)
		{
			return result;
		}

		Submission	submission = mElementInfo.getSubmission(submission_name);
		if (null == submission)
		{
			return result;
		}
		
		Collection<String> file_names = submission.getFileNames();
		for (String file_name : file_names)
		{
			if (mRequestState.hasUploadedFile(file_name))
			{
				result.add(file_name);
			}
		}
		
		return result;
	}
	
	boolean hasUploadedFile(String name)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;
		
		validateFile(name);

		String submission = getSubmission();
		if (null == submission)
		{
			return false;
		}
		
		return mRequestState.hasUploadedFile(name);
	}
		
	boolean isFileEmpty(String name)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;
		
		UploadedFile file = getUploadedFile(name);
		return null == file ||
			   null == file.getFile() ||
			   0 == file.getFile().length();
	}
	
	UploadedFile getUploadedFile(String name)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;
		
		validateFile(name);

		String submission = getSubmission();
		if (null == submission)
		{
			return null;
		}
 
		return mRequestState.getUploadedFile(name);
	}
	
	UploadedFile[] getUploadedFiles(String name)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;
		
		validateFile(name);

		String submission = getSubmission();
		if (null == submission)
		{
			return null;
		}

		return mRequestState.getUploadedFiles(name);
	}
	
	void setOutputValues(String name, String[] values)
	{
		assert name != null;
		assert name.length() > 0;
		assert values != null;
		
		mOutputs.put(name, values);
		fireOutputValueSet(name, values);
	}
	
	void setAutomatedOutputValues(String name, String[] values)
	{
		mOutputs.putFallback(name, values);
		
		fireAutomatedOutputValueSet(name, values);
	}
	
	void clearOutputValue(String name)
	{
		assert name != null;
		assert name.length() > 0;
		
		// clearing the output value instead of removing it, this prevents
		// later automated processing to fill it in again
		mOutputs.put(name, null);
		fireOutputValueCleared(name);
	}
	
	void clearAutomatedOutputValue(String name)
	{
		// clearing the output value instead of removing it, this prevents
		// later automated processing to fill it in again
		mOutputs.putFallback(name, null);
		fireAutomatedOutputValueCleared(name);
	}
	
	void addOutputListener(OutputListener listener)
	{
		if (null == mOutputListeners)
		{
			mOutputListeners = new ArrayList<OutputListener>();
		}
		
		mOutputListeners.add(listener);
	}
	
	void removeOutputListener(OutputListener listener)
	{
		mOutputListeners.remove(listener);
	}

	void fireOutputValueSet(String name, String[] values)
	{
		if (null == mOutputListeners)
		{
			return;
		}
		
		for (OutputListener listener : mOutputListeners)
		{
			listener.outputValueSet(name, values);
		}
	}

	void fireOutputValueCleared(String name)
	{
		if (null == mOutputListeners)
		{
			return;
		}
		
		for (OutputListener listener : mOutputListeners)
		{
			listener.outputValueCleared(name);
		}
	}

	void fireAutomatedOutputValueSet(String name, String[] values)
	{
		if (null == mOutputListeners)
		{
			return;
		}
		
		for (OutputListener listener : mOutputListeners)
		{
			listener.automatedOutputValueSet(name, values);
		}
	}

	void fireAutomatedOutputValueCleared(String name)
	{
		if (null == mOutputListeners)
		{
			return;
		}
		
		for (OutputListener listener : mOutputListeners)
		{
			listener.automatedOutputValueCleared(name);
		}
	}
	
	void addOutcookieListener(OutcookieListener listener)
	{
		if (null == mOutcookieListeners)
		{
			mOutcookieListeners = new ArrayList<OutcookieListener>();
		}
		
		mOutcookieListeners.add(listener);
	}
	
	void removeOutcookieListener(OutcookieListener listener)
	{
		mOutcookieListeners.remove(listener);
	}

	void fireOutcookieSet(Cookie cookie)
	{
		if (null == mOutcookieListeners)
		{
			return;
		}
		
		for (OutcookieListener listener : mOutcookieListeners)
		{
			listener.outcookieSet(cookie);
		}
	}

	boolean duringStepBack()
	{
		return mSteppedBack;
	}

	void exit(String name)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;

		mElementInfo.validateExitName(name);

		throw new ExitTriggeredException(name);
	}

	/**
	 * Retrieves the inputs values that a child receives when the parent
	 * delegates the control flow to it. This is based on the current output
	 * values of the parent element, combined with the global variables
	 */
	private Map<String, String[]> collectChildInputValues(ElementInfo targetElement)
	{
		ElementExecutionState element_state = getElementState();

		Map<String, String[]> inputs = null;
		
		inputs = element_state.getTriggerInputs();
		
		if (null == inputs)
		{
			inputs = element_state.getRequestParameters();
		}

		if (mElementInfo.hasGlobalVars() &&
			targetElement.hasGlobalVars())
		{
			String[]	input_values_array = null;
			
			for (Map.Entry<String, String[]> output_entry : mOutputs.aggregateValues().entrySet())
			{
				// create automatic data link for global variables
				if (mElementInfo.containsGlobalVar(output_entry.getKey()) &&
					targetElement.containsGlobalVar(output_entry.getKey()))
				{
					input_values_array = output_entry.getValue();
					inputs.put(output_entry.getKey(), input_values_array);
				}
			}
		}
		
		return inputs;
	}
	
	/**
	 * Retrieves the inputs values that an exit receives when it is
	 * followed. This is based on the current output values of the
	 * active element.
	 */
	private Map<String, String[]> collectExitInputValues(FlowLink flowLink, Set<Map.Entry<String, String[]>> outputEntries, ElementInfo target, boolean snapback)
	throws EngineException
	{
		Map<String, String[]> inputs = new LinkedHashMap<String, String[]>();
		
		// preserve the embedding element's inputs and global vars
		if (target.getId().equals(mElementInfo.getId()))
		{
			ElementContext context = this;
			while (context.mRequestState.isEmbedded())
			{
				context = context.mRequestState.getEmbeddingContext().getElementContext();
				
				ElementExecutionState embedded_element_state = context.getElementState();
				
				for (String input_name : context.mElementInfo.getInputNames())
				{
					if (embedded_element_state.hasInputValue(input_name))
					{
						inputs.put(input_name, embedded_element_state.getInputValues(input_name));
					}
				}
				for (String globalvar_name : context.mElementInfo.getGlobalVarNames())
				{
					if (embedded_element_state.hasInputValue(globalvar_name))
					{
						inputs.put(globalvar_name, embedded_element_state.getInputValues(globalvar_name));
					}
				}
			}
		}

		if (mElementInfo.hasSnapbackDataLinks() ||
			mElementInfo.hasDataLink(target) ||
			(mElementInfo.hasGlobalVars() &&
			 target.hasGlobalVars()))
		{
			Map<String, String[]>	dest_input_candidates = new LinkedHashMap<String, String[]>();
			
			// if the exit is reflective, preserve the input values that have
			// identically named output values
			if (target == mElementInfo)
			{
				Collection<String>	input_names = mElementInfo.getInputNames();
				Collection<String>	output_names = mElementInfo.getOutputNames();
				Iterator<String>	input_names_it = input_names.iterator();
				String				input_name = null;
				String[]			input_values = null;
				while (input_names_it.hasNext())
				{
					input_name = input_names_it.next();
					
					if (output_names.contains(input_name))
					{
						input_values = getInputValues(input_name);
						if (input_values != null)
						{
							dest_input_candidates.put(input_name, input_values);
						}
					}
				}
			}
			
			// add the global default values
			GlobalVar	globalvar_data = null;
			
			for (Map.Entry<String, GlobalVar> globalvar_entry : mElementInfo.getGlobalVarEntries())
			{
				globalvar_data = globalvar_entry.getValue();
				if (globalvar_data != null &&
					globalvar_data.getDefaultValues() != null)
				{
					dest_input_candidates.put(globalvar_entry.getKey(), globalvar_data.getDefaultValues());
				}
			}

			// add the output values
			String[]	output_values_array = null;
			
			for (Map.Entry<String, String[]> output_entry : outputEntries)
			{
				output_values_array = output_entry.getValue();
				dest_input_candidates.put(output_entry.getKey(), output_values_array);
			}
			
			// process the exit input value candidates
			Collection<String>	dest_input_names = null;
			
			GlobalVar	globalvar_source = null;
			GlobalVar	globalvar_target = null;
			
			for (String candidate : dest_input_candidates.keySet())
			{
				if (!mElementInfo.containsDepartureVar(candidate))
				{
					// create automatic data link for global variables
					if (mElementInfo.containsGlobalVar(candidate) &&
						target.containsGlobalVar(candidate))
					{
						globalvar_source = mElementInfo.getGlobalVarInfo(candidate);
						globalvar_target = target.getGlobalVarInfo(candidate);
						
						// only create the link if the global vars are in the
						// same group scope
						if (globalvar_source.getGroupId() == globalvar_target.getGroupId())
						{
							inputs.put(candidate, dest_input_candidates.get(candidate));
						}
					}
					
					// translate outputs to inputs through a possible data link
					dest_input_names = mElementInfo.getDataLinkInputs(candidate, target, snapback, flowLink);
					if (dest_input_names != null)
					{
						for (String dest_input_name : dest_input_names)
						{
							inputs.put(dest_input_name, dest_input_candidates.get(candidate));
						}
					}
				}
			}
		}
		
		return inputs;
	}

	private static Map<String, String[]> overrideOutputValues(Map<String, String[]> outputValueMap, String[] outputValues)
	throws EngineException
	{
		if (null == outputValues ||
			0 == outputValues.length)
		{
			return outputValueMap;
		}
		
		Map<String, String[]>	outputs = new LinkedHashMap<String, String[]>(outputValueMap);
		// store the output overrides
		for (int i = 0; i < outputValues.length; i += 2)
		{
			outputs.put(outputValues[i], new String[]{outputValues[i+1]});
		}
		
		return outputs;
	}
	
	private Map<String, String[]> collectExitInputValues(FlowLink flowLink, ElementInfo target, boolean snapback, Map<String, String[]> outputValueMap, String[] outputValues)
	throws EngineException
	{
		// override current output values
		Map<String, String[]>	overridden_outputs = overrideOutputValues(outputValueMap, outputValues);
		
		// construct the exit input parameters
		return collectExitInputValues(flowLink, overridden_outputs.entrySet(), target, snapback);
	}
	
	FlowState collectExitParameters(FlowLink flowlink, Map<String, String[]> outputValueMap, String[] outputValues)
	{
		FlowState state = new FlowState();
		
		ElementExecutionState element_state = getElementState();
		
		// construct the exit input parameters
		Map<String, String[]> inputs = collectExitInputValues(flowlink, flowlink.getTarget(), flowlink.isSnapback(), outputValueMap, outputValues);

		// Create or preserve the request parameters that were initially send to the child element
		// this permits completely seperate processing of any other parameters.
		// Maintain a stack of successful parent elements.
		if (element_state.inInheritanceStructure() &&
			!flowlink.cancelInheritance())
		{
			state.putParameter(ReservedParameters.CHILDREQUEST, getEncodedChildRequest());
			
			// preserve the trigger list
			List<TriggerContext>	new_trigger_context = element_state.cloneTriggerList();
			new_trigger_context.add(TriggerContext.generateExitTrigger(mElementInfo, flowlink.getExitName(), inputs));
			
			state.putParameter(ReservedParameters.TRIGGERLIST, TriggerListEncoder.encode(new_trigger_context));
		}
		else
		{
			// construct the exit input parameters
			state.setParameters(inputs);
		}

		if (!flowlink.cancelContinuations())
		{
			// Preserve the continuation id if a continuation context is active
			// and the exit goes back to the same element
			if (mElementInfo == flowlink.getTarget())
			{
				String continuation_id = ContinuationContext.getActiveContextId();
				if (continuation_id != null)
				{
					state.putParameter(ReservedParameters.CONTID, continuation_id);
				}
			}
		}
		
		return state;
	}

	private String getContextId()
	{
		if (mContextId != null)
		{
			return mContextId;
		}
		
		synchronized (mElement)
		{
			mContextId = mRequestState.buildContextId();			
		}
		
		return mContextId;
	}
	
	FlowState collectSubmissionParameters(String name, String[] parameterValues, Set<Map.Entry<String, String[]>> outputEntries)
	{
		FlowState state = new FlowState();
		
		state.putParameter(ReservedParameters.SUBMISSION, name);
		
		// add the submission parameters
		if (parameterValues != null)
		{
			String	parameter_name = null;
			String	parameter_value = null;
			for (int i = 0; i < parameterValues.length; i += 2)
			{
				parameter_name = parameterValues[i];
				parameter_value = parameterValues[i+1];
				state.putParameter(parameter_name,  parameter_value);
			}

			validateParameter(parameter_name);
		}
		
		// Create the submission context parameter
		Submission submission = mElementInfo.getSubmission(name);
		if (null == submission ||
			Scope.LOCAL == submission.getScope())
		{
			String submission_context = getContextId();
			String target = getElementInfo().getId();
			if (!submission_context.equals(target))
			{
				StringBuilder submission_context_buffer = new StringBuilder(submission_context);
				submission_context_buffer.append("^");
				submission_context_buffer.append(target);
				submission_context = submission_context_buffer.toString();
			}
			try
			{
				state.putParameter(ReservedParameters.SUBMISSIONCONTEXT, Base64.encodeToString(submission_context.getBytes("UTF-8"), false));
			}
			catch (UnsupportedEncodingException e)
			{
				// should never happen
			}
		}

		// Preserve the continuation ID if a continuation context is active
		if (null == submission ||
			!submission.getCancelContinuations())
		{
			String continuation_id = ContinuationContext.getActiveContextId();
			if (continuation_id != null)
			{
				state.putParameter(ReservedParameters.CONTID, continuation_id);
			}
		}
			
		ElementExecutionState element_state = getElementState();
		
		// create or preserve the request parameters that were initially send to the child element
		// this permits completely seperate processing of any other parameters
		if (element_state.inInheritanceStructure())
		{
			// preserve the original child request
			state.putParameter(ReservedParameters.CHILDREQUEST, getEncodedChildRequest());
			
			// preserve the trigger list
			state.putParameter(ReservedParameters.TRIGGERLIST, element_state.encodeTriggerList());
		}
			
		// preserve the embedding element's inputs and global vars
		ElementContext context = this;
		while (context.mRequestState.isEmbedded())
		{
			context = context.mRequestState.getEmbeddingContext().getElementContext();
			ElementExecutionState embedded_element_state = context.getElementState();
			
			for (String input_name : context.mElementInfo.getInputNames())
			{
				if (embedded_element_state.hasInputValue(input_name))
				{
					state.putSubmissionGlobalInput(input_name, embedded_element_state.getInputValues(input_name, false));
				}
			}
			
			for (String globalvar_name : context.mElementInfo.getGlobalVarNames())
			{
				if (embedded_element_state.hasInputValue(globalvar_name))
				{
					state.putSubmissionGlobalInput(globalvar_name, embedded_element_state.getInputValues(globalvar_name, false));
				}
			}
		}

		// preserve the global variables
		for (String globalvar_name : mElementInfo.getGlobalVarNames())
		{
			if (element_state.hasInputValue(globalvar_name))
			{
				state.putSubmissionGlobalInput(globalvar_name, element_state.getInputValues(globalvar_name, false));
			}
		}
		
		// activate reflective datalinks for global vars
		if (outputEntries != null)
		{
			for (Map.Entry<String, String[]> output : outputEntries)
			{
				// global vars
				if (mElementInfo.containsGlobalVar(output.getKey()))
				{
					state.putSubmissionGlobalInput(output.getKey(), output.getValue());
				}
			}
		}

		// add this element's inputs
		Map<String, String[]>	element_inputs = null;
		
		// preserve the input values
		for (String input_name : mElementInfo.getInputNames())
		{
			if (element_state.hasInputValue(input_name))
			{
				if (null == element_inputs)
				{
					element_inputs = new LinkedHashMap<String, String[]>();
				}
				
				element_inputs.put(input_name, element_state.getInputValues(input_name));
			}
		}
		
		// merge this element's inputs with its preserved inputs
		Map<String, String[]> preserved_inputs = collectPreservedInputs(outputEntries);
		
		String context_id = getContextId();
		if (preserved_inputs != null &&
			preserved_inputs.size() > 0)
		{
			if (null == element_inputs)
			{
				element_inputs = new LinkedHashMap<String, String[]>();
			}
			element_inputs.putAll(preserved_inputs);
		}
		state.setSubmissionElementInputs(element_inputs);
		
		// remember this element's context identifier for when the context inputs are extracted
		state.setSubmissionContextId(context_id);
		
		return state;
	}
	
	Map<String, String[]> collectPreservedInputs(Set<Map.Entry<String, String[]>> outputEntries)
	{
		Map<String, String[]> element_inputs = null;
		
		// activate reflective datalinks for which outputs point to the inputs
		if (outputEntries != null)
		{
			for (Map.Entry<String, String[]> output : outputEntries)
			{
				// reflective datalinks
				Collection<String> datalink_inputs = mElementInfo.getDataLinkInputs(output.getKey(), mElementInfo, false, null);
				if (datalink_inputs != null)
				{
					for (String input_name : datalink_inputs)
					{
						if (null == element_inputs)
						{
							element_inputs = new LinkedHashMap<String, String[]>();
						}
						
						element_inputs.put(input_name, output.getValue());
					}
				}
			}
		}
		
		return element_inputs;
	}

	private <BeanType> BeanType retrieveSubmissionBeanNoValidation(Submission submission, Class<BeanType> beanClass, String prefix)
	throws EngineException
	{
		assert submission != null;
		assert beanClass != null;

		ElementExecutionState element_state = getElementState();
		
		BeanType bean_instance = getNewBeanInstance(beanClass);

		try
		{
			HashMap<String, PropertyDescriptor>	bean_properties = BeanUtils.getUppercasedBeanProperties(beanClass);
			
			String[] parameter_values = null;
			
			for (String parameter_name : submission.getParameterNames())
			{
				if (element_state.hasRequestParameterValue(parameter_name))
				{
					parameter_values = element_state.getRequestParameterValues(parameter_name);
					if (parameter_values != null &&
						parameter_values.length > 0)
					{
						BeanUtils.setUppercasedBeanProperty(parameter_name, parameter_values, prefix, bean_properties, bean_instance, null);
					}
				}
			}
			
			for (Pattern parameter_regexp : submission.getParameterRegexps())
			{
				for (String parameter_name : getParameterNames(parameter_regexp.pattern()))
				{
					if (element_state.hasRequestParameterValue(parameter_name))
					{
						parameter_values = element_state.getRequestParameterValues(parameter_name);
						if (parameter_values != null &&
							parameter_values.length > 0)
						{
							BeanUtils.setUppercasedBeanProperty(parameter_name, parameter_values, prefix, bean_properties, bean_instance, null);
						}
					}
				}
			}
			
			for (String uploadedfile_name : getUploadedFileNames())
			{
				UploadedFile file = getUploadedFile(uploadedfile_name);
				BeanUtils.setUppercasedBeanProperty(uploadedfile_name, file, prefix, bean_properties, bean_instance);
			}
		}
		catch (BeanUtilsException e)
		{
			throw new EngineException(e);
		}
		
		return bean_instance;
	}
	
	<BeanType> BeanType getNamedSubmissionBean(String submissionName, String beanName)
	throws EngineException
	{
		assert submissionName != null;
		assert submissionName.length() > 0;
		assert beanName != null;
		assert beanName.length() > 0;

		mElementInfo.validateSubmissionName(submissionName);
		
		if (!hasSubmission(submissionName))
		{
			return null;
		}
		
		BeanDeclaration	bean = mElementInfo.getSubmission(submissionName).getNamedBean(beanName);
		Class<BeanType>	bean_class = null;
		
		try
		{
			bean_class = (Class<BeanType>)Class.forName(bean.getClassname());
		}
		catch (ClassNotFoundException e)
		{
			throw new NamedSubmissionBeanClassnameErrorException(mElementInfo.getDeclarationName(), submissionName, beanName, bean.getClassname(), e);
		}
		
		return retrieveSubmissionBeanNoValidation(mElementInfo.getSubmission(submissionName), bean_class, bean.getPrefix());
	}
	
	<BeanType> BeanType getSubmissionBean(String submissionName, Class<BeanType> beanClass, String prefix)
	throws EngineException
	{
		assert submissionName != null;
		assert submissionName.length() > 0;
		assert beanClass != null;

		mElementInfo.validateSubmissionName(submissionName);
		
		if (!hasSubmission(submissionName))
		{
			return null;
		}
		
		return retrieveSubmissionBeanNoValidation(mElementInfo.getSubmission(submissionName), beanClass, prefix);
	}
	
	void fillSubmissionBean(String submissionName, Object bean, String prefix)
	throws EngineException
	{
		assert submissionName != null;
		assert submissionName.length() > 0;
		
		if (null == bean)
		{
			return;
		}

		mElementInfo.validateSubmissionName(submissionName);
		
		if (!hasSubmission(submissionName))
		{
			return;
		}
		
		Submission submission = mElementInfo.getSubmission(submissionName);

		ElementExecutionState element_state = getElementState();

		try
		{
			HashMap<String, PropertyDescriptor>	bean_properties = BeanUtils.getUppercasedBeanProperties(bean.getClass());
			String[]							parameter_values = null;

			Object	empty_bean = null;

			// handle regular parameters
			for (String parameter_name : submission.getParameterNames())
			{
				parameter_values = element_state.getRequestParameterValues(parameter_name);
				if (null == empty_bean &&
					(null == parameter_values ||
					0 == parameter_values[0].length()))
				{
					try
					{
						empty_bean = bean.getClass().newInstance();
					}
					catch (InstantiationException e)
					{
						throw new EngineException("Unexpected error while invoking the default constructor of the bean with class '"+bean.getClass().getName()+"'.", e);
					}
					catch (IllegalAccessException e)
					{
						throw new EngineException("No permission to invoke the default constructor of the bean with class '"+bean.getClass().getName()+"'.", e);
					}
				}

				BeanUtils.setUppercasedBeanProperty(parameter_name, parameter_values, prefix, bean_properties, bean, empty_bean);
			}

			// handle regexp parameters
			for (Pattern parameter_regexp : submission.getParameterRegexps())
			{
				for (String parameter_name : getParameterNames(parameter_regexp.pattern()))
				{
					parameter_values = element_state.getRequestParameterValues(parameter_name);
					if (null == empty_bean &&
						(null == parameter_values ||
						0 == parameter_values[0].length()))
					{
						try
						{
							empty_bean = bean.getClass().newInstance();
						}
						catch (InstantiationException e)
						{
							throw new EngineException("Unexpected error while invoking the default constructor of the bean with class '"+bean.getClass().getName()+"'.", e);
						}
						catch (IllegalAccessException e)
						{
							throw new EngineException("No permission to invoke the default constructor of the bean with class '"+bean.getClass().getName()+"'.", e);
						}
					}

					BeanUtils.setUppercasedBeanProperty(parameter_name, parameter_values, prefix, bean_properties, bean, empty_bean);
				}
			}

			// automatically handle uploaded files
			for (String uploadedfile_name : getUploadedFileNames())
			{
				UploadedFile file = getUploadedFile(uploadedfile_name);
				BeanUtils.setUppercasedBeanProperty(uploadedfile_name, file, prefix, bean_properties, bean);
			}
		}
		catch (BeanUtilsException e)
		{
			throw new EngineException(e);
		}
	}
	
	<BeanType> BeanType getNamedInputBean(String name)
	throws EngineException
	{
		assert name != null;
		
		mElementInfo.validateInbeanName(name);

		BeanDeclaration	input_bean = mElementInfo.getNamedInbeanInfo(name);
		if (null == input_bean)
		{
			input_bean = mElementInfo.getNamedGlobalBeanInfo(name);
		}
		
		Class<BeanType>	input_bean_class = null;
		
		try
		{
			input_bean_class = (Class<BeanType>)Class.forName(input_bean.getClassname());
		}
		catch (ClassNotFoundException e)
		{
			throw new NamedInbeanClassnameErrorException(mElementInfo.getDeclarationName(),name, input_bean.getClassname());
		}
		
		return getInputBean(input_bean_class, input_bean.getPrefix());
	}
	
	<BeanType> BeanType getInputBean(Class<BeanType> beanClass, String prefix)
	throws EngineException
	{
		assert beanClass != null;

		ElementExecutionState element_state = getElementState();
		
		BeanType bean_instance = getNewBeanInstance(beanClass);

		try
		{
			HashMap<String, PropertyDescriptor>	bean_properties = BeanUtils.getUppercasedBeanProperties(beanClass);
			
			// merge the input and global variable names
			Collection<String>	input_names = mElementInfo.getInputNames();
			Collection<String>	globalvar_names = mElementInfo.getGlobalVarNames();
			ArrayList<String>	merged_names = new ArrayList<String>();
			merged_names.addAll(input_names);
			merged_names.addAll(globalvar_names);
			
			// process all the possible input names
			String[]	values = null;
			
			for (String name : merged_names)
			{
				if (element_state.hasInputValue(name))
				{
					values = element_state.getInputValues(name);
					if (values != null &&
						values.length > 0)
					{
						BeanUtils.setUppercasedBeanProperty(name, values, prefix, bean_properties, bean_instance, null);
					}
				}
			}
		}
		catch (BeanUtilsException e)
		{
			throw new EngineException(e);
		}
		
		return bean_instance;
	}
	
	void setNamedOutputBean(String name, Object bean)
	throws EngineException
	{
		assert name != null;

		mElementInfo.validateOutbeanName(name);

		BeanDeclaration	output_bean = mElementInfo.getNamedOutbeanInfo(name);
		if (null == output_bean)
		{
			output_bean = mElementInfo.getNamedGlobalBeanInfo(name);
		}
		
		setOutputBean(bean, output_bean.getPrefix());
	}

	void setOutputBean(Object bean, String prefix)
	throws EngineException
	{
		if (null == bean)		throw new IllegalArgumentException("bean can't be null.");
		
		Map<String, String[]> values = collectOutputBeanValues(bean, prefix, null);
		
		String name;
		String[] value;
		for (Map.Entry<String, String[]> entry : values.entrySet())
		{
			name = entry.getKey();
			value = entry.getValue();
			
			setOutputValues(name, value);
			
			if (getElementState().inInheritanceStructure() &&
				mElementInfo.containsChildTrigger(name))
			{
				triggerChild(name, value);
			}
		}
	}

	Map<String, String[]> collectOutputBeanValues(Object bean, String prefix, Collection<String> included)
	throws BeanInstanceValuesErrorException
	{
		if (null == bean)	return Collections.emptyMap();
		
		Map<String, String[]> values = new LinkedHashMap<String, String[]>();
		try
		{
			Constrained 		constrained = ConstrainedUtils.makeConstrainedInstance(bean);
			ConstrainedProperty	constrained_property = null;
			
			Set<String>	merged_names = new LinkedHashSet<String>();
			
			// handle outputs
			Collection<String>	output_names = mElementInfo.getOutputNames();
			if (null == included)
			{
				merged_names.addAll(output_names);
			}
			else
			{
				for (String name : output_names)
				{
					if (included.contains(name))
					{
						merged_names.add(name);
					}
				}
			}
			
			// handle globals
			Collection<String>	globalvar_names = mElementInfo.getGlobalVarNames();
			if (null == included)
			{
				merged_names.addAll(globalvar_names);
			}
			else
			{
				for (String name : globalvar_names)
				{
					if (included.contains(name))
					{
						merged_names.add(name);
					}
				}
			}
			
			// create the merged array
			String[]	merged_names_array = new String[merged_names.size()];
			merged_names.toArray(merged_names_array);
			
			// process all the possible output names
			Map<String, Object>	property_values = BeanUtils.getPropertyValues(bean, merged_names_array, null, prefix);
			Object				property_value = null;
			
			for (String property_name : property_values.keySet())
			{
				property_value = property_values.get(property_name);
				
				if (property_value != null)
				{
					// get the constrained property if that's appropriate
					if (constrained != null)
					{
						if (prefix != null)
						{
							constrained_property = constrained.getConstrainedProperty(property_name.substring(prefix.length()));
						}
						else
						{
							constrained_property = constrained.getConstrainedProperty(property_name);
						}
					}
					
					values.put(property_name, getOutputObjectValues(property_name, property_value, constrained_property));
				}
			}
		}
		catch (BeanUtilsException e)
		{
			throw new BeanInstanceValuesErrorException(bean, e);
		}
		return values;
	}

	private static <BeanType> BeanType getNewBeanInstance(Class<BeanType> beanClass)
	throws EngineException
	{
		BeanType bean_instance;

		try
		{
			bean_instance = beanClass.newInstance();
		}
		catch (InstantiationException e)
		{
			throw new EngineException("Can't instantiate a bean with class '"+beanClass.getName()+"'.", e);
		}
		catch (IllegalAccessException e)
		{
			throw new EngineException("No permission to instantiate a bean with class '"+beanClass.getName()+"'.", e);
		}

		return bean_instance;
	}

	void processEmbeddedElement(Template template, ElementSupport embeddingElement, String elementId, String differentiator, Object data)
	throws TemplateException, EngineException
	{
		// process the embedded elements
		if (template.hasFilteredValues(TAG_ELEMENT))
		{
			List<String[]>	element_tags = template.getFilteredValues(TAG_ELEMENT);
			for (String[] captured_groups : element_tags)
			{
				if (null == differentiator)
				{
					if (elementId.equals(captured_groups[3]))
					{
						processEmbeddedElement(captured_groups[0], template, embeddingElement, captured_groups[3], null, data);
						return;
					}
				}
				else
				{
					if (elementId.equals(captured_groups[3]))
					{
						String	value_id = captured_groups[1]+":";
							
						String	differentiator_value_id = value_id+differentiator;
						if (template.hasValueId(differentiator_value_id))
						{
							processEmbeddedElement(differentiator_value_id, template, embeddingElement, elementId, differentiator, data);
							return;
						}
						else if (template.hasValueId(value_id))
						{
							processEmbeddedElement(value_id, template, embeddingElement, elementId, differentiator, data);
							return;
						}
					}
				}
			}
		}

		throw new EmbeddedElementNotFoundException(elementId);
	}

	void processEmbeddedElement(String valueId, Template template, ElementSupport embeddingElement, String elementId, String differentiator, Object data)
	throws TemplateException, EngineException
	{
		if (!template.hasValueId(valueId))
		{
			throw new EmbeddedElementNotFoundException(elementId);
		}
		
		ElementExecutionState element_state = getElementState();
		
		ElementInfo 	embedded_element = null;
		RequestState	embedded_state = null;
		Response		embedded_response = null;
		
		// try to obtain the embedded element and throw an exception if
		// it couldn't be found
		embedded_element = mElementInfo.getSite().resolveId(elementId, mElementInfo);
		if (null == embedded_element)
		{
			throw new ElementIdNotFoundException(elementId);
		}

		// build the embedded element request parameter by merging
		// the current element's request parameters with its input values
		// also merge in the state global vars
		Map<String, String[]> parameters = new HashMap<String, String[]>(element_state.getRequestParameters());
		for (Map.Entry<String, String[]> input_entry : element_state.getInputEntries())
		{
			if (!parameters.containsKey(input_entry.getKey()))
			{
				parameters.put(input_entry.getKey(), input_entry.getValue());
			}
		}
		if (mRequestState.getStateGlobalVars() != null)
		{
			for (Map.Entry<String, String[]> globalvar_entry : mRequestState.getStateGlobalVars().entrySet())
			{
				parameters.put(globalvar_entry.getKey(), globalvar_entry.getValue());
			}
		}
		
		// build the request and response objects for the embedded element
		// and service the request
		EmbeddingContext embedding_context = new EmbeddingContext(this, embeddingElement, template, template.getDefaultValue(valueId), differentiator, data);
		embedded_response = mResponse.createEmbeddedResponse(valueId, differentiator);
		embedded_state = RequestState.getEmbeddedInstance(embedded_response, embedding_context, parameters, embedded_element);
		
		embedded_state.service();
		embedded_response.close();
		
		if (embedding_context.getCancelEmbedding())
		{
			// handle embedding cancellation by throwing a dedicated exception
			// that will bubble up to the first embedding element and print
			// out the embedded content
			throw new CancelEmbeddingTriggeredException(embedded_response.getEmbeddedContent());
		}
		else
		{
			// set the output of the element to the value in the template
			template.setValue(valueId, embedded_response.getEmbeddedContent());
		}
	}

	private String getEncodedChildRequest()
	{
		String child_request = null;

		ElementExecutionState element_state = getElementState();
		
		if (element_state.hasRequestParameterValue(ReservedParameters.CHILDREQUEST))
		{
			child_request = element_state.getRequestParameter(ReservedParameters.CHILDREQUEST);
		}
		else
		{
			String element_id = element_state.getInheritanceStack().get(0);
			child_request = ChildRequestEncoder.encode(mRequestState.getSite().resolveId(element_id), mRequestState);
		}
		
		return child_request;
	}
}
