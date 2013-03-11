/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDatabaseResources.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.resources;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.resources.exceptions.ResourceFinderErrorException;
import com.uwyn.rife.resources.exceptions.ResourceWriterErrorException;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.FileUtils;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import com.uwyn.rife.tools.InputStreamUser;
import com.uwyn.rife.tools.exceptions.FileUtilsErrorException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import junit.framework.TestCase;

public class TestDatabaseResources extends TestCase
{
	private Datasource	mDatasource = null;
	private String		mResource = "This just contains some text to\n"+
				"verify if\n"+
				"\n"+
				"resources can be found\n"+
				"\n"+
				"and\n"+
				"read\n"+
				"\n"+
				"correctly.\n";
	private String		mResourceUtf8 = "This just contains some text to\n"+
				"verify if\n"+
				"\n"+
				"resources can be found\n"+
				"\n"+
				"and\n"+
				"read\n"+
				"\n"+
				"correctly.\n"+
				"Here are some encoding-specific chars : ¡¢£¥§¨©ª«¬®.\n";
	
	public TestDatabaseResources(Datasource datasource, String datasourceName, String name)
	{
		super(name);
		mDatasource = datasource;
	}
	
	protected void setUp()
	throws Exception
	{
		DatabaseResources	resource_finder = DatabaseResourcesFactory.getInstance(mDatasource);
		try
		{
			resource_finder.install();
			resource_finder.addResource("resources/test.txt", mResource);
			resource_finder.addResource("resources/test-utf8.txt", mResourceUtf8);
		}
		catch (ResourceWriterErrorException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	protected void tearDown()
	throws Exception
	{
		DatabaseResources	resource_finder = DatabaseResourcesFactory.getInstance(mDatasource);
		try
		{
			resource_finder.remove();
		}
		catch (ResourceWriterErrorException e)
		{
			// that's ok, it's probably already gone
		}
	}
	
	public void testInstantiation()
	{
		ResourceFinder	resource_finder1 = DatabaseResourcesFactory.getInstance(mDatasource);
		ResourceFinder	resource_finder2 = DatabaseResourcesFactory.getInstance(mDatasource);
		assertNotNull(resource_finder1);
		assertNotNull(resource_finder2);
		assertSame(resource_finder1, resource_finder2);
	}

	public void testInstall()
	{
		DatabaseResources	resource_finder = DatabaseResourcesFactory.getInstance(mDatasource);
		try
		{
			resource_finder.remove();

			resource_finder.install();
			try
			{
				resource_finder.install();
				fail();
			}
			catch (ResourceWriterErrorException e)
			{
				assertTrue(true);
			}
		}
		catch (ResourceWriterErrorException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testRemove()
	{
		DatabaseResources	resource_finder = DatabaseResourcesFactory.getInstance(mDatasource);
		try
		{
			resource_finder.remove();

			resource_finder.install();
			resource_finder.remove();
			try
			{
				resource_finder.remove();
				fail();
			}
			catch (ResourceWriterErrorException e)
			{
				assertTrue(true);
			}
		}
		catch (ResourceWriterErrorException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testAddResource()
	{
		DatabaseResources	resource_finder = DatabaseResourcesFactory.getInstance(mDatasource);
		try
		{
			resource_finder.addResource("just/some/resource", "the content of this resource\nyes it's there");
		}
		catch (ResourceWriterErrorException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetUnknownResource()
	{
		DatabaseResources	resource_finder = DatabaseResourcesFactory.getInstance(mDatasource);
		assertNull(resource_finder.getResource("this/resource/doesnt/exist.txt"));
	}

	public void testGetResourceByName()
	{
		DatabaseResources	resource_finder = DatabaseResourcesFactory.getInstance(mDatasource);
		assertNotNull(resource_finder.getResource("resources/test.txt"));
	}

	public void testUpdateResource()
	{
		DatabaseResources	resource_finder = DatabaseResourcesFactory.getInstance(mDatasource);
		try
		{
			String content1 = "the content of this resource\nyes it's there";
			String content2 = "the content of this resource has been modified";

			resource_finder.addResource("resources/test.txt", content1);

			String	result1 = resource_finder.getContent("resources/test.txt");
			long	time1 = resource_finder.getModificationTime("resources/test.txt");
			assertEquals(content1, result1);

			assertTrue(resource_finder.updateResource("resources/test.txt", content2));

			String	result2 = resource_finder.getContent("resources/test.txt");
			long	time2 = resource_finder.getModificationTime("resources/test.txt");
			assertEquals(content2, result2);

			assertTrue(time1 != time2);
		}
		catch (ResourceFinderErrorException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		catch (ResourceWriterErrorException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testUpdateMissingResource()
	{
		DatabaseResources	resource_finder = DatabaseResourcesFactory.getInstance(mDatasource);
		try
		{
			assertNull(resource_finder.getContent("resources/test_blah.txt"));
			assertFalse(resource_finder.updateResource("resources/test_blah.txt", "blah"));
			assertNull(resource_finder.getContent("resources/test_blah.txt"));
		}
		catch (ResourceFinderErrorException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		catch (ResourceWriterErrorException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testRemoveResource()
	{
		DatabaseResources	resource_finder = DatabaseResourcesFactory.getInstance(mDatasource);
		try
		{
			assertNotNull(resource_finder.getContent("resources/test.txt"));
			assertTrue(resource_finder.removeResource("resources/test.txt"));
			assertNull(resource_finder.getContent("resources/test.txt"));
			assertFalse(resource_finder.removeResource("resources/test.txt"));
		}
		catch (ResourceFinderErrorException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		catch (ResourceWriterErrorException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetUnknownStreamByName()
	{
		DatabaseResources	resource_finder = DatabaseResourcesFactory.getInstance(mDatasource);

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
		DatabaseResources	resource_finder = DatabaseResourcesFactory.getInstance(mDatasource);

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
		DatabaseResources	resource_finder = DatabaseResourcesFactory.getInstance(mDatasource);

		try
		{
			resource_finder.useStream("resources/test.txt", new InputStreamUser() {
					public Object useInputStream(InputStream stream)
					throws InnerClassException
					{
						assertNotNull(stream);
						try
						{
							assertEquals(mResource, FileUtils.readString(stream));
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

	public void testGetStreamByResource()
	{
		DatabaseResources	resource_finder = DatabaseResourcesFactory.getInstance(mDatasource);

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
							assertEquals(mResource, FileUtils.readString(stream));
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
		DatabaseResources	resource_finder = DatabaseResourcesFactory.getInstance(mDatasource);

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
		DatabaseResources	resource_finder = DatabaseResourcesFactory.getInstance(mDatasource);

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
		DatabaseResources	resource_finder = DatabaseResourcesFactory.getInstance(mDatasource);

		try
		{
			String content = resource_finder.getContent("resources/test.txt");
			assertNotNull(content);
			assertEquals(content, mResource);
		}
		catch (ResourceFinderErrorException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetContentByResource()
	{
		DatabaseResources	resource_finder = DatabaseResourcesFactory.getInstance(mDatasource);

		URL resource = resource_finder.getResource("resources/test.txt");
		try
		{
			String content = resource_finder.getContent(resource);
			assertNotNull(content);
			assertEquals(content, mResource);
		}
		catch (ResourceFinderErrorException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testGetContentByNameAndEncoding()
	{
		DatabaseResources	resource_finder = DatabaseResourcesFactory.getInstance(mDatasource);
		
		try
		{
			String content = resource_finder.getContent("resources/test-utf8.txt", "UTF-8");
			assertNotNull(content);
			assertEquals(content, mResourceUtf8);
		}
		catch (ResourceFinderErrorException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testGetContentByResourceAndEncoding()
	{
		DatabaseResources	resource_finder = DatabaseResourcesFactory.getInstance(mDatasource);

		URL resource = resource_finder.getResource("resources/test-utf8.txt");
		try
		{
			String content = resource_finder.getContent(resource, "UTF-8");
			assertNotNull(content);
			assertEquals(content, mResourceUtf8);
		}
		catch (ResourceFinderErrorException e)
		{
			assertFalse(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetUnknownModificationTimeByName()
	{
		DatabaseResources	resource_finder = DatabaseResourcesFactory.getInstance(mDatasource);

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
		DatabaseResources	resource_finder = DatabaseResourcesFactory.getInstance(mDatasource);

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
		DatabaseResources	resource_finder = DatabaseResourcesFactory.getInstance(mDatasource);

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
		DatabaseResources	resource_finder = DatabaseResourcesFactory.getInstance(mDatasource);

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
