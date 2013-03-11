/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Element.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.ElementMemberFieldUncloneableException;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.tools.ObjectUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * This is a convenience abstract class that implements the {@link
 * ElementAware} interface and extends the {@link ElementSupport} class.
 * <p>There are no mandatory abstract methods to implement and all {@link
 * ElementSupport} methods are local.
 * <p>Additionally, the {@link #clone} method is implemented to provide as
 * good as possible default behaviour for continuations usage.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public abstract class Element extends ElementSupport implements ElementAware, Cloneable
{
	public final void noticeElement(ElementSupport element)
	{
		// an Element is already aware of itself
	}
	
	public void processElement()
	throws EngineException
	{
	}
	
	/**
	 * No-op default constructor that can only be used by extending classes.
	 *
	 * @since 1.0
	 */
	protected Element()
	{
	}
	
	/**
	 * Provides default cloning behavior by trying to make deep clones of all
	 * member variables, correctly handling primitives and collections.
	 * <p>Cloning is important when this element uses continuations since at
	 * each continuation step a clone will be made of the element instance to
	 * be able to still execute older continuations.
	 *
	 * @return a clone of this element instance
	 * @since 1.0
	 */
	public Object clone()
	throws CloneNotSupportedException
	{
		Element new_element = (Element)super.clone();
		
		// make a clone of all member fields that are references
		Field[] fields = this.getClass().getDeclaredFields();
		Class   type = null;
		Object  value = null;
		Object  value_new = null;
		for (Field field : fields)
		{
			field.setAccessible(true);
			type = field.getType();
			
			// primitive types don't have to be cloned
			if (type != boolean.class &&
				type != int.class &&
				type != long.class &&
				type != float.class &&
				type != double.class &&
				type != byte.class &&
				type != char.class)
			{
				// don't clone static and final fields
				if (Modifier.isStatic(field.getModifiers()) ||
					Modifier.isFinal(field.getModifiers()) ||
					Modifier.isTransient(field.getModifiers()))
				{
					continue;
				}
				
				// obtain the field's value
				try
				{
					value = field.get(this);
				}
				catch (Throwable e)
				{
					throw new ElementMemberFieldUncloneableException(getClass().getName(), field.getName(), e);
				}
				
				// null values are skipped
				if (null == value)
				{
					continue;
				}
				
				// clone it and set it as the value of this element's clone
				try
				{
					value_new = ObjectUtils.deepClone(value);
				}
				catch (CloneNotSupportedException e)
				{
					throw new ElementMemberFieldUncloneableException(getClass().getName(), field.getName(), e);
				}
				
				try
				{
					field.set(new_element, value_new);
				}
				catch (Throwable e)
				{
					throw new ElementMemberFieldUncloneableException(getClass().getName(), field.getName(), e);
				}
			}
		}

		return new_element;
	}
}


