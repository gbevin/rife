/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestValidationBuilderXhtml.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import com.uwyn.rife.site.exceptions.MissingMarkingBlockException;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.template.TemplateFactory;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

public class TestValidationBuilderXhtml extends TestCase
{
	public TestValidationBuilderXhtml(String name)
	{
		super(name);
	}

	public void testInstantiate()
	{
		ValidationBuilderXhtml builder = new ValidationBuilderXhtml();
		assertNotNull(builder);
	}

	public void testClone()
	{
		ValidationBuilderXhtml builder1 = new ValidationBuilderXhtml();
		ValidationBuilderXhtml builder2 = (ValidationBuilderXhtml)builder1.clone();
		assertNotNull(builder2);
		assertNotSame(builder1, builder2);
	}
	
	public void testSetFallbackErrorAreaInvalidArguments()
	{
		ValidationBuilderXhtml builder = new ValidationBuilderXhtml();
		builder.setFallbackErrorArea(null, null);
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_errors_fallbackarea_basic");
		builder.setFallbackErrorArea(template, null);
		assertEquals("\n", template.getContent());
	}
	
	public void testSetFallbackErrorAreaBasic()
	{
		ValidationBuilderXhtml builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_errors_fallbackarea_basic");
		builder.setFallbackErrorArea(template, "my message");
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_fallbackarea_basic_out").getContent(), template.getContent());
	}
	
	public void testSetFallbackErrorAreaWildcardFormatted()
	{
		ValidationBuilderXhtml builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_errors_fallbackarea_wildcardformatted");
		builder.setFallbackErrorArea(template, "my message");
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_fallbackarea_wildcardformatted_out").getContent(), template.getContent());
	}
	
	public void testSetFallbackErrorAreaFormatted()
	{
		ValidationBuilderXhtml builder = new ValidationBuilderXhtml();

		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_errors_fallbackarea_formatted");
		builder.setFallbackErrorArea(template, "my message");
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_fallbackarea_formatted_out").getContent(), template.getContent());
	}

	public void testSetFallbackErrorAreaWildcardDecorated()
	{
		ValidationBuilderXhtml builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_errors_fallbackarea_wildcarddecorated");
		builder.setFallbackErrorArea(template, "my message");
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_fallbackarea_wildcarddecorated_out").getContent(), template.getContent());
	}
	
	public void testSetFallbackErrorAreaDecorated()
	{
		ValidationBuilderXhtml builder = new ValidationBuilderXhtml();

		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_errors_fallbackarea_decorated");
		builder.setFallbackErrorArea(template, "my message");
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_fallbackarea_decorated_out").getContent(), template.getContent());
	}

	public void testGenerateValidationErrorsInvalidArguments()
	{
		ValidationBuilderXhtml builder = new ValidationBuilderXhtml();
		assertEquals(0, builder.generateValidationErrors(null, null, null, null).size());
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_errors_raw");
		String raw_content = template.getContent();
		assertNotNull(template);
		assertEquals(0, builder.generateValidationErrors(template, null, null, null).size());
		assertEquals(raw_content, template.getContent());
		assertEquals(0, builder.generateValidationErrors(template, null, null, null).size());
		assertEquals(raw_content, template.getContent());
		assertEquals(0, builder.generateValidationErrors(template, new ArrayList<ValidationError>(), null, null).size());
		assertEquals(raw_content, template.getContent());
	}
	
