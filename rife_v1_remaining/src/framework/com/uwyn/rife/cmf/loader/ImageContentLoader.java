/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ImageContentLoader.java 3939 2008-04-26 20:09:07Z gbevin $
 */
package com.uwyn.rife.cmf.loader;

import com.uwyn.rife.cmf.loader.image.*;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads raw content as image data. The internal type to which everything will
 * be converted is <code>java.awt.Image</code>.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3939 $
 * @since 1.0
 * @see com.uwyn.rife.cmf.loader.ContentLoader
 */
public class ImageContentLoader extends ContentLoader<Image>
{
	private static final List<ContentLoaderBackend<Image>> sBackends;
	
	static
	{
		sBackends = new ArrayList<ContentLoaderBackend<Image>>();
		sBackends.add(new ImageIOLoader());
		sBackends.add(new JaiLoader());
		sBackends.add(new JMagickLoader());
		sBackends.add(new JimiLoader());
		sBackends.add(new ImageJLoader());
		sBackends.add(new ImageroReaderLoader());
	}

	public List<ContentLoaderBackend<Image>> getBackends()
	{
		return sBackends;
	}
}
