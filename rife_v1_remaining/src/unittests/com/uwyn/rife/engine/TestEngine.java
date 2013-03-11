/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestEngine.java 3933 2008-04-25 20:41:45Z gbevin $
 */
package com.uwyn.rife.engine;

import java.io.InputStream;
import java.net.InetAddress;

import com.meterware.httpunit.*;
import com.uwyn.rife.TestCaseServerside;
import com.uwyn.rife.template.TemplateFactory;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.HttpUtils;
import com.uwyn.rife.tools.IntegerUtils;
import com.uwyn.rife.tools.StringUtils;

public class TestEngine extends TestCaseServerside
{
	public TestEngine(int siteType, String name)
	{
		super(siteType, name);
	}

	public void testNoInitParam()
	throws Exception
	{
		setupSite(null);

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/participant/element");
		WebResponse response = null;

		response = conversation.getResponse(request);

        // Get the host name
		String hostname = InetAddress.getByName("127.0.0.1").getHostName();

		assertEquals("text/html", response.getContentType());
		assertEquals("Just some text 127.0.0.1:"+hostname+":.ELEMENT:", response.getText());
	}

	public void testSimplePlain()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/simple/plain");
		WebResponse response = null;

		response = conversation.getResponse(request);

        // Get the host name
		String hostname = InetAddress.getByName("127.0.0.1").getHostName();

