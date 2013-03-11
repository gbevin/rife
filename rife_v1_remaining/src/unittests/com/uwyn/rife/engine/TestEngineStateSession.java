/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestEngineStateSession.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import java.io.File;

import com.meterware.httpunit.*;
import com.uwyn.rife.TestCaseServerside;
import com.uwyn.rife.tools.FileUtils;

public class TestEngineStateSession extends TestCaseServerside
{
	public TestEngineStateSession(int siteType, String name)
	{
		super(siteType, name);
	}

	public void testSubmissionInputsPreserved()
	throws Exception
	{
		setupSite("site/statesession.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	submission_link = null;
		WebForm submission_form = null;

		request = new GetMethodWebRequest("http://localhost:8181/submissions/inputspreserved");
		request.setParameter("input1", "submission1input1value");
		request.setParameter("input4", "submission1input4value");
		try
		{
			response = conversation.getResponse(request);
		}
		catch (Throwable e)
		{getLogSink().getInternalException().printStackTrace();}
		assertTrue(isSessionResponse(response));
		submission_link = response.getLinkWith("submission1");
		assertTrue(isSessionLink(submission_link));
		submission_link.click();
		response = conversation.getCurrentPage();

		assertEquals("thevalue"+
			"submission1input1value"+
			"null"+
			"input3default", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/submissions/inputspreserved");
		request.setParameter("input1", "submission2input1value");
		request.setParameter("input4", "submission2input4value");
		response = conversation.getResponse(request);
		submission_form = response.getForms()[0];
		assertTrue(isSessionForm(submission_form));
		submission_form.setParameter("login", "gbevin2");
		response = submission_form.submit();
		submission_form = response.getForms()[0];
		assertTrue(isSessionForm(submission_form));
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
		assertTrue(isSessionForm(submission_form));
		response = submission_form.submit();
		submission_form = response.getForms()[1];
		assertTrue(isSessionForm(submission_form));
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
		assertTrue(isSessionLink(submission_link));
		submission_link.click();
		response = conversation.getCurrentPage();

		assertEquals("submission4"+
			"submission4input1value"+
			"null"+
			"input3default", response.getText());
	}

	public void testFileUploadSingleParamMultipleFiles()
	throws Exception
	{
		setupSite("site/statesession.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest	request = null;
		WebResponse	response = null;
		WebForm		form = null;

		request = new GetMethodWebRequest("http://localhost:8181/fileupload/simple");
		response = conversation.getResponse(request);
		assertTrue(isSessionResponse(response));
		form = response.getForms()[0];
		assertTrue(isSessionForm(form));

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

	public void testExistsDirectlink()
	throws Exception
	{
		setupSite("site/statesession.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/exits/directlink/source");
		WebResponse response = conversation.getResponse(request);
		assertTrue(isSessionResponse(response));
		WebLink	direct_link = response.getLinkWith("direct link");
		assertTrue(isSessionLink(direct_link));
		direct_link.click();
		response = conversation.getCurrentPage();
		assertEquals("this isgreat", response.getText());
	}
	
	public void testExitsGeneratedUrl()
	throws Exception
	{
		setupSite("site/statesession.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/exits/generatedurl/source");
		response = conversation.getResponse(request);
		assertTrue(isSessionResponse(response));
		WebLink	exit1_link = response.getLinkWith("exit1");
		assertTrue(isSessionLink(exit1_link));
		response = exit1_link.click();
		assertEquals("the first,the second", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/exits/generatedurl/source");
		response = conversation.getResponse(request);
		WebLink	exit2_link = response.getLinkWith("exit2");
		assertTrue(isSessionLink(exit2_link));
		response = exit2_link.click();
		assertEquals("the second,the third", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/exits/generatedurl/source?switch=overridden");
		response = conversation.getResponse(request);
		WebLink	exit1_link_overridden = response.getLinkWith("exit1");
		assertTrue(isSessionLink(exit1_link_overridden));
		response = exit1_link_overridden.click();
		assertEquals("the overridden first,the second", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/exits/generatedurl/source?switch=overridden");
		response = conversation.getResponse(request);
		WebLink	exit2_link_overridden = response.getLinkWith("exit2");
		assertTrue(isSessionLink(exit2_link_overridden));
		response = exit2_link_overridden.click();
		assertEquals("the second,the overridden third", response.getText());
	}

	public void testExistsDirectlinkQuerystate()
	throws Exception
	{
		setupSite("site/statesession.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/exits/directlink/source/querystate");
		WebResponse response = conversation.getResponse(request);
		assertTrue(isSessionResponse(response));
		WebLink	direct_link = response.getLinkWith("direct link");
		assertFalse(isSessionLink(direct_link));
		direct_link.click();
		response = conversation.getCurrentPage();
		assertEquals("this isgreat", response.getText());
	}

	public void testSubmissionSubsite()
	throws Exception
	{
		setupSite("site/statesession.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/subsite/submission");
		WebResponse response = conversation.getResponse(request);
		assertTrue(isSessionResponse(response));
		WebForm submission_form = response.getForms()[0];
		assertTrue(isSessionForm(submission_form));
		submission_form.setParameter("login", "gbevin2");
		submission_form.setParameter("password", "oncemoremypassword");
		response = submission_form.submit();

		assertEquals("gbevin2,oncemoremypassword", response.getText());
	}
	
	private boolean isSessionResponse(WebResponse response)
	{
		return 1 == response.getNewCookieNames().length &&
			"JSESSIONID".equals(response.getNewCookieNames()[0]);
	}
	
	private boolean isSessionLink(WebLink link)
	{
		return link.getURLString().indexOf(ReservedParameters.STATEID) != -1;
	}
	
	private boolean isSessionForm(WebForm form)
	{
		return form.hasParameterNamed(ReservedParameters.STATEID);
	}
}
