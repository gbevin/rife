/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestEngineSubsites.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.uwyn.rife.TestCaseServerside;
import java.net.InetAddress;

public class TestEngineSubsites extends TestCaseServerside
{
	public TestEngineSubsites(int siteType, String name)
	{
		super(siteType, name);
	}

	public void testGlobalVarsOverriddenBySubsite()
	throws Exception
	{
		setupSite("site/subsites.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/globalvar/overridden/globalvar");
		response = conversation.getResponse(request);
		assertEquals("parent1", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/globalvar/overridden/globalvar");
		request.setParameter("globalvar1", "request value1");
		response = conversation.getResponse(request);
		assertEquals("request value1|subsite value2|", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/globalvar/overridden/globalvar");
		request.setParameter("globalvar1", "request value1");
		request.setParameter("globalvar2", "request value2");
		request.setParameter("globalvar3", "request value3");
		response = conversation.getResponse(request);
		assertEquals("request value1|request value2|request value3", response.getText());
	}

	public void testGlobalVarsDefaultTriggeredChild()
	throws Exception
	{
		setupSite("site/subsites.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/globalvar/defaulttriggeredchild/globalvar");
		response = conversation.getResponse(request);
		assertEquals("default value1|default value2|subsite value3", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/globalvar/defaulttriggeredchild/globalvar");
		request.setParameter("globalvar1", "request value1");
		request.setParameter("globalvar2", "request value2");
		request.setParameter("globalvar3", "request value3");
		response = conversation.getResponse(request);
		assertEquals("request value1|request value2|request value3", response.getText());
	}

	public void testGlobalVarsStraightToSubsite()
	throws Exception
	{
		setupSite("site/subsites.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/globalvar/straight/globalvar");
		response = conversation.getResponse(request);
		assertEquals("default value1|default value2||subsite value4", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/globalvar/straight/globalvar");
		request.setParameter("globalvar1", "request value1");
		request.setParameter("globalvar2", "request value2");
		request.setParameter("globalvar3", "request value3");
		request.setParameter("globalvar4", "request value4");
		response = conversation.getResponse(request);
		assertEquals("request value1|request value2|request value3|request value4", response.getText());
	}

	public void testGlobalDestDataLinking()
	throws Exception
	{
		setupSite("site/subsites.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		// here data is provided to the destination through the outputs of the source
		request = new GetMethodWebRequest("http://localhost:8181/globalexit/isolation/globaldestsource");
		request.setParameter("globalvar1", "request value1");
		request.setParameter("sourceinput1", "request value1 dest1");
		response = conversation.getResponse(request);
		assertEquals("globaldest1-request value1|default value2|-request value1 dest1|default input value 2|default output value 3", response.getText());

		// no data is provided at all to the destination and thus only global vars are available
		request = new GetMethodWebRequest("http://localhost:8181/globalexit/isolation/globaldestsource");
		request.setParameter("globalvar1", "request value1");
		request.setParameter("sourceinput1", "request value1 dest2");
		response = conversation.getResponse(request);
		assertEquals("globaldest2-request value1|default value2|-||", response.getText());
	}

	/**
	 * Test a global exit that links to a subsite with an arrival element
	 */
	public void testGlobalDestArrival()
	throws Exception
	{
		setupSite("site/subsites.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		// toplevel site
		request = new GetMethodWebRequest("http://localhost:8181/globalexit/arrival");
		response = conversation.getResponse(request);
		assertEquals(
			"null"+
			"null"+
			"/globalexit/arrival"+
			"arrival", response.getText());

		// subsite
		request = new GetMethodWebRequest("http://localhost:8181/globalexit/straight/globaldestsource");
		request.setParameter("exitselector", "arrival");
		response = conversation.getResponse(request);
		assertEquals(
			"null"+
			"null"+
			"/globalexit/straight/globaldestsource"+
			"arrival", response.getText());
	}

	/**
	 * Tests a global exit that's only defined in the subsite and not in the parent
	 **/
	public void testGlobalDestIsolation()
	throws Exception
	{
		setupSite("site/subsites.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/globalexit/isolation/globaldestsource");
		request.setParameter("globalvar1", "request value1");
		request.setParameter("sourceinput1", "request value1 dest3");
		response = conversation.getResponse(request);
		assertEquals("globaldesttargetfirst-request value1|subsite value2|-||", response.getText());
	}

	// subsites aren't able to override the global exit target by defining elements with the same id
	// globalvar is thus also not passed along since the target element lies in a different scope
	public void testGlobalDestRemainsInitialDefinition()
	throws Exception
	{
		setupSite("site/subsites.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/globalexit/overloading/globaldestsource");
		request.setParameter("exitselector", "1");
		response = conversation.getResponse(request);
		assertEquals("globaldest1-default value1|default value2|-||", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/globalexit/overloading/globaldestsource");
		request.setParameter("exitselector", "2");
		response = conversation.getResponse(request);
		assertEquals("globaldest2-default value1|default value2|-||", response.getText());
	}

	public void testGlobalDestStraightToSubsite()
	throws Exception
	{
		setupSite("site/subsites.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/globalexit/straight/globaldestsource");
		request.setParameter("exitselector", "1");
		response = conversation.getResponse(request);
		assertEquals("globaldest1-default value1|default value2|-||", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/globalexit/straight/globaldestsource");
		request.setParameter("exitselector", "2");
		response = conversation.getResponse(request);
		assertEquals("globaldest2-default value1|default value2|-||", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/globalexit/straight/globaldestsource");
		request.setParameter("exitselector", "3");
		response = conversation.getResponse(request);
		assertEquals("subsite3-globaldesttarget1-default value1|default value2|", response.getText());
	}

	public void testAbsoluteExit()
	throws Exception
	{
		setupSite("site/subsites.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		// globalvar4 is not passed on due to the different scopes and thus the default value is used
		request = new GetMethodWebRequest("http://localhost:8181/absolute");
		response = conversation.getResponse(request);
		assertEquals("absolute_target:value1absolute|default value2|value3absolute|subsite value4", response.getText());
	}

	public void testInheritanceSingle()
	throws Exception
	{
		setupSite("site/subsites.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		// single inheritance
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/single/targetchild");
		response = conversation.getResponse(request);
		assertEquals("parent1", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/inheritance/single/targetchild");
		request.setParameter("globalvar1", "request value1");
		response = conversation.getResponse(request);
		assertEquals("targetchild", response.getText());
	}

	public void testInheritanceStack()
	throws Exception
	{
		setupSite("site/subsites.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		// inheritance stack
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/stack/targetchild");
		response = conversation.getResponse(request);
		assertEquals("parent1", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/inheritance/stack/targetchild");
		request.setParameter("globalvar1", "request value1");
		response = conversation.getResponse(request);
		assertEquals("inheritance parent 1", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/inheritance/stack/targetchild");
		request.setParameter("globalvar1", "request value1");
		request.setParameter("globalvar_inheritance1", "request value");
		response = conversation.getResponse(request);
		assertEquals("targetchild", response.getText());
	}

	public void testInheritanceElementWithInheritanceStack()
	throws Exception
	{
		setupSite("site/subsites.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		// inheritance from element that has its own inheritance stack
		request = new GetMethodWebRequest("http://localhost:8181/inheritance/repeatedstack/targetchild");
		request.setParameter("globalvar1", "request value1");
		response = conversation.getResponse(request);
		assertEquals("inheritance parent 2", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/inheritance/repeatedstack/targetchild");
		request.setParameter("globalvar1", "request value1");
		request.setParameter("globalvar_inheritance2", "request value");
		response = conversation.getResponse(request);
		assertEquals("targetchild", response.getText());
	}

	public void testInheritanceParentSubsiteChildElement()
	throws Exception
	{
		setupSite("site/subsites.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/inheritance/parent/childelement");
		response = conversation.getResponse(request);
		assertEquals("parent1", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/inheritance/parent/childelement");
		request.setParameter("globalvar1", "request value1");
		response = conversation.getResponse(request);
		assertEquals("parent subsite arrival", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/inheritance/parent/childelement");
		request.setParameter("globalvar1", "request value1");
		request.setParameter("parent_subsite_trigger", "request value");
		response = conversation.getResponse(request);
		assertEquals("targetchild", response.getText());
	}

	public void testInheritanceParentSubsiteChildSubsite()
	throws Exception
	{
		setupSite("site/subsites.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/inheritance/parent/childsubsite/targetchild");
		response = conversation.getResponse(request);
		assertEquals("parent1", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/inheritance/parent/childsubsite/targetchild");
		request.setParameter("globalvar1", "request value1");
		response = conversation.getResponse(request);
		assertEquals("parent subsite arrival", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/inheritance/parent/childsubsite/targetchild");
		request.setParameter("globalvar1", "request value1");
		request.setParameter("parent_subsite_trigger", "request value");
		response = conversation.getResponse(request);
		assertEquals("targetchild", response.getText());
	}

	public void testArrivalSimple()
	throws Exception
	{
		setupSite("site/subsites.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181");
		response = conversation.getResponse(request);
		assertEquals("output value 1output value 2"+
					 "/"+
					 "arrival", response.getText());
	}

	public void testArrivalSimpleRedirect()
	throws Exception
	{
		setupSite("site/subsites.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/arrival_redirect");
		response = conversation.getResponse(request);
		assertEquals("output value 1output value 2"+
					 "/subsite/arrival_redirect/arrival"+
					 "arrival", response.getText());
	}

	public void testArrivalNourlRedirect()
	throws Exception
	{
		setupSite("site/subsites.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/arrival_redirect_nourl");
		response = conversation.getResponse(request);
		assertEquals("output value 1output value 2"+
					 "/subsite/arrival_redirect_nourl/"+
					 "arrival", response.getText());
	}

	public void testArrivalExitToSubsite()
	throws Exception
	{
		setupSite("site/subsites.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		// element with flowlink to subsite (and thus arrival element)
		request = new GetMethodWebRequest("http://localhost:8181/arrival_target_subsite");
		response = conversation.getResponse(request);
		assertEquals(
			"output value 1"+
			"output value 2"+
			"/arrival_target_subsite"+
			"arrival", response.getText());

		// direct access of subsite's url prefix (and thus arrival element)
		request = new GetMethodWebRequest("http://localhost:8181/subsite/arrival");
		request.setParameter("input1", "output value 1a");
		request.setParameter("input2", "output value 2a");
		response = conversation.getResponse(request);
		assertEquals(
			"output value 1a"+
			"output value 2a"+
			"/subsite/arrival"+
			"arrival", response.getText());

		// direct access of arrival element
		request = new GetMethodWebRequest("http://localhost:8181/subsite/arrival/arrival");
		request.setParameter("input1", "output value 1b");
		request.setParameter("input2", "output value 2b");
		response = conversation.getResponse(request);
		assertEquals(
			"output value 1b"+
			"output value 2b"+
			"/subsite/arrival/arrival"+
			"arrival", response.getText());
	}

	public void testArrivalInheritance()
	throws Exception
	{
		setupSite("site/subsites.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		// element with flowlink to subsite (and thus arrival element)
		// without a child trigger
		request = new GetMethodWebRequest("http://localhost:8181/arrival_inheritance");
		response = conversation.getResponse(request);
		assertEquals("parent1", response.getText());

		// element with flowlink to subsite (and thus arrival element)
		// with a valid child trigger
		request = new GetMethodWebRequest("http://localhost:8181/arrival_inheritance");
		request.setParameter("globalvar1", "request value1");
		response = conversation.getResponse(request);
		assertEquals(
			"output value 1"+
			"output value 2"+
			"/arrival_inheritance"+
			"arrival", response.getText());

		// direct access of subsite's url prefix (and thus arrival element)
		// without a child trigger
		request = new GetMethodWebRequest("http://localhost:8181/subsite/arrival_inheritance");
		request.setParameter("input1", "output value 1a");
		request.setParameter("input2", "output value 2a");
		response = conversation.getResponse(request);
		assertEquals("parent1", response.getText());

		// direct access of subsite's url prefix (and thus arrival element)
		// with a valid child trigger
		request = new GetMethodWebRequest("http://localhost:8181/subsite/arrival_inheritance");
		request.setParameter("globalvar1", "request value1");
		request.setParameter("input1", "output value 1a");
		request.setParameter("input2", "output value 2a");
		response = conversation.getResponse(request);
		assertEquals(
			"output value 1a"+
			"output value 2a"+
			"/subsite/arrival_inheritance"+
			"arrival", response.getText());

		// direct access of arrival element
		// without a child trigger
		request = new GetMethodWebRequest("http://localhost:8181/subsite/arrival_inheritance/arrival");
		request.setParameter("input1", "output value 1a");
		request.setParameter("input2", "output value 2a");
		response = conversation.getResponse(request);
		assertEquals("parent1", response.getText());

		// direct access of arrival element
		// with a valid child trigger
		request = new GetMethodWebRequest("http://localhost:8181/subsite/arrival_inheritance/arrival");
		request.setParameter("globalvar1", "request value1");
		request.setParameter("input1", "output value 1a");
		request.setParameter("input2", "output value 2a");
		response = conversation.getResponse(request);
		assertEquals(
			"output value 1a"+
			"output value 2a"+
			"/subsite/arrival_inheritance/arrival"+
			"arrival", response.getText());
	}

	public void testArrivalNoUrl()
	throws Exception
	{
		setupSite("site/subsites.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		// element with flowlink to subsite (and thus arrival element)
		request = new GetMethodWebRequest("http://localhost:8181/arrival_nourl");
		response = conversation.getResponse(request);
		assertEquals(
			"output value 1"+
			"output value 2"+
			"/arrival_nourl"+
			"arrival", response.getText());

		// direct access of subsite's url prefix, since the arrival element has
		// no url, the site url prefix doesn't refer to the actual arrival element
		request = new GetMethodWebRequest("http://localhost:8181/subsite/arrival_nourl");
		request.setParameter("input1", "output value 1a");
		request.setParameter("input2", "output value 2a");
		response = conversation.getResponse(request);
		assertEquals(
			"output value 1a"+
			"output value 2a"+
			"/subsite/arrival_nourl"+
			"arrival", response.getText());
	}

	public void testDeparture()
	throws Exception
	{
		setupSite("site/subsites.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/departure/source");
		request.setParameter("globalvar2", "request value1");
		request.setParameter("globalvar3", "request value2");
		request.setParameter("globalvar_inheritance1", "request value3");
		request.setParameter("globalvar_inheritance2", "request value4");
		request.setParameter("globalvar_inheritance3", "request value5");
		response = conversation.getResponse(request);
		assertEquals(
			"default value1"+
			"request value1"+
			"request value2"+
			"source value1"+
			"some"+
			"another value"+
			"null"+
			"set by source"+
			"some"+
			"null", response.getText());
	}

	public void testArrivalStraightToSubsite()
	throws Exception
	{
		setupSite("site/subsites.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		// access arrival directly
		request = new GetMethodWebRequest("http://localhost:8181/subsite/arrival_subsite");
		request.setParameter("input1", "input value 1");
		request.setParameter("input2", "input value 2");

		response = conversation.getResponse(request);

		assertEquals(
			"input value 1"+
			"input value 2"+
			"/subsite/arrival_subsite"+
			"arrival", response.getText());

		// access arrival directly with additional slash
		request = new GetMethodWebRequest("http://localhost:8181/subsite/arrival_subsite/");
		request.setParameter("input1", "input value 1");
		request.setParameter("input2", "input value 2");

		response = conversation.getResponse(request);

		assertEquals(
			"input value 1"+
			"input value 2"+
			"/subsite/arrival_subsite/"+
			"arrival", response.getText());

		// direct access of arrival element
		request = new GetMethodWebRequest("http://localhost:8181/subsite/arrival_subsite/arrival");
		request.setParameter("input1", "input value 1b");
		request.setParameter("input2", "input value 2b");
		response = conversation.getResponse(request);
		assertEquals(
			"input value 1b"+
			"input value 2b"+
			"/subsite/arrival_subsite/arrival"+
			"arrival", response.getText());
	}

	public void testArrivalStraightToSubsiteTrailingSlash()
	throws Exception
	{
		setupSite("site/subsites.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		// access arrival directly
		request = new GetMethodWebRequest("http://localhost:8181/subsite/arrival_subsite_slash/");
		request.setParameter("input1", "input value 1");
		request.setParameter("input2", "input value 2");

		response = conversation.getResponse(request);

		assertEquals(
			"input value 1"+
			"input value 2"+
			"/subsite/arrival_subsite_slash/"+
			"arrival", response.getText());

		// access arrival directly without the slash
		request = new GetMethodWebRequest("http://localhost:8181/subsite/arrival_subsite_slash");
		request.setParameter("input1", "input value 1");
		request.setParameter("input2", "input value 2");

		response = conversation.getResponse(request);

		assertEquals(
			"input value 1"+
			"input value 2"+
			"/subsite/arrival_subsite_slash"+
			"arrival", response.getText());

		// direct access of arrival element
		request = new GetMethodWebRequest("http://localhost:8181/subsite/arrival_subsite_slash/arrival");
		request.setParameter("input1", "input value 1b");
		request.setParameter("input2", "input value 2b");
		response = conversation.getResponse(request);
		assertEquals(
			"input value 1b"+
			"input value 2b"+
			"/subsite/arrival_subsite_slash/arrival"+
			"arrival", response.getText());
	}

	public void testSubsiteMultipleSlash()
	throws Exception
	{
		setupSite("site/subsites.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		// access arrival directly
		request = new GetMethodWebRequest("http://localhost:8181/subsite/multiple_slash/simple");

		response = conversation.getResponse(request);

        // Get the host name
		String hostname = InetAddress.getByName("127.0.0.1").getHostName();
		assertEquals("Just some text 127.0.0.1:"+hostname+":.MULTIPLE_SLASH.simple_html:", response.getText());
	}

	public void testSubsiteNoFile()
	throws Exception
	{
		setupSite("site/subsites.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		// access arrival directly
		request = new GetMethodWebRequest("http://localhost:8181/nofile/simple");

		response = conversation.getResponse(request);

        // Get the host name
		String hostname = InetAddress.getByName("127.0.0.1").getHostName();
		assertEquals("Just some text 127.0.0.1:"+hostname+":.NOFILE.SIMPLE:", response.getText());
	}
	
	public void testArrivalDefaultTemplate()
	throws Exception
	{
		setupSite("site/subsites.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		request = new GetMethodWebRequest("http://localhost:8181/subsite/arrival_default_template");
		response = conversation.getResponse(request);
		assertEquals("arrival default template\n"+
					 ".ARRIVAL_DEFAULT_TEMPLATE.\n"+
					 ".ARRIVAL_DEFAULT_TEMPLATE.DefaultTemplate\n"+
					 "/subsite/arrival_default_template\n", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/subsite/arrival_default_template/arrival");
		response = conversation.getResponse(request);
		assertEquals("arrival default template\n"+
					 ".ARRIVAL_DEFAULT_TEMPLATE.DefaultTemplate\n"+
					 ".ARRIVAL_DEFAULT_TEMPLATE.DefaultTemplate\n"+
					 "/subsite/arrival_default_template/arrival\n", response.getText());
	}
	
	public void testAnnotationsDestClassIdPrefix()
	throws Exception
	{
		setupSite("site/subsites.xml");
		
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		
		// access arrival directly
		request = new GetMethodWebRequest("http://localhost:8181/annotationssource");
		response = conversation.getResponse(request);
		assertEquals("output value 1:output value 2", response.getText());
	}
}

