/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
  * $Id: Janino2Site.java 3918 2008-04-14 17:35:35Z gbevin $
*/
package com.uwyn.rife.engine;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.engine.exceptions.NotFoundProcessingErrorException;
import com.uwyn.rife.engine.exceptions.ParsingErrorException;
import com.uwyn.rife.engine.exceptions.ProcessingErrorException;
import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.resources.exceptions.ResourceFinderErrorException;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import com.uwyn.rife.tools.InputStreamUser;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import org.codehaus.janino.Scanner;
import org.codehaus.janino.ScriptEvaluator;

class Janino2Site implements SiteProcessor
{
	Janino2Site()
	{
	}

	public void processSite(SiteBuilder builder, String declarationName, ResourceFinder resourceFinder)
	throws EngineException
	{
		JaninoProcessor processor = new JaninoProcessor(builder);
		
		String processed_path = null;
		try
		{
			// process the site declaration
			try
			{
				processed_path = declarationName;
				processor.processJanino(processed_path, resourceFinder);
			}
			catch (NotFoundProcessingErrorException e)
			{
				processed_path = DEFAULT_SITES_PATH+declarationName;
				processor.processJanino(processed_path, resourceFinder);
			}
		}
		catch (Exception e)
		{
			throw new ProcessingErrorException("site", declarationName, e);
		}
	
		// obtain the modification time
		if (RifeConfig.Engine.getSiteAutoReload())
		{
			URL resource = resourceFinder.getResource(processed_path);
			if (null == resource)
			{
				throw new NotFoundProcessingErrorException("site", processed_path, null);
			}
			
			try
			{
				builder.addResourceModificationTime(new UrlResource(resource, processed_path), resourceFinder.getModificationTime(resource));
			}
			catch (ResourceFinderErrorException e)
			{
				throw new ProcessingErrorException("site", declarationName, "Error while retrieving the modification time.", e);
			}
		}
	}
	
	private class JaninoProcessor
	{
		private SiteBuilder	mSiteBuilder = null;
		
		private JaninoProcessor(SiteBuilder builder)
		{
			mSiteBuilder = builder;
		}
		
		public synchronized void processJanino(final String janinoPath, ResourceFinder resourceFinder)
		{
			if (null == janinoPath)			throw new IllegalArgumentException("janinoPath can't be null.");
			if (janinoPath.length() == 0)	throw new IllegalArgumentException("janinoPath can't be empty.");
			if (null == resourceFinder)		throw new IllegalArgumentException("resourceFinder can't be null.");
			
			// retrieve a stream towards the janino script
			ScriptEvaluator evaluator = null;
			try
			{
				evaluator = resourceFinder.useStream(janinoPath, new InputStreamUser() {
						public ScriptEvaluator useInputStream(InputStream stream)
						throws InnerClassException
						{
							if (null == stream)
							{
								throw new NotFoundProcessingErrorException("site", janinoPath, null);
							}
							
							// parse the janino script and create an evaluator
							try
							{
								return new ScriptEvaluator(new Scanner(janinoPath, stream), Void.TYPE, new String[] {"builder"}, new Class[] {SiteBuilder.class}, new Class[0], getClass().getClassLoader());
							}
							catch (Throwable e)
							{
								throw new ParsingErrorException("site", janinoPath, e);
							}
						}
					});
			}
			catch (ResourceFinderErrorException e)
			{
				throw new NotFoundProcessingErrorException("site", janinoPath, e);
			}
			
			// evaluate the script and provide the parameter
			try
			{
				evaluator.evaluate(new Object[] {mSiteBuilder});
			}
			catch (InvocationTargetException e)
			{
				throw new ProcessingErrorException("site", janinoPath, e);
			}
		}
	}
}
