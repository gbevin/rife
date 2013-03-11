/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestEngineSoapXFire.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.uwyn.rife.TestCaseServerside;
import com.uwyn.rife.engine.testwebservices.soap.xfire.CalculatorApi;
import com.uwyn.rife.engine.testwebservices.soap.xfire.EchoApi;
import com.uwyn.rife.engine.testwebservices.soap.xfire.XFireElementServiceApi;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.HttpUtils;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.invoker.ObjectInvoker;

public class TestEngineSoapXFire extends TestCaseServerside
{
	public TestEngineSoapXFire(int siteType, String name)
	{
		super(siteType, name);
	}
	
	public void testEcho()
	throws Exception
	{
		setupSite("site/soap_xfire.xml");
		
		String endpoint = "http://localhost:8181/echo";
		assertTrue(HttpUtils.retrievePage(endpoint + "?wsdl").getContent().startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
																					  "<wsdl:definitions targetNamespace=\"http://xfire.soap.testwebservices.engine.rife.uwyn.com\" xmlns:tns=\"http://xfire.soap.testwebservices.engine.rife.uwyn.com\" xmlns:wsdlsoap=\"http://schemas.xmlsoap.org/wsdl/soap/\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenc11=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:soapenc12=\"http://www.w3.org/2003/05/soap-encoding\" xmlns:soap11=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\">"));
		
		XFire xfire = XFireFactory.newInstance().getXFire();		
		XFireProxyFactory factory = new XFireProxyFactory(xfire);
		Service service = xfire.getServiceRegistry().getService("Echo");
		EchoApi echo = (EchoApi)factory.create(service, endpoint);
		assertEquals("I got : 'the value'", echo.echo("the value"));
	}
	
	public void testCalculator()
	throws Exception
	{
		setupSite("site/soap_xfire.xml");
		
		String endpoint = "http://localhost:8181/calculator";
		assertTrue(HttpUtils.retrievePage(endpoint + "?wsdl").getContent().startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
																					  "<wsdl:definitions targetNamespace=\"http://xfire.soap.testwebservices.engine.rife.uwyn.com\" xmlns:tns=\"http://xfire.soap.testwebservices.engine.rife.uwyn.com\" xmlns:wsdlsoap=\"http://schemas.xmlsoap.org/wsdl/soap/\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenc11=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:soapenc12=\"http://www.w3.org/2003/05/soap-encoding\" xmlns:soap11=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\">"));
		
		XFire xfire = XFireFactory.newInstance().getXFire();		
		XFireProxyFactory factory = new XFireProxyFactory(xfire);
		Service service = xfire.getServiceRegistry().getService("Calculator");
		CalculatorApi calculator = (CalculatorApi)factory.create(service, endpoint);
		assertEquals(112, calculator.add(23, 89));
		assertEquals(47, calculator.substract(79, 32));
	}
	
	public void testEchoAuth()
	throws Exception
	{
		setupSite("site/soap_xfire.xml");

		String endpoint = "http://localhost:8181/echoauth";
 		
		XFire xfire = XFireFactory.newInstance().getXFire();		
		XFireProxyFactory factory = new XFireProxyFactory(xfire);
		Service service = xfire.getServiceRegistry().getService("EchoAuth");
		
		try
		{
			Logger.getLogger("org.codehaus.xfire").setLevel(Level.OFF);
			EchoApi echo_noauth = (EchoApi)factory.create(service, endpoint);
			echo_noauth.echo("Yooohoooo!");
		}
		catch (Throwable e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), e instanceof java.lang.NullPointerException);
		}

		EchoApi echo_auth = (EchoApi)factory.create(service, endpoint+"?login=gbevin&password=yeolpass&submission=credentials");
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
		
		String url_auth = "http://localhost:8181/echoauth?authid="+auth_id;
		echo_auth = (EchoApi)factory.create(service, url_auth);
		assertEquals("I got : 'Yooohoooo!'", echo_auth.echo("Yooohoooo!"));
	}
	
	public void testElementService()
	throws Exception
	{
		setupSite("site/soap_xfire.xml");
		
		String endpoint = "http://localhost:8181/elementservice";
		assertTrue(HttpUtils.retrievePage(endpoint + "?wsdl").getContent().startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
																					  "<wsdl:definitions targetNamespace=\"http://xfire.soap.testwebservices.engine.rife.uwyn.com\" xmlns:tns=\"http://xfire.soap.testwebservices.engine.rife.uwyn.com\" xmlns:wsdlsoap=\"http://schemas.xmlsoap.org/wsdl/soap/\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenc11=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:soapenc12=\"http://www.w3.org/2003/05/soap-encoding\" xmlns:soap11=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\">"));
		
		XFire xfire = XFireFactory.newInstance().getXFire();		
		XFireProxyFactory factory = new XFireProxyFactory(xfire);
		Service service = xfire.getServiceRegistry().getService("XFireElementService");
		XFireElementServiceApi element_service = (XFireElementServiceApi)factory.create(service, endpoint+"?input1=value1&input2=value2");
		
		assertEquals("value1", element_service.getElementInput("input1"));
		assertEquals("value2", element_service.getElementInput("input2"));
		assertNull(element_service.getElementInput("input3"));
		PrintStream out = System.out;
		Level orig_level = Logger.getLogger(ObjectInvoker.class.getName()).getLevel();
		try
		{
			System.setOut(new PrintStream(new ByteArrayOutputStream()));
			Logger.getLogger(ObjectInvoker.class.getName()).setLevel(Level.OFF);
			element_service.getElementInput("unknown");
			fail();
		}
		catch (XFireRuntimeException e)
		{
			assertTrue(e.getCause() instanceof XFireFault);
			assertEquals("The element 'rife/soap/xfire.xml' doesn't contain input 'unknown'.", e.getCause().getMessage());
		}
		finally
		{
			System.setOut(out);
			Logger.getLogger(ObjectInvoker.class.getName()).setLevel(orig_level);
		}
	}
}

