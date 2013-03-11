/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: OutcookieValues.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.ElementOutjectionException;
import com.uwyn.rife.engine.exceptions.OutcookieOutjectionException;
import com.uwyn.rife.engine.exceptions.OutputOutjectionException;
import com.uwyn.rife.tools.BeanPropertyProcessor;
import com.uwyn.rife.tools.BeanUtils;
import com.uwyn.rife.tools.Convert;
import com.uwyn.rife.tools.exceptions.BeanUtilsException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.Cookie;

class OutcookieValues
{
	private ElementContext	mContext = null;
	private ElementAware	mElement = null;
	
	private Map<String, String>	mValues = null;
	private Map<String, Method>	mOutcookieGetters = null;
	
	OutcookieValues(ElementContext context)
	{
		assert context != null;
		
		mContext = context;
		mContext.getElementInfo().getSite();
		mElement = mContext.getElementSupport().getElementAware();
		mValues = new LinkedHashMap<String, String>();
		
		// try to obtain the outcookie getters from the cache
		// if this wasn't possible, detect them and store them in the cache
		final ElementInfo element_info = mContext.getElementInfo();
		mOutcookieGetters = element_info.getSite().getCachedOutcookieGetters(element_info.getId());
		if (null == mOutcookieGetters)
		{
			final Collection<String> names_outcookies = element_info.getOutcookieNames();
			final Collection<String> names_globalcookies = element_info.getGlobalCookieNames();
			try
			{
				BeanUtils.processProperties(BeanUtils.GETTERS, mElement.getClass(), null, null, null, new BeanPropertyProcessor() {
						public boolean gotProperty(String name, PropertyDescriptor descriptor)
						throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
						{
							Method method = descriptor.getReadMethod();
							if (names_outcookies.contains(name) ||
								names_globalcookies.contains(name))
							
							{
								if (null == mOutcookieGetters)
								{
									mOutcookieGetters = new LinkedHashMap<String, Method>();
								}
								
								mOutcookieGetters.put(name, method);
							}
							
							return true;
						}
					});
				
				if (null == mOutcookieGetters)
				{
					mOutcookieGetters = Collections.EMPTY_MAP;
				}
				element_info.getSite().putCachedOutcookieGetters(element_info.getId(), mOutcookieGetters);
			}
			catch (BeanUtilsException e)
			{
				throw new ElementOutjectionException(mContext.getElementInfo().getDeclarationName(), mElement.getClass(), e);
			}
		}
	}
	
	void processGetters()
	{
		Object	value = null;
		Cookie	cookie = null;
		if (mOutcookieGetters != null && mOutcookieGetters.size() > 0)
		{
			for (Map.Entry<String, Method> outcookie_getter : mOutcookieGetters.entrySet())
			{
				if (!mValues.containsKey(outcookie_getter.getKey()))
				{
					value = getPropertyValue(outcookie_getter.getKey());
					if (value != null)
					{
						cookie = new Cookie(outcookie_getter.getKey(), Convert.toString(value));
						cookie.setPath("");
						mContext.setCookieRaw(cookie);
					}
				}
			}
		}
	}
	
	void processGetterChildTriggers()
	{
		if (mContext.getElementState().inInheritanceStructure())
		{
			Collection<String> 	childtrigger_names = mContext.getElementInfo().getChildTriggerNames();
			
			Object	value = null;
			if (mOutcookieGetters != null && mOutcookieGetters.size() > 0)
			{
				for (Map.Entry<String, Method> outcookie_getter : mOutcookieGetters.entrySet())
				{
					if (childtrigger_names.contains(outcookie_getter.getKey()))
					{
						value = getPropertyValue(outcookie_getter.getKey());
						if (value != null)
						{
							mContext.triggerChild(outcookie_getter.getKey(), new String[] {Convert.toString(value)});
						}
					}
				}
			}
		}
	}
	
	void put(String name, String value)
	{
		if (null == name) return;
		
		if (mOutcookieGetters != null)
		{
			mOutcookieGetters.remove(name);
		}
		
		mValues.put(name, value);
	}
	
	String get(String name)
	{
		if (null == name) return null;
		
		String outcookie = mValues.get(name);
		if (null == outcookie &&
			mContext.getElementInfo().hasOutcookieDefaults())
		{
			outcookie = mContext.getElementInfo().getOutcookieDefaultValue(name);
		}
		
		if (null == outcookie)
		{
			outcookie = getPropertyValue(name);
		}
		
		return outcookie;
	}

	private String getPropertyValue(String name)
	throws OutputOutjectionException
	{
		if (null == mOutcookieGetters)
		{
			return null;
		}
		
		Method method = mOutcookieGetters.get(name);
		if (method != null)
		{
			try
			{
				Object value = method.invoke(mElement, (Object[])null);
				if (value != null)
				{
					return Convert.toString(value);
				}
			}
			catch (Exception e)
			{
				throw new OutcookieOutjectionException(mContext.getElementInfo().getDeclarationName(), mElement.getClass(), e);
			}
		}
		
		return null;
	}
	
	boolean contains(String name)
	{
		if (null == name) return false;
		
		if (mValues.containsKey(name))
		{
			return true;
		}
		
		if (getPropertyValue(name) != null)
		{
			return true;
		}
		
		return false;
	}
	
	Map<String, String> aggregateValues()
	{
		Map<String, String> entry_map = null;

		// handle default outcookie values
		if (mContext.getElementInfo().hasOutcookieDefaults())
		{
			if (null == entry_map)
			{
				entry_map = new LinkedHashMap<String, String>();
			}
			
			for (Map.Entry<String, String> outcookie_defaults_entry : mContext.getElementInfo().getOutcookieEntries())
			{
				if (outcookie_defaults_entry.getValue() != null)
				{
					entry_map.put(outcookie_defaults_entry.getKey(), outcookie_defaults_entry.getValue());
				}
			}
		}
		
		// handle outcookie getter outjection
		if (mOutcookieGetters != null && mOutcookieGetters.size() > 0)
		{
			Object	value = null;
			try
			{
				for (Map.Entry<String, Method> outcookie_getter : mOutcookieGetters.entrySet())
				{
					value = outcookie_getter.getValue().invoke(mElement, (Object[])null);
					if (value != null)
					{
						if (null == entry_map)
						{
							entry_map = new LinkedHashMap<String, String>();
						}

						entry_map.put(outcookie_getter.getKey(), Convert.toString(value));
					}
				}
			}
			catch (Exception e)
			{
				throw new OutcookieOutjectionException(mContext.getElementInfo().getDeclarationName(), mElement.getClass(), e);
			}
		}
		
		// handle outcookies that have been explicitly set
		if (null == entry_map)
		{
			return Collections.unmodifiableMap(mValues);
		}
		else
		{
			entry_map.putAll(mValues);
		}
		
		return Collections.unmodifiableMap(entry_map);
	}
}
