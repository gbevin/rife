/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSubmission.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.*;

import com.uwyn.rife.tools.ExceptionUtils;
import java.util.LinkedHashMap;
import junit.framework.TestCase;

public class TestSubmission extends TestCase
{
	public TestSubmission(String name)
	{
		super(name);
	}
	
	public void testInstantiation()
	{
		Submission submission = null;

		submission = new Submission();

		assertNotNull(submission);
	}

	public void testNoInitialParameters()
	{
		Submission submission = new Submission();

		assertEquals(submission.getParameterNames().size(), 0);
	}

	public void testNoInitialNamedBeans()
	{
		Submission submission = new Submission();

		assertEquals(submission.getBeanNames().size(), 0);
	}

	public void testNoInitialBeans()
	{
		Submission submission = new Submission();

		assertEquals(submission.getBeans().size(), 0);
	}

	public void testNoInitialFiles()
	{
		Submission submission = new Submission();

		assertEquals(submission.getFileNames().size(), 0);
	}

	public void testAddParameter()
	throws EngineException
	{
		ElementInfo element = new ElementInfo("element/test4.xml", "/", TestElement4.class.getName(), ElementType.JAVA_CLASS);
		Submission submission = new Submission();
		element.addSubmission("submission1", submission);

		submission.addParameter("parameter1", null);
		assertEquals(submission.getParameterNames().size(), 1);
		submission.addParameter("parameter2", null);
		assertEquals(submission.getParameterNames().size(), 2);
		submission.addParameter("parameter3", null);
		assertEquals(submission.getParameterNames().size(), 3);

		assertTrue(submission.containsParameter("parameter1"));
		assertTrue(submission.containsParameter("parameter2"));
		assertTrue(submission.containsParameter("parameter3"));
	}

	public void testAddParameterRegexp()
	throws EngineException
	{
		ElementInfo element = new ElementInfo("element/test4.xml", "/", TestElement4.class.getName(), ElementType.JAVA_CLASS);
		Submission submission = new Submission();
		element.addSubmission("submission1", submission);

		submission.addParameterRegexp("paramA(\\d+)");
		assertEquals(submission.getParameterRegexps().size(), 1);
		submission.addParameterRegexp("paramB(\\d+)");
		assertEquals(submission.getParameterRegexps().size(), 2);
		submission.addParameterRegexp("paramC(\\d+)");
		assertEquals(submission.getParameterRegexps().size(), 3);

		assertTrue(submission.containsParameter("paramA1"));
		assertTrue(submission.containsParameter("paramA2"));
		assertTrue(submission.containsParameter("paramA3"));
		assertTrue(submission.containsParameter("paramB1"));
		assertTrue(submission.containsParameter("paramB2"));
		assertTrue(submission.containsParameter("paramB3"));
		assertTrue(submission.containsParameter("paramC1"));
		assertTrue(submission.containsParameter("paramC2"));
		assertTrue(submission.containsParameter("paramC3"));
		assertTrue(!submission.containsParameter("paramA"));
		assertTrue(!submission.containsParameter("paramB"));
		assertTrue(!submission.containsParameter("paramC"));
	}

	public void testAddFile()
	throws EngineException
	{
		ElementInfo element = new ElementInfo("element/test4.xml", "/", TestElement4.class.getName(), ElementType.JAVA_CLASS);
		Submission submission = new Submission();
		element.addSubmission("submission1", submission);

		submission.addFile("file1");
		assertEquals(submission.getFileNames().size(), 1);
		submission.addFile("file2");
		assertEquals(submission.getFileNames().size(), 2);
		submission.addFile("file3");
		assertEquals(submission.getFileNames().size(), 3);

		assertTrue(submission.containsFile("file1"));
		assertTrue(submission.containsFile("file2"));
		assertTrue(submission.containsFile("file3"));
	}

