/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: OutputValues.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import java.util.*;

import com.uwyn.rife.engine.exceptions.ElementOutjectionException;
import com.uwyn.rife.engine.exceptions.OutbeanOutjectionException;
import com.uwyn.rife.engine.exceptions.OutputOutjectionException;
import com.uwyn.rife.tools.ArrayUtils;
import com.uwyn.rife.tools.BeanPropertyProcessor;
import com.uwyn.rife.tools.BeanUtils;
import com.uwyn.rife.tools.exceptions.BeanUtilsException;
import com.uwyn.rife.tools.exceptions.LightweightError;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class OutputValues
{
	private ElementContext	mContext = null;
	private ElementAware	mElement = null;
	
	private Map<String, String[]>	mValues = null;
	private Map<String, String[]>	mFallbackValues = null;
	private Map<String, Method>		mOutputGetters = null;
	private Map<String, Method>		mOutbeanGetters = null;
	private Set<String>				mNonGetterOutputs = null;
	
	OutputValues(ElementContext context)
	{
		assert context != null;
		
		mContext = context;
		mElement = mContext.getElementSupport().getElementAware();
		mValues = new LinkedHashMap<String, String[]>();
		
		// try to obtain the output and outbean getters from the cache
		// if this wasn't possible, detect them and store them in the cache
		ElementInfo element_info = mContext.getElementInfo();
		mOutputGetters = element_info.getSite().getCachedOutputGetters(element_info.getId());
		mOutbeanGetters = element_info.getSite().getCachedOutbeanGetters(element_info.getId());
		if (null == mOutputGetters ||
			null == mOutbeanGetters)
		{
			mOutputGetters = null;
			mOutbeanGetters = null;
			
			try
			{
				BeanUtils.processProperties(BeanUtils.GETTERS, mElement.getClass(), null, null, null, new OutputValuesPropertyProcessor());

				if (null == mOutputGetters)
				{
					mOutputGetters = Collections.EMPTY_MAP;
				}
				if (null == mOutbeanGetters)
				{
					mOutbeanGetters = Collections.EMPTY_MAP;
				}
				element_info.getSite().putCachedOutputGetters(element_info.getId(), mOutputGetters);
				element_info.getSite().putCachedOutbeanGetters(element_info.getId(), mOutbeanGetters);
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
		if (mOutputGetters != null && mOutputGetters.size() > 0)
		{
			try
			{
				for (Map.Entry<String, Method> output_getter : mOutputGetters.entrySet())
				{
					value = output_getter.getValue().invoke(mElement, (Object[])null);
					if (value != null)
					{
						mContext.fireOutputValueSet(output_getter.getKey(), ArrayUtils.createStringArray(value, null));
					}
				}
			}
			catch (LightweightError e)
			{
				throw e;
			}
			catch (Exception e)
			{
				throw new OutputOutjectionException(mContext.getElementInfo().getDeclarationName(), mElement.getClass(), e);
			}
		}
		
		if (mOutbeanGetters != null && mOutbeanGetters.size() > 0)
		{
			BeanDeclaration	bean = null;
			for (Map.Entry<String, Method> outbean_getter : mOutbeanGetters.entrySet())
			{
				try
				{
					bean = mContext.getElementInfo().getNamedOutbeanInfo(outbean_getter.getKey());
					value = outbean_getter.getValue().invoke(mElement, (Object[])null);
					if (value != null)
					{
						Map<String, String[]> bean_values = mContext.collectOutputBeanValues(value, bean.getPrefix(), null);
						for (Map.Entry<String, String[]> entry : bean_values.entrySet())
						{
							if (entry.getValue() != null)
							{
								mContext.fireOutputValueSet(entry.getKey(), entry.getValue());
							}
						}
					}
				}
				catch (LightweightError e)
				{
					throw e;
				}
				catch (Exception e)
				{
					throw new OutbeanOutjectionException(mContext.getElementInfo().getDeclarationName(), mElement.getClass(), outbean_getter.getKey(), e);
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
			if (mOutputGetters != null && mOutputGetters.size() > 0)
			{
				try
				{
					for (Map.Entry<String, Method> output_getter : mOutputGetters.entrySet())
					{
						if (childtrigger_names.contains(output_getter.getKey()))
						{
							value = output_getter.getValue().invoke(mElement, (Object[])null);
							if (value != null)
							{
								mContext.triggerChild(output_getter.getKey(), ArrayUtils.createStringArray(value, null));
							}
						}
					}
				}
				catch (LightweightError e)
				{
					throw e;
				}
				catch (Exception e)
				{
					throw new OutputOutjectionException(mContext.getElementInfo().getDeclarationName(), mElement.getClass(), e);
				}
			}

			if (mOutbeanGetters != null && mOutbeanGetters.size() > 0)
			{
				BeanDeclaration	bean = null;
				for (Map.Entry<String, Method> outbean_getter : mOutbeanGetters.entrySet())
				{
					try
					{
						bean = mContext.getElementInfo().getNamedOutbeanInfo(outbean_getter.getKey());
						value = outbean_getter.getValue().invoke(mElement, (Object[])null);
						if (value != null)
						{
							Map<String, String[]> bean_values = mContext.collectOutputBeanValues(value, bean.getPrefix(), null);
							for (Map.Entry<String, String[]> entry : bean_values.entrySet())
							{
								if (entry.getValue() != null)
								{
									mContext.triggerChild(entry.getKey(), entry.getValue());
								}
							}
						}
					}
					catch (LightweightError e)
					{
						throw e;
					}
					catch (Exception e)
					{
						throw new OutbeanOutjectionException(mContext.getElementInfo().getDeclarationName(), mElement.getClass(), outbean_getter.getKey(), e);
					}
				}
			}
		}
	}
	
	void put(String name, String[] values)
	{
		if (null == name) return;
		
		if (mOutputGetters != null)
		{
			if (null == values)
			{
				if (mNonGetterOutputs != null)
				{
					mNonGetterOutputs.remove(name);
				}
			}
			else
			{
				if (null == mNonGetterOutputs)
				{
					mNonGetterOutputs = new HashSet<String>();
				}

				mNonGetterOutputs.add(name);
			}
		}

		mValues.put(name, values);
	}
	
	void putFallback(String name, String[] values)
	{
		if (null == name) return;
		
		if (null == mFallbackValues)
		{
			mFallbackValues = new LinkedHashMap<String, String[]>();
		}

		mFallbackValues.put(name, values);
	}
	
	String[] get(String name)
	{
		if (null == name) return null;
		
		String[] outputs = mValues.get(name);
		if (null == outputs &&
			mContext.getElementInfo().hasOutputDefaults())
		{
			outputs = mContext.getElementInfo().getOutputDefaultValues(name);
		}
		
		if (null == outputs &&
			mOutputGetters != null &&
			mOutputGetters.containsKey(name))
		{
			try
			{
				Object value = mOutputGetters.get(name).invoke(mElement, (Object[])null);
				if (value != null)
				{
					outputs = ArrayUtils.createStringArray(value, null);
				}
			}
			catch (Exception e)
			{
				throw new OutputOutjectionException(mContext.getElementInfo().getDeclarationName(), mElement.getClass(), e);
			}
		}
		
		if (null == outputs &&
			mFallbackValues != null)
		{
			outputs = mFallbackValues.get(name);
		}
		
		return outputs;
	}
	
	Map<String, String[]> aggregateValues()
	{
		Map<String, String[]> entry_map = null;

		// handle default output values
		if (mContext.getElementInfo().hasOutputDefaults())
		{
			if (null == entry_map)
			{
				entry_map = new LinkedHashMap<String, String[]>();
			}
			
			for (Map.Entry<String, String[]> output_defaults_entry : mContext.getElementInfo().getOutputEntries())
			{
				if (output_defaults_entry.getValue() != null)
				{
					entry_map.put(output_defaults_entry.getKey(), output_defaults_entry.getValue());
				}
			}
		}
		
		// handle outbeans getter outjection
		if (mOutbeanGetters != null && mOutbeanGetters.size() > 0)
		{
			BeanDeclaration	bean = null;
			Object			value = null;
			for (Map.Entry<String, Method> outbean_getter : mOutbeanGetters.entrySet())
			{
				try
				{
					bean = mContext.getElementInfo().getNamedOutbeanInfo(outbean_getter.getKey());
					value = outbean_getter.getValue().invoke(mElement, (Object[])null);
					if (value != null)
					{
						if (null == entry_map)
						{
							entry_map = new LinkedHashMap<String, String[]>();
						}
						
						Map<String, String[]> bean_values = mContext.collectOutputBeanValues(value, bean.getPrefix(), null);
						entry_map.putAll(bean_values);
					}
				}
				catch (Exception e)
				{
					throw new OutbeanOutjectionException(mContext.getElementInfo().getDeclarationName(), mElement.getClass(), outbean_getter.getKey(), e);
				}
			}
		}
		
		// handle output getter outjection
		if (mOutputGetters != null && mOutputGetters.size() > 0)
		{
			Object	value = null;
			try
			{
				for (Map.Entry<String, Method> output_getter : mOutputGetters.entrySet())
				{
					if (mNonGetterOutputs != null &&
						mNonGetterOutputs.contains(output_getter.getKey()))
					{
						continue;
					}
					
					value = output_getter.getValue().invoke(mElement, (Object[])null);
					if (value != null)
					{
						if (null == entry_map)
						{
							entry_map = new LinkedHashMap<String, String[]>();
						}

						entry_map.put(output_getter.getKey(), ArrayUtils.createStringArray(value, null));
					}
				}
			}
			catch (Exception e)
			{
				throw new OutputOutjectionException(mContext.getElementInfo().getDeclarationName(), mElement.getClass(), e);
			}
		}
		
		if (mFallbackValues != null && mFallbackValues.size() > 0)
		{
			if (null == entry_map)
			{
				entry_map = new LinkedHashMap<String, String[]>();
			}
			
			for (Map.Entry<String, String[]> fallback_output : mFallbackValues.entrySet())
			{
				if (!entry_map.containsKey(fallback_output.getKey()))
				{
					entry_map.put(fallback_output.getKey(), fallback_output.getValue());
				}
			}
		}

		// handle outputs that have been explicitly set
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
	
	private class OutputValuesPropertyProcessor implements BeanPropertyProcessor
	{
		private ElementInfo			mElementInfo;
		private Collection<String>	mNamesOutputs;
		private Collection<String>	mNamesGlobalvars;
		private Collection<String>	mNamesOutbeans;
		private Collection<String>	mNamesGlobalbeans;
		
		OutputValuesPropertyProcessor()
		{
			mElementInfo = mContext.getElementInfo();
			mNamesOutputs = mElementInfo.getOutputNames();
			mNamesGlobalvars = mElementInfo.getGlobalVarNames();
			mNamesOutbeans = mElementInfo.getNamedOutbeanNames();
			mNamesGlobalbeans = mElementInfo.getNamedGlobalBeanNames();
		}
		
		public boolean gotProperty(String name, PropertyDescriptor descriptor)
		throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
		{
			Method method = descriptor.getReadMethod();
            if (mNamesOutputs.contains(name) ||
				mNamesGlobalvars.contains(name))
			{
				if (null == mOutputGetters)
				{
					mOutputGetters = new LinkedHashMap<String, Method>();
				}

                method.setAccessible(true);
				mOutputGetters.put(name, method);
			}
			else if (mNamesOutbeans.contains(name) ||
					 mNamesGlobalbeans.contains(name))
			{
				if (null == mOutbeanGetters)
				{
					mOutbeanGetters = new LinkedHashMap<String, Method>();
				}
				
				BeanDeclaration bean = mElementInfo.getNamedOutbeanInfo(name);
				try
				{
					if (bean.getBeanClass().isAssignableFrom(method.getReturnType()))
					{
                        method.setAccessible(true);
						mOutbeanGetters.put(name, method);
					}
				}
				catch (ClassNotFoundException e)
				{
					throw new OutbeanOutjectionException(mContext.getElementInfo().getDeclarationName(), mElement.getClass(), name, e);
				}
			}
			
			return true;
		}
	}
}
