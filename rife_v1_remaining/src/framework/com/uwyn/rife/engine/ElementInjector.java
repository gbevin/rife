/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementInjector.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.*;

import com.uwyn.rife.tools.BeanPropertyProcessor;
import com.uwyn.rife.tools.BeanUtils;
import com.uwyn.rife.tools.Convert;
import com.uwyn.rife.tools.exceptions.BeanUtilsException;
import com.uwyn.rife.tools.exceptions.ConversionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

class ElementInjector
{
	private ElementContext	mContext = null;
	private ElementSupport	mElement = null;
	private ElementAware	mElementAware = null;
	private ElementInfo		mElementInfo = null;
	
	private Map<String, Method>		mPropertySetters = null;
	private Map<String, Method>		mIncookieSetters = null;
	private Map<String, Method>		mInputSetters = null;
	private Map<String, Method>		mInbeanSetters = null;
	private SubmissionSettersCache	mSubmissionSettersCache = null;

	ElementInjector(ElementContext context)
	{
		assert context != null;
		
		mContext = context;
		mElement = mContext.getElementSupport();
		mElementAware = mElement.getElementAware();
		mElementInfo = context.getElementInfo();
		
		// try to obtain the element's setter methods from the cache
		mPropertySetters = mElementInfo.getSite().getCachedPropertySetters(mElementInfo.getId());
		mIncookieSetters = mElementInfo.getSite().getCachedIncookieSetters(mElementInfo.getId());
		mInputSetters = mElementInfo.getSite().getCachedInputSetters(mElementInfo.getId());
		mInbeanSetters = mElementInfo.getSite().getCachedInbeanSetters(mElementInfo.getId());
		mSubmissionSettersCache = mElementInfo.getSite().getSubmissionSettersCache(mElementInfo.getId());
		
		// if at least one of the setter types couldn't be obtained, re-detect them all
		if (null == mPropertySetters ||
			null == mIncookieSetters ||
			null == mInputSetters ||
			null == mInbeanSetters ||
			null == mSubmissionSettersCache)
		{
			try
			{
				if (null == mSubmissionSettersCache)
				{
					mSubmissionSettersCache = new SubmissionSettersCache();
				}

				BeanUtils.processProperties(BeanUtils.SETTERS, mElementAware.getClass(), null, null, null, new ElementInjectorPropertyProcessor());
				
				if (null == mPropertySetters)
				{
					mPropertySetters = Collections.EMPTY_MAP;
				}
				if (null == mIncookieSetters)
				{
					mIncookieSetters = Collections.EMPTY_MAP;
				}
				if (null == mInputSetters)
				{
					mInputSetters = Collections.EMPTY_MAP;
				}
				if (null == mInbeanSetters)
				{
					mInbeanSetters = Collections.EMPTY_MAP;
				}

				// store the this element's setters in the cache
				mElementInfo.getSite().putCachedPropertySetters(mElementInfo.getId(), mPropertySetters);
				mElementInfo.getSite().putCachedIncookieSetters(mElementInfo.getId(), mIncookieSetters);
				mElementInfo.getSite().putCachedInputSetters(mElementInfo.getId(), mInputSetters);
				mElementInfo.getSite().putCachedInbeanSetters(mElementInfo.getId(), mInbeanSetters);
				mElementInfo.getSite().putSubmissionSettersCache(mElementInfo.getId(), mSubmissionSettersCache);
			}
			catch (BeanUtilsException e)
			{
				throw new ElementInjectionException(mElementInfo.getDeclarationName(), mElementAware.getClass(), e);
			}
		}
	}
	