	public void testAddBean()
	throws EngineException
	{
		ElementInfo element = new ElementInfo("element/test4.xml", "/", TestElement4.class.getName(), ElementType.JAVA_CLASS);
		Submission submission = new Submission();
		element.addSubmission("submission1", submission);

		assertFalse(submission.containsNamedBean("bean1"));

		BeanDeclaration bean1 = new BeanDeclaration("com.uwyn.rife.engine.testelements.submission.BeanImpl", null, null);
		submission.addBean(bean1, null);
		assertEquals(submission.getBeanNames().size(), 0);
		assertEquals(submission.getBeans().size(), 1);
		assertEquals(submission.getBeans().iterator().next(), bean1);
		assertEquals(submission.getParameterNames().size(), 22);
		assertEquals(submission.getFileNames().size(), 3);
		assertTrue(submission.containsParameter("enum"));
		assertTrue(submission.containsParameter("string"));
		assertTrue(submission.containsParameter("stringbuffer"));
		assertTrue(submission.containsParameter("int"));
		assertTrue(submission.containsParameter("integer"));
		assertTrue(submission.containsParameter("char"));
		assertTrue(submission.containsParameter("boolean"));
		assertTrue(submission.containsParameter("booleanObject"));
		assertTrue(submission.containsParameter("byteObject"));
		assertTrue(submission.containsParameter("double"));
		assertTrue(submission.containsParameter("doubleObject"));
		assertTrue(submission.containsParameter("float"));
		assertTrue(submission.containsParameter("floatObject"));
		assertTrue(submission.containsParameter("long"));
		assertTrue(submission.containsParameter("longObject"));
		assertTrue(submission.containsParameter("short"));
		assertTrue(submission.containsParameter("shortObject"));
		assertTrue(submission.containsParameter("date"));
		assertTrue(submission.containsParameter("dateFormatted"));
		assertTrue(submission.containsParameter("datesFormatted"));
		assertTrue(submission.containsParameter("serializableParam"));
		assertTrue(submission.containsParameter("serializableParams"));
		assertTrue(submission.containsFile("stringFile"));
		assertTrue(submission.containsFile("bytesFile"));
		assertTrue(submission.containsFile("streamFile"));
	}

	public void testAddBeanPrefix()
	throws EngineException
	{
		ElementInfo element = new ElementInfo("element/test4.xml", "/", TestElement4.class.getName(), ElementType.JAVA_CLASS);
		Submission submission = new Submission();
		element.addSubmission("submission1", submission);

		assertFalse(submission.containsNamedBean("bean1"));

		BeanDeclaration bean1 = new BeanDeclaration("com.uwyn.rife.engine.testelements.submission.BeanImpl", "prefix_", null);
		submission.addBean(bean1, null);
		assertEquals(submission.getBeanNames().size(), 0);
		assertEquals(submission.getBeans().size(), 1);
		assertEquals(submission.getBeans().iterator().next(), bean1);
		assertEquals(submission.getParameterNames().size(), 22);
		assertEquals(submission.getFileNames().size(), 3);
		assertTrue(submission.containsParameter("prefix_enum"));
		assertTrue(submission.containsParameter("prefix_string"));
		assertTrue(submission.containsParameter("prefix_stringbuffer"));
		assertTrue(submission.containsParameter("prefix_int"));
		assertTrue(submission.containsParameter("prefix_integer"));
		assertTrue(submission.containsParameter("prefix_char"));
		assertTrue(submission.containsParameter("prefix_boolean"));
		assertTrue(submission.containsParameter("prefix_booleanObject"));
		assertTrue(submission.containsParameter("prefix_byteObject"));
		assertTrue(submission.containsParameter("prefix_double"));
		assertTrue(submission.containsParameter("prefix_doubleObject"));
		assertTrue(submission.containsParameter("prefix_float"));
		assertTrue(submission.containsParameter("prefix_floatObject"));
		assertTrue(submission.containsParameter("prefix_long"));
		assertTrue(submission.containsParameter("prefix_longObject"));
		assertTrue(submission.containsParameter("prefix_short"));
		assertTrue(submission.containsParameter("prefix_shortObject"));
		assertTrue(submission.containsParameter("prefix_date"));
		assertTrue(submission.containsParameter("prefix_dateFormatted"));
		assertTrue(submission.containsParameter("prefix_datesFormatted"));
		assertTrue(submission.containsParameter("prefix_serializableParam"));
		assertTrue(submission.containsParameter("prefix_serializableParams"));
		assertTrue(submission.containsFile("prefix_stringFile"));
		assertTrue(submission.containsFile("prefix_bytesFile"));
		assertTrue(submission.containsFile("prefix_streamFile"));
	}
	
