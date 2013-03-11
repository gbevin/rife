/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RequestState.java 3928 2008-04-22 16:25:18Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.continuations.CallState;
import com.uwyn.rife.continuations.ContinuationContext;
import com.uwyn.rife.continuations.exceptions.AnswerException;
import com.uwyn.rife.engine.exceptions.CancelEmbeddingTriggeredException;
import com.uwyn.rife.engine.exceptions.EmbedPropertiesErrorException;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.tools.exceptions.LightweightError;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.*;

class RequestState
{
	private final static ThreadLocal<RequestState> ACTIVE_REQUEST_STATES = new ThreadLocal<RequestState>();
	
	private InitConfig					mInitConfig = null;
	private Site						mSite = null;
	private Request						mRequest = null;
	private Response					mResponse = null;
	private String						mGateUrl = null;
	private ElementInfo					mTarget = null;
	private ElementInfo					mSnapback = null;
	private String						mActiveElementId = null;
	private EmbeddingContext			mEmbeddingContext = null;
	private ElementInfo					mPrecedenceTarget = null;
	private Map<String, Cookie>			mStateCookies = null;
	private Map<String, String[]>		mStateGlobalVars = null;
	private ResultStates				mResultStatesRestored = null;
	private ResultStates				mResultStatesObtained = null;
	private ElementSupport				mErrorElement = null;
	private Throwable					mErrorException = null;
	
	private ElementExecutionState		mElementExecutionState = null;
	
	private String								mContinuationId = null;
	private ContinuationContext<ElementSupport>	mContinuationContext = null;
	
	static RequestState getActiveRequestState()
	{
		return ACTIVE_REQUEST_STATES.get();
	}
	
	static RequestState getInstance(InitConfig initConfig, Site site, Request request, Response response, String gateUrl, ResultStates resultStates, String pathInfo, ElementInfo target)
	{
		RequestState state = new RequestState(initConfig, site, request, response, gateUrl, resultStates, new ResultStates(resultStates), pathInfo, null);
		
		state.setTarget(target);
		state.setSnapback(target);
		state.setupContinuations();
		
		return state;
	}
	
	static RequestState getEmbeddedInstance(Response response, EmbeddingContext embeddingContext, Map<String, String[]> parameters, ElementInfo embeddedElement)
	{
		RequestState embedding_state = embeddingContext.getElementContext().getRequestState();
		
		RequestState embedded_state = new RequestState(embedding_state.getInitConfig(), embedding_state.getSite(), embedding_state.getRequest(), response, embedding_state.getGateUrl(), embedding_state.getElementResultStatesRestored(), embedding_state.getElementResultStatesObtained(), "", embeddingContext);

		embedded_state.setErrorElement(embedding_state.getErrorElement());
		embedded_state.setErrorException(embedding_state.getErrorException());
		embedded_state.setTarget(embeddedElement);
		embedded_state.setSnapback(embeddedElement);
		embedded_state.setupContinuations();
		
		// preserve the request parameters, trigger inputs and request method, this
		// makes the embedded element behave correctly after exits and such
		embedded_state.getElementState().setMethod(embedding_state.getElementState().getMethod());
		embedded_state.getElementState().setRequestParameters(parameters);
		embedded_state.getElementState().setTriggerInputs(embedding_state.getElementState().getTriggerInputs());
		embedded_state.setStateCookies(embedding_state.getStateCookies());
		
		return embedded_state;
	}
	
	private RequestState(InitConfig initConfig, Site site, Request request, Response response, String gateUrl, ResultStates resultStatesRestored, ResultStates resultStatesObtained, String pathInfo, EmbeddingContext embeddingContext)
	throws EngineException
	{
		assert initConfig != null;
		assert request != null;
		assert gateUrl != null;
		
		mInitConfig = initConfig;
		mGateUrl = gateUrl;
		mSite = site;
		mEmbeddingContext = embeddingContext;
		mResultStatesRestored = resultStatesRestored;
		mResultStatesObtained = resultStatesObtained;
		
		mRequest = request;
		mResponse = response;

		mElementExecutionState = new ElementExecutionState(this);
		mElementExecutionState.setPathInfo(pathInfo);
	}
	