	void performInjection(String submissionName)
	{
		Submission submission = null;
		if (submissionName != null)
		{
			submission = mElementInfo.getSubmission(submissionName);
		}
		
		// inject the element properties
		for (Map.Entry<String, Method> setter : mPropertySetters.entrySet())
		{
			Method write = setter.getValue();
			Class type = write.getParameterTypes()[0];
			try
			{
				write.invoke(mElementAware, Convert.toType(mElementInfo.getProperty(setter.getKey()), type));
			}
			catch (Exception e)
			{
				throw new PropertyInjectionException(mElementInfo.getDeclarationName(), mElementAware.getClass(), setter.getKey(), e);
			}
		}
		
		// inject the element parameters vars
		if (submission != null)
		{
			Map<String, Method> param_setters = mSubmissionSettersCache.getCachedSubmissionparamSetters(submissionName);
			if (param_setters != null)
			{
				for (Map.Entry<String, Method> setter : param_setters.entrySet())
				{
					Method write = setter.getValue();
					Class type = write.getParameterTypes()[0];
					
					try
					{
						if (type.isArray())
						{
							String[] values = mElement.getParameterValues(setter.getKey());
							if (values != null)
							{
								write.invoke(mElementAware, new Object[] {Convert.toType(values, type)});
							}
						}
						else
						{
							String value = mElement.getParameter(setter.getKey());
							if (value != null)
							{
								write.invoke(mElementAware, new Object[] {Convert.toType(value, type)});
							}
						}
					}
					catch (ConversionException e)
					{
						try
						{
							String[] values = submission.getParameterDefaultValues(setter.getKey());
							if (values != null && type.isArray())
							{
								if (values != null)
								{
									write.invoke(mElementAware, new Object[] {Convert.toType(values, type)});
								}
							}
							else if (values != null && values.length > 0)
							{
								String value = values[0];
								if (value != null)
								{
									write.invoke(mElementAware, new Object[] {Convert.toType(value, type)});
								}
							}
							else if (!type.isPrimitive())
							{
								write.invoke(mElementAware, new Object[] {null});
							}
						}
						catch (Exception e2)
						{
							throw new ParameterInjectionException(mElementInfo.getDeclarationName(), mElementAware.getClass(), submissionName, setter.getKey(), e2);
						}
					}
					catch (Exception e)
					{
						throw new ParameterInjectionException(mElementInfo.getDeclarationName(), mElementAware.getClass(), submissionName, setter.getKey(), e);
					}
				}
			}
		}

		// inject the element incookies and global cookies
		for (Map.Entry<String, Method> setter : mIncookieSetters.entrySet())
		{
			Method write = setter.getValue();
			Class type = write.getParameterTypes()[0];
			
			try
			{
				write.invoke(mElementAware, new Object[] {Convert.toType(mElement.getCookieValue(setter.getKey()), type)});
			}
			catch (ConversionException e)
			{
				try
				{
					String value = mElementInfo.getIncookieDefaultValue(setter.getKey());
					if (value != null)
					{
						write.invoke(mElementAware, new Object[] {Convert.toType(value, type)});
					}
					else if (!type.isPrimitive())
					{
						write.invoke(mElementAware, new Object[] {null});
					}
				}
				catch (Exception e2)
				{
					throw new IncookieInjectionException(mElementInfo.getDeclarationName(), mElementAware.getClass(), setter.getKey(), e2);
				}
			}
			catch (Exception e)
			{
				throw new IncookieInjectionException(mElementInfo.getDeclarationName(), mElementAware.getClass(), setter.getKey(), e);
			}
		}

		// inject the element inputs and global vars
		for (Map.Entry<String, Method> setter : mInputSetters.entrySet())
		{
			Method write = setter.getValue();
			Class type = write.getParameterTypes()[0];
			
			try
			{
				if (type.isArray())
				{
					write.invoke(mElementAware, new Object[] {Convert.toType(mElement.getInputValues(setter.getKey()), type)});
				}
				else
				{
					write.invoke(mElementAware, new Object[] {Convert.toType(mElement.getInput(setter.getKey()), type)});
				}
			}
			catch (ConversionException e)
			{
				try
				{
					String[] values = mElementInfo.getInputDefaultValues(setter.getKey());
					if (values != null && type.isArray())
					{
						if (values != null)
						{
							write.invoke(mElementAware, new Object[] {Convert.toType(values, type)});
						}
					}
					else if (values != null && values.length > 0)
					{
						String value = values[0];
						if (value != null)
						{
							write.invoke(mElementAware, new Object[] {Convert.toType(value, type)});
						}
					}
					else if (!type.isPrimitive())
					{
						write.invoke(mElementAware, new Object[] {null});
					}
				}
				catch (Exception e2)
				{
					throw new InputInjectionException(mElementInfo.getDeclarationName(), mElementAware.getClass(), setter.getKey(), e2);
				}
			}
			catch (Exception e)
			{
				throw new InputInjectionException(mElementInfo.getDeclarationName(), mElementAware.getClass(), setter.getKey(), e);
			}
		}
		
		// inject the element named inbeans
		for (Map.Entry<String, Method> setter : mInbeanSetters.entrySet())
		{
			Method write = setter.getValue();
			Class type = write.getParameterTypes()[0];
			
			try
			{
				write.invoke(mElementAware, new Object[] {Convert.toType(mElement.getNamedInputBean(setter.getKey()), type)});
			}
			catch (Exception e)
			{
				throw new NamedInbeanInjectionException(mElementInfo.getDeclarationName(), mElementAware.getClass(), setter.getKey(), e);
			}
		}

		// inject the element named submission beans
		if (submission != null)
		{
			Map<String, Method> bean_setters = mSubmissionSettersCache.getCachedSubmissionbeanSetters(submissionName);
			if (bean_setters != null)
			{
				for (Map.Entry<String, Method> setter : bean_setters.entrySet())
				{
					Method write = setter.getValue();
					Class type = write.getParameterTypes()[0];
					
					try
					{
						write.invoke(mElementAware, new Object[] {Convert.toType(mElement.getNamedSubmissionBean(submissionName, setter.getKey()), type)});
					}
					catch (Exception e)
					{
						throw new NamedSubmissionBeanInjectionException(mElementInfo.getDeclarationName(), mElementAware.getClass(), setter.getKey(), e);
					}
				}
			}
			
			// inject the element submission file uploads
			Map<String, Method> file_setters = mSubmissionSettersCache.getCachedUploadedfileSetters(submissionName);
			if (file_setters != null)
			{
				for (Map.Entry<String, Method> setter : file_setters.entrySet())
				{
					Method write = setter.getValue();
					
					UploadedFile file = mContext.getUploadedFile(setter.getKey());
					if (file != null)
					{
						try
						{
							write.invoke(mElementAware, new Object[] {file});
						}
						catch (Exception e)
						{
							throw new UploadedSubmissionFilesInjectionException(mElementInfo.getDeclarationName(), mElementAware.getClass(), setter.getKey(), e);
						}
					}
				}
			}
		}
	}
	
