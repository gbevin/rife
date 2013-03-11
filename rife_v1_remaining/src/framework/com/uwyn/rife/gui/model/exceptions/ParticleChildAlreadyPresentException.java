/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParticleChildAlreadyPresentException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model.exceptions;

import com.uwyn.rife.gui.model.ParticleModel;

public class ParticleChildAlreadyPresentException extends ParticlePropertyException
{
	private static final long serialVersionUID = -7194922042980567149L;
	
	ParticleModel 	mParent;
	ParticleModel	mChild;

	public ParticleChildAlreadyPresentException(ParticleModel parent, ParticleModel child)
	{
		super("The child particle couldn't be added to its parent since an equal sibling exists.");
		
		mParent = parent;
		mChild = child;
	}
	
	public ParticleModel getParent()
	{
		return mParent;
	}
	
	public ParticleModel getChild()
	{
		return mChild;
	}
}