	void setupContinuations()
	{
		mContinuationId = null;
		mContinuationContext = null;
		
		// try to obtain the continuation ID from the result states
		if (mResultStatesObtained != null)
		{
			String context_id = buildContextId();
			ElementResultState result_state = mResultStatesObtained.get(context_id);
			if (result_state != null)
			{
				mContinuationId = result_state.getContinuationId();
			}
		}

		// get the explicit continuation ID if it exists
		if (null == mContinuationId)
		{
			String[]	continuation_id_values = mElementExecutionState.getRequestParameters().get(ReservedParameters.CONTID);
			if (continuation_id_values != null &&
				continuation_id_values.length > 0)
			{
				mContinuationId = continuation_id_values[0];
			}
		}
	}
	
	String getContinuationId()
	{
		return mContinuationId;
	}
	
	void setContinuationId(String id)
	{
		mContinuationId = id;
		mContinuationContext = null;
	}
	
	ContinuationContext getContinuationContext(ElementInfo elementInfo)
	{
		if (mContinuationId != null &&
			null == mContinuationContext)
		{
			try
			{
				mContinuationContext = mSite.getContinuationManager().resumeContext(mContinuationId);
			}
			catch (CloneNotSupportedException e)
			{
				throw new EngineException(e);
			}
		}
		
		if (mContinuationContext != null)
		{
			ElementSupport	continuable_element = mContinuationContext.getContinuable();
			ElementInfo		continuable_element_info = continuable_element._getElementInfo();
			// the element info can be null if this continuation context was obtained
			// through Terracotta from another node, in that case the comparison below
			// can't be done
			if (null == continuable_element_info)
			{
				return mContinuationContext;
			}
			
			String	continuable_absolute_element_id = Site.getAbsoluteId(continuable_element_info.getId(), continuable_element_info);
			if (null == elementInfo || continuable_absolute_element_id.equals(elementInfo.getId()))
			{
				return mContinuationContext;
			}
		}
		return null;
	}
	
	ResultStates getElementResultStatesRestored()
	{
		return mResultStatesRestored;
	}
	
	ResultStates getElementResultStatesObtained()
	{
		return mResultStatesObtained;
	}
	
	String buildContextId()
	{
		return buildContextId(new StringBuilder()).toString();
	}
	
	private StringBuilder buildContextId(StringBuilder buffer)
	{
		boolean is_embedded_submission = isEmbedded() && null == getTarget().getUrl();
		if (is_embedded_submission)
		{
			buffer = getEmbeddingContext().getElementContext().getRequestState().buildContextId(buffer);
			buffer.append("::");
			buffer.append(getEmbeddingContext().getTemplate().getName());
			buffer.append(":");
		}
	
		// during precedence, target the submission context id to the actual target element
		if (isPreceeding())
		{
			buffer.append(getPrecedenceTarget().getId());
		}
		// during inheritance, target the submission context id to the actual target element
		else if (mElementExecutionState.inInheritanceStructure())
		{
			buffer.append(getTarget().getId());
		}
		// use the ID of the active element if it's available
		else if (mActiveElementId != null)
		{
			buffer.append(mActiveElementId);
		}
		// use the id of the target element
		else
		{
			buffer.append(getTarget().getId());
		}

		if (is_embedded_submission)
		{
			String differentiator = getEmbeddingContext().getDifferentiator();
			if (differentiator != null)
			{
				buffer.append(":");
				buffer.append(differentiator);
			}
		}

		return buffer;
	}
	
