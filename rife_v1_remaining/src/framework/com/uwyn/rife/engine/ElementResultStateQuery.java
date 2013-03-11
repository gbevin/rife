/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementResultStateQuery.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import java.io.UnsupportedEncodingException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class ElementResultStateQuery implements ElementResultState, Serializable
{
	private static final long serialVersionUID = -6134474367166651863L;

	private String					mContextId;
	private String					mContinuationId;
	private Map<String, String[]>	mPreservedInputs;
	
	private transient String		mStringEncodedState;
	private transient String		mBase64EncodedState;

	public ElementResultStateQuery(String contextId)
	{
		mContextId = contextId;
	}
	
	public void populate(ElementResultState other)
	{
		this.mContextId = other.getContextId();
		this.mContinuationId = other.getContinuationId();
		this.mPreservedInputs = other.getPreservedInputs();
	}
	
	public String getContextId()
	{
		return mContextId;
	}
	
	void setEncodedState(String stringEncodedState, String base64EncodedState)
	{
		mContinuationId = null;
		mPreservedInputs = null;
		
		mStringEncodedState = stringEncodedState;
		mBase64EncodedState = base64EncodedState;
	}
	
	String getStringEncodedState()
	{
		if (null == mStringEncodedState)
		{
			if (mContinuationId != null)
			{
				if (null == mPreservedInputs)
				{
					mPreservedInputs = new LinkedHashMap<String, String[]>();
				}
				
				mPreservedInputs.put(ReservedParameters.CONTID, new String[] {mContinuationId});
			}
			
			try
			{
				mStringEncodedState = new String(ParameterMapEncoder.encodeToBytes(mPreservedInputs, mContextId), "UTF-8");
			}
			catch (UnsupportedEncodingException e)
			{
				// can't happen, UTF-8 is always supported
			}
		}
		
		return mStringEncodedState;
	}
		
	public String getBase64EncodedState()
	{
		if (null == mBase64EncodedState)
		{
			if (mContinuationId != null)
			{
				if (null == mPreservedInputs)
				{
					mPreservedInputs = new LinkedHashMap<String, String[]>();
				}
				
				mPreservedInputs.put(ReservedParameters.CONTID, new String[] {mContinuationId});
			}
			
			mBase64EncodedState = ParameterMapEncoder.encodeToBase64String(mPreservedInputs, mContextId);
		}
		
		return mBase64EncodedState;
	}
	
	public void setContinuationId(String continuationId)
	{
		mStringEncodedState = null;
		mBase64EncodedState = null;
		
		mContinuationId = continuationId;
	}
	
	public String getContinuationId()
	{
		if (null == mContinuationId)
		{
			extractState();
		}
		
		return mContinuationId;
	}
	
	public void setPreservedInputs(Map<String, String[]> preservedInputs)
	{
		mStringEncodedState = null;
		mBase64EncodedState = null;
		
		mPreservedInputs = preservedInputs;
	}
	
	public Map<String, String[]> getPreservedInputs()
	{
		if (null == mPreservedInputs)
		{
			extractState();
		}
		
		return mPreservedInputs;
	}

	private void extractState()
	{
		if (mPreservedInputs != null)
		{
			return;
		}
		
		Map<String, String[]> inputs = ParameterMapEncoder.decodeFromString(getStringEncodedState());
		if (inputs.containsKey(ReservedParameters.CONTID))
		{
			String[] contid = inputs.remove(ReservedParameters.CONTID);
			if (contid.length > 0)
			{
				mContinuationId = contid[0];
			}
		}
		mPreservedInputs = inputs;
	}
}
