/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParticipantImages.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.rep.participants;

import com.uwyn.rife.rep.BlockingParticipant;
import com.uwyn.rife.swing.Images;

public class ParticipantImages extends BlockingParticipant
{
	protected Images	mImages = null;

	public ParticipantImages()
	{
		setInitializationMessage("Loading application specific images ...");
		setCleanupMessage("Cleaning up application specific images ...");
	}

	protected void initialize()
	{
		mImages = new Images(getParameter());
	}

	protected Object _getObject(Object key)
	{
		return mImages;
	}
}
