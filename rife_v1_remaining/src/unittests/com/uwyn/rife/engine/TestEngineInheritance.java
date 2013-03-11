/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestEngineInheritance.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import java.net.URL;

import com.meterware.httpunit.*;

import com.uwyn.rife.TestCaseServerside;
import com.uwyn.rife.engine.exceptions.ExitTargetUrlMissingException;
import com.uwyn.rife.engine.exceptions.RequestAccessDeniedException;
import com.uwyn.rife.engine.exceptions.SubmissionInheritanceUrlMissingException;

public class TestEngineInheritance extends TestCaseServerside
{
	public TestEngineInheritance(int siteType, String name)
	{
		super(siteType, name);
	}
	
	public void testInheritanceSimple()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	link = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/simple");
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		response = link.click();
		assertEquals("This is the child", response.getText());
	}

	public void testInheritanceBean()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	link = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/bean");
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		response = link.click();
		assertEquals("This is the child", response.getText());
	}
	
	public void testInheritanceSimpleOutjection()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	link = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/bean/outjection");
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		response = link.click();
		assertEquals("This is the child", response.getText());
	}
	
	public void testInheritanceBeanOutjection()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	link = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/simple/outjection");
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		response = link.click();
		assertEquals("This is the child", response.getText());
	}
	
	public void testInheritanceInterface()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	link = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/interface");
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		response = link.click();
		assertEquals("This is the interface child", response.getText());
	}

	public void testInheritanceGlobalvar()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	link = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/globalvar");
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		response = link.click();
		assertEquals("This is the child", response.getText());
	}

	public void testInheritanceTargetRetrievel()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	link = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/targetretrieval");
		response = conversation.getResponse(request);
		assertTrue(0 == response.getText().indexOf("element/inheritance/targetretrieval_child.xml"));
		link = response.getLinkWith("activate child");
		response = link.click();
		assertEquals("element/inheritance/targetretrieval_child.xml : this is the child", response.getText());
	}

	public void testInheritanceCookie()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	link = null;
		
		// check if the child trigger is activated by an output cookie
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/cookie");
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		response = link.click();
		assertEquals("This is the child", response.getText());

		// check if the child trigger is activated through an existing cookie
		conversation.addCookie("trigger", "ok");
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/cookie");
		response = conversation.getResponse(request);
		assertEquals("This is the child", response.getText());
	}
	
	public void testInheritanceCookieOutjection()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	link = null;
		
		// check if the child trigger is activated by an output cookie
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/cookie/outjection");
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		response = link.click();
		assertEquals("This is the child", response.getText());
		
		// check if the child trigger is activated through an existing cookie
		conversation.addCookie("trigger", "ok");
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/cookie/outjection");
		response = conversation.getResponse(request);
		assertEquals("This is the child", response.getText());
	}
	
	public void testInheritanceGlobalCookie()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	link = null;
		
		// check if the child trigger is activated by an output cookie
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/globalcookie");
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		response = link.click();
		assertEquals("This is the child", response.getText());

		// check if the child trigger is activated through an existing cookie
		conversation.addCookie("trigger", "ok");
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/globalcookie");
		response = conversation.getResponse(request);
		assertEquals("This is the child", response.getText());
	}
	
	public void testInheritanceGlobalCookieOutjection()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	link = null;
		
		// check if the child trigger is activated by an output cookie
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/globalcookie/outjection");
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		response = link.click();
		assertEquals("This is the child", response.getText());
		
		// check if the child trigger is activated through an existing cookie
		conversation.addCookie("trigger", "ok");
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/globalcookie/outjection");
		response = conversation.getResponse(request);
		assertEquals("This is the child", response.getText());
	}
	
	public void testInheritanceInputdefault()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/inputdefault");
		response = conversation.getResponse(request);
		assertEquals("This is the child", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/inputdefault");
		request.setParameter("trigger", "dontdoit");
		response = conversation.getResponse(request);
		assertEquals("This is the parent", response.getText());
	}

	public void testInheritanceIncookiedefault()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/incookiedefault");
		response = conversation.getResponse(request);
		assertEquals("This is the child", response.getText());
		
		conversation.addCookie("trigger", "dontdoit");
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/incookiedefault");
		response = conversation.getResponse(request);
		assertEquals("This is the parent", response.getText());
	}

	public void testInheritanceOutputdefault()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/outputdefault");
		response = conversation.getResponse(request);
		assertEquals("This is the parentThis is the child", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/outputdefault");
		request.setParameter("trigger", "noinfluence");
		response = conversation.getResponse(request);
		assertEquals("This is the parentThis is the child", response.getText());
	}

	public void testInheritanceOutcookiedefault()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/outcookiedefault");
		response = conversation.getResponse(request);
		assertEquals("This is the parentThis is the child", response.getText());
		
		conversation.addCookie("trigger", "noinfluence");
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/outcookiedefault");
		response = conversation.getResponse(request);
		assertEquals("This is the parentThis is the child", response.getText());
	}

	public void testInheritanceInputoutputdefault()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/inputoutputdefault");
		response = conversation.getResponse(request);
		assertEquals("This is the parentThis is the child", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/inputoutputdefault");
		request.setParameter("trigger", "doit");
		response = conversation.getResponse(request);
		assertEquals("This is the child", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/inputoutputdefault");
		request.setParameter("trigger", "dontdoit");
		response = conversation.getResponse(request);
		assertEquals("This is the parentThis is the child", response.getText());
	}

	public void testInheritanceIncookieoutcookiedefault()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		conversation = new WebConversation();
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/incookieoutcookiedefault");
		response = conversation.getResponse(request);
		assertEquals("This is the parentThis is the child", response.getText());
		
		conversation = new WebConversation();
		conversation.addCookie("trigger", "doit");
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/incookieoutcookiedefault");
		response = conversation.getResponse(request);
		assertEquals("This is the child", response.getText());
		
		conversation = new WebConversation();
		conversation.addCookie("trigger", "dontdoit");
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/incookieoutcookiedefault");
		response = conversation.getResponse(request);
		assertEquals("This is the parentThis is the child", response.getText());
	}

	public void testInheritanceGlobaldefault()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/globaldefault");
		response = conversation.getResponse(request);
		assertEquals("This is the child", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/globaldefault");
		request.setParameter("globaltrigger", "dontdoit");
		response = conversation.getResponse(request);
		assertEquals("This is the parent", response.getText());
	}

	public void testInheritanceGlobalcookiedefault()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/globalcookiedefault");
		response = conversation.getResponse(request);
		assertEquals("This is the child", response.getText());
		
		conversation = new WebConversation();
		conversation.addCookie("trigger", "dontdoit");
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/globalcookiedefault");
		response = conversation.getResponse(request);
		assertEquals("This is the parentThis is the child", response.getText());
	}

	public void testInheritanceNoTrigger()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	link = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/notrigger");
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		response = link.click();
		assertEquals("This is the child", response.getText());
	}
	
	public void testInheritanceAccessDenied()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/accessdenied");
		request.setParameter("trigger", "ok");
		try
		{
			response = conversation.getResponse(request);
			fail();
			assertNotNull(response);
		}
		catch (HttpInternalErrorException e)
		{
			assertTrue(getLogSink().getInternalException() instanceof RequestAccessDeniedException);
		}
	}
	
	public void testInheritanceParampreserve()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm	form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/parampreserve");
		request.setParameter("input1", "will this");
		request.setParameter("input2", "arrive");
		response = conversation.getResponse(request);

		form = response.getForms()[0];
		form.setParameter("param1", "don't go to child");
		response = form.submit();
		
		form = response.getForms()[0];
		form.setParameter("param1", "go to child");
		response = form.submit();
		
		assertEquals("will this,arrive", response.getText());
	}

	public void testInheritanceInputpreserve()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink link = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/inputpreserve");
		request.setParameter("input1", "will this");
		request.setParameter("input2", "arrive");
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		response = link.click();
		
		assertEquals("will this,arrive\n", response.getText());
	}

	public void testInheritancePathinfopreserve()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink link = null;
		
		String serverUrl = "http://localhost:8181";
		String page = "/inheritance/pathinfopreserve/will/this/arrive";
		request = new GetMethodWebRequest(serverUrl + page);
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		
		// Confirm that the url contains pathinfo before clicking it
		URL linkUrl = new URL(serverUrl + link.getURLString());
		assertEquals(page, linkUrl.getPath());
		
		response = link.click();
		assertEquals("/will/this/arrive", response.getText());
	}
	
	public void testInheritancePathinfoPreserveThroughPost() 
	throws Exception
	{
		setupSite("site/inheritance.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		String webappUrl = "http://localhost:8181";
		String urlWithPathInfo = "/inheritance/pathinfopreservethroughpost/will/this/arrive";
		request = new GetMethodWebRequest(webappUrl + urlWithPathInfo);
		response = conversation.getResponse(request);
		
		WebForm form = response.getFormWithName("activatechild");
		// Confirm that the form action contains pathinfo before submitting it
		String formAction = form.getAction();
		assertEquals(urlWithPathInfo, formAction );
		response = form.submit();
		
		assertEquals("/will/this/arrive", response.getText());
	}

	public void testInheritanceParamspreserveDirectTrigger()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/paramspreserve/directtrigger");
		request.setParameter("submission", "testsubmission");
		request.setParameter("param1", "will this");
		request.setParameter("param2", "arrive");
		response = conversation.getResponse(request);
		
		assertEquals("will this,arrive\n", response.getText());
	}

	public void testInheritanceInputpreserveDirectTrigger()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/inputpreserve/directtrigger");
		request.setParameter("input1", "will this");
		request.setParameter("input2", "arrive");
		response = conversation.getResponse(request);
		
		assertEquals("will this,arrive\n", response.getText());
	}

	public void testInheritancePathinfopreserveDirectTrigger()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/pathinfopreserve/directtrigger/will/this/arrive");
		response = conversation.getResponse(request);
		
		assertEquals("/will/this/arrive", response.getText());
	}

	public void testInheritanceParamspreserveMiddle()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/paramspreserve/middle");
		request.setParameter("submission", "testsubmission");
		request.setParameter("param1", "will this");
		request.setParameter("param2", "arrive");
		response = conversation.getResponse(request);
		
		assertEquals("will this,arrive\n", response.getText());
	}
	
	public void testInheritanceDeep()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm	form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/deep");
		request.setParameter("childinput1", "will this");
		request.setParameter("childinput2", "arrive");
		response = conversation.getResponse(request);

		form = response.getFormWithName("formparent3");
		form.setParameter("activationparent3", "don't go to child");
		response = form.submit();
		
		form = response.getFormWithName("formparent3");
		form.setParameter("activationparent3", "go to child");
		response = form.submit();
		
		form = response.getFormWithName("formparent2");
		form.setParameter("activationparent2", "don't go to child");
		response = form.submit();
		
		form = response.getFormWithName("formparent2");
		form.setParameter("activationparent2", "go to child");
		response = form.submit();
		
		form = response.getFormWithName("formparent1");
		form.setParameter("activationparent1", "don't go to child");
		response = form.submit();
		
		form = response.getFormWithName("formparent1");
		form.setParameter("activationparent1", "go to child");
		response = form.submit();
		
		assertEquals("will this,arrive", response.getText());
	}

	public void testInheritanceExits()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm	form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/exits");
		request.setParameter("childinput1", "will this");
		request.setParameter("childinput2", "arrive too");
		response = conversation.getResponse(request);

		form = response.getFormWithName("formparent2");
		form.setParameter("activationparent2", "don't go to child");
		response = form.submit();

		form = response.getFormWithName("formparent2");
		form.setParameter("activationparent2", "go to child");
		response = form.submit();
		
		form = response.getFormWithName("formparent1");
		form.setParameter("activationparent1", "don't go to exit");
		response = form.submit();
		
		form = response.getFormWithName("formparent1");
		form.setParameter("activationparent1", "go to exit");
		response = form.submit();
		
		form = response.getFormWithName("formparent1exit1");
		form.setParameter("activationparent1exit1", "don't go to exit");
		response = form.submit();
		
		form = response.getFormWithName("formparent1exit1");
		form.setParameter("activationparent1exit1", "go to exit");
		response = form.submit();
		
		form = response.getFormWithName("formparent1exit2");
		form.setParameter("activationparent1exit2", "don't go to child");
		response = form.submit();

		form = response.getFormWithName("formparent1exit2");
		form.setParameter("activationparent1exit2", "go to child");
		response = form.submit();

		form = response.getFormWithName("formparent2");
		form.setParameter("activationparent2", "don't go to child");
		response = form.submit();

		form = response.getFormWithName("formparent2");
		form.setParameter("activationparent2", "go to child");
		response = form.submit();

		assertEquals("will this,arrive too", response.getText());
	}

	public void testInheritanceExitsCancelInheritance()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm	form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/exits/cancelinheritance");
		request.setParameter("childinput1", "will this");
		request.setParameter("childinput2", "arrive too");
		response = conversation.getResponse(request);
	
		form = response.getFormWithName("formparent2");
		form.setParameter("activationparent2", "don't go to child");
		response = form.submit();

		form = response.getFormWithName("formparent2");
		form.setParameter("activationparent2", "go to child");
		response = form.submit();
		
		form = response.getFormWithName("formparent1");
		form.setParameter("activationparent1", "don't go to exit");
		response = form.submit();
		
		form = response.getFormWithName("formparent1");
		form.setParameter("activationparent1", "go to exit");
		response = form.submit();
		
		form = response.getFormWithName("formparent1exit1");
		form.setParameter("activationparent1exit1", "don't go to exit");
		response = form.submit();
		
		form = response.getFormWithName("formparent1exit1");
		form.setParameter("activationparent1exit1", "go to exit");
		response = form.submit();
		
		form = response.getFormWithName("formparent1exit2");
		form.setParameter("activationparent1exit2", "don't go to child");
		response = form.submit();

		form = response.getFormWithName("formparent1exit2");
		form.setParameter("activationparent1exit2", "go to child");
		try
		{
			// the current inheritance structure will get cancelled
			// and a new one will be created for the EXITS_CANCELINHERITANCE_PARENT1
			// element which inherits from EXITS_CANCELINHERITANCE_PARENT2
			// since the latter has no url in the site structure, an error will
			// occur when it generates a submission url
			response = form.submit();
			fail();
		}
		catch (Throwable e)
		{
			Throwable internal_exception = getLogSink().getInternalException();
			assertTrue(internal_exception instanceof SubmissionInheritanceUrlMissingException);
			SubmissionInheritanceUrlMissingException internal_exception2 = (SubmissionInheritanceUrlMissingException)internal_exception;
			assertEquals(internal_exception2.getDeclarationName(), "element/inheritance/exits_parent1.xml");
			assertEquals(internal_exception2.getTargetDeclarationName(), "element/inheritance/exits_parent2.xml");
		}
	}

	public void testInheritanceGlobalexitsCancelInheritance()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm	form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/globalexits/cancelinheritance");
		request.setParameter("childinput1", "will this");
		request.setParameter("childinput2", "arrive too");
		response = conversation.getResponse(request);
	
		form = response.getFormWithName("formparent2");
		form.setParameter("activationparent2", "don't go to child");
		response = form.submit();

		form = response.getFormWithName("formparent2");
		form.setParameter("activationparent2", "go to child");
		response = form.submit();
		
		form = response.getFormWithName("formparent1");
		form.setParameter("activationparent1", "don't go to exit");
		response = form.submit();
		
		form = response.getFormWithName("formparent1");
		form.setParameter("activationparent1", "go to exit");
		response = form.submit();
		
		form = response.getFormWithName("formparent1exit1");
		form.setParameter("activationparent1exit1", "don't go to exit");
		response = form.submit();
		
		form = response.getFormWithName("formparent1exit1");
		form.setParameter("activationparent1exit1", "go to exit");
		response = form.submit();
		
		form = response.getFormWithName("formparent1exit2");
		form.setParameter("activationparent1exit2", "don't go to child");
		response = form.submit();

		form = response.getFormWithName("formparent1exit2");
		form.setParameter("activationparent1exit2", "go to child");
		try
		{
			// the current inheritance structure will get cancelled
			// and a new one will be created for the EXITS_CANCELINHERITANCE_PARENT1
			// element which inherits from EXITS_CANCELINHERITANCE_PARENT2
			// since the latter has no url in the site structure, an error will
			// occur when it generates a submission url
			response = form.submit();
			fail();
		}
		catch (Throwable e)
		{
			Throwable internal_exception = getLogSink().getInternalException();
			assertTrue(internal_exception instanceof SubmissionInheritanceUrlMissingException);
			SubmissionInheritanceUrlMissingException internal_exception2 = (SubmissionInheritanceUrlMissingException)internal_exception;
			assertEquals(internal_exception2.getDeclarationName(), "element/inheritance/globalexits_parent1.xml");
			assertEquals(internal_exception2.getTargetDeclarationName(), "element/inheritance/globalexits_parent2.xml");
		}
	}

	public void testInheritanceExitlinks()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	link = null;
		WebForm form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/exitlinks");
		request.setParameter("childinput1", "will this also");
		request.setParameter("childinput2", "arrive too");
		response = conversation.getResponse(request);

		link = response.getLinkWith("direct link");
		WebResponse response_no_form = link.click();
		assertEquals("no form", response_no_form.getText());

		link = response.getLinkWith("direct link with form");
		response = link.click();

		form = response.getFormWithName("formparent1exit1");
		form.setParameter("activationparent1exit1", "dont go to child");
		response = form.submit();
		
		form = response.getFormWithName("formparent1exit1");
		form.setParameter("activationparent1exit1", "go to child");
		response = form.submit();
		
		assertEquals("will this also,arrive too", response.getText());
	}

	public void testInheritanceExitlinksCancelInheritance()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;

		request = new GetMethodWebRequest("http://localhost:8181/inheritance/exitlinks/cancelinheritance");
		request.setParameter("childinput1", "will this also");
		request.setParameter("childinput2", "arrive too");
		try
		{
			conversation.getResponse(request);
			fail();
		}
		catch (Throwable e)
		{
			Throwable internal_exception = getLogSink().getInternalException();
			assertTrue(internal_exception instanceof ExitTargetUrlMissingException);
			ExitTargetUrlMissingException internal_exception2 = (ExitTargetUrlMissingException)internal_exception;
			assertEquals(internal_exception2.getSourceDeclarationName(), "element/inheritance/exitlinks_parent1.xml");
			assertEquals(internal_exception2.getExitName(), "exit1");
			assertEquals(internal_exception2.getTargetDeclarationName(), "element/inheritance/exitlinks_parent1_exit1.xml");
		}
	}

	public void testInheritanceExitDataflow()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/exitdataflow");
		request.setParameter("input1", "invalid");
		request.setParameter("childinput1", "some more data");
		request.setParameter("childinput2", "that has to arrive");
		response = conversation.getResponse(request);
		
		assertEquals("child not triggered", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/inheritance/exitdataflow");
		request.setParameter("input1", "validoutputs");
		request.setParameter("childinput1", "some more data");
		request.setParameter("childinput2", "that has to arrive");
		response = conversation.getResponse(request);
		
		assertEquals("some more data,that has to arrive", response.getText());
	}

	public void testInheritanceParampass()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm	form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/parampass");
		request.setParameter("childinput1", "will this");
		request.setParameter("childinput2", "arrive");
		response = conversation.getResponse(request);

		form = response.getFormWithName("formparent3");
		form.setParameter("activationparent3", "don't go to child");
		response = form.submit();
		
		form = response.getFormWithName("formparent3");
		form.setParameter("activationparent3", "go to child");
		response = form.submit();
		
		form = response.getFormWithName("formparent2");
		form.setParameter("activationparent2", "don't go to child");
		response = form.submit();
		
		form = response.getFormWithName("formparent2");
		form.setParameter("activationparent2", "go to child");
		response = form.submit();
		
		form = response.getFormWithName("formparent1");
		form.setParameter("activationparent1", "don't go to child");
		response = form.submit();
		
		form = response.getFormWithName("formparent1");
		form.setParameter("activationparent1", "go to child");
		response = form.submit();
		
		assertEquals("will this,arrive", response.getText());
	}

	public void testInheritanceSuccessive()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm	form = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/successive");
		request.setParameter("childinput1", "will this succession");
		request.setParameter("childinput2", "arrive too");
		response = conversation.getResponse(request);

		form = response.getFormWithName("formparent1");
		form.setParameter("activationparent1", "don't go to exit");
		response = form.submit();
		
		form = response.getFormWithName("formparent1");
		form.setParameter("activationparent1", "go to exit");
		response = form.submit();

		form = response.getFormWithName("formparent2");
		form.setParameter("activationparent2", "don't go to child");
		response = form.submit();

		form = response.getFormWithName("formparent2");
		form.setParameter("activationparent2", "go to child");
		response = form.submit();

		form = response.getFormWithName("formparent1exit");
		form.setParameter("activationparent1exit", "don't go to child");
		response = form.submit();

		form = response.getFormWithName("formparent1exit");
		form.setParameter("activationparent1exit", "go to child");
		response = form.submit();

		assertEquals("will this succession,arrive too", response.getText());
	}

	public void testInheritanceArrival()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	link = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/arrival/");
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		response = link.click();
		assertEquals("This is the child", response.getText());
	}

	public void testInheritanceGlobalScopeAbsolute()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	link = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/globalscope/absolute");
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		response = link.click();
		assertEquals("This is the child", response.getText());
	}

	public void testInheritanceGlobalScopeRelative()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	link = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/globalscope/relative");
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		response = link.click();
		assertEquals("This is the child", response.getText());
	}

	public void testInheritanceDifferentParentSameChild()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	link = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/differentparent_samechild1");
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		response = link.click();
		assertEquals("This is parent1."+
					 "This is the child", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/differentparent_samechild2");
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		response = link.click();
		assertEquals("This is parent2."+
					 "This is the child", response.getText());
	}

	public void testInheritanceGroup()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	link = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/group");
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		response = link.click();
		assertEquals("This is the child", response.getText());
	}

	public void testInheritanceGroupArrival()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	link = null;
		
		request = new GetMethodWebRequest("http://localhost:8181");
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		response = link.click();
		assertEquals("This is the child", response.getText());
	}

	public void testInheritanceMultipleSubmissions()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	link = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/severalsubmissions");
		response = conversation.getResponse(request);
		link = response.getLinkWith("activate child");
		response = link.click();

		link = response.getLinkWith("submit");
		response = link.click();

		assertFalse("the child received its submission".equals(response.getText()));
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/severalsubmissions?submission=activatechild&submission=childsubmission");
		response = conversation.getResponse(request);

		assertEquals("the child received its submission", response.getText());
	}
	
	public void testInheritanceGroupSubsite()
	throws Exception
	{
		setupSite("site/inheritance.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/child1");
		response = conversation.getResponse(request);
		assertEquals(".GROUP_SUBSITE_PARENT1\n" +
                ".GROUP_SUBSITE_PARENT2\n" +
                ".GROUP_SUBSITE_PARENT3\n" +
                ".CHILD1\n", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/group_subsite/child2");
		response = conversation.getResponse(request);
		assertEquals(".GROUP_SUBSITE_PARENT1\n" +
                ".GROUP_SUBSITE_PARENT2\n" +
                ".GROUP_SUBSITE_PARENT3\n" +
                ".GROUP_SUBSITE_PARENT4\n" +
                ".GROUP_SUBSITE_SUBSITE.CHILD2\n", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/group_subsite/child3");
		response = conversation.getResponse(request);
		assertEquals(".GROUP_SUBSITE_PARENT1\n" +
                ".GROUP_SUBSITE_PARENT2\n" +
                ".GROUP_SUBSITE_PARENT3\n" +
                ".GROUP_SUBSITE_PARENT4\n" +
                ".GROUP_SUBSITE_PARENT5\n" +
                ".GROUP_SUBSITE_SUBSITE.CHILD3\n", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/group_subsite/child4");
		response = conversation.getResponse(request);
		assertEquals(".GROUP_SUBSITE_PARENT1\n" +
                ".GROUP_SUBSITE_PARENT2\n" +
                ".GROUP_SUBSITE_PARENT3\n" +
                ".GROUP_SUBSITE_PARENT4\n" +
                ".GROUP_SUBSITE_PARENT5\n" +
                ".GROUP_SUBSITE_PARENT6\n" +
                ".GROUP_SUBSITE_SUBSITE.CHILD4\n", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/group_subsite/child5");
		response = conversation.getResponse(request);
		assertEquals(".GROUP_SUBSITE_PARENT1\n" +
                ".GROUP_SUBSITE_PARENT2\n" +
                ".GROUP_SUBSITE_PARENT3\n" +
                ".GROUP_SUBSITE_PARENT7\n" +
                ".GROUP_SUBSITE_PARENT8\n" +
                ".GROUP_SUBSITE_PARENT9\n" +
                ".GROUP_SUBSITE_SUBSITE.CHILD5\n", response.getText());
	}
}

