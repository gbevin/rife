/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestReloadDeclarations.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpNotFoundException;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.uwyn.rife.TestCaseServerside;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.FileUtils;
import java.io.File;
import java.net.InetAddress;
import java.net.URL;

public class TestReloadDeclarations extends TestCaseServerside
{
	public TestReloadDeclarations(int siteType, String name)
	{
		super(siteType, name);
	}

	public void testReloadElementDeclaration()
	throws Exception
	{
		try
		{
			// setup the temporary directory
			String site_dir = RifeConfig.Global.getTempPath()+File.separator+"reloadsite";
			File site_dir_file = new File(site_dir);
			site_dir_file.mkdirs();
			String element_dir = RifeConfig.Global.getTempPath()+File.separator+"reloadelement";
			File element_dir_file = new File(element_dir);
			element_dir_file.mkdirs();

			// setup the site xml file
			ResourceFinder	resource_finder = null;
			URL				resource = null;
			File			site_file = null;
			File			element_file = null;

			resource_finder = ResourceFinderClasspath.getInstance();

			// create the site xml file
			site_file = new File(site_dir + File.separator + "site.xml");
			site_file.delete();
			element_file = new File(element_dir + File.separator + "simple.xml");
			element_file.delete();
			resource = resource_finder.getResource("site/reload_element_blueprint.xml");
			FileUtils.copy(resource.openStream(), site_file);

			// create the first element xml file
			resource = resource_finder.getResource("element/engine/simple_plain.xml");
			FileUtils.copy(resource.openStream(), element_file);

			// test the site
			setupSite("reloadsite/site.xml");
			WebConversation	conversation = new WebConversation();
			WebRequest request = null;
			WebResponse response = null;

			request = new GetMethodWebRequest("http://localhost:8181/reloadtest");

			// get the host name
			String hostname = InetAddress.getByName("127.0.0.1").getHostName();

			// get the content of the first element
			response = conversation.getResponse(request);
			assertEquals("text/plain", response.getContentType());
			assertEquals("Just some text 127.0.0.1:"+hostname+":.SIMPLE:", response.getText());

			RifeConfig.Global.setAutoReloadDelay(3000);

			// wait a second
			Thread.sleep(1000);

			// overwrite the element file with new content
			resource = resource_finder.getResource("element/engine/simple_html.xml");
			FileUtils.copy(resource.openStream(), element_file);

			// wait a second
			Thread.sleep(1000);

			// get the content of the first element
			response = conversation.getResponse(request);
			assertEquals("text/plain", response.getContentType());
			assertEquals("Just some text 127.0.0.1:"+hostname+":.SIMPLE:", response.getText());

			// wait two seconds
			Thread.sleep(2000);

			// get the content of the new element
			response = conversation.getResponse(request);
			assertEquals("text/html", response.getContentType());
			assertEquals("Just some text 127.0.0.1:"+hostname+":.SIMPLE:", response.getText());

			// clean up the copied files
			element_file.delete();
			site_file.delete();
		}
		catch (Throwable e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}
		finally
		{
			RifeConfig.Global.setAutoReloadDelay(0);
		}
	}

	public void testReloadSiteDeclaration()
	throws Exception
	{
		try
		{
			// setup the temporary directory
			String site_dir = RifeConfig.Global.getTempPath()+File.separator+"reloadsite";
			File site_dir_file = new File(site_dir);
			site_dir_file.mkdirs();
			String element_dir = RifeConfig.Global.getTempPath()+File.separator+"reloadelement";
			File element_dir_file = new File(element_dir);
			element_dir_file.mkdirs();

			// setup the site xml file
			ResourceFinder	resource_finder = null;
			URL				resource = null;
			File			site_file = null;
			File			element_file = null;

			resource_finder = ResourceFinderClasspath.getInstance();
			site_file = new File(site_dir + File.separator + "site.xml");
			site_file.delete();
			element_file = new File(element_dir + File.separator + "simple.xml");
			element_file.delete();

			// create the site xml file
			resource = resource_finder.getResource("site/reload_element_blueprint.xml");
			FileUtils.copy(resource.openStream(), site_file);

			// create the first element xml file
			resource = resource_finder.getResource("element/engine/simple_html.xml");
			FileUtils.copy(resource.openStream(), element_file);

			// test the site
			setupSite("reloadsite/site.xml");
			WebConversation	conversation = new WebConversation();
			WebRequest request = null;
			WebResponse response = null;

			// Get the host name
			String hostname = InetAddress.getByName("127.0.0.1").getHostName();

			request = new GetMethodWebRequest("http://localhost:8181/reloadtest");

			// get the content of the first element
			response = conversation.getResponse(request);
			assertEquals("Just some text 127.0.0.1:"+hostname+":.SIMPLE:", response.getText());

			RifeConfig.Global.setAutoReloadDelay(2000);

			// wait a second
			Thread.sleep(1000);

			// overwrite the element file with new content
			resource = resource_finder.getResource("site/reload_site_blueprint.xml");
			FileUtils.copy(resource.openStream(), site_file);

			// get the content of the first element
			response = conversation.getResponse(request);
			assertEquals("Just some text 127.0.0.1:"+hostname+":.SIMPLE:", response.getText());

			// wait two seconds and a half
			Thread.sleep(2500);

			// get the content of the new element
			try
			{
				response = conversation.getResponse(request);
				fail();
			}
			catch (HttpNotFoundException e)
			{
				assertTrue(true);
			}

			request = new GetMethodWebRequest("http://localhost:8181/newurl");
			response = conversation.getResponse(request);
			assertEquals("Just some text 127.0.0.1:"+hostname+":.SIMPLE:", response.getText());

			// clean up the copied files
			element_file.delete();
			site_file.delete();
		}
		catch (Throwable e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}
		finally
		{
			RifeConfig.Global.setAutoReloadDelay(0);
		}
	}

