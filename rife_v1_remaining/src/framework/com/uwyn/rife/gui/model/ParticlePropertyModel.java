/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParticlePropertyModel.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.uwyn.rife.gui.model.exceptions.ParticlePropertyCreationException;
import com.uwyn.rife.gui.model.exceptions.GuiModelException;

public abstract class ParticlePropertyModel
{
	private ParticleModel	mParticle = null;
	private String			mName = null;
	private String			mDescription = null;

	private ParticlePropertyModel()
	{
	}

	protected ParticlePropertyModel(ParticleModel particle, String name)
	throws GuiModelException
	{
		assert particle != null;
		assert name != null;
		
		setName(name);
		try
		{
			particle.addProperty(this);
		}
		catch (GuiModelException e)
		{
			throw new ParticlePropertyCreationException(e);
		}
	}

	public ParticleModel getParticle()
	{
		synchronized (this)
		{
			return mParticle;
		}
	}

	protected void setParticle(ParticleModel particle)
	{
		synchronized (this)
		{
			mParticle = particle;
		}
	}

	public String getDescription()
	{
		synchronized (this)
		{
			return mDescription;
		}
	}

	public void setDescription(String description)
	{
		synchronized (this)
		{
			mDescription = description;
		}
	}

	public boolean equals(Object object)
	{
		if (object instanceof ParticlePropertyModel)
		{
			ParticlePropertyModel property = (ParticlePropertyModel)object;
			if (property.getParticle() == getParticle() &&
				property.getClass() == getClass() &&
				property.getName().equals(getName()))
			{
				return true;
			}
		}

		return false;
	}

	public int hashCode()
	{
		int result = 17;

		result = 37*result + getParticle().hashCode();
		result = 37*result + getClass().hashCode();
		result = 37*result + getName().hashCode();

		return result;
	}

	public String getName()
	{
		synchronized (this)
		{
			return mName;
		}
	}

	protected void setName(String name)
	{
		assert name != null;
		assert name.length() > 0;

		synchronized (this)
		{
			mName = name;
		}
	}

	public final boolean isValidName(String name)
	{
		if (null == name)		throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty.");

		ParticlePropertyModel temp_property = findConflictingProperty(getParticle(), getClass(), name);

		if (temp_property == this ||
			temp_property == null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static boolean isValidName(ParticleModel particle, Class type, String name)
	{
		if (null == particle)	throw new IllegalArgumentException("particle can't be null.");
		if (null == type)		throw new IllegalArgumentException("type can't be null.");
		if (null == name)		throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty.");

		ParticlePropertyModel temp_property = null;
        Method find_conflicting_property_method = null;

		Class class_to_search = type;

        while (class_to_search != null)
		{
			try
			{
				find_conflicting_property_method = class_to_search.getDeclaredMethod("findConflictingProperty", new Class[] {ParticleModel.class, Class.class, String.class});
				break;
			}
			catch (NoSuchMethodException e)
			{
				class_to_search = class_to_search.getSuperclass();
				find_conflicting_property_method = null;
			}
		}

		if (find_conflicting_property_method != null)
        {
            try
            {
            	temp_property = (ParticlePropertyModel)find_conflicting_property_method.invoke(null, new Object[] {particle, type, name});
            }
            catch (IllegalAccessException e)
            {
                temp_property = null;
            }
            catch (IllegalArgumentException e)
            {
                temp_property = null;
            }
            catch (InvocationTargetException e)
            {
                temp_property = null;
            }
        }

		if (temp_property == null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	protected static ParticlePropertyModel findConflictingProperty(ParticleModel particle, Class type, String name)
	{
		assert particle != null;
		assert type != null;
		assert name != null;
		assert name.length() > 0;
		
		return particle.getProperty(type, name);
	}
}