	public void testAddBeanGroup()
	throws EngineException
	{
		ElementInfo element = new ElementInfo("element/test4.xml", "/", TestElement4.class.getName(), ElementType.JAVA_CLASS);
		Submission submission = new Submission();
		element.addSubmission("submission1", submission);
		
		assertFalse(submission.containsNamedBean("bean1"));
		
		BeanDeclaration bean1 = new BeanDeclaration("com.uwyn.rife.engine.testelements.submission.BeanImpl", null, "somegroup");
		submission.addBean(bean1, null);
		assertEquals(submission.getBeanNames().size(), 0);
		assertEquals(submission.getBeans().size(), 1);
		assertEquals(submission.getBeans().iterator().next(), bean1);
		assertEquals(submission.getParameterNames().size(), 5);
		assertEquals(submission.getFileNames().size(), 0);
		assertTrue(submission.containsParameter("enum"));
		assertTrue(submission.containsParameter("string"));
		assertTrue(submission.containsParameter("int"));
		assertTrue(submission.containsParameter("longObject"));
		assertTrue(submission.containsParameter("short"));
	}
	
	public void testAddBeanGroupPrefix()
	throws EngineException
	{
		ElementInfo element = new ElementInfo("element/test4.xml", "/", TestElement4.class.getName(), ElementType.JAVA_CLASS);
		Submission submission = new Submission();
		element.addSubmission("submission1", submission);
		
		assertFalse(submission.containsNamedBean("bean1"));
		
		BeanDeclaration bean1 = new BeanDeclaration("com.uwyn.rife.engine.testelements.submission.BeanImpl", "prefix_", "somegroup");
		submission.addBean(bean1, null);
		assertEquals(submission.getBeanNames().size(), 0);
		assertEquals(submission.getBeans().size(), 1);
		assertEquals(submission.getBeans().iterator().next(), bean1);
		assertEquals(submission.getParameterNames().size(), 5);
		assertEquals(submission.getFileNames().size(), 0);
		assertTrue(submission.containsParameter("prefix_enum"));
		assertTrue(submission.containsParameter("prefix_string"));
		assertTrue(submission.containsParameter("prefix_int"));
		assertTrue(submission.containsParameter("prefix_longObject"));
		assertTrue(submission.containsParameter("prefix_short"));
	}
	
	public void testAddNamedBean()
	throws EngineException
	{
		ElementInfo element = new ElementInfo("element/test4.xml", "/", TestElement4.class.getName(), ElementType.JAVA_CLASS);
		Submission submission = new Submission();
		element.addSubmission("submission1", submission);

		assertFalse(submission.containsNamedBean("bean1"));

		BeanDeclaration bean1 = new BeanDeclaration("com.uwyn.rife.engine.testelements.submission.BeanImpl", null, null);
		submission.addBean(bean1, "bean1");
		assertTrue(submission.containsNamedBean("bean1"));
		assertEquals(submission.getBeanNames().size(), 1);
		assertEquals(submission.getBeanNames().iterator().next(), "bean1");
		assertEquals(submission.getBeans().size(), 1);
		assertEquals(submission.getBeans().iterator().next(), bean1);
		assertEquals(submission.getParameterNames().size(), 22);
		assertEquals(submission.getFileNames().size(), 3);
		assertTrue(submission.containsParameter("enum"));
		assertTrue(submission.containsParameter("string"));
		assertTrue(submission.containsParameter("stringbuffer"));
		assertTrue(submission.containsParameter("int"));
		assertTrue(submission.containsParameter("integer"));
		assertTrue(submission.containsParameter("char"));
		assertTrue(submission.containsParameter("boolean"));
		assertTrue(submission.containsParameter("booleanObject"));
		assertTrue(submission.containsParameter("byteObject"));
		assertTrue(submission.containsParameter("double"));
		assertTrue(submission.containsParameter("doubleObject"));
		assertTrue(submission.containsParameter("float"));
		assertTrue(submission.containsParameter("floatObject"));
		assertTrue(submission.containsParameter("long"));
		assertTrue(submission.containsParameter("longObject"));
		assertTrue(submission.containsParameter("short"));
		assertTrue(submission.containsParameter("shortObject"));
		assertTrue(submission.containsParameter("date"));
		assertTrue(submission.containsParameter("dateFormatted"));
		assertTrue(submission.containsParameter("datesFormatted"));
		assertTrue(submission.containsParameter("serializableParam"));
		assertTrue(submission.containsParameter("serializableParams"));
		assertTrue(submission.containsFile("stringFile"));
		assertTrue(submission.containsFile("bytesFile"));
		assertTrue(submission.containsFile("streamFile"));
		BeanDeclaration bean2 = submission.getNamedBean("bean1");
		assertNotNull(bean2);
		assertEquals(bean1, bean2);
	}

