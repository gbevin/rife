/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestEngineSubmissions.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.meterware.httpunit.Base64;
import com.meterware.httpunit.*;
import com.uwyn.rife.TestCaseServerside;
import com.uwyn.rife.config.Config;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.engine.exceptions.MultipartFileTooBigException;
import com.uwyn.rife.engine.exceptions.ParameterUnknownException;
import com.uwyn.rife.engine.exceptions.SubmissionUnknownException;
import com.uwyn.rife.engine.testelements.submission.BeanImpl.SerializableParam;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.template.TemplateFactory;
import com.uwyn.rife.tools.*;
import com.uwyn.rife.tools.exceptions.FileUtilsErrorException;
import com.uwyn.rife.tools.exceptions.InnerClassException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.HashMap;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class TestEngineSubmissions extends TestCaseServerside
{
	public TestEngineSubmissions(int siteType, String name)
	{
		super(siteType, name);
	}

	public void testContextIndependence()
	throws Exception
	{
		WebConversation	conversation = null;
		WebRequest		request = null;
		WebResponse		response = null;

		setupSite("site/submissions.xml");
		conversation = new WebConversation();
		request = new PostMethodWebRequest("http://localhost:8181/submissions/valid");
		request.setParameter(ReservedParameters.SUBMISSION, "login");
		request.setParameter("login", "gbevin");
		request.setParameter("password", "mypassword");
		request.setParameter("array", new String[] {"1", "3", "4"});
		response = conversation.getResponse(request);

		assertEquals("gbevin,mypassword,1|3|4", response.getText());

		setupSite("/PREFIX", "site/submissions.xml");
		conversation = new WebConversation();
		request = new PostMethodWebRequest("http://localhost:8181/PREFIX/submissions/valid");
		request.setParameter(ReservedParameters.SUBMISSION, "login");
		request.setParameter("login", "gbevin");
		request.setParameter("password", "mypassword");
		request.setParameter("array", new String[] {"1", "3", "4"});
		response = conversation.getResponse(request);

		assertEquals("gbevin,mypassword,1|3|4", response.getText());
	}

	public void testParameters()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new PostMethodWebRequest("http://localhost:8181/submissions/valid");
		request.setParameter(ReservedParameters.SUBMISSION, "login");
		request.setParameter("login", "gbevin");
		request.setParameter("password", "mypassword");
		request.setParameter("array", new String[] {"1", "3", "4"});
		WebResponse response = conversation.getResponse(request);

		assertEquals("gbevin,mypassword,1|3|4", response.getText());
	}

	public void testParametersRegexp()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new PostMethodWebRequest("http://localhost:8181/params/regexp");
		WebResponse response = null;
		request.setParameter(ReservedParameters.SUBMISSION, "login");
		request.setParameter("login", "gbevin");
		request.setParameter("password", "mypassword");
		request.setParameter("array", new String[] {"1", "3", "4"});
		request.setParameter("loffin", "gbevin2");
		request.setParameter("paword", "mypasswordtoo");
		request.setParameter("aray", new String[] {"9", "4", "2"});

		response = conversation.getResponse(request);

		assertEquals("9|4|2,1|3|4,gbevin2,gbevin,mypassword,mypasswordtoo,mypassword,mypasswordtoo,", response.getText());
	}

	public void testParametersTyped()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new PostMethodWebRequest("http://localhost:8181/submissions/typed");
		WebResponse response = null;
		request.setParameter(ReservedParameters.SUBMISSION, "typed");
		request.setParameter("paramstring1", "astring");
		request.setParameter("paramint1", ""+Integer.MAX_VALUE);
		request.setParameter("paramlong1", ""+Long.MAX_VALUE);
		request.setParameter("paramdouble1", "9873434.4334");
		request.setParameter("paramfloat1", "23.12");
		response = conversation.getResponse(request);
		assertEquals("paramstring1:astring"+
			"paramstring2:null"+
			"paramstring2default:stringdefault"+
			"paramint1:"+Integer.MAX_VALUE+
			"paramint2:0"+
			"paramint2default:123"+
			"paramlong1:"+Long.MAX_VALUE+
			"paramlong2:0"+
			"paramlong2default:983749876"+
			"paramdouble1:9873434.4334"+
			"paramdouble2:0.0"+
			"paramdouble2default:34778.34"+
			"paramfloat1:23.12"+
			"paramfloat2:0.0"+
			"paramfloat2default:324.34", response.getText());
	}

	public void testParametersTypedInjection()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new PostMethodWebRequest("http://localhost:8181/submissions/typed_injection");
		WebResponse response = null;
		request.setParameter(ReservedParameters.SUBMISSION, "typed");
		request.setParameter("paramstring1", "astring");
		request.setParameter("paramint1", ""+Integer.MAX_VALUE);
		request.setParameter("paramlong1", ""+Long.MAX_VALUE);
		request.setParameter("paramdouble1", "9873434.4334");
		request.setParameter("paramfloat1", "23.12");
		request.setParameter("parammultiple", new String [] {"one", "two", "three"});
		response = conversation.getResponse(request);
		assertEquals("paramstring1:astring"+
					 "paramstring2:null"+
					 "paramint1:"+Integer.MAX_VALUE+
					 "paramint2:378"+
					 "paramlong1:"+Long.MAX_VALUE+
					 "paramlong2:0"+
					 "paramdouble1:9873434.4334"+
					 "paramdouble2:0.0"+
					 "paramfloat1:23.12"+
					 "paramfloat2:0.0"+
					 "parammultiple:one-two-three", response.getText());
	}

	public void testParametersTypedMultiple()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new PostMethodWebRequest("http://localhost:8181/submissions/typed/multiple");
		WebResponse response = null;
		request.setParameter(ReservedParameters.SUBMISSION, "typed_multiple");
		request.setParameter("paramstring", new String [] {"one", "two", "three"});
		request.setParameter("paramint", new String [] {String.valueOf(89), "ok", String.valueOf(12), String.valueOf(2), "cracotte", String.valueOf(56)});
		request.setParameter("paramlong", new String [] {String.valueOf(4342), String.valueOf(54543), "okko", String.valueOf(3434), "kili", String.valueOf(44342)});
		request.setParameter("paramdouble", new String [] {"rokko", String.valueOf(9873434.4334d), String.valueOf(23984.945d), String.valueOf(348900.6534d), String.valueOf(153932.343d), "kirri"});
		request.setParameter("paramfloat", new String [] {String.valueOf(432.34f), "ohno", String.valueOf(9234.65f), String.valueOf(2345.98f), "nokki", String.valueOf(633.9f)});
		response = conversation.getResponse(request);
		assertEquals("paramstring:one,two,three"+
			"paramint:89,12,2,56"+
			"paramlong:4342,54543,3434,44342"+
			"paramdouble:9873434.4334,23984.945,348900.6534,153932.343"+
			"paramfloat:432.34,9234.65,2345.98,633.9", response.getText());
	}

	public void testParametersMissingSubmission()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/submissions/valid");
		WebResponse response = null;
		request.setParameter("login", "gbevin");
		request.setParameter("password", "mypassword");
		request.setParameter("array", new String[] {"1", "3", "4"});
		response = conversation.getResponse(request);
		assertEquals("null,null,null", response.getText());
	}

	public void testParametersUnknownSubmission()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new PostMethodWebRequest("http://localhost:8181/submissions/unknown");

		try
		{
			conversation.getResponse(request);
			fail();
		}
		catch (HttpInternalErrorException e)
		{
			assertTrue(getLogSink().getInternalException() instanceof SubmissionUnknownException);

			SubmissionUnknownException	e2 = (SubmissionUnknownException)getLogSink().getInternalException();
			assertEquals("unknown", e2.getSubmissionName());
			assertEquals(e2.getDeclarationName(), "element/submission/unknown.xml");
		}
	}

	public void testParameterDoesntExist()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new PostMethodWebRequest("http://localhost:8181/submissions/doesntexist");
		request.setParameter(ReservedParameters.SUBMISSION, "login");
		request.setParameter("login", "gbevin");
		request.setParameter("password", "mypassword");
		request.setParameter("array", new String[] {"1", "3", "4"});

		try
		{
			conversation.getResponse(request);
			fail();
		}
		catch (HttpInternalErrorException e)
		{
			assertTrue(getLogSink().getInternalException() instanceof ParameterUnknownException);

			ParameterUnknownException	e2 = (ParameterUnknownException)getLogSink().getInternalException();
			assertEquals("login", e2.getParameterName());
			assertEquals(e2.getDeclarationName(), "element/submission/doesntexist.xml");
		}
	}

	public void testParametersUnknown()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/submissions/params_unknown");
		request.setParameter("login", "gbevin");
		request.setParameter("password", "mypassword");
		try
		{
			conversation.getResponse(request);
			fail();
		}
		catch (HttpInternalErrorException e)
		{
			assertTrue(getLogSink().getInternalException() instanceof ParameterUnknownException);

			ParameterUnknownException	e2 = (ParameterUnknownException)getLogSink().getInternalException();
			assertEquals("login_unknown", e2.getParameterName());
			assertEquals(e2.getDeclarationName(), "element/submission/params_unknown.xml");
		}
	}

	public void testSubmissionMultiple()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;

		request = new GetMethodWebRequest("http://localhost:8181/submissions/multiple");
		response = conversation.getResponse(request);
		form = response.getFormWithName("login");
		form.setParameter("login", "gbevin");
		form.setParameter("password", "mypassword");
		form.setParameter("language", form.getOptionValues("language"));
		response = form.submit();
		assertEquals("gbevin,mypassword,fr|nl", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/submissions/multiple");
		response = conversation.getResponse(request);
		form = response.getFormWithName("register");
		form.setParameter("login", "gbevin");
		form.setParameter("password", "mypassword");
		form.setParameter("firstname", "Geert");
		form.setParameter("lastname", "Bevin");
		response = form.submit();
		assertEquals("gbevin,mypassword,Geert,Bevin", response.getText());
	}

	public void testSubmissionSendGet()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/submissions/sendget");
		WebResponse response = conversation.getResponse(request);
		WebLink	submission_link = response.getLinkWith("submissionlink");
		submission_link.click();
		response = conversation.getCurrentPage();

		assertEquals("gbevin,stillmypassword", response.getText());
	}

	public void testSubmissionSendPost()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/submissions/sendpost");
		WebResponse response = conversation.getResponse(request);
		WebForm submission_form = response.getForms()[0];
		submission_form.setParameter("login", "gbevin2");
		submission_form.setParameter("password", "oncemoremypassword");
		response = submission_form.submit();

		assertEquals("gbevin2,oncemoremypassword", response.getText());
	}

	public void testSubmissionSendPostHidden()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/submissions/sendposthidden");
		WebResponse response = conversation.getResponse(request);
		WebForm submission_form = response.getForms()[0];
		submission_form.setParameter("login", "gbevin2");
		submission_form.setParameter("password", "oncemoremypassword");
		response = submission_form.submit();

		assertEquals("gbevin2,oncemoremypassword", response.getText());
	}

	public void testSubmissionSendGetHidden()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/submissions/sendgethidden");
		WebResponse response = conversation.getResponse(request);
		WebForm submission_form = response.getForms()[0];
		submission_form.setParameter("login", "gbevin2");
		submission_form.setParameter("password", "oncemoremypassword");
		response = submission_form.submit();

		assertEquals("gbevin2,oncemoremypassword", response.getText());
	}

	public void testSubmissionGeneratedUrl()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	submission_link = null;
		WebForm submission_form = null;

		request = new GetMethodWebRequest("http://localhost:8181/submissions/generatedurl");
		response = conversation.getResponse(request);
		submission_link = response.getLinkWith("submission1");
		submission_link.click();
		response = conversation.getCurrentPage();

		assertEquals("thevalue", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/submissions/generatedurl");
		response = conversation.getResponse(request);
		submission_form = response.getForms()[0];
		submission_form.setParameter("login", "gbevin2");
		response = submission_form.submit();
		submission_form = response.getForms()[0];
		submission_form.setParameter("password", "oncemoremypassword");
		response = submission_form.submit();

		assertEquals("gbevin2,oncemoremypassword", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/submissions/generatedurl");
		response = conversation.getResponse(request);
		submission_form = response.getForms()[1];
		response = submission_form.submit();
		submission_form = response.getForms()[1];
		submission_form.setParameter("login", "me");
		response = submission_form.submit();

		assertEquals("me,it is", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/submissions/generatedurl");
		response = conversation.getResponse(request);
		submission_link = response.getLinkWith("submission4");
		submission_link.click();
		response = conversation.getCurrentPage();

		assertEquals("submission4", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/submissions/generatedurl");
		response = conversation.getResponse(request);
		submission_form = response.getForms()[2];
		response = submission_form.submit();
		submission_form = response.getForms()[2];
		submission_form.setParameter("password", "this pass");
		response = submission_form.submit();

		assertEquals("one more,this pass", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/submissions/generatedurl");
		response = conversation.getResponse(request);
		submission_link = response.getLinkWith("submission6");
		submission_link.click();
		response = conversation.getCurrentPage();

		assertEquals("submission6", response.getText());
	}

	public void testSubmissionGeneratedUrlPathinfo()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	submission_link = null;
		WebForm submission_form = null;

		request = new GetMethodWebRequest("http://localhost:8181/submissions/generatedurlpathinfo");
		response = conversation.getResponse(request);
		submission_link = response.getLinkWith("submission1");
		submission_link.click();
		response = conversation.getCurrentPage();

		assertEquals("/"+
			"thevalue", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/submissions/generatedurlpathinfo");
		response = conversation.getResponse(request);
		submission_form = response.getForms()[0];
		submission_form.setParameter("login", "gbevin2");
		response = submission_form.submit();
		submission_form = response.getForms()[0];
		submission_form.setParameter("password", "oncemoremypassword");
		response = submission_form.submit();

		assertEquals("/"+
			"gbevin2,oncemoremypassword", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/submissions/generatedurlpathinfo");
		response = conversation.getResponse(request);
		submission_form = response.getForms()[1];
		response = submission_form.submit();
		submission_form = response.getForms()[1];
		submission_form.setParameter("login", "me");
		response = submission_form.submit();

		assertEquals("/"+
			"me,it is", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/submissions/generatedurlpathinfo");
		response = conversation.getResponse(request);
		submission_link = response.getLinkWith("submission4");
		submission_link.click();
		response = conversation.getCurrentPage();

		assertEquals("/"+
			"submission4", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/submissions/generatedurlpathinfo");
		response = conversation.getResponse(request);
		submission_form = response.getForms()[2];
		response = submission_form.submit();
		submission_form = response.getForms()[2];
		submission_form.setParameter("password", "this pass");
		response = submission_form.submit();

		assertEquals("/"+
			"one more,this pass", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/submissions/generatedurlpathinfo");
		response = conversation.getResponse(request);
		submission_link = response.getLinkWith("submission6");
		submission_link.click();
		response = conversation.getCurrentPage();

		assertEquals("/"+
			"submission6", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/submissions/generatedurlpathinfo/thepathinfo");
		response = conversation.getResponse(request);
		submission_link = response.getLinkWith("submission1");
		submission_link.click();
		response = conversation.getCurrentPage();

		assertEquals("/thepathinfo"+
			"thevalue", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/submissions/generatedurlpathinfo/thepathinfo2");
		response = conversation.getResponse(request);
		submission_form = response.getForms()[0];
		submission_form.setParameter("login", "gbevin2");
		response = submission_form.submit();
		submission_form = response.getForms()[0];
		submission_form.setParameter("password", "oncemoremypassword");
		response = submission_form.submit();

		assertEquals("/thepathinfo2"+
			"gbevin2,oncemoremypassword", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/submissions/generatedurlpathinfo/thepathinfo3");
		response = conversation.getResponse(request);
		submission_form = response.getForms()[1];
		response = submission_form.submit();
		submission_form = response.getForms()[1];
		submission_form.setParameter("login", "me");
		response = submission_form.submit();

		assertEquals("/thepathinfo3"+
			"me,it is", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/submissions/generatedurlpathinfo/thepathinfo4");
		response = conversation.getResponse(request);
		submission_link = response.getLinkWith("submission4");
		submission_link.click();
		response = conversation.getCurrentPage();

		assertEquals("/thepathinfo4"+
			"submission4", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/submissions/generatedurlpathinfo/thepathinfo5");
		response = conversation.getResponse(request);
		submission_form = response.getForms()[2];
		response = submission_form.submit();
		submission_form = response.getForms()[2];
		submission_form.setParameter("password", "this pass");
		response = submission_form.submit();

		assertEquals("/thepathinfo5"+
			"one more,this pass", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/submissions/generatedurlpathinfo/thepathinfo6");
		response = conversation.getResponse(request);
		submission_link = response.getLinkWith("submission6");
		submission_link.click();
		response = conversation.getCurrentPage();

		assertEquals("/thepathinfo6"+
			"submission6", response.getText());
	}

	public void testSubmissionGeneratedUrlOverflow()
	throws Exception
	{
		setupSite("site/submissions.xml");

		// setup swallowing log formatters
		HashMap<Handler, Formatter>	formatters = new HashMap<Handler, Formatter>();
		SwallowingLogFormatter		formatter = new SwallowingLogFormatter();
		Logger logger = Logger.getLogger("");
		for (Handler handler : logger.getHandlers())
		{
			formatters.put(handler, handler.getFormatter());
			handler.setFormatter(formatter);
		}

		try
		{
			WebConversation	conversation = new WebConversation();
			WebRequest request = null;
			WebResponse response = null;
			WebLink	submission_link = null;

			request = new GetMethodWebRequest("http://localhost:8181/submissions/generatedurloverflow");
			response = conversation.getResponse(request);
			submission_link = response.getLinkWith("submission1");
			submission_link.click();
			response = conversation.getCurrentPage();

			assertEquals(StringUtils.repeat("abcdefghijklmnopqrstuvwxyz", 74)+"01234567890", response.getText());

			assertEquals(1, formatter.getRecords().size());
			assertEquals("The submission 'submission1' of element '.GENERATEDURLOVERFLOW' generated an URL whose length of 2049 exceeds the maximum length of 2048 bytes, using session state store instead. The generated URL was '/submissions/generatedurloverflow?submission=submission1&parameter="+StringUtils.repeat("abcdefghijklmnopqrstuvwxyz", 74)+"01234567890"+"&submissioncontext=LkdFTkVSQVRFRFVSTE9WRVJGTE9X'.", formatter.getRecords().get(0).getMessage());
		}
		finally
		{
			// restore the previous formatters
			for (Handler handler : logger.getHandlers())
			{
				handler.setFormatter(formatters.get(handler));
			}
		}
	}

	public void testSubmissionInputsPreserved()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	submission_link = null;
		WebForm submission_form = null;

		request = new GetMethodWebRequest("http://localhost:8181/submissions/inputspreserved");
		request.setParameter("input1", "submission1input1value");
		request.setParameter("input4", "submission1input4value");
		response = conversation.getResponse(request);
		submission_link = response.getLinkWith("submission1");
		String url = submission_link.getURLString();
		request = new GetMethodWebRequest("http://localhost:8181"+url);
		response = conversation.getResponse(request);
		assertEquals("thevalue"+
			"submission1input1value"+
			"null"+
			"input3default", response.getText());
		request = new GetMethodWebRequest("http://localhost:8181"+url+"&input1=submission1input1overriddenvalue");
		response = conversation.getResponse(request);
		assertEquals("thevalue"+
			"submission1input1value"+
			"null"+
			"input3default", response.getText());


		request = new GetMethodWebRequest("http://localhost:8181/submissions/inputspreserved");
		request.setParameter("input1", "submission2input1value");
		request.setParameter("input4", "submission2input4value");
		response = conversation.getResponse(request);
		submission_form = response.getForms()[0];
		submission_form.setParameter("login", "gbevin2");
		response = submission_form.submit();
		submission_form = response.getForms()[0];
		submission_form.setParameter("password", "oncemoremypassword");
		response = submission_form.submit();
		assertEquals("gbevin2,oncemoremypassword"+
			"submission2input1value"+
			"null"+
			"input3default", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/submissions/inputspreserved");
		request.setParameter("input1", "submission3input1value");
		request.setParameter("input4", "submission3input4value");
		response = conversation.getResponse(request);
		submission_form = response.getForms()[1];
		response = submission_form.submit();
		submission_form = response.getForms()[1];
		submission_form.setParameter("login", "me");
		response = submission_form.submit();
		assertEquals("me,it is"+
			"submission3input1value"+
			"null"+
			"input3default", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/submissions/inputspreserved");
		request.setParameter("input1", "submission4input1value");
		request.setParameter("input4", "submission4input4value");
		response = conversation.getResponse(request);
		submission_link = response.getLinkWith("submission4");
		url = submission_link.getURLString();
		request = new GetMethodWebRequest("http://localhost:8181"+url);
		response = conversation.getResponse(request);
		assertEquals("submission4"+
			"submission4input1value"+
			"null"+
			"input3default", response.getText());
		request = new GetMethodWebRequest("http://localhost:8181"+url+"&input1=submission4input1overriddenvalue");
		response = conversation.getResponse(request);
		assertEquals("submission4"+
			"submission4input1value"+
			"null"+
			"input3default", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/submissions/inputspreserved");
		request.setParameter("input1", "submission5input1value");
		request.setParameter("input4", "submission5input4value");
		response = conversation.getResponse(request);
		submission_form = response.getForms()[2];
		response = submission_form.submit();
		submission_form = response.getForms()[2];
		submission_form.setParameter("password", "this pass");
		response = submission_form.submit();
		assertEquals("one more,this pass"+
			"submission5input1value"+
			"null"+
			"input3default", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/submissions/inputspreserved");
		request.setParameter("input1", "submission6input1value");
		request.setParameter("input4", "submission6input4value");
		response = conversation.getResponse(request);
		submission_link = response.getLinkWith("submission6");
		url = submission_link.getURLString();
		request = new GetMethodWebRequest("http://localhost:8181"+url);
		response = conversation.getResponse(request);
		assertEquals("submission6"+
			"submission6input1value"+
			"null"+
			"input3default", response.getText());
		request = new GetMethodWebRequest("http://localhost:8181"+url+"&input1=submission6input1overriddenvalue");
		response = conversation.getResponse(request);
		assertEquals("submission6"+
			"submission6input1value"+
			"null"+
			"input3default", response.getText());
	}

	public void testSubmissionBean()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/submissions/bean/normal");
		WebResponse response = null;
		WebForm form = null;
		response = conversation.getResponse(request);

		form = response.getFormWithName("submissionform");
		request = form.getRequest();
		request.setParameter("enum", "WEDNESDAY");
		request.setParameter("string", "the string");
		request.setParameter("boolean", "y");
		request.setParameter("string", "the string");
		request.setParameter("stringbuffer", "the stringbuffer");
		request.setParameter("int", "23154");
		request.setParameter("integer", "893749");
		request.setParameter("char", "u");
		request.setParameter("character", "R");
		request.setParameter("boolean", "y");
		request.setParameter("booleanObject", "no");
		request.setParameter("byte", "120");
		request.setParameter("byteObject", "21");
		request.setParameter("double", "34878.34");
		request.setParameter("doubleObject", "25435.98");
		request.setParameter("float", "3434.76");
		request.setParameter("floatObject", "6534.8");
		request.setParameter("long", "34347897");
		request.setParameter("longObject", "2335454");
		request.setParameter("short", "32");
		request.setParameter("shortObject", "12");
		request.setParameter("date", "2005-08-20 09:44");
		request.setParameter("dateFormatted", "Sat 20 Aug 2005 09:44:00");
		request.setParameter("datesFormatted", new String[] {"Sun 21 Aug 2005 11:06:14", "Mon 17 Jul 2006 16:05:31"});
		request.setParameter("serializableParam", SerializationUtils.serializeToString(new SerializableParam(13, "Thirteen")));
		request.setParameter("serializableParams", new String[] {SerializationUtils.serializeToString(new SerializableParam(9, "Nine")),SerializationUtils.serializeToString(new SerializableParam(91, "NinetyOne"))});
		request.selectFile("stringFile", "somedesign.html", new StringBufferInputStream("this is some html content"), "text/html");
		byte[] image_bytes = ResourceFinderClasspath.getInstance().useStream("uwyn.png", new InputStreamUser() {
				public Object useInputStream(InputStream stream) throws InnerClassException
				{
						try
						{
							return FileUtils.readBytes(stream);
						}
						catch (FileUtilsErrorException e)
						{
							throwException(e);
						}

						return null;
				}
			});
		request.selectFile("bytesFile", "someimage.png", new ByteArrayInputStream(image_bytes), "image/png");
		request.selectFile("streamFile", "somefile.png", new ByteArrayInputStream(image_bytes), null);

		response = conversation.getResponse(request);

		assertEquals("WEDNESDAY,the string,the stringbuffer,23154,893749,u,null,true,false,0,21,34878.34,25435.98,3434.76,6534.8,34347897,2335454,32,12,this is some html content,true,someimage.png,true,Sat 20 Aug 2005 09:44:00,Sun 21 Aug 2005 11:06:14,Mon 17 Jul 2006 16:05:31,13:Thirteen,9:Nine,91:NinetyOne", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/submissions/bean/normal");
		response = conversation.getResponse(request);

		form = response.getFormWithName("submissionform");
		request = form.getRequest();
		request.setParameter("enum", "invalid");
		request.setParameter("string", "the string");
		request.setParameter("boolean", "y");
		request.setParameter("string", "the string");
		request.setParameter("stringbuffer", "the stringbuffer");
		request.setParameter("int", "23fd33");
		request.setParameter("char", "u");
		request.setParameter("character", "R");
		request.setParameter("boolean", "y");
		request.setParameter("booleanObject", "no");
		request.setParameter("byte", "120");
		request.setParameter("byteObject", "21");
		request.setParameter("double", "zef.34");
		request.setParameter("doubleObject", "25435.98");
		request.setParameter("float", "3434.76");
		request.setParameter("floatObject", "6534.8");
		request.setParameter("long", "34347897");
		request.setParameter("longObject", "233f5454");
		request.setParameter("short", "32");
		request.setParameter("shortObject", "");
		request.setParameter("datesFormatted", new String[] {"Sun 21 Aug 2005 11:06:14", "Mon 18 Jul 2006 16:05:31"});
		request.setParameter("serializableParam", "invalid");
		request.setParameter("serializableParams", new String[] {"invalid", SerializationUtils.serializeToString(new SerializableParam(91, "NinetyOne"))});

		response = conversation.getResponse(request);

		assertEquals("INVALID : datesFormatted\nNOTNUMERIC : double\nINVALID : enum\nNOTNUMERIC : int\nNOTNUMERIC : longObject\nINVALID : serializableParam\nINVALID : serializableParams\nnull,the string,the stringbuffer,0,null,u,null,true,false,0,21,0.0,25435.98,3434.76,6534.8,34347897,null,32,null,null,null,null,null,null,Sun 21 Aug 2005 11:06:14,null,null,null,91:NinetyOne", response.getText());
	}

	public void testSubmissionBeanPrefix()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/submissions/bean/prefix");
		WebResponse response = null;
		WebForm form = null;
		response = conversation.getResponse(request);

		form = response.getFormWithName("submissionform");
		request = form.getRequest();
		request.setParameter(ReservedParameters.SUBMISSION, "bean");
		request.setParameter("prefix_enum", "MONDAY");
		request.setParameter("prefix_string", "the string");
		request.setParameter("prefix_boolean", "y");
		request.setParameter("prefix_string", "the string");
		request.setParameter("prefix_stringbuffer", "the stringbuffer");
		request.setParameter("prefix_int", "23154");
		request.setParameter("prefix_integer", "893749");
		request.setParameter("prefix_char", "u");
		request.setParameter("prefix_character", "R");
		request.setParameter("prefix_boolean", "y");
		request.setParameter("prefix_booleanObject", "no");
		request.setParameter("prefix_byte", "120");
		request.setParameter("prefix_byteObject", "21");
		request.setParameter("prefix_double", "34878.34");
		request.setParameter("prefix_doubleObject", "25435.98");
		request.setParameter("prefix_float", "3434.76");
		request.setParameter("prefix_floatObject", "6534.8");
		request.setParameter("prefix_long", "34347897");
		request.setParameter("prefix_longObject", "2335454");
		request.setParameter("prefix_short", "32");
		request.setParameter("prefix_shortObject", "12");
		request.setParameter("prefix_date", "2005-08-20 09:44");
		request.setParameter("prefix_dateFormatted", "Sat 20 Aug 2005 09:44:00");
		request.setParameter("prefix_datesFormatted", new String[] {"Sun 21 Aug 2005 11:06:14", "Mon 17 Jul 2006 16:05:31"});
		request.setParameter("prefix_serializableParam", SerializationUtils.serializeToString(new SerializableParam(13, "Thirteen")));
		request.setParameter("prefix_serializableParams", new String[] {SerializationUtils.serializeToString(new SerializableParam(9, "Nine")),SerializationUtils.serializeToString(new SerializableParam(91, "NinetyOne"))});
		request.selectFile("prefix_stringFile", "somedesign.html", new StringBufferInputStream("this is some html content"), "text/html");
		byte[] image_bytes = ResourceFinderClasspath.getInstance().useStream("uwyn.png", new InputStreamUser() {
				public Object useInputStream(InputStream stream) throws InnerClassException
				{
						try
						{
							return FileUtils.readBytes(stream);
						}
						catch (FileUtilsErrorException e)
						{
							throwException(e);
						}

						return null;
				}
			});
		request.selectFile("prefix_bytesFile", "someimage.png", new ByteArrayInputStream(image_bytes), "image/png");
		request.selectFile("prefix_streamFile", "somefile.png", new ByteArrayInputStream(image_bytes), null);

		response = conversation.getResponse(request);

		assertEquals("MONDAY,the string,the stringbuffer,23154,893749,u,null,true,false,0,21,34878.34,25435.98,3434.76,6534.8,34347897,2335454,32,12,this is some html content,true,someimage.png,true,Sat 20 Aug 2005 09:44:00,Sun 21 Aug 2005 11:06:14,Mon 17 Jul 2006 16:05:31,13:Thirteen,9:Nine,91:NinetyOne", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/submissions/bean/prefix");
		response = conversation.getResponse(request);

		form = response.getFormWithName("submissionform");
		request = form.getRequest();
		request.setParameter("prefix_enum", "invalid");
		request.setParameter("prefix_string", "the string");
		request.setParameter("prefix_boolean", "y");
		request.setParameter("prefix_string", "the string");
		request.setParameter("prefix_stringbuffer", "the stringbuffer");
		request.setParameter("prefix_int", "23fd33");
		request.setParameter("prefix_char", "u");
		request.setParameter("prefix_character", "R");
		request.setParameter("prefix_boolean", "y");
		request.setParameter("prefix_booleanObject", "no");
		request.setParameter("prefix_byte", "120");
		request.setParameter("prefix_byteObject", "21");
		request.setParameter("prefix_double", "zef.34");
		request.setParameter("prefix_doubleObject", "25435.98");
		request.setParameter("prefix_float", "3434.76");
		request.setParameter("prefix_floatObject", "6534.8");
		request.setParameter("prefix_long", "34347897");
		request.setParameter("prefix_longObject", "233f5454");
		request.setParameter("prefix_short", "32");
		request.setParameter("prefix_shortObject", "");
		request.setParameter("prefix_datesFormatted", new String[] {"Sun 21 Aug 2005 11:06:14", "Mon 18 Jul 2006 16:05:31"});
		request.setParameter("prefix_serializableParam", "invalid");
		request.setParameter("prefix_serializableParams", new String[] {"invalid", SerializationUtils.serializeToString(new SerializableParam(91, "NinetyOne"))});
		response = conversation.getResponse(request);

		assertEquals("INVALID : datesFormatted\nNOTNUMERIC : double\nINVALID : enum\nNOTNUMERIC : int\nNOTNUMERIC : longObject\nINVALID : serializableParam\nINVALID : serializableParams\nnull,the string,the stringbuffer,0,null,u,null,true,false,0,21,0.0,25435.98,3434.76,6534.8,34347897,null,32,null,null,null,null,null,null,Sun 21 Aug 2005 11:06:14,null,null,null,91:NinetyOne", response.getText());
	}

	public void testNamedSubmissionBean()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new PostMethodWebRequest("http://localhost:8181/submissions/named_bean/normal");
		request.setParameter(ReservedParameters.SUBMISSION, "bean");
		request.setParameter("enum", "SATURDAY");
		request.setParameter("string", "the string");
		request.setParameter("boolean", "y");
		request.setParameter("string", "the string");
		request.setParameter("stringbuffer", "the stringbuffer");
		request.setParameter("int", "23154");
		request.setParameter("integer", "893749");
		request.setParameter("char", "u");
		request.setParameter("character", "R");
		request.setParameter("boolean", "y");
		request.setParameter("booleanObject", "no");
		request.setParameter("byte", "120");
		request.setParameter("byteObject", "21");
		request.setParameter("double", "34878.34");
		request.setParameter("doubleObject", "25435.98");
		request.setParameter("float", "3434.76");
		request.setParameter("floatObject", "6534.8");
		request.setParameter("long", "34347897");
		request.setParameter("longObject", "2335454");
		request.setParameter("short", "32");
		request.setParameter("shortObject", "12");
		WebResponse response = conversation.getResponse(request);

		assertEquals("SATURDAY,the string,the stringbuffer,23154,893749,u,null,true,false,0,21,34878.34,25435.98,3434.76,6534.8,34347897,2335454,32,12", response.getText());

		request = new PostMethodWebRequest("http://localhost:8181/submissions/named_bean/normal");
		request.setParameter(ReservedParameters.SUBMISSION, "bean");
		request.setParameter("enum", "invalid");
		request.setParameter("string", "the string");
		request.setParameter("boolean", "y");
		request.setParameter("string", "the string");
		request.setParameter("stringbuffer", "the stringbuffer");
		request.setParameter("int", "23fd33");
		request.setParameter("char", "u");
		request.setParameter("character", "R");
		request.setParameter("boolean", "y");
		request.setParameter("booleanObject", "no");
		request.setParameter("byte", "120");
		request.setParameter("byteObject", "21");
		request.setParameter("double", "zef.34");
		request.setParameter("doubleObject", "25435.98");
		request.setParameter("float", "3434.76");
		request.setParameter("floatObject", "6534.8");
		request.setParameter("long", "34347897");
		request.setParameter("longObject", "233f5454");
		request.setParameter("short", "32");
		request.setParameter("shortObject", "");
		response = conversation.getResponse(request);

		assertEquals("NOTNUMERIC : double\nINVALID : enum\nNOTNUMERIC : int\nNOTNUMERIC : longObject\nnull,the string,the stringbuffer,0,null,u,null,true,false,0,21,0.0,25435.98,3434.76,6534.8,34347897,null,32,null", response.getText());
	}

	public void testNamedSubmissionBeanPrefix()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new PostMethodWebRequest("http://localhost:8181/submissions/named_bean/prefix");
		request.setParameter(ReservedParameters.SUBMISSION, "bean");
		request.setParameter("prefix_enum", "TUESDAY");
		request.setParameter("prefix_string", "the string");
		request.setParameter("prefix_boolean", "y");
		request.setParameter("prefix_string", "the string");
		request.setParameter("prefix_stringbuffer", "the stringbuffer");
		request.setParameter("prefix_int", "23154");
		request.setParameter("prefix_integer", "893749");
		request.setParameter("prefix_char", "u");
		request.setParameter("prefix_character", "R");
		request.setParameter("prefix_boolean", "y");
		request.setParameter("prefix_booleanObject", "no");
		request.setParameter("prefix_byte", "120");
		request.setParameter("prefix_byteObject", "21");
		request.setParameter("prefix_double", "34878.34");
		request.setParameter("prefix_doubleObject", "25435.98");
		request.setParameter("prefix_float", "3434.76");
		request.setParameter("prefix_floatObject", "6534.8");
		request.setParameter("prefix_long", "34347897");
		request.setParameter("prefix_longObject", "2335454");
		request.setParameter("prefix_short", "32");
		request.setParameter("prefix_shortObject", "12");
		WebResponse response = conversation.getResponse(request);

		assertEquals("TUESDAY,the string,the stringbuffer,23154,893749,u,null,true,false,0,21,34878.34,25435.98,3434.76,6534.8,34347897,2335454,32,12", response.getText());

		request = new PostMethodWebRequest("http://localhost:8181/submissions/named_bean/prefix");
		request.setParameter(ReservedParameters.SUBMISSION, "bean");
		request.setParameter("prefix_enum", "invalid");
		request.setParameter("prefix_string", "the string");
		request.setParameter("prefix_boolean", "y");
		request.setParameter("prefix_string", "the string");
		request.setParameter("prefix_stringbuffer", "the stringbuffer");
		request.setParameter("prefix_int", "23fd33");
		request.setParameter("prefix_char", "u");
		request.setParameter("prefix_character", "R");
		request.setParameter("prefix_boolean", "y");
		request.setParameter("prefix_booleanObject", "no");
		request.setParameter("prefix_byte", "120");
		request.setParameter("prefix_byteObject", "21");
		request.setParameter("prefix_double", "zef.34");
		request.setParameter("prefix_doubleObject", "25435.98");
		request.setParameter("prefix_float", "3434.76");
		request.setParameter("prefix_floatObject", "6534.8");
		request.setParameter("prefix_long", "34347897");
		request.setParameter("prefix_longObject", "233f5454");
		request.setParameter("prefix_short", "32");
		request.setParameter("prefix_shortObject", "");
		response = conversation.getResponse(request);

		assertEquals("NOTNUMERIC : double\nINVALID : enum\nNOTNUMERIC : int\nNOTNUMERIC : longObject\nnull,the string,the stringbuffer,0,null,u,null,true,false,0,21,0.0,25435.98,3434.76,6534.8,34347897,null,32,null", response.getText());
	}

	public void testNamedSubmissionBeanInjection()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new PostMethodWebRequest("http://localhost:8181/submissions/named_bean/normal/injection");
		request.setParameter(ReservedParameters.SUBMISSION, "bean");
		request.setParameter("enum", "THURSDAY");
		request.setParameter("string", "the string");
		request.setParameter("boolean", "y");
		request.setParameter("string", "the string");
		request.setParameter("stringbuffer", "the stringbuffer");
		request.setParameter("int", "23154");
		request.setParameter("integer", "893749");
		request.setParameter("char", "u");
		request.setParameter("character", "R");
		request.setParameter("boolean", "y");
		request.setParameter("booleanObject", "no");
		request.setParameter("byte", "120");
		request.setParameter("byteObject", "21");
		request.setParameter("double", "34878.34");
		request.setParameter("doubleObject", "25435.98");
		request.setParameter("float", "3434.76");
		request.setParameter("floatObject", "6534.8");
		request.setParameter("long", "34347897");
		request.setParameter("longObject", "2335454");
		request.setParameter("short", "32");
		request.setParameter("shortObject", "12");
		WebResponse response = conversation.getResponse(request);

		assertEquals("THURSDAY,the string,the stringbuffer,23154,893749,u,null,true,false,0,21,34878.34,25435.98,3434.76,6534.8,34347897,2335454,32,12", response.getText());

		request = new PostMethodWebRequest("http://localhost:8181/submissions/named_bean/normal/injection");
		request.setParameter(ReservedParameters.SUBMISSION, "bean");
		request.setParameter("enum", "invalid");
		request.setParameter("string", "the string");
		request.setParameter("boolean", "y");
		request.setParameter("string", "the string");
		request.setParameter("stringbuffer", "the stringbuffer");
		request.setParameter("int", "23fd33");
		request.setParameter("char", "u");
		request.setParameter("character", "R");
		request.setParameter("boolean", "y");
		request.setParameter("booleanObject", "no");
		request.setParameter("byte", "120");
		request.setParameter("byteObject", "21");
		request.setParameter("double", "zef.34");
		request.setParameter("doubleObject", "25435.98");
		request.setParameter("float", "3434.76");
		request.setParameter("floatObject", "6534.8");
		request.setParameter("long", "34347897");
		request.setParameter("longObject", "233f5454");
		request.setParameter("short", "32");
		request.setParameter("shortObject", "");
		response = conversation.getResponse(request);

		assertEquals("NOTNUMERIC : double\nINVALID : enum\nNOTNUMERIC : int\nNOTNUMERIC : longObject\nnull,the string,the stringbuffer,0,null,u,null,true,false,0,21,0.0,25435.98,3434.76,6534.8,34347897,null,32,null", response.getText());
	}

	public void testNamedSubmissionBeanPrefixInjection()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new PostMethodWebRequest("http://localhost:8181/submissions/named_bean/prefix/injection");
		request.setParameter(ReservedParameters.SUBMISSION, "bean");
		request.setParameter("prefix_enum", "TUESDAY");
		request.setParameter("prefix_string", "the string");
		request.setParameter("prefix_boolean", "y");
		request.setParameter("prefix_string", "the string");
		request.setParameter("prefix_stringbuffer", "the stringbuffer");
		request.setParameter("prefix_int", "23154");
		request.setParameter("prefix_integer", "893749");
		request.setParameter("prefix_char", "u");
		request.setParameter("prefix_character", "R");
		request.setParameter("prefix_boolean", "y");
		request.setParameter("prefix_booleanObject", "no");
		request.setParameter("prefix_byte", "120");
		request.setParameter("prefix_byteObject", "21");
		request.setParameter("prefix_double", "34878.34");
		request.setParameter("prefix_doubleObject", "25435.98");
		request.setParameter("prefix_float", "3434.76");
		request.setParameter("prefix_floatObject", "6534.8");
		request.setParameter("prefix_long", "34347897");
		request.setParameter("prefix_longObject", "2335454");
		request.setParameter("prefix_short", "32");
		request.setParameter("prefix_shortObject", "12");
		WebResponse response = conversation.getResponse(request);

		assertEquals("TUESDAY,the string,the stringbuffer,23154,893749,u,null,true,false,0,21,34878.34,25435.98,3434.76,6534.8,34347897,2335454,32,12", response.getText());

		request = new PostMethodWebRequest("http://localhost:8181/submissions/named_bean/prefix/injection");
		request.setParameter(ReservedParameters.SUBMISSION, "bean");
		request.setParameter("prefix_enum", "invalid");
		request.setParameter("prefix_string", "the string");
		request.setParameter("prefix_boolean", "y");
		request.setParameter("prefix_string", "the string");
		request.setParameter("prefix_stringbuffer", "the stringbuffer");
		request.setParameter("prefix_int", "23fd33");
		request.setParameter("prefix_char", "u");
		request.setParameter("prefix_character", "R");
		request.setParameter("prefix_boolean", "y");
		request.setParameter("prefix_booleanObject", "no");
		request.setParameter("prefix_byte", "120");
		request.setParameter("prefix_byteObject", "21");
		request.setParameter("prefix_double", "zef.34");
		request.setParameter("prefix_doubleObject", "25435.98");
		request.setParameter("prefix_float", "3434.76");
		request.setParameter("prefix_floatObject", "6534.8");
		request.setParameter("prefix_long", "34347897");
		request.setParameter("prefix_longObject", "233f5454");
		request.setParameter("prefix_short", "32");
		request.setParameter("prefix_shortObject", "");
		response = conversation.getResponse(request);

		assertEquals("NOTNUMERIC : double\nINVALID : enum\nNOTNUMERIC : int\nNOTNUMERIC : longObject\nnull,the string,the stringbuffer,0,null,u,null,true,false,0,21,0.0,25435.98,3434.76,6534.8,34347897,null,32,null", response.getText());
	}

	public void testSubmissionFillBean()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/submissions/fill/bean/normal");
		WebResponse response = null;
		WebForm form = null;
		response = conversation.getResponse(request);

		form = response.getFormWithName("submissionform");
		request = form.getRequest();
		request.setParameter("enum", "MONDAY");
		request.setParameter("string", "the string");
		request.setParameter("boolean", "y");
		request.setParameter("string", "the string");
		request.setParameter("stringbuffer", "the stringbuffer");
		request.setParameter("int", "23154");
		request.setParameter("integer", "893749");
		request.setParameter("char", "u");
		request.setParameter("character", "R");
		request.setParameter("boolean", "y");
		request.setParameter("booleanObject", "no");
		request.setParameter("byte", "120");
		request.setParameter("byteObject", "21");
		request.setParameter("double", "34878.34");
		request.setParameter("doubleObject", "25435.98");
		request.setParameter("float", "3434.76");
		request.setParameter("floatObject", "6534.8");
		request.setParameter("long", "34347897");
		request.setParameter("longObject", "2335454");
		request.setParameter("short", "32");
		request.setParameter("shortObject", "12");
		request.selectFile("stringFile", "somedesign.html", new StringBufferInputStream("this is some html content"), "text/html");
		byte[] image_bytes = ResourceFinderClasspath.getInstance().useStream("uwyn.png", new InputStreamUser() {
				public Object useInputStream(InputStream stream) throws InnerClassException
				{
					try
					{
						return FileUtils.readBytes(stream);
					}
					catch (FileUtilsErrorException e)
					{
						throwException(e);
					}

					return null;
				}
			});
		request.selectFile("bytesFile", "someimage.png", new ByteArrayInputStream(image_bytes), "image/png");
		request.selectFile("streamFile", "somefile.png", new ByteArrayInputStream(image_bytes), null);

		response = conversation.getResponse(request);

		assertEquals("MONDAY,the string,the stringbuffer,23154,893749,u,b,true,false,22,21,34878.34,25435.98,3434.76,6534.8,34347897,2335454,32,12,this is some html content,true,someimage.png,true", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/submissions/fill/bean/normal");
		response = conversation.getResponse(request);

		form = response.getFormWithName("submissionform");
		request = form.getRequest();
		request.setParameter("enum", "invalid");
		request.setParameter("string", "the string");
		request.setParameter("boolean", "y");
		request.setParameter("string", "the string");
		request.setParameter("stringbuffer", "the stringbuffer");
		request.setParameter("int", "23fd33");
		request.setParameter("char", "u");
		request.setParameter("character", "R");
		request.setParameter("boolean", "y");
		request.setParameter("booleanObject", "no");
		request.setParameter("byte", "120");
		request.setParameter("byteObject", "21");
		request.setParameter("double", "zef.34");
		request.setParameter("doubleObject", "25435.98");
		request.setParameter("float", "3434.76");
		request.setParameter("floatObject", "6534.8");
		request.setParameter("long", "34347897");
		request.setParameter("longObject", "233f5454");
		request.setParameter("short", "32");
		request.setParameter("shortObject", "");

		response = conversation.getResponse(request);

		assertEquals("NOTNUMERIC : double\nINVALID : enum\nNOTNUMERIC : int\nNOTNUMERIC : longObject\nnull,the string,the stringbuffer,999,null,u,b,true,false,22,21,123.45,25435.98,3434.76,6534.8,34347897,55,32,null,stringFile,false,null,false", response.getText());
	}

	public void testSubmissionFillBeanGroup()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/submissions/fill/bean/group");
		WebResponse response = null;
		WebForm form = null;
		response = conversation.getResponse(request);

		form = response.getFormWithName("submissionform");
		request = form.getRequest();
		request.setParameter("enum", "SUNDAY");
		request.setParameter("string", "the string");
		request.setParameter("boolean", "y");
		request.setParameter("string", "the string");
		request.setParameter("stringbuffer", "the stringbuffer");
		request.setParameter("int", "23154");
		request.setParameter("integer", "893749");
		request.setParameter("char", "u");
		request.setParameter("character", "R");
		request.setParameter("boolean", "y");
		request.setParameter("booleanObject", "no");
		request.setParameter("byte", "120");
		request.setParameter("byteObject", "21");
		request.setParameter("double", "34878.34");
		request.setParameter("doubleObject", "25435.98");
		request.setParameter("float", "3434.76");
		request.setParameter("floatObject", "6534.8");
		request.setParameter("long", "34347897");
		request.setParameter("longObject", "2335454");
		request.setParameter("short", "32");
		request.setParameter("shortObject", "12");
		request.selectFile("stringFile", "somedesign.html", new StringBufferInputStream("this is some html content"), "text/html");
		byte[] image_bytes = ResourceFinderClasspath.getInstance().useStream("uwyn.png", new InputStreamUser() {
				public Object useInputStream(InputStream stream) throws InnerClassException
				{
					try
					{
						return FileUtils.readBytes(stream);
					}
					catch (FileUtilsErrorException e)
					{
						throwException(e);
					}

					return null;
				}
			});
		request.selectFile("bytesFile", "someimage.png", new ByteArrayInputStream(image_bytes), "image/png");
		request.selectFile("streamFile", "somefile.png", new ByteArrayInputStream(image_bytes), null);

		response = conversation.getResponse(request);

		assertEquals("SUNDAY,the string,stringbuffer,23154,111,a,b,false,true,22,33,123.45,234.56,321.54,432.65,44,2335454,32,77,stringFile,false,null,false", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/submissions/fill/bean/group");
		response = conversation.getResponse(request);

		form = response.getFormWithName("submissionform");
		request = form.getRequest();
		request.setParameter("enum", "invalid");
		request.setParameter("string", "the string");
		request.setParameter("boolean", "y");
		request.setParameter("string", "the string");
		request.setParameter("stringbuffer", "the stringbuffer");
		request.setParameter("int", "23fd33");
		request.setParameter("char", "u");
		request.setParameter("character", "R");
		request.setParameter("boolean", "y");
		request.setParameter("booleanObject", "no");
		request.setParameter("byte", "120");
		request.setParameter("byteObject", "21");
		request.setParameter("double", "zef.34");
		request.setParameter("doubleObject", "25435.98");
		request.setParameter("float", "3434.76");
		request.setParameter("floatObject", "6534.8");
		request.setParameter("long", "34347897");
		request.setParameter("longObject", "233f5454");
		request.setParameter("shortObject", "");

		response = conversation.getResponse(request);

		assertEquals("NOTNUMERIC : int\nNOTNUMERIC : longObject\nSATURDAY,the string,stringbuffer,999,111,a,b,false,true,22,33,123.45,234.56,321.54,432.65,44,55,-24,77,stringFile,false,null,false", response.getText());
	}


	public void testParamsGenerated()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new PostMethodWebRequest("http://localhost:8181/params/generated");
		request.setParameter(ReservedParameters.SUBMISSION, "name");
		request.setParameter("wantsupdates", "on");
		request.setParameter("colors", new String[] {"orange", "blue", "green"});
		request.setParameter("firstname", "Geert");
		request.setParameter("lastname", "Bevin");
		response = conversation.getResponse(request);
		assertEquals("Geert, Bevin\n"+
			"<input type=\"checkbox\" name=\"wantsupdates\" checked=\"checked\"> I want updates<br />\n"+
			"<input type=\"checkbox\" name=\"colors\" value=\"orange\" checked=\"checked\">orange<br />\n"+
			"<input type=\"checkbox\" name=\"colors\" value=\"blue\" checked=\"checked\">blue<br />\n"+
			"<input type=\"checkbox\" name=\"colors\" value=\"red\">red<br />\n"+
			"<input type=\"radio\" name=\"firstname\" checked=\"checked\"> Geert\n"+
			"<input type=\"radio\" name=\"firstname\"> Nathalie\n"+
			"<select name=\"lastname\">\n"+
			"\t<option value=\"Bevin\" selected=\"selected\">Bevin</option>\n"+
			"\t<option value=\"Mafessoni\">Mafessoni</option>\n"+
			"</select>\n", response.getText());

		request = new PostMethodWebRequest("http://localhost:8181/params/generated");
		request.setParameter(ReservedParameters.SUBMISSION, "name");
		response = conversation.getResponse(request);
		assertEquals(", \n"+
			"<input type=\"checkbox\" name=\"wantsupdates\"> I want updates<br />\n"+
			"<input type=\"checkbox\" name=\"colors\" value=\"orange\">orange<br />\n"+
			"<input type=\"checkbox\" name=\"colors\" value=\"blue\">blue<br />\n"+
			"<input type=\"checkbox\" name=\"colors\" value=\"red\">red<br />\n"+
			"<input type=\"radio\" name=\"firstname\"> Geert\n"+
			"<input type=\"radio\" name=\"firstname\"> Nathalie\n"+
			"<select name=\"lastname\">\n"+
			"\t<option value=\"Bevin\">Bevin</option>\n"+
			"\t<option value=\"Mafessoni\">Mafessoni</option>\n"+
			"</select>\n", response.getText());
	}

	public void testFormGenerated()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new PostMethodWebRequest("http://localhost:8181/form/generated");
		request.setParameter(ReservedParameters.SUBMISSION, "form");
		response = conversation.getResponse(request);
		assertEquals(TemplateFactory.ENGINEHTML.get("formbuilder_fields_out_constrained_empty").getContent(), response.getText());
	}

	public void testSetSubmissionBean()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/set_submission_bean");
		response = conversation.getResponse(request);
		assertEquals(", \n"+
			"<input type=\"checkbox\" name=\"wantsupdates\"> I want updates<br />\n"+
			"<input type=\"checkbox\" name=\"colors\" value=\"orange\">orange<br />\n"+
			"<input type=\"checkbox\" name=\"colors\" value=\"blue\">blue<br />\n"+
			"<input type=\"checkbox\" name=\"colors\" value=\"red\">red<br />\n"+
			"<input type=\"radio\" name=\"firstname\"> Geert\n"+
			"<input type=\"radio\" name=\"firstname\"> Nathalie\n"+
			"<select name=\"lastname\">\n"+
			"\t<option value=\"Bevin\">Bevin</option>\n"+
			"\t<option value=\"Mafessoni\">Mafessoni</option>\n"+
			"</select>\n", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/set_submission_bean");
		request.setParameter("populated", "yes");
		response = conversation.getResponse(request);
		assertEquals("Geert, Bevin\n"+
			"<input type=\"checkbox\" name=\"wantsupdates\" checked=\"checked\"> I want updates<br />\n"+
			"<input type=\"checkbox\" name=\"colors\" value=\"orange\" checked=\"checked\">orange<br />\n"+
			"<input type=\"checkbox\" name=\"colors\" value=\"blue\">blue<br />\n"+
			"<input type=\"checkbox\" name=\"colors\" value=\"red\" checked=\"checked\">red<br />\n"+
			"<input type=\"radio\" name=\"firstname\" checked=\"checked\"> Geert\n"+
			"<input type=\"radio\" name=\"firstname\"> Nathalie\n"+
			"<select name=\"lastname\">\n\t<option value=\"Bevin\" selected=\"selected\">Bevin</option>\n"+
			"\t<option value=\"Mafessoni\">Mafessoni</option>\n"+
			"</select>\n", response.getText());
	}

	public void testParamsDefaults()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new PostMethodWebRequest("http://localhost:8181/params/defaults");
		request.setParameter(ReservedParameters.SUBMISSION, "name");
		WebResponse response = conversation.getResponse(request);
		assertEquals("John|Paul,27,the element config value,", response.getText());
	}

	public void testFileUploadOmitted()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm		form = null;

		// no file provided
		request = new GetMethodWebRequest("http://localhost:8181/fileupload/simple");
		response = conversation.getResponse(request);
		form = response.getForms()[0];

		form.setParameter("purpose", "it will serve you well");
		response =  form.submit();
		assertEquals("no file 1;no file 2;it will serve you well", response.getText());
	}

	public void testFileUploadEmpty()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm		form = null;

		// empty file provided
		request = new GetMethodWebRequest("http://localhost:8181/fileupload/simple");
		response = conversation.getResponse(request);
		form = response.getForms()[0];

		File empty_upload = File.createTempFile("rifetest", ".tmp");
		empty_upload.deleteOnExit();
		FileUtils.writeString("", empty_upload);

		form.setParameter("purpose", "it will serve you well");
		UploadFileSpec empty_upload_spec = new UploadFileSpec(empty_upload);
		form.setParameter("doc1", new UploadFileSpec[] {empty_upload_spec});

		response = form.submit();
		assertEquals("empty file 1;no file 2;it will serve you well", response.getText());

		empty_upload.delete();
	}

	public void testFileUploadSingleParam()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm		form = null;

		request = new GetMethodWebRequest("http://localhost:8181/fileupload/simple");
		response = conversation.getResponse(request);
		form = response.getForms()[0];

		String	upload_content = "abcdefghijklmnopqrstuvwxyz";
		File	upload = File.createTempFile("rifetest", ".tmp");
		upload.deleteOnExit();
		FileUtils.writeString(upload_content, upload);

		form.setParameter("purpose", "it will serve you well");
		UploadFileSpec upload_spec = new UploadFileSpec(upload);
		form.setParameter("doc1", new UploadFileSpec[] {upload_spec});

		response = form.submit();
		assertEquals(upload_content+";no file 2;it will serve you well", response.getText());

		upload.delete();
	}

	public void testFileUploadInjection()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm		form = null;

		request = new GetMethodWebRequest("http://localhost:8181/fileupload/injection");
		response = conversation.getResponse(request);
		form = response.getForms()[0];

		String	upload_content = "abcdefghijklmnopqrstuvwxyz";
		File	upload = File.createTempFile("rifetest", ".tmp");
		upload.deleteOnExit();
		FileUtils.writeString(upload_content, upload);

		form.setParameter("purpose", "it will serve you well");
		UploadFileSpec upload_spec = new UploadFileSpec(upload);
		form.setParameter("doc1", new UploadFileSpec[] {upload_spec});

		response = form.submit();
		assertEquals(upload_content+";it will serve you well", response.getText());

		upload.delete();
	}

	public void testFileUploadMultipleParams()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm		form = null;

		request = new GetMethodWebRequest("http://localhost:8181/fileupload/simple");
		response = conversation.getResponse(request);
		form = response.getForms()[0];

		String	upload1_content = "abcdefghijklmnopqrstuvwxyz";
		File	upload1 = File.createTempFile("rifetest", ".tmp");
		String	upload2_content = "oiuezroizehfkjsdgfhgizeugfizuhfksjdhfiuzhfiuzehfizeuhfziuh";
		File	upload2 = File.createTempFile("rifetest", ".tmp");
		upload1.deleteOnExit();
		FileUtils.writeString(upload1_content, upload1);
		upload2.deleteOnExit();
		FileUtils.writeString(upload2_content, upload2);

		form.setParameter("purpose", "it will serve you well");
		UploadFileSpec upload_spec1 = new UploadFileSpec(upload1);
		UploadFileSpec upload_spec2 = new UploadFileSpec(upload2);
		form.setParameter("doc1", new UploadFileSpec[] {upload_spec1});
		form.setParameter("doc2", new UploadFileSpec[] {upload_spec2});

		response = form.submit();
		assertEquals(upload1_content+";"+upload2_content+";it will serve you well", response.getText());

		upload1.delete();
		upload2.delete();
	}

	public void testFileUploadSingleParamMultipleFiles()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm		form = null;

		request = new GetMethodWebRequest("http://localhost:8181/fileupload/simple");
		response = conversation.getResponse(request);
		form = response.getForms()[0];

		String	upload1_content = "abcdefghijklmnopqrstuvwxyz";
		File	upload1 = File.createTempFile("rifetest", ".tmp");
		String	upload2_content = "oiuezroizehfkjsdgfhgizeugfizuhfksjdhfiuzhfiuzehfizeuhfziuh";
		File	upload2 = File.createTempFile("rifetest", ".tmp");
		upload1.deleteOnExit();
		FileUtils.writeString(upload1_content, upload1);
		upload2.deleteOnExit();
		FileUtils.writeString(upload2_content, upload2);

		form.setParameter("purpose", "it will serve you well");
		UploadFileSpec upload_spec1 = new UploadFileSpec(upload1);
		UploadFileSpec upload_spec2 = new UploadFileSpec(upload2);
		form.setParameter("doc1", new UploadFileSpec[] {upload_spec1, upload_spec2});

		response = form.submit();
		assertEquals(upload1_content+","+upload2_content+";no file 2;it will serve you well", response.getText());

		upload1.delete();
		upload2.delete();
	}

	public void testFileUploadRegexp()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm		form = null;

		request = new GetMethodWebRequest("http://localhost:8181/fileupload/regexp");
		response = conversation.getResponse(request);
		form = response.getForms()[0];

		form.setParameter("somefile", new UploadFileSpec[] {new UploadFileSpec("file1.txt", new StringBufferInputStream("file1somefilecontent"), "text/plain")});
		form.setParameter("yourdoc1", new UploadFileSpec[] {new UploadFileSpec("file2.txt", new StringBufferInputStream("file2yourdoc1content"), "text/plain")});
		form.setParameter("hisdoc1", new UploadFileSpec[] {new UploadFileSpec("file3.txt", new StringBufferInputStream("file3hisdoc1content"), "text/plain")});
		form.setParameter("thisdoc2", new UploadFileSpec[] {new UploadFileSpec("file4.txt", new StringBufferInputStream("file4thisdoc2content"), "text/plain")});

		response = form.submit();
		assertEquals("file1somefilecontent,\nfile3hisdoc1content,file4thisdoc2content,", response.getText());
	}

	public void testFileUploadSizeLimit()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm		form = null;

		String			upload_content_part = "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567"; // 128 bytes
		StringBuffer	upload_content = new StringBuffer();
		File	upload = File.createTempFile("rifetest", ".tmp");
		upload.deleteOnExit();
		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 8; j++) // 1KB
			{
				upload_content.append(upload_content_part);
			}
		}
		FileUtils.writeString(upload_content.toString(), upload);
		UploadFileSpec upload_spec = null;

		// exactly the same size as the limit
		Config.getRepInstance().setParameter(RifeConfig.Engine.PARAM_FILEUPLOAD_SIZE_LIMIT, ""+upload_content.length());
		request = new GetMethodWebRequest("http://localhost:8181/fileupload/simple");
		response = conversation.getResponse(request);
		form = response.getForms()[0];

		form.setParameter("purpose", "it will serve you well");
		upload_spec = new UploadFileSpec(upload);
		form.setParameter("doc1", new UploadFileSpec[] {upload_spec});

		response = form.submit();
		assertEquals(upload_content.toString()+";no file 2;it will serve you well", response.getText());

		// exceeding the size by 1
		Config.getRepInstance().setParameter(RifeConfig.Engine.PARAM_FILEUPLOAD_SIZE_LIMIT, ""+(upload_content.length()-1));
		request = new GetMethodWebRequest("http://localhost:8181/fileupload/simple");
		response = conversation.getResponse(request);
		form = response.getForms()[0];

		// throw no exception when size is exceeded, but don't provide the uploaded file
		Config.getRepInstance().setParameter(RifeConfig.Engine.PARAM_FILEUPLOAD_SIZE_EXCEPTION, false);
		form.setParameter("purpose", "it will serve you well");
		upload_spec = new UploadFileSpec(upload);
		form.setParameter("doc1", new UploadFileSpec[] {upload_spec});
		response = form.submit();
		assertEquals("file 1 size exceeded;no file 2;it will serve you well", response.getText());

		// throw exception when size is exceeded
		Config.getRepInstance().setParameter(RifeConfig.Engine.PARAM_FILEUPLOAD_SIZE_EXCEPTION, true);
		form.setParameter("purpose", "it will serve you well");
		upload_spec = new UploadFileSpec(upload);
		form.setParameter("doc1", new UploadFileSpec[] {upload_spec});

		try
		{
			response = form.submit();
			fail();
		}
		catch (HttpInternalErrorException e)
		{
			assertTrue(getLogSink().getInternalException() instanceof MultipartFileTooBigException);

			MultipartFileTooBigException	e2 = (MultipartFileTooBigException)getLogSink().getInternalException();
			assertEquals("doc1", e2.getFileName());
			assertEquals(upload_content.length()-1, e2.getSizeLimit());
		}

		// disable size limit
		Config.getRepInstance().setParameter(RifeConfig.Engine.PARAM_FILEUPLOAD_SIZE_CHECK, false);
		request = new GetMethodWebRequest("http://localhost:8181/fileupload/simple");
		response = conversation.getResponse(request);
		form = response.getForms()[0];

		form.setParameter("purpose", "it will serve you well");
		upload_spec = new UploadFileSpec(upload);
		form.setParameter("doc1", new UploadFileSpec[] {upload_spec});

		response = form.submit();

		upload.delete();

		Config.getRepInstance().setParameter(RifeConfig.Engine.PARAM_FILEUPLOAD_SIZE_CHECK, "true");
	}

	public void testExitActivation()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new PostMethodWebRequest("http://localhost:8181/exit/source");
		request.setParameter(ReservedParameters.SUBMISSION, "activate_exit");
		request.setParameter("submitted_value", "the value");
		WebResponse response = conversation.getResponse(request);

		assertEquals("exit target : the value", response.getText());
	}

	public void testDifferentSubmissioncontext()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new PostMethodWebRequest("http://localhost:8181/submissions/valid");
		request.setParameter(ReservedParameters.SUBMISSION, "login");
		request.setParameter(ReservedParameters.SUBMISSIONCONTEXT, Base64.encode(".VALID"));
		request.setParameter("login", "gbevin");
		request.setParameter("password", "mypassword");
		request.setParameter("array", new String[] {"1", "3", "4"});
		WebResponse response = conversation.getResponse(request);

		assertEquals("gbevin,mypassword,1|3|4", response.getText());

		request = new PostMethodWebRequest("http://localhost:8181/submissions/valid");
		request.setParameter(ReservedParameters.SUBMISSION, "login");
		request.setParameter(ReservedParameters.SUBMISSIONCONTEXT, Base64.encode(".TYPED^.VALID"));
		request.setParameter("login", "gbevin");
		request.setParameter("password", "mypassword");
		request.setParameter("array", new String[] {"1", "3", "4"});
		response = conversation.getResponse(request);

		assertEquals("null,null,null", response.getText());
	}

	public void testInvalidSubmissioncontext()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new PostMethodWebRequest("http://localhost:8181/submissions/valid");
		request.setParameter(ReservedParameters.SUBMISSION, "login");
		request.setParameter(ReservedParameters.SUBMISSIONCONTEXT, Base64.encode("129:.9990::PPLM^.VALID"));
		request.setParameter("login", "gbevin");
		request.setParameter("password", "mypassword");
		request.setParameter("array", new String[] {"1", "3", "4"});
		WebResponse response = conversation.getResponse(request);

		assertEquals("null,null,null", response.getText());
	}

	public void testDifferentSubmissiontarget()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new PostMethodWebRequest("http://localhost:8181/submissions/valid");
		request.setParameter(ReservedParameters.SUBMISSION, "login");
		request.setParameter(ReservedParameters.SUBMISSIONCONTEXT, Base64.encode(".VALID"));
		request.setParameter("login", "gbevin");
		request.setParameter("password", "mypassword");
		request.setParameter("array", new String[] {"1", "3", "4"});
		WebResponse response = conversation.getResponse(request);

		assertEquals("gbevin,mypassword,1|3|4", response.getText());

		request = new PostMethodWebRequest("http://localhost:8181/submissions/valid");
		request.setParameter(ReservedParameters.SUBMISSION, "login");
		request.setParameter(ReservedParameters.SUBMISSIONCONTEXT, Base64.encode(".VALID^.TYPED"));
		request.setParameter("login", "gbevin");
		request.setParameter("password", "mypassword");
		request.setParameter("array", new String[] {"1", "3", "4"});
		response = conversation.getResponse(request);

		assertEquals("gbevin,mypassword,1|3|4", response.getText());
	}

	public void testInvalidSubmissiontarget()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new PostMethodWebRequest("http://localhost:8181/submissions/valid");
		request.setParameter(ReservedParameters.SUBMISSION, "login");
		request.setParameter(ReservedParameters.SUBMISSIONCONTEXT, Base64.encode(".VALID^OIJH:98//JHSD::8"));
		request.setParameter("login", "gbevin");
		request.setParameter("password", "mypassword");
		request.setParameter("array", new String[] {"1", "3", "4"});
		WebResponse response = conversation.getResponse(request);

		assertEquals("gbevin,mypassword,1|3|4", response.getText());
	}

	public void testNonParamInputsOverlap()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		// trigger a first submission through direct request parameters with
		// inputs, check if it arrived correrctly
		request = new GetMethodWebRequest("http://localhost:8181/submissions/inputsnonparam");
		request.setParameter("input1", "input1value");
		request.setParameter("input4", "input4value");
		request.setParameter(ReservedParameters.SUBMISSION, "submission1");
		request.setParameter(ReservedParameters.SUBMISSIONCONTEXT, Base64.encode(".INPUTSNONPARAM"));
		request.setParameter("parameter1", "parameter1value");
		request.setParameter("parameter2", "parameter2value");
		response = conversation.getResponse(request);
		assertEquals("parameter1value"+
			"parameter2value"+
			"input1value"+
			"null"+
			"input3default"+
			"<a href=\"/submissions/inputsnonparam?submission=submission1&submissioncontext=LklOUFVUU05PTlBBUkFN&inputs=LklOUFVUU05PTlBBUkFNYwBpbnB1dDFuAGlucHV0MXZhbHVl\">thelink</a>", response.getText());
		// extract the link and the inputs parameter to construct a new
		// request which makes it possible to check that overlapped inputs
		// are still preserved
		request = new GetMethodWebRequest("http://localhost:8181/submissions/inputsnonparam");
		request.setParameter(ReservedParameters.SUBMISSION, new String[] {"submission1", "submissionoverlap"});
		request.setParameter(ReservedParameters.SUBMISSIONCONTEXT, new String[] {Base64.encode(".INPUTSNONPARAM"), Base64.encode(".INPUTSOVERLAP")});
		request.setParameter("parameter1", "parameter1value");
		request.setParameter("parameter2", "parameter2value");
		request.setParameter("input1", "input1value");
		request.setParameter("input4", "input4value");
		request.setParameter("inputs", response.getLinkWith("thelink").getParameterValues("inputs"));
		assertEquals("parameter1value"+
			"parameter2value"+
			"input1value"+
			"null"+
			"input3default"+
			"<a href=\"/submissions/inputsnonparam?submission=submission1&submissioncontext=LklOUFVUU05PTlBBUkFN&inputs=LklOUFVUU05PTlBBUkFNYwBpbnB1dDFuAGlucHV0MXZhbHVl\">thelink</a>", response.getText());

		// check that inputs are not obtained from prohibited request parameters
		request = new GetMethodWebRequest("http://localhost:8181/submissions/inputsnonparam");
		request.setParameter(ReservedParameters.SUBMISSION, new String[] {"submission1", "submissionoverlap"});
		request.setParameter(ReservedParameters.SUBMISSIONCONTEXT, new String[] {Base64.encode(".INPUTSNONPARAM"), Base64.encode(".INPUTSOVERLAP")});
		request.setParameter("parameter1", "parameter1value");
		request.setParameter("parameter2", "parameter2value");
		request.setParameter("input1", "input1value");
		request.setParameter("input4", "input4value");
		response = conversation.getResponse(request);
		assertEquals("parameter1value"+
			"parameter2value"+
			"null"+
			"null"+
			"input3default"+
			"<a href=\"/submissions/inputsnonparam?submission=submission1&submissioncontext=LklOUFVUU05PTlBBUkFN\">thelink</a>", response.getText());
		request = new GetMethodWebRequest("http://localhost:8181/submissions/inputsnonparam");
		request.setParameter(ReservedParameters.SUBMISSION, new String[] {"submissionoverlap", "submission1"});
		request.setParameter(ReservedParameters.SUBMISSIONCONTEXT, new String[] {Base64.encode(".INPUTSOVERLAP"), Base64.encode(".INPUTSNONPARAM")});
		request.setParameter("parameter1", "parameter1value");
		request.setParameter("parameter2", "parameter2value");
		request.setParameter("input1", "input1value");
		request.setParameter("input4", "input4value");
		response = conversation.getResponse(request);
		assertEquals("parameter1value"+
			"parameter2value"+
			"null"+
			"null"+
			"input3default"+
			"<a href=\"/submissions/inputsnonparam?submission=submission1&submissioncontext=LklOUFVUU05PTlBBUkFN\">thelink</a>", response.getText());
	}

	public void testParameterOverlap()
	throws Exception
	{
		setupSite("site/submissions.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new PostMethodWebRequest("http://localhost:8181/submissions/valid");
		request.setParameter(ReservedParameters.SUBMISSION, new String[] {"login", "loginoverlap"});
		request.setParameter(ReservedParameters.SUBMISSIONCONTEXT, new String[] {Base64.encode(".VALID"), Base64.encode(".PARAMETERSOVERLAP")});
		request.setParameter("login", "gbevin");
		request.setParameter("password", "mypassword");
		request.setParameter("array", new String[] {"1", "3", "4"});
		WebResponse response = conversation.getResponse(request);

		assertEquals("gbevin,mypassword,1|3|4", response.getText());
	}
}
