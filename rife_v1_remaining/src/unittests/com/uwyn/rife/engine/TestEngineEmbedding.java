/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestEngineEmbedding.java 3930 2008-04-24 11:10:22Z gbevin $
 */
package com.uwyn.rife.engine;

import com.meterware.httpunit.*;
import com.uwyn.rife.TestCaseServerside;
import com.uwyn.rife.config.Config;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.test.MockConversation;
import com.uwyn.rife.test.MockForm;
import com.uwyn.rife.test.MockResponse;
import com.uwyn.rife.test.ParsedHtml;
import com.uwyn.rife.tools.StringUtils;

import java.net.InetAddress;
import java.util.List;

public class TestEngineEmbedding extends TestCaseServerside
{
	public TestEngineEmbedding(int siteType, String name)
	{
		super(siteType, name);
	}
	
	public void testSimple()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

        // Get the host name
		String hostname = InetAddress.getByName("127.0.0.1").getHostName();

		request = new GetMethodWebRequest("http://localhost:8181/simple");
		response = conversation.getResponse(request);
		assertEquals("Embedded: false"+
					 "The element \"Value: null"+
					 "Embedded: true"+
					 "Just some text 127.0.0.1:"+hostname+":\" is being embedded.", response.getText());
	}

	public void testValue()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

        // Get the host name
		String hostname = InetAddress.getByName("127.0.0.1").getHostName();

		request = new GetMethodWebRequest("http://localhost:8181/value");
		response = conversation.getResponse(request);
		assertEquals("The element \""+
					 "Value: this is the value"+
					 "Embedded: true"+
					 "Just some text 127.0.0.1:"+hostname+":"+
					 "\" is being embedded.\n"+
					 "Value: this is the value"+
					 "Embedded: true"+
					 "Just some text 127.0.0.1:"+hostname+":"+
					 "\n"+
					 "Value: this is another value"+
					 "Embedded: true"+
					 "Just some text 127.0.0.1:"+hostname+":"+
					 "\n"+
					 "Value: this is the third value"+
					 "Embedded: true"+
					 "Just some text 127.0.0.1:"+hostname+":"+
					 "\n", response.getText());
	}

	public void testData()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/data");
		response = conversation.getResponse(request);
		assertEquals("The element \"Data: value1\" is being embedded.\n"+
					 "Data: value1\n"+
					 "Data: value2\n"+
					 "Data: value3\n", response.getText());
	}

	public void testProperties()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/properties");
		response = conversation.getResponse(request);
		assertEquals("The element \"Properties"+
					 "yes"+
					 "no"+
					 "else"+
					 "yeah"+
					 "null"+
					 "\" is being embedded.\n", response.getText());
	}

	public void testClearTemplate()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

        // Get the host name
		String hostname = InetAddress.getByName("127.0.0.1").getHostName();

		request = new GetMethodWebRequest("http://localhost:8181/clear_template");
		response = conversation.getResponse(request);
		assertEquals("The element \"Value: nullEmbedded: trueJust some text 127.0.0.1:"+hostname+":\" is being embedded."+
					 "The element \"Value: nullEmbedded: trueJust some text 127.0.0.1:"+hostname+":\" is being embedded.", response.getText());
	}

	public void testSubmission()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm		form = null;

		request = new GetMethodWebRequest("http://localhost:8181/submission");
		response = conversation.getResponse(request);
		assertEquals("The element \"<form method=\"post\" action=\"/submission/embedded\" name=\"embedded\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n\" is being embedded.", response.getText());
		form = response.getForms()[0];
		form.setParameter("login", "the login");
		form.setParameter("password", "its password");
		response = form.submit();
		assertEquals("the login,its password", response.getText());
	}

	public void testList()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm		form = null;

		String hostname = InetAddress.getByName("127.0.0.1").getHostName();

		request = new GetMethodWebRequest("http://localhost:8181/list");
		response = conversation.getResponse(request);
		assertEquals("Value: null"+"Embedded: true"+"Just some text 127.0.0.1:"+hostname+":\n"+
			"<div>0\n"+
			"<form method=\"post\" action=\"/list\" name=\"form0\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"changeEntry\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".LIST::engine_embedding_list:.LISTENTRY_EMBEDDED:0^.LISTENTRY_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"value\">\n"+
			"<input type=\"submit\" />\n"+
			"</form>\n"+
			"</div>\n"+
			"<div>1\n"+
			"<form method=\"post\" action=\"/list\" name=\"form1\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"changeEntry\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".LIST::engine_embedding_list:.LISTENTRY_EMBEDDED:1^.LISTENTRY_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"value\">\n"+
			"<input type=\"submit\" />\n"+
			"</form>\n"+
			"</div>\n"+
			"<div>2\n"+
			"<form method=\"post\" action=\"/list\" name=\"form2\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"changeEntry\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".LIST::engine_embedding_list:.LISTENTRY_EMBEDDED:2^.LISTENTRY_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"value\">\n"+
			"<input type=\"submit\" />\n"+
			"</form>\n"+
			"</div>\n"+
			"<div>3\n"+
			"<form method=\"post\" action=\"/list\" name=\"form3\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"changeEntry\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".LIST::engine_embedding_list:.LISTENTRY_EMBEDDED:3^.LISTENTRY_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"value\">\n"+
			"<input type=\"submit\" />\n"+
			"</form>\n"+
			"</div>\n"+
			"<div>4\n"+
			"<form method=\"post\" action=\"/list\" name=\"form4\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"changeEntry\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".LIST::engine_embedding_list:.LISTENTRY_EMBEDDED:4^.LISTENTRY_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"value\">\n"+
			"<input type=\"submit\" />\n"+
			"</form>\n"+
			"</div>\n"+
			"<div>5\n"+
			"<form method=\"post\" action=\"/list\" name=\"form5\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"changeEntry\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".LIST::engine_embedding_list:.LISTENTRY_EMBEDDED:5^.LISTENTRY_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"value\">\n"+
			"<input type=\"submit\" />\n"+
			"</form>\n"+
			"</div>\n"+
			"<div>6\n"+
			"<form method=\"post\" action=\"/list\" name=\"form6\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"changeEntry\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".LIST::engine_embedding_list:.LISTENTRY_EMBEDDED:6^.LISTENTRY_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"value\">\n"+
			"<input type=\"submit\" />\n"+
			"</form>\n"+
			"</div>\n"+
			"<div>7\n"+
			"<form method=\"post\" action=\"/list\" name=\"form7\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"changeEntry\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".LIST::engine_embedding_list:.LISTENTRY_EMBEDDED:7^.LISTENTRY_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"value\">\n"+
			"<input type=\"submit\" />\n"+
			"</form>\n"+
			"</div>\n"+
			"<div>8\n"+
			"<form method=\"post\" action=\"/list\" name=\"form8\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"changeEntry\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".LIST::engine_embedding_list:.LISTENTRY_EMBEDDED:8^.LISTENTRY_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"value\">\n"+
			"<input type=\"submit\" />\n"+
			"</form>\n"+
			"</div>\n"+
			"<div>9\n"+
			"<form method=\"post\" action=\"/list\" name=\"form9\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"changeEntry\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".LIST::engine_embedding_list:.LISTENTRY_EMBEDDED:9^.LISTENTRY_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"value\">\n"+
			"<input type=\"submit\" />\n"+
			"</form>\n"+
			"</div>\n"+
			"\n"+
			"\n", response.getText());

		form = response.getFormWithName("form3");
		form.setParameter("value", "formvalue3");
		response = form.submit();
		assertEquals("Value: null"+"Embedded: true"+"Just some text 127.0.0.1:"+hostname+":\n"+
			"<div>0\n"+
			"<form method=\"post\" action=\"/list\" name=\"form0\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"changeEntry\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".LIST::engine_embedding_list:.LISTENTRY_EMBEDDED:0^.LISTENTRY_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"value\">\n"+
			"<input type=\"submit\" />\n"+
			"</form>\n"+
			"</div>\n"+
			"<div>1\n"+
			"<form method=\"post\" action=\"/list\" name=\"form1\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"changeEntry\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".LIST::engine_embedding_list:.LISTENTRY_EMBEDDED:1^.LISTENTRY_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"value\">\n"+
			"<input type=\"submit\" />\n"+
			"</form>\n"+
			"</div>\n"+
			"<div>2\n"+
			"<form method=\"post\" action=\"/list\" name=\"form2\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"changeEntry\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".LIST::engine_embedding_list:.LISTENTRY_EMBEDDED:2^.LISTENTRY_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"value\">\n"+
			"<input type=\"submit\" />\n"+
			"</form>\n"+
			"</div>\n"+
			"<div>3\n"+
			"formvalue3</div>\n"+
			"<div>4\n"+
			"<form method=\"post\" action=\"/list\" name=\"form4\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"changeEntry\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".LIST::engine_embedding_list:.LISTENTRY_EMBEDDED:4^.LISTENTRY_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"value\">\n"+
			"<input type=\"submit\" />\n"+
			"</form>\n"+
			"</div>\n"+
			"<div>5\n"+
			"<form method=\"post\" action=\"/list\" name=\"form5\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"changeEntry\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".LIST::engine_embedding_list:.LISTENTRY_EMBEDDED:5^.LISTENTRY_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"value\">\n"+
			"<input type=\"submit\" />\n"+
			"</form>\n"+
			"</div>\n"+
			"<div>6\n"+
			"<form method=\"post\" action=\"/list\" name=\"form6\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"changeEntry\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".LIST::engine_embedding_list:.LISTENTRY_EMBEDDED:6^.LISTENTRY_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"value\">\n"+
			"<input type=\"submit\" />\n"+
			"</form>\n"+
			"</div>\n"+
			"<div>7\n"+
			"<form method=\"post\" action=\"/list\" name=\"form7\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"changeEntry\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".LIST::engine_embedding_list:.LISTENTRY_EMBEDDED:7^.LISTENTRY_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"value\">\n"+
			"<input type=\"submit\" />\n"+
			"</form>\n"+
			"</div>\n"+
			"<div>8\n"+
			"<form method=\"post\" action=\"/list\" name=\"form8\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"changeEntry\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".LIST::engine_embedding_list:.LISTENTRY_EMBEDDED:8^.LISTENTRY_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"value\">\n"+
			"<input type=\"submit\" />\n"+
			"</form>\n"+
			"</div>\n"+
			"<div>9\n"+
			"<form method=\"post\" action=\"/list\" name=\"form9\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"changeEntry\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".LIST::engine_embedding_list:.LISTENTRY_EMBEDDED:9^.LISTENTRY_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"value\">\n"+
			"<input type=\"submit\" />\n"+
			"</form>\n"+
			"</div>\n"+
			"\n"+
			"\n", response.getText());

		form = response.getFormWithName("form7");
		form.setParameter("value", "formvalue7");
		response = form.submit();
		assertEquals("Value: null"+"Embedded: true"+"Just some text 127.0.0.1:"+hostname+":\n"+
			"<div>0\n"+
			"<form method=\"post\" action=\"/list\" name=\"form0\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"changeEntry\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".LIST::engine_embedding_list:.LISTENTRY_EMBEDDED:0^.LISTENTRY_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"value\">\n"+
			"<input type=\"submit\" />\n"+
			"</form>\n"+
			"</div>\n"+
			"<div>1\n"+
			"<form method=\"post\" action=\"/list\" name=\"form1\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"changeEntry\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".LIST::engine_embedding_list:.LISTENTRY_EMBEDDED:1^.LISTENTRY_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"value\">\n"+
			"<input type=\"submit\" />\n"+
			"</form>\n"+
			"</div>\n"+
			"<div>2\n"+
			"<form method=\"post\" action=\"/list\" name=\"form2\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"changeEntry\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".LIST::engine_embedding_list:.LISTENTRY_EMBEDDED:2^.LISTENTRY_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"value\">\n"+
			"<input type=\"submit\" />\n"+
			"</form>\n"+
			"</div>\n"+
			"<div>3\n"+
			"<form method=\"post\" action=\"/list\" name=\"form3\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"changeEntry\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".LIST::engine_embedding_list:.LISTENTRY_EMBEDDED:3^.LISTENTRY_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"value\">\n"+
			"<input type=\"submit\" />\n"+
			"</form>\n"+
			"</div>\n"+
			"<div>4\n"+
			"<form method=\"post\" action=\"/list\" name=\"form4\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"changeEntry\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".LIST::engine_embedding_list:.LISTENTRY_EMBEDDED:4^.LISTENTRY_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"value\">\n"+
			"<input type=\"submit\" />\n"+
			"</form>\n"+
			"</div>\n"+
			"<div>5\n"+
			"<form method=\"post\" action=\"/list\" name=\"form5\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"changeEntry\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".LIST::engine_embedding_list:.LISTENTRY_EMBEDDED:5^.LISTENTRY_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"value\">\n"+
			"<input type=\"submit\" />\n"+
			"</form>\n"+
			"</div>\n"+
			"<div>6\n"+
			"<form method=\"post\" action=\"/list\" name=\"form6\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"changeEntry\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".LIST::engine_embedding_list:.LISTENTRY_EMBEDDED:6^.LISTENTRY_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"value\">\n"+
			"<input type=\"submit\" />\n"+
			"</form>\n"+
			"</div>\n"+
			"<div>7\n"+
			"formvalue7</div>\n"+
			"<div>8\n"+
			"<form method=\"post\" action=\"/list\" name=\"form8\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"changeEntry\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".LIST::engine_embedding_list:.LISTENTRY_EMBEDDED:8^.LISTENTRY_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"value\">\n"+
			"<input type=\"submit\" />\n"+
			"</form>\n"+
			"</div>\n"+
			"<div>9\n"+
			"<form method=\"post\" action=\"/list\" name=\"form9\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"changeEntry\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".LIST::engine_embedding_list:.LISTENTRY_EMBEDDED:9^.LISTENTRY_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"value\">\n"+
			"<input type=\"submit\" />\n"+
			"</form>\n"+
			"</div>\n"+
			"\n"+
			"\n", response.getText());
	}

	public void testSubmissionExit()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm		form = null;

		request = new GetMethodWebRequest("http://localhost:8181/submission_exit");
		response = conversation.getResponse(request);
		assertEquals("The element \"<form method=\"post\" action=\"/submission_exit/embedded\" name=\"embedded\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_EXIT_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n\" is being embedded.\n", response.getText());
		form = response.getForms()[0];
		form.setParameter("login", "the login");
		form.setParameter("password", "its password");
		response = form.submit();
		assertEquals("The element \"<form method=\"post\" action=\"/submission_exit/embedded\" name=\"embedded\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_EXIT_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n\" is being embedded here too.\n", response.getText());
	}

	public void testSubmissionExitNourl()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm		form = null;

		request = new GetMethodWebRequest("http://localhost:8181/submission_exit_nourl");
		response = conversation.getResponse(request);
		assertEquals("The element nourl \"<form method=\"post\" action=\"/submission_exit_nourl\" name=\"embedded\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_EXIT_NOURL::engine_embedding_submission_exit_nourl:.SUBMISSION_EXIT_EMBEDDED_NOURL^.SUBMISSION_EXIT_EMBEDDED_NOURL")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n\" is being embedded.\n", response.getText());
		form = response.getForms()[0];
		form.setParameter("login", "the login");
		form.setParameter("password", "its password");
		response = form.submit();
		assertEquals("The element nourl \"The element \"<form method=\"post\" action=\"/submission_exit/embedded\" name=\"embedded\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_EXIT_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n\" is being embedded here too.\n\" is being embedded.\n", response.getText());
	}

	public void testSubmissionExitCancelEmbedding()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm		form = null;

		request = new GetMethodWebRequest("http://localhost:8181/submission_exit_cancel");
		response = conversation.getResponse(request);
		assertEquals("The element cancel \"<form method=\"post\" action=\"/submission_exit_cancel\" name=\"embedded\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_EXIT_CANCEL::engine_embedding_submission_exit_cancel:.SUBMISSION_EXIT_EMBEDDED_CANCEL^.SUBMISSION_EXIT_EMBEDDED_CANCEL")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n\" is being embedded.\n", response.getText());
		form = response.getForms()[0];
		form.setParameter("login", "the login");
		form.setParameter("password", "its password");
		response = form.submit();
		assertEquals("The element \"<form method=\"post\" action=\"/submission_exit/embedded\" name=\"embedded\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_EXIT_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n\" is being embedded here too.\n", response.getText());
	}

	public void testSubmissionNourl()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm		form = null;

		request = new GetMethodWebRequest("http://localhost:8181/submission_nourl");
		response = conversation.getResponse(request);
		assertEquals("The element \"<form method=\"post\" action=\"/submission_nourl\" name=\"embedded\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_NOURL::engine_embedding_submission_nourl:.SUBMISSION_NOURL_EMBEDDED^.SUBMISSION_NOURL_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n\" is being embedded.\n", response.getText());
		form = response.getForms()[0];
		form.setParameter("login", "the login");
		form.setParameter("password", "its password");
		response = form.submit();
		assertEquals("The element \"the login,its password\" is being embedded.\n", response.getText());
	}

	public void testSubmissionNourlPathinfo()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm		form = null;

		request = new GetMethodWebRequest("http://localhost:8181/submission_nourl_pathinfo");
		response = conversation.getResponse(request);
		assertEquals("The element \"<form method=\"post\" action=\"/submission_nourl_pathinfo/\" name=\"embedded\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_NOURL_PATHINFO::engine_embedding_submission_nourl_pathinfo:.SUBMISSION_NOURL_PATHINFO_EMBEDDED^.SUBMISSION_NOURL_PATHINFO_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n\" is being embedded.\n", response.getText());
		form = response.getForms()[0];
		form.setParameter("login", "the login");
		form.setParameter("password", "its password");
		response = form.submit();
		assertEquals("The element \"the login,its password\" is being embedded.\n", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/submission_nourl_pathinfo/somepathinfo");
		response = conversation.getResponse(request);
		assertEquals("The element \"<form method=\"post\" action=\"/submission_nourl_pathinfo/somepathinfo\" name=\"embedded\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_NOURL_PATHINFO::engine_embedding_submission_nourl_pathinfo:.SUBMISSION_NOURL_PATHINFO_EMBEDDED^.SUBMISSION_NOURL_PATHINFO_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n\" is being embedded.\n", response.getText());
		form = response.getForms()[0];
		form.setParameter("login", "the login");
		form.setParameter("password", "its password");
		response = form.submit();
		assertEquals("The element \"the login,its password\" is being embedded.\n", response.getText());
	}

	public void testSubmissionSame()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm		form = null;

		request = new GetMethodWebRequest("http://localhost:8181/submission_same");
		request.setParameter("language", "dutch");
		response = conversation.getResponse(request);
		assertEquals("The element \"<form method=\"post\" action=\"/submission_same/embedded\" name=\"embedded\">\n" +
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_SAME_EMBEDDED")+"\" /><input name=\"inputs\" type=\"hidden\" value=\"bGFuZ3VhZ2VuAGR1dGNo\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"<input type=\"text\" name=\"language\">\n"+
			"</form>\n\" is being embedded.\n"+
			"<form method=\"post\" action=\"/submission_same\" name=\"embedding\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_SAME")+"\" /><input name=\"inputs\" type=\"hidden\" value=\"LlNVQk1JU1NJT05fU0FNRWMAbGFuZ3VhZ2VuAGR1dGNo\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>,dutch\n", response.getText());

		form = response.getFormWithName("embedded");
		form.setParameter("login", "the login");
		form.setParameter("password", "its password");
		form.setParameter("language", "english");
		assertEquals("the login,its password,english", form.submit().getText());

		form = response.getFormWithName("embedding");
		form.setParameter("login", "the login");
		form.setParameter("password", "its password");
		response = form.submit();
		assertEquals("The element \"<form method=\"post\" action=\"/submission_same/embedded\" name=\"embedded\">\n"+
					 "<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_SAME_EMBEDDED")+"\" /><input name=\"inputs\" type=\"hidden\" value=\"bGFuZ3VhZ2VuAGR1dGNo\" /><input name=\"ctxt\" type=\"hidden\" value=\"LlNVQk1JU1NJT05fU0FNRWMAbGFuZ3VhZ2VuAGR1dGNo\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"<input type=\"text\" name=\"language\">\n"+
			"</form>\n\" is being embedded.\n"+
			"the login,its password,dutch\n", form.submit().getText());
	}

	public void testSubmissionSameNourl()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm		form = null;

		request = new GetMethodWebRequest("http://localhost:8181/submission_same_nourl");
		request.setParameter("language", "dutch");
		response = conversation.getResponse(request);
		assertEquals("The element \"<form method=\"post\" action=\"/submission_same_nourl\" name=\"embedded\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_SAME_NOURL::engine_embedding_submission_same_nourl:.SUBMISSION_SAME_NOURL_EMBEDDED^.SUBMISSION_SAME_NOURL_EMBEDDED")+"\" /><input name=\"inputs\" type=\"hidden\" value=\"bGFuZ3VhZ2VuAGR1dGNo\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"<input type=\"text\" name=\"language\">\n"+
			"</form>\n\" is being embedded.\n"+
			"<form method=\"post\" action=\"/submission_same_nourl\" name=\"embedding\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_SAME_NOURL")+"\" /><input name=\"inputs\" type=\"hidden\" value=\"LlNVQk1JU1NJT05fU0FNRV9OT1VSTGMAbGFuZ3VhZ2VuAGR1dGNo\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>,dutch\n", response.getText());

		form = response.getFormWithName("embedded");
		form.setParameter("login", "the login");
		form.setParameter("password", "its password");
		form.setParameter("language", "english");
		assertEquals("The element \"the login,its password,english\" is being embedded.\n"+
			"<form method=\"post\" action=\"/submission_same_nourl\" name=\"embedding\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_SAME_NOURL")+"\" /><input name=\"inputs\" type=\"hidden\" value=\"LlNVQk1JU1NJT05fU0FNRV9OT1VSTGMAbGFuZ3VhZ2VuAGR1dGNo\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>,dutch\n", form.submit().getText());

		form = response.getFormWithName("embedding");
		form.setParameter("login", "the login");
		form.setParameter("password", "its password");
		response = form.submit();
		assertEquals("The element \"<form method=\"post\" action=\"/submission_same_nourl\" name=\"embedded\">\n"+
					 "<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_SAME_NOURL::engine_embedding_submission_same_nourl:.SUBMISSION_SAME_NOURL_EMBEDDED^.SUBMISSION_SAME_NOURL_EMBEDDED")+"\" /><input name=\"inputs\" type=\"hidden\" value=\"bGFuZ3VhZ2VuAGR1dGNo\" /><input name=\"ctxt\" type=\"hidden\" value=\"LlNVQk1JU1NJT05fU0FNRV9OT1VSTGMAbGFuZ3VhZ2VuAGR1dGNo\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"<input type=\"text\" name=\"language\">\n"+
			"</form>\n\" is being embedded.\n"+
			"the login,its password,dutch\n", form.submit().getText());
	}

	public void testSubmissionGlobalSameNourl()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm		form = null;

		request = new GetMethodWebRequest("http://localhost:8181/submission_global_same_nourl");
		request.setParameter("language", "dutch");
		response = conversation.getResponse(request);
		assertEquals("The element \"<form method=\"post\" action=\"/submission_global_same_nourl\" name=\"embedded\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"inputs\" type=\"hidden\" value=\"bGFuZ3VhZ2VuAGR1dGNo\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"<input type=\"text\" name=\"language\">\n"+
			"</form>\n\" is being embedded.\n"+
			"<form method=\"post\" action=\"/submission_global_same_nourl\" name=\"embedding\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"inputs\" type=\"hidden\" value=\"LlNVQk1JU1NJT05fR0xPQkFMX1NBTUVfTk9VUkxjAGxhbmd1YWdlbgBkdXRjaA==\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>,dutch\n", response.getText());

		form = response.getFormWithName("embedded");
		form.setParameter("login", "the login");
		form.setParameter("password", "its password");
		form.setParameter("language", "english");
		assertEquals("The element \"the login,its password,english\" is being embedded.\n"+
			"the login,its password,dutch\n", form.submit().getText());
	}

	public void testSubmissionMultiple()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm		form = null;

		request = new GetMethodWebRequest("http://localhost:8181/submission_multiple");
		response = conversation.getResponse(request);
		assertEquals("Element1 \"<form method=\"post\" action=\"/submission_multiple/embedded\" name=\"embedded0\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_MULTIPLE_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n"+
			"\" being embedded.\n"+
			"Element2 \"<form method=\"post\" action=\"/submission_multiple/embedded\" name=\"embedded1\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_MULTIPLE_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n"+
			"\" being embedded.\n"+
			"Element3 \"<form method=\"post\" action=\"/submission_multiple/embedded\" name=\"embedded2\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_MULTIPLE_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n"+
			"\" being embedded.\n"+
			"Element4 \"<form method=\"post\" action=\"/submission_multiple/embedded\" name=\"embedded3\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_MULTIPLE_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n"+
			"\" being embedded.\n", response.getText());

		form = response.getForms()[0];
		form.setParameter("login", "the login 0");
		form.setParameter("password", "its password 0");
		assertEquals("the login 0,its password 0,", form.submit().getText());

		form = response.getForms()[1];
		form.setParameter("login", "the login 1");
		form.setParameter("password", "its password 1");
		assertEquals("the login 1,its password 1,", form.submit().getText());

		form = response.getForms()[2];
		form.setParameter("login", "the login 2");
		form.setParameter("password", "its password 2");
		assertEquals("the login 2,its password 2,", form.submit().getText());

		form = response.getForms()[3];
		form.setParameter("login", "the login 3");
		form.setParameter("password", "its password 3");
		assertEquals("the login 3,its password 3,", form.submit().getText());
	}

	public void testSubmissionMultipleNourl()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm		form = null;

		request = new GetMethodWebRequest("http://localhost:8181/submission_multiple_nourl");
		response = conversation.getResponse(request);
		assertEquals("Element1 \"<form method=\"post\" action=\"/submission_multiple_nourl\" name=\"embedded0\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_MULTIPLE_NOURL::engine_embedding_submission_multiple_nourl:.SUBMISSION_MULTIPLE_NOURL_EMBEDDED^.SUBMISSION_MULTIPLE_NOURL_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n"+
			"\" being embedded.\n"+
			"Element2 \"<form method=\"post\" action=\"/submission_multiple_nourl\" name=\"embedded1\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_MULTIPLE_NOURL::engine_embedding_submission_multiple_nourl:.SUBMISSION_MULTIPLE_NOURL_EMBEDDED:nr1^.SUBMISSION_MULTIPLE_NOURL_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n"+
			"\" being embedded.\n"+
			"Element3 \"<form method=\"post\" action=\"/submission_multiple_nourl\" name=\"embedded2\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_MULTIPLE_NOURL::engine_embedding_submission_multiple_nourl:.SUBMISSION_MULTIPLE_NOURL_EMBEDDED:nr2^.SUBMISSION_MULTIPLE_NOURL_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n"+
			"\" being embedded.\n"+
			"Element4 \"<form method=\"post\" action=\"/submission_multiple_nourl\" name=\"embedded3\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_MULTIPLE_NOURL::engine_embedding_submission_multiple_nourl:.SUBMISSION_MULTIPLE_NOURL_EMBEDDED:nr3^.SUBMISSION_MULTIPLE_NOURL_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n"+
			"\" being embedded.\n", response.getText());

		form = response.getForms()[0];
		form.setParameter("login", "the login 0");
		form.setParameter("password", "its password 0");
		assertEquals("Element1 \"the login 0,its password 0,0\" being embedded.\n"+
			"Element2 \"<form method=\"post\" action=\"/submission_multiple_nourl\" name=\"embedded1\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_MULTIPLE_NOURL::engine_embedding_submission_multiple_nourl:.SUBMISSION_MULTIPLE_NOURL_EMBEDDED:nr1^.SUBMISSION_MULTIPLE_NOURL_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n"+
			"\" being embedded.\n"+
			"Element3 \"<form method=\"post\" action=\"/submission_multiple_nourl\" name=\"embedded2\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_MULTIPLE_NOURL::engine_embedding_submission_multiple_nourl:.SUBMISSION_MULTIPLE_NOURL_EMBEDDED:nr2^.SUBMISSION_MULTIPLE_NOURL_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n"+
			"\" being embedded.\n"+
			"Element4 \"<form method=\"post\" action=\"/submission_multiple_nourl\" name=\"embedded3\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_MULTIPLE_NOURL::engine_embedding_submission_multiple_nourl:.SUBMISSION_MULTIPLE_NOURL_EMBEDDED:nr3^.SUBMISSION_MULTIPLE_NOURL_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n"+
			"\" being embedded.\n", form.submit().getText());

		form = response.getForms()[1];
		form.setParameter("login", "the login 1");
		form.setParameter("password", "its password 1");
		assertEquals("Element1 \"<form method=\"post\" action=\"/submission_multiple_nourl\" name=\"embedded0\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_MULTIPLE_NOURL::engine_embedding_submission_multiple_nourl:.SUBMISSION_MULTIPLE_NOURL_EMBEDDED^.SUBMISSION_MULTIPLE_NOURL_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n"+
			"\" being embedded.\n"+
			"Element2 \"the login 1,its password 1,1\" being embedded.\n"+
			"Element3 \"<form method=\"post\" action=\"/submission_multiple_nourl\" name=\"embedded2\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_MULTIPLE_NOURL::engine_embedding_submission_multiple_nourl:.SUBMISSION_MULTIPLE_NOURL_EMBEDDED:nr2^.SUBMISSION_MULTIPLE_NOURL_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n"+
			"\" being embedded.\n"+
			"Element4 \"<form method=\"post\" action=\"/submission_multiple_nourl\" name=\"embedded3\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_MULTIPLE_NOURL::engine_embedding_submission_multiple_nourl:.SUBMISSION_MULTIPLE_NOURL_EMBEDDED:nr3^.SUBMISSION_MULTIPLE_NOURL_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n"+
			"\" being embedded.\n", form.submit().getText());

		form = response.getForms()[2];
		form.setParameter("login", "the login 2");
		form.setParameter("password", "its password 2");
		assertEquals("Element1 \"<form method=\"post\" action=\"/submission_multiple_nourl\" name=\"embedded0\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_MULTIPLE_NOURL::engine_embedding_submission_multiple_nourl:.SUBMISSION_MULTIPLE_NOURL_EMBEDDED^.SUBMISSION_MULTIPLE_NOURL_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n"+
			"\" being embedded.\n"+
			"Element2 \"<form method=\"post\" action=\"/submission_multiple_nourl\" name=\"embedded1\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_MULTIPLE_NOURL::engine_embedding_submission_multiple_nourl:.SUBMISSION_MULTIPLE_NOURL_EMBEDDED:nr1^.SUBMISSION_MULTIPLE_NOURL_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n"+
			"\" being embedded.\n"+
			"Element3 \"the login 2,its password 2,2\" being embedded.\n"+
			"Element4 \"<form method=\"post\" action=\"/submission_multiple_nourl\" name=\"embedded3\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_MULTIPLE_NOURL::engine_embedding_submission_multiple_nourl:.SUBMISSION_MULTIPLE_NOURL_EMBEDDED:nr3^.SUBMISSION_MULTIPLE_NOURL_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n"+
			"\" being embedded.\n", form.submit().getText());

		form = response.getForms()[3];
		form.setParameter("login", "the login 3");
		form.setParameter("password", "its password 3");
		assertEquals("Element1 \"<form method=\"post\" action=\"/submission_multiple_nourl\" name=\"embedded0\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_MULTIPLE_NOURL::engine_embedding_submission_multiple_nourl:.SUBMISSION_MULTIPLE_NOURL_EMBEDDED^.SUBMISSION_MULTIPLE_NOURL_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n"+
			"\" being embedded.\n"+
			"Element2 \"<form method=\"post\" action=\"/submission_multiple_nourl\" name=\"embedded1\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_MULTIPLE_NOURL::engine_embedding_submission_multiple_nourl:.SUBMISSION_MULTIPLE_NOURL_EMBEDDED:nr1^.SUBMISSION_MULTIPLE_NOURL_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n"+
			"\" being embedded.\n"+
			"Element3 \"<form method=\"post\" action=\"/submission_multiple_nourl\" name=\"embedded2\">\n"+
			"<input name=\"submission\" type=\"hidden\" value=\"credentials\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SUBMISSION_MULTIPLE_NOURL::engine_embedding_submission_multiple_nourl:.SUBMISSION_MULTIPLE_NOURL_EMBEDDED:nr2^.SUBMISSION_MULTIPLE_NOURL_EMBEDDED")+"\" />\n"+
			"<input type=\"text\" name=\"login\">\n"+
			"<input type=\"text\" name=\"password\">\n"+
			"</form>\n"+
			"\" being embedded.\n"+
			"Element4 \"the login 3,its password 3,3\" being embedded.\n", form.submit().getText());
	}

	public void testGlobalvar()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm		form = null;

		request = new GetMethodWebRequest("http://localhost:8181/globalvar");
		response = conversation.getResponse(request);
		assertEquals("value 1,"+
			"value 2,"+
			"value 3,"+
			"value 4,"+
			"<!--V 'OUTPUT:var5'/-->,"+
			"<form action=\"/globalvar\"><input name=\"submission\" type=\"hidden\" value=\"submission\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".GLOBALVAR::engine_embedding_globalvar:.GLOBALVAR_EMBEDDED^.GLOBALVAR_EMBEDDED")+"\" /><input type=\"submit\" /></form>\n", response.getText());
		form = response.getForms()[0];
		response = form.submit();
		assertEquals("embedded value 1,"+
			"<!--V 'OUTPUT:var2'/-->,"+
			"value 3,"+
			"value 4,"+
			"<!--V 'OUTPUT:var5'/-->,submitted\n", response.getText());
	}

	public void testEmbedderInputs()
	throws Exception
	{
		SiteBuilder builder = new SiteBuilder("site/embedding.xml", ResourceFinderClasspath.getInstance());
		MockConversation conversation = new MockConversation(builder.getSite());
		MockForm form;
		ParsedHtml parsed;
		MockResponse response;

		response = conversation.doRequest("/embedder_inputs?var1=value%201&var2=value%202&var3=value%203&var4=value%204");
		assertEquals("value 1," +
					 "value 2," +
					 "value 3," +
					 "value 4," +
					 "<!--V 'INPUT:var5'/-->," +
					 "<!--V 'INPUT:var6'/-->," +
					 "<form action=\"/embedder_inputs/\"><input name=\"submission\" type=\"hidden\" value=\"submission\" /><input name=\"submissioncontext\" type=\"hidden\" value=\"" + Base64.encode(".EMBEDDER_INPUTS::engine_embedding_embedderinputs:.EMBEDDER_INPUTS_EMBEDDED^.EMBEDDER_INPUTS_EMBEDDED") + "\" /><input name=\"inputs\" type=\"hidden\" value=\"dmFyMW4AdmFsdWUgMXAAdmFyMm4AdmFsdWUgMnAAdmFyM24AdmFsdWUgM3AAdmFyNG4AdmFsdWUgNA==_LkVNQkVEREVSX0lOUFVUUzo6ZW5naW5lX2VtYmVkZGluZ19lbWJlZGRlcmlucHV0czouRU1CRURERVJfSU5QVVRTX0VNQkVEREVEYwB2YXIzbgB2YWx1ZSAzcAB2YXI0bgB2YWx1ZSA0\" /><input type=\"submit\" /></form>\n", response.getText());
		parsed = response.getParsedHtml();
		form = parsed.getForms().get(0);
		response = form.submit();
		assertEquals("value 1," +
					 "value 2," +
					 "value 3," +
					 "value 4," +
					 "<!--V 'INPUT:var5'/-->," +
					 "<!--V 'INPUT:var6'/-->," +
					 "embedded no var6," +
					 "submitted\n", response.getText());

		response = conversation.doRequest("/embedder_inputs/test/value5/value6?var1=value%201&var2=value%202&var3=value%203&var4=value%204");
		assertEquals("value 1," +
					 "value 2," +
					 "value 3," +
					 "value 4," +
					 "value5," +
					 "value6," +
					 "<form action=\"/embedder_inputs/test/value5/value6\"><input name=\"submission\" type=\"hidden\" value=\"submission\" /><input name=\"submissioncontext\" type=\"hidden\" value=\"" + Base64.encode(".EMBEDDER_INPUTS::engine_embedding_embedderinputs:.EMBEDDER_INPUTS_EMBEDDED^.EMBEDDER_INPUTS_EMBEDDED") + "\" /><input name=\"inputs\" type=\"hidden\" value=\"dmFyMW4AdmFsdWUgMXAAdmFyMm4AdmFsdWUgMnAAdmFyM24AdmFsdWUgM3AAdmFyNW4AdmFsdWU1cAB2YXI2bgB2YWx1ZTZwAHZhcjRuAHZhbHVlIDQ=_LkVNQkVEREVSX0lOUFVUUzo6ZW5naW5lX2VtYmVkZGluZ19lbWJlZGRlcmlucHV0czouRU1CRURERVJfSU5QVVRTX0VNQkVEREVEYwB2YXIzbgB2YWx1ZSAzcAB2YXI0bgB2YWx1ZSA0cAB2YXI1bgB2YWx1ZTU=\" /><input type=\"submit\" /></form>\n", response.getText());
		parsed = response.getParsedHtml();
		form = parsed.getForms().get(0);
		response = form.submit();
		assertEquals("value 1," +
					 "value 2," +
					 "value 3," +
					 "value 4," +
					 "value5," +
					 "value6," +
					 "embedded value6," +
					 "submitted\n", response.getText());

		response = conversation.doRequest("/embedder_inputs/test/value5/value6?var1=value%201&var2=value%202&var3=value%203&var4=value%204");
		assertEquals("value 1," +
					 "value 2," +
					 "value 3," +
					 "value 4," +
					 "value5," +
					 "value6," +
					 "<form action=\"/embedder_inputs/test/value5/value6\"><input name=\"submission\" type=\"hidden\" value=\"submission\" /><input name=\"submissioncontext\" type=\"hidden\" value=\"" + Base64.encode(".EMBEDDER_INPUTS::engine_embedding_embedderinputs:.EMBEDDER_INPUTS_EMBEDDED^.EMBEDDER_INPUTS_EMBEDDED") + "\" /><input name=\"inputs\" type=\"hidden\" value=\"dmFyMW4AdmFsdWUgMXAAdmFyMm4AdmFsdWUgMnAAdmFyM24AdmFsdWUgM3AAdmFyNW4AdmFsdWU1cAB2YXI2bgB2YWx1ZTZwAHZhcjRuAHZhbHVlIDQ=_LkVNQkVEREVSX0lOUFVUUzo6ZW5naW5lX2VtYmVkZGluZ19lbWJlZGRlcmlucHV0czouRU1CRURERVJfSU5QVVRTX0VNQkVEREVEYwB2YXIzbgB2YWx1ZSAzcAB2YXI0bgB2YWx1ZSA0cAB2YXI1bgB2YWx1ZTU=\" /><input type=\"submit\" /></form>\n", response.getText());
		parsed = response.getParsedHtml();
		form = parsed.getForms().get(0);
		form.setParameter("var6", "submission value6");
		response = form.submit();
		assertEquals("value 1," +
					 "value 2," +
					 "value 3," +
					 "value 4," +
					 "value5," +
					 "value6," +
					 "embedded submission value6," +
					 "submitted\n", response.getText());
	}

	public void testCookies()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm		form = null;

		request = new GetMethodWebRequest("http://localhost:8181/cookies");
		response = conversation.getResponse(request);
		assertEquals("value 1,"+
			"value 2,"+
			"value 3,"+
			"value 4,"+
			"<!--V 'OUTCOOKIE:cookie5'/-->,"+
			"<form action=\"/cookies\"><input name=\"submission\" type=\"hidden\" value=\"submission\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".COOKIES::engine_embedding_cookies:.COOKIES_EMBEDDED^.COOKIES_EMBEDDED")+"\" /><input type=\"submit\" /></form>\n", response.getText());
		form = response.getForms()[0];
		response = form.submit();
		assertEquals("embedded value 1,"+
			"embedded value 2,"+
			"value 3,"+
			"embedded value 4,"+
			"<!--V 'OUTCOOKIE:cookie5'/-->,submitted\n", response.getText());
	}

	public void testCookiesOutjection()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm		form = null;

		request = new GetMethodWebRequest("http://localhost:8181/cookies/outjection");
		response = conversation.getResponse(request);
		assertEquals("value 1,"+
					 "value 2,"+
					 "value 3,"+
					 "value 4,"+
					 "<!--V 'OUTCOOKIE:cookie5'/-->,"+
					 "<form action=\"/cookies/outjection\"><input name=\"submission\" type=\"hidden\" value=\"submission\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".COOKIES_OUTJECTION::engine_embedding_cookies_outjection:.COOKIES_OUTJECTION_EMBEDDED^.COOKIES_OUTJECTION_EMBEDDED")+"\" /><input type=\"submit\" /></form>\n", response.getText());
		form = response.getForms()[0];
		response = form.submit();
		assertEquals("embedded value 1,"+
					 "embedded value 2,"+
					 "value 3,"+
					 "embedded value 4,"+
					 "<!--V 'OUTCOOKIE:cookie5'/-->,submitted\n", response.getText());
	}

	public void testSuccessiveGlobalcookie()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;

		request = new GetMethodWebRequest("http://localhost:8181/successive_globalcookie");
		response = conversation.getResponse(request);
		assertEquals("embedded value 1,embedded value 2,<!--V 'OUTCOOKIE:cookie3'/-->,<!--V 'OUTCOOKIE:cookie4'/-->,<!--V 'OUTCOOKIE:cookie5'/-->\n"+
			"first: embedded\n"+
			"second: embedded value 1embedded value 2no cookie3\n", response.getText());
	}

	public void testInheritanceEmbedded()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	link = null;

		request = new GetMethodWebRequest("http://localhost:8181/inheritance/embedded");
		response = conversation.getResponse(request);
		assertEquals("The element \"<html><body><a href=\"/inheritance/embedded/target?submission=activatechild&submissioncontext="+StringUtils.encodeUrl(Base64.encode(".INHERITANCE_EMBEDDED_TARGET^.INHERITANCE_EMBEDDED_TARGET_PARENT"))+"&childrequest=ZWxlbWVudC9lbWJlZGRpbmcvZW1iZWRkZWQvaW5oZXJpdGFuY2VfY2hpbGQueG1seABHRVRtAA%3D%3D&triggerlist=\">activate child</a></body></html>\" is being embedded.\n", response.getText());
		link = response.getLinkWith("activate child");
		response = link.click();
		assertEquals("This is the child", response.getText());
	}

	public void testInheritanceEmbeddedNourl()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	link = null;

		request = new GetMethodWebRequest("http://localhost:8181/inheritance/embedded/nourl");
		response = conversation.getResponse(request);
		assertEquals("The element \"<html><body><a href=\"/inheritance/embedded/nourl?submission=activatechild&submissioncontext="+StringUtils.encodeUrl(Base64.encode(".INHERITANCE_EMBEDDED_NOURL::engine_embedding_inheritance_embedded_nourl:.INHERITANCE_EMBEDDED_NOURL_TARGET^.INHERITANCE_EMBEDDED_NOURL_TARGET_PARENT"))+"&childrequest=ZWxlbWVudC9lbWJlZGRpbmcvZW1iZWRkZWQvaW5oZXJpdGFuY2VfY2hpbGQueG1seABHRVRtAA%3D%3D&triggerlist=\">activate child</a></body></html>\" is being embedded.\n", response.getText());
		link = response.getLinkWith("activate child");
		response = link.click();
		assertEquals("The element \"This is the child\" is being embedded.\n", response.getText());
	}
	
	public void testInheritanceEmbeddedNourlPathinfo()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	link = null;

		request = new GetMethodWebRequest("http://localhost:8181/inheritance/embedded/nourl_pathinfo");
		response = conversation.getResponse(request);
		String arg1 = "The element \"<html><body><a href=\"/inheritance/embedded/nourl_pathinfo/?submission=activatechild&submissioncontext="+StringUtils.encodeUrl(Base64.encode(".INHERITANCE_EMBEDDED_NOURL_PATHINFO::engine_embedding_inheritance_embedded_nourl_pathinfo:.INHERITANCE_EMBEDDED_NOURL_PATHINFO_TARGET^.INHERITANCE_EMBEDDED_NOURL_PATHINFO_TARGET_PARENT"))+"&childrequest=ZWxlbWVudC9lbWJlZGRpbmcvZW1iZWRkZWQvaW5oZXJpdGFuY2VfY2hpbGQueG1seABHRVRtAA%3D%3D&triggerlist=\">activate child</a></body></html>\" is being embedded.\n";
		assertEquals(arg1, response.getText());
		link = response.getLinkWith("activate child");
		response = link.click();
		assertEquals("The element \"This is the child\" is being embedded.\n", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/embedded/nourl_pathinfo/somepathinfo");
		response = conversation.getResponse(request);
		arg1 = "The element \"<html><body><a href=\"/inheritance/embedded/nourl_pathinfo/somepathinfo?submission=activatechild&submissioncontext="+StringUtils.encodeUrl(Base64.encode(".INHERITANCE_EMBEDDED_NOURL_PATHINFO::engine_embedding_inheritance_embedded_nourl_pathinfo:.INHERITANCE_EMBEDDED_NOURL_PATHINFO_TARGET^.INHERITANCE_EMBEDDED_NOURL_PATHINFO_TARGET_PARENT"))+"&childrequest=ZWxlbWVudC9lbWJlZGRpbmcvZW1iZWRkZWQvaW5oZXJpdGFuY2VfY2hpbGQueG1seABHRVRtAA%3D%3D&triggerlist=\">activate child</a></body></html>\" is being embedded.\n";
		assertEquals(arg1, response.getText());
		link = response.getLinkWith("activate child");
		response = link.click();
		assertEquals("The element \"This is the child\" is being embedded.\n", response.getText());
	}
	
	public void testSuccessiveInheritanceEmbeddedNourl()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	link = null;

		request = new GetMethodWebRequest("http://localhost:8181/inheritance_successive/embedded/nourl");
		response = conversation.getResponse(request);
		assertEquals("The element \"<html><body><a href=\"/inheritance_successive/embedded/nourl?submission=activatechild&submissioncontext="+StringUtils.encodeUrl(Base64.encode(".INHERITANCE_SUCCESSIVE_EMBEDDED_NOURL::engine_embedding_inheritance_embedded_nourl:.INHERITANCE_EMBEDDED_NOURL_TARGET^.INHERITANCE_EMBEDDED_NOURL_TARGET_PARENT"))+"&childrequest=ZWxlbWVudC9lbWJlZGRpbmcvZW1iZWRkZWQvaW5oZXJpdGFuY2VfY2hpbGQueG1seABHRVRtAA%3D%3D&triggerlist=\">activate child</a></body></html>\" is being embedded.\n", response.getText());
		link = response.getLinkWith("activate child");
		response = link.click();
		assertEquals("The element \"This is the child\" is being embedded.\n", response.getText());
	}
	
	public void testSuccessiveInheritanceEmbeddedNourlPathinfo()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	link = null;

		request = new GetMethodWebRequest("http://localhost:8181/inheritance_successive/embedded/nourl_pathinfo_successive");
		response = conversation.getResponse(request);
		String arg1 = "The element \"<html><body><a href=\"/inheritance_successive/embedded/nourl_pathinfo_successive/?submission=activatechild&submissioncontext="+StringUtils.encodeUrl(Base64.encode(".INHERITANCE_SUCCESSIVE_EMBEDDED_NOURL_PATHINFO::engine_embedding_inheritance_embedded_nourl:.INHERITANCE_EMBEDDED_NOURL_TARGET^.INHERITANCE_EMBEDDED_NOURL_TARGET_PARENT"))+"&childrequest=ZWxlbWVudC9lbWJlZGRpbmcvZW1iZWRkZWQvaW5oZXJpdGFuY2VfY2hpbGQueG1seABHRVRtAA%3D%3D&triggerlist=\">activate child</a></body></html>\" is being embedded.\n";
		assertEquals(arg1, response.getText());
		link = response.getLinkWith("activate child");
		response = link.click();
		assertEquals("The element \"This is the child\" is being embedded.\n", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/inheritance_successive/embedded/nourl_pathinfo_successive/somepathinfo");
		response = conversation.getResponse(request);
		arg1 = "The element \"<html><body><a href=\"/inheritance_successive/embedded/nourl_pathinfo_successive/somepathinfo?submission=activatechild&submissioncontext="+StringUtils.encodeUrl(Base64.encode(".INHERITANCE_SUCCESSIVE_EMBEDDED_NOURL_PATHINFO::engine_embedding_inheritance_embedded_nourl:.INHERITANCE_EMBEDDED_NOURL_TARGET^.INHERITANCE_EMBEDDED_NOURL_TARGET_PARENT"))+"&childrequest=ZWxlbWVudC9lbWJlZGRpbmcvZW1iZWRkZWQvaW5oZXJpdGFuY2VfY2hpbGQueG1seABHRVRtAA%3D%3D&triggerlist=\">activate child</a></body></html>\" is being embedded.\n";
		assertEquals(arg1, response.getText());
		link = response.getLinkWith("activate child");
		response = link.click();
		assertEquals("The element \"This is the child\" is being embedded.\n", response.getText());
	}

	public void testInheritanceCookieEmbedding()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	link = null;

		request = new GetMethodWebRequest("http://localhost:8181/inheritance/cookie/embedding");
		response = conversation.getResponse(request);
		assertEquals("<html><body><a href=\"/inheritance/cookie/embedding?submission=activatechild&submissioncontext="+StringUtils.encodeUrl(Base64.encode(".INHERITANCE_COOKIE_EMBEDDING^.INHERITANCE_COOKIE_EMBEDDING_PARENT"))+"&childrequest=ZWxlbWVudC9lbWJlZGRpbmcvaW5oZXJpdGFuY2VfY29va2llX2VtYmVkZGluZy54bWx4AEdFVG0A&triggerlist=\">activate child</a></body></html>", response.getText());
		link = response.getLinkWith("activate child");
		response = link.click();
		assertEquals("The element \"This is the child\" is being embedded.\n", response.getText());
	}

	public void testInheritanceGlobalvarEmbedding()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	link = null;

		request = new GetMethodWebRequest("http://localhost:8181/inheritance/globalvar/embedding");
		response = conversation.getResponse(request);
		assertEquals("<html><body><a href=\"/inheritance/globalvar/embedding?submission=activatechild&submissioncontext="+StringUtils.encodeUrl(Base64.encode(".INHERITANCE_GLOBALVAR_EMBEDDING^.INHERITANCE_GLOBALVAR_EMBEDDING_PARENT"))+"&childrequest=ZWxlbWVudC9lbWJlZGRpbmcvaW5oZXJpdGFuY2VfZ2xvYmFsdmFyX2VtYmVkZGluZy54bWx4AEdFVG0A&triggerlist=\">activate child</a></body></html>", response.getText());
		link = response.getLinkWith("activate child");
		response = link.click();
		assertEquals("The element \"This is the child\" is being embedded.\n", response.getText());
	}

	public void testScripted()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebForm form = null;

		request = new GetMethodWebRequest("http://localhost:8181/scripted");
		request.setParameter("input1", "these values");
		request.setParameter("input2", "are scripted");
		response = conversation.getResponse(request);
		assertEquals("The element \"these values,are scripted\" is being embedded.", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/scripted");
		request.setParameter("input1", "form");
		response = conversation.getResponse(request);
		assertEquals("The element \"<html><body>\n"+
			"\t\t\t\t<form action=\"/scripted/embedded\" method=\"post\">\n"+
			"\t\t\t\t<input name=\"submission\" type=\"hidden\" value=\"login\" /><input name=\"submissioncontext\" type=\"hidden\" value=\""+Base64.encode(".SCRIPTED_EMBEDDED")+"\" /><input name=\"inputs\" type=\"hidden\" value=\"LlNDUklQVEVEX0VNQkVEREVEYwBpbnB1dDFuAGZvcm0=\" /><input name=\"login\" type=\"text\">\n"+
			"\t\t\t\t<input name=\"password\" type=\"password\">\n"+
			"\t\t\t\t<input type=\"submit\">\n"+
			"\t\t\t\t</form>\n"+
			"\t\t\t\t</body></html>\" is being embedded.", response.getText());
		form = response.getForms()[0];
		form.setParameter("login", "mylogin");
		form.setParameter("password", "mypassword");
		response = form.submit();

		assertEquals("mylogin,mypassword", response.getText());
	}

	public void testExits()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/exits");
		response = conversation.getResponse(request);
		assertEquals("The element \"destination\" is being embedded.", response.getText());
	}

	public void testExitNourlReflective()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/exit_nourlreflective");
		response = conversation.getResponse(request);
		assertEquals("no embedderinput", response.getElementWithID("embedder_value1").getAttribute("value"));
		assertEquals("no embeddedinput", response.getElementWithID("embedder_value2").getAttribute("value"));
		assertEquals("no input", response.getElementWithID("embedded_inputvalue").getAttribute("value"));
		assertEquals("/exit_nourlreflective?embeddedinput=outputvalue", response.getLinks()[0].getURLString());

		response = response.getLinks()[0].click();
		assertEquals("no embedderinput", response.getElementWithID("embedder_value1").getAttribute("value"));
		assertEquals("outputvalue", response.getElementWithID("embedder_value2").getAttribute("value"));
		assertEquals("outputvalue", response.getElementWithID("embedded_inputvalue").getAttribute("value"));

		request = new GetMethodWebRequest("http://localhost:8181/exit_nourlreflective?embedderinput=embeddervalue1&embeddedinput=embeddervalue2");
		response = conversation.getResponse(request);
		assertEquals("embeddervalue1", response.getElementWithID("embedder_value1").getAttribute("value"));
		assertEquals("embeddervalue2", response.getElementWithID("embedder_value2").getAttribute("value"));
		assertEquals("embeddervalue2", response.getElementWithID("embedded_inputvalue").getAttribute("value"));
		assertEquals("/exit_nourlreflective?embedderinput=embeddervalue1&embeddedinput=outputvalue", response.getLinks()[0].getURLString());

		response = response.getLinks()[0].click();
		assertEquals("embeddervalue1", response.getElementWithID("embedder_value1").getAttribute("value"));
		assertEquals("outputvalue", response.getElementWithID("embedder_value2").getAttribute("value"));
		assertEquals("outputvalue", response.getElementWithID("embedded_inputvalue").getAttribute("value"));
	}

	public void testRelative()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/relative1/toplevel");
		response = conversation.getResponse(request);
		assertEquals("The element \"Relative1 embedded\" is being embedded.", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/relative2/toplevel");
		response = conversation.getResponse(request);
		assertEquals("The element \"Relative2 embedded\" is being embedded.", response.getText());
	}

	public void testParent()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/relative1/parent");
		response = conversation.getResponse(request);
		assertEquals("The element \"Relative2 embedded\" is being embedded.", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/relative2/parent");
		response = conversation.getResponse(request);
		assertEquals("The element \"Relative1 embedded\" is being embedded.", response.getText());
	}

	public void testExplicitProcessing()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	link = null;

		request = new GetMethodWebRequest("http://localhost:8181/explicit_processing");
		response = conversation.getResponse(request);
		link = response.getLinkWith("late");
		response = link.click();
		link = response.getLinkWith("result");
		response = link.click();
		assertTrue(response.getText().indexOf("Input \"value2\"") != -1);
		assertTrue(response.getText().indexOf("Input \"embedded_value2\"") == -1);

		request = new GetMethodWebRequest("http://localhost:8181/explicit_processing");
		response = conversation.getResponse(request);
		link = response.getLinkWith("early");
		response = link.click();
		link = response.getLinkWith("result");
		response = link.click();
		assertTrue(response.getText().indexOf("Input \"value2\"") == -1);
		assertTrue(response.getText().indexOf("Input \"embedded_value2\"") != -1);
	}

	public void testPriorities()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/priorities");
		response = conversation.getResponse(request);
		assertEquals("These elements are embedded:\n"+
			".PRIORITIES_EMBEDDED_LATE\n"+
			".PRIORITIES_EMBEDDED_NORMAL\n"+
			".PRIORITIES_EMBEDDED_EARLY\n"+
			"done.\n"+
			".PRIORITIES_EMBEDDED_EARLY\n"+
			".PRIORITIES_EMBEDDED_NORMAL\n"+
			".PRIORITIES\n"+
			".PRIORITIES_EMBEDDED_LATE\n", response.getText());
	}

	public void testGlobalVarSiblingEmbedded()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/globalvar_sibling");
		response = conversation.getResponse(request);
		assertEquals("embedded2: .GLOBALVAR_SIBLING_EMBEDDED2: inheritancevalue\n"+
					 "embedded3: .GLOBALVAR_SIBLING_EMBEDDED3: null\n"+
					 "embedded1: .GLOBALVAR_SIBLING_EMBEDDED1b: .GLOBALVAR_SIBLING_EMBEDDED1: inheritancevalue\n"+
					 "embedded3b: .GLOBALVAR_SIBLING_EMBEDDED3: inheritancevalue\n"+
					 "embedded4: .GLOBALVAR_SIBLING_EMBEDDED4: inheritancevalue\n", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/globalvar_sibling?clear=true");
		response = conversation.getResponse(request);
		assertEquals("embedded2: .GLOBALVAR_SIBLING_EMBEDDED2: null\n"+
					 "embedded3: .GLOBALVAR_SIBLING_EMBEDDED3: null\n"+
					 "embedded1: .GLOBALVAR_SIBLING_EMBEDDED1b: .GLOBALVAR_SIBLING_EMBEDDED1: inheritancevalue\n"+
					 "embedded3b: .GLOBALVAR_SIBLING_EMBEDDED3: inheritancevalue\n"+
					 "embedded4: .GLOBALVAR_SIBLING_EMBEDDED4: inheritancevalue\n", response.getText());
	}

	public void testGlobalVarSiblingEmbeddedBijection()
	throws Exception
	{
		setupSite("site/embedding.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/globalvar_sibling/bijection");
		response = conversation.getResponse(request);
		assertEquals("embedded2: .GLOBALVAR_SIBLING_BIJECTION_EMBEDDED2: inheritancevalue\n"+
					 "embedded3: .GLOBALVAR_SIBLING_BIJECTION_EMBEDDED3: null\n"+
					 "embedded1: .GLOBALVAR_SIBLING_BIJECTION_EMBEDDED1b: .GLOBALVAR_SIBLING_BIJECTION_EMBEDDED1: inheritancevalue\n"+
					 "embedded3b: .GLOBALVAR_SIBLING_BIJECTION_EMBEDDED3: inheritancevalue\n"+
					 "embedded4: .GLOBALVAR_SIBLING_BIJECTION_EMBEDDED4: inheritancevalue\n", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/globalvar_sibling/bijection?clear=true");
		response = conversation.getResponse(request);
		assertEquals("embedded2: .GLOBALVAR_SIBLING_BIJECTION_EMBEDDED2: null\n"+
					 "embedded3: .GLOBALVAR_SIBLING_BIJECTION_EMBEDDED3: null\n"+
					 "embedded1: .GLOBALVAR_SIBLING_BIJECTION_EMBEDDED1b: .GLOBALVAR_SIBLING_BIJECTION_EMBEDDED1: inheritancevalue\n"+
					 "embedded3b: .GLOBALVAR_SIBLING_BIJECTION_EMBEDDED3: inheritancevalue\n"+
					 "embedded4: .GLOBALVAR_SIBLING_BIJECTION_EMBEDDED4: inheritancevalue\n", response.getText());
	}

	public void testPrintAndWriteBuffer()
	throws Exception
	{
		setupSite("site/embedding.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/printandwrite_buffer");
		response = conversation.getResponse(request);
		assertEquals("The element \"write2write4print1print3\" is being embedded.\n", response.getText());
	}

	public void testPrintAndWriteNobuffer()
	throws Exception
	{
		setupSite("site/embedding.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/printandwrite_nobuffer");
		response = conversation.getResponse(request);
		assertEquals("The element \"print1write2print3write4\" is being embedded.\n", response.getText());
	}

	public void testStatefulElementsCounter()
	throws Exception
	{
		Site site = new SiteBuilder("site/embedding.xml").getSite();

		MockConversation conversation = new MockConversation(site);

		MockResponse response1 = conversation.doRequest("/counter");
		List<MockResponse> responses1 = response1.getEmbeddedResponses();
		assertEquals(2, responses1.size());
		assertEquals("0", responses1.get(0).getTemplate().getValue("counter"));
		assertEquals("0", responses1.get(1).getTemplate().getValue("counter"));
		ParsedHtml parsed1 = response1.getParsedHtml();

		MockResponse response2 = parsed1.getFormWithId("increase1").submit();
		List<MockResponse> responses2 = response2.getEmbeddedResponses();
		assertEquals(2, responses2.size());
		assertEquals("1", responses2.get(0).getTemplate().getValue("counter"));
		assertEquals("0", responses2.get(1).getTemplate().getValue("counter"));
		ParsedHtml parsed2 = response2.getParsedHtml();

		MockResponse response3 = parsed2.getFormWithId("increase2").submit();
		List<MockResponse> responses3 = response3.getEmbeddedResponses();
		assertEquals(2, responses3.size());
		assertEquals("1", responses3.get(0).getTemplate().getValue("counter"));
		assertEquals("1", responses3.get(1).getTemplate().getValue("counter"));
		ParsedHtml parsed3 = response3.getParsedHtml();

		MockResponse response4 = parsed3.getFormWithId("increase2").submit();
		List<MockResponse> responses4 = response4.getEmbeddedResponses();
		assertEquals(2, responses4.size());
		assertEquals("1", responses4.get(0).getTemplate().getValue("counter"));
		assertEquals("2", responses4.get(1).getTemplate().getValue("counter"));
		ParsedHtml parsed4 = response4.getParsedHtml();

		MockResponse response5 = parsed4.getFormWithId("increase2").submit();
		List<MockResponse> responses5 = response5.getEmbeddedResponses();
		assertEquals(2, responses5.size());
		assertEquals("1", responses5.get(0).getTemplate().getValue("counter"));
		assertEquals("3", responses5.get(1).getTemplate().getValue("counter"));
		ParsedHtml parsed5 = response5.getParsedHtml();

		MockResponse response6 = parsed5.getFormWithId("increase1").submit();
		List<MockResponse> responses6 = response6.getEmbeddedResponses();
		assertEquals(2, responses6.size());
		assertEquals("2", responses6.get(0).getTemplate().getValue("counter"));
		assertEquals("3", responses6.get(1).getTemplate().getValue("counter"));
		ParsedHtml parsed6 = response6.getParsedHtml();

		MockResponse response7 = parsed6.getFormWithId("decrease2").submit();
		List<MockResponse> responses7 = response7.getEmbeddedResponses();
		assertEquals(2, responses7.size());
		assertEquals("2", responses7.get(0).getTemplate().getValue("counter"));
		assertEquals("2", responses7.get(1).getTemplate().getValue("counter"));
		ParsedHtml parsed7 = response7.getParsedHtml();

		MockResponse response8 = parsed7.getFormWithId("decrease1").submit();
		List<MockResponse> responses8 = response8.getEmbeddedResponses();
		assertEquals(2, responses8.size());
		assertEquals("1", responses8.get(0).getTemplate().getValue("counter"));
		assertEquals("2", responses8.get(1).getTemplate().getValue("counter"));

		MockResponse response7b = parsed6.getFormWithId("increase2").submit();
		List<MockResponse> responses7b = response7b.getEmbeddedResponses();
		assertEquals(2, responses7b.size());
		assertEquals("2", responses7b.get(0).getTemplate().getValue("counter"));
		assertEquals("4", responses7b.get(1).getTemplate().getValue("counter"));
	}

	public void testStatefulElementsCounterSessionState()
	throws Exception
	{
		Site site = new SiteBuilder("site/embedding.xml").getSite();

		MockConversation conversation = new MockConversation(site);

		MockResponse response1 = conversation.doRequest("/counter_session");
		List<MockResponse> responses1 = response1.getEmbeddedResponses();
		assertEquals(2, responses1.size());
		assertEquals("0", responses1.get(0).getTemplate().getValue("counter"));
		assertEquals("0", responses1.get(1).getTemplate().getValue("counter"));
		ParsedHtml parsed1 = response1.getParsedHtml();

		MockForm form1 = parsed1.getFormWithId("increase1");
		assertEquals(1, form1.getParameters().size());
		assertNotNull(form1.getParameterValue(ReservedParameters.STATEID));
		MockResponse response2 = form1.submit();
		List<MockResponse> responses2 = response2.getEmbeddedResponses();
		assertEquals(2, responses2.size());
		assertEquals("1", responses2.get(0).getTemplate().getValue("counter"));
		assertEquals("0", responses2.get(1).getTemplate().getValue("counter"));
		ParsedHtml parsed2 = response2.getParsedHtml();

		MockForm form2 = parsed2.getFormWithId("increase2");
		assertEquals(1, form2.getParameters().size());
		assertNotNull(form2.getParameterValue(ReservedParameters.STATEID));
		MockResponse response3 = form2.submit();
		List<MockResponse> responses3 = response3.getEmbeddedResponses();
		assertEquals(2, responses3.size());
		assertEquals("1", responses3.get(0).getTemplate().getValue("counter"));
		assertEquals("1", responses3.get(1).getTemplate().getValue("counter"));
		ParsedHtml parsed3 = response3.getParsedHtml();

		MockForm form3 = parsed3.getFormWithId("increase2");
		assertEquals(1, form3.getParameters().size());
		assertNotNull(form3.getParameterValue(ReservedParameters.STATEID));
		MockResponse response4 = form3.submit();
		List<MockResponse> responses4 = response4.getEmbeddedResponses();
		assertEquals(2, responses4.size());
		assertEquals("1", responses4.get(0).getTemplate().getValue("counter"));
		assertEquals("2", responses4.get(1).getTemplate().getValue("counter"));
		ParsedHtml parsed4 = response4.getParsedHtml();

		MockForm form4 = parsed4.getFormWithId("increase2");
		assertEquals(1, form4.getParameters().size());
		assertNotNull(form4.getParameterValue(ReservedParameters.STATEID));
		MockResponse response5 = form4.submit();
		List<MockResponse> responses5 = response5.getEmbeddedResponses();
		assertEquals(2, responses5.size());
		assertEquals("1", responses5.get(0).getTemplate().getValue("counter"));
		assertEquals("3", responses5.get(1).getTemplate().getValue("counter"));
		ParsedHtml parsed5 = response5.getParsedHtml();

		MockForm form5 = parsed5.getFormWithId("increase1");
		assertEquals(1, form5.getParameters().size());
		assertNotNull(form5.getParameterValue(ReservedParameters.STATEID));
		MockResponse response6 = form5.submit();
		List<MockResponse> responses6 = response6.getEmbeddedResponses();
		assertEquals(2, responses6.size());
		assertEquals("2", responses6.get(0).getTemplate().getValue("counter"));
		assertEquals("3", responses6.get(1).getTemplate().getValue("counter"));
		ParsedHtml parsed6 = response6.getParsedHtml();

		MockForm form6 = parsed6.getFormWithId("decrease2");
		assertEquals(1, form6.getParameters().size());
		assertNotNull(form6.getParameterValue(ReservedParameters.STATEID));
		MockResponse response7 = form6.submit();
		List<MockResponse> responses7 = response7.getEmbeddedResponses();
		assertEquals(2, responses7.size());
		assertEquals("2", responses7.get(0).getTemplate().getValue("counter"));
		assertEquals("2", responses7.get(1).getTemplate().getValue("counter"));
		ParsedHtml parsed7 = response7.getParsedHtml();

		MockForm form7 = parsed7.getFormWithId("decrease1");
		assertEquals(1, form7.getParameters().size());
		assertNotNull(form7.getParameterValue(ReservedParameters.STATEID));
		MockResponse response8 = form7.submit();
		List<MockResponse> responses8 = response8.getEmbeddedResponses();
		assertEquals(2, responses8.size());
		assertEquals("1", responses8.get(0).getTemplate().getValue("counter"));
		assertEquals("2", responses8.get(1).getTemplate().getValue("counter"));

		MockForm form8 = parsed6.getFormWithId("increase2");
		assertEquals(1, form8.getParameters().size());
		assertNotNull(form8.getParameterValue(ReservedParameters.STATEID));
		MockResponse response7b = form8.submit();
		List<MockResponse> responses7b = response7b.getEmbeddedResponses();
		assertEquals(2, responses7b.size());
		assertEquals("2", responses7b.get(0).getTemplate().getValue("counter"));
		assertEquals("4", responses7b.get(1).getTemplate().getValue("counter"));
	}

	public void testStatefulElementsCounterContinuations()
	throws Exception
	{
		Site site = new SiteBuilder("site/embedding.xml").getSite();
		
		MockConversation conversation = new MockConversation(site);
		
		MockResponse response1 = conversation.doRequest("/countercontinuations");
		List<MockResponse> responses1 = response1.getEmbeddedResponses();
		assertEquals(2, responses1.size());
		assertEquals("0", responses1.get(0).getTemplate().getValue("counter"));
		assertEquals("0", responses1.get(1).getTemplate().getValue("counter"));
		ParsedHtml parsed1 = response1.getParsedHtml();
		
		MockResponse response2 = parsed1.getFormWithId("increase1").submit();
		List<MockResponse> responses2 = response2.getEmbeddedResponses();
		assertEquals(2, responses2.size());
		assertEquals("1", responses2.get(0).getTemplate().getValue("counter"));
		assertEquals("0", responses2.get(1).getTemplate().getValue("counter"));
		ParsedHtml parsed2 = response2.getParsedHtml();
		
		MockResponse response3 = parsed2.getFormWithId("increase2").submit();
		List<MockResponse> responses3 = response3.getEmbeddedResponses();
		assertEquals(2, responses3.size());
		assertEquals("1", responses3.get(0).getTemplate().getValue("counter"));
		assertEquals("1", responses3.get(1).getTemplate().getValue("counter"));
		ParsedHtml parsed3 = response3.getParsedHtml();
		
		MockResponse response4 = parsed3.getFormWithId("increase2").submit();
		List<MockResponse> responses4 = response4.getEmbeddedResponses();
		assertEquals(2, responses4.size());
		assertEquals("1", responses4.get(0).getTemplate().getValue("counter"));
		assertEquals("2", responses4.get(1).getTemplate().getValue("counter"));
		ParsedHtml parsed4 = response4.getParsedHtml();
		
		MockResponse response5 = parsed4.getFormWithId("increase2").submit();
		List<MockResponse> responses5 = response5.getEmbeddedResponses();
		assertEquals(2, responses5.size());
		assertEquals("1", responses5.get(0).getTemplate().getValue("counter"));
		assertEquals("3", responses5.get(1).getTemplate().getValue("counter"));
		ParsedHtml parsed5 = response5.getParsedHtml();
		
		MockResponse response6 = parsed5.getFormWithId("increase1").submit();
		List<MockResponse> responses6 = response6.getEmbeddedResponses();
		assertEquals(2, responses6.size());
		assertEquals("2", responses6.get(0).getTemplate().getValue("counter"));
		assertEquals("3", responses6.get(1).getTemplate().getValue("counter"));
		ParsedHtml parsed6 = response6.getParsedHtml();
		
		MockResponse response7 = parsed6.getFormWithId("decrease2").submit();
		List<MockResponse> responses7 = response7.getEmbeddedResponses();
		assertEquals(2, responses7.size());
		assertEquals("2", responses7.get(0).getTemplate().getValue("counter"));
		assertEquals("2", responses7.get(1).getTemplate().getValue("counter"));
		ParsedHtml parsed7 = response7.getParsedHtml();
		
		MockResponse response8 = parsed7.getFormWithId("decrease1").submit();
		List<MockResponse> responses8 = response8.getEmbeddedResponses();
		assertEquals(2, responses8.size());
		assertEquals("1", responses8.get(0).getTemplate().getValue("counter"));
		assertEquals("2", responses8.get(1).getTemplate().getValue("counter"));
		
		MockResponse response7b = parsed6.getFormWithId("increase2").submit();
		List<MockResponse> responses7b = response7b.getEmbeddedResponses();
		assertEquals(2, responses7b.size());
		assertEquals("2", responses7b.get(0).getTemplate().getValue("counter"));
		assertEquals("4", responses7b.get(1).getTemplate().getValue("counter"));
	}

	public void testStatefulElementsCounterSessionStateNoclone()
	throws Exception
	{
		Config.getRepInstance().setParameter(RifeConfig.Engine.PARAM_SESSION_STATE_STORE_CLONING, false);
		try
		{
			Site site = new SiteBuilder("site/embedding.xml").getSite();

			MockConversation conversation = new MockConversation(site);

			MockResponse response1 = conversation.doRequest("/counter_session");
			List<MockResponse> responses1 = response1.getEmbeddedResponses();
			assertEquals(2, responses1.size());
			assertEquals("0", responses1.get(0).getTemplate().getValue("counter"));
			assertEquals("0", responses1.get(1).getTemplate().getValue("counter"));
			ParsedHtml parsed1 = response1.getParsedHtml();

			MockForm form1 = parsed1.getFormWithId("increase1");
			assertEquals(1, form1.getParameters().size());
			assertNotNull(form1.getParameterValue(ReservedParameters.STATEID));
			MockResponse response2 = form1.submit();
			List<MockResponse> responses2 = response2.getEmbeddedResponses();
			assertEquals(2, responses2.size());
			assertEquals("1", responses2.get(0).getTemplate().getValue("counter"));
			assertEquals("0", responses2.get(1).getTemplate().getValue("counter"));
			ParsedHtml parsed2 = response2.getParsedHtml();

			MockForm form2 = parsed2.getFormWithId("increase2");
			assertEquals(1, form2.getParameters().size());
			assertNotNull(form2.getParameterValue(ReservedParameters.STATEID));
			MockResponse response3 = form2.submit();
			List<MockResponse> responses3 = response3.getEmbeddedResponses();
			assertEquals(2, responses3.size());
			assertEquals("1", responses3.get(0).getTemplate().getValue("counter"));
			assertEquals("1", responses3.get(1).getTemplate().getValue("counter"));
			ParsedHtml parsed3 = response3.getParsedHtml();

			MockForm form3 = parsed3.getFormWithId("increase2");
			assertEquals(1, form3.getParameters().size());
			assertNotNull(form3.getParameterValue(ReservedParameters.STATEID));
			MockResponse response4 = form3.submit();
			List<MockResponse> responses4 = response4.getEmbeddedResponses();
			assertEquals(2, responses4.size());
			assertEquals("1", responses4.get(0).getTemplate().getValue("counter"));
			assertEquals("2", responses4.get(1).getTemplate().getValue("counter"));
			ParsedHtml parsed4 = response4.getParsedHtml();

			MockForm form4 = parsed4.getFormWithId("increase2");
			assertEquals(1, form4.getParameters().size());
			assertNotNull(form4.getParameterValue(ReservedParameters.STATEID));
			MockResponse response5 = form4.submit();
			List<MockResponse> responses5 = response5.getEmbeddedResponses();
			assertEquals(2, responses5.size());
			assertEquals("1", responses5.get(0).getTemplate().getValue("counter"));
			assertEquals("3", responses5.get(1).getTemplate().getValue("counter"));
			ParsedHtml parsed5 = response5.getParsedHtml();

			MockForm form5 = parsed5.getFormWithId("increase1");
			assertEquals(1, form5.getParameters().size());
			assertNotNull(form5.getParameterValue(ReservedParameters.STATEID));
			MockResponse response6 = form5.submit();
			List<MockResponse> responses6 = response6.getEmbeddedResponses();
			assertEquals(2, responses6.size());
			assertEquals("2", responses6.get(0).getTemplate().getValue("counter"));
			assertEquals("3", responses6.get(1).getTemplate().getValue("counter"));
			ParsedHtml parsed6 = response6.getParsedHtml();

			MockForm form6 = parsed6.getFormWithId("decrease2");
			assertEquals(1, form6.getParameters().size());
			assertNotNull(form6.getParameterValue(ReservedParameters.STATEID));
			MockResponse response7 = form6.submit();
			List<MockResponse> responses7 = response7.getEmbeddedResponses();
			assertEquals(2, responses7.size());
			assertEquals("2", responses7.get(0).getTemplate().getValue("counter"));
			assertEquals("2", responses7.get(1).getTemplate().getValue("counter"));
			ParsedHtml parsed7 = response7.getParsedHtml();

			MockForm form7 = parsed7.getFormWithId("decrease1");
			assertEquals(1, form7.getParameters().size());
			assertNotNull(form7.getParameterValue(ReservedParameters.STATEID));
			MockResponse response8 = form7.submit();
			List<MockResponse> responses8 = response8.getEmbeddedResponses();
			assertEquals(2, responses8.size());
			assertEquals("1", responses8.get(0).getTemplate().getValue("counter"));
			assertEquals("2", responses8.get(1).getTemplate().getValue("counter"));

			MockForm form8 = parsed6.getFormWithId("increase2");
			assertEquals(1, form8.getParameters().size());
			assertNotNull(form8.getParameterValue(ReservedParameters.STATEID));
			MockResponse response7b = form8.submit();
			List<MockResponse> responses7b = response7b.getEmbeddedResponses();
			assertEquals(2, responses7b.size());
			assertEquals("1", responses7b.get(0).getTemplate().getValue("counter"));
			assertEquals("4", responses7b.get(1).getTemplate().getValue("counter"));
		}
		finally
		{
			Config.getRepInstance().setParameter(RifeConfig.Engine.PARAM_SESSION_STATE_STORE_CLONING, true);
		}
	}

	public void testStatefulExplicitProcessing()
	throws Exception
	{
		Site site = new SiteBuilder("site/embedding.xml").getSite();

		MockConversation conversation = new MockConversation(site);
		List<MockForm> forms;

		MockResponse response = conversation.doRequest("/statefulexplicitprocessing");
		forms = response.getParsedHtml().getForms();
		assertEquals(7, forms.size());

		assertEquals("two", forms.get(1).getName());
		response = forms.get(1).submit();
		assertEquals("two", response.xpathString("//div[@id='id_two']/text()").trim());
		forms = response.getParsedHtml().getForms();
		assertEquals(6, forms.size());
		assertEquals("three", forms.get(1).getName());

		assertEquals("one", forms.get(0).getName());
		response = forms.get(0).submit();
		assertEquals("one", response.xpathString("//div[@id='id_one']/text()").trim());
		assertEquals("two", response.xpathString("//div[@id='id_two']/text()").trim());
		forms = response.getParsedHtml().getForms();
		assertEquals(5, forms.size());
		assertEquals("three", forms.get(0).getName());

		assertEquals("six", forms.get(3).getName());
		response = forms.get(3).submit();
		assertEquals("one", response.xpathString("//div[@id='id_one']/text()").trim());
		assertEquals("two", response.xpathString("//div[@id='id_two']/text()").trim());
		assertEquals("six", response.xpathString("//div[@id='id_six']/text()").trim());
		forms = response.getParsedHtml().getForms();
		assertEquals(4, forms.size());
		assertEquals("seven", forms.get(3).getName());

		assertEquals("four", forms.get(1).getName());
		response = forms.get(1).submit();
		assertEquals("one", response.xpathString("//div[@id='id_one']/text()").trim());
		assertEquals("two", response.xpathString("//div[@id='id_two']/text()").trim());
		assertEquals("four", response.xpathString("//div[@id='id_four']/text()").trim());
		assertEquals("six", response.xpathString("//div[@id='id_six']/text()").trim());
		forms = response.getParsedHtml().getForms();
		assertEquals(3, forms.size());
		assertEquals("five", forms.get(1).getName());

		assertEquals("three", forms.get(0).getName());
		response = forms.get(0).submit();
		assertEquals("one", response.xpathString("//div[@id='id_one']/text()").trim());
		assertEquals("two", response.xpathString("//div[@id='id_two']/text()").trim());
		assertEquals("three", response.xpathString("//div[@id='id_three']/text()").trim());
		assertEquals("four", response.xpathString("//div[@id='id_four']/text()").trim());
		assertEquals("six", response.xpathString("//div[@id='id_six']/text()").trim());
		forms = response.getParsedHtml().getForms();
		assertEquals(2, forms.size());
		assertEquals("five", forms.get(0).getName());

		assertEquals("seven", forms.get(1).getName());
		response = forms.get(1).submit();
		assertEquals("one", response.xpathString("//div[@id='id_one']/text()").trim());
		assertEquals("two", response.xpathString("//div[@id='id_two']/text()").trim());
		assertEquals("three", response.xpathString("//div[@id='id_three']/text()").trim());
		assertEquals("four", response.xpathString("//div[@id='id_four']/text()").trim());
		assertEquals("six", response.xpathString("//div[@id='id_six']/text()").trim());
		assertEquals("seven", response.xpathString("//div[@id='id_seven']/text()").trim());
		forms = response.getParsedHtml().getForms();
		assertEquals(1, forms.size());
		assertEquals("five", forms.get(0).getName());

		assertEquals("five", forms.get(0).getName());
		response = forms.get(0).submit();
		assertEquals("one", response.xpathString("//div[@id='id_one']/text()").trim());
		assertEquals("two", response.xpathString("//div[@id='id_two']/text()").trim());
		assertEquals("three", response.xpathString("//div[@id='id_three']/text()").trim());
		assertEquals("four", response.xpathString("//div[@id='id_four']/text()").trim());
		assertEquals("five", response.xpathString("//div[@id='id_five']/text()").trim());
		assertEquals("six", response.xpathString("//div[@id='id_six']/text()").trim());
		assertEquals("seven", response.xpathString("//div[@id='id_seven']/text()").trim());
		forms = response.getParsedHtml().getForms();
		assertEquals(0, forms.size());
	}

	public void testStatefulExplicitProcessingSessionState()
	throws Exception
	{
		Site site = new SiteBuilder("site/embedding.xml").getSite();

		MockConversation conversation = new MockConversation(site);
		List<MockForm> forms;

		MockResponse response = conversation.doRequest("/statefulexplicitprocessing_session");
		forms = response.getParsedHtml().getForms();
		assertEquals(7, forms.size());
		for (MockForm form : forms)
		{
			assertEquals(1, form.getParameters().size());
			assertNotNull(form.getParameterValue(ReservedParameters.STATEID));
		}

		assertEquals("two", forms.get(1).getName());
		response = forms.get(1).submit();
		assertEquals("two", response.xpathString("//div[@id='id_two']/text()").trim());
		forms = response.getParsedHtml().getForms();
		assertEquals(6, forms.size());
		assertEquals("three", forms.get(1).getName());
		for (MockForm form : forms)
		{
			assertEquals(1, form.getParameters().size());
			assertNotNull(form.getParameterValue(ReservedParameters.STATEID));
		}

		assertEquals("one", forms.get(0).getName());
		response = forms.get(0).submit();
		assertEquals("one", response.xpathString("//div[@id='id_one']/text()").trim());
		assertEquals("two", response.xpathString("//div[@id='id_two']/text()").trim());
		forms = response.getParsedHtml().getForms();
		assertEquals(5, forms.size());
		assertEquals("three", forms.get(0).getName());
		for (MockForm form : forms)
		{
			assertEquals(1, form.getParameters().size());
			assertNotNull(form.getParameterValue(ReservedParameters.STATEID));
		}

		assertEquals("six", forms.get(3).getName());
		response = forms.get(3).submit();
		assertEquals("one", response.xpathString("//div[@id='id_one']/text()").trim());
		assertEquals("two", response.xpathString("//div[@id='id_two']/text()").trim());
		assertEquals("six", response.xpathString("//div[@id='id_six']/text()").trim());
		forms = response.getParsedHtml().getForms();
		assertEquals(4, forms.size());
		assertEquals("seven", forms.get(3).getName());
		for (MockForm form : forms)
		{
			assertEquals(1, form.getParameters().size());
			assertNotNull(form.getParameterValue(ReservedParameters.STATEID));
		}

		assertEquals("four", forms.get(1).getName());
		response = forms.get(1).submit();
		assertEquals("one", response.xpathString("//div[@id='id_one']/text()").trim());
		assertEquals("two", response.xpathString("//div[@id='id_two']/text()").trim());
		assertEquals("four", response.xpathString("//div[@id='id_four']/text()").trim());
		assertEquals("six", response.xpathString("//div[@id='id_six']/text()").trim());
		forms = response.getParsedHtml().getForms();
		assertEquals(3, forms.size());
		assertEquals("five", forms.get(1).getName());
		for (MockForm form : forms)
		{
			assertEquals(1, form.getParameters().size());
			assertNotNull(form.getParameterValue(ReservedParameters.STATEID));
		}

		assertEquals("three", forms.get(0).getName());
		response = forms.get(0).submit();
		assertEquals("one", response.xpathString("//div[@id='id_one']/text()").trim());
		assertEquals("two", response.xpathString("//div[@id='id_two']/text()").trim());
		assertEquals("three", response.xpathString("//div[@id='id_three']/text()").trim());
		assertEquals("four", response.xpathString("//div[@id='id_four']/text()").trim());
		assertEquals("six", response.xpathString("//div[@id='id_six']/text()").trim());
		forms = response.getParsedHtml().getForms();
		assertEquals(2, forms.size());
		assertEquals("five", forms.get(0).getName());
		for (MockForm form : forms)
		{
			assertEquals(1, form.getParameters().size());
			assertNotNull(form.getParameterValue(ReservedParameters.STATEID));
		}

		assertEquals("seven", forms.get(1).getName());
		response = forms.get(1).submit();
		assertEquals("one", response.xpathString("//div[@id='id_one']/text()").trim());
		assertEquals("two", response.xpathString("//div[@id='id_two']/text()").trim());
		assertEquals("three", response.xpathString("//div[@id='id_three']/text()").trim());
		assertEquals("four", response.xpathString("//div[@id='id_four']/text()").trim());
		assertEquals("six", response.xpathString("//div[@id='id_six']/text()").trim());
		assertEquals("seven", response.xpathString("//div[@id='id_seven']/text()").trim());
		forms = response.getParsedHtml().getForms();
		assertEquals(1, forms.size());
		assertEquals("five", forms.get(0).getName());
		for (MockForm form : forms)
		{
			assertEquals(1, form.getParameters().size());
			assertNotNull(form.getParameterValue(ReservedParameters.STATEID));
		}

		assertEquals("five", forms.get(0).getName());
		response = forms.get(0).submit();
		assertEquals("one", response.xpathString("//div[@id='id_one']/text()").trim());
		assertEquals("two", response.xpathString("//div[@id='id_two']/text()").trim());
		assertEquals("three", response.xpathString("//div[@id='id_three']/text()").trim());
		assertEquals("four", response.xpathString("//div[@id='id_four']/text()").trim());
		assertEquals("five", response.xpathString("//div[@id='id_five']/text()").trim());
		assertEquals("six", response.xpathString("//div[@id='id_six']/text()").trim());
		assertEquals("seven", response.xpathString("//div[@id='id_seven']/text()").trim());
		forms = response.getParsedHtml().getForms();
		assertEquals(0, forms.size());
	}

	public void testErrorHandlingHandler()
	throws Exception
	{
		Site site = new SiteBuilder("site/embedding.xml").getSite();

		MockConversation conversation = new MockConversation(site);
		MockResponse response = conversation.doRequest("/errorhandling/handler");
		String txt = response.getText();
		assertEquals("The element \".Erroneous\n" + "This is an error.\n" + "java.lang.RuntimeException\" is being embedded.", txt);
	}

	public void testErrorHandlingNoHandler()
	throws Exception
	{
		Site site = new SiteBuilder("site/embedding.xml").getSite();

		MockConversation conversation = new MockConversation(site);
		try
		{
			conversation.doRequest("/errorhandling/nohandler");
			fail("Expected exception");
		}
		catch (RuntimeException e)
		{
			assertEquals("This is an error.", e.getMessage());
		}
	}
}