	ElementContext getElementContext(ElementInfo elementInfo, Response response)
	throws EngineException
	{
		ElementSupport element = null;
		
		ContinuationContext context = getContinuationContext(elementInfo);
		if (context != null)
		{
			element = (ElementSupport)mContinuationContext.getContinuable();
			synchronized (element)
			{
				// ensure that the element's element info is set to the element, mainly
				// for use with Terracotta
				element.setElementInfo(elementInfo);
			}
		}
		else
		{
			element = elementInfo.getElement();
		}
		
		if (null == element)
		{
			throw new EngineException("Error while instantiating the element '"+elementInfo.getDeclarationName()+"' at url.");
		}
		
		ElementContext	element_context = new ElementContext(element, this, response);
		if (null == element_context)
		{
			throw new EngineException("Error while constructing the context for the element '"+elementInfo.getDeclarationName()+"'.");
		}
		
		return element_context;
	}
	
	void handlePrecedence(Response response, ElementInfo precedenceTarget)
	{
		// obtain the precedence stack and if it exists the top parent
		Stack<ElementInfo> precedence_stack = precedenceTarget.getPrecedenceStack();
		if (precedence_stack != null &&
			precedence_stack.size() > 0)
		{
			// preserve the original state variables
			ElementInfo		original_target = getTarget();
			Stack<String>	original_inheritance_stack = mElementExecutionState.getInheritanceStack();
			RequestMethod	original_method = mElementExecutionState.getMethod();
			
			// retain initiating precedence target element info to be able to
			// check for a precedence structure later
			if (null == getPrecedenceTarget())
			{
				setPrecedenceTarget(precedenceTarget);
				mElementExecutionState.setMethod(RequestMethod.PRECEDENCE);
			}
			
			// process all precedence elements
			ElementInfo		active_element = null;
			
			for (int i = precedence_stack.size()-1; i >= 0; i--)
			{
				active_element = precedence_stack.get(i);
				
				if (active_element == precedenceTarget)
				{
					break;
				}
				
				// process the precedence element
				setTarget(active_element);
				service();
			}
			
			// clear the preserved precedence target element info only if this
			// was the element info that initiated it
			if (precedenceTarget == getPrecedenceTarget())
			{
				setPrecedenceTarget(null);
			}
			
			// restore original state variables
			setTarget(original_target);
			mElementExecutionState.setInheritanceStack(original_inheritance_stack);
			mElementExecutionState.setMethod(original_method);
		}
	}
	
