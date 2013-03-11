/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestXml2ElementModel.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model;

import com.uwyn.rife.resources.ResourceFinderClasspath;
import java.util.Iterator;
import junit.framework.TestCase;

public class TestXml2ElementModel extends TestCase
{
	public TestXml2ElementModel(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
		Xml2ElementModel xml2elementmodel = new Xml2ElementModel();
		
		assertNotNull(xml2elementmodel);
	}

	public void testParser()
	{
		ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
		Xml2ElementModel		xml2elementmodel = new Xml2ElementModel();
		
		xml2elementmodel.processXml("xml/test_xml2elementmodel.xml", resourcefinder);
		
		ElementModel elementmodel = xml2elementmodel.getElementModel();
		
		assertEquals(elementmodel.getId().getName(), "JustSomeElement");
		assertEquals(elementmodel.getImplementation().getName(), "com.uwyn.rife.element.elementModel");
		assertEquals(elementmodel.getDescription(), "This is the description of the element.");
		
		ElementInputModel			input = null;
		Iterator<ElementInputModel>	input_it = null;
		assertEquals(elementmodel.countInputs(), 3);
		input_it = elementmodel.getInputs().iterator();
		input = input_it.next();
		assertEquals(input.getName(), "Input1");
		assertEquals(input.getDescription(), "This is the first input.");
		input = input_it.next();
		assertEquals(input.getName(), "Input2");
		assertEquals(input.getDescription(), "This is the second input.");
		input = input_it.next();
		assertEquals(input.getName(), "Input3");
		assertEquals(input.getDescription(), "This is the third input.");
		assertTrue(false == input_it.hasNext());
		
		ElementOutputModel				output = null;
		Iterator<ElementOutputModel>	output_it = null;
		assertEquals(elementmodel.countOutputs(), 2);
		output_it = elementmodel.getOutputs().iterator();
		output = output_it.next();
		assertEquals(output.getName(), "Output1");
		assertEquals(output.getDescription(), "This is the first output.");
		output = output_it.next();
		assertEquals(output.getName(), "Output2");
		assertEquals(output.getDescription(), "This is the second output.");
		assertTrue(false == output_it.hasNext());
		
		ElementExitModel			exit = null;
		Iterator<ElementExitModel>	exit_it = null;
		assertEquals(elementmodel.countExits(), 4);
		exit_it = elementmodel.getExits().iterator();
		exit = exit_it.next();
		assertEquals(exit.getName(), "Exit1");
		assertEquals(exit.getDescription(), "This is the first exit.");
		exit = exit_it.next();
		assertEquals(exit.getName(), "Exit2");
		assertEquals(exit.getDescription(), "This is the second exit.");
		exit = exit_it.next();
		assertEquals(exit.getName(), "Exit3");
		assertEquals(exit.getDescription(), "This is the third exit.");
		exit = exit_it.next();
		assertEquals(exit.getName(), "Exit4");
		assertEquals(exit.getDescription(), "This is the fourth exit.");
		assertTrue(false == exit_it.hasNext());
		
		SubmissionModel						submission = null;
		Iterator<SubmissionModel>			submission_it = null;
		SubmissionParameterModel			parameter = null;
		Iterator<SubmissionParameterModel>	parameter_it = null;
		assertEquals(elementmodel.countSubmissions(), 2);
		submission_it = elementmodel.getSubmissions().iterator();
		submission = submission_it.next();
		assertEquals(submission.getId().getName(), "Submission1");
		assertEquals(submission.getDescription(), "This is the first submission.");
		assertEquals(submission.countParameters(), 3);
		parameter_it = submission.getParameters().iterator();
		parameter = parameter_it.next();
		assertEquals(parameter.getName(), "Param1");
		assertEquals(parameter.getDescription(), "This is the first parameter of the first submission.");
		parameter = parameter_it.next();
		assertEquals(parameter.getName(), "Param2");
		assertEquals(parameter.getDescription(), "This is the second parameter of the first submission.");
		parameter = parameter_it.next();
		assertEquals(parameter.getName(), "Param3");
		assertEquals(parameter.getDescription(), "This is the third parameter of the first submission.");
		assertTrue(false == parameter_it.hasNext());
		submission = submission_it.next();
		assertEquals(submission.getId().getName(), "Submission2");
		assertEquals(submission.getDescription(), "This is the second submission.");
		assertEquals(submission.countParameters(), 2);
		parameter_it = submission.getParameters().iterator();
		parameter = parameter_it.next();
		assertEquals(parameter.getName(), "Param1");
		assertEquals(parameter.getDescription(), "This is the first parameter of the second submission.");
		parameter = parameter_it.next();
		assertEquals(parameter.getName(), "Param2");
		assertEquals(parameter.getDescription(), "This is the second parameter of the second submission.");
		assertTrue(false == parameter_it.hasNext());
		assertTrue(false == submission_it.hasNext());
	}
}

