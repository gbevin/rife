/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Images.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.swing;

import com.uwyn.rife.rep.Participant;
import com.uwyn.rife.rep.Rep;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.tools.ClasspathUtils;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.ImageIcon;

public class Images
{
	public final static String	DEFAULT_PARTICIPANT_NAME = "ParticipantImages";

	private String						mPath = null;
	private HashMap<String, ImageIcon>	mImages = null;
	
	public Images()
	{
		mImages = new HashMap<String, ImageIcon>();
	}
	
	public Images(String path)
	{
		if (null == path)			throw new IllegalArgumentException("path can't be null.");
		if (0 == path.length())		throw new IllegalArgumentException("path can't be empty.");
		
		mPath = path;
		initialize();

		assert mPath != null;
		assert mPath.length() > 0;
		assert mImages != null;
	}
	
	private void initialize()
	{
		mImages = new HashMap<String, ImageIcon>();

		ArrayList<String>	resources = ClasspathUtils.getResourcesInDirectory(mPath, new FileFilter() {
				public boolean accept(File file)
				{
					if (file.getName().equals("framework") ||
						file.getName().equals("unittests") ||
						file.getName().equals("templates") ||
						file.getName().equals("elements"))
					{
						return false;
					}
					
					return true;
				}
			});
		
		String	graphics_filename_short = null;
		
		for (String graphics_filename : resources)
		{
			graphics_filename_short = graphics_filename.substring(mPath.length());

			mImages.put(graphics_filename_short, new ImageIcon(ResourceFinderClasspath.getInstance().getResource(graphics_filename)));
		}
	}

    public static boolean hasRepInstance()
    {
        return Rep.hasParticipant(DEFAULT_PARTICIPANT_NAME);
    }

    public static Images getRepInstance()
    {
		Participant	participant = Rep.getParticipant(DEFAULT_PARTICIPANT_NAME);
		if (null == participant)
		{
			return null;
		}
		
        return (Images)participant.getObject();
    }
	
	public ImageIcon getImageIcon(String path)
	{
		if (null == path)	throw new IllegalArgumentException("path can't be null.");
		
		return mImages.get(path);
	}

	public ArrayList<String> getImageIconNames(String prefix)
	{
		if (null == prefix)	throw new IllegalArgumentException("prefix can't be null.");
		
		ArrayList<String>	matching_images = new ArrayList<String>();
		
		for (String image_filename : mImages.keySet())
		{
			if (image_filename.startsWith(prefix))
			{
				matching_images.add(image_filename);
			}
		}

		return matching_images;
	}
}
