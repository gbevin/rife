/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TriggerContext.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import java.util.HashMap;
import java.util.Map;

class TriggerContext
{
	private String					mDeclarationName = null;
	private int						mType = -1;
	private String					mTriggerName = null;
	private String[]				mTriggerValues = null;
	private Map<String, String[]>	mParameters = null;
	
	static final int					TRIGGER_CHILD = 1;
	static final int					TRIGGER_EXIT = 2;
	
	TriggerContext()
	{
		mParameters = new HashMap<String, String[]>();
		mTriggerName = "";
		mTriggerValues = new String[0];
	}
	
	TriggerContext(String declarationName, int type)
	{
		this();
		mDeclarationName = declarationName;
		mType = type;
	}

	static TriggerContext generateChildTrigger(ElementInfo elementInfo, String name, String[] values, Map<String, String[]> inputs)
	{
		TriggerContext	trigger_context = new TriggerContext(elementInfo.getDeclarationName(), TRIGGER_CHILD);
		trigger_context.setTriggerName(name);
		trigger_context.setTriggerValues(values);
		trigger_context.setParameters(inputs);
		
		return trigger_context;
	}

	static TriggerContext generateExitTrigger(ElementInfo elementInfo, String name, Map<String, String[]> inputs)
	{
		TriggerContext	trigger_context = new TriggerContext(elementInfo.getDeclarationName(), TRIGGER_EXIT);
		trigger_context.setTriggerName(name);
		trigger_context.setParameters(inputs);
		
		return trigger_context;
	}
	
	void setDeclarationName(String declarationName)
	{
		mDeclarationName = declarationName;
	}
	
	String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	void setType(int type)
	{
		mType = type;
	}
	
	int getType()
	{
		return mType;
	}
	
	void setTriggerName(String triggerName)
	{
		mTriggerName = triggerName;
	}
	
	String getTriggerName()
	{
		return mTriggerName;
	}
	
	void setTriggerValues(String[] triggerValues)
	{
		mTriggerValues = triggerValues;
	}
	
	String[] getTriggerValues()
	{
		return mTriggerValues;
	}
	
	void setParameters(Map<String, String[]> parameters)
	{
		mParameters = parameters;
	}
	
	Map<String, String[]> getParameters()
	{
		return mParameters;
	}
	
	String[] getParameterValues()
	{
		if (0 == mParameters.size())
		{
			return null;
		}
		else
		{
			return mParameters.values().iterator().next();
		}
	}
}

