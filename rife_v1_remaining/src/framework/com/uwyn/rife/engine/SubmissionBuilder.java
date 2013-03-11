/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SubmissionBuilder.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.BeanDeclaration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SubmissionBuilder
{
	private ElementInfoBuilder	mElementInfoBuilder = null;
	private	String				mName = null;

	private LinkedHashMap<String, String[]>			mParameters = null;
	private ArrayList<String>						mParametersRegexp = null;
	private LinkedHashMap<BeanDeclaration, String>	mBeans = null;
	private ArrayList<String>						mFiles = null;
	private ArrayList<String>						mFilesRegexp = null;
	private Scope									mScope = null;
	private boolean									mCancelContinuations = false;
	
	SubmissionBuilder(ElementInfoBuilder elementInfoBuilder, String name)
	{
		assert elementInfoBuilder != null;
		assert name != null;
	
		mElementInfoBuilder = elementInfoBuilder;
		mName = name;

		return;
	}
	
	Submission getSubmission(ElementInfo elementInfo)
	{
		Submission	submission = new Submission();
		elementInfo.addSubmission(mName, submission);
		submission.setCancelContinuations(mCancelContinuations);
		submission.setScope(mScope);
		
		if (mParameters != null)
		{
			for (Map.Entry<String, String[]> parameter : mParameters.entrySet())
			{
				submission.addParameter(parameter.getKey(), parameter.getValue());
			}
		}
		
		if (mParametersRegexp != null)
		{
			for (String pattern : mParametersRegexp)
			{
				submission.addParameterRegexp(pattern);
			}
		}
		
		if (mBeans != null)
		{
			for (Map.Entry<BeanDeclaration, String> bean : mBeans.entrySet())
			{
				submission.addBean(bean.getKey(), bean.getValue());
			}
		}
		
		if (mFiles != null)
		{
			for (String name : mFiles)
			{
				submission.addFile(name);
			}
		}
		
		if (mFilesRegexp != null)
		{
			for (String pattern : mFilesRegexp)
			{
				submission.addFileRegexp(pattern);
			}
		}
		
		return submission;
	}
	
	public SubmissionBuilder cancelContinuations(boolean cancelContinuations)
	{
		mCancelContinuations = cancelContinuations;
		return this;
	}
	
	public SubmissionBuilder setScope(Scope scope)
	{
		mScope = scope;
		
		return this;
	}
	
	public SubmissionBuilder addParameter(String name)
	{
		return addParameter(name, null);
	}
	
	public SubmissionBuilder addParameter(String name, String[] defaultValues)
	{
		if (null == mParameters)
		{
			mParameters = new LinkedHashMap<String, String[]>();
		}

		mParameters.put(name, defaultValues);
		
		return this;
	}
	
	public SubmissionBuilder addParameterRegexp(String pattern)
	{
		if (null == mParametersRegexp)
		{
			mParametersRegexp = new ArrayList<String>();
		}
		
		mParametersRegexp.add(pattern);
		
		return this;
	}
	
	public SubmissionBuilder addBean(String classname)
	{
		return addBean(classname, null, null, null);
	}
	
	public SubmissionBuilder addBean(String classname, String prefix)
	{
		return addBean(classname, prefix, null, null);
	}
	
	public SubmissionBuilder addBean(String classname, String prefix, String name)
	{
		return addBean(classname, prefix, name, null);
	}
	
	public SubmissionBuilder addBean(String classname, String prefix, String name, String groupName)
	{
		if (prefix != null && 0 == prefix.length())			prefix = null;
		if (name != null && 0 == name.length())				name = null;
		if (groupName != null && 0 == groupName.length())	groupName = null;

		if (null == mBeans)
		{
			mBeans = new LinkedHashMap<BeanDeclaration, String>();
		}
		
		mBeans.put(new BeanDeclaration(classname, prefix, groupName), name);
		
		return this;
	}
	
	public SubmissionBuilder addBean(Class klass)
	{
		return addBean(klass, null, null, null);
	}
	
	public SubmissionBuilder addBean(Class klass, String prefix)
	{
		return addBean(klass, prefix, null, null);
	}
	
	public SubmissionBuilder addBean(Class klass, String prefix, String name)
	{
		return addBean(klass, prefix, name, null);
	}
	
	public SubmissionBuilder addBean(Class klass, String prefix, String name, String groupName)
	{
		return addBean(new BeanDeclaration(klass, prefix, groupName), name);
	}
	
	private SubmissionBuilder addBean(BeanDeclaration beanDeclaration, String name)
	{
		if (null == mBeans)
		{
			mBeans = new LinkedHashMap<BeanDeclaration, String>();
		}
		
		mBeans.put(beanDeclaration, name);
		
		return this;
	}
	
	public SubmissionBuilder addFile(String name)
	{
		if (null == mFiles)
		{
			mFiles = new ArrayList<String>();
		}
		
		mFiles.add(name);
		
		return this;
	}
	
	public SubmissionBuilder addFileRegexp(String pattern)
	{
		if (null == mFilesRegexp)
		{
			mFilesRegexp = new ArrayList<String>();
		}
		
		mFilesRegexp.add(pattern);
		
		return this;
	}
	
	public ElementInfoBuilder leaveSubmission()
	{
		return mElementInfoBuilder;
	}
}
