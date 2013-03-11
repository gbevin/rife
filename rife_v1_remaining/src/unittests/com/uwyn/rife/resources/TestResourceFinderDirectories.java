/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestResourceFinderDirectories.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.resources;

import com.uwyn.rife.config.Config;
import com.uwyn.rife.resources.exceptions.ResourceFinderErrorException;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.FileUtils;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import com.uwyn.rife.tools.InputStreamUser;
import com.uwyn.rife.tools.exceptions.FileUtilsErrorException;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import junit.framework.TestCase;

public class TestResourceFinderDirectories extends TestCase
{
	public TestResourceFinderDirectories(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
		ResourceFinder	resource_finder1 = new ResourceFinderDirectories(new File[] {new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"config")});
		ResourceFinder	resource_finder2 = new ResourceFinderDirectories(new File[] {new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"config")});
		assertNotNull(resource_finder1);
		assertNotNull(resource_finder2);
		assertNotSame(resource_finder1, resource_finder2);
	}
	
	public void testGetUnknownResource()
	{
		ResourceFinder	resource_finder = new ResourceFinderDirectories(new File[] {
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"templates"),
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"config")});
		
		assertNull(resource_finder.getResource("this/resource/doesnt/exist.txt"));
	}
	
	public void testGetResourceByName()
	{
		ResourceFinder	resource_finder = new ResourceFinderDirectories(new File[] {
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"templates"),
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"config")});

		assertNotNull(resource_finder.getResource("resources/test.txt"));
	}
	
	public void testGetUnknownStreamByName()
	{
		ResourceFinder	resource_finder = new ResourceFinderDirectories(new File[] {
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"templates"),
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"config")});
		
		try
		{
			resource_finder.useStream("this/resource/doesnt/exist.txt", new InputStreamUser() {
					public Object useInputStream(InputStream stream)
					throws InnerClassException
					{
						assertNull(stream);
						
						return null;
					}
				});
		}
		catch (ResourceFinderErrorException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testGetUnknownStreamByResource()
	{
		ResourceFinder	resource_finder = new ResourceFinderDirectories(new File[] {
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"templates"),
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"config")});

		try
		{
			resource_finder.useStream(new URL("file://this/resource/doesnt/exist.txt"), new InputStreamUser() {
					public Object useInputStream(InputStream stream)
					throws InnerClassException
					{
						assertNull(stream);
						
						return null;
					}
				});
		}
		catch (ResourceFinderErrorException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		catch (MalformedURLException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testGetStreamByName()
	{
		ResourceFinder	resource_finder = new ResourceFinderDirectories(new File[] {
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"templates"),
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"config")});
		
		try
		{
			resource_finder.useStream("resources/test.txt", new InputStreamUser() {
					public Object useInputStream(InputStream stream)
					throws InnerClassException
					{
						assertNotNull(stream);
						try
						{
							assertEquals("This just contains some text to\n"+
								"verify if\n"+
								"\n"+
								"resources can be found\n"+
								"\n"+
								"and\n"+
								"read\n"+
								"\n"+
								"correctly.\n", FileUtils.readString(stream));
						}
						catch (FileUtilsErrorException e)
						{
							assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
						}
						return null;
					}
				});
		}
		catch (ResourceFinderErrorException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testGetStreamByRkesource()
	{
		ResourceFinder	resource_finder = new ResourceFinderDirectories(new File[] {
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"templates"),
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"config")});
		
		URL resource = resource_finder.getResource("resources/test.txt");
		try
		{
			resource_finder.useStream(resource, new InputStreamUser() {
					public Object useInputStream(InputStream stream)
					throws InnerClassException
					{
						assertNotNull(stream);
						try
						{
							assertEquals("This just contains some text to\n"+
								"verify if\n"+
								"\n"+
								"resources can be found\n"+
								"\n"+
								"and\n"+
								"read\n"+
								"\n"+
								"correctly.\n", FileUtils.readString(stream));
						}
						catch (FileUtilsErrorException e)
						{
							assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
						}
						return null;
					}
				});
		}
		catch (ResourceFinderErrorException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testGetUnknownContentByName()
	{
		ResourceFinder	resource_finder = new ResourceFinderDirectories(new File[] {
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"templates"),
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"config")});
		
		try
		{
			String content = resource_finder.getContent("this/resource/doesnt/exist.txt");
			assertNull(content);
		}
		catch (ResourceFinderErrorException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testGetUnknownContentByResource()
	{
		ResourceFinder	resource_finder = new ResourceFinderDirectories(new File[] {
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"templates"),
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"config")});
		
		try
		{
			String content = resource_finder.getContent(new URL("file://this/resource/doesnt/exist.txt"));
			assertNull(content);
		}
		catch (ResourceFinderErrorException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		catch (MalformedURLException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testGetContentByName()
	{
		ResourceFinder	resource_finder = new ResourceFinderDirectories(new File[] {
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"templates"),
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"config")});
		
		try
		{
			String content = resource_finder.getContent("resources/test.txt");
			assertNotNull(content);
			assertEquals(content, "This just contains some text to\n"+
				"verify if\n"+
				"\n"+
				"resources can be found\n"+
				"\n"+
				"and\n"+
				"read\n"+
				"\n"+
				"correctly.\n");
		}
		catch (ResourceFinderErrorException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testGetContentByResource()
	{
		ResourceFinder	resource_finder = new ResourceFinderDirectories(new File[] {
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"templates"),
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"config")});
		
		URL resource = resource_finder.getResource("resources/test.txt");
		try
		{
			String content = resource_finder.getContent(resource);
			assertNotNull(content);
			assertEquals(content, "This just contains some text to\n"+
				"verify if\n"+
				"\n"+
				"resources can be found\n"+
				"\n"+
				"and\n"+
				"read\n"+
				"\n"+
				"correctly.\n");
		}
		catch (ResourceFinderErrorException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testGetContentByNameAndEncoding()
	{
		ResourceFinder	resource_finder = new ResourceFinderDirectories(new File[] {
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"templates"),
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"config")});
		
		try
		{
			String content = resource_finder.getContent("resources/test-utf8.txt", "UTF-8");
			assertNotNull(content);
			assertEquals(content, "This just contains some text to\n"+
				"verify if\n"+
				"\n"+
				"resources can be found\n"+
				"\n"+
				"and\n"+
				"read\n"+
				"\n"+
				"correctly.\n"+
				"Here are some encoding-specific chars : ¡¢£¤¥¦§¨©ª«¬­®.\n");
		}
		catch (ResourceFinderErrorException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testGetContentByResourceAndEncoding()
	{
		ResourceFinder	resource_finder = new ResourceFinderDirectories(new File[] {
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"templates"),
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"config")});
		
		URL resource = resource_finder.getResource("resources/test-utf8.txt");
		try
		{
			String content = resource_finder.getContent(resource, "UTF-8");
			assertNotNull(content);
			assertEquals(content, "This just contains some text to\n"+
				"verify if\n"+
				"\n"+
				"resources can be found\n"+
				"\n"+
				"and\n"+
				"read\n"+
				"\n"+
				"correctly.\n"+
				"Here are some encoding-specific chars : ¡¢£¤¥¦§¨©ª«¬­®.\n");
		}
		catch (ResourceFinderErrorException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testGetUnknownModificationTimeByName()
	{
		ResourceFinder	resource_finder = new ResourceFinderDirectories(new File[] {
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"templates"),
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"config")});
		
		try
		{
			long time = resource_finder.getModificationTime("this/resource/doesnt/exist.txt");
			assertEquals(-1, time);
		}
		catch (ResourceFinderErrorException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testGetUnknownModificationTimeByResource()
	{
		ResourceFinder	resource_finder = new ResourceFinderDirectories(new File[] {
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"templates"),
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"config")});
		
		try
		{
			long time = resource_finder.getModificationTime(new URL("file://this/resource/doesnt/exist.txt"));
			assertEquals(-1, time);
		}
		catch (ResourceFinderErrorException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		catch (MalformedURLException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testGetModificationTimeByName()
	{
		ResourceFinder	resource_finder = new ResourceFinderDirectories(new File[] {
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"templates"),
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"config")});
		
		try
		{
			long time = resource_finder.getModificationTime("resources/test.txt");
			assertTrue(time != -1);
		}
		catch (ResourceFinderErrorException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testGetModificationTimeByResource()
	{
		ResourceFinder	resource_finder = new ResourceFinderDirectories(new File[] {
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"templates"),
			new File(Config.getRepInstance().getString("RIFE_HOME")+File.separator+"config")});
		
		URL resource = resource_finder.getResource("resources/test.txt");
		try
		{
			long time = resource_finder.getModificationTime(resource);
			assertTrue(time != -1);
		}
		catch (ResourceFinderErrorException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
}