	void service()
	throws EngineException
	{
		// Get the currently active request state to be able to restore it
		// as the active one after this new one finished executing. This is
		// needed for embedded requests.
		RequestState previous = ACTIVE_REQUEST_STATES.get();
		
		// Set the actively running request state
		ACTIVE_REQUEST_STATES.set(this);

		try
		{
			// obtain the inheritance stack and if it exists the top parent
			ElementInfo	element_info = null;
			if (mElementExecutionState.inInheritanceStructure())
			{
				String element_id = mElementExecutionState.getInheritanceStack().pop();
				element_info = mSite.resolveId(element_id);
			}
			if (null == element_info)
			{
				element_info = mTarget;
			}
			
			// preserve the ongoing continuation context
			ContinuationContext previous_context = ContinuationContext.getActiveContext();

			try
			{
				Object			call_answer = null;
				ElementContext	element_context = getElementContext(element_info, mResponse);
				while(true)
				{
					try
					{
						try
						{
							// successively process each element context until none is available anymore
							ContinuationContext.setActiveContext(null);
							ElementContext next_element_context = element_context.processContext();
							while (next_element_context != null)
							{
								element_context = next_element_context;
								ContinuationContext.setActiveContext(null);
								synchronized (this)
								{
									mActiveElementId = element_context.getElementInfo().getId();
									try
									{
										next_element_context = element_context.processContext();
									}
									finally
									{
										mActiveElementId = null;
									}
								}
							}

							call_answer = null;
							break;
						}
						catch (AnswerException e)
						{
							synchronized (this)
							{
								// obtain the context and the answer of the answering element
								ContinuationContext context = e.getContext();
								call_answer = e.getAnswer();

								// handle the call state of the last processed element context
								if (context != null &&
									context.getActiveCallState() != null)
								{
									CallState call_state = context.getActiveCallState();
									mContinuationContext = null;
									mContinuationId = call_state.getContinuationId();
									mElementExecutionState = ((ElementExecutionState)call_state.getState()).clone();
									mElementExecutionState.setRequestState(this);

									// try to obtain the continuation context
									ContinuationContext continuation_context = getContinuationContext(null);
									if (null == continuation_context)
									{
										break;
									}
									element_info = ((ElementSupport)continuation_context.getContinuable()).getElementInfo();

									// set the call answer
									continuation_context.setCallAnswer(call_answer);

									// set the request target to the actual element, cancelling
									// the previous exit target that was set during the call
									// continuation
									mTarget = element_info;

									// create the new element context
									element_context = getElementContext(element_info, mResponse);

									// propagate the outputs of the answer element to the call element
									ElementSupport answer_element = (ElementSupport)e.getContext().getContinuable();
									ElementContext element_context_answer = (ElementContext)answer_element._getElementContext();
									OutputValues outputs = element_context.getOutputs();
									for (Map.Entry<String, String[]> output_entry : element_context_answer.getOutputs().aggregateValues().entrySet())
									{
										outputs.put(output_entry.getKey(), output_entry.getValue());
									}
								}
							}
						}
						catch (CancelEmbeddingTriggeredException e)
						{
							if (isEmbedded())
							{
								throw e;
							}

							mResponse.print(e.getEmbeddingContent());
							mResponse.flush();
							break;
						}
					}
					catch (LightweightError e)
					{
						throw e;
					}
					catch (Throwable e)
					{
						ErrorHandler matching_handler = null;
						if (element_info != null)
						{
							if (element_info.hasErrorHandlers())
							{
								for (ErrorHandler handler : element_info.getErrorHandlers())
								{
									if (handler.appliesToException(e))
									{
										matching_handler = handler;
										break;
									}
								}
							}
						}

						if (matching_handler != null)
						{
							mResponse.clearBuffer();
							mErrorElement = element_context.getElementSupport();
							mErrorException = e;
							element_context = getElementContext(matching_handler.getTarget(), mResponse);
						}
						else
						{
							if (e instanceof RuntimeException)
							{
								throw (RuntimeException)e;
							}
							else
							{
								throw new EngineException(e);
							}
						}
					}
				}
			}
			finally
			{
				// restore ongoing continuation context
				ContinuationContext.setActiveContext(previous_context);
			}
		}
		finally
		{
			// restory the previously running request state
			ACTIVE_REQUEST_STATES.set(previous);
		}
	}
	
	void setTarget(ElementInfo targetElement)
	{
		assert targetElement != null;
		
		synchronized (mElementExecutionState)
		{
			mTarget = targetElement;
			mElementExecutionState.clearVirtualInputs();
			
			// create the initial inheritance stack
			Stack<ElementInfo> target_inheritancestack = mTarget.getInheritanceStack();
			mElementExecutionState.setInheritanceStack(null);
			if (target_inheritancestack != null)
			{
				Stack<String> inheritance_stack = new Stack<String>();
				for (ElementInfo element_info : target_inheritancestack)
				{
					inheritance_stack.add(element_info.getId());
				}
				mElementExecutionState.setInheritanceStack(inheritance_stack);
			}
			
			// process the possible trigger list
			if (mElementExecutionState.inInheritanceStructure())
			{
				if (!mElementExecutionState.hasTriggerList())
				{
					if (isEmbedded()) // don't take the trigger list that the embedder created as the trigger list for the embedded element
					{
						mElementExecutionState.setTriggerList(new ArrayList<TriggerContext>());
					}
					else
					{
						// if no trigger list is present, decode it from the request
						mElementExecutionState.setTriggerList(TriggerListEncoder.decode(mElementExecutionState.getRequestParameterValues(ReservedParameters.TRIGGERLIST)));
					}
				}
			}
		}
	}
	
	void setSnapback(ElementInfo snapbackElement)
	{
		assert snapbackElement != null;
		
		mSnapback = snapbackElement;
	}
	
