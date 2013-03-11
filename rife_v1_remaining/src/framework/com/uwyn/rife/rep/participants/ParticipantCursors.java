/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParticipantCursors.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.rep.participants;

import com.uwyn.rife.rep.BlockingParticipant;
import com.uwyn.rife.swing.Cursors;

public class ParticipantCursors extends BlockingParticipant
{
	protected Cursors	mCursors = null;

	public ParticipantCursors()
	{
		setInitializationMessage("Creating the application cursors ...");
		setCleanupMessage("Cleaning up the application cursors ...");
	}

	protected void initialize()
	{
		mCursors = new Cursors(getParameter());
	}

	protected Object _getObject(Object key)
	{
		return mCursors;
	}
}
