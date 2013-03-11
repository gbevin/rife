/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParticleModelListener.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model;

public interface ParticleModelListener
{
	void parentChanged();

	void childAdded(ParticleModel child);

	void childRemoved(ParticleModel child);

	void propertyAdded(ParticlePropertyModel property);

	void propertyRenamed(ParticlePropertyModel property);

	void propertyRemoved(ParticlePropertyModel property);
}