	InitConfig getInitConfig()
	{
		return mInitConfig;
	}
	
	Site getSite()
	{
		return mSite;
	}
	
	void clearRequest()
	{
		mRequest = null;
	}
	
	Request getRequest()
	{
		return mRequest;
	}
	
	Response getResponse()
	{
		return mResponse;
	}
	
	ElementInfo getTarget()
	{
		return mTarget;
	}
	
	ElementInfo getSnapback()
	{
		return mSnapback;
	}
	
	EmbeddingContext getEmbeddingContext()
	{
		return mEmbeddingContext;
	}
	
	boolean isEmbedded()
	{
		return mEmbeddingContext != null;
	}
	
	String getEmbedDifferentiator()
	{
		if (null == mEmbeddingContext)
		{
			return null;
		}
		
		return mEmbeddingContext.getDifferentiator();
	}

	String getEmbedValue()
	{
		if (null == mEmbeddingContext)
		{
			return null;
		}
		
		return mEmbeddingContext.getValue();
	}

	Object getEmbedData()
	{
		if (null == mEmbeddingContext)
		{
			return null;
		}
		
		return mEmbeddingContext.getData();
	}

	Properties getEmbedProperties()
	throws EmbedPropertiesErrorException
	{
		if (null == mEmbeddingContext)
		{
			return null;
		}

		try
		{
			return mEmbeddingContext.getEmbedProperties();
		}
		catch (IOException e)
		{
			throw new EmbedPropertiesErrorException(mTarget.getDeclarationName(), mEmbeddingContext.getValue(), e);
		}
	}

	void setPrecedenceTarget(ElementInfo elementInfo)
	{
		mPrecedenceTarget = elementInfo;
	}
	
	ElementInfo getPrecedenceTarget()
	{
		return mPrecedenceTarget;
	}
	
	boolean isPreceeding()
	{
		return mPrecedenceTarget != null;
	}
	
	String getGateUrl()
	{
		return mGateUrl;
	}
	
	ElementExecutionState getElementState()
	{
		return mElementExecutionState;
	}
	
	String getServerRootUrl(int port)
	{
		return mRequest.getServerRootUrl(port);
	}
	
	String getWebappRootUrl(int port)
	{
		if (RifeConfig.Engine.getProxyRootUrl() != null)
		{
			return RifeConfig.Engine.getProxyRootUrl();
		}
		
		StringBuilder webapp_root = new StringBuilder();
		webapp_root.append(getServerRootUrl(port));
		String gate_url = getGateUrl();
		if (!gate_url.startsWith("/"))
		{
			webapp_root.append("/");
		}
		webapp_root.append(gate_url);
		if (gate_url.length() > 0 &&
			!gate_url.endsWith("/"))
		{
			webapp_root.append("/");
		}

		return webapp_root.toString();
	}

	void setErrorElement(ElementSupport errorElement)
	{
		mErrorElement = errorElement;
	}

	void setErrorException(Throwable errorException)
	{
		mErrorException = errorException;
	}

	ElementSupport getErrorElement()
	{
		return mErrorElement;
	}

	Throwable getErrorException()
	{
		return mErrorException;
	}

	// wrapped methods
	Object getRequestAttribute(String name)
	{
		return mRequest.getAttribute(name);
	}
	
	boolean hasRequestAttribute(String name)
	{
		return mRequest.hasAttribute(name);
	}
	
	Enumeration getRequestAttributeNames()
	{
		return mRequest.getAttributeNames();
	}
	
	String getCharacterEncoding()
	{
		return mRequest.getCharacterEncoding();
	}
	
	String getContentType()
	{
		return mRequest.getContentType();
	}
	
	long getDateHeader(String name)
	{
		return mRequest.getDateHeader(name);
	}
	
	String getHeader(String name)
	{
		return mRequest.getHeader(name);
	}
	
	Enumeration getHeaderNames()
	{
		return mRequest.getHeaderNames();
	}
	
