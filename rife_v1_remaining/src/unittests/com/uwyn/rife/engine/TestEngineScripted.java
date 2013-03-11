/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestEngineScripted.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.meterware.httpunit.*;

import com.uwyn.rife.TestCaseServerside;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.FileUtils;
import com.uwyn.rife.tools.exceptions.FileUtilsErrorException;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class TestEngineScripted extends TestCaseServerside
{
	private static final boolean	BENCHMARK = false;
	private static final int		BENCHMARK_ITERATIONS = 200;
	
	public TestEngineScripted(int siteType, String name)
	{
		super(siteType, name);
	}

	public void testReloadScriptedJavaElement()
	throws Exception
	{
		// setup the temporary directory
		String site_dir = RifeConfig.Global.getTempPath()+File.separator+"reloadsite";
		File site_dir_file = new File(site_dir);
		site_dir_file.mkdirs();
		String element_dir = RifeConfig.Global.getTempPath()+File.separator+"reloadelement";
		File element_dir_file = new File(element_dir);
		element_dir_file.mkdirs();
		String scriptedjava_dir = RifeConfig.Global.getTempPath()+File.separator+"reloadscript";
		File scriptedjava_dir_file = new File(scriptedjava_dir);
		scriptedjava_dir_file.mkdirs();

		// setup the site xml file
		ResourceFinder	resource_finder = null;
		URL				resource = null;
		File			site_file = null;
		File			element_file = null;
		File			scriptedjava_file = null;

		resource_finder = ResourceFinderClasspath.getInstance();
		site_file = new File(site_dir + File.separator + "site.xml");
		site_file.delete();
		element_file = new File(element_dir + File.separator + "scriptedjava.xml");
		element_file.delete();
		scriptedjava_file = new File(scriptedjava_dir + File.separator + "scriptedjava.java");
		scriptedjava_file.delete();

		// create the site xml file
		resource = resource_finder.getResource("site/reload_scriptedjava_blueprint.xml");
		try
		{
			FileUtils.copy(resource.openStream(), site_file);
		}
		catch (FileUtilsErrorException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}
		catch (IOException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}

		// create the first element xml file
		resource = resource_finder.getResource("element/scripted/reload_scriptedjava_blueprint.xml");
		try
		{
			FileUtils.copy(resource.openStream(), element_file);
		}
		catch (FileUtilsErrorException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}
		catch (IOException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}

		// wait a second, in case a previous test xas executed
		// to ensure that the new file is considered as modified
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		// create the first script java element file
		resource = resource_finder.getResource("elementjava/reloadtest1.jav");
		try
		{
			FileUtils.copy(resource.openStream(), scriptedjava_file);
		}
		catch (FileUtilsErrorException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}
		catch (IOException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}

		// test the site
		setupSite("reloadsite/site.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/scriptedjava");

		// get the content of the first element
		response = conversation.getResponse(request);
		assertEquals("outer 1 : reloadtest1", response.getText());

		// wait a second
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		// overwrite the element file with new content
		resource = resource_finder.getResource("elementjava/reloadtest2.jav");
		try
		{
			FileUtils.copy(resource.openStream(), scriptedjava_file);
		}
		catch (FileUtilsErrorException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}
		catch (IOException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}

		// get the content of the new element
		response = conversation.getResponse(request);
		assertEquals("outer 2 : reloadtest2", response.getText());
		
		// perform a reload to see if the new element is properly being used afterwards
		response = conversation.getResponse(request);
		assertEquals("outer 2 : reloadtest2", response.getText());

		// clean up the copied files
		scriptedjava_file.delete();
		element_file.delete();
		site_file.delete();
	}

	public void testScriptedJavaPackageTest()
	throws Exception
	{
		setupSite("site/scripted.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/scripted/java/package");
		response = conversation.getResponse(request);
		assertEquals("this is a package test", response.getText());
	}

	public void testScriptedJava()
	throws Exception
	{
		setupSite("site/scripted.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;

		long start = System.currentTimeMillis();
		int i = 0;
		do
		{
			request = new GetMethodWebRequest("http://localhost:8181/scripted/java");
			request.setParameter("input1", "these values");
			request.setParameter("input2", "are scripted");
			response = conversation.getResponse(request);

			assertEquals("these values,are scripted", response.getText());

			request = new GetMethodWebRequest("http://localhost:8181/scripted/java");
			request.setParameter("input1", "form");
			response = conversation.getResponse(request);
			form = response.getForms()[0];
			form.setParameter("login", "gbevin");
			form.setParameter("password", "mypassword");
			response = form.submit();

			assertEquals("gbevin,mypassword", response.getText());
		}
		while (BENCHMARK && i++ <= BENCHMARK_ITERATIONS);
		if (BENCHMARK)	System.out.println("JAVA took "+(System.currentTimeMillis()-start));
	}

	public void testScriptedRhino()
	throws Exception
	{
		setupSite("site/scripted.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;

		long start = System.currentTimeMillis();
		int i = 0;
		do
		{
			request = new GetMethodWebRequest("http://localhost:8181/scripted/rhino");
			request.setParameter("input1", "these values");
			request.setParameter("input2", "are scripted");
			response = conversation.getResponse(request);

			assertEquals("these values,are scripted", response.getText());

			request = new GetMethodWebRequest("http://localhost:8181/scripted/rhino");
			request.setParameter("input1", "form");
			response = conversation.getResponse(request);
			form = response.getForms()[0];
			form.setParameter("login", "gbevin");
			form.setParameter("password", "mypassword");
			response = form.submit();

			assertEquals("gbevin,mypassword", response.getText());
		}
		while (BENCHMARK && i++ <= BENCHMARK_ITERATIONS);
		if (BENCHMARK)	System.out.println("RHINO took "+(System.currentTimeMillis()-start));
	}

	public void testScriptedJython()
	throws Exception
	{
		setupSite("site/scripted.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;

		long start = System.currentTimeMillis();
		int i = 0;
		do
		{
			request = new GetMethodWebRequest("http://localhost:8181/scripted/jython");
			request.setParameter("input1", "these values");
			request.setParameter("input2", "are scripted");
			response = conversation.getResponse(request);

			assertEquals("these values,are scripted", response.getText());

			request = new GetMethodWebRequest("http://localhost:8181/scripted/jython");
			request.setParameter("input1", "form");
			response = conversation.getResponse(request);
			form = response.getForms()[0];
			form.setParameter("login", "gbevin");
			form.setParameter("password", "mypassword");
			response = form.submit();

			assertEquals("gbevin,mypassword", response.getText());
		}
		while (BENCHMARK && i++ <= BENCHMARK_ITERATIONS);
		if (BENCHMARK)	System.out.println("JYTHON took "+(System.currentTimeMillis()-start));
	}

	public void testScriptedJruby()
	throws Exception
	{
		setupSite("site/scripted.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;
		
		long start = System.currentTimeMillis();
		int i = 0;
		do
		{
			request = new GetMethodWebRequest("http://localhost:8181/scripted/jruby");
			request.setParameter("input1", "these values");
			request.setParameter("input2", "are scripted");
			try
			{
				response = conversation.getResponse(request);
			}
			catch (Throwable e) {getLogSink().getInternalException().printStackTrace();}
			
			assertEquals("these values,are scripted", response.getText());
			
			request = new GetMethodWebRequest("http://localhost:8181/scripted/jruby");
			request.setParameter("input1", "form");
			response = conversation.getResponse(request);
			form = response.getForms()[0];
			form.setParameter("login", "gbevin");
			form.setParameter("password", "mypassword");
			response = form.submit();
			
			assertEquals("gbevin,mypassword", response.getText());
		}
		while (BENCHMARK && i++ <= BENCHMARK_ITERATIONS);
		if (BENCHMARK)	System.out.println("JRUBY took "+(System.currentTimeMillis()-start));
	}
	
	public void testScriptedJacl()
	throws Exception
	{
		setupSite("site/scripted.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;

		long start = System.currentTimeMillis();
		int i = 0;
		do
		{
			request = new GetMethodWebRequest("http://localhost:8181/scripted/jacl");
			request.setParameter("input1", "these values");
			request.setParameter("input2", "are scripted");
			response = conversation.getResponse(request);

			assertEquals("these values,are scripted", response.getText());

			request = new GetMethodWebRequest("http://localhost:8181/scripted/jacl");
			request.setParameter("input1", "form");
			response = conversation.getResponse(request);
			form = response.getForms()[0];
			form.setParameter("login", "gbevin");
			form.setParameter("password", "mypassword");
			response = form.submit();

			assertEquals("gbevin,mypassword", response.getText());
		}
		while (BENCHMARK && i++ <= BENCHMARK_ITERATIONS);
		if (BENCHMARK)	System.out.println("JACL took "+(System.currentTimeMillis()-start));
	}

	public void testScriptedBeanshell()
	throws Exception
	{
		setupSite("site/scripted.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;

		long start = System.currentTimeMillis();
		int i = 0;
		do
		{
			request = new GetMethodWebRequest("http://localhost:8181/scripted/beanshell");
			request.setParameter("input1", "these values");
			request.setParameter("input2", "are scripted");
			response = conversation.getResponse(request);

			assertEquals("these values,are scripted", response.getText());

			request = new GetMethodWebRequest("http://localhost:8181/scripted/beanshell");
			request.setParameter("input1", "form");
			response = conversation.getResponse(request);
			form = response.getForms()[0];
			form.setParameter("login", "gbevin");
			form.setParameter("password", "mypassword");
			response = form.submit();

			assertEquals("gbevin,mypassword", response.getText());
		}
		while (BENCHMARK && i++ <= BENCHMARK_ITERATIONS);
		if (BENCHMARK)	System.out.println("BEANSHELL took "+(System.currentTimeMillis()-start));
	}

	public void testScriptedPnuts()
	throws Exception
	{
		setupSite("site/scripted.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;

		long start = System.currentTimeMillis();
		int i = 0;
		do
		{
			request = new GetMethodWebRequest("http://localhost:8181/scripted/pnuts");
			request.setParameter("input1", "these values");
			request.setParameter("input2", "are scripted");
			response = conversation.getResponse(request);

			assertEquals("these values,are scripted", response.getText());

			request = new GetMethodWebRequest("http://localhost:8181/scripted/pnuts");
			request.setParameter("input1", "form");
			response = conversation.getResponse(request);
			form = response.getForms()[0];
			form.setParameter("login", "gbevin");
			form.setParameter("password", "mypassword");
			response = form.submit();

			assertEquals("gbevin,mypassword", response.getText());
		}
		while (BENCHMARK && i++ <= BENCHMARK_ITERATIONS);
		if (BENCHMARK)	System.out.println("PNUTS took "+(System.currentTimeMillis()-start));
	}

	public void testScriptedGroovy()
	throws Exception
	{
		setupSite("site/scripted.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;

		long start = System.currentTimeMillis();
		int i = 0;
		do
		{
			request = new GetMethodWebRequest("http://localhost:8181/scripted/groovy");
			request.setParameter("input1", "these values");
			request.setParameter("input2", "are scripted");
			response = conversation.getResponse(request);

			assertEquals("these values,are scripted", response.getText());

			request = new GetMethodWebRequest("http://localhost:8181/scripted/groovy");
			request.setParameter("input1", "form");
			response = conversation.getResponse(request);
			form = response.getForms()[0];
			form.setParameter("login", "gbevin");
			form.setParameter("password", "mypassword");
			response = form.submit();

			assertEquals("gbevin,mypassword", response.getText());
		}
		while (BENCHMARK && i++ <= BENCHMARK_ITERATIONS);
		if (BENCHMARK)	System.out.println("GROOVY took "+(System.currentTimeMillis()-start));
	}

	public void testScriptedJanino()
	throws Exception
	{
		setupSite("site/scripted.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;

		long start = System.currentTimeMillis();
		int i = 0;
		do
		{
			request = new GetMethodWebRequest("http://localhost:8181/scripted/janino");
			request.setParameter("input1", "these values");
			request.setParameter("input2", "are scripted");
			try
			{
				response = conversation.getResponse(request);
			}
			catch (Throwable e) {getLogSink().getInternalException().printStackTrace(); return;}

			assertEquals("these values,are scripted", response.getText());

			request = new GetMethodWebRequest("http://localhost:8181/scripted/janino");
			request.setParameter("input1", "form");
			response = conversation.getResponse(request);
			form = response.getForms()[0];
			form.setParameter("login", "gbevin");
			form.setParameter("password", "mypassword");
			response = form.submit();

			assertEquals("gbevin,mypassword", response.getText());
		}
		while (BENCHMARK && i++ <= BENCHMARK_ITERATIONS);
		if (BENCHMARK)	System.out.println("JANINO took "+(System.currentTimeMillis()-start));
	}
//
	public void testScriptedDefaultdirJava()
	throws Exception
	{
		setupSite("site/scripted.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;

		request = new GetMethodWebRequest("http://localhost:8181/scripted/defaultdir/java");
		request.setParameter("input1", "these values");
		request.setParameter("input2", "are scripted");
		try
		{
			response = conversation.getResponse(request);
		}
		catch (Throwable e) {getLogSink().getInternalException().printStackTrace();}

		assertEquals("defaultdir these values,are scripted", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/scripted/defaultdir/java");
		request.setParameter("input1", "form");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "mypassword");
		response = form.submit();

		assertEquals("defaultdir gbevin,mypassword", response.getText());
	}

	public void testScriptedDefaultdirRhino()
	throws Exception
	{
		setupSite("site/scripted.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;

		request = new GetMethodWebRequest("http://localhost:8181/scripted/defaultdir/rhino");
		request.setParameter("input1", "these values");
		request.setParameter("input2", "are scripted");
		response = conversation.getResponse(request);

		assertEquals("defaultdir these values,are scripted", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/scripted/defaultdir/rhino");
		request.setParameter("input1", "form");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "mypassword");
		response = form.submit();

		assertEquals("defaultdir gbevin,mypassword", response.getText());
	}

	public void testScriptedDefaultdirJython()
	throws Exception
	{
		setupSite("site/scripted.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;

		request = new GetMethodWebRequest("http://localhost:8181/scripted/defaultdir/jython");
		request.setParameter("input1", "these values");
		request.setParameter("input2", "are scripted");
		response = conversation.getResponse(request);

		assertEquals("defaultdir these values,are scripted", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/scripted/defaultdir/jython");
		request.setParameter("input1", "form");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "mypassword");
		response = form.submit();

		assertEquals("defaultdir gbevin,mypassword", response.getText());
	}
	
	public void testScriptedDefaultdirJruby()
	throws Exception
	{
		setupSite("site/scripted.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/scripted/defaultdir/jruby");
		request.setParameter("input1", "these values");
		request.setParameter("input2", "are scripted");
		response = conversation.getResponse(request);
		
		assertEquals("defaultdir these values,are scripted", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/scripted/defaultdir/jruby");
		request.setParameter("input1", "form");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "mypassword");
		response = form.submit();
		
		assertEquals("defaultdir gbevin,mypassword", response.getText());
	}
	
	public void testScriptedDefaultdirJacl()
	throws Exception
	{
		setupSite("site/scripted.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;

		request = new GetMethodWebRequest("http://localhost:8181/scripted/defaultdir/jacl");
		request.setParameter("input1", "these values");
		request.setParameter("input2", "are scripted");
		response = conversation.getResponse(request);

		assertEquals("defaultdir these values,are scripted", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/scripted/defaultdir/jacl");
		request.setParameter("input1", "form");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "mypassword");
		response = form.submit();

		assertEquals("defaultdir gbevin,mypassword", response.getText());
	}

	public void testScriptedDefaultdirBeanshell()
	throws Exception
	{
		setupSite("site/scripted.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;

		request = new GetMethodWebRequest("http://localhost:8181/scripted/defaultdir/beanshell");
		request.setParameter("input1", "these values");
		request.setParameter("input2", "are scripted");
		response = conversation.getResponse(request);

		assertEquals("defaultdir these values,are scripted", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/scripted/defaultdir/beanshell");
		request.setParameter("input1", "form");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "mypassword");
		response = form.submit();

		assertEquals("defaultdir gbevin,mypassword", response.getText());
	}

	public void testScriptedDefaultdirPnuts()
	throws Exception
	{
		setupSite("site/scripted.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;

		request = new GetMethodWebRequest("http://localhost:8181/scripted/defaultdir/pnuts");
		request.setParameter("input1", "these values");
		request.setParameter("input2", "are scripted");
		response = conversation.getResponse(request);

		assertEquals("defaultdir these values,are scripted", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/scripted/defaultdir/pnuts");
		request.setParameter("input1", "form");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "mypassword");
		response = form.submit();

		assertEquals("defaultdir gbevin,mypassword", response.getText());
	}

	public void testScriptedDefaultdirGroovy()
	throws Exception
	{
		setupSite("site/scripted.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;

		request = new GetMethodWebRequest("http://localhost:8181/scripted/defaultdir/groovy");
		request.setParameter("input1", "these values");
		request.setParameter("input2", "are scripted");
		response = conversation.getResponse(request);

		assertEquals("defaultdir these values,are scripted", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/scripted/defaultdir/groovy");
		request.setParameter("input1", "form");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "mypassword");
		response = form.submit();

		assertEquals("defaultdir gbevin,mypassword", response.getText());
	}

	public void testScriptedDefaultdirJanino()
	throws Exception
	{
		setupSite("site/scripted.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;

		request = new GetMethodWebRequest("http://localhost:8181/scripted/defaultdir/janino");
		request.setParameter("input1", "these values");
		request.setParameter("input2", "are scripted");
		response = conversation.getResponse(request);

		assertEquals("defaultdir these values,are scripted", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/scripted/defaultdir/janino");
		request.setParameter("input1", "form");
		response = conversation.getResponse(request);
		form = response.getForms()[0];
		form.setParameter("login", "gbevin");
		form.setParameter("password", "mypassword");
		response = form.submit();

		assertEquals("defaultdir gbevin,mypassword", response.getText());
	}

	public void testScriptedInheritanceJava()
	throws Exception
	{
		setupSite("site/scripted.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink link = null;

		request = new GetMethodWebRequest("http://localhost:8181/scripted/java/inheritance");
		response = conversation.getResponse(request);
		assertTrue(!response.getText().equals("This is the child"));

		request = new GetMethodWebRequest("http://localhost:8181/scripted/java/inheritance");
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		response = link.click();

		assertEquals("This is the child", response.getText());
	}

	public void testScriptedInheritanceRhino()
	throws Exception
	{
		setupSite("site/scripted.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink link = null;

		request = new GetMethodWebRequest("http://localhost:8181/scripted/rhino/inheritance");
		response = conversation.getResponse(request);
		assertTrue(!response.getText().equals("This is the child"));

		request = new GetMethodWebRequest("http://localhost:8181/scripted/rhino/inheritance");
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		response = link.click();

		assertEquals("This is the child", response.getText());
	}

	public void testScriptedInheritanceJython()
	throws Exception
	{
		setupSite("site/scripted.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink link = null;

		request = new GetMethodWebRequest("http://localhost:8181/scripted/jython/inheritance");
		response = conversation.getResponse(request);
		assertTrue(!response.getText().equals("This is the child"));

		request = new GetMethodWebRequest("http://localhost:8181/scripted/jython/inheritance");
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		response = link.click();

		assertEquals("This is the child", response.getText());
	}

	public void testScriptedInheritanceJruby()
	throws Exception
	{
		setupSite("site/scripted.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink link = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/scripted/jruby/inheritance");
		response = conversation.getResponse(request);
		assertTrue(!response.getText().equals("This is the child"));
		
		request = new GetMethodWebRequest("http://localhost:8181/scripted/jruby/inheritance");
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		response = link.click();
		
		assertEquals("This is the child", response.getText());
	}
	
	public void testScriptedInheritanceJacl()
	throws Exception
	{
		setupSite("site/scripted.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink link = null;

		request = new GetMethodWebRequest("http://localhost:8181/scripted/jacl/inheritance");
		response = conversation.getResponse(request);
		assertTrue(!response.getText().equals("This is the child"));

		request = new GetMethodWebRequest("http://localhost:8181/scripted/jacl/inheritance");
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		response = link.click();

		assertEquals("This is the child", response.getText());
	}

	public void testScriptedInheritanceBeanshell()
	throws Exception
	{
		setupSite("site/scripted.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink link = null;

		request = new GetMethodWebRequest("http://localhost:8181/scripted/beanshell/inheritance");
		response = conversation.getResponse(request);
		assertTrue(!response.getText().equals("This is the child"));

		request = new GetMethodWebRequest("http://localhost:8181/scripted/beanshell/inheritance");
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		response = link.click();

		assertEquals("This is the child", response.getText());
	}

	public void testScriptedInheritancePnuts()
	throws Exception
	{
		setupSite("site/scripted.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink link = null;

		request = new GetMethodWebRequest("http://localhost:8181/scripted/pnuts/inheritance");
		response = conversation.getResponse(request);
		assertTrue(!response.getText().equals("This is the child"));

		request = new GetMethodWebRequest("http://localhost:8181/scripted/pnuts/inheritance");
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		response = link.click();

		assertEquals("This is the child", response.getText());
	}

	public void testScriptedInheritanceGroovy()
	throws Exception
	{
		setupSite("site/scripted.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink link = null;

		request = new GetMethodWebRequest("http://localhost:8181/scripted/groovy/inheritance");
		response = conversation.getResponse(request);
		assertTrue(!response.getText().equals("This is the child"));

		request = new GetMethodWebRequest("http://localhost:8181/scripted/groovy/inheritance");
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		response = link.click();

		assertEquals("This is the child", response.getText());
	}

	public void testScriptedInheritanceJanino()
	throws Exception
	{
		setupSite("site/scripted.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink link = null;

		request = new GetMethodWebRequest("http://localhost:8181/scripted/janino/inheritance");
		response = conversation.getResponse(request);
		assertTrue(!response.getText().equals("This is the child"));

		request = new GetMethodWebRequest("http://localhost:8181/scripted/groovy/inheritance");
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		response = link.click();

		assertEquals("This is the child", response.getText());
	}
}