	public void testGenerateValidationErrorsNovalues()
	{
		ValidationBuilderXhtml	builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_errors_novalues");
		
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.validate();
		builder.generateValidationErrors(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_novalues").getContent(), template.getContent());
	}
	
	public void testGenerateValidationErrorsRaw()
	{
		ValidationBuilderXhtml	builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_errors_raw");
		
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.validate();
		bean.addValidationError(new ValidationError.WRONGFORMAT("login"));
		builder.generateValidationErrors(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_raw_out").getContent(), template.getContent());
	}
	
	public void testGenerateValidationErrorsRawFallbackblock()
	{
		ValidationBuilderXhtml	builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_errors_raw_fallbackblock");
		
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.validate();
		bean.addValidationError(new ValidationError.WRONGFORMAT("login"));
		builder.generateValidationErrors(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_raw_fallbackblock_out").getContent(), template.getContent());
	}
	
	public void testGenerateValidationErrorsMessages()
	{
		ValidationBuilderXhtml	builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_errors_messages");
		
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.setAnotherlogin("123456789012345678901");
		bean.setPassword("1234567890");
		bean.setColors(new String[] {"invalid"});
		bean.validate();
		builder.generateValidationErrors(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_messages_out").getContent(), template.getContent());
	}
	
	public void testGenerateValidationErrorsMessagesPrefix()
	{
		ValidationBuilderXhtml	builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_errors_messages_prefix");
		
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.setPassword("1234567890");
		bean.setColors(new String[] {"invalid"});
		bean.validate();
		builder.generateValidationErrors(template, bean.getValidationErrors(), bean.getValidatedSubjects(), "prefix_");
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_messages_prefix_out").getContent(), template.getContent());
	}
	
	public void testGenerateValidationErrorsFormattedmessages()
	{
		ValidationBuilderXhtml	builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_errors_formattedmessages");
		
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.setAnotherlogin("123456789012345678901");
		bean.setPassword("1234567890");
		bean.setColors(new String[] {"invalid"});
		bean.validate();
		builder.generateValidationErrors(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_formattedmessages_out").getContent(), template.getContent());
	}
	
	public void testGenerateValidationErrorsFormattedmessagesNocontent()
	{
		ValidationBuilderXhtml	builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_errors_formattedmessages_nocontent");
		
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.setAnotherlogin("123456789012345678901");
		bean.setPassword("1234567890");
		bean.setColors(new String[] {"invalid"});
		bean.validate();
		builder.generateValidationErrors(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_formattedmessages_nocontent_out").getContent(), template.getContent());
	}
	
	public void testGenerateValidationErrorsFormattedmessagesPrefix()
	{
		ValidationBuilderXhtml	builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_errors_formattedmessages_prefix");
		
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.setAnotherlogin("123456789012345678901");
		bean.setPassword("1234567890");
		bean.setColors(new String[] {"invalid"});
		bean.validate();
		builder.generateValidationErrors(template, bean.getValidationErrors(), bean.getValidatedSubjects(), "prefix_");
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_formattedmessages_prefix_out").getContent(), template.getContent());
	}
	
	public void testGenerateValidationErrorsPositionedmessages()
	{
		ValidationBuilderXhtml	builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_errors_positionedmessages");
		
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.validate();
		builder.generateValidationErrors(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_positionedmessages_out1").getContent(), template.getContent());
		
		bean.resetValidation();
		bean.validate();
		bean.makeSubjectValid("anotherlogin");
		bean.makeSubjectValid("anotherpassword");
		builder.generateValidationErrors(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_positionedmessages_out2").getContent(), template.getContent());
		
		bean.resetValidation();
		bean.validate();
		bean.makeSubjectValid("login");
		bean.makeSubjectValid("customquestion");
		builder.generateValidationErrors(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_positionedmessages_out3").getContent(), template.getContent());
	}
	
	public void testGenerateValidationErrorsPositionedmessagesPrefix()
	{
		ValidationBuilderXhtml	builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_errors_positionedmessages_prefix");
		
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.validate();
		builder.generateValidationErrors(template, bean.getValidationErrors(), bean.getValidatedSubjects(), "prefix_");
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_positionedmessages_prefix_out1").getContent(), template.getContent());
		
		bean.resetValidation();
		bean.validate();
		bean.makeSubjectValid("anotherlogin");
		bean.makeSubjectValid("anotherpassword");
		builder.generateValidationErrors(template, bean.getValidationErrors(), bean.getValidatedSubjects(), "prefix_");
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_positionedmessages_prefix_out2").getContent(), template.getContent());
		
		bean.resetValidation();
		bean.validate();
		bean.makeSubjectValid("login");
		bean.makeSubjectValid("customquestion");
		builder.generateValidationErrors(template, bean.getValidationErrors(), bean.getValidatedSubjects(), "prefix_");
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_positionedmessages_prefix_out3").getContent(), template.getContent());
	}
	
	public void testGenerateValidationErrorsDecoratedmessages()
	{
		ValidationBuilderXhtml	builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_errors_decoratedmessages");
		
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.validate();
		builder.generateValidationErrors(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_decoratedmessages_out1").getContent(), template.getContent());
		
		bean.resetValidation();
		bean.validate();
		bean.makeSubjectValid("anotherlogin");
		bean.makeSubjectValid("anotherpassword");
		builder.generateValidationErrors(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_decoratedmessages_out2").getContent(), template.getContent());
		
		bean.resetValidation();
		bean.validate();
		bean.makeSubjectValid("login");
		bean.makeSubjectValid("customquestion");
		builder.generateValidationErrors(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_decoratedmessages_out3").getContent(), template.getContent());
	}
	
	public void testGenerateValidationErrorsDecoratedmessagesPrefix()
	{
		ValidationBuilderXhtml	builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_errors_decoratedmessages_prefix");
		
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.validate();
		builder.generateValidationErrors(template, bean.getValidationErrors(), bean.getValidatedSubjects(), "prefix_");
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_decoratedmessages_prefix_out1").getContent(), template.getContent());
		
		bean.resetValidation();
		bean.validate();
		bean.makeSubjectValid("anotherlogin");
		bean.makeSubjectValid("anotherpassword");
		builder.generateValidationErrors(template, bean.getValidationErrors(), bean.getValidatedSubjects(), "prefix_");
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_decoratedmessages_prefix_out2").getContent(), template.getContent());
		
		bean.resetValidation();
		bean.validate();
		bean.makeSubjectValid("login");
		bean.makeSubjectValid("customquestion");
		builder.generateValidationErrors(template, bean.getValidationErrors(), bean.getValidatedSubjects(), "prefix_");
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_decoratedmessages_prefix_out3").getContent(), template.getContent());
	}
	
	public void testRemoveValidationErrorsInvalidArguments()
	{
		ValidationBuilderXhtml builder = new ValidationBuilderXhtml();
		builder.removeValidationErrors(null, null, null);
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_errors_raw");
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.validate();
		bean.addValidationError(new ValidationError.WRONGFORMAT("login"));
		builder.generateValidationErrors(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
		String raw_content = template.getContent();
		builder.removeValidationErrors(template, null, null);
		assertEquals(raw_content, template.getContent());
		builder.removeValidationErrors(template, new ArrayList<String>(), null);
		assertEquals(raw_content, template.getContent());
	}
	
	public void testRemoveValidationErrorsNovalues()
	{
		ValidationBuilderXhtml	builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_errors_novalues");
		
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.validate();
		builder.generateValidationErrors(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_novalues").getContent(), template.getContent());
		builder.removeValidationErrors(template, bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_novalues").getContent(), template.getContent());
	}
	
	public void testRemoveValidationErrorsRaw()
	{
		ValidationBuilderXhtml	builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_errors_raw");
		
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.validate();
		bean.addValidationError(new ValidationError.WRONGFORMAT("login"));
		builder.generateValidationErrors(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_raw_out").getContent(), template.getContent());
		
		builder.removeValidationErrors(template, bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_raw").getContent(), template.getContent());
	}
	
	public void testRemoveValidationErrorsDecoratedmessages()
	{
		ValidationBuilderXhtml	builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_errors_decoratedmessages");
		
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.validate();
		builder.generateValidationErrors(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_decoratedmessages_out1").getContent(), template.getContent());
		
		builder.removeValidationErrors(template, bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_decoratedmessages").getContent(), template.getContent());
	}
	
	public void testRemoveValidationErrorsDecoratedmessagesMissingSubjects()
	{
		ValidationBuilderXhtml	builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_errors_decoratedmessages");
		
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.validate();
		builder.generateValidationErrors(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_decoratedmessages_out1").getContent(), template.getContent());
		
		List<String> subjects = bean.getValidatedSubjects();
		subjects.remove(0);
		subjects.remove(0);
		subjects.remove(0);
		subjects.remove(0);
		subjects.remove(0);
		builder.removeValidationErrors(template, subjects, null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_decoratedmessages_partly_removed").getContent(), template.getContent());
	}
	
	public void testRemoveValidationErrorsDecoratedmessagesPrefix()
	{
		ValidationBuilderXhtml	builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_errors_decoratedmessages_prefix");
		
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.validate();
		builder.generateValidationErrors(template, bean.getValidationErrors(), bean.getValidatedSubjects(), "prefix_");
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_decoratedmessages_prefix_out1").getContent(), template.getContent());
		
		builder.removeValidationErrors(template, bean.getValidatedSubjects(), "prefix_");
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_decoratedmessages_prefix").getContent(), template.getContent());
	}
	
	public void testGenerateErrorMarkingsInvalidArguments()
	{
		ValidationBuilderXhtml builder = new ValidationBuilderXhtml();
		assertEquals(0, builder.generateErrorMarkings(null, null, null, null).size());
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_mark_simple");
		String raw_content = template.getContent();
		assertNotNull(template);
		assertEquals(0, builder.generateErrorMarkings(template, null, null, null).size());
		assertEquals(raw_content, template.getContent());
		assertEquals(0, builder.generateErrorMarkings(template, new ArrayList<ValidationError>(), null, null).size());
		assertEquals(raw_content, template.getContent());
	}
	
	public void testGenerateErrorMarkingsNoValues()
	{
		ValidationBuilderXhtml	builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_errors_novalues");
		
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.validate();
		builder.generateErrorMarkings(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_novalues").getContent(), template.getContent());
	}
	
	public void testGenerateErrorMarkingsMising()
	{
		ValidationBuilderXhtml	builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_mark_missing");
		
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.validate();
		try
		{
			builder.generateErrorMarkings(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
			fail("exception not thrown");
		}
		catch (MissingMarkingBlockException e)
		{
			assertEquals("MARK:ERROR", e.getBlockId());
		}
	}
	
	public void testGenerateErrorMarkingsSimple()
	{
		ValidationBuilderXhtml	builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_mark_simple");
		
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.validate();
		builder.generateErrorMarkings(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_mark_simple_out").getContent(), template.getContent());
	}
	
	public void testGenerateErrorMarkingsPositioned()
	{
		ValidationBuilderXhtml	builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_mark_positioned");
		
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.validate();
		builder.generateErrorMarkings(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_mark_positioned_out1").getContent(), template.getContent());

		bean.resetValidation();
		bean.validate();
		bean.makeSubjectValid("anotherlogin");
		bean.makeSubjectValid("anotherpassword");
		bean.makeSubjectValid("anothercustomquestion");
		bean.addValidationError(new ValidationError.INCOMPLETE("customoptions"));
		builder.generateErrorMarkings(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_mark_positioned_out2").getContent(), template.getContent());
		
		bean.resetValidation();
		bean.validate();
		bean.makeSubjectValid("login");
		bean.makeSubjectValid("customquestion");
		bean.makeSubjectValid("options");
		bean.addValidationError(new ValidationError.INCOMPLETE("customoptions"));
		builder.generateErrorMarkings(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_mark_positioned_out3").getContent(), template.getContent());
	}
	
	public void testGenerateErrorMarkingsSelective()
	{
		ValidationBuilderXhtml	builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_mark_selective");
		
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.validate();
		builder.generateErrorMarkings(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_mark_selective_out1").getContent(), template.getContent());

		bean.resetValidation();
		bean.validate();
		bean.makeSubjectValid("anotherlogin");
		bean.makeSubjectValid("anotherpassword");
		bean.makeSubjectValid("anothercustomquestion");
		bean.addValidationError(new ValidationError.INCOMPLETE("customoptions"));
		builder.generateErrorMarkings(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_mark_selective_out2").getContent(), template.getContent());
		
		bean.resetValidation();
		bean.validate();
		bean.makeSubjectValid("login");
		bean.makeSubjectValid("customquestion");
		bean.makeSubjectValid("options");
		bean.addValidationError(new ValidationError.INCOMPLETE("customoptions"));
		builder.generateErrorMarkings(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_mark_selective_out3").getContent(), template.getContent());
	}
	
	public void testGenerateErrorMarkingsSimplePrefix()
	{
		ValidationBuilderXhtml	builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_mark_simple_prefix");
		
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.validate();
		builder.generateErrorMarkings(template, bean.getValidationErrors(), bean.getValidatedSubjects(), "prefix_");
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_mark_simple_out").getContent(), template.getContent());
	}
	
	public void testGenerateErrorMarkingsPositionedPrefix()
	{
		ValidationBuilderXhtml	builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_mark_positioned_prefix");
		
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.validate();
		builder.generateErrorMarkings(template, bean.getValidationErrors(), bean.getValidatedSubjects(), "prefix_");
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_mark_positioned_out1").getContent(), template.getContent());

		bean.resetValidation();
		bean.validate();
		bean.makeSubjectValid("anotherlogin");
		bean.makeSubjectValid("anotherpassword");
		bean.makeSubjectValid("anothercustomquestion");
		bean.addValidationError(new ValidationError.INCOMPLETE("customoptions"));
		builder.generateErrorMarkings(template, bean.getValidationErrors(), bean.getValidatedSubjects(), "prefix_");
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_mark_positioned_out2").getContent(), template.getContent());
		
		bean.resetValidation();
		bean.validate();
		bean.makeSubjectValid("login");
		bean.makeSubjectValid("customquestion");
		bean.makeSubjectValid("options");
		bean.addValidationError(new ValidationError.INCOMPLETE("customoptions"));
		builder.generateErrorMarkings(template, bean.getValidationErrors(), bean.getValidatedSubjects(), "prefix_");
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_mark_positioned_out3").getContent(), template.getContent());
	}
	
	public void testGenerateErrorMarkingsSelectivePrefix()
	{
		ValidationBuilderXhtml	builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_mark_selective_prefix");
		
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.validate();
		builder.generateErrorMarkings(template, bean.getValidationErrors(), bean.getValidatedSubjects(), "prefix_");
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_mark_selective_out1").getContent(), template.getContent());

		bean.resetValidation();
		bean.validate();
		bean.makeSubjectValid("anotherlogin");
		bean.makeSubjectValid("anotherpassword");
		bean.makeSubjectValid("anothercustomquestion");
		bean.addValidationError(new ValidationError.INCOMPLETE("customoptions"));
		builder.generateErrorMarkings(template, bean.getValidationErrors(), bean.getValidatedSubjects(), "prefix_");
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_mark_selective_out2").getContent(), template.getContent());
		
		bean.resetValidation();
		bean.validate();
		bean.makeSubjectValid("login");
		bean.makeSubjectValid("customquestion");
		bean.makeSubjectValid("options");
		bean.addValidationError(new ValidationError.INCOMPLETE("customoptions"));
		builder.generateErrorMarkings(template, bean.getValidationErrors(), bean.getValidatedSubjects(), "prefix_");
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_mark_selective_out3").getContent(), template.getContent());
	}
	
	public void testRemoveErrorMarkingsInvalidArguments()
	{
		ValidationBuilderXhtml builder = new ValidationBuilderXhtml();
		builder.removeErrorMarkings(null, null, null);
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_mark_simple");
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.validate();
		builder.generateErrorMarkings(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_mark_simple_out").getContent(), template.getContent());
		String raw_content = template.getContent();
		builder.removeErrorMarkings(template, null, null);
		assertEquals(raw_content, template.getContent());
		builder.removeErrorMarkings(template, new ArrayList<String>(), null);
		assertEquals(raw_content, template.getContent());
	}
	
	public void testRemoveErrorMarkingsNoValues()
	{
		ValidationBuilderXhtml	builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_errors_novalues");
		
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.validate();
		builder.generateErrorMarkings(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_novalues").getContent(), template.getContent());
		builder.removeErrorMarkings(template, bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_errors_novalues").getContent(), template.getContent());
	}
	
	public void testRemoveErrorMarkingsPositioned()
	{
		ValidationBuilderXhtml	builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_mark_positioned");
		
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.validate();
		builder.generateErrorMarkings(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_mark_positioned_out1").getContent(), template.getContent());
		builder.removeErrorMarkings(template, bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_mark_positioned").getContent(), template.getContent());
	}
	
	public void testRemoveErrorMarkingsPositionedPrefix()
	{
		ValidationBuilderXhtml	builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_mark_positioned_prefix");
		
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.validate();
		builder.generateErrorMarkings(template, bean.getValidationErrors(), bean.getValidatedSubjects(), "prefix_");
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_mark_positioned_out1").getContent(), template.getContent());
		builder.removeErrorMarkings(template, bean.getValidatedSubjects(), "prefix_");
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_mark_positioned_prefix").getContent(), template.getContent());
	}
	
	public void testRemoveErrorMarkingsPositionedMissingSubjects()
	{
		ValidationBuilderXhtml	builder = new ValidationBuilderXhtml();
		
		Template template = TemplateFactory.ENGINEHTML.get("validationbuilder_mark_positioned");
		
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.validate();
		builder.generateErrorMarkings(template, bean.getValidationErrors(), bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_mark_positioned_out1").getContent(), template.getContent());

		List<String> subjects = bean.getValidatedSubjects();
		subjects.remove(0);
		subjects.remove(0);
		subjects.remove(0);
		subjects.remove(0);
		subjects.remove(0);
		builder.removeErrorMarkings(template, bean.getValidatedSubjects(), null);
		assertEquals(TemplateFactory.ENGINEHTML.get("validationbuilder_mark_positioned_partly_removed").getContent(), template.getContent());
	}
}
