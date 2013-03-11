/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Submission.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.*;
import java.util.*;

import com.uwyn.rife.site.Constrained;
import com.uwyn.rife.site.ConstrainedUtils;
import com.uwyn.rife.site.ValidatedConstrained;
import com.uwyn.rife.site.ValidationGroup;
import com.uwyn.rife.tools.BeanUtils;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.StringUtils;
import com.uwyn.rife.tools.exceptions.BeanUtilsException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Submission implements Cloneable
{
	private String									mName = null;
	private ElementInfo								mElementInfo = null;
	private LinkedHashMap<String, String[]>			mParameters = null;
	private ArrayList<String>						mParameterDefaults = null;
	private ArrayList<Pattern>						mParameterRegexps = null;
	private ArrayList<String>						mFiles = null;
	private ArrayList<Pattern>						mFileRegexps = null;
	private boolean									mHasParameterDefaults = false;
	private ArrayList<BeanDeclaration>				mBeans = null;
	private LinkedHashMap<String, BeanDeclaration>	mNamedBeans = null;
	private Scope									mScope = null;
	private boolean									mCancelContinuations = false;
	
	Submission()
	{
		mParameters = new LinkedHashMap<String, String[]>();
		mParameterDefaults = new ArrayList<String>();
		mParameterRegexps = new ArrayList<Pattern>();
		mFiles = new ArrayList<String>();
		mFileRegexps = new ArrayList<Pattern>();
		mBeans = new ArrayList<BeanDeclaration>();
		mNamedBeans = new LinkedHashMap<String, BeanDeclaration>();
		mScope = Scope.LOCAL;
	}
	
	void setName(String name)
	{
		assert name != null;
		assert name.length() > 0;
		
		mName = name;
	}
	
	public String getName()
	{
		return mName;
	}
	
	void setScope(Scope scope)
	{
		if (null == scope)
		{
			scope = Scope.LOCAL;
		}
		
		mScope = scope;
	}
	
	public Scope getScope()
	{
		return mScope;
	}
	
	void setElementInfo(ElementInfo elementInfo)
	{
		mElementInfo = elementInfo;
	}
	
	public ElementInfo getElementInfo()
	{
		return mElementInfo;
	}
	
	public Collection<String> getParameterNames()
	{
		return mParameters.keySet();
	}
	
	public Collection<Pattern> getParameterRegexps()
	{
		return mParameterRegexps;
	}
	
	public boolean containsParameter(String name)
	{
		assert name != null;
		assert name.length() > 0;

		// check if a fixed parameter exists with this name
		if (mParameters.containsKey(name))
		{
			return true;
		}
		
		// check if the name matches a parameter regular expression
		if (StringUtils.getMatchingRegexp(name, mParameterRegexps) != null)
		{
			return true;
		}
		
		return false;
	}
	
	public boolean hasParameterDefaults()
	{
		return mHasParameterDefaults;
	}
	
	public Collection<String> getParameterDefaultNames()
	{
		return mParameterDefaults;
	}
	
	public String[] getParameterDefaultValues(String name)
	{
		assert name != null;
		assert name.length() > 0;

		return mParameters.get(name);
	}
	
	public boolean hasParameterDefaultValues(String name)
	{
		assert name != null;
		assert name.length() > 0;

		if (mParameters.get(name) != null)
		{
			return true;
		}
		
		return false;
	}
	
	public Collection<BeanDeclaration> getBeans()
	{
		return mBeans;
	}

	public boolean hasNamedBeans()
	{
		return mNamedBeans != null && mNamedBeans.size() > 0;
	}

	public Collection<String> getBeanNames()
	{
		return mNamedBeans.keySet();
	}
	
	public BeanDeclaration getNamedBean(String name)
	throws EngineException
	{
		if (null == name)		throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty.");

		validateBeanName(name);
		
		return mNamedBeans.get(name);
	}
	
	public boolean containsNamedBean(String name)
	{
		if (null == name)		throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty.");

		return mNamedBeans.containsKey(name);
	}
	
	void validateBeanName(String name)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;
		
		if (!containsNamedBean(name))
		{
			throw new NamedSubmissionBeanUnknownException(mElementInfo.getDeclarationName(), mName, name);
		}
	}
	
	void addParameter(String name, String[] defaultValues)
	throws EngineException
	{
		assert mElementInfo != null;
		assert name != null;
		assert name.length() > 0;

		if (defaultValues != null && 0 == defaultValues.length)
		{
			defaultValues = null;
		}

		// check if the parameter doesn't exist already
		if (mParameters.containsKey(name))
		{
			throw new ParameterExistsException(mElementInfo.getDeclarationName(), name, mName);
		}
		
		// check if there's no conflicting input
		if (mElementInfo.containsInput(name))
		{
			throw new ParameterInputConflictException(mElementInfo.getDeclarationName(), name, mName);
		}
		
		// check if there's no conflicting incookie
		if (mElementInfo.containsIncookie(name))
		{
			throw new ParameterIncookieConflictException(mElementInfo.getDeclarationName(), name, mName);
		}

		// check if there's no conflicting file
		if (mFiles.contains(name))
		{
			throw new ParameterFileConflictException(mElementInfo.getDeclarationName(), name, mName);
		}
		
		// check if there's no conflicting global var
		if (mElementInfo.containsGlobalVar(name))
		{
			throw new ParameterGlobalVarConflictException(mElementInfo.getDeclarationName(), name, mName);
		}
		
		// check if there's no conflicting global cookie
		if (mElementInfo.containsGlobalCookie(name))
		{
			throw new ParameterGlobalCookieConflictException(mElementInfo.getDeclarationName(), name, mName);
		}
		
		// check the parameter regular expressions
		Matcher match_parameter = StringUtils.getMatchingRegexp(name, mParameterRegexps);
		if (match_parameter != null)
		{
			throw new ParameterParameterRegexpConflictException(mElementInfo.getDeclarationName(), name, mName, match_parameter.pattern().pattern());
		}
		
		// check the file regular expressions
		Matcher match_file = StringUtils.getMatchingRegexp(name, mFileRegexps);
		if (match_file != null)
		{
			throw new ParameterFileRegexpConflictException(mElementInfo.getDeclarationName(), name, mName, match_file.pattern().pattern());
		}
		
		if (defaultValues != null)
		{
			mHasParameterDefaults = true;
			mParameterDefaults.add(name);
			
			if (mElementInfo != null)
			{
				mElementInfo.setHasSubmissionDefaults(true);
			}
		}

		mParameters.put(name, defaultValues);
	}
	
	void addParameterRegexp(String pattern)
	throws EngineException
	{
		assert mElementInfo != null;
		assert pattern != null;
		assert pattern.length() > 0;
		
		if (!pattern.startsWith("^"))
		{
			pattern = "^"+pattern;
		}
		if (!pattern.endsWith("$"))
		{
			pattern = pattern+"$";
		}
								
		Pattern	compiled_pattern = null;
		try
		{
			compiled_pattern = Pattern.compile(pattern);
		}
		catch (PatternSyntaxException e)
		{
			throw new ParameterRegexpInvalidException(mElementInfo.getDeclarationName(), pattern, mName, e);
		}
		
		Matcher	matcher = null;
		
		// check if there's no conflicting input
		if ((matcher = StringUtils.getRegexpMatch(mElementInfo.getInputNames(), compiled_pattern)) != null)
		{
			throw new ParameterRegexpInputConflictException(mElementInfo.getDeclarationName(), pattern, mName, matcher.group());
		}
		
		// check if there's no conflicting incookie
		if ((matcher = StringUtils.getRegexpMatch(mElementInfo.getIncookieNames(), compiled_pattern)) != null)
		{
			throw new ParameterRegexpIncookieConflictException(mElementInfo.getDeclarationName(), pattern, mName, matcher.group());
		}
		
		// check if there are no parameter conflicts
		if ((matcher = StringUtils.getRegexpMatch(mParameters.keySet(), compiled_pattern)) != null)
		{
			throw new ParameterRegexpParameterConflictException(mElementInfo.getDeclarationName(), pattern, mName, matcher.group());
		}
		
		// check if there's no conflicting file
		if ((matcher = StringUtils.getRegexpMatch(mFiles, compiled_pattern)) != null)
		{
			throw new ParameterRegexpFileConflictException(mElementInfo.getDeclarationName(), pattern, mName, matcher.group());
		}
		
		// check if there's no conflicting global var
		if ((matcher = StringUtils.getRegexpMatch(mElementInfo.getGlobalVarNames(), compiled_pattern)) != null)
		{
			throw new ParameterRegexpGlobalVarConflictException(mElementInfo.getDeclarationName(), pattern, mName, matcher.group());
		}
		
		// check if there's no conflicting global cookie
		if ((matcher = StringUtils.getRegexpMatch(mElementInfo.getGlobalCookieNames(), compiled_pattern)) != null)
		{
			throw new ParameterRegexpGlobalCookieConflictException(mElementInfo.getDeclarationName(), pattern, mName, matcher.group());
		}

		mParameterRegexps.add(compiled_pattern);
	}
	
	void addBean(BeanDeclaration bean, String name)
	throws EngineException
	{
		assert bean != null;
		
		Class bean_class = null;
		try
		{
			bean_class = bean.getBeanClass();
		}
		catch (ClassNotFoundException e)
		{
			if (null == name)
			{
				throw new SubmissionBeanClassnameErrorException(mElementInfo.getDeclarationName(), mName, bean.getClassname(), e);
			}
			else
			{
				throw new NamedSubmissionBeanClassnameErrorException(mElementInfo.getDeclarationName(), mName, name, bean.getClassname(), e);
			}
		}
		
		try
		{

			Object		instance = bean_class.newInstance();
			Constrained	constrained = ConstrainedUtils.makeConstrainedInstance(instance);
			Set<String>	properties;
			if (bean.getGroupName() != null)
			{
				if (!(instance instanceof ValidatedConstrained))
				{
					if (null == name)
					{
						throw new SubmissionBeanGroupRequiresValidatedConstrainedException(mElementInfo.getDeclarationName(), mName, bean.getClassname(), bean.getGroupName());
					}
					else
					{
						throw new NamedSubmissionBeanGroupRequiresValidatedConstrainedException(mElementInfo.getDeclarationName(), mName, name, bean.getClassname(), bean.getGroupName());
					}
				}
				
				ValidatedConstrained validation = (ValidatedConstrained)instance;
				ValidationGroup group = validation.getGroup(bean.getGroupName());
				if (null == group)
				{
					if (null == name)
					{
						throw new SubmissionBeanGroupNotFoundException(mElementInfo.getDeclarationName(), mName, bean.getClassname(), bean.getGroupName());
					}
					else
					{
						throw new NamedSubmissionBeanGroupNotFoundException(mElementInfo.getDeclarationName(), mName, name, bean.getClassname(), bean.getGroupName());
					}
				}
				properties = new LinkedHashSet<String>();
				if (null == bean.getPrefix())
				{
					properties.addAll(group.getPropertyNames());
				}
				else
				{
					for (String property_name : (List<String>)group.getPropertyNames())
					{
						properties.add(bean.getPrefix()+property_name);
					}
				}
			}
			else
			{
				properties = BeanUtils.getPropertyNames(bean_class, null, null, bean.getPrefix());
			}
			
			for (String property : properties)
			{
				if (ConstrainedUtils.editConstrainedProperty(constrained, property, bean.getPrefix()))
				{
					if (ConstrainedUtils.fileConstrainedProperty(constrained, property, bean.getPrefix()))
					{
						if (!containsFile(property))
						{
							addFile(property);
						}
					}
					else
					{
						if (!containsParameter(property))
						{
							addParameter(property, null);
						}
					}
				}
			}
		}
		catch (IllegalAccessException e)
		{
			if (null == name)
			{
				throw new SubmissionBeanPropertiesErrorException(mElementInfo.getDeclarationName(), mName, bean.getClassname(), e);
			}
			else
			{
				throw new NamedSubmissionBeanPropertiesErrorException(mElementInfo.getDeclarationName(), mName, name, bean.getClassname(), e);
			}
		}
		catch (InstantiationException e)
		{
			if (null == name)
			{
				throw new SubmissionBeanPropertiesErrorException(mElementInfo.getDeclarationName(), mName, bean.getClassname(), e);
			}
			else
			{
				throw new NamedSubmissionBeanPropertiesErrorException(mElementInfo.getDeclarationName(), mName, name, bean.getClassname(), e);
			}
		}
		catch (BeanUtilsException e)
		{
			if (null == name)
			{
				throw new SubmissionBeanPropertiesErrorException(mElementInfo.getDeclarationName(), mName, bean.getClassname(), e);
			}
			else
			{
				throw new NamedSubmissionBeanPropertiesErrorException(mElementInfo.getDeclarationName(), mName, name, bean.getClassname(), e);
			}
		}
		
		if (name != null)
		{
			if (mNamedBeans.containsKey(name))
			{
				throw new NamedSubmissionBeanExistsException(mElementInfo.getDeclarationName(), mName, name);
			}
			
			mNamedBeans.put(name, bean);
		}
		mBeans.add(bean);
	}
	
	void addFile(String name)
	throws EngineException
	{
		assert name != null;
		assert name.length() > 0;
		
		// check if the file doesn't exist already
		if (mFiles.contains(name))
		{
			throw new FileExistsException(mElementInfo.getDeclarationName(), name, mName);
		}
		
		// check if there's no conflicting input
		if (mElementInfo.containsInput(name))
		{
			throw new FileInputConflictException(mElementInfo.getDeclarationName(), name, mName);
		}
		
		// check if there's no conflicting incookie
		if (mElementInfo.containsIncookie(name))
		{
			throw new FileIncookieConflictException(mElementInfo.getDeclarationName(), name, mName);
		}

		// check if there's no conflicting parameter
		if (mParameters.containsKey(name))
		{
			throw new FileParameterConflictException(mElementInfo.getDeclarationName(), name, mName);
		}
		
		// check if there's no conflicting global var
		if (mElementInfo.containsGlobalVar(name))
		{
			throw new FileGlobalVarConflictException(mElementInfo.getDeclarationName(), name, mName);
		}
		
		// check if there's no conflicting global cookie
		if (mElementInfo.containsGlobalCookie(name))
		{
			throw new FileGlobalCookieConflictException(mElementInfo.getDeclarationName(), name, mName);
		}
		
		// check the parameter regular expressions
		Matcher match_parameter = StringUtils.getMatchingRegexp(name, mParameterRegexps);
		if (match_parameter != null)
		{
			throw new FileParameterRegexpConflictException(mElementInfo.getDeclarationName(), name, mName, match_parameter.pattern().pattern());
		}
		
		// check the file regular expressions
		Matcher match_file = StringUtils.getMatchingRegexp(name, mFileRegexps);
		if (match_file != null)
		{
			throw new FileFileRegexpConflictException(mElementInfo.getDeclarationName(), name, mName, match_file.pattern().pattern());
		}
		
		mFiles.add(name);
	}
	
	void addFileRegexp(String pattern)
	throws EngineException
	{
		assert mElementInfo != null;
		assert pattern != null;
		assert pattern.length() > 0;
		
		if (!pattern.startsWith("^"))
		{
			pattern = "^"+pattern;
		}
		if (!pattern.endsWith("$"))
		{
			pattern = pattern+"$";
		}
		
		Pattern	compiled_pattern = null;
		try
		{
			compiled_pattern = Pattern.compile(pattern);
		}
		catch (PatternSyntaxException e)
		{
			throw new FileRegexpInvalidException(mElementInfo.getDeclarationName(), pattern, mName, e);
		}
		
		Matcher	matcher = null;
		
		// check if there's no conflicting input
		if ((matcher = StringUtils.getRegexpMatch(mElementInfo.getInputNames(), compiled_pattern)) != null)
		{
			throw new FileRegexpInputConflictException(mElementInfo.getDeclarationName(), pattern, mName, matcher.group());
		}
		
		// check if there's no conflicting incookie
		if ((matcher = StringUtils.getRegexpMatch(mElementInfo.getIncookieNames(), compiled_pattern)) != null)
		{
			throw new FileRegexpIncookieConflictException(mElementInfo.getDeclarationName(), pattern, mName, matcher.group());
		}
		
		// check if there are no parameter conflicts
		if ((matcher = StringUtils.getRegexpMatch(mParameters.keySet(), compiled_pattern)) != null)
		{
			throw new FileRegexpParameterConflictException(mElementInfo.getDeclarationName(), pattern, mName, matcher.group());
		}
		
		// check if there's no conflicting file
		if ((matcher = StringUtils.getRegexpMatch(mFiles, compiled_pattern)) != null)
		{
			throw new FileRegexpFileConflictException(mElementInfo.getDeclarationName(), pattern, mName, matcher.group());
		}
		
		// check if there's no conflicting global var
		if ((matcher = StringUtils.getRegexpMatch(mElementInfo.getGlobalVarNames(), compiled_pattern)) != null)
		{
			throw new FileRegexpGlobalVarConflictException(mElementInfo.getDeclarationName(), pattern, mName, matcher.group());
		}
		
		// check if there's no conflicting global cookie
		if ((matcher = StringUtils.getRegexpMatch(mElementInfo.getGlobalCookieNames(), compiled_pattern)) != null)
		{
			throw new FileRegexpGlobalCookieConflictException(mElementInfo.getDeclarationName(), pattern, mName, matcher.group());
		}
		
		mFileRegexps.add(compiled_pattern);
	}
	
	public boolean hasFiles()
	{
		return mFiles != null && mFiles.size() > 0;
	}
	
	public Collection<String> getFileNames()
	{
		return mFiles;
	}
	
	public Collection<Pattern> getFileRegexps()
	{
		return mFileRegexps;
	}
	
	public boolean containsFile(String name)
	{
		assert name != null;
		assert name.length() > 0;
		
		// check if a fixed file exists with this name
		if (mFiles.contains(name))
		{
			return true;
		}
		
		// check if the name matches a file regular expression
		if (StringUtils.getMatchingRegexp(name, mFileRegexps) != null)
		{
			return true;
		}
		
		return false;
	}
	
	public void setCancelContinuations(boolean cancelContinuations)
	{
		mCancelContinuations = cancelContinuations;
	}
	
	public boolean getCancelContinuations()
	{
		return mCancelContinuations;
	}
	
	public synchronized Submission clone()
	{
        Submission new_submission = null;
		try
		{
			new_submission = (Submission)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			// this should never happen
			Logger.getLogger("com.uwyn.rife.engine").severe(ExceptionUtils.getExceptionStackTrace(e));
		}
		
		new_submission.mElementInfo = null;
		
		if (mParameters != null)
		{
			new_submission.mParameters = new LinkedHashMap<String, String[]>(mParameters);
		}
		if (mParameterRegexps != null)
		{
			new_submission.mParameterRegexps = new ArrayList<Pattern>(mParameterRegexps);
		}
		if (mFiles != null)
		{
			new_submission.mFiles = new ArrayList<String>(mFiles);
		}
		if (mFileRegexps != null)
		{
			new_submission.mFileRegexps = new ArrayList<Pattern>(mFileRegexps);
		}
		if (mBeans != null)
		{
			new_submission.mBeans = new ArrayList<BeanDeclaration>(mBeans);
		}
		if (mNamedBeans != null)
		{
			new_submission.mNamedBeans = new LinkedHashMap<String, BeanDeclaration>(mNamedBeans);
		}
		
		return new_submission;
	}
}

