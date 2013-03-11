/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SubmissionModel.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model;

import com.uwyn.rife.gui.model.exceptions.GuiModelException;
import com.uwyn.rife.gui.model.exceptions.ParticlePropertyInvalidRemovalException;
import java.util.Collection;

public class SubmissionModel extends ParticleModel
{
	private SubmissionIdModel	mSubmissionIdModel = null;
	
	SubmissionModel(ElementModel element, String id)
	throws GuiModelException
	{
		super();
		
		assert element != null;
		assert id != null;
		assert id.length() > 0;

		try
		{
			mSubmissionIdModel = new SubmissionIdModel(this, id);
		}
		catch (GuiModelException e)
		{
			e.fillInStackTrace();
			throw e;
		}
		
		element.addChild(this);
	}

	protected ParticleModel findConflictingParticle(ParticleModel parentParticle)
	{
		assert parentParticle != null;
		
		for (SubmissionModel sibling : parentParticle.getChildren(SubmissionModel.class))
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

		if (property instanceof SubmissionIdModel)
		{
			throw new ParticlePropertyInvalidRemovalException(this, property);
		}
		else
		{
			return super.removeProperty(property);
		}
	}

	public SubmissionIdModel getId()
	{
		return mSubmissionIdModel;
	}
	
	public SubmissionParameterModel addParameter(String name)
	throws GuiModelException
	{
		if (null == name)		throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty.");

		return new SubmissionParameterModel(this, name);
	}

	public Collection<SubmissionParameterModel> getParameters()
	{
		return getProperties(SubmissionParameterModel.class);
	}

	public int countParameters()
	{
		return countProperties(SubmissionParameterModel.class);
	}
}


