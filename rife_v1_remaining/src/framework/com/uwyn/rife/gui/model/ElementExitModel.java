/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementExitModel.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model;

import com.uwyn.rife.gui.model.exceptions.GuiModelException;

public class ElementExitModel extends ElementPropertyModel
{
	public ElementExitModel(ElementModel element, String name)
	throws GuiModelException
	{
		super(element, name);
	}

	public boolean equals(Object object)
	{
		if (object instanceof ParticlePropertyModel)
		{
			ParticlePropertyModel property = (ParticlePropertyModel)object;
			if (property.getParticle() == getParticle() &&
				object instanceof ElementExitModel &&
				property.getName().equals(getName()))
			{
				return true;
			}
		}

		return false;
	}

	protected static ParticlePropertyModel findConflictingProperty(ParticleModel particle, Class type, String name)
	{
		assert particle != null;
		assert type != null;
		assert name != null;
		assert name.length() > 0;
		
		return particle.getProperty(ElementExitModel.class, name);
	}
}