	Enumeration getHeaders(String name)
	{
		return mRequest.getHeaders(name);
	}
	
	int getIntHeader(String name)
	{
		return mRequest.getIntHeader(name);
	}
	
	Locale getLocale()
	{
		return mRequest.getLocale();
	}
	
	Enumeration getLocales()
	{
		return mRequest.getLocales();
	}
	
	String getProtocol()
	{
		return mRequest.getProtocol();
	}
	
	String getRemoteAddr()
	{
		return mRequest.getRemoteAddr();
	}
	
	String getRemoteUser()
	{
		return mRequest.getRemoteUser();
	}
	
	String getRemoteHost()
	{
		return mRequest.getRemoteHost();
	}
	
	int getServerPort()
	{
		return mRequest.getServerPort();
	}
	
	String getScheme()
	{
		return mRequest.getScheme();
	}
	
	String getServerName()
	{
		return mRequest.getServerName();
	}
	
	boolean isSecure()
	{
		return mRequest.isSecure();
	}
	
	void removeRequestAttribute(String name)
	{
		mRequest.removeAttribute(name);
	}
	
	void setRequestAttribute(String name, Object object)
	{
		mRequest.setAttribute(name, object);
	}
	
	// shielded methods
	boolean hasUploadedFile(String name)
	{
		return mRequest.hasFile(name);
	}
	
	UploadedFile getUploadedFile(String name)
	{
		return mRequest.getFile(name);
	}
	
	UploadedFile[] getUploadedFiles(String name)
	{
		return mRequest.getFiles(name);
	}
	
	Collection<String> getUploadedFileNames()
	{
		return mRequest.getFiles().keySet();
	}
	
	void setStateCookies(Map<String, Cookie> stateCookies)
	{
		mStateCookies = stateCookies;
	}
	
	Map<String, Cookie> getStateCookies()
	{
		return mStateCookies;
	}
	
	void setStateCookie(Cookie cookie)
	{
		assert cookie != null;
		assert cookie.getName() != null;
		
		if (null == mStateCookies)
		{
			mStateCookies = new HashMap<String, Cookie>();
		}
		
		mStateCookies.put(cookie.getName(), cookie);
	}
	
	boolean hasCookie(String name)
	{
		assert name != null;
		
		if (mStateCookies != null &&
			mStateCookies.containsKey(name))
		{
			return true;
		}
		
		return mRequest.hasCookie(name);
	}
	
	Cookie getCookie(String name)
	{
		assert name != null;
		
		if (mStateCookies != null &&
			mStateCookies.containsKey(name))
		{
			return mStateCookies.get(name);
		}
		
		return mRequest.getCookie(name);
	}
	
	HashMap<String, Cookie> getCookies()
	{
		HashMap<String, Cookie> cookies = new HashMap<String, Cookie>();
		
		Cookie[] request_cookies = mRequest.getCookies();
		if (request_cookies != null)
		{
			for (Cookie cookie : request_cookies)
			{
				cookies.put(cookie.getName(), cookie);
			}
		}
		
		if (mStateCookies != null)
		{
			cookies.putAll(mStateCookies);
		}
		
		return cookies;
	}
	
	Map<String, String[]> getStateGlobalVars()
	{
		return mStateGlobalVars;
	}
	
	void setStateGlobalVar(String name, String[] values)
	{
		assert name != null;
		
		if (null == mStateGlobalVars)
		{
			mStateGlobalVars = new HashMap<String, String[]>();
		}
		
		mStateGlobalVars.put(name, values);
	}
	
	void clearStateGlobalVar(String name)
	{
		assert name != null;
		
		if (null == mStateGlobalVars)
		{
			return;
		}
		
		mStateGlobalVars.remove(name);
	}
	
	EmbeddingListener getEmbeddingListener()
	{
		return new EmbeddingListener(this);
	}
	
	PrecedenceListener getPrecedenceListener()
	{
		return new PrecedenceListener(this);
	}
	
	static class EmbeddingListener implements OutputListener, OutcookieListener
	{
		
