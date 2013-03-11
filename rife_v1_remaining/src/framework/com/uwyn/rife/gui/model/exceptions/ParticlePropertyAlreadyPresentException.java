/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParticlePropertyAlreadyPresentException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model.exceptions;

import com.uwyn.rife.gui.model.ParticleModel;
import com.uwyn.rife.gui.model.ParticlePropertyModel;

public class ParticlePropertyAlreadyPresentException extends ParticlePropertyException
{
	private static final long serialVersionUID = 8827931964384956677L;
	
	ParticleModel 			mParticle;
	ParticlePropertyModel	mProperty;
	
	public ParticlePropertyAlreadyPresentException(ParticleModel particle, ParticlePropertyModel property)
	{
		super("The particle property with name '"+property.getName()+"' and type '"+property.getClass().getName()+"', already has an equal instance present in the particle.");
		
		mParticle = particle;
		mProperty = property;
	}
	
	public ParticleModel getParticle()
	{
		return mParticle;
	}
	
	public ParticlePropertyModel getParticleProperty()
	{
		return mProperty;
	}
}