	private class ElementInjectorPropertyProcessor implements BeanPropertyProcessor
	{
		private Collection<String>	mPropertynames;
		private Collection<String>	mNamesIncookies;
		private Collection<String>	mNamesInputs;
		private Collection<String>	mNamesInbeans;

		ElementInjectorPropertyProcessor()
		{
			mPropertynames = mElementInfo.getInjectablePropertyNames();
			
			if (!mElementInfo.hasIncookies() &&
				!mElementInfo.hasGlobalCookies())
			{
				mNamesIncookies = null;
			}
			else
			{
				final Collection<String> names_globalcookies = mElementInfo.getGlobalCookieNames();
				if (names_globalcookies != null &&
					names_globalcookies.size() > 0)
				{
					mNamesIncookies = new ArrayList<String>(mElementInfo.getIncookieNames());
					mNamesIncookies.addAll(names_globalcookies);
				}
				else
				{
					mNamesIncookies = mElementInfo.getIncookieNames();
				}
			}
			
			if (!mElementInfo.hasInputs() &&
				!mElementInfo.hasGlobalVars())
			{
				mNamesInputs = null;
			}
			else
			{
				Collection<String> names_globalvars = mElementInfo.getGlobalVarNames();
				if (names_globalvars != null &&
					names_globalvars.size() > 0)
				{
					mNamesInputs = new ArrayList<String>(mElementInfo.getInputNames());
					mNamesInputs.addAll(names_globalvars);
				}
				else
				{
					mNamesInputs = mElementInfo.getInputNames();
				}
			}
			
			if (mElementInfo.hasNamedInbeans())
			{
				mNamesInbeans = mElementInfo.getNamedInbeanNames();
			}
			else
			{
				mNamesInbeans = null;
			}
		}
		
		public boolean gotProperty(String name, PropertyDescriptor descriptor)
		throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
		{
			Method write = descriptor.getWriteMethod();
			Class type = write.getParameterTypes()[0];
			
			// detect the element properties
			if (mPropertynames != null &&
				mPropertynames.contains(name))
			{
				if (null == mPropertySetters)
				{
					mPropertySetters = new LinkedHashMap<String, Method>();
				}
                write.setAccessible(true);
				mPropertySetters.put(name, write);
			}
			// detect the element incookies and global cookies
			else if (mNamesIncookies != null &&
					 mNamesIncookies.contains(name))
			{
				if (null == mIncookieSetters)
				{
					mIncookieSetters = new LinkedHashMap<String, Method>();
				}
                write.setAccessible(true);
				mIncookieSetters.put(name, write);
			}
			// detect the element inputs and global vars
			else if (mNamesInputs != null &&
					 mNamesInputs.contains(name))
			{
				if (null == mInputSetters)
				{
					mInputSetters = new LinkedHashMap<String, Method>();
				}
                write.setAccessible(true);
				mInputSetters.put(name, write);
			}
			// detect the element named inbeans
			else if (mNamesInbeans != null &&
					 mNamesInbeans.contains(name))
			{
				if (null == mInbeanSetters)
				{
					mInbeanSetters = new LinkedHashMap<String, Method>();
				}
                write.setAccessible(true);
				mInbeanSetters.put(name, write);
			}
			else
			{
				for (Submission submission : mElementInfo.getSubmissions())
				{
					if (submission.containsParameter(name))
					{
						Map<String, Method> param_setters = mSubmissionSettersCache.getCachedSubmissionparamSetters(submission.getName());
						if (null == param_setters)
						{
							param_setters = new LinkedHashMap<String, Method>();
							mSubmissionSettersCache.putCachedSubmissionparamSetters(submission.getName(), param_setters);
						}
                        write.setAccessible(true);
						param_setters.put(name, write);
					}
					else if (submission.containsNamedBean(name))
					{
						Map<String, Method> bean_setters = mSubmissionSettersCache.getCachedSubmissionbeanSetters(submission.getName());
						if (null == bean_setters)
						{
							bean_setters = new LinkedHashMap<String, Method>();
							mSubmissionSettersCache.putCachedSubmissionbeanSetters(submission.getName(), bean_setters);
						}
                        write.setAccessible(true);
						bean_setters.put(name, write);
					}
					else if (submission.containsFile(name) &&
							 UploadedFile.class.isAssignableFrom(type))
					{
						Map<String, Method> file_setters = mSubmissionSettersCache.getCachedUploadedfileSetters(submission.getName());
						if (null == file_setters)
						{
							file_setters = new LinkedHashMap<String, Method>();
							mSubmissionSettersCache.putCachedUploadedfileSetters(submission.getName(), file_setters);
						}
                        write.setAccessible(true);
						file_setters.put(name, write);
					}
				}
			}
			
			return true;
		}
	}
}