		assertEquals("text/plain", response.getContentType());
		assertEquals("Just some text 127.0.0.1:"+hostname+":.simple_plain:", response.getText());
	}

	public void testSimpleHtml()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/simple/html");
		WebResponse response = conversation.getResponse(request);

        // Get the host name
		String hostname = InetAddress.getByName("127.0.0.1").getHostName();

		assertEquals("text/html", response.getContentType());
		assertEquals("Just some text 127.0.0.1:"+hostname+":.simple_html:", response.getText());
	}

	public void testSimpleInterface()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/simple/interface");
		WebResponse response = conversation.getResponse(request);

        // Get the host name
		String hostname = InetAddress.getByName("127.0.0.1").getHostName();

		assertEquals("text/html", response.getContentType());
		assertEquals("Just some text 127.0.0.1:"+hostname+":", response.getText());
	}

	public void testProperties()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/properties1");
		response = conversation.getResponse(request);
		assertEquals("Property 1 = property1a"+
					 "Property 2 = property2a"+
				     "Property 3 = null"+
					 "Property 4 = the value", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/properties2");
		response = conversation.getResponse(request);
		assertEquals("Property 1 = property1b"+
					 "Property 2 = null"+
				     "Property 3 = property3b"+
					 "Property 4 = out: <html>\n"+
					 "	<head>\n"+
					 "		<title>Template without blocks</title>\n"+
					 "	</head>\n"+
					 "\n"+
					 "	<body>\n"+
					 "		<h1>This is a template without blocks.</h1>\n"+
					 "	</body>\n"+
					 "</html>", response.getText());
	}

	public void testArrivalRoot()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181");
		response = conversation.getResponse(request);
		assertEquals("/", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/");
		response = conversation.getResponse(request);
		assertEquals("/", response.getText());
	}

	public void testArrivalNoUrl()
	throws Exception
	{
		setupSite("site/engine_arrivalnourl.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181");
		response = conversation.getResponse(request);
		assertEquals("/", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/");
		response = conversation.getResponse(request);
		assertEquals("/", response.getText());
	}

	public void testAutoid()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/autoid");
		response = conversation.getResponse(request);
		assertEquals(".autoid", response.getText());
	}

	public void testExplicitid()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/explicitid");
		response = conversation.getResponse(request);
		assertEquals(".THIS_ID_IS_EXPLICIT", response.getText());
	}

	public void testPathInfoEnabled()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

        // Get the host name
		String hostname = InetAddress.getByName("127.0.0.1").getHostName();

		request = new GetMethodWebRequest("http://localhost:8181/simple/pathinfo/some/path");
		response = conversation.getResponse(request);
		assertEquals("Just some text 127.0.0.1:"+hostname+":.PATHINFO:/some/path", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/simple/pathinfo/");
		response = conversation.getResponse(request);
		assertEquals("Just some text 127.0.0.1:"+hostname+":.PATHINFO:/", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/simple/pathinfo/another_path_info");
		response = conversation.getResponse(request);
		assertEquals("Just some text 127.0.0.1:"+hostname+":.PATHINFO:/another_path_info", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/simple/pathinfoddd");
		response = conversation.getResponse(request);
		assertEquals("This is the fallback content.", response.getText());
	}

	public void testArrivalPathInfoEnabled()
	throws Exception
	{
		setupSite("site/engine_arrivalpathinfo.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

        // Get the host name
		String hostname = InetAddress.getByName("127.0.0.1").getHostName();

		request = new GetMethodWebRequest("http://localhost:8181/some/path");
		response = conversation.getResponse(request);
		assertEquals("Just some text 127.0.0.1:"+hostname+":.:/some/path", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/");
		response = conversation.getResponse(request);
		assertEquals("Just some text 127.0.0.1:"+hostname+":.:/", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/another_path_info");
		response = conversation.getResponse(request);
		assertEquals("Just some text 127.0.0.1:"+hostname+":.:/another_path_info", response.getText());
	}

	public void testPathInfoCoexist()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

        // Get the host name
		String hostname = InetAddress.getByName("127.0.0.1").getHostName();

		request = new GetMethodWebRequest("http://localhost:8181/simple_coexist");
		response = conversation.getResponse(request);
		assertEquals("Just some text 127.0.0.1:"+hostname+":.COEXIST_REGULAR:", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/simple_coexist/");
		response = conversation.getResponse(request);
		assertEquals("Just some text 127.0.0.1:"+hostname+":.COEXIST_REGULAR:", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/simple_coexist/pathinfo");
		response = conversation.getResponse(request);
		assertEquals("Just some text 127.0.0.1:"+hostname+":.COEXIST_PATHINFO:/pathinfo", response.getText());
	}

	public void testPathInfoDisabledFallback()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		// this should go to the element since a trailing slash resolves to the
		// nearest match (slash itself or the element registered without the
		// trailing slash
		String hostname = InetAddress.getByName("127.0.0.1").getHostName();

		request = new GetMethodWebRequest("http://localhost:8181/simple/html/");
		response = conversation.getResponse(request);
		assertEquals("Just some text 127.0.0.1:"+hostname+":.simple_html:", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/simple/html/some/path");
		response = conversation.getResponse(request);
		assertEquals("This is the fallback content.", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/simple/html/another_path_info");
		response = conversation.getResponse(request);
		assertEquals("This is the fallback content.", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/simple/htmlddd");
		response = conversation.getResponse(request);
		assertEquals("This is the fallback content.", response.getText());
	}

	public void testPathInfoDisabledNoFallback()
	throws Exception
	{
		setupSite("site/engine_nofallback.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

        // Get the host name
		String hostname = InetAddress.getByName("127.0.0.1").getHostName();

		request = new GetMethodWebRequest("http://localhost:8181/simple/html");
		response = conversation.getResponse(request);
		assertEquals("Just some text 127.0.0.1:"+hostname+":.SIMPLEHTML:", response.getText());

		// this should go to the element since a trailing slash resolves to the
		// nearest match (slash itself or the element registered without the
		// trailing slash
		request = new GetMethodWebRequest("http://localhost:8181/simple/html/");
		try
		{
			response = conversation.getResponse(request);
			assertTrue(true);
		}
		catch (HttpNotFoundException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		request = new GetMethodWebRequest("http://localhost:8181/simple/html/some/path");
		try
		{
			response = conversation.getResponse(request);
			fail();
		}
		catch (HttpNotFoundException e)
		{
			assertTrue(true);
		}

		request = new GetMethodWebRequest("http://localhost:8181/simple/html/another_path_info");
		try
		{
			response = conversation.getResponse(request);
			fail();
		}
		catch (HttpNotFoundException e)
		{
			assertTrue(true);
		}

		request = new GetMethodWebRequest("http://localhost:8181/simple/htmlddd");
		try
		{
			response = conversation.getResponse(request);
			fail();
		}
		catch (HttpNotFoundException e)
		{
			assertTrue(true);
		}
	}

	public void testHeaders()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/headers");
		WebResponse response = conversation.getResponse(request);

		assertTrue(response.getHeaderFieldNames().length > 4);
		assertEquals("attachment; filename=thefile.zip", response.getHeaderField("CONTENT-DISPOSITION"));
		assertEquals("Fri, 25 Oct 2002 19:20:58 GMT", response.getHeaderField("DATEHEADER"));
		assertEquals("1212", response.getHeaderField("INTHEADER"));
	}

	public void testContentlength()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/contentlength");
		try
		{
			WebResponse response = conversation.getResponse(request);

			assertEquals(13, response.getContentLength());
			assertEquals("this goes out", response.getText());
		}
		catch (RuntimeException e)
		{
			assertEquals("setContentLength not implemented", e.getMessage());
		}
	}

	public void testDynamicContenttype()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/dynamiccontenttype");
		WebResponse response = null;

		request.setParameter("switch", "text");
		response = conversation.getResponse(request);
		assertEquals("text/plain", response.getContentType());

		request.setParameter("switch", "html");
		response = conversation.getResponse(request);
		assertEquals("text/html", response.getContentType());
	}

	public void testStaticProperties()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/staticproperties");
		WebResponse response = conversation.getResponse(request);

		assertEquals("property1:value1"+
			"property2:value2"+
			"property3:value3b"+
			"property4:emptyproperty4"+
			"propertystring1:astring"+
			"propertystring2:null"+
			"propertystring2default:stringdefault"+
			"propertyboolean1:true"+
			"propertyboolean2:false"+
			"propertyboolean2default:false"+
			"propertyint1:38746873"+
			"propertyint2:0"+
			"propertyint2default:123"+
			"propertylong1:3468364786"+
			"propertylong2:0"+
			"propertylong2default:983749876"+
			"propertydouble1:9873434.4334"+
			"propertydouble2:0.0"+
			"propertydouble2default:34778.34"+
			"propertyfloat1:23.12"+
			"propertyfloat2:0.0"+
			"propertyfloat2default:324.34"+
			"propertyconfig:before-the element config value-after", response.getText());
	}

	public void testFallback()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/fallback");
		response = conversation.getResponse(request);
		assertEquals("This is the fallback content.", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/zefzegtze");
		response = conversation.getResponse(request);
		assertEquals("This is the fallback content.", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/dfdfgergfer");
		response = conversation.getResponse(request);
		assertEquals("This is the fallback content.", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/zegzegzegz");
		response = conversation.getResponse(request);
		assertEquals("This is the fallback content.", response.getText());
	}

	public void testBinary()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/binary");
		response = conversation.getResponse(request);
		InputStream inputstream = response.getInputStream();
		byte[] integer_bytes = new byte[4];
		assertEquals(4, inputstream.read(integer_bytes));
		assertEquals(87634675, IntegerUtils.bytesToInt(integer_bytes));
	}

	public void testPrintAndWriteBuffer()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/printandwrite_buffer");
		response = conversation.getResponse(request);
		assertEquals("write2write4print1print3", response.getText());
	}

	public void testPrintAndWriteNobuffer()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/printandwrite_nobuffer");
		response = conversation.getResponse(request);
		assertEquals("print1write2print3write4", response.getText());
	}

	public void testInnerClass()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/innerclass");
		response = conversation.getResponse(request);
		assertEquals("InnerClass's output", response.getText());
	}

	public void testPackagePrivateClass()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/packageprivateclass");
		response = conversation.getResponse(request);
		assertEquals("PackagePrivateClassOutput's output", response.getText());
	}

	public void testPackagePrivateMethod()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/packageprivatemethod");
		response = conversation.getResponse(request);
		assertEquals("PackagePrivateMethodOutput's output", response.getText());
	}

	public void testInitConfig()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/initconfig");
		WebResponse response = conversation.getResponse(request);

		assertEquals("jetty-6.1.9", response.getText());
	}

	public void testInitialize()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = null;
		WebRequest request = null;
		WebResponse response = null;

		conversation = new WebConversation();
		request = new GetMethodWebRequest("http://localhost:8181/initialize");
		response = conversation.getResponse(request);

		assertEquals("/initialize", response.getText());
	}

	public void testInitializeInterface()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = null;
		WebRequest request = null;
		WebResponse response = null;

		conversation = new WebConversation();
		request = new GetMethodWebRequest("http://localhost:8181/initialize_interface");
		response = conversation.getResponse(request);

		assertEquals("/initialize_interface", response.getText());
	}

	public void testDeployment()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = null;
		WebRequest request = null;
		WebResponse response = null;

		conversation = new WebConversation();
		request = new GetMethodWebRequest("http://localhost:8181/deployment");
		response = conversation.getResponse(request);

		String		response_text = response.getText();
		String[]	response_parts = StringUtils.splitToArray(response_text, ":");

		assertEquals("initvalue", response_parts[0]);

		int		count = Integer.parseInt(response_parts[1]);

		assertEquals(response_text, response.getText());
		assertEquals(response_text, response.getText());
		assertEquals(response_text, response.getText());

		setupSite("site/engine.xml");

		conversation = new WebConversation();
		request = new GetMethodWebRequest("http://localhost:8181/deployment");
		response = conversation.getResponse(request);

		assertEquals(response_parts[0]+":"+(count+1), response.getText());
		assertEquals(response_parts[0]+":"+(count+1), response.getText());
		assertEquals(response_parts[0]+":"+(count+1), response.getText());
	}

	public void testDeploymentInterface()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = null;
		WebRequest request = null;
		WebResponse response = null;

		conversation = new WebConversation();
		request = new GetMethodWebRequest("http://localhost:8181/deployment_interface");
		response = conversation.getResponse(request);

		String		response_text = response.getText();
		String[]	response_parts = StringUtils.splitToArray(response_text, ":");

		assertEquals("initvalue", response_parts[0]);

		int		count = Integer.parseInt(response_parts[1]);

		assertEquals(response_text, response.getText());
		assertEquals(response_text, response.getText());
		assertEquals(response_text, response.getText());

		setupSite("site/engine.xml");

		conversation = new WebConversation();
		request = new GetMethodWebRequest("http://localhost:8181/deployment_interface");
		response = conversation.getResponse(request);

		assertEquals(response_parts[0]+":"+(count+1), response.getText());
		assertEquals(response_parts[0]+":"+(count+1), response.getText());
		assertEquals(response_parts[0]+":"+(count+1), response.getText());
	}

	public void testGenerateForm()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		new HttpUtils.Request("http://localhost:8181/form").headers(new String[][] {{"accept-encoding", "gzip"}}).retrieve();

		request = new GetMethodWebRequest("http://localhost:8181/form");
		response = conversation.getResponse(request);
		assertEquals(TemplateFactory.ENGINEHTML.get("formbuilder_fields_out_constrained_values").getContent(), response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/form?remove=1");
		response = conversation.getResponse(request);
		assertEquals(TemplateFactory.ENGINEHTML.get("formbuilder_fields").getContent(), response.getText());
	}

	public void testGenerateEmptyForm()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/form_empty");
		response = conversation.getResponse(request);
		assertEquals(TemplateFactory.ENGINEHTML.get("formbuilder_fields_out_constrained_empty").getContent(), response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/form_empty?remove=1");
		response = conversation.getResponse(request);
		assertEquals(TemplateFactory.ENGINEHTML.get("formbuilder_fields").getContent(), response.getText());
	}

	public void testGenerateFormPrefix()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/form?prefix=1");
		response = conversation.getResponse(request);
		assertEquals(TemplateFactory.ENGINEHTML.get("formbuilder_form_prefix_out_constrained_values").getContent(), response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/form?prefix=1&remove=1");
		response = conversation.getResponse(request);
		assertEquals(TemplateFactory.ENGINEHTML.get("formbuilder_form_prefix").getContent(), response.getText());
	}

	public void testGenerateEmptyFormPrefix()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/form_empty?prefix=1");
		response = conversation.getResponse(request);
		assertEquals(TemplateFactory.ENGINEHTML.get("formbuilder_form_prefix_out_constrained_empty").getContent(), response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/form_empty?prefix=1&remove=1");
		response = conversation.getResponse(request);
		assertEquals(TemplateFactory.ENGINEHTML.get("formbuilder_form_prefix").getContent(), response.getText());
	}

	public void testGzipEncoding()
	throws Exception
	{
		setupSite("site/engine.xml");

		HttpUtils.Page compressed_page = new HttpUtils.Request("http://localhost:8181/simple/html").headers(new String[][] {{"Accept-Encoding", "gzip"}}).retrieve();
		String compressed_content = compressed_page.getContent();
		int compressed_length = Integer.parseInt(compressed_page.getHeaderField("Content-Length"));

		assertTrue(compressed_length != compressed_content.length());

		HttpUtils.Page uncompressed_page = HttpUtils.retrievePage("http://localhost:8181/simple/html");
		String uncompressed_content = uncompressed_page.getContent();

		assertNull(uncompressed_page.getHeaderField("Content-Length"));
		assertEquals(compressed_content, uncompressed_content);
	}

	public void testForward()
	throws Exception
	{
		setupSite("site/engine_nofallback.xml");

		// Get the host name
		String hostname = InetAddress.getByName("127.0.0.1").getHostName();

		HttpUtils.Page page1 = HttpUtils.retrievePage("http://localhost:8181/forward?go=0");
		assertEquals("not forwarded", page1.getContent());

		HttpUtils.Page page2 = HttpUtils.retrievePage("http://localhost:8181/forward?go=1");
		assertEquals("Just some text 127.0.0.1:"+hostname+":.SIMPLEHTML:", page2.getContent());

		HttpUtils.Page page3 = HttpUtils.retrievePage("http://localhost:8181/forward?go=2");
		assertEquals(404, page3.getResponseCode());
		assertTrue(page3.getResponseMessage().indexOf("Not Found") != -1);
		assertNull(page3.getContent());
	}

	public void testTemplateContentType()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/template_content_type");
		WebResponse response = null;

		response = conversation.getResponse(request);

		assertEquals("application/xml", response.getContentType());
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
			"<!DOCTYPE config SYSTEM \"/dtd/config.dtd\">\n"+
			"\n"+
			"<config>\n"+
			"<!--V 'params'/--></config>\n", response.getText());
	}
	
	public void testGeneratedProperties()
	throws Exception
	{
		setupSite("site/engine.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/generated_properties");
		WebResponse response = null;
		
		response = conversation.getResponse(request);
		
		assertEquals(System.getProperty("java.vm.specification.vendor")+" "+System.getProperty("java.vm.specification.version")+"\n"+
					 "<!--V 'PROPERTY:unknownProperty'/-->\n"+
					 "custom property value\n"+
					 "default\n", response.getText());
	}

	public void testErrorHandlerTargetWithoutExceptions()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/errors1");
		WebResponse response = null;

		response = conversation.getResponse(request);

		assertEquals(".Errors1\n" +
					 "null\n" +
					 "null", response.getText());
	}

	public void testErrorHandlerEngineException()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/erroneous?exceptionType=EngineException");
		WebResponse response = null;

		response = conversation.getResponse(request);

		assertEquals(".Errors1\n" +
					 ".Erroneous\n" +
					 "This is an engine exception.\n" +
					 "com.uwyn.rife.engine.exceptions.EngineException", response.getText());
	}

	public void testErrorHandlerRuntimeException()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/erroneous?exceptionType=RuntimeException");
		WebResponse response = null;

		response = conversation.getResponse(request);

		assertEquals(".Errors2\n" +
					 ".Erroneous\n" +
					 "This is a runtime exception.\n" +
					 "java.lang.RuntimeException", response.getText());
	}

	public void testErrorHandlerNestedEngineException()
	throws Exception
	{
		setupSite("site/engine.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/erroneous?exceptionType=nested%20EngineException");
		WebResponse response = null;

		response = conversation.getResponse(request);

		assertEquals(".Errors1\n" +
					 ".Erroneous\n" +
					 "This is a runtime exception with a nested engine exception.\n" +
					 "java.lang.RuntimeException", response.getText());
	}
}
