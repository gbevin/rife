/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SiteModel.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model;

import java.util.Collection;

import com.uwyn.rife.gui.model.exceptions.GuiModelException;
import com.uwyn.rife.gui.model.exceptions.SiteElementAlreadyPresentException;

public class SiteModel extends ParticleModel
{
	public SiteModel()
	{
		super();
	}
	
	public void addElement(ElementModel element)
	throws GuiModelException
    {
		if (null == element)	throw new IllegalArgumentException("element can't be null.");

		try
		{
			addChild(element);
		}
		catch (GuiModelException e)
		{
			throw new SiteElementAlreadyPresentException(e);
		}
		
		assert getChildren().contains(element);
	}

	public Collection<ElementModel> getElements()
    {
		Collection<ElementModel> result = getChildren(ElementModel.class);
		
		assert result != null;
		
		return result;
	}

    public int countElements()
    {
        int result = countChildren(ElementModel.class);
		
		assert result >= 0;
		
		return result;
    }
}