	public void testAddNamedBeanPrefix()
	throws EngineException
	{
		ElementInfo element = new ElementInfo("element/test4.xml", "/", TestElement4.class.getName(), ElementType.JAVA_CLASS);
		Submission submission = new Submission();
		element.addSubmission("submission1", submission);

		assertFalse(submission.containsNamedBean("bean1"));

		BeanDeclaration bean1 = new BeanDeclaration("com.uwyn.rife.engine.testelements.submission.BeanImpl", "prefix_", null);
		submission.addBean(bean1, "bean1");
		assertTrue(submission.containsNamedBean("bean1"));
		assertEquals(submission.getBeanNames().size(), 1);
		assertEquals(submission.getBeanNames().iterator().next(), "bean1");
		assertEquals(submission.getBeans().size(), 1);
		assertEquals(submission.getBeans().iterator().next(), bean1);
		assertEquals(submission.getParameterNames().size(), 22);
		assertEquals(submission.getFileNames().size(), 3);
		assertTrue(submission.containsParameter("prefix_enum"));
		assertTrue(submission.containsParameter("prefix_string"));
		assertTrue(submission.containsParameter("prefix_stringbuffer"));
		assertTrue(submission.containsParameter("prefix_int"));
		assertTrue(submission.containsParameter("prefix_integer"));
		assertTrue(submission.containsParameter("prefix_char"));
		assertTrue(submission.containsParameter("prefix_boolean"));
		assertTrue(submission.containsParameter("prefix_booleanObject"));
		assertTrue(submission.containsParameter("prefix_byteObject"));
		assertTrue(submission.containsParameter("prefix_double"));
		assertTrue(submission.containsParameter("prefix_doubleObject"));
		assertTrue(submission.containsParameter("prefix_float"));
		assertTrue(submission.containsParameter("prefix_floatObject"));
		assertTrue(submission.containsParameter("prefix_long"));
		assertTrue(submission.containsParameter("prefix_longObject"));
		assertTrue(submission.containsParameter("prefix_short"));
		assertTrue(submission.containsParameter("prefix_shortObject"));
		assertTrue(submission.containsParameter("prefix_date"));
		assertTrue(submission.containsParameter("prefix_dateFormatted"));
		assertTrue(submission.containsParameter("prefix_datesFormatted"));
		assertTrue(submission.containsParameter("prefix_serializableParam"));
		assertTrue(submission.containsParameter("prefix_serializableParams"));
		assertTrue(submission.containsFile("prefix_stringFile"));
		assertTrue(submission.containsFile("prefix_bytesFile"));
		assertTrue(submission.containsFile("prefix_streamFile"));
		BeanDeclaration bean2 = submission.getNamedBean("bean1");
		assertNotNull(bean2);
		assertEquals(bean1, bean2);
	}

