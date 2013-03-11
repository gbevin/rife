/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FlowState.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class FlowState implements Serializable
{
	private static final long serialVersionUID = -8198565791703792063L;
	
	private Map<String, String[]>	mParameters = null;
	private Map<String, String[]>	mSubmissionGlobalInputs = null;
	private Map<String, String[]>	mSubmissionElementInputs = null;
	private String					mSubmissionContextId = null;
	
	public boolean isEmpty()
	{
		return (null == mParameters || 0 == mParameters.size()) &&
			(null == mSubmissionGlobalInputs || 0 == mSubmissionGlobalInputs.size()) &&
			(null == mSubmissionElementInputs || 0 == mSubmissionElementInputs.size()) &&
			null == mSubmissionContextId;
	}
	
	public void putParameter(String key, String[] value)
	{
		if (null == mParameters)
		{
			mParameters = new LinkedHashMap<String, String[]>();
		}
		
		mParameters.put(key, value);
	}
	
	public void putParameter(String key, String value)
	{
		putParameter(key, new String[] {value});
	}
	
	public void setParameters(Map<String, String[]> parameters)
	{
		mParameters = parameters;
	}
	
	public boolean hasParameters()
	{
		return mParameters != null &&
			mParameters.size() > 0;
	}
	
	public Map<String, String[]> getParameters()
	{
		return mParameters;
	}
	
	public void putSubmissionGlobalInput(String key, String[] value)
	{
		if (null == mSubmissionGlobalInputs)
		{
			mSubmissionGlobalInputs = new LinkedHashMap<String, String[]>();
		}
		
		mSubmissionGlobalInputs.put(key, value);
	}
	
	public Map<String, String[]> getSubmissionGlobalInputs()
	{
		return mSubmissionGlobalInputs;
	}
	
	public boolean hasSubmissionGlobalInputs()
	{
		return mSubmissionGlobalInputs != null &&
			mSubmissionGlobalInputs.size() > 0;
	}
	
	public void setSubmissionElementInputs(Map<String, String[]> inputs)
	{
		mSubmissionElementInputs = inputs;
	}
	
	public Map<String, String[]> getSubmissionElementInputs()
	{
		return mSubmissionElementInputs;
	}
	
	public boolean hasSubmissionElementInputs()
	{
		return mSubmissionElementInputs != null &&
			mSubmissionElementInputs.size() > 0;
	}
	
	public void setSubmissionContextId(String submissionContextId)
	{
		mSubmissionContextId = submissionContextId;
	}
	
	public String getSubmissionContextId()
	{
		return mSubmissionContextId;
	}
}

