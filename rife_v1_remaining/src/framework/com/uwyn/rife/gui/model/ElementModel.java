/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementModel.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model;

import com.uwyn.rife.gui.model.exceptions.GuiModelException;
import com.uwyn.rife.gui.model.exceptions.ParticlePropertyInvalidRemovalException;
import java.util.Collection;
import java.util.Iterator;

public class ElementModel extends ParticleModel
{
	private ElementIdModel	mElementIdModel = null;
	
	public ElementModel(String id)
	throws GuiModelException
	{
		super();

		if (null == id)			throw new IllegalArgumentException("id can't be null.");
		if (0 == id.length())	throw new IllegalArgumentException("id can't be empty.");
		
		try
		{
			mElementIdModel = new ElementIdModel(this, id);
		}
		catch (GuiModelException e)
		{
			e.fillInStackTrace();
			throw e;
		}
	}

	protected ParticleModel findConflictingParticle(ParticleModel parentParticle)
	{
		assert parentParticle != null;
		
		for (ElementModel sibling : parentParticle.getChildren(ElementModel.class))
		{
			if (sibling.getId().getName().equals(getId().getName()))
			{
				return sibling;
			}
		}

		return null;
	}

	public boolean removeProperty(ParticlePropertyModel property)
	throws GuiModelException
	{
		if (null == property)	throw new IllegalArgumentException("property can't be null.");

		if (property instanceof ElementIdModel)
		{
			throw new ParticlePropertyInvalidRemovalException(this, property);
		}
		else
		{
			return super.removeProperty(property);
		}
	}

	public ElementImplementationModel setImplementation(String name)
	throws GuiModelException
	{
		if (null == name)		throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty.");
		
		synchronized (mPropertiesMonitor)
		{
			ElementImplementationModel current_classname = getImplementation();
			
			if (current_classname != null)
			{
				removeProperty(current_classname);
			}
	
			return new ElementImplementationModel(this, name);
		}
	}

	public ElementInputModel addInput(String name)
	throws GuiModelException
	{
		if (null == name)		throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty.");

		return new ElementInputModel(this, name);
	}

	public ElementOutputModel addOutput(String name)
	throws GuiModelException
	{
		if (null == name)		throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty.");

		return new ElementOutputModel(this, name);
	}

	public ElementExitModel addExit(String name)
	throws GuiModelException
	{
		if (null == name)		throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty.");

		return new ElementExitModel(this, name);
	}
	
	public SubmissionModel addSubmission(String name)
	throws GuiModelException
    {
		if (null == name)		throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty.");

		return new SubmissionModel(this, name);
	}

	public ElementImplementationModel getImplementation()
	{
		Iterator<ElementImplementationModel>	implementation_it = getProperties(ElementImplementationModel.class).iterator();
		
		if (implementation_it.hasNext())
		{
			return implementation_it.next();
		}
		else
		{
			return null;
		}
	}

	public ElementIdModel getId()
	{
		return mElementIdModel;
	}

	public Collection<ElementInputModel> getInputs()
	{
		return getProperties(ElementInputModel.class);
	}

	public Collection<ElementOutputModel> getOutputs()
	{
		return getProperties(ElementOutputModel.class);
	}

	public Collection<ElementExitModel> getExits()
	{
		return getProperties(ElementExitModel.class);
	}

	public Collection<SubmissionModel> getSubmissions()
    {
		Collection<SubmissionModel> result = getChildren(SubmissionModel.class);
		
		assert result != null;
		
		return result;
	}

	public int countInputs()
	{
		return countProperties(ElementInputModel.class);
	}

	public int countOutputs()
	{
		return countProperties(ElementOutputModel.class);
	}

	public int countExits()
	{
		return countProperties(ElementExitModel.class);
	}

    public int countSubmissions()
    {
        int result = countChildren(SubmissionModel.class);
		
		assert result >= 0;
		
		return result;
    }
}