	public void testReloadSubsiteDeclaration()
	throws Exception
	{
		try
		{
			// setup the temporary directory
			String site_dir = RifeConfig.Global.getTempPath()+File.separator+"reloadsite";
			File site_dir_file = new File(site_dir);
			site_dir_file.mkdirs();
			String element_dir = RifeConfig.Global.getTempPath()+File.separator+"reloadelement";
			File element_dir_file = new File(element_dir);
			element_dir_file.mkdirs();

			// setup the site xml file
			ResourceFinder	resource_finder = null;
			URL				resource = null;
			File			site_file = null;
			File			subsite_file = null;
			File			element_file = null;

			resource_finder = ResourceFinderClasspath.getInstance();
			site_file = new File(site_dir + File.separator + "site.xml");
			site_file.delete();
			subsite_file = new File(site_dir + File.separator + "subsite.xml");
			subsite_file.delete();
			element_file = new File(element_dir + File.separator + "simple.xml");
			element_file.delete();

			// create the site xml file
			resource = resource_finder.getResource("site/reload_subsite_blueprint.xml");
			FileUtils.copy(resource.openStream(), site_file);

			// create the site xml file
			resource = resource_finder.getResource("site/reload_element_blueprint.xml");
			FileUtils.copy(resource.openStream(), subsite_file);

			// create the first element xml file
			resource = resource_finder.getResource("element/engine/simple_html.xml");
			FileUtils.copy(resource.openStream(), element_file);

			// test the site
			setupSite("reloadsite/site.xml");
			WebConversation	conversation = new WebConversation();
			WebRequest request = null;
			WebResponse response = null;

			// Get the host name
			String hostname = InetAddress.getByName("127.0.0.1").getHostName();

			request = new GetMethodWebRequest("http://localhost:8181/subsite/reloadtest");

			// get the content of the first element
			response = conversation.getResponse(request);
			assertEquals("Just some text 127.0.0.1:"+hostname+":.SUBSITE.SIMPLE:", response.getText());

			RifeConfig.Global.setAutoReloadDelay(2000);

			// wait a second
			Thread.sleep(1000);

			// overwrite the element file with new content
			resource = resource_finder.getResource("site/reload_site_blueprint.xml");
			FileUtils.copy(resource.openStream(), subsite_file);

			// get the content of the first element
			response = conversation.getResponse(request);
			assertEquals("Just some text 127.0.0.1:"+hostname+":.SUBSITE.SIMPLE:", response.getText());

			// wait two seconds and a half
			Thread.sleep(2500);

			// get the content of the new element
			try
			{
				response = conversation.getResponse(request);
				fail();
			}
			catch (HttpNotFoundException e)
			{
				assertTrue(true);
			}

			request = new GetMethodWebRequest("http://localhost:8181/subsite/newurl");
			response = conversation.getResponse(request);
			assertEquals("Just some text 127.0.0.1:"+hostname+":.SUBSITE.SIMPLE:", response.getText());

			// clean up the copied files
			element_file.delete();
			subsite_file.delete();
			site_file.delete();
		}
		catch (Throwable e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}
		finally
		{
			RifeConfig.Global.setAutoReloadDelay(0);
		}
	}

