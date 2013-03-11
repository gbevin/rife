/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestAnnotations2ElementInfo.java 3961 2008-07-11 11:35:59Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.annotations.*;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.engine.exceptions.PropertyNameMismatchErrorException;
import com.uwyn.rife.engine.testelements.annotations.*;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import junit.framework.TestCase;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class TestAnnotations2ElementInfo extends TestCase
{
	public TestAnnotations2ElementInfo(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
		SiteBuilder			site_builder = new SiteBuilder("test", ResourceFinderClasspath.getInstance());
		ElementInfoBuilder	elementinfo_builder = site_builder.enterElement(ElementInfoProcessorFactory.ANNOTATIONS_IDENTIFIER+":"+Simple.class.getName());

		assertNotNull(elementinfo_builder);
	}
	
	public void testPrioritizedMethod()
	throws Exception
	{
		Object test_object = new Object() {
			@SuppressWarnings("unused")
			public void eee() {}
			@SuppressWarnings("unused")
			public void bbb() {}
			@SuppressWarnings("unused")
			public void ddd() {}
			@SuppressWarnings("unused")
			public void ccc() {}
			@SuppressWarnings("unused")
			public void aaa() {}
		};
		
		Class test_class = test_object.getClass();
		
		Method aaa = test_class.getDeclaredMethod("aaa", new Class[] {});
		Method bbb = test_class.getDeclaredMethod("bbb", new Class[] {});
		Method ccc = test_class.getDeclaredMethod("ccc", new Class[] {});
		Method ddd = test_class.getDeclaredMethod("ddd", new Class[] {});
		Method eee = test_class.getDeclaredMethod("eee", new Class[] {});
		
		PrioritizedMethod prior_aaa1 = new PrioritizedMethod(aaa, null);
		PrioritizedMethod prior_aaa2 = new PrioritizedMethod(aaa, null);
		PrioritizedMethod prior_aaa3 = new PrioritizedMethod(aaa, new int[] {0, 3});
		PrioritizedMethod prior_bbb = new PrioritizedMethod(bbb, new int[] {0});
		PrioritizedMethod prior_ccc = new PrioritizedMethod(ccc, new int[] {0, 3});
		PrioritizedMethod prior_ddd = new PrioritizedMethod(ddd, new int[] {1, 4});
		PrioritizedMethod prior_eee = new PrioritizedMethod(eee, new int[] {1, 4, 0});
		
		assertTrue(prior_aaa1.equals(prior_aaa1));
		assertTrue(prior_aaa1.equals(prior_aaa2));
		assertFalse(prior_aaa1.equals(prior_aaa3));
		assertTrue(prior_aaa2.equals(prior_aaa1));
		assertTrue(prior_aaa2.equals(prior_aaa2));
		assertFalse(prior_aaa2.equals(prior_aaa3));
		assertFalse(prior_aaa3.equals(prior_aaa1));
		assertFalse(prior_aaa3.equals(prior_aaa2));
		assertTrue(prior_aaa3.equals(prior_aaa3));
		
		assertTrue(prior_aaa1.compareTo(prior_aaa1) == 0);
		assertTrue(prior_aaa1.compareTo(prior_aaa2) == 0);
		assertTrue(prior_aaa1.compareTo(prior_aaa3) < 0);
		assertTrue(prior_aaa1.compareTo(prior_bbb) < 0);
		assertTrue(prior_aaa1.compareTo(prior_ccc) < 0);
		assertTrue(prior_aaa1.compareTo(prior_ddd) < 0);
		assertTrue(prior_aaa1.compareTo(prior_eee) < 0);
		
		assertTrue(prior_aaa2.compareTo(prior_aaa1) == 0);
		assertTrue(prior_aaa2.compareTo(prior_aaa2) == 0);
		assertTrue(prior_aaa2.compareTo(prior_aaa3) < 0);
		assertTrue(prior_aaa2.compareTo(prior_bbb) < 0);
		assertTrue(prior_aaa2.compareTo(prior_ccc) < 0);
		assertTrue(prior_aaa2.compareTo(prior_ddd) < 0);
		assertTrue(prior_aaa2.compareTo(prior_eee) < 0);
		
		assertTrue(prior_aaa3.compareTo(prior_aaa1) > 0);
		assertTrue(prior_aaa3.compareTo(prior_aaa2) > 0);
		assertTrue(prior_aaa3.compareTo(prior_aaa3) == 0);
		assertTrue(prior_aaa3.compareTo(prior_bbb) > 0);
		assertTrue(prior_aaa3.compareTo(prior_ccc) < 0);
		assertTrue(prior_aaa3.compareTo(prior_ddd) < 0);
		assertTrue(prior_aaa3.compareTo(prior_eee) < 0);
		
		assertTrue(prior_bbb.compareTo(prior_aaa1) > 0);
		assertTrue(prior_bbb.compareTo(prior_aaa2) > 0);
		assertTrue(prior_bbb.compareTo(prior_aaa3) < 0);
		assertTrue(prior_bbb.compareTo(prior_bbb) == 0);
		assertTrue(prior_bbb.compareTo(prior_ccc) < 0);
		assertTrue(prior_bbb.compareTo(prior_ddd) < 0);
		assertTrue(prior_bbb.compareTo(prior_eee) < 0);
		
		assertTrue(prior_ccc.compareTo(prior_aaa1) > 0);
		assertTrue(prior_ccc.compareTo(prior_aaa2) > 0);
		assertTrue(prior_ccc.compareTo(prior_aaa3) > 0);
		assertTrue(prior_ccc.compareTo(prior_bbb) > 0);
		assertTrue(prior_ccc.compareTo(prior_ccc) == 0);
		assertTrue(prior_ccc.compareTo(prior_ddd) < 0);
		assertTrue(prior_ccc.compareTo(prior_eee) < 0);
		
		assertTrue(prior_ddd.compareTo(prior_aaa1) > 0);
		assertTrue(prior_ddd.compareTo(prior_aaa2) > 0);
		assertTrue(prior_ddd.compareTo(prior_aaa3) > 0);
		assertTrue(prior_ddd.compareTo(prior_bbb) > 0);
		assertTrue(prior_ddd.compareTo(prior_ccc) > 0);
		assertTrue(prior_ddd.compareTo(prior_ddd) == 0);
		assertTrue(prior_ddd.compareTo(prior_eee) < 0);
		
		assertTrue(prior_eee.compareTo(prior_aaa1) > 0);
		assertTrue(prior_eee.compareTo(prior_aaa2) > 0);
		assertTrue(prior_eee.compareTo(prior_aaa3) > 0);
		assertTrue(prior_eee.compareTo(prior_bbb) > 0);
		assertTrue(prior_eee.compareTo(prior_ccc) > 0);
		assertTrue(prior_eee.compareTo(prior_ddd) > 0);
		assertTrue(prior_eee.compareTo(prior_eee) == 0);
	}

	public void testParser()
	{
		SiteBuilder			site_builder = new SiteBuilder("test", ResourceFinderClasspath.getInstance());
		ElementInfoBuilder	elementinfo_builder = site_builder.enterElement(ElementInfoProcessorFactory.ANNOTATIONS_IDENTIFIER+":"+Simple.class.getName());

		elementinfo_builder.process();

		ElementInfo elementinfo = elementinfo_builder.createElementInfo(new LinkedHashMap<String, GlobalExit>(), new LinkedHashMap<String, GlobalVar>(), new LinkedHashMap<String, String>(), new LinkedHashMap<String, BeanDeclaration>(), new ArrayList<ErrorHandler>());

		assertEquals(elementinfo.getInputNames().size(), 28);
		assertTrue(elementinfo.containsInput("input1"));
		assertTrue(elementinfo.containsInput("input2"));
		assertTrue(elementinfo.containsInput("input3"));
		assertTrue(elementinfo.containsInput("string1"));
		assertTrue(elementinfo.containsInput("string2"));
		assertTrue(elementinfo.containsInput("string3"));
		assertTrue(elementinfo.containsInput("enum4"));
		assertTrue(elementinfo.containsInput("date5"));
		assertTrue(elementinfo.containsInput("string6"));
		assertTrue(elementinfo.containsInput("string7"));
		assertTrue(elementinfo.containsInput("string8"));
		assertTrue(elementinfo.containsInput("prefix_string1"));
		assertTrue(elementinfo.containsInput("prefix_string2"));
		assertTrue(elementinfo.containsInput("prefix_string3"));
		assertTrue(elementinfo.containsInput("prefix_enum4"));
		assertTrue(elementinfo.containsInput("prefix_date5"));
		assertTrue(elementinfo.containsInput("prefixgroup_enum"));
		assertTrue(elementinfo.containsInput("prefixgroup_string"));
		assertTrue(elementinfo.containsInput("prefixgroup_int"));
		assertTrue(elementinfo.containsInput("prefixgroup_longObject"));
		assertTrue(elementinfo.containsInput("prefixgroup_short"));
		assertTrue(elementinfo.containsInput("enum"));
		assertTrue(elementinfo.containsInput("string"));
		assertTrue(elementinfo.containsInput("int"));
		assertTrue(elementinfo.containsInput("longObject"));
		assertTrue(elementinfo.containsInput("short"));
		assertTrue(elementinfo.containsInput("key1"));
		assertTrue(elementinfo.containsInput("key2"));

		assertEquals(elementinfo.getOutputNames().size(), 27);
		assertTrue(elementinfo.containsOutput("output1"));
		assertTrue(elementinfo.containsOutput("output2"));
		assertTrue(elementinfo.containsOutput("output3"));
		assertTrue(elementinfo.containsOutput("output4"));
		assertTrue(elementinfo.containsOutput("string1"));
		assertTrue(elementinfo.containsOutput("string2"));
		assertTrue(elementinfo.containsOutput("string3"));
		assertTrue(elementinfo.containsOutput("enum4"));
		assertTrue(elementinfo.containsOutput("date5"));
		assertTrue(elementinfo.containsOutput("string6"));
		assertTrue(elementinfo.containsOutput("string7"));
		assertTrue(elementinfo.containsOutput("string8"));
		assertTrue(elementinfo.containsOutput("prefix_enum4"));
		assertTrue(elementinfo.containsOutput("prefix_date5"));
		assertTrue(elementinfo.containsOutput("prefix_string6"));
		assertTrue(elementinfo.containsOutput("prefix_string7"));
		assertTrue(elementinfo.containsOutput("prefix_string8"));
		assertTrue(elementinfo.containsOutput("prefixgroup_enum"));
		assertTrue(elementinfo.containsOutput("prefixgroup_string"));
		assertTrue(elementinfo.containsOutput("prefixgroup_int"));
		assertTrue(elementinfo.containsOutput("prefixgroup_longObject"));
		assertTrue(elementinfo.containsOutput("prefixgroup_short"));
		assertTrue(elementinfo.containsOutput("enum"));
		assertTrue(elementinfo.containsOutput("string"));
		assertTrue(elementinfo.containsOutput("int"));
		assertTrue(elementinfo.containsOutput("longObject"));
		assertTrue(elementinfo.containsOutput("short"));

		assertEquals(elementinfo.getIncookieNames().size(), 2);
		assertTrue(elementinfo.containsIncookie("incookie1"));
		assertTrue(elementinfo.containsIncookie("incookie2"));

		assertEquals(elementinfo.getOutcookieNames().size(), 4);
		assertTrue(elementinfo.containsOutcookie("outcookie1"));
		assertTrue(elementinfo.containsOutcookie("outcookie2"));
		assertTrue(elementinfo.containsOutcookie("outcookie3"));
		assertTrue(elementinfo.containsOutcookie("outcookie4"));

		assertEquals(elementinfo.getNamedInbeanNames().size(), 2);
		assertTrue(elementinfo.containsNamedInbean("inbean1"));
		assertEquals(elementinfo.getNamedInbeanInfo("inbean1").getClassname(), "com.uwyn.rife.engine.testelements.exits.BeanImpl1");
		assertEquals(elementinfo.getNamedInbeanInfo("inbean1").getPrefix(), null);
		assertEquals(elementinfo.getNamedInbeanInfo("inbean1").getGroupName(), null);
		assertTrue(elementinfo.containsNamedInbean("inbean2"));
		assertEquals(elementinfo.getNamedInbeanInfo("inbean2").getClassname(), "com.uwyn.rife.engine.testelements.submission.BeanImpl");
		assertEquals(elementinfo.getNamedInbeanInfo("inbean2").getPrefix(), "prefixgroup_");
		assertEquals(elementinfo.getNamedInbeanInfo("inbean2").getGroupName(), "somegroup");

		assertEquals(elementinfo.getNamedOutbeanNames().size(), 2);
		assertTrue(elementinfo.containsNamedOutbean("outbean1"));
		assertEquals(elementinfo.getNamedOutbeanInfo("outbean1").getClassname(), "com.uwyn.rife.engine.testelements.exits.BeanImpl2");
		assertEquals(elementinfo.getNamedOutbeanInfo("outbean1").getPrefix(), "prefix_");
		assertEquals(elementinfo.getNamedOutbeanInfo("outbean1").getGroupName(), null);
		assertTrue(elementinfo.containsNamedOutbean("outbean2"));
		assertEquals(elementinfo.getNamedOutbeanInfo("outbean2").getClassname(), "com.uwyn.rife.engine.testelements.submission.BeanImpl");
		assertEquals(elementinfo.getNamedOutbeanInfo("outbean2").getPrefix(), null);
		assertEquals(elementinfo.getNamedOutbeanInfo("outbean2").getGroupName(), "somegroup");

		assertEquals(elementinfo.getChildTriggerNames().size(), 2);
		assertTrue(elementinfo.containsChildTrigger("input1"));
		assertTrue(elementinfo.containsChildTrigger("input2"));

		assertEquals(elementinfo.getExitNames().size(), 3);
		assertTrue(elementinfo.containsExit("exit1"));
		assertTrue(elementinfo.containsExit("exit2"));
		assertTrue(elementinfo.containsExit("exit3"));

		assertEquals(elementinfo.getSubmissionNames().size(), 3);
		assertTrue(elementinfo.containsSubmission("submission1"));
		assertTrue(elementinfo.containsSubmission("submission2"));
		assertTrue(elementinfo.containsSubmission("anotherSubmission"));
		
		assertNotNull(elementinfo.getSubmission("submission1"));
		assertNotNull(elementinfo.getSubmission("submission2"));

		assertEquals(elementinfo.getSubmission("submission1").getParameterNames().size(), 13);
		assertTrue(elementinfo.getSubmission("submission1").containsParameter("param1"));
		assertTrue(elementinfo.getSubmission("submission1").containsParameter("param2"));
		assertTrue(elementinfo.getSubmission("submission1").containsParameter("param3"));
		assertTrue(elementinfo.getSubmission("submission1").containsParameter("subm_enum"));
		assertTrue(elementinfo.getSubmission("submission1").containsParameter("subm_string"));
		assertTrue(elementinfo.getSubmission("submission1").containsParameter("subm_int"));
		assertTrue(elementinfo.getSubmission("submission1").containsParameter("subm_longObject"));
		assertTrue(elementinfo.getSubmission("submission1").containsParameter("subm_short"));
		assertTrue(elementinfo.getSubmission("submission1").containsParameter("subm_string1"));
		assertTrue(elementinfo.getSubmission("submission1").containsParameter("subm_string2"));
		assertTrue(elementinfo.getSubmission("submission1").containsParameter("subm_string3"));
		assertTrue(elementinfo.getSubmission("submission1").containsParameter("subm_enum4"));
		assertTrue(elementinfo.getSubmission("submission1").containsParameter("subm_date5"));
		assertEquals(elementinfo.getSubmission("submission1").getParameterRegexps().size(), 2);
		assertTrue(elementinfo.getSubmission("submission1").containsParameter("paramA1"));
		assertTrue(elementinfo.getSubmission("submission1").containsParameter("paramA2"));
		assertTrue(elementinfo.getSubmission("submission1").containsParameter("paramB1"));
		assertTrue(elementinfo.getSubmission("submission1").containsParameter("paramB2"));
		assertEquals(elementinfo.getSubmission("submission1").getFileNames().size(), 2);
		assertTrue(elementinfo.getSubmission("submission1").containsFile("file1"));
		assertTrue(elementinfo.getSubmission("submission1").containsFile("file2"));

		assertEquals(elementinfo.getSubmission("submission2").getParameterNames().size(), 3);
		assertTrue(elementinfo.getSubmission("submission2").containsParameter("param1"));
		assertTrue(elementinfo.getSubmission("submission2").containsParameter("param2"));
		assertTrue(elementinfo.getSubmission("submission2").containsParameter("param3"));
		assertEquals(elementinfo.getSubmission("submission2").getParameterRegexps().size(), 1);
		assertTrue(elementinfo.getSubmission("submission2").containsParameter("paramC1"));
		assertTrue(elementinfo.getSubmission("submission2").containsParameter("paramC2"));
		assertEquals(elementinfo.getSubmission("submission2").getFileNames().size(), 2);
		assertTrue(elementinfo.getSubmission("submission2").containsFile("file1"));
		assertTrue(elementinfo.getSubmission("submission2").containsFile("file2"));
		
		assertEquals(elementinfo.getSubmission("anotherSubmission").getParameterNames().size(), 1);
		assertTrue(elementinfo.getSubmission("anotherSubmission").containsParameter("param4"));
		assertEquals(elementinfo.getSubmission("anotherSubmission").getParameterRegexps().size(), 0);
		assertEquals(elementinfo.getSubmission("anotherSubmission").getFileNames().size(), 0);
	}

	public void testPropertyMismatchedIncookie()
	{
		SiteBuilder			site_builder = new SiteBuilder("test", ResourceFinderClasspath.getInstance());
		ElementInfoBuilder	elementinfo_builder = site_builder.enterElement(ElementInfoProcessorFactory.ANNOTATIONS_IDENTIFIER+":"+PropertyMismatchInCookie.class.getName());

		try
		{
			elementinfo_builder.process();
			fail("Expected exception.");
		}
		catch (EngineException e1)
		{
			if (e1.getCause() instanceof PropertyNameMismatchErrorException)
			{
				PropertyNameMismatchErrorException e2 = (PropertyNameMismatchErrorException)e1.getCause();
				assertEquals("incookie", e2.getActualPropertyName());
				assertEquals("setIncookie", e2.getMethod().getName());
				assertEquals(PropertyMismatchInCookie.class.getName(), e2.getImplementationName());
				assertSame(InCookieProperty.class, e2.getAnnotationType());
				assertEquals("badname", e2.getExpectedPropertyName());
				assertEquals("test", e2.getSiteDeclarationName());
			}
		}
	}
	
	public void testPropertyMismatchedInBean()
	{
		SiteBuilder			site_builder = new SiteBuilder("test", ResourceFinderClasspath.getInstance());
		ElementInfoBuilder	elementinfo_builder = site_builder.enterElement(ElementInfoProcessorFactory.ANNOTATIONS_IDENTIFIER+":"+PropertyMismatchInBean.class.getName());
		
		try
		{
			elementinfo_builder.process();
			fail("Expected exception.");
		}
		catch (EngineException e1)
		{
			if (e1.getCause() instanceof PropertyNameMismatchErrorException)
			{
				PropertyNameMismatchErrorException e2 = (PropertyNameMismatchErrorException)e1.getCause();
				assertEquals("inBean1", e2.getActualPropertyName());
				assertEquals("setInBean1", e2.getMethod().getName());
				assertEquals(PropertyMismatchInBean.class.getName(), e2.getImplementationName());
				assertSame(InBeanProperty.class, e2.getAnnotationType());
				assertEquals("badname", e2.getExpectedPropertyName());
				assertEquals("test", e2.getSiteDeclarationName());
			}
		}
	}
	
	public void testPropertyMismatchedInput()
	{
		SiteBuilder			site_builder = new SiteBuilder("test", ResourceFinderClasspath.getInstance());
		ElementInfoBuilder	elementinfo_builder = site_builder.enterElement(ElementInfoProcessorFactory.ANNOTATIONS_IDENTIFIER+":"+PropertyMismatchInput.class.getName());
		
		try
		{
			elementinfo_builder.process();
			fail("Expected exception.");
		}
		catch (EngineException e1)
		{
			if (e1.getCause() instanceof PropertyNameMismatchErrorException)
			{
				PropertyNameMismatchErrorException e2 = (PropertyNameMismatchErrorException)e1.getCause();
				assertEquals("input", e2.getActualPropertyName());
				assertEquals("setInput", e2.getMethod().getName());
				assertEquals(PropertyMismatchInput.class.getName(), e2.getImplementationName());
				assertSame(InputProperty.class, e2.getAnnotationType());
				assertEquals("badname", e2.getExpectedPropertyName());
				assertEquals("test", e2.getSiteDeclarationName());
			}
		}
	}
	
	public void testPropertyMismatchedParam()
	{
		SiteBuilder			site_builder = new SiteBuilder("test", ResourceFinderClasspath.getInstance());
		ElementInfoBuilder	elementinfo_builder = site_builder.enterElement(ElementInfoProcessorFactory.ANNOTATIONS_IDENTIFIER+":"+PropertyMismatchParam.class.getName());
		
		try
		{
			elementinfo_builder.process();
			fail("Expected exception.");
		}
		catch (EngineException e1)
		{
			if (e1.getCause() instanceof PropertyNameMismatchErrorException)
			{
				PropertyNameMismatchErrorException e2 = (PropertyNameMismatchErrorException)e1.getCause();
				assertEquals("param", e2.getActualPropertyName());
				assertEquals("setParam", e2.getMethod().getName());
				assertEquals(PropertyMismatchParam.class.getName(), e2.getImplementationName());
				assertSame(ParamProperty.class, e2.getAnnotationType());
				assertEquals("badname", e2.getExpectedPropertyName());
				assertEquals("test", e2.getSiteDeclarationName());
			}
		}
	}
	
	public void testPropertyMismatchedSubmissionBean()
	{
		SiteBuilder			site_builder = new SiteBuilder("test", ResourceFinderClasspath.getInstance());
		ElementInfoBuilder	elementinfo_builder = site_builder.enterElement(ElementInfoProcessorFactory.ANNOTATIONS_IDENTIFIER+":"+PropertyMismatchSubmissionBean.class.getName());
		
		try
		{
			elementinfo_builder.process();
			fail("Expected exception.");
		}
		catch (EngineException e1)
		{
			if (e1.getCause() instanceof PropertyNameMismatchErrorException)
			{
				PropertyNameMismatchErrorException e2 = (PropertyNameMismatchErrorException)e1.getCause();
				assertEquals("submissionBean", e2.getActualPropertyName());
				assertEquals("setSubmissionBean", e2.getMethod().getName());
				assertEquals(PropertyMismatchSubmissionBean.class.getName(), e2.getImplementationName());
				assertSame(SubmissionBeanProperty.class, e2.getAnnotationType());
				assertEquals("badname", e2.getExpectedPropertyName());
				assertEquals("test", e2.getSiteDeclarationName());
			}
		}
	}
	
	public void testPropertyMismatchedFile()
	{
		SiteBuilder			site_builder = new SiteBuilder("test", ResourceFinderClasspath.getInstance());
		ElementInfoBuilder	elementinfo_builder = site_builder.enterElement(ElementInfoProcessorFactory.ANNOTATIONS_IDENTIFIER+":"+PropertyMismatchFile.class.getName());
		
		try
		{
			elementinfo_builder.process();
			fail("Expected exception.");
		}
		catch (EngineException e1)
		{
			if (e1.getCause() instanceof PropertyNameMismatchErrorException)
			{
				PropertyNameMismatchErrorException e2 = (PropertyNameMismatchErrorException)e1.getCause();
				assertEquals("file", e2.getActualPropertyName());
				assertEquals("setFile", e2.getMethod().getName());
				assertEquals(PropertyMismatchFile.class.getName(), e2.getImplementationName());
				assertSame(FileProperty.class, e2.getAnnotationType());
				assertEquals("badname", e2.getExpectedPropertyName());
				assertEquals("test", e2.getSiteDeclarationName());
			}
		}
	}
	
	public void testPropertyMismatchedOutBean()
	{
		SiteBuilder			site_builder = new SiteBuilder("test", ResourceFinderClasspath.getInstance());
		ElementInfoBuilder	elementinfo_builder = site_builder.enterElement(ElementInfoProcessorFactory.ANNOTATIONS_IDENTIFIER+":"+PropertyMismatchOutBean.class.getName());
		
		try
		{
			elementinfo_builder.process();
			fail("Expected exception.");
		}
		catch (EngineException e1)
		{
			if (e1.getCause() instanceof PropertyNameMismatchErrorException)
			{
				PropertyNameMismatchErrorException e2 = (PropertyNameMismatchErrorException)e1.getCause();
				assertEquals("outBean1", e2.getActualPropertyName());
				assertEquals("setOutBean1", e2.getMethod().getName());
				assertEquals(PropertyMismatchOutBean.class.getName(), e2.getImplementationName());
				assertSame(OutBeanProperty.class, e2.getAnnotationType());
				assertEquals("badname", e2.getExpectedPropertyName());
				assertEquals("test", e2.getSiteDeclarationName());
			}
		}
	}
	
	public void testPropertyMismatchedOutCookie()
	{
		SiteBuilder			site_builder = new SiteBuilder("test", ResourceFinderClasspath.getInstance());
		ElementInfoBuilder	elementinfo_builder = site_builder.enterElement(ElementInfoProcessorFactory.ANNOTATIONS_IDENTIFIER+":"+PropertyMismatchOutCookie.class.getName());
		
		try
		{
			elementinfo_builder.process();
			fail("Expected exception.");
		}
		catch (EngineException e1)
		{
			if (e1.getCause() instanceof PropertyNameMismatchErrorException)
			{
				PropertyNameMismatchErrorException e2 = (PropertyNameMismatchErrorException)e1.getCause();
				assertEquals("outcookie", e2.getActualPropertyName());
				assertEquals("setOutCookie", e2.getMethod().getName());
				assertEquals(PropertyMismatchOutCookie.class.getName(), e2.getImplementationName());
				assertSame(OutCookieProperty.class, e2.getAnnotationType());
				assertEquals("badname", e2.getExpectedPropertyName());
				assertEquals("test", e2.getSiteDeclarationName());
			}
		}
	}
	
	public void testPropertyMismatchedOutput()
	{
		SiteBuilder			site_builder = new SiteBuilder("test", ResourceFinderClasspath.getInstance());
		ElementInfoBuilder	elementinfo_builder = site_builder.enterElement(ElementInfoProcessorFactory.ANNOTATIONS_IDENTIFIER+":"+PropertyMismatchOutput.class.getName());
		
		try
		{
			elementinfo_builder.process();
			fail("Expected exception.");
		}
		catch (EngineException e1)
		{
			if (e1.getCause() instanceof PropertyNameMismatchErrorException)
			{
				PropertyNameMismatchErrorException e2 = (PropertyNameMismatchErrorException)e1.getCause();
				assertEquals("output", e2.getActualPropertyName());
				assertEquals("setOutput", e2.getMethod().getName());
				assertEquals(PropertyMismatchOutput.class.getName(), e2.getImplementationName());
				assertSame(OutputProperty.class, e2.getAnnotationType());
				assertEquals("badname", e2.getExpectedPropertyName());
				assertEquals("test", e2.getSiteDeclarationName());
			}
		}
	}
}
