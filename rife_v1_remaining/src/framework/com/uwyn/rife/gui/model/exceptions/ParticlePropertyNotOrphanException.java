/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParticlePropertyNotOrphanException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model.exceptions;

import com.uwyn.rife.gui.model.ParticlePropertyModel;

public class ParticlePropertyNotOrphanException extends ParticlePropertyException
{
	private static final long serialVersionUID = 5542499345158785324L;
	
	ParticlePropertyModel mProperty;
	
	public ParticlePropertyNotOrphanException(ParticlePropertyModel property)
	{
		super("The particle property with name '"+property.getName()+"' and type '"+property.getClass().getName()+"', already belongs to a particle.");
		
		mProperty = property;
	}
	
	public ParticlePropertyModel getParticleProperty()
	{
		return mProperty;
	}
}