	public void testMoveElementDeclaration()
	throws Exception
	{
		try
		{
			// setup the temporary directory
			String site_dir = RifeConfig.Global.getTempPath()+File.separator+"movesite";
			File site_dir_file = new File(site_dir);
			site_dir_file.mkdirs();
			String element_dir = RifeConfig.Global.getTempPath()+File.separator+"moveelement";
			File element_dir_file = new File(element_dir);
			element_dir_file.mkdirs();

			// setup the site xml file
			ResourceFinder	resource_finder = null;
			URL				resource = null;
			File			site_file = null;
			File			element_file = null;

			resource_finder = ResourceFinderClasspath.getInstance();
			site_file = new File(site_dir + File.separator + "site.xml");
			site_file.delete();
			element_file = new File(element_dir + File.separator + "simple1.xml");
			element_file.delete();

			// create the site xml file
			resource = resource_finder.getResource("site/move_element_blueprint1.xml");
			FileUtils.copy(resource.openStream(), site_file);

			// create the first element xml file
			resource = resource_finder.getResource("element/engine/simple_plain.xml");
			FileUtils.copy(resource.openStream(), element_file);

			// test the site
			setupSite("movesite/site.xml");
			WebConversation	conversation = new WebConversation();
			WebRequest request = null;
			WebResponse response = null;

			request = new GetMethodWebRequest("http://localhost:8181/movetest");

			// Get the host name
			String hostname = InetAddress.getByName("127.0.0.1").getHostName();

			// get the content of the first element
			response = conversation.getResponse(request);
			assertEquals("text/plain", response.getContentType());
			assertEquals("Just some text 127.0.0.1:"+hostname+":.SIMPLE:", response.getText());

			RifeConfig.Global.setAutoReloadDelay(2000);

			// wait a second
			Thread.sleep(1000);

			// overwrite the site declaration file with new content to reference the new name of the element
			resource = resource_finder.getResource("site/move_element_blueprint2.xml");
			FileUtils.copy(resource.openStream(), site_file);

			// move the element
			File new_element_file = new File(element_dir + File.separator + "simple2.xml");
			new_element_file.delete();
			element_file.renameTo(new_element_file);

			// get the content of the first element
			response = conversation.getResponse(request);
			assertEquals("text/plain", response.getContentType());
			assertEquals("Just some text 127.0.0.1:"+hostname+":.SIMPLE:", response.getText());

			// wait two seconds and a half
			Thread.sleep(2500);

			// get the content of the moved element
			response = conversation.getResponse(request);
			assertEquals("text/plain", response.getContentType());
			assertEquals("Just some text 127.0.0.1:"+hostname+":.SIMPLE:", response.getText());

			// clean up the copied files
			element_file.delete();
			site_file.delete();
		}
		catch (Throwable e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}
		finally
		{
			RifeConfig.Global.setAutoReloadDelay(0);
		}
	}

	public void testMoveSubsiteDeclaration()
	throws Exception
	{
		try
		{
			// setup the temporary directory
			String site_dir = RifeConfig.Global.getTempPath()+File.separator+"movesite";
			File site_dir_file = new File(site_dir);
			site_dir_file.mkdirs();
			String element_dir = RifeConfig.Global.getTempPath()+File.separator+"moveelement";
			File element_dir_file = new File(element_dir);
			element_dir_file.mkdirs();

			// setup the site xml file
			ResourceFinder	resource_finder = null;
			URL				resource = null;
			File			site_file = null;
			File			subsite_file = null;
			File			element_file = null;

			resource_finder = ResourceFinderClasspath.getInstance();
			site_file = new File(site_dir + File.separator + "site.xml");
			site_file.delete();
			subsite_file = new File(site_dir + File.separator + "subsite1.xml");
			subsite_file.delete();
			element_file = new File(element_dir + File.separator + "simple1.xml");
			element_file.delete();

			// create the site xml file
			resource = resource_finder.getResource("site/move_subsite_blueprint1.xml");
			FileUtils.copy(resource.openStream(), site_file);

			// create the site xml file
			resource = resource_finder.getResource("site/move_element_blueprint1.xml");
			FileUtils.copy(resource.openStream(), subsite_file);

			// create the first element xml file
			resource = resource_finder.getResource("element/engine/simple_html.xml");
			FileUtils.copy(resource.openStream(), element_file);

			// test the site
			setupSite("movesite/site.xml");
			WebConversation	conversation = new WebConversation();
			WebRequest request = null;
			WebResponse response = null;

			// Get the host name
			String hostname = InetAddress.getByName("127.0.0.1").getHostName();

			request = new GetMethodWebRequest("http://localhost:8181/subsite/movetest");

			// get the content of the first element
			response = conversation.getResponse(request);
			assertEquals("text/html", response.getContentType());
			assertEquals("Just some text 127.0.0.1:"+hostname+":.SUBSITE.SIMPLE:", response.getText());

			RifeConfig.Global.setAutoReloadDelay(2000);

			// wait a second
			Thread.sleep(1000);

			// overwrite the site declaration file with new content to reference the new name of the subsite
			resource = resource_finder.getResource("site/move_subsite_blueprint2.xml");
			FileUtils.copy(resource.openStream(), subsite_file);

			// move the subsite
			File new_subsite_file = new File(site_dir + File.separator + "subsite2.xml");
			new_subsite_file.delete();
			subsite_file.renameTo(new_subsite_file);

			// get the content of the first element
			response = conversation.getResponse(request);
			assertEquals("text/html", response.getContentType());
			assertEquals("Just some text 127.0.0.1:"+hostname+":.SUBSITE.SIMPLE:", response.getText());

			// wait two seconds and a half
			Thread.sleep(2500);

			// get the content of the moved subsite
			response = conversation.getResponse(request);
			assertEquals("text/html", response.getContentType());
			assertEquals("Just some text 127.0.0.1:"+hostname+":.SUBSITE.SIMPLE:", response.getText());

			// clean up the copied files
			element_file.delete();
			subsite_file.delete();
			site_file.delete();
		}
		catch (Throwable e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}
		finally
		{
			RifeConfig.Global.setAutoReloadDelay(0);
		}
	}
}

