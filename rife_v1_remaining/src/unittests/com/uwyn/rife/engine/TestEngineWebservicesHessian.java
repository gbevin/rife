/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestEngineWebservicesHessian.java 3933 2008-04-25 20:41:45Z gbevin $
 */
package com.uwyn.rife.engine;

import com.caucho.hessian.client.HessianProxyFactory;
import com.uwyn.rife.TestCaseServerside;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.engine.testwebservices.webservices.hessian.BasicApi;
import com.uwyn.rife.engine.testwebservices.webservices.hessian.EchoApi;
import com.uwyn.rife.engine.testwebservices.webservices.hessian.HessianElementServiceApi;
import com.uwyn.rife.engine.exceptions.InputUnknownException;
import com.meterware.httpunit.*;

import java.util.logging.Logger;
import java.util.logging.Level;

public class TestEngineWebservicesHessian extends TestCaseServerside
{
	public TestEngineWebservicesHessian(int siteType, String name)
	{
		super(siteType, name);
	}

	// The Hessian tests have all been collapsed into one since there
	// seems to be a problem with connections not being established
	// in the Hessian proxy, after the Jetty server is restarted.
	// This is probably a race condition somewhere in the Hessian
	// code, don't want to waste time debugging this in detail since I
	// couldn't find anything obvious and this kind of situation is almost
	// never happening in the real world.
	public void testBasic()
	throws Exception
	{
		setupSite("site/webservices_hessian.xml");

		// ensure all connections are setup/closed
		Thread.sleep(5000);

		{
			String url = "http://localhost:8181/basic";

			HessianProxyFactory factory = new HessianProxyFactory();
			BasicApi basic = (BasicApi)factory.create(BasicApi.class, url);

			assertEquals("Hello, world", basic.hello());
		}
//	}

//	public void testEcho()
//	throws Exception
//	{
//		setupSite("site/webservices_hessian.xml");
//
		{
			String url = "http://localhost:8181/echo";

			HessianProxyFactory factory = new HessianProxyFactory();
			EchoApi echo = (EchoApi)factory.create(EchoApi.class, url);

			assertEquals("I got : 'Yooohoooo!'", echo.echo("Yooohoooo!"));
		}
//	}
//
//	public void testEchoAuth()
//	throws Exception
//	{
//		setupSite("site/webservices_hessian.xml");
//
		{
			HessianProxyFactory factory = new HessianProxyFactory();

			String url_noauth = "http://localhost:8181/echoauth";
			EchoApi echo_noauth = (EchoApi)factory.create(EchoApi.class, url_noauth);
			try
			{
				echo_noauth.echo("Yooohoooo!");
			}
			catch (Throwable e)
			{
				assertTrue(ExceptionUtils.getExceptionStackTrace(e), e instanceof com.caucho.hessian.client.HessianRuntimeException);
			}

			String url_auth = "http://localhost:8181/echoauth?login=gbevin&password=yeolpass&submission=credentials";
			EchoApi echo_auth = (EchoApi)factory.create(EchoApi.class, url_auth);
			assertEquals("I got : 'Yooohoooo!'", echo_auth.echo("Yooohoooo!"));

			WebConversation	conversation = new WebConversation();
			WebRequest request = null;
			WebResponse response = null;
			WebForm form = null;
			String auth_id;
			request = new GetMethodWebRequest("http://localhost:8181/auth");
			response = conversation.getResponse(request);
			form = response.getForms()[0];
			form.setParameter("login", "guest");
			form.setParameter("password", "guestpass");
			response = form.submit();
			assertEquals(0, response.getForms().length);
			auth_id = response.getTitle();

			url_auth = "http://localhost:8181/echoauth?authid="+auth_id;
			echo_auth = (EchoApi)factory.create(EchoApi.class, url_auth);
			assertEquals("I got : 'Yooohoooo!'", echo_auth.echo("Yooohoooo!"));
		}
//	}
//
//	public void testElementService()
//	throws Exception
//	{
//		setupSite("site/webservices_hessian.xml");
//
		{
			String url = "http://localhost:8181/elementservice?input1=value1&input2=value2";

			HessianProxyFactory factory = new HessianProxyFactory();
			HessianElementServiceApi service = (HessianElementServiceApi)factory.create(HessianElementServiceApi.class, url);

			assertEquals("value1", service.getElementInput("input1"));
			assertEquals("value2", service.getElementInput("input2"));
			assertNull(service.getElementInput("input3"));

			Level orig_level = Logger.getLogger("com.caucho.hessian.server.HessianSkeleton").getLevel();
			try
			{
				Logger.getLogger("com.caucho.hessian.server.HessianSkeleton").setLevel(Level.OFF);
				service.getElementInput("unknown");
				fail();
			}
			catch (InputUnknownException e)
			{
				assertEquals(e.getInputName(), "unknown");
			}
			finally
			{
				Logger.getLogger("com.caucho.hessian.server.HessianSkeleton").setLevel(orig_level);
			}
		}
//	}
	}
}