	public void testAddDuplicateParameter()
	{
		ElementInfo element = new ElementInfo("element/test4.xml", "/", TestElement4.class.getName(), ElementType.JAVA_CLASS);
		Submission submission = new Submission();
		element.addSubmission("submission1", submission);

		try
		{
			submission.addParameter("parameter1", null);
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getParameterNames().size(), 1);
		try
		{
			submission.addParameter("parameter1", null);
			fail();
		}
		catch (ParameterExistsException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getParameterName(), "parameter1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getParameterNames().size(), 1);
	}

	public void testAddDuplicateFile()
	{
		ElementInfo element = new ElementInfo("element/test4.xml", "/", TestElement4.class.getName(), ElementType.JAVA_CLASS);
		Submission submission = new Submission();
		element.addSubmission("submission1", submission);

		try
		{
			submission.addFile("file1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getFileNames().size(), 1);
		try
		{
			submission.addFile("file1");
			fail();
		}
		catch (FileExistsException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getFileName(), "file1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getFileNames().size(), 1);
	}
	
	public void testAddDuplicateNamedBean()
	{
		ElementInfo element = new ElementInfo("element/test4.xml", "/", TestElement4.class.getName(), ElementType.JAVA_CLASS);
		Submission submission = new Submission();
		element.addSubmission("submission1", submission);
		
		try
		{
			submission.addBean(new BeanDeclaration("com.uwyn.rife.engine.testelements.submission.BeanImpl", null, null), "bean1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getBeanNames().size(), 1);
		try
		{
			submission.addBean(new BeanDeclaration("com.uwyn.rife.engine.testelements.submission.BeanImpl", "prefix_", null), "bean1");
			fail();
		}
		catch (NamedSubmissionBeanExistsException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getBeanName(), "bean1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getBeanNames().size(), 1);
	}
	
	public void testAddBeanUnknownGroup()
	{
		ElementInfo element = new ElementInfo("element/test4.xml", "/", TestElement4.class.getName(), ElementType.JAVA_CLASS);
		Submission submission = new Submission();
		element.addSubmission("submission1", submission);

		try
		{
			submission.addBean(new BeanDeclaration("com.uwyn.rife.engine.testelements.submission.BeanImpl", null, "unknown"), null);
		}
		catch (SubmissionBeanGroupNotFoundException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getClassName(), "com.uwyn.rife.engine.testelements.submission.BeanImpl");
			assertEquals(e.getGroupName(), "unknown");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getBeanNames().size(), 0);
		assertEquals(submission.getBeans().size(), 0);
		
		try
		{
			submission.addBean(new BeanDeclaration("com.uwyn.rife.engine.testelements.submission.BeanImpl", null, "unknown"), "bean1");
		}
		catch (NamedSubmissionBeanGroupNotFoundException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getClassName(), "com.uwyn.rife.engine.testelements.submission.BeanImpl");
			assertEquals(e.getGroupName(), "unknown");
			assertEquals(e.getBeanName(), "bean1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getBeanNames().size(), 0);
		assertEquals(submission.getBeans().size(), 0);
	}
	
	public void testAddBeanGroupOnNonValidationClass()
	{
		ElementInfo element = new ElementInfo("element/test4.xml", "/", TestElement4.class.getName(), ElementType.JAVA_CLASS);
		Submission submission = new Submission();
		element.addSubmission("submission1", submission);
		
		try
		{
			submission.addBean(new BeanDeclaration("com.uwyn.rife.engine.testelements.submission.BeanPojo", null, "thegroup"), null);
		}
		catch (SubmissionBeanGroupRequiresValidatedConstrainedException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getClassName(), "com.uwyn.rife.engine.testelements.submission.BeanPojo");
			assertEquals(e.getGroupName(), "thegroup");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getBeanNames().size(), 0);
		assertEquals(submission.getBeans().size(), 0);
		
		try
		{
			submission.addBean(new BeanDeclaration("com.uwyn.rife.engine.testelements.submission.BeanPojo", null, "thegroup"), "bean1");
		}
		catch (NamedSubmissionBeanGroupRequiresValidatedConstrainedException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getClassName(), "com.uwyn.rife.engine.testelements.submission.BeanPojo");
			assertEquals(e.getGroupName(), "thegroup");
			assertEquals(e.getBeanName(), "bean1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getBeanNames().size(), 0);
		assertEquals(submission.getBeans().size(), 0);
	}
	
	public void testValidateBeanName()
	throws EngineException
	{
		ElementInfo element = new ElementInfo("element/test4.xml", "/", TestElement4.class.getName(), ElementType.JAVA_CLASS);
		Submission submission = new Submission();
		element.addSubmission("submission1", submission);

		try
		{
			submission.validateBeanName("bean1");
			fail();
		}
		catch (NamedSubmissionBeanUnknownException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getBeanName(), "bean1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		submission.addBean(new BeanDeclaration("com.uwyn.rife.engine.testelements.submission.BeanImpl", null, null), "bean1");
		try
		{
			submission.validateBeanName("bean1");
			assertTrue(true);
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetNamedBean()
	throws EngineException
	{
		ElementInfo element = new ElementInfo("element/test4.xml", "/", TestElement4.class.getName(), ElementType.JAVA_CLASS);
		Submission submission = new Submission();
		element.addSubmission("submission1", submission);

		try
		{
			submission.getNamedBean("bean1");
			fail();
		}
		catch (NamedSubmissionBeanUnknownException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getBeanName(), "bean1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		submission.addBean(new BeanDeclaration("com.uwyn.rife.engine.testelements.submission.BeanImpl", "prf", null), "bean1");
		try
		{
			assertEquals(submission.getNamedBean("bean1").getClassname(), "com.uwyn.rife.engine.testelements.submission.BeanImpl");
			assertEquals(submission.getNamedBean("bean1").getPrefix(), "prf");
			assertTrue(true);
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testParameterRegexpConflict()
	{
		ElementInfo element = new ElementInfo("element/test4.xml", "/", TestElement4.class.getName(), ElementType.JAVA_CLASS);
		Submission submission = new Submission();
		element.addSubmission("submission1", submission);

		try
		{
			submission.addParameterRegexp("parameterregexp(\\d+)");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getParameterRegexps().size(), 1);
		
		element.addInput("input1", null);
		try
		{
			submission.addParameterRegexp("input(.*)");
			fail();
		}
		catch (ParameterRegexpInputConflictException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getInputName(), "input1");
			assertEquals(e.getConflictName(), "^input(.*)$");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getParameterRegexps().size(), 1);
		
		element.addIncookie("incookie1", null);
		try
		{
			submission.addParameterRegexp("incookie(.*)");
			fail();
		}
		catch (ParameterRegexpIncookieConflictException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getIncookieName(), "incookie1");
			assertEquals(e.getConflictName(), "^incookie(.*)$");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getParameterRegexps().size(), 1);
		
		submission.addParameter("parameter1", null);
		try
		{
			submission.addParameterRegexp("parameter(\\d+)");
			fail();
		}
		catch (ParameterRegexpParameterConflictException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getParameterName(), "parameter1");
			assertEquals(e.getConflictName(), "^parameter(\\d+)$");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getParameterRegexps().size(), 1);

		submission.addFile("file1");
		try
		{
			submission.addParameterRegexp("file(\\d+)");
			fail();
		}
		catch (ParameterRegexpFileConflictException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getFileName(), "file1");
			assertEquals(e.getConflictName(), "^file(\\d+)$");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getParameterRegexps().size(), 1);
		
		element.setGlobalVars(new LinkedHashMap<String, GlobalVar>() {{ put("globalvar1", new GlobalVar(null)); }});
		try
		{
			submission.addParameterRegexp("globalvar(.*)");
			fail();
		}
		catch (ParameterRegexpGlobalVarConflictException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getGlobalVarName(), "globalvar1");
			assertEquals(e.getConflictName(), "^globalvar(.*)$");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getParameterRegexps().size(), 1);
		
		element.setGlobalCookies(new LinkedHashMap<String, String>() {{ put("globalcookie1", null); }});
		try
		{
			submission.addParameterRegexp("globalcookie(.*)");
			fail();
		}
		catch (ParameterRegexpGlobalCookieConflictException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getGlobalCookieName(), "globalcookie1");
			assertEquals(e.getConflictName(), "^globalcookie(.*)$");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getParameterRegexps().size(), 1);
		
	}

	public void testParameterConflicts()
	{
		ElementInfo element = new ElementInfo("element/test4.xml", "/", TestElement4.class.getName(), ElementType.JAVA_CLASS);
		Submission submission = new Submission();
		element.addSubmission("submission1", submission);

		try
		{
			submission.addParameter("parameter1", null);
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getParameterNames().size(), 1);

		element.addInput("input1", null);
		try
		{
			submission.addParameter("input1", null);
			fail();
		}
		catch (ParameterInputConflictException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getConflictName(), "input1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getParameterNames().size(), 1);

		element.addIncookie("incookie1", null);
		try
		{
			submission.addParameter("incookie1", null);
			fail();
		}
		catch (ParameterIncookieConflictException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getConflictName(), "incookie1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getParameterNames().size(), 1);

		submission.addFile("file1");
		try
		{
			submission.addParameter("file1", null);
			fail();
		}
		catch (ParameterFileConflictException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getConflictName(), "file1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getParameterNames().size(), 1);
		
		submission.addParameterRegexp("regexpparameter(\\d+)");
		try
		{
			submission.addParameter("regexpparameter1", null);
			fail();
		}
		catch (ParameterParameterRegexpConflictException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getConflictName(), "regexpparameter1");
			assertEquals(e.getParameterRegexp(), "^regexpparameter(\\d+)$");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getFileNames().size(), 1);
		
		submission.addFileRegexp("regexpfile(\\d+)");
		try
		{
			submission.addParameter("regexpfile1", null);
			fail();
		}
		catch (ParameterFileRegexpConflictException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getConflictName(), "regexpfile1");
			assertEquals(e.getFileRegexp(), "^regexpfile(\\d+)$");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getFileNames().size(), 1);
		
		LinkedHashMap<String, GlobalVar>	globals_vars = new LinkedHashMap<String, GlobalVar>();
		globals_vars.put("globalvar1", new GlobalVar(null));
		element = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element.setGlobalVars(globals_vars);
		submission = new Submission();
		element.addSubmission("submission1", submission);
		try
		{
			submission.addParameter("globalvar1", null);
			fail();
		}
		catch (ParameterGlobalVarConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getConflictName(), "globalvar1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getParameterNames().size(), 0);
		
		LinkedHashMap<String, String>	globals_cookies = new LinkedHashMap<String, String>();
		globals_cookies.put("globalcookie1", null);
		element = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element.setGlobalCookies(globals_cookies);
		submission = new Submission();
		element.addSubmission("submission1", submission);
		try
		{
			submission.addParameter("globalcookie1", null);
			fail();
		}
		catch (ParameterGlobalCookieConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getConflictName(), "globalcookie1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getParameterNames().size(), 0);
	}
	
	public void testFileRegexpConflict()
	{
		ElementInfo element = new ElementInfo("element/test4.xml", "/", TestElement4.class.getName(), ElementType.JAVA_CLASS);
		Submission submission = new Submission();
		element.addSubmission("submission1", submission);
		
		try
		{
			submission.addFileRegexp("fileregexp(\\d+)");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getFileRegexps().size(), 1);
		
		element.addInput("input1", null);
		try
		{
			submission.addFileRegexp("input(.*)");
			fail();
		}
		catch (FileRegexpInputConflictException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getInputName(), "input1");
			assertEquals(e.getConflictName(), "^input(.*)$");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getFileRegexps().size(), 1);
		
		element.addIncookie("incookie1", null);
		try
		{
			submission.addFileRegexp("incookie(.*)");
			fail();
		}
		catch (FileRegexpIncookieConflictException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getIncookieName(), "incookie1");
			assertEquals(e.getConflictName(), "^incookie(.*)$");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getFileRegexps().size(), 1);
		
		submission.addFile("file1");
		try
		{
			submission.addFileRegexp("file(\\d+)");
			fail();
		}
		catch (FileRegexpFileConflictException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getFileName(), "file1");
			assertEquals(e.getConflictName(), "^file(\\d+)$");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getFileRegexps().size(), 1);
		
		submission.addParameter("param1", null);
		try
		{
			submission.addFileRegexp("param(\\d+)");
			fail();
		}
		catch (FileRegexpParameterConflictException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getParameterName(), "param1");
			assertEquals(e.getConflictName(), "^param(\\d+)$");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getFileRegexps().size(), 1);
		
		element.setGlobalVars(new LinkedHashMap<String, GlobalVar>() {{ put("globalvar1", new GlobalVar(null)); }});
		try
		{
			submission.addFileRegexp("globalvar(.*)");
			fail();
		}
		catch (FileRegexpGlobalVarConflictException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getGlobalVarName(), "globalvar1");
			assertEquals(e.getConflictName(), "^globalvar(.*)$");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getFileRegexps().size(), 1);
		
		element.setGlobalCookies(new LinkedHashMap<String, String>() {{ put("globalcookie1", null); }});
		try
		{
			submission.addFileRegexp("globalcookie(.*)");
			fail();
		}
		catch (FileRegexpGlobalCookieConflictException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getGlobalCookieName(), "globalcookie1");
			assertEquals(e.getConflictName(), "^globalcookie(.*)$");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getFileRegexps().size(), 1);
		
	}
	
	public void testFileConflicts()
	{
		ElementInfo element = new ElementInfo("element/test4.xml", "/", TestElement4.class.getName(), ElementType.JAVA_CLASS);
		Submission submission = new Submission();
		element.addSubmission("submission1", submission);

		try
		{
			submission.addFile("file1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getFileNames().size(), 1);

		element.addInput("input1", null);
		try
		{
			submission.addFile("input1");
			fail();
		}
		catch (FileInputConflictException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getConflictName(), "input1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getFileNames().size(), 1);

		element.addIncookie("incookie1", null);
		try
		{
			submission.addFile("incookie1");
			fail();
		}
		catch (FileIncookieConflictException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getConflictName(), "incookie1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getFileNames().size(), 1);
		
		submission.addParameter("parameter1", null);
		try
		{
			submission.addFile("parameter1");
			fail();
		}
		catch (FileParameterConflictException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getConflictName(), "parameter1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getFileNames().size(), 1);
		
		submission.addParameterRegexp("regexpparameter(\\d+)");
		try
		{
			submission.addFile("regexpparameter1");
			fail();
		}
		catch (FileParameterRegexpConflictException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getConflictName(), "regexpparameter1");
			assertEquals(e.getParameterRegexp(), "^regexpparameter(\\d+)$");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getFileNames().size(), 1);
		
		submission.addFileRegexp("regexpfile(\\d+)");
		try
		{
			submission.addFile("regexpfile1");
			fail();
		}
		catch (FileFileRegexpConflictException e)
		{
			assertSame(e.getDeclarationName(), "element/test4.xml");
			assertEquals(e.getSubmissionName(), "submission1");
			assertEquals(e.getConflictName(), "regexpfile1");
			assertEquals(e.getFileRegexp(), "^regexpfile(\\d+)$");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getFileNames().size(), 1);
		
		LinkedHashMap<String, GlobalVar>	globals_vars = new LinkedHashMap<String, GlobalVar>();
		globals_vars.put("globalvar1", new GlobalVar(null));
		element = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element.setGlobalVars(globals_vars);
		submission = new Submission();
		element.addSubmission("submission1", submission);
		try
		{
			submission.addFile("globalvar1");
			fail();
		}
		catch (FileGlobalVarConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getConflictName(), "globalvar1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getFileNames().size(), 0);
		
		LinkedHashMap<String, String>	globals_cookies = new LinkedHashMap<String, String>();
		globals_cookies.put("globalcookie1", null);
		element = new ElementInfo("element/test2.xml", "text/html", TestElement2.class.getName(), ElementType.JAVA_CLASS);
		element.setGlobalCookies(globals_cookies);
		submission = new Submission();
		element.addSubmission("submission1", submission);
		try
		{
			submission.addFile("globalcookie1");
			fail();
		}
		catch (FileGlobalCookieConflictException e)
		{
			assertEquals(e.getDeclarationName(), "element/test2.xml");
			assertEquals(e.getConflictName(), "globalcookie1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(submission.getFileNames().size(), 0);
	}

	public void testAddParameterDefaultValues()
	throws EngineException
	{
		ElementInfo element = new ElementInfo("element/test4.xml", "/", TestElement4.class.getName(), ElementType.JAVA_CLASS);
		Submission submission = new Submission();
		element.addSubmission("submission1", submission);

		assertTrue(false == submission.hasParameterDefaults());

		submission.addParameter("parameter1", new String[] {"one","two"});
		submission.addParameter("parameter2", new String[] {"three"});
		submission.addParameter("parameter3", null);

		assertTrue(submission.hasParameterDefaults());

		assertTrue(submission.hasParameterDefaultValues("parameter1"));
		assertTrue(submission.hasParameterDefaultValues("parameter2"));
		assertTrue(false == submission.hasParameterDefaultValues("parameter3"));

		assertNotNull(submission.getParameterDefaultValues("parameter1"));
		assertNotNull(submission.getParameterDefaultValues("parameter2"));
		assertNull(submission.getParameterDefaultValues("parameter3"));

		assertEquals(2, submission.getParameterDefaultValues("parameter1").length);
		assertEquals(1, submission.getParameterDefaultValues("parameter2").length);

		assertEquals("one", submission.getParameterDefaultValues("parameter1")[0]);
		assertEquals("two", submission.getParameterDefaultValues("parameter1")[1]);
		assertEquals("three", submission.getParameterDefaultValues("parameter2")[0]);
	}
}

class TestElement4 extends Element
{
	public void processElement()
	throws EngineException
	{
		print("the content");
	}
}

