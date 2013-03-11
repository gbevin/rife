/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementResultStateSession.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import java.io.Serializable;
import java.util.Map;
import java.util.LinkedHashMap;

public class ElementResultStateSession implements ElementResultState, Serializable
{
	private static final long serialVersionUID = 8239885817838567612L;
	
	private String					mContextId;
	private String					mContinuationId;
	private Map<String, String[]>	mPreservedInputs;

	private transient String		mBase64EncodedState;

	public ElementResultStateSession(String contextId)
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
	
	public void setContinuationId(String continuationId)
	{
		mContinuationId = continuationId;
	}
	
	public String getContinuationId()
	{
		return mContinuationId;
	}
	
	public void setPreservedInputs(Map<String, String[]> inputs)
	{
		if (inputs != null)
		{
			if (inputs.containsKey(ReservedParameters.CONTID))
			{
				String[] contid = inputs.remove(ReservedParameters.CONTID);
				if (contid.length > 0)
				{
					setContinuationId(contid[0]);
				}
			}
		}
		
		mPreservedInputs = inputs;
	}
	
	public Map<String, String[]> getPreservedInputs()
	{
		return mPreservedInputs;
	}

	public String getBase64EncodedState()
	{
		if (null == mBase64EncodedState)
		{
			if (mContinuationId != null)
			{
				Map<String, String[]> params;
				if (mPreservedInputs != null)
				{
					params = new LinkedHashMap<String, String[]>(mPreservedInputs);
				}
				else
				{
					params = new LinkedHashMap<String, String[]>();
				}

				params.put(ReservedParameters.CONTID, new String[] {mContinuationId});
				mBase64EncodedState = ParameterMapEncoder.encodeToBase64String(params, mContextId);
			}
			else
			{
				mBase64EncodedState = ParameterMapEncoder.encodeToBase64String(mPreservedInputs, mContextId);
			}
		}

		return mBase64EncodedState;
	}
}
