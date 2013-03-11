/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParticleModel.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model;

import com.uwyn.rife.gui.model.exceptions.GuiModelException;
import com.uwyn.rife.gui.model.exceptions.ParticleChildAlreadyPresentException;
import com.uwyn.rife.gui.model.exceptions.ParticlePropertyAlreadyPresentException;
import com.uwyn.rife.gui.model.exceptions.ParticlePropertyInvalidNameException;
import com.uwyn.rife.gui.model.exceptions.ParticlePropertyNotOrphanException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public abstract class ParticleModel
{
	private ParticleModel						mParent = null;
	private ArrayList<ParticleModel>			mChildren = null;
	private ArrayList<ParticlePropertyModel>	mProperties = null;
	private HashSet<ParticleModelListener>		mListeners = null;
	private String								mDescription = null;
	
	protected final Object	mParentMonitor = new Object();
	protected final Object	mChildrenMonitor = new Object();
	protected final Object	mPropertiesMonitor = new Object();
	protected final Object	mListenersMonitor = new Object();

	protected ParticleModel()
	{
        initialize();
	}

	private void initialize()
	{
		synchronized (mChildrenMonitor)
		{
	        mChildren = new ArrayList<ParticleModel>();
		}
		synchronized (mPropertiesMonitor)
		{
			mProperties = new ArrayList<ParticlePropertyModel>();
		}
		synchronized (mListenersMonitor)
		{
			mListeners = new HashSet<ParticleModelListener>();
		}
		
		assert 0 == mChildren.size();
		assert 0 == mProperties.size();
		assert 0 == mListeners.size();
	}

	public String getDescription()
	{
		return mDescription;
	}

	public void setDescription(String description)
	{
		mDescription = description;
	}

	protected void addChild(ParticleModel child)
	throws GuiModelException
	{
		if (null == child)	throw new IllegalArgumentException("child can't be null.");

		if (null == child.findConflictingParticle(this))
		{
			boolean result = false;
			
			synchronized (mChildrenMonitor)
			{
				result = mChildren.add(child);
			}
			
			if (result)
			{
				child.setParent(this);
				fireChildAdded(child);
			}
		}
		else
		{
			throw new ParticleChildAlreadyPresentException(this, child);
		}
		
		assert mChildren.contains(child);
		assert this == child.getParent();
	}

	protected ParticleModel findConflictingParticle(ParticleModel parentParticle)
	{
		if (null == parentParticle)	throw new IllegalArgumentException("parentParticle can't be null.");

		for (ParticleModel sibling : parentParticle.getChildren(getClass()))
		{
			if (sibling.equals(this))
			{
				return sibling;
			}
		}

		return null;
	}

	Collection<ParticleModel> getChildren()
	{
		synchronized (mChildrenMonitor)
		{
			return mChildren;
		}
	}

	public <ChildType extends ParticleModel> Collection<ChildType> getChildren(Class<ChildType> type)
	{
		ArrayList<ChildType>	result = new ArrayList<ChildType>();
		
		synchronized (mChildrenMonitor)
		{
			for (ParticleModel child : mChildren)
			{
				if (type.isInstance(child))
				{
					result.add((ChildType)child);
				}
			}
		}
		
		return result;
	}

	public int countChildren()
	{
		synchronized (mChildrenMonitor)
		{
			return mChildren.size();
		}
	}

	public int countChildren(Class type)
	{
		if (null == type)	throw new IllegalArgumentException("type can't be null.");

		int						result = 0;
		
		synchronized (mChildrenMonitor)
		{
			for (ParticleModel child : mChildren)
			{
				if (type.isInstance(child))
				{
					result++;
				}
			}
		}

		assert result >= 0;
		
		return result;
	}

	protected boolean setParent(ParticleModel parent)
	{
		synchronized (mParentMonitor)
		{
			if (parent != mParent)
			{
				mParent = parent;
				fireParentChanged();
				
				return true;
			}
		}

		return false;
	}

	public ParticleModel getParent()
	{
		synchronized (mParentMonitor)
		{
			return mParent;
		}
	}

	public boolean removeChild(ParticleModel child)
	{
		if (null == child)	throw new IllegalArgumentException("child can't be null.");

		boolean result = false;
		
		synchronized (mChildrenMonitor)
		{
			result = mChildren.remove(child);
		}
		
		if (result)
		{
			child.setParent(null);
            fireChildRemoved(child);
		}
		
		return result;
	}

	public boolean containsChild(ParticleModel particle)
	{
		if (null == particle)	throw new IllegalArgumentException("particle can't be null.");

		for (ParticleModel property : getChildren(particle.getClass()))
		{
			if (property.equals(particle))
			{
				return true;
			}
		}

		return false;
	}

	protected boolean addProperty(ParticlePropertyModel property)
	throws GuiModelException
	{
		assert property != null;
		
		synchronized (mPropertiesMonitor)
		{
			if (ParticlePropertyModel.isValidName(this, property.getClass(), property.getName()))
			{
				if (property.getParticle() != null)
				{
					throw new ParticlePropertyNotOrphanException(property);
				}
				else if (mProperties.contains(property))
				{
					throw new ParticlePropertyAlreadyPresentException(this, property);
				}
				else
				{
					mProperties.add(property);
					property.setParticle(this);
					firePropertyAdded(property);
				}
			}
			else
			{
				throw new ParticlePropertyInvalidNameException(this, property);
			}
		}
		
		assert mProperties.contains(property);
		assert this == property.getParticle();

		return true;
	}

	public boolean containsProperty(ParticlePropertyModel propertyToCheck)
	{
		if (null == propertyToCheck)	throw new IllegalArgumentException("propertyToCheck can't be null.");

		for (ParticlePropertyModel property : getProperties(propertyToCheck.getClass()))
		{
			if (property.equals(propertyToCheck))
			{
				return true;
			}
		}

		return false;
	}

	public ParticlePropertyModel getProperty(Class<? extends ParticlePropertyModel> type, String name)
	{
		if (null == type)		throw new IllegalArgumentException("type can't be null.");
		if (null == name)		throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty.");

		for (ParticlePropertyModel property : getProperties(type))
		{
			if (property.getName().equals(name))
			{
				return property;
			}
		}

		return null;
	}

	Collection<ParticlePropertyModel> getProperties()
	{
		synchronized (mPropertiesMonitor)
		{
	        return mProperties;
		}
    }

	public <PropertyType extends ParticlePropertyModel> Collection<PropertyType> getProperties(Class<PropertyType> type)
	{
		ArrayList<PropertyType>	result = new ArrayList<PropertyType>();
		
		for (ParticlePropertyModel property : getProperties())
		{
			if (type.isInstance(property))
			{
				result.add((PropertyType)property);
			}
		}
		
		assert result != null;
		
		return result;
	}

	public int countProperties()
	{
        int result = 0;
		
		synchronized (mPropertiesMonitor)
		{
			result = mProperties.size();
		}
		
		assert result >= 0;
		
		return result;
    }

	public int countProperties(Class type)
	{
		if (null == type)	throw new IllegalArgumentException("type can't be null.");

		int								result = 0;

		synchronized (mPropertiesMonitor)
		{
			for (ParticlePropertyModel property : mProperties)
			{
				if (type.isInstance(property))
				{
					result++;
				}
			}
		}

		assert result >= 0;
		
		return result;
	}

	public boolean renameProperty(ParticlePropertyModel property, String newName)
	throws GuiModelException
	{
		if (null == property)		throw new IllegalArgumentException("property can't be null.");
		if (null == newName)		throw new IllegalArgumentException("newName can't be null.");
		if (0 == newName.length())	throw new IllegalArgumentException("newName can't be empty.");

		if (property.isValidName(newName))
		{
			if (!property.getName().equals(newName))
			{
				property.setName(newName);
				firePropertyRenamed(property);
			}
			else
			{
				return false;
			}
		}
		else
		{
			throw new ParticlePropertyInvalidNameException(this, property);
		}
		
		assert property.getName().equals(newName);

		return true;
	}

	public boolean removeProperty(ParticlePropertyModel property)
	throws GuiModelException
	{
		if (null == property)	throw new IllegalArgumentException("property can't be null.");

		boolean result = false;
		
		synchronized (mPropertiesMonitor)
		{
			result = mProperties.remove(property);
		}
		
		if (result)
		{
			property.setParticle(null);
            firePropertyRemoved(property);
		}
		else
		{
			return false;
		}
		
		assert !containsProperty(property);
		assert property.getParticle() != this;

		return true;
	}

	public boolean addParticleListener(ParticleModelListener listener)
	{
		if (null == listener)	throw new IllegalArgumentException("listener can't be null.");

		boolean result = false;
		
		synchronized (mListenersMonitor)
		{
			if (!mListeners.contains(listener))
			{
				result = mListeners.add(listener);
			}
			else
			{
				result = true;
			}
		}
		
		assert mListeners.contains(listener);
		
		return result;
	}

	public boolean removeParticleListener(ParticleModelListener listener)
	{
		if (null == listener)	throw new IllegalArgumentException("listener can't be null.");

        boolean result = false;
		
		synchronized (mListenersMonitor)
		{
			result = mListeners.remove(listener);
		}
		
		assert !mListeners.contains(listener);
		
		return result;
	}

	private void fireParentChanged()
	{
		synchronized (mListenersMonitor)
		{
			for (ParticleModelListener listener : mListeners)
			{
				listener.parentChanged();
			}
		}
	}

	private void fireChildAdded(ParticleModel child)
	{
		assert child != null;
		
		synchronized (mListenersMonitor)
		{
			for (ParticleModelListener listener : mListeners)
			{
				listener.childAdded(child);
			}
		}
	}

	private void fireChildRemoved(ParticleModel child)
	{
		assert child != null;

		synchronized (mListenersMonitor)
		{
			for (ParticleModelListener listener : mListeners)
			{
				listener.childRemoved(child);
			}
		}
	}

	private void firePropertyAdded(ParticlePropertyModel property)
	{
		assert property != null;

		synchronized (mListenersMonitor)
		{
			for (ParticleModelListener listener : mListeners)
			{
				listener.propertyAdded(property);
			}
		}
	}

	private void firePropertyRenamed(ParticlePropertyModel property)
	{
		assert property != null;

		synchronized (mListenersMonitor)
		{
			for (ParticleModelListener listener : mListeners)
			{
				listener.propertyRenamed(property);
			}
		}
	}

	private void firePropertyRemoved(ParticlePropertyModel property)
	{
		assert property != null;

		synchronized (mListenersMonitor)
		{
			for (ParticleModelListener listener : mListeners)
			{
				listener.propertyRemoved(property);
			}
		}
	}

    public String toString()
    {
        return toString(0);
    }

    public String toString(int level)
    {
		if (level < 0)	throw new IllegalArgumentException("level should be at least 0.");

        String output = "";
        String indent = "";

        for (int i = 0; i < level; i++)
        {
            indent += "    ";
        }

        output += indent+getClass()+"\n";

		Collection<ParticlePropertyModel>	properties = getProperties();
		if (properties.size() > 0)
		{
			output += indent+"PROPERTIES\n";
			for (ParticlePropertyModel property : properties)
			{
				output += indent+property.getClass()+"="+property.getName()+"\n";
			}
		}

		Collection<ParticleModel>	children = getChildren();
		if (children.size() > 0)
		{
			output += indent+"CHILDREN\n";
			for (ParticleModel child : children)
			{
				output += child.toString(level+1)+"\n";
			}
		}

		return output;
    }
}
