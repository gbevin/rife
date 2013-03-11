/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Cursors.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.swing;

import com.uwyn.rife.rep.Participant;
import com.uwyn.rife.rep.Rep;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;

public class Cursors
{
	public final static String	DEFAULT_PARTICIPANT_NAME = "ParticipantCursors";
	
	private String					mPath = null;
	private HashMap<String, Cursor>	mCursors = null;
	
	public Cursors()
	{
		mCursors = new HashMap<String, Cursor>();
	}
	
	public Cursors(String path)
	{
		if (null == path)			throw new IllegalArgumentException("path can't be null.");
		if (0 == path.length())		throw new IllegalArgumentException("path can't be empty.");
		
		mPath = path;
		initialize();

		assert mPath != null;
		assert mPath.length() > 0;
		assert mCursors != null;
	}
	
	private void initialize()
	{
		Images	images = Images.getRepInstance();
		
		mCursors = new HashMap<String, Cursor>();

		Point	hotspot = new Point(6, 6);
		String	cursor_filename_short = null;
		for (String	cursor_filename : images.getImageIconNames(mPath))
		{
			cursor_filename_short = cursor_filename.substring(mPath.length(), cursor_filename.length()-4);

			Image icon_image = images.getImageIcon(cursor_filename).getImage();
			mCursors.put(cursor_filename_short, Toolkit.getDefaultToolkit().createCustomCursor(icon_image, hotspot, cursor_filename_short));
		}
	}

    public static boolean hasRepInstance()
    {
        return Rep.hasParticipant(DEFAULT_PARTICIPANT_NAME);
    }

    public static Cursors getRepInstance()
    {
		Participant	participant = Rep.getParticipant(DEFAULT_PARTICIPANT_NAME);
		if (null == participant)
		{
			return null;
		}
		
        return (Cursors)participant.getObject();
    }
	
	public Cursor getCursor(String path)
	{
		if (null == path)	throw new IllegalArgumentException("path can't be null.");
		
		return mCursors.get(path);
	}

	public ArrayList<String> getCursorNames(String prefix)
	{
		if (null == prefix)	throw new IllegalArgumentException("prefix can't be null.");
		
		ArrayList<String>	matching_cursors = new ArrayList<String>();
		
		for (String cursor_filename : mCursors.keySet())
		{
			if (cursor_filename.startsWith(prefix))
			{
				matching_cursors.add(cursor_filename);
			}
		}

		return matching_cursors;
	}
}
