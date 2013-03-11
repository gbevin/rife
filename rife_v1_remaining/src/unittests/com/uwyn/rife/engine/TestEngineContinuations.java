/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestEngineContinuations.java 3936 2008-04-26 12:05:37Z gbevin $
 */
package com.uwyn.rife.engine;

import com.meterware.httpunit.*;

import com.uwyn.rife.TestCaseServerside;
import com.uwyn.rife.engine.testelements.continuations.AllTypes;
import com.uwyn.rife.tools.StringUtils;
import java.net.InetAddress;
import junit.framework.ComparisonFailure;

public class TestEngineContinuations extends TestCaseServerside
{
	public TestEngineContinuations(int siteType, String name)
	{
		super(siteType, name);
	}

	public void testNoPause()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/nopause");
		response = conversation.getResponse(request);

		assertEquals("", response.getText());
	}

	public void testSimple()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/simple");
		response = conversation.getResponse(request);

		String text = response.getText();
		String[] lines = StringUtils.splitToArray(text, "\n");
		assertEquals(2, lines.length);
		assertEquals("before simple pause", lines[0]);

		request = new GetMethodWebRequest("http://localhost:8181/simple?"+ReservedParameters.CONTID+"="+lines[1]);
		response = conversation.getResponse(request);

		assertEquals("after simple pause", response.getText());
	}

	public void testSimpleInterface()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/simple_interface");
		response = conversation.getResponse(request);

		String text = response.getText();
		String[] lines = StringUtils.splitToArray(text, "\n");
		assertEquals(2, lines.length);
		assertEquals("before simple pause", lines[0]);

		request = new GetMethodWebRequest("http://localhost:8181/simple_interface?"+ReservedParameters.CONTID+"="+lines[1]);
		response = conversation.getResponse(request);

		assertEquals("after simple pause", response.getText());
	}

	public void testNull()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/null");
		response = conversation.getResponse(request);

		String text = response.getText();
		String[] lines = StringUtils.splitToArray(text, "\n");
		assertEquals(2, lines.length);
		assertEquals("before null pause", lines[0]);

		request = new GetMethodWebRequest("http://localhost:8181/null?response=after%20null%20pause&"+ReservedParameters.CONTID+"="+lines[1]);
		response = conversation.getResponse(request);

		assertEquals("after null pause", response.getText());
	}

	public void testNullReference()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/null_reference");
		response = conversation.getResponse(request);

		request = new GetMethodWebRequest("http://localhost:8181/null_reference?"+ReservedParameters.CONTID+"="+response.getText());
		try
		{
			response = conversation.getResponse(request);
		}
		catch (Throwable e)
		{
			assertTrue(getLogSink().getInternalException() instanceof NullPointerException);
		}
	}

	public void testNullConditional()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/null_conditional?value=thevalue");
		response = conversation.getResponse(request);

		String text = response.getText();
		assertTrue(text.startsWith("thevalue"));

		request = new GetMethodWebRequest("http://localhost:8181/null_conditional?"+ReservedParameters.CONTID+"="+text.substring(8));
		response = conversation.getResponse(request);

		assertEquals("thevalue", response.getText());
	}

	public void testConditional()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse response = null;
		WebForm		form = null;

		request = new GetMethodWebRequest("http://localhost:8181/conditional");
		response = conversation.getResponse(request);

		assertEquals("printing", response.getTitle());
		form = response.getFormWithName("pause");
		assertNotNull(form);
		form.setParameter("answer", "1");
		response = form.submit();

		assertEquals("pauzing", response.getTitle());
		form = response.getFormWithName("pause");
		assertNotNull(form);
		form.setParameter("answer", "1"); // will not be checked
		response = form.submit();

		assertEquals("pauzingprinting", response.getTitle());
		form = response.getFormWithName("pause");
		assertNotNull(form);
		form.setParameter("answer", "0");
		response = form.submit();

		assertEquals("printing", response.getTitle());
		form = response.getFormWithName("pause");
		assertNotNull(form);

		request = new GetMethodWebRequest("http://localhost:8181/conditional");
		response = conversation.getResponse(request);

		assertEquals("printing", response.getTitle());
		form = response.getFormWithName("pause");
		assertNotNull(form);
		form.setParameter("stop", "1");
		response = form.submit();

		assertEquals("stopping", response.getTitle());
	}

	public void testMemberMethod()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/membermethod");
		response = conversation.getResponse(request);

		String text = response.getText();
		String[] lines = StringUtils.splitToArray(text, "\n");
		assertEquals(2, lines.length);
		assertEquals("before pause", lines[0]);

		request = new GetMethodWebRequest("http://localhost:8181/membermethod?"+ReservedParameters.CONTID+"="+lines[1]);
		response = conversation.getResponse(request);

		assertEquals("me value 6899", response.getText());
	}

	public void testPrivateMethod()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/private_method");
		response = conversation.getResponse(request);

		request = new GetMethodWebRequest("http://localhost:8181/private_method?"+ReservedParameters.CONTID+"="+response.getText());
		response = conversation.getResponse(request);

		assertEquals("1234", response.getText());
	}

	public void testSynchronization()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		String text = null;
		String[] lines = null;

		request = new GetMethodWebRequest("http://localhost:8181/synchronization");
		response = conversation.getResponse(request);

		text = response.getText();
		lines = StringUtils.splitToArray(text, "\n");
		assertEquals(2, lines.length);
		assertEquals("monitor this", lines[0]);

		request = new GetMethodWebRequest("http://localhost:8181/synchronization?"+ReservedParameters.CONTID+"="+lines[1]);
		response = conversation.getResponse(request);

		text = response.getText();
		lines = StringUtils.splitToArray(text, "\n");
		assertEquals(2, lines.length);
		assertEquals("monitor member", lines[0]);

		request = new GetMethodWebRequest("http://localhost:8181/synchronization?"+ReservedParameters.CONTID+"="+lines[1]);
		response = conversation.getResponse(request);

		text = response.getText();
		lines = StringUtils.splitToArray(text, "\n");
		assertEquals(2, lines.length);
		assertEquals("monitor static", lines[0]);

		request = new GetMethodWebRequest("http://localhost:8181/synchronization?"+ReservedParameters.CONTID+"="+lines[1]);
		response = conversation.getResponse(request);

		assertEquals("done", response.getText());
	}

	public void testThrow()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse response = null;
		WebForm		form = null;

		request = new GetMethodWebRequest("http://localhost:8181/throw");
		response = conversation.getResponse(request);

		form = response.getFormWithName("action");
		assertNotNull(form);
		form.setParameter("throw", "1");
		response = form.submit();

		assertEquals("do throw = true : throw message : finally message", response.getTitle());
		form = response.getFormWithName("action");
		assertNotNull(form);
		form.setParameter("throw", "1"); // will not be checked
		response = form.submit();

		assertEquals("do throw = true : throw message : finally message : all done", response.getTitle());
		form = response.getFormWithName("action");
		assertNotNull(form);

		request = new GetMethodWebRequest("http://localhost:8181/throw");
		response = conversation.getResponse(request);

		form = response.getFormWithName("action");
		assertNotNull(form);
		form.setParameter("throw", "0");
		response = form.submit();

		assertEquals("do throw = false : finally message", response.getTitle());
		form = response.getFormWithName("action");
		assertNotNull(form);
		form.setParameter("throw", "1"); // will not be checked
		response = form.submit();

		assertEquals("do throw = false : finally message : all done", response.getTitle());
		form = response.getFormWithName("action");
		assertNotNull(form);
	}

	public void testTryCatch()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse response = null;
		WebForm		form = null;

		request = new GetMethodWebRequest("http://localhost:8181/trycatch");
		response = conversation.getResponse(request);

		assertEquals("start", response.getTitle());
		form = response.getFormWithName("action");
		assertNotNull(form);
		form.setParameter("throw", "1");
		response = form.submit();

		assertEquals("start : throw done catch", response.getTitle());
		form = response.getFormWithName("action");
		assertNotNull(form);
		form.setParameter("throw", "1"); // will not be checked since the value of the first param is stored in a local variable in the element
		response = form.submit();

		assertEquals("start : throw done catch : finally done", response.getTitle());
		form = response.getFormWithName("action");
		assertNotNull(form);
		form.setParameter("throw", "0"); // will not be checked since the value of the first param is stored in a local variable in the element
		response = form.submit();

		assertEquals("start : throw done catch : finally done : all done", response.getTitle());
		form = response.getFormWithName("action");
		assertNotNull(form);
		form.setParameter("throw", "1"); // will not be checked since the value of the first param is stored in a local variable in the element
		response = form.submit();

		assertEquals("start", response.getTitle());
		form = response.getFormWithName("action");
		assertNotNull(form);
		form.setParameter("throw", "0");
		response = form.submit();

		assertEquals("start : throw not done", response.getTitle());
		form = response.getFormWithName("action");
		assertNotNull(form);
		form.setParameter("throw", "1"); // will not be checked since the value of the first param is stored in a local variable in the element
		response = form.submit();

		assertEquals("start : throw not done : finally done", response.getTitle());
		form = response.getFormWithName("action");
		assertNotNull(form);
		form.setParameter("throw", "0"); // will not be checked since the value of the first param is stored in a local variable in the element
		response = form.submit();

		assertEquals("start : throw not done : finally done : all done", response.getTitle());
		form = response.getFormWithName("action");
		assertNotNull(form);
		form.setParameter("throw", "1"); // will not be checked since the value of the first param is stored in a local variable in the element
		response = form.submit();
	}

	public void testFinally()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse response = null;
		String		text;
		String[]	lines;

		try
		{
			request = new GetMethodWebRequest("http://localhost:8181/finally");
			response = conversation.getResponse(request);
			text = response.getText();
			lines = StringUtils.splitToArray(text, "\n");
			assertEquals(2, lines.length);
			assertEquals("start", lines[0]);

			request = new GetMethodWebRequest("http://localhost:8181/finally?"+ReservedParameters.CONTID+"="+lines[1]);
			response = conversation.getResponse(request);
			text = response.getText();
			lines = StringUtils.splitToArray(text, "\n");
			assertEquals(2, lines.length);
			assertEquals("try", lines[0]);

			request = new GetMethodWebRequest("http://localhost:8181/finally?"+ReservedParameters.CONTID+"="+lines[1]);
			response = conversation.getResponse(request);
			text = response.getText();
			lines = StringUtils.splitToArray(text, "\n");
			assertEquals(2, lines.length);
			assertEquals("catch", lines[0]);

			request = new GetMethodWebRequest("http://localhost:8181/finally?"+ReservedParameters.CONTID+"="+lines[1]);
			response = conversation.getResponse(request);
			text = response.getText();
			lines = StringUtils.splitToArray(text, "\n");
			assertEquals(2, lines.length);
			assertEquals("finally", lines[0]);

			request = new GetMethodWebRequest("http://localhost:8181/finally?"+ReservedParameters.CONTID+"="+lines[1]);
			response = conversation.getResponse(request);
			assertEquals("after finally", response.getText());
		}
		catch (ComparisonFailure e)
		{
			throw e;
		}
		catch (Throwable e)
		{
			/* leave this in, since JDK 1.4 compiles finally differently (with JSR/RET opcodes)
			   in that case continuations doesn't work
			   see http://uwyn.com/issues/browse/RIFE-114 */
			getLogSink().getInternalException().printStackTrace();
		}
	}

	public void testInstanceOf()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/instanceof");
		response = conversation.getResponse(request);

		String text = response.getText();
		String[] lines = StringUtils.splitToArray(text, "\n");
		assertEquals(2, lines.length);
		assertEquals("before instanceof pause", lines[0]);

		request = new GetMethodWebRequest("http://localhost:8181/instanceof?"+ReservedParameters.CONTID+"="+lines[1]);
		response = conversation.getResponse(request);

		assertEquals("after instanceof pause", response.getText());
	}

	public void testInnerClass()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/innerclass");
		response = conversation.getResponse(request);

		String text = response.getText();
		String[] lines = StringUtils.splitToArray(text, "\n");
		assertEquals(2, lines.length);
		assertEquals("before pause", lines[0]);

		request = new GetMethodWebRequest("http://localhost:8181/innerclass?"+ReservedParameters.CONTID+"="+lines[1]);
		response = conversation.getResponse(request);

		assertEquals("InnerClass's output", response.getText());
	}

	public void testAllTypes()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		String text = null;
		String[] lines = null;

		request = new GetMethodWebRequest("http://localhost:8181/alltypes");
		response = conversation.getResponse(request);

		for (int i = 8; i < 40; i++)
		{
			text = response.getText();
			lines = StringUtils.splitToArray(text, "\n");
			assertEquals(2, lines.length);
			assertEquals(AllTypes.BEFORE+" while "+i, lines[0]);

			request = new GetMethodWebRequest("http://localhost:8181/alltypes?"+ReservedParameters.CONTID+"="+lines[1]);
			response = conversation.getResponse(request);
		}

		text = response.getText();
		lines = StringUtils.splitToArray(text, "\n");
		assertEquals(2, lines.length);
		assertEquals(AllTypes.BEFORE+" a", lines[0]);

		request = new GetMethodWebRequest("http://localhost:8181/alltypes?"+ReservedParameters.CONTID+"="+lines[1]);
		response = conversation.getResponse(request);

		text = response.getText();
		lines = StringUtils.splitToArray(text, "\n");
		assertEquals(2, lines.length);
		assertEquals(AllTypes.BEFORE+" b", lines[0]);

		request = new GetMethodWebRequest("http://localhost:8181/alltypes?"+ReservedParameters.CONTID+"="+lines[1]);
		response = conversation.getResponse(request);

		text = response.getText();
		lines = StringUtils.splitToArray(text, "\n");
		assertEquals(2, lines.length);
		assertEquals(AllTypes.BEFORE+" c", lines[0]);

		request = new GetMethodWebRequest("http://localhost:8181/alltypes?"+ReservedParameters.CONTID+"="+lines[1]);
		response = conversation.getResponse(request);

		assertEquals("40,1209000,11,16,7,8,\n"+
			"9223372036854775807,0,9223372036854775709,922337203685477570,8,-1,99,\n"+
			"0.4,8.4,-80.4,-80.0,0.0,-1.0,\n"+
			"2389.98,2407.3799996185303,-10.0,-1.0,-0.0,2397.3799996185303,\n"+
			"local ok,some value 6899,\n"+
			"true|false|false,K|O,54.7|9.8,82324.45|997823.23|87.8998,98|12,8|11,\n"+
			"111111|444444|666666|999999,111111|444444|666666|999999,333|8888|99,333|66|99,\n"+
			"zero|one|two|null,zero|one|two|null,ini|mini|moo,\n"+
			"3:str 0 0|replaced|str 0 2|str 0 3||str 1 0|str 1 1|str 1 2|str 1 3||str 2 0|str 2 1|str 2 2|str 2 3,\n"+
			"3:str 0 0|replaced|str 0 2|str 0 3||str 1 0|str 1 1|str 1 2|str 1 3||str 2 0|str 2 1|str 2 2|str 2 3,\n"+
			"2:str 0 0|str 0 1||str 1 0|str 1 1,\n"+
			"-98|97,-98|97,98|23|11,\n"+
			"2:0|1|2|3|4||100|101|102|-89|104,\n"+
			"2:0|1|2|3|4||100|101|102|-89|104,\n"+
			"3:0|1|2||100|101|102||200|201|202,\n"+
			"2,4,member ok,8111|8333,2:31|32|33|34||35|36|37|38,\n"+
			"1,3,static ok,9111|9333,3:1|2|3|4||5|6|7|8||9|10|11|12,\n"+
			"2,4,member ok,8111|8333,2:31|32|33|34||35|36|37|38,\n"+
			"1,3,static ok,9111|9333,3:1|2|3|4||5|6|7|8||9|10|11|12,\n"+
			"100,400,member ok two,8333|8111|23687,1:35|36|37|38,\n"+
			"60,600,static ok two,23476|9333|9111|8334,2:9|10|11|12||1|2|3|4,\n"+
			"2:3:3:0|1|2|3|4|5|6|7||10|11|12|13|14|15|16|17||20|21|22|23|24|25|26|27|||100|101|102|103|104|105|106|107||110|111|112|113|114|115|116|117||120|121|122|123|-99|null|126|127,\n"+
			"2:3:3:0|1|2|3|4|5|6|7||10|11|12|13|14|15|16|17||20|21|22|23|24|25|26|27|||100|101|102|103|104|105|106|107||110|111|112|113|114|115|116|117||120|121|122|123|-99|null|126|127,\n"+
			"4:1|3||5|7||11|-199||17|19,\n"+
			"4:1|3||5|7||11|-199||17|19,\n"+
			"me value 6899,\n"+
			"2147483647,25,4,109912,118,-2147483648", response.getText());
	}

	public void testStepBack()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebForm		form = null;

		request = new GetMethodWebRequest("http://localhost:8181/stepback");
		WebResponse response1 = conversation.getResponse(request);

		form = response1.getFormWithName("getanswer");
		assertEquals(" : true", response1.getTitle());
		assertNotNull(form);
		form.setCheckbox("start", true);
		WebResponse response2 = form.submit();
		assertEquals("0 : false", response2.getTitle());

		form = response2.getFormWithName("getanswer");
		assertNotNull(form);
		form.setParameter("answer", "12");
		WebResponse response3 = form.submit();
		assertEquals("12 : true", response3.getTitle());

		form = response3.getFormWithName("getanswer");
		assertNotNull(form);
		form.setParameter("answer", "32");
		WebResponse response4 = form.submit();
		assertEquals("44 : true", response4.getTitle());


		form = response4.getFormWithName("getanswer");
		assertNotNull(form);
		form.setParameter("answer", "41");
		WebResponse response5 = form.submit();
		assertEquals("got a total of 85 : false", response5.getTitle());
	}

	public void testSubmissionForm()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebForm		form = null;

		request = new GetMethodWebRequest("http://localhost:8181/submission/form");
		WebResponse response1 = conversation.getResponse(request);

		form = response1.getFormWithName("getanswer");
		assertEquals("0", response1.getTitle());
		assertNotNull(form);
		form.setParameter("answer", "12");
		WebResponse response2 = form.submit();
		assertEquals("12", response2.getTitle());

		form = response2.getFormWithName("nocontinuations");
		assertNotNull(form);
		form.setParameter("answer", "32");
		WebResponse response3a = form.submit();
		assertEquals("0", response3a.getTitle());

		form = response2.getFormWithName("getanswer");
		assertNotNull(form);
		form.setParameter("answer", "32");
		WebResponse response3b = form.submit();
		assertEquals("44", response3b.getTitle());

		form = response3b.getFormWithName("getanswer");
		assertNotNull(form);
		form.setParameter("answer", "41");
		WebResponse response4 = form.submit();
		assertEquals("", response4.getTitle());

		form = response4.getFormWithName("getanswer");
		assertNull(form);

		assertEquals("got a total of 85", response4.getText());
	}

	public void testExitForm()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebForm		form = null;

		request = new GetMethodWebRequest("http://localhost:8181/exit/form");
		WebResponse response1 = conversation.getResponse(request);
		assertEquals("0", response1.getTitle());

		form = response1.getFormWithName("getanswer");
		assertNotNull(form);
		form.setParameter("answer", "12");
		WebResponse response2 = form.submit();
		assertEquals("12", response2.getTitle());

		form = response2.getFormWithName("getanswer");
		assertNotNull(form);
		form.setParameter("answer", "32");
		WebResponse response3a = form.submit();
		assertEquals("44", response3a.getTitle());

		form = response2.getFormWithName("nocontinuations");
		assertNotNull(form);
		form.setParameter("answer", "32");
		WebResponse response3b = form.submit();
		assertEquals("0", response3b.getTitle());

		form = response3a.getFormWithName("getanswer");
		assertNotNull(form);
		form.setParameter("answer", "41");
		WebResponse response4 = form.submit();
		assertEquals("", response4.getTitle());

		form = response4.getFormWithName("getanswer");
		assertNull(form);

		assertEquals("got a total of 85", response4.getText());
	}

	public void testSubmissionQuery()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest		request = null;
		WebLink			link = null;
		String			link_text = null;

		request = new GetMethodWebRequest("http://localhost:8181/submission/query");
		WebResponse response1 = conversation.getResponse(request);

		link = response1.getLinkWith("getanswer");
		assertNotNull(link);
		link_text = link.getURLString()+"&answer=9";
		request = new GetMethodWebRequest("http://localhost:8181"+link_text);
		WebResponse response2 = conversation.getResponse(request);
		assertEquals("9", response2.getTitle());

		link = response2.getLinkWith("nocontinuations");
		assertNotNull(link);
		link_text = link.getURLString()+"&answer=14";
		request = new GetMethodWebRequest("http://localhost:8181"+link_text);
		WebResponse response3a = conversation.getResponse(request);
		assertEquals("0", response3a.getTitle());

		link = response2.getLinkWith("getanswer");
		assertNotNull(link);
		link_text = link.getURLString()+"&answer=14";
		request = new GetMethodWebRequest("http://localhost:8181"+link_text);
		WebResponse response3b = conversation.getResponse(request);
		assertEquals("23", response3b.getTitle());

		link = response3b.getLinkWith("getanswer");
		assertNotNull(link);
		link_text = link.getURLString()+"&answer=89";
		request = new GetMethodWebRequest("http://localhost:8181"+link_text);
		WebResponse response4 = conversation.getResponse(request);
		assertEquals("", response4.getTitle());

		link = response4.getLinkWith("getanswer");
		assertNull(link);

		assertEquals("got a total of 112", response4.getText());
	}

	public void testExitQuery()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest		request = null;
		WebLink			link = null;
		String			link_text = null;

		request = new GetMethodWebRequest("http://localhost:8181/exit/query");
		WebResponse response1 = conversation.getResponse(request);
		assertEquals("0", response1.getTitle());

		link = response1.getLinkWith("getanswer");
		assertNotNull(link);
		link_text = link.getURLString()+"&answer=9";
		request = new GetMethodWebRequest("http://localhost:8181"+link_text);
		WebResponse response2 = conversation.getResponse(request);
		assertEquals("9", response2.getTitle());

		link = response2.getLinkWith("getanswer");
		assertNotNull(link);
		link_text = link.getURLString()+"&answer=14";
		request = new GetMethodWebRequest("http://localhost:8181"+link_text);
		WebResponse response3a = conversation.getResponse(request);
		assertEquals("23", response3a.getTitle());

		link = response2.getLinkWith("nocontinuations");
		assertNotNull(link);
		link_text = link.getURLString()+"?answer=14";
		request = new GetMethodWebRequest("http://localhost:8181"+link_text);
		WebResponse response3b = conversation.getResponse(request);
		assertEquals("0", response3b.getTitle());

		link = response3a.getLinkWith("getanswer");
		assertNotNull(link);
		link_text = link.getURLString()+"&answer=89";
		request = new GetMethodWebRequest("http://localhost:8181"+link_text);
		WebResponse response4 = conversation.getResponse(request);
		assertEquals("", response4.getTitle());

		link = response4.getLinkWith("getanswer");
		assertNull(link);

		assertEquals("got a total of 112", response4.getText());
	}

	public void testInheritance()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebForm		form = null;
		WebLink		link = null;

		request = new GetMethodWebRequest("http://localhost:8181/inheritance");
		WebResponse response1 = null;
		response1 = conversation.getResponse(request);

		link = response1.getLinkWith("activate child");
		response1 = link.click();

		form = response1.getFormWithName("getanswer");
		assertEquals("0", response1.getTitle());
		assertNotNull(form);
		form.setParameter("answer", "12");
		WebResponse response2 = form.submit();

		link = response2.getLinkWith("activate child");
		response2 = link.click();

		assertEquals("12", response2.getTitle());

		form = response2.getFormWithName("getanswer");
		assertNotNull(form);
		form.setParameter("answer", "32");
		WebResponse response3 = form.submit();

		link = response3.getLinkWith("activate child");
		response3 = link.click();

		assertEquals("44", response3.getTitle());

		form = response3.getFormWithName("getanswer");
		assertNotNull(form);
		form.setParameter("answer", "41");
		WebResponse response4 = form.submit();

		link = response4.getLinkWith("activate child");
		response4 = link.click();

		form = response4.getFormWithName("getanswer");
		assertNull(form);

		assertEquals("got a total of 85", response4.getText());
	}

	public void testUniqueIDPerRequestNoClone()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;

		request = new GetMethodWebRequest("http://localhost:8181/noclone");
		WebResponse response1 = conversation.getResponse(request);

		WebForm form1 = response1.getFormWithName("getanswer");
		assertEquals("0", response1.getTitle());
		assertNotNull(form1);
		String cont1 = form1.getParameterValue("contid");
		assertNotNull(cont1);
		form1.setParameter("answer", "12");

		WebResponse response2 = form1.submit();
		assertEquals("12", response2.getTitle());
		WebForm form2 = response2.getFormWithName("getanswer");
		assertNotNull(form2);
		String cont2 = form2.getParameterValue("contid");
		assertNotNull(cont2);
		assertFalse(cont2.equals(cont1));
		form2.setParameter("answer", "32");

		WebResponse response3 = form2.submit();
		assertEquals("44", response3.getTitle());
		WebForm form3 = response3.getFormWithName("getanswer");
		assertNotNull(form3);
		String cont3 = form3.getParameterValue("contid");
		assertNotNull(cont3);
		assertFalse(cont3.equals(cont1));
		assertFalse(cont3.equals(cont2));
		form3.setParameter("answer", "41");

		// check if previous continuation contexts makes the logic
		// start from scratch again
		WebResponse response4 = form1.submit();
		assertEquals("0", response4.getTitle());
		WebForm form4 = response4.getFormWithName("getanswer");
		assertNotNull(form4);
		String cont4 = form4.getParameterValue("contid");
		assertNotNull(cont4);

		WebResponse response5 = form2.submit();
		assertEquals("0", response5.getTitle());
		WebForm form5 = response5.getFormWithName("getanswer");
		assertNotNull(form5);
		String cont5 = form5.getParameterValue("contid");
		assertNotNull(cont5);

		// each should have a unique id
		assertFalse(cont4.equals(cont1));
		assertFalse(cont4.equals(cont2));
		assertFalse(cont4.equals(cont3));
		assertFalse(cont4.equals(cont5));
		assertFalse(cont5.equals(cont1));
		assertFalse(cont5.equals(cont2));
		assertFalse(cont5.equals(cont3));
		assertFalse(cont5.equals(cont4));

		// perform the last step in the calculation
		WebResponse response6 = form3.submit();
		WebForm form6 = response6.getFormWithName("getanswer");
		assertNull(form6);

		assertEquals("got a total of 85", response6.getText());
	}

	public void testUniqueIDPerRequestClone()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;

		request = new GetMethodWebRequest("http://localhost:8181/clone");
		WebResponse response1 = conversation.getResponse(request);

		WebForm form1 = response1.getFormWithName("getanswer");
		assertEquals("0", response1.getTitle());
		assertNotNull(form1);
		String cont1 = form1.getParameterValue("contid");
		assertNotNull(cont1);
		form1.setParameter("answer", "12");

		WebResponse response2 = form1.submit();
		assertEquals("12", response2.getTitle());
		WebForm form2 = response2.getFormWithName("getanswer");
		assertNotNull(form2);
		String cont2 = form2.getParameterValue("contid");
		assertNotNull(cont2);
		assertFalse(cont2.equals(cont1));
		form2.setParameter("answer", "32");

		WebResponse response3 = form2.submit();
		assertEquals("44", response3.getTitle());
		WebForm form3 = response3.getFormWithName("getanswer");
		assertNotNull(form3);
		String cont3 = form3.getParameterValue("contid");
		assertNotNull(cont3);
		assertFalse(cont3.equals(cont1));
		assertFalse(cont3.equals(cont2));
		form3.setParameter("answer", "41");

		// check if previous continuation contexts have been preserved
		// and new ones cloned
		WebResponse response4 = form1.submit();
		assertEquals("12", response4.getTitle());
		WebForm form4 = response4.getFormWithName("getanswer");
		assertNotNull(form4);
		String cont4 = form4.getParameterValue("contid");
		assertNotNull(cont4);

		WebResponse response5 = form2.submit();
		assertEquals("44", response5.getTitle());
		WebForm form5 = response5.getFormWithName("getanswer");
		assertNotNull(form5);
		String cont5 = form5.getParameterValue("contid");
		assertNotNull(cont5);

		// each should have a unique id
		assertFalse(cont4.equals(cont1));
		assertFalse(cont4.equals(cont2));
		assertFalse(cont4.equals(cont3));
		assertFalse(cont4.equals(cont5));
		assertFalse(cont5.equals(cont1));
		assertFalse(cont5.equals(cont2));
		assertFalse(cont5.equals(cont3));
		assertFalse(cont5.equals(cont4));

		// perform the last step in the calculation
		WebResponse response6 = form3.submit();
		WebForm form6 = response6.getFormWithName("getanswer");
		assertNull(form6);

		assertEquals("got a total of 85", response6.getText());
	}

	public void testEmbedding()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse response = null;
		WebForm		form = null;

		String hostname = InetAddress.getByName("127.0.0.1").getHostName();
		String embedded = "Just some text 127.0.0.1:"+hostname+":.EMBEDDING_EMBEDDED:";

		request = new GetMethodWebRequest("http://localhost:8181/embedding");
		response = conversation.getResponse(request);

		assertEquals(response.getTitle(), embedded);
		form = response.getFormWithName("getanswer");
		assertNotNull(form);
		form.setParameter("answer", "12");
		response = form.submit();

		assertEquals(response.getTitle(), embedded);
		form = response.getFormWithName("getanswer");
		assertNotNull(form);
		form.setParameter("answer", "32");
		response = form.submit();

		assertEquals(response.getTitle(), embedded);
		form = response.getFormWithName("getanswer");
		assertNotNull(form);
		form.setParameter("answer", "41");
		response = form.submit();

		assertEquals(response.getTitle(), "");
		form = response.getFormWithName("getanswer");
		assertNull(form);

		assertEquals("got a total of 85", response.getText());
	}

	public void testNumberguess()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse response = null;
		WebForm		form = null;

		int low_bound = 0;
		int high_bound = 100;
		int last_guess = 50;

		int tries = 0;

		request = new GetMethodWebRequest("http://localhost:8181/numberguess");
		response = conversation.getResponse(request);

		do
		{
			form = response.getFormWithName("perform_guess");
			assertNotNull(form);
			form.setParameter("guess", String.valueOf(last_guess));
			response = form.submit();
			tries++;

			String text = response.getText();
			if (text.indexOf("lower") != -1)
			{
				high_bound = last_guess;
				last_guess = low_bound+(last_guess-low_bound)/2;
			}
			else if (text.indexOf("higher") != -1)
			{
				if (last_guess == low_bound &&
					low_bound == high_bound - 1)
				{
					last_guess = high_bound;
				}
				else
				{
					low_bound = last_guess;
					last_guess = last_guess+(high_bound-last_guess)/2;
				}
			}
		}
		while (response.getTitle().equals("Perform a guess"));

		String text = response.getText();
		assertTrue(text.indexOf("the answer was "+last_guess) != -1);
		assertTrue(text.indexOf("guessed it in "+tries+" tries") != -1);
	}

	public void testCallNoAnswer()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/no_answer");
		response = conversation.getResponse(request);

		String text = response.getText();
		String[] lines = StringUtils.splitToArray(text, "\n");
		assertEquals(4, lines.length);
		assertEquals("before simple call", lines[0]);
		assertEquals("in exit", lines[2]);
		assertEquals("", lines[3]);

		request = new GetMethodWebRequest("http://localhost:8181/no_answer?"+ReservedParameters.CONTID+"="+lines[1]);
		response = conversation.getResponse(request);

		assertEquals("after simple call", response.getText());
	}

	public void testSimpleCall()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/simple_call");
		response = conversation.getResponse(request);

		String text = response.getText();
		String[] lines = StringUtils.splitToArray(text, "\n");
		assertEquals(4, lines.length);
		assertEquals("before simple call", lines[0]);
		assertEquals("in exit", lines[2]);
		assertEquals("after simple call", lines[3]);

		request = new GetMethodWebRequest("http://localhost:8181/simple_call?"+ReservedParameters.CONTID+"="+lines[1]);
		response = conversation.getResponse(request);

		assertEquals("after simple call", response.getText());
	}

	public void testInheritanceCall()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink link = null;

		request = new GetMethodWebRequest("http://localhost:8181/inheritance_call?input1=value1");
		response = conversation.getResponse(request);

		link = response.getLinkWith("submission");
		response = link.click();

		link = response.getLinkWith("yes");
		assertEquals("received yes"+"This is the child value1", link.click().getText());

		link = response.getLinkWith("no");
		assertEquals("received no"+"This is the child value1", link.click().getText());
	}

	public void testChainedCall()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/chained_call1");
		response = conversation.getResponse(request);

		String text = response.getText();
		String[] lines = StringUtils.splitToArray(text, "\n");
		assertEquals(7, lines.length);
		assertEquals("before chained call 1", lines[0]);
		assertEquals("before chained call 2", lines[2]);
		assertEquals("before chained call 3", lines[4]);
		assertEquals("", lines[6]);

		request = new GetMethodWebRequest("http://localhost:8181/chained_call3?"+ReservedParameters.CONTID+"="+lines[5]);
		response = conversation.getResponse(request);

		assertEquals("after chained call 3\n"+
			"after chained call 2\n"+
			"after chained call 1\n", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/chained_call2?"+ReservedParameters.CONTID+"="+lines[3]);
		response = conversation.getResponse(request);

		assertEquals("after chained call 2\n"+
			"after chained call 1\n", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/chained_call1?"+ReservedParameters.CONTID+"="+lines[1]);
		response = conversation.getResponse(request);

		assertEquals("after chained call 1\n", response.getText());
	}

	public void testCallAnswer()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/call_answer");
		response = conversation.getResponse(request);

		String text = response.getText();
		String[] lines = StringUtils.splitToArray(text, "\n");
		assertEquals(6, lines.length);
		assertEquals("before call", lines[0]);
		assertEquals("the data:somevalue", lines[2]);
		assertEquals("before answer", lines[3]);
		assertEquals("the exit's answer", lines[4]);
		assertEquals("after call", lines[5]);

		request = new GetMethodWebRequest("http://localhost:8181/call_answer?"+ReservedParameters.CONTID+"="+lines[1]);
		response = conversation.getResponse(request);

		assertEquals("after call", response.getText());
	}

	public void testCallSubmission()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink link = null;

		request = new GetMethodWebRequest("http://localhost:8181/call_submission?input1=value1");
		response = conversation.getResponse(request);

		link = response.getLinkWith("submission");
		response = link.click();

		link = response.getLinkWith("yes");
		assertEquals("received yes"+
					 "value1", link.click().getText());

		link = response.getLinkWith("no");
		assertEquals("received no"+
					 "value1", link.click().getText());
	}

	public void testCallSubmissionArrival()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink link = null;

		request = new GetMethodWebRequest("http://localhost:8181/?input1=value2");
		response = conversation.getResponse(request);

		link = response.getLinkWith("submission");
		response = link.click();

		link = response.getLinkWith("yes");
		assertEquals("received yes"+
					 "value2", link.click().getText());

		link = response.getLinkWith("no");
		assertEquals("received no"+
					 "value2", link.click().getText());
	}

	public void testCallSubmissionNoPause()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink link = null;

		request = new GetMethodWebRequest("http://localhost:8181/call_submission/nopause?input1=value1");
		response = conversation.getResponse(request);

		link = response.getLinkWith("submission");
		response = link.click();

		link = response.getLinkWith("yes");
		assertEquals("received yes"+
					 "value1", link.click().getText());

		link = response.getLinkWith("no");
		assertEquals("received no"+
					 "value1", link.click().getText());
	}

	public void testAnswerWithoutCall()
	throws Exception
	{
		setupSite("site/continuations.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/answer_without_call");
		response = conversation.getResponse(request);
		assertEquals("the first value"+
			"2rda-2rdd-2rdc-2rdb", response.getText());
	}

	public void testCallAnswerGlobalvar()
	throws Exception
	{
		setupSite("site/continuations.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/call_answer_globalvar");
		response = conversation.getResponse(request);

		String text = response.getText();
		String[] lines = StringUtils.splitToArray(text, "\n");
		assertEquals(7, lines.length);
		assertEquals("before call", lines[0]);
		assertEquals("the data:beforecall", lines[2]);
		assertEquals("before answer", lines[3]);
		assertEquals("the exit's answer", lines[4]);
		assertEquals("the data:beforecall,beforeanswer", lines[5]);
		assertEquals("after call", lines[6]);

		request = new GetMethodWebRequest("http://localhost:8181/call_answer_globalvar?"+ReservedParameters.CONTID+"="+lines[1]);
		response = conversation.getResponse(request);

		// output values aren't part of the state of a continuation, they belong to the response of one particular request
		assertEquals("the data:null\nafter call", response.getText());
	}
}