		private RequestState	mState = null;
		
		private EmbeddingListener(RequestState state)
		{
			assert state != null;
			
			mState = state;
		}
		
		public void outputValueSet(String name, String[] values)
		{
			if (!mState.isEmbedded())
			{
				return;
			}
			
			if (!mState.getEmbeddingContext().getElementContext().getElementInfo().containsGlobalVar(name))
			{
				return;
			}
			
			mState.getEmbeddingContext().getElementContext().setOutputValues(name, values);
			mState.getEmbeddingContext().getElementContext().getRequestState().setStateGlobalVar(name, values);
		}
		
		public void outputValueCleared(String name)
		{
			if (!mState.isEmbedded())
			{
				return;
			}
			
			if (!mState.getEmbeddingContext().getElementContext().getElementInfo().containsGlobalVar(name))
			{
				return;
			}
			
			mState.getEmbeddingContext().getElementContext().clearOutputValue(name);
			mState.getEmbeddingContext().getElementContext().getRequestState().clearStateGlobalVar(name);
		}
		
		public void automatedOutputValueSet(String name, String[] values)
		{
			if (!mState.isEmbedded())
			{
				return;
			}
			
			if (!mState.getEmbeddingContext().getElementContext().getElementInfo().containsGlobalVar(name))
			{
				return;
			}
			
			mState.getEmbeddingContext().getElementContext().setAutomatedOutputValues(name, values);
			mState.getEmbeddingContext().getElementContext().getRequestState().setStateGlobalVar(name, values);
		}
		
		public void automatedOutputValueCleared(String name)
		{
			if (!mState.isEmbedded())
			{
				return;
			}
			
			if (!mState.getEmbeddingContext().getElementContext().getElementInfo().containsGlobalVar(name))
			{
				return;
			}
			
			mState.getEmbeddingContext().getElementContext().clearAutomatedOutputValue(name);
			mState.getEmbeddingContext().getElementContext().getRequestState().clearStateGlobalVar(name);
		}
		
		public void outcookieSet(Cookie cookie)
		{
			if (!mState.isEmbedded())
			{
				return;
			}
			
			if (!mState.getEmbeddingContext().getElementContext().getElementInfo().containsIncookie(cookie.getName()) &&
				!mState.getEmbeddingContext().getElementContext().getElementInfo().containsGlobalCookie(cookie.getName()))
			{
				return;
			}
			
			mState.getEmbeddingContext().getElementContext().setCookie(cookie);
			mState.getEmbeddingContext().getElementContext().getRequestState().setStateCookie(cookie);
		}
	}
	
	static class PrecedenceListener implements OutputListener, OutcookieListener
	{
		private RequestState	mState = null;
		
		private PrecedenceListener(RequestState state)
		{
			assert state != null;
			
			mState = state;
		}
		
		public void outputValueSet(String name, String[] values)
		{
			if (!mState.isPreceeding())
			{
				return;
			}
			
			if (!mState.getPrecedenceTarget().containsGlobalVar(name))
			{
				return;
			}
			
			mState.getElementState().getRequestParameters().put(name, values);
		}
		
		public void outputValueCleared(String name)
		{
			if (!mState.isPreceeding())
			{
				return;
			}
			
			if (!mState.getPrecedenceTarget().containsGlobalVar(name))
			{
				return;
			}
			
			mState.getElementState().getRequestParameters().remove(name);
		}
		
		public void automatedOutputValueSet(String name, String[] values)
		{
			outputValueSet(name, values);
		}
		
		public void automatedOutputValueCleared(String name)
		{
			outputValueCleared(name);
		}
		
		public void outcookieSet(Cookie cookie)
		{
			if (!mState.isPreceeding())
			{
				return;
			}
			
			if (!mState.getPrecedenceTarget().containsIncookie(cookie.getName()) &&
				!mState.getPrecedenceTarget().containsGlobalCookie(cookie.getName()))
			{
				return;
			}
			
			mState.setStateCookie(cookie);
		}
	}
}

