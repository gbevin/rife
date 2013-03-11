/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestFormBuilderXml.java 3947 2008-05-05 12:23:39Z gbevin $
 */
package com.uwyn.rife.site;

import com.uwyn.rife.template.Template;
import com.uwyn.rife.template.TemplateFactory;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.StringUtils;
import com.uwyn.rife.tools.exceptions.BeanUtilsException;
import junit.framework.TestCase;

import java.util.*;

public class TestFormBuilderXml extends TestCase
{
	public enum RadioInListEnum {a1, a3, a2}

	public enum CheckboxInListEnum {
		v1(1), v3(3), v2(2);

		private int mNumber;

		CheckboxInListEnum(int number)
		{
			mNumber = number;
		}

		public String toString()
		{
			return String.valueOf(mNumber);
		}
	}
	public enum SelectInListEnum {black, red, green, blue}
	public enum SelectInListEnum2 {black, red, blue}
	public enum SelectInListEnum3 {
		v1(1), v3(3), v5(5), v9(9);

		private int mNumber;

		SelectInListEnum3(int number)
		{
			mNumber = number;
		}

		public String toString()
		{
			return String.valueOf(mNumber);
		}
	}

	public TestFormBuilderXml(String name)
	{
		super(name);
	}

	public void testInstantiate()
	{
		FormBuilderXml builder = new FormBuilderXml();
		assertNotNull(builder);
	}

	public void testClone()
	{
		FormBuilderXml builder1 = new FormBuilderXml();
		FormBuilderXml builder2 = (FormBuilderXml)builder1.clone();
		assertNotNull(builder2);
		assertNotSame(builder1, builder2);
	}

	public void testGetValidationBuilder()
	{
		FormBuilderXml builder = new FormBuilderXml();
		assertNotNull(builder.getValidationBuilder());
	}

	public void testGenerateFieldInvalidArguments()
	{
		FormBuilderXml builder = new FormBuilderXml();
		assertEquals(0, builder.generateField(null, (ConstrainedProperty)null, null, null).size());
		assertEquals(0, builder.generateField(null, (String)null, null, null).size());

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");
		String raw_content = template.getContent();
		assertNotNull(template);
		assertEquals(0, builder.generateField(template, (ConstrainedProperty)null, null, null).size());
		assertEquals(raw_content, template.getContent());
		assertEquals(0, builder.generateField(template, (String)null, null, null).size());
		assertEquals(raw_content, template.getContent());
	}

	public void testRemoveFieldInvalidArguments()
	{
		FormBuilderXml builder = new FormBuilderXml();
		builder.removeField(null, null, null);

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");
		String raw_content = template.getContent();
		assertNotNull(template);
		builder.removeField(template, null, null);
		assertEquals(raw_content, template.getContent());
		builder.removeField(template, "", null);
		assertEquals(raw_content, template.getContent());
	}

	public void testGenerateFieldHiddenWithDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, "hidden", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:HIDDEN:hidden", set_values.iterator().next());
		assertEquals("<field><name>hidden</name><type>hidden</type><id>thehiddenone</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[52]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("hidden"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:HIDDEN:hidden", set_values.iterator().next());
		assertEquals("<field><name>hidden</name><type>hidden</type><id>thehiddenone</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[52]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("hidden"), new String[] {null, "één"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:HIDDEN:hidden", set_values.iterator().next());
		assertEquals("<field><name>hidden</name><type>hidden</type><id>thehiddenone</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[52]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("hidden"), new String[] {"één"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:HIDDEN:hidden", set_values.iterator().next());
		assertEquals("<field><name>hidden</name><type>hidden</type><id>thehiddenone</id><value>één</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[52]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("hidden").defaultValue("non&e").maxLength(20), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:HIDDEN:hidden", set_values.iterator().next());
		assertEquals("<field><name>hidden</name><type>hidden</type><id>thehiddenone</id><value>non&amp;e</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[52]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("hidden").defaultValue("non&e").maxLength(20), new String[] {"hé", "you"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:HIDDEN:hidden", set_values.iterator().next());
		assertEquals("<field><name>hidden</name><type>hidden</type><id>thehiddenone</id><value>hé</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[52]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:HIDDEN:hidden", "already set");
		set_values = builder.generateField(template, new ConstrainedProperty("hidden"), null, null);
		assertEquals(0, set_values.size());
		assertEquals("already set", StringUtils.splitToArray(template.getContent(), "\n")[52]);
		template.removeValue("FORM:HIDDEN:hidden");
	}

	public void testGenerateFieldHiddenWithoutDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, "anotherhidden", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:HIDDEN:anotherhidden", set_values.iterator().next());
		assertEquals("<field><name>anotherhidden</name><type>hidden</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[53]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("anotherhidden"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:HIDDEN:anotherhidden", set_values.iterator().next());
		assertEquals("<field><name>anotherhidden</name><type>hidden</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[53]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("anotherhidden"), new String[] {"één"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:HIDDEN:anotherhidden", set_values.iterator().next());
		assertEquals("<field><name>anotherhidden</name><type>hidden</type><value>één</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[53]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("anotherhidden").defaultValue("non&e").maxLength(20), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:HIDDEN:anotherhidden", set_values.iterator().next());
		assertEquals("<field><name>anotherhidden</name><type>hidden</type><value>non&amp;e</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[53]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();
	}

	public void testGenerateFieldHiddenPrefix()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, "hidden", null, "another");
		assertEquals(1, set_values.size());
		assertEquals("FORM:HIDDEN:anotherhidden", set_values.iterator().next());
		assertEquals("<field><name>anotherhidden</name><type>hidden</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[53]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("hidden"), null, "another");
		assertEquals(1, set_values.size());
		assertEquals("FORM:HIDDEN:anotherhidden", set_values.iterator().next());
		assertEquals("<field><name>anotherhidden</name><type>hidden</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[53]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:HIDDEN:anotherhidden", "already set");
		set_values = builder.generateField(template, new ConstrainedProperty("hidden"), null, "another");
		assertEquals(0, set_values.size());
		assertEquals("already set", StringUtils.splitToArray(template.getContent(), "\n")[53]);
		template.removeValue("FORM:HIDDEN:anotherhidden");
	}

	public void testGenerateFieldHiddenTemplateNameWithDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields_templatename");

		set_values = builder.replaceField(template, "templatenamehidden", "hidden", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:HIDDEN:templatenamehidden", set_values.iterator().next());
		assertEquals("<field><name>hidden</name><type>hidden</type><id>thehiddenone</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[52]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamehidden", new ConstrainedProperty("hidden"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:HIDDEN:templatenamehidden", set_values.iterator().next());
		assertEquals("<field><name>hidden</name><type>hidden</type><id>thehiddenone</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[52]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamehidden", new ConstrainedProperty("hidden"), new String[] {null, "één"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:HIDDEN:templatenamehidden", set_values.iterator().next());
		assertEquals("<field><name>hidden</name><type>hidden</type><id>thehiddenone</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[52]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamehidden", new ConstrainedProperty("hidden"), new String[] {"één"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:HIDDEN:templatenamehidden", set_values.iterator().next());
		assertEquals("<field><name>hidden</name><type>hidden</type><id>thehiddenone</id><value>één</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[52]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamehidden", new ConstrainedProperty("hidden").defaultValue("non&e").maxLength(20), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:HIDDEN:templatenamehidden", set_values.iterator().next());
		assertEquals("<field><name>hidden</name><type>hidden</type><id>thehiddenone</id><value>non&amp;e</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[52]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamehidden", new ConstrainedProperty("hidden").defaultValue("non&e").maxLength(20), new String[] {"hé", "you"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:HIDDEN:templatenamehidden", set_values.iterator().next());
		assertEquals("<field><name>hidden</name><type>hidden</type><id>thehiddenone</id><value>hé</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[52]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:HIDDEN:templatenamehidden", "already set");
		set_values = builder.replaceField(template, "templatenamehidden", new ConstrainedProperty("hidden"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:HIDDEN:templatenamehidden", set_values.iterator().next());
		assertEquals("<field><name>hidden</name><type>hidden</type><id>thehiddenone</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[52]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();
	}

	public void testRemoveFieldHidden()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		String raw_content = template.getContent();

		builder.generateField(template, new ConstrainedProperty("hidden").defaultValue("non&e").maxLength(20), new String[] {"hé", "you"}, null);
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "hidden", null);
		assertEquals(raw_content, template.getContent());
	}

	public void testRemoveFieldHiddenPrefix()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		String raw_content = template.getContent();

		builder.generateField(template, new ConstrainedProperty("hidden").defaultValue("non&e").maxLength(20), new String[] {"hé", "you"}, "another");
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "hidden", "another");
		assertEquals(raw_content, template.getContent());
	}

	public void testRemoveFieldHiddenTemplateName()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields_templatename");

		String raw_content = template.getContent();

		builder.replaceField(template, "templatenamehidden", new ConstrainedProperty("hidden").defaultValue("non&e").maxLength(20), new String[] {"hé", "you"}, null);
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "templatenamehidden");
		assertEquals(raw_content, template.getContent());
	}

	public void testGenerateFieldInputWithDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;
		Iterator<String>	set_values_it;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, "login", null, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:INPUT:login", set_values_it.next());
		assertEquals("FORM:DISPLAY:login", set_values_it.next());
		assertEquals("<field><name>login</name><type>text</type><size>10</size></field>", StringUtils.splitToArray(template.getContent(), "\n")[0]);
		assertEquals("<field><value></value><class>thedisplayedone</class></field>", StringUtils.splitToArray(template.getContent(), "\n")[54]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("login"), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:INPUT:login", set_values_it.next());
		assertEquals("FORM:DISPLAY:login", set_values_it.next());
		assertEquals("<field><name>login</name><type>text</type><size>10</size></field>", StringUtils.splitToArray(template.getContent(), "\n")[0]);
		assertEquals("<field><value></value><class>thedisplayedone</class></field>", StringUtils.splitToArray(template.getContent(), "\n")[54]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("login"), new String[] {null, "één"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:INPUT:login", set_values_it.next());
		assertEquals("FORM:DISPLAY:login", set_values_it.next());
		assertEquals("<field><name>login</name><type>text</type><size>10</size></field>", StringUtils.splitToArray(template.getContent(), "\n")[0]);
		assertEquals("<field><value></value><class>thedisplayedone</class></field><field><value>één</value><class>thedisplayedone</class></field>", StringUtils.splitToArray(template.getContent(), "\n")[54]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("login"), new String[] {"één"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:INPUT:login", set_values_it.next());
		assertEquals("FORM:DISPLAY:login", set_values_it.next());
		assertEquals("<field><name>login</name><type>text</type><size>10</size><value>één</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[0]);
		assertEquals("<field><value>één</value><class>thedisplayedone</class></field>", StringUtils.splitToArray(template.getContent(), "\n")[54]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("login").defaultValue("non&e").maxLength(20), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:INPUT:login", set_values_it.next());
		assertEquals("FORM:DISPLAY:login", set_values_it.next());
		assertEquals("<field><name>login</name><type>text</type><size>10</size><value>non&amp;e</value><maxlength>20</maxlength></field>", StringUtils.splitToArray(template.getContent(), "\n")[0]);
		assertEquals("<field><value>non&amp;e</value><class>thedisplayedone</class></field>", StringUtils.splitToArray(template.getContent(), "\n")[54]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("login").defaultValue("non&e").maxLength(20), new String[] {"hé", "you"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:INPUT:login", set_values_it.next());
		assertEquals("FORM:DISPLAY:login", set_values_it.next());
		assertEquals("<field><name>login</name><type>text</type><size>10</size><value>hé</value><maxlength>20</maxlength></field>", StringUtils.splitToArray(template.getContent(), "\n")[0]);
		assertEquals("<field><value>hé</value><class>thedisplayedone</class></field><field><value>you</value><class>thedisplayedone</class></field>", StringUtils.splitToArray(template.getContent(), "\n")[54]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		template.setValue("FORM:INPUT:login", "already set");
		template.setValue("FORM:DISPLAY:login", "already set too");
		set_values = builder.generateField(template, new ConstrainedProperty("login"), null, null);
		assertEquals(0, set_values.size());
		assertEquals("already set", StringUtils.splitToArray(template.getContent(), "\n")[0]);
		assertEquals("already set too", StringUtils.splitToArray(template.getContent(), "\n")[54]);
		template.removeValue("FORM:INPUT:login");
		template.removeValue("FORM:DISPLAY:login");
	}

	public void testGenerateFieldInputWithoutDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;
		Iterator<String>	set_values_it;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, "anotherlogin", null, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:INPUT:anotherlogin", set_values_it.next());
		assertEquals("FORM:DISPLAY:anotherlogin", set_values_it.next());
		assertEquals("<field><name>anotherlogin</name><type>text</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[1]);
		assertEquals("<field><value></value></field>", StringUtils.splitToArray(template.getContent(), "\n")[55]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("anotherlogin"), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:INPUT:anotherlogin", set_values_it.next());
		assertEquals("FORM:DISPLAY:anotherlogin", set_values_it.next());
		assertEquals("<field><name>anotherlogin</name><type>text</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[1]);
		assertEquals("<field><value></value></field>", StringUtils.splitToArray(template.getContent(), "\n")[55]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("anotherlogin"), new String[] {"één"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:INPUT:anotherlogin", set_values_it.next());
		assertEquals("FORM:DISPLAY:anotherlogin", set_values_it.next());
		assertEquals("FORM:INPUT:anotherlogin", set_values.iterator().next());
		assertEquals("<field><name>anotherlogin</name><type>text</type><value>één</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[1]);
		assertEquals("<field><value>één</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[55]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("anotherlogin").defaultValue("non&e").maxLength(20), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:INPUT:anotherlogin", set_values_it.next());
		assertEquals("FORM:DISPLAY:anotherlogin", set_values_it.next());
		assertEquals("FORM:INPUT:anotherlogin", set_values.iterator().next());
		assertEquals("<field><name>anotherlogin</name><type>text</type><value>non&amp;e</value><maxlength>20</maxlength></field>", StringUtils.splitToArray(template.getContent(), "\n")[1]);
		assertEquals("<field><value>non&amp;e</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[55]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();
	}

	public void testGenerateFieldInputPrefix()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;
		Iterator<String>	set_values_it;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, "login", null, "another");
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:INPUT:anotherlogin", set_values_it.next());
		assertEquals("FORM:DISPLAY:anotherlogin", set_values_it.next());
		assertEquals("<field><name>anotherlogin</name><type>text</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[1]);
		assertEquals("<field><value></value></field>", StringUtils.splitToArray(template.getContent(), "\n")[55]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("login"), null, "another");
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:INPUT:anotherlogin", set_values_it.next());
		assertEquals("FORM:DISPLAY:anotherlogin", set_values_it.next());
		assertEquals("<field><name>anotherlogin</name><type>text</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[1]);
		assertEquals("<field><value></value></field>", StringUtils.splitToArray(template.getContent(), "\n")[55]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		template.setValue("FORM:INPUT:anotherlogin", "already set");
		template.setValue("FORM:DISPLAY:anotherlogin", "already set too");
		set_values = builder.generateField(template, new ConstrainedProperty("login"), null, "another");
		assertEquals(0, set_values.size());
		assertEquals("already set", StringUtils.splitToArray(template.getContent(), "\n")[1]);
		assertEquals("already set too", StringUtils.splitToArray(template.getContent(), "\n")[55]);
		template.removeValue("FORM:INPUT:anotherlogin");
		template.removeValue("FORM:DISPLAY:anotherlogin");
	}

	public void testGenerateFieldInputWithDefaultTemplateName()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;
		Iterator<String>	set_values_it;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields_templatename");

		set_values = builder.replaceField(template, "templatenamelogin", "login", null, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:INPUT:templatenamelogin", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamelogin", set_values_it.next());
		assertEquals("<field><name>login</name><type>text</type><size>10</size></field>", StringUtils.splitToArray(template.getContent(), "\n")[0]);
		assertEquals("<field><value></value><class>thedisplayedone</class></field>", StringUtils.splitToArray(template.getContent(), "\n")[54]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamelogin", new ConstrainedProperty("login"), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:INPUT:templatenamelogin", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamelogin", set_values_it.next());
		assertEquals("<field><name>login</name><type>text</type><size>10</size></field>", StringUtils.splitToArray(template.getContent(), "\n")[0]);
		assertEquals("<field><value></value><class>thedisplayedone</class></field>", StringUtils.splitToArray(template.getContent(), "\n")[54]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamelogin", new ConstrainedProperty("login"), new String[] {null, "één"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:INPUT:templatenamelogin", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamelogin", set_values_it.next());
		assertEquals("<field><name>login</name><type>text</type><size>10</size></field>", StringUtils.splitToArray(template.getContent(), "\n")[0]);
		assertEquals("<field><value></value><class>thedisplayedone</class></field><field><value>één</value><class>thedisplayedone</class></field>", StringUtils.splitToArray(template.getContent(), "\n")[54]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamelogin", new ConstrainedProperty("login"), new String[] {"één"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:INPUT:templatenamelogin", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamelogin", set_values_it.next());
		assertEquals("<field><name>login</name><type>text</type><size>10</size><value>één</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[0]);
		assertEquals("<field><value>één</value><class>thedisplayedone</class></field>", StringUtils.splitToArray(template.getContent(), "\n")[54]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamelogin", new ConstrainedProperty("login").defaultValue("non&e").maxLength(20), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:INPUT:templatenamelogin", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamelogin", set_values_it.next());
		assertEquals("<field><name>login</name><type>text</type><size>10</size><value>non&amp;e</value><maxlength>20</maxlength></field>", StringUtils.splitToArray(template.getContent(), "\n")[0]);
		assertEquals("<field><value>non&amp;e</value><class>thedisplayedone</class></field>", StringUtils.splitToArray(template.getContent(), "\n")[54]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamelogin", new ConstrainedProperty("login").defaultValue("non&e").maxLength(20), new String[] {"hé", "you"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:INPUT:templatenamelogin", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamelogin", set_values_it.next());
		assertEquals("<field><name>login</name><type>text</type><size>10</size><value>hé</value><maxlength>20</maxlength></field>", StringUtils.splitToArray(template.getContent(), "\n")[0]);
		assertEquals("<field><value>hé</value><class>thedisplayedone</class></field><field><value>you</value><class>thedisplayedone</class></field>", StringUtils.splitToArray(template.getContent(), "\n")[54]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		template.setValue("FORM:INPUT:templatenamelogin", "already set");
		template.setValue("FORM:DISPLAY:templatenamelogin", "already set too");
		set_values = builder.replaceField(template, "templatenamelogin", new ConstrainedProperty("login"), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:INPUT:templatenamelogin", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamelogin", set_values_it.next());
		assertEquals("<field><name>login</name><type>text</type><size>10</size></field>", StringUtils.splitToArray(template.getContent(), "\n")[0]);
		assertEquals("<field><value></value><class>thedisplayedone</class></field>", StringUtils.splitToArray(template.getContent(), "\n")[54]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();
	}

	public void testRemoveFieldInput()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		String raw_content = template.getContent();

		builder.generateField(template, new ConstrainedProperty("login").defaultValue("non&e").maxLength(20), new String[] {"hé", "you"}, null);
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "login", null);
		assertEquals(raw_content, template.getContent());
	}

	public void testRemoveFieldInputPrefix()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		String raw_content = template.getContent();

		builder.generateField(template, new ConstrainedProperty("login").defaultValue("non&e").maxLength(20), new String[] {"hé", "you"}, "another");
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "login", "another");
		assertEquals(raw_content, template.getContent());
	}

	public void testRemoveFieldInputTemplateName()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields_templatename");

		String raw_content = template.getContent();

		builder.replaceField(template, "templatenamelogin", new ConstrainedProperty("login").defaultValue("non&e").maxLength(20), new String[] {"hé", "you"}, null);
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "templatenamelogin");
		assertEquals(raw_content, template.getContent());
	}

	public void testGenerateFieldSecretWithDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, "password", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:SECRET:password", set_values.iterator().next());
		assertEquals("<field><name>password</name><type>secret</type><size>10</size></field>", StringUtils.splitToArray(template.getContent(), "\n")[2]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("password"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:SECRET:password", set_values.iterator().next());
		assertEquals("<field><name>password</name><type>secret</type><size>10</size></field>", StringUtils.splitToArray(template.getContent(), "\n")[2]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("password"), new String[] {null, "één"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:SECRET:password", set_values.iterator().next());
		assertEquals("<field><name>password</name><type>secret</type><size>10</size></field>", StringUtils.splitToArray(template.getContent(), "\n")[2]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("password"), new String[] {"één"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:SECRET:password", set_values.iterator().next());
		assertEquals("<field><name>password</name><type>secret</type><size>10</size></field>", StringUtils.splitToArray(template.getContent(), "\n")[2]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("password").defaultValue("non&e").maxLength(20), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:SECRET:password", set_values.iterator().next());
		assertEquals("<field><name>password</name><type>secret</type><size>10</size><maxlength>20</maxlength></field>", StringUtils.splitToArray(template.getContent(), "\n")[2]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("password").defaultValue("non&e").maxLength(20), new String[] {"hé", "you"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:SECRET:password", set_values.iterator().next());
		assertEquals("<field><name>password</name><type>secret</type><size>10</size><maxlength>20</maxlength></field>", StringUtils.splitToArray(template.getContent(), "\n")[2]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:SECRET:password", "already set");
		set_values = builder.generateField(template, new ConstrainedProperty("password"), null, null);
		assertEquals(0, set_values.size());
		assertEquals("already set", StringUtils.splitToArray(template.getContent(), "\n")[2]);
		template.removeValue("FORM:SECRET:password");
	}

	public void testGenerateFieldSecretWithoutDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, "anotherpassword", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:SECRET:anotherpassword", set_values.iterator().next());
		assertEquals("<field><name>anotherpassword</name><type>secret</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[3]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("anotherpassword"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:SECRET:anotherpassword", set_values.iterator().next());
		assertEquals("<field><name>anotherpassword</name><type>secret</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[3]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("anotherpassword"), new String[] {"één"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:SECRET:anotherpassword", set_values.iterator().next());
		assertEquals("<field><name>anotherpassword</name><type>secret</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[3]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("anotherpassword").defaultValue("non&e").maxLength(20), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:SECRET:anotherpassword", set_values.iterator().next());
		assertEquals("<field><name>anotherpassword</name><type>secret</type><maxlength>20</maxlength></field>", StringUtils.splitToArray(template.getContent(), "\n")[3]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();
	}

	public void testGenerateFieldSecretPrefix()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, "password", null, "another");
		assertEquals(1, set_values.size());
		assertEquals("FORM:SECRET:anotherpassword", set_values.iterator().next());
		assertEquals("<field><name>anotherpassword</name><type>secret</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[3]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("password"), null, "another");
		assertEquals(1, set_values.size());
		assertEquals("FORM:SECRET:anotherpassword", set_values.iterator().next());
		assertEquals("<field><name>anotherpassword</name><type>secret</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[3]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:SECRET:anotherpassword", "already set");
		set_values = builder.generateField(template, new ConstrainedProperty("password"), null, "another");
		assertEquals(0, set_values.size());
		assertEquals("already set", StringUtils.splitToArray(template.getContent(), "\n")[3]);
		template.removeValue("FORM:SECRET:anotherpassword");
	}

	public void testGenerateFieldSecretTemplateNameWithDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields_templatename");

		set_values = builder.replaceField(template, "templatenamepassword", "password", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:SECRET:templatenamepassword", set_values.iterator().next());
		assertEquals("<field><name>password</name><type>secret</type><size>10</size></field>", StringUtils.splitToArray(template.getContent(), "\n")[2]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamepassword", new ConstrainedProperty("password"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:SECRET:templatenamepassword", set_values.iterator().next());
		assertEquals("<field><name>password</name><type>secret</type><size>10</size></field>", StringUtils.splitToArray(template.getContent(), "\n")[2]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamepassword", new ConstrainedProperty("password"), new String[] {null, "één"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:SECRET:templatenamepassword", set_values.iterator().next());
		assertEquals("<field><name>password</name><type>secret</type><size>10</size></field>", StringUtils.splitToArray(template.getContent(), "\n")[2]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamepassword", new ConstrainedProperty("password"), new String[] {"één"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:SECRET:templatenamepassword", set_values.iterator().next());
		assertEquals("<field><name>password</name><type>secret</type><size>10</size></field>", StringUtils.splitToArray(template.getContent(), "\n")[2]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamepassword", new ConstrainedProperty("password").defaultValue("non&e").maxLength(20), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:SECRET:templatenamepassword", set_values.iterator().next());
		assertEquals("<field><name>password</name><type>secret</type><size>10</size><maxlength>20</maxlength></field>", StringUtils.splitToArray(template.getContent(), "\n")[2]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamepassword", new ConstrainedProperty("password").defaultValue("non&e").maxLength(20), new String[] {"hé", "you"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:SECRET:templatenamepassword", set_values.iterator().next());
		assertEquals("<field><name>password</name><type>secret</type><size>10</size><maxlength>20</maxlength></field>", StringUtils.splitToArray(template.getContent(), "\n")[2]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:SECRET:templatenamepassword", "already set");
		set_values = builder.replaceField(template, "templatenamepassword", new ConstrainedProperty("password"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:SECRET:templatenamepassword", set_values.iterator().next());
		assertEquals("<field><name>password</name><type>secret</type><size>10</size></field>", StringUtils.splitToArray(template.getContent(), "\n")[2]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();
	}

	public void testRemoveFieldSecret()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		String raw_content = template.getContent();

		builder.generateField(template, new ConstrainedProperty("password").defaultValue("non&e").maxLength(20), new String[] {"hé", "you"}, null);
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "password", null);
		assertEquals(raw_content, template.getContent());
	}

	public void testRemoveFieldSecretPrefix()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		String raw_content = template.getContent();

		builder.generateField(template, new ConstrainedProperty("password").defaultValue("non&e").maxLength(20), new String[] {"hé", "you"}, "another");
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "password", "another");
		assertEquals(raw_content, template.getContent());
	}

	public void testRemoveFieldSecretTemplateName()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields_templatename");

		String raw_content = template.getContent();

		builder.replaceField(template, "templatenamepassword", new ConstrainedProperty("password").defaultValue("non&e").maxLength(20), new String[] {"hé", "you"}, null);
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "templatenamepassword");
		assertEquals(raw_content, template.getContent());
	}

	public void testGenerateFieldTextareaWithDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, "comment", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:TEXTAREA:comment", set_values.iterator().next());
		assertEquals("<field><name>comment</name><type>textarea</type><cols>10</cols> <rows>5</rows> <id>comment[!V 'FORM:VALUE'/]</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[4]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("comment"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:TEXTAREA:comment", set_values.iterator().next());
		assertEquals("<field><name>comment</name><type>textarea</type><cols>10</cols> <rows>5</rows> <id>comment[!V 'FORM:VALUE'/]</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[4]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("comment"), new String[] {null, "één"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:TEXTAREA:comment", set_values.iterator().next());
		assertEquals("<field><name>comment</name><type>textarea</type><cols>10</cols> <rows>5</rows> <id>comment[!V 'FORM:VALUE'/]</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[4]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("comment"), new String[] {"één"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:TEXTAREA:comment", set_values.iterator().next());
		assertEquals("<field><name>comment</name><type>textarea</type><cols>10</cols> <rows>5</rows> <id>commentéén</id><value>één</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[4]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("comment").defaultValue("non&e").maxLength(20), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:TEXTAREA:comment", set_values.iterator().next());
		assertEquals("<field><name>comment</name><type>textarea</type><cols>10</cols> <rows>5</rows> <id>commentnon&amp;e</id><value>non&amp;e</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[4]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("comment").defaultValue("non&e").maxLength(20), new String[] {"hé", "you"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:TEXTAREA:comment", set_values.iterator().next());
		assertEquals("<field><name>comment</name><type>textarea</type><cols>10</cols> <rows>5</rows> <id>commenthé</id><value>hé</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[4]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:TEXTAREA:comment", "already set");
		set_values = builder.generateField(template, new ConstrainedProperty("comment"), null, null);
		assertEquals(0, set_values.size());
		assertEquals("already set", StringUtils.splitToArray(template.getContent(), "\n")[4]);
		template.removeValue("FORM:TEXTAREA:comment");
	}

	public void testGenerateFieldTextareaWithoutDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, "anothercomment", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:TEXTAREA:anothercomment", set_values.iterator().next());
		assertEquals("<field><name>anothercomment</name><type>textarea</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[5]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("anothercomment"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:TEXTAREA:anothercomment", set_values.iterator().next());
		assertEquals("<field><name>anothercomment</name><type>textarea</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[5]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("anothercomment"), new String[] {"één"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:TEXTAREA:anothercomment", set_values.iterator().next());
		assertEquals("<field><name>anothercomment</name><type>textarea</type><value>één</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[5]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("anothercomment").defaultValue("non&e").maxLength(20), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:TEXTAREA:anothercomment", set_values.iterator().next());
		assertEquals("<field><name>anothercomment</name><type>textarea</type><value>non&amp;e</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[5]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();
	}

	public void testGenerateFieldTextareaPrefix()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, "comment", null, "another");
		assertEquals(1, set_values.size());
		assertEquals("FORM:TEXTAREA:anothercomment", set_values.iterator().next());
		assertEquals("<field><name>anothercomment</name><type>textarea</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[5]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("comment"), null, "another");
		assertEquals(1, set_values.size());
		assertEquals("FORM:TEXTAREA:anothercomment", set_values.iterator().next());
		assertEquals("<field><name>anothercomment</name><type>textarea</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[5]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:TEXTAREA:anothercomment", "already set");
		set_values = builder.generateField(template, new ConstrainedProperty("comment"), null, "another");
		assertEquals(0, set_values.size());
		assertEquals("already set", StringUtils.splitToArray(template.getContent(), "\n")[5]);
		template.removeValue("FORM:TEXTAREA:anothercomment");
	}

	public void testGenerateFieldTextareaTemplateNameWithDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields_templatename");

		set_values = builder.replaceField(template, "templatenamecomment", "comment", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:TEXTAREA:templatenamecomment", set_values.iterator().next());
		assertEquals("<field><name>comment</name><type>textarea</type><cols>10</cols> <rows>5</rows> <id>comment[!V 'FORM:VALUE'/]</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[4]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamecomment", new ConstrainedProperty("comment"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:TEXTAREA:templatenamecomment", set_values.iterator().next());
		assertEquals("<field><name>comment</name><type>textarea</type><cols>10</cols> <rows>5</rows> <id>comment[!V 'FORM:VALUE'/]</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[4]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamecomment", new ConstrainedProperty("comment"), new String[] {null, "één"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:TEXTAREA:templatenamecomment", set_values.iterator().next());
		assertEquals("<field><name>comment</name><type>textarea</type><cols>10</cols> <rows>5</rows> <id>comment[!V 'FORM:VALUE'/]</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[4]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamecomment", new ConstrainedProperty("comment"), new String[] {"één"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:TEXTAREA:templatenamecomment", set_values.iterator().next());
		assertEquals("<field><name>comment</name><type>textarea</type><cols>10</cols> <rows>5</rows> <id>commentéén</id><value>één</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[4]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamecomment", new ConstrainedProperty("comment").defaultValue("non&e").maxLength(20), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:TEXTAREA:templatenamecomment", set_values.iterator().next());
		assertEquals("<field><name>comment</name><type>textarea</type><cols>10</cols> <rows>5</rows> <id>commentnon&amp;e</id><value>non&amp;e</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[4]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamecomment", new ConstrainedProperty("comment").defaultValue("non&e").maxLength(20), new String[] {"hé", "you"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:TEXTAREA:templatenamecomment", set_values.iterator().next());
		assertEquals("<field><name>comment</name><type>textarea</type><cols>10</cols> <rows>5</rows> <id>commenthé</id><value>hé</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[4]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:TEXTAREA:templatenamecomment", "already set");
		set_values = builder.replaceField(template, "templatenamecomment", new ConstrainedProperty("comment"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:TEXTAREA:templatenamecomment", set_values.iterator().next());
		assertEquals("<field><name>comment</name><type>textarea</type><cols>10</cols> <rows>5</rows> <id>comment[!V 'FORM:VALUE'/]</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[4]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();
	}

	public void testRemoveFieldTextarea()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		String raw_content = template.getContent();

		builder.generateField(template, new ConstrainedProperty("comment").defaultValue("non&e").maxLength(20), new String[] {"hé", "you"}, null);
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "comment", null);
		assertEquals(raw_content, template.getContent());
	}

	public void testRemoveFieldTextareaPrefix()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		String raw_content = template.getContent();

		builder.generateField(template, new ConstrainedProperty("comment").defaultValue("non&e").maxLength(20), new String[] {"hé", "you"}, "another");
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "comment", "another");
		assertEquals(raw_content, template.getContent());
	}

	public void testRemoveFieldTextareaTemplateName()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields_templatename");

		String raw_content = template.getContent();

		builder.replaceField(template, "templatenamecomment", new ConstrainedProperty("comment").defaultValue("non&e").maxLength(20), new String[] {"hé", "you"}, null);
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "templatenamecomment", null);
		assertEquals(raw_content, template.getContent());
	}

	public void testGenerateFieldRadioWithDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;
		Iterator<String>	set_values_it;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, "question", null, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:question", set_values_it.next());
		assertEquals("FORM:DISPLAY:question", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>question[!V 'FORM:VALUE'/]</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value></value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("question"), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:question", set_values_it.next());
		assertEquals("FORM:DISPLAY:question", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>question[!V 'FORM:VALUE'/]</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value></value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("question").inList(null, "a1", null, "a3", "a2"), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:question", set_values_it.next());
		assertEquals("FORM:DISPLAY:question", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona1</id><value>a1</value></field>answer1<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona3</id><value>a3</value></field>a3<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona2</id><value>a2</value></field>answer2", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value></value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, SelectInListEnum.class, new ConstrainedProperty("question").inList(null, "a1", null, "a3", "a2"), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:question", set_values_it.next());
		assertEquals("FORM:DISPLAY:question", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona1</id><value>a1</value></field>answer1<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona3</id><value>a3</value></field>a3<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona2</id><value>a2</value></field>answer2", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value></value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, "question", null, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:question", set_values_it.next());
		assertEquals("FORM:DISPLAY:question", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona1</id><value>a1</value></field>answer1<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona3</id><value>a3</value></field>a3<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona2</id><value>a2</value></field>answer2", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value></value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("question").inList(null, "a1", null, "a3", "a2").defaultValue("a2"), new String[] {null, "a1"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:question", set_values_it.next());
		assertEquals("FORM:DISPLAY:question", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona1</id><value>a1</value></field>answer1<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona3</id><value>a3</value></field>a3<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona2</id><value>a2</value><checked>1</checked></field>answer2", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value>answer2</value></field><field><value>answer1</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, new ConstrainedProperty("question").defaultValue("a2"), new String[] {null, "a1"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:question", set_values_it.next());
		assertEquals("FORM:DISPLAY:question", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona1</id><value>a1</value></field>answer1<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona3</id><value>a3</value></field>a3<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona2</id><value>a2</value><checked>1</checked></field>answer2", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value>answer2</value></field><field><value>answer1</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("question").inList(null, "a1", null, "a3", "a2"), new String[] {"a1", "a2"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:question", set_values_it.next());
		assertEquals("FORM:DISPLAY:question", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona1</id><value>a1</value><checked>1</checked></field>answer1<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona3</id><value>a3</value></field>a3<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona2</id><value>a2</value></field>answer2", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value>answer1</value></field><field><value>answer2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, "question", new String[] {"a1", "a2"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:question", set_values_it.next());
		assertEquals("FORM:DISPLAY:question", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona1</id><value>a1</value><checked>1</checked></field>answer1<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona3</id><value>a3</value></field>a3<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona2</id><value>a2</value></field>answer2", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value>answer1</value></field><field><value>answer2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("question").inList(null, "a1", null, "a3", "a2").defaultValue("a3"), new String[] {"a1", "a2"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:question", set_values_it.next());
		assertEquals("FORM:DISPLAY:question", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona1</id><value>a1</value><checked>1</checked></field>answer1<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona3</id><value>a3</value></field>a3<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona2</id><value>a2</value></field>answer2", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value>answer1</value></field><field><value>answer2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, new ConstrainedProperty("question").defaultValue("a3"), new String[] {"a1", "a2"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:question", set_values_it.next());
		assertEquals("FORM:DISPLAY:question", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona1</id><value>a1</value><checked>1</checked></field>answer1<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona3</id><value>a3</value></field>a3<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona2</id><value>a2</value></field>answer2", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value>answer1</value></field><field><value>answer2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("question").inList(null, "a1", null, "a3", "a2"), new String[] {"a4"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:question", set_values_it.next());
		assertEquals("FORM:DISPLAY:question", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona1</id><value>a1</value></field>answer1<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona3</id><value>a3</value></field>a3<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona2</id><value>a2</value></field>answer2", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value>a4</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, "question", new String[] {"a4"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:question", set_values_it.next());
		assertEquals("FORM:DISPLAY:question", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona1</id><value>a1</value></field>answer1<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona3</id><value>a3</value></field>a3<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona2</id><value>a2</value></field>answer2", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value>a4</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		template.setValue("FORM:RADIO:question", "already set");
		template.setValue("FORM:DISPLAY:question", "already set too");
		set_values = builder.generateField(template, new ConstrainedProperty("question"), null, null);
		assertEquals(0, set_values.size());
		assertEquals("already set", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("already set too", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		template.removeValue("FORM:RADIO:question");
		template.removeValue("FORM:DISPLAY:question");
	}

	public void testGenerateFieldRadioWithoutDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, "anotherquestion", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anotherquestion", set_values.iterator().next());
		assertEquals("<field><name>anotherquestion</name><type>radio</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[10]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("anotherquestion"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anotherquestion", set_values.iterator().next());
		assertEquals("<field><name>anotherquestion</name><type>radio</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[10]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("anotherquestion").inList(null, "a1", null, "a3", "a2"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anotherquestion", set_values.iterator().next());
		assertEquals("<field><name>anotherquestion</name><type>radio</type><value>a1</value></field>another answer 1<field><name>anotherquestion</name><type>radio</type><value>a3</value></field>a3<field><name>anotherquestion</name><type>radio</type><value>a2</value></field>another answer 2", StringUtils.splitToArray(template.getContent(), "\n")[10]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, "anotherquestion", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anotherquestion", set_values.iterator().next());
		assertEquals("<field><name>anotherquestion</name><type>radio</type><value>a1</value></field>another answer 1<field><name>anotherquestion</name><type>radio</type><value>a3</value></field>a3<field><name>anotherquestion</name><type>radio</type><value>a2</value></field>another answer 2", StringUtils.splitToArray(template.getContent(), "\n")[10]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("anotherquestion").inList(null, "a1", null, "a3", "a2").defaultValue("a3"), new String[] {"a1", "a2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anotherquestion", set_values.iterator().next());
		assertEquals("<field><name>anotherquestion</name><type>radio</type><value>a1</value><checked>1</checked></field>another answer 1<field><name>anotherquestion</name><type>radio</type><value>a3</value></field>a3<field><name>anotherquestion</name><type>radio</type><value>a2</value></field>another answer 2", StringUtils.splitToArray(template.getContent(), "\n")[10]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, new ConstrainedProperty("anotherquestion").defaultValue("a3"), new String[] {"a1", "a2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anotherquestion", set_values.iterator().next());
		assertEquals("<field><name>anotherquestion</name><type>radio</type><value>a1</value><checked>1</checked></field>another answer 1<field><name>anotherquestion</name><type>radio</type><value>a3</value></field>a3<field><name>anotherquestion</name><type>radio</type><value>a2</value></field>another answer 2", StringUtils.splitToArray(template.getContent(), "\n")[10]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();
	}

	public void testGenerateFieldRadioPrefix()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, "question", null, "another");
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anotherquestion", set_values.iterator().next());
		assertEquals("<field><name>anotherquestion</name><type>radio</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[10]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("question"), null, "another");
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anotherquestion", set_values.iterator().next());
		assertEquals("<field><name>anotherquestion</name><type>radio</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[10]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("question").inList(null, "a1", null, "a3", "a2"), null, "another");
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anotherquestion", set_values.iterator().next());
		assertEquals("<field><name>anotherquestion</name><type>radio</type><value>a1</value></field>another answer 1<field><name>anotherquestion</name><type>radio</type><value>a3</value></field>a3<field><name>anotherquestion</name><type>radio</type><value>a2</value></field>another answer 2", StringUtils.splitToArray(template.getContent(), "\n")[10]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, "question", null, "another");
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anotherquestion", set_values.iterator().next());
		assertEquals("<field><name>anotherquestion</name><type>radio</type><value>a1</value></field>another answer 1<field><name>anotherquestion</name><type>radio</type><value>a3</value></field>a3<field><name>anotherquestion</name><type>radio</type><value>a2</value></field>another answer 2", StringUtils.splitToArray(template.getContent(), "\n")[10]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("question").inList(null, "a1", null, "a3", "a2").defaultValue("a3"), new String[] {"a1", "a2"}, "another");
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anotherquestion", set_values.iterator().next());
		assertEquals("<field><name>anotherquestion</name><type>radio</type><value>a1</value><checked>1</checked></field>another answer 1<field><name>anotherquestion</name><type>radio</type><value>a3</value></field>a3<field><name>anotherquestion</name><type>radio</type><value>a2</value></field>another answer 2", StringUtils.splitToArray(template.getContent(), "\n")[10]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, new ConstrainedProperty("question").defaultValue("a3"), new String[] {"a1", "a2"}, "another");
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anotherquestion", set_values.iterator().next());
		assertEquals("<field><name>anotherquestion</name><type>radio</type><value>a1</value><checked>1</checked></field>another answer 1<field><name>anotherquestion</name><type>radio</type><value>a3</value></field>a3<field><name>anotherquestion</name><type>radio</type><value>a2</value></field>another answer 2", StringUtils.splitToArray(template.getContent(), "\n")[10]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:RADIO:anotherquestion", "already set");
		set_values = builder.generateField(template, new ConstrainedProperty("question"), null, "another");
		assertEquals(0, set_values.size());
		assertEquals("already set", StringUtils.splitToArray(template.getContent(), "\n")[10]);
		template.removeValue("FORM:RADIO:anotherquestion");
	}

	public void testGenerateFieldRadioDynamic()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;
		Iterator<String>	set_values_it;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		template.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"question:a1", "dynamic first"},
					{"question:a2", "dynamic second"},
					{"question:a3", "dynamic third"}
				};
			}});

		set_values = builder.generateField(template, new ConstrainedProperty("question").inList("a1", "a3", "a2").defaultValue("a2"), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:question", set_values_it.next());
		assertEquals("FORM:DISPLAY:question", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona1</id><value>a1</value></field>dynamic first<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona3</id><value>a3</value></field>dynamic third<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona2</id><value>a2</value><checked>1</checked></field>dynamic second", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value>dynamic second</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, new ConstrainedProperty("question").defaultValue("a2"), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:question", set_values_it.next());
		assertEquals("FORM:DISPLAY:question", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona1</id><value>a1</value></field>dynamic first<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona3</id><value>a3</value></field>dynamic third<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona2</id><value>a2</value><checked>1</checked></field>dynamic second", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value>dynamic second</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("question").inList("a1", "a3", "a2").defaultValue("a2"), new String[] {"a1", "a2"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:question", set_values_it.next());
		assertEquals("FORM:DISPLAY:question", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona1</id><value>a1</value><checked>1</checked></field>dynamic first<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona3</id><value>a3</value></field>dynamic third<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona2</id><value>a2</value></field>dynamic second", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value>dynamic first</value></field><field><value>dynamic second</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, new ConstrainedProperty("question").defaultValue("a2"), new String[] {"a1", "a2"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:question", set_values_it.next());
		assertEquals("FORM:DISPLAY:question", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona1</id><value>a1</value><checked>1</checked></field>dynamic first<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona3</id><value>a3</value></field>dynamic third<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona2</id><value>a2</value></field>dynamic second", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value>dynamic first</value></field><field><value>dynamic second</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		template.clear();

		template.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"question:a1", "dynamic first"},
				};
			}});

		set_values = builder.generateField(template, new ConstrainedProperty("question").inList("a1", "a3", "a2").defaultValue("a2"), new String[] {"a1", "a2"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:question", set_values_it.next());
		assertEquals("FORM:DISPLAY:question", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona1</id><value>a1</value><checked>1</checked></field>dynamic first<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona3</id><value>a3</value></field>a3<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona2</id><value>a2</value></field>answer2", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value>dynamic first</value></field><field><value>answer2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, new ConstrainedProperty("question").defaultValue("a2"), new String[] {"a1", "a2"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:question", set_values_it.next());
		assertEquals("FORM:DISPLAY:question", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona1</id><value>a1</value><checked>1</checked></field>dynamic first<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona3</id><value>a3</value></field>a3<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona2</id><value>a2</value></field>answer2", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value>dynamic first</value></field><field><value>answer2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("question").inList("a1", "a3", "a2").defaultValue("a2"), new String[] {"a2"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:question", set_values_it.next());
		assertEquals("FORM:DISPLAY:question", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona1</id><value>a1</value></field>dynamic first<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona3</id><value>a3</value></field>a3<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona2</id><value>a2</value><checked>1</checked></field>answer2", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value>answer2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, new ConstrainedProperty("question").defaultValue("a2"), new String[] {"a2"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:question", set_values_it.next());
		assertEquals("FORM:DISPLAY:question", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona1</id><value>a1</value></field>dynamic first<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona3</id><value>a3</value></field>a3<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona2</id><value>a2</value><checked>1</checked></field>answer2", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value>answer2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();
	}

	public void testGenerateFieldRadioTemplateNameWithDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;
		Iterator<String>	set_values_it;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields_templatename");

		set_values = builder.replaceField(template, "templatenamequestion", "question", null, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:templatenamequestion", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamequestion", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>question[!V 'FORM:VALUE'/]</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value></value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamequestion", new ConstrainedProperty("question"), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:templatenamequestion", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamequestion", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>question[!V 'FORM:VALUE'/]</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value></value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamequestion", new ConstrainedProperty("question").inList(null, "a1", null, "a3", "a2"), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:templatenamequestion", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamequestion", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona1</id><value>a1</value></field>answer1<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona3</id><value>a3</value></field>a3<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona2</id><value>a2</value></field>answer2", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value></value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamequestion", RadioInListEnum.class, "question", null, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:templatenamequestion", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamequestion", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona1</id><value>a1</value></field>answer1<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona3</id><value>a3</value></field>a3<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona2</id><value>a2</value></field>answer2", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value></value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamequestion", new ConstrainedProperty("question").inList(null, "a1", null, "a3", "a2").defaultValue("a2"), new String[] {null, "a1"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:templatenamequestion", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamequestion", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona1</id><value>a1</value></field>answer1<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona3</id><value>a3</value></field>a3<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona2</id><value>a2</value><checked>1</checked></field>answer2", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value>answer2</value></field><field><value>answer1</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamequestion", RadioInListEnum.class, new ConstrainedProperty("question").defaultValue("a2"), new String[] {null, "a1"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:templatenamequestion", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamequestion", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona1</id><value>a1</value></field>answer1<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona3</id><value>a3</value></field>a3<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona2</id><value>a2</value><checked>1</checked></field>answer2", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value>answer2</value></field><field><value>answer1</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamequestion", new ConstrainedProperty("question").inList(null, "a1", null, "a3", "a2"), new String[] {"a1", "a2"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:templatenamequestion", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamequestion", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona1</id><value>a1</value><checked>1</checked></field>answer1<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona3</id><value>a3</value></field>a3<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona2</id><value>a2</value></field>answer2", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value>answer1</value></field><field><value>answer2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamequestion", RadioInListEnum.class, "question", new String[] {"a1", "a2"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:templatenamequestion", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamequestion", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona1</id><value>a1</value><checked>1</checked></field>answer1<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona3</id><value>a3</value></field>a3<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona2</id><value>a2</value></field>answer2", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value>answer1</value></field><field><value>answer2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamequestion", new ConstrainedProperty("question").inList(null, "a1", null, "a3", "a2").defaultValue("a3"), new String[] {"a1", "a2"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:templatenamequestion", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamequestion", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona1</id><value>a1</value><checked>1</checked></field>answer1<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona3</id><value>a3</value></field>a3<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona2</id><value>a2</value></field>answer2", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value>answer1</value></field><field><value>answer2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamequestion", RadioInListEnum.class, new ConstrainedProperty("question").defaultValue("a3"), new String[] {"a1", "a2"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:templatenamequestion", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamequestion", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona1</id><value>a1</value><checked>1</checked></field>answer1<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona3</id><value>a3</value></field>a3<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona2</id><value>a2</value></field>answer2", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value>answer1</value></field><field><value>answer2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamequestion", new ConstrainedProperty("question").inList(null, "a1", null, "a3", "a2"), new String[] {"a4"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:templatenamequestion", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamequestion", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona1</id><value>a1</value></field>answer1<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona3</id><value>a3</value></field>a3<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona2</id><value>a2</value></field>answer2", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value>a4</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamequestion", RadioInListEnum.class, "question", new String[] {"a4"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:templatenamequestion", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamequestion", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona1</id><value>a1</value></field>answer1<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona3</id><value>a3</value></field>a3<field><name>question</name><type>radio</type><alt>sometext</alt> <id>questiona2</id><value>a2</value></field>answer2", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value>a4</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		template.setValue("FORM:RADIO:templatenamequestion", "already set");
		template.setValue("FORM:DISPLAY:templatenamequestion", "already set too");
		set_values = builder.replaceField(template, "templatenamequestion", "question", null, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:RADIO:templatenamequestion", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamequestion", set_values_it.next());
		assertEquals("<field><name>question</name><type>radio</type><alt>sometext</alt> <id>question[!V 'FORM:VALUE'/]</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		assertEquals("<field><value></value></field>", StringUtils.splitToArray(template.getContent(), "\n")[56]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();
	}

	public void testGenerateFieldRadioCustomWithDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, "customquestion", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:customquestion", set_values.iterator().next());
		assertEquals("<field><name>customquestion</name><type>radio</type><alt>customtext</alt></field>", StringUtils.splitToArray(template.getContent(), "\n")[14]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("customquestion"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:customquestion", set_values.iterator().next());
		assertEquals("<field><name>customquestion</name><type>radio</type><alt>customtext</alt></field>", StringUtils.splitToArray(template.getContent(), "\n")[14]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("customquestion").inList(null, "a1", null, "a3", "a2"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:customquestion", set_values.iterator().next());
		assertEquals("custom answer 1 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a1</value></field>a3 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a3</value></field>custom answer 2 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[14]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("customquestion").inList(null, "a1", null, "a3", "a2"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:customquestion", set_values.iterator().next());
		assertEquals("custom answer 1 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a1</value></field>a3 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a3</value></field>custom answer 2 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[14]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, new ConstrainedProperty("customquestion").defaultValue("a2"), new String[] {null, "a1"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:customquestion", set_values.iterator().next());
		assertEquals("custom answer 1 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a1</value></field>a3 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a3</value></field>custom answer 2 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a2</value><checked>1</checked></field>", StringUtils.splitToArray(template.getContent(), "\n")[14]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("customquestion").inList(null, "a1", null, "a3", "a2"), new String[] {"a1", "a2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:customquestion", set_values.iterator().next());
		assertEquals("custom answer 1 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a1</value><checked>1</checked></field>a3 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a3</value></field>custom answer 2 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[14]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, "customquestion", new String[] {"a1", "a2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:customquestion", set_values.iterator().next());
		assertEquals("custom answer 1 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a1</value><checked>1</checked></field>a3 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a3</value></field>custom answer 2 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[14]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("customquestion").inList(null, "a1", null, "a3", "a2").defaultValue("a3"), new String[] {"a1", "a2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:customquestion", set_values.iterator().next());
		assertEquals("custom answer 1 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a1</value><checked>1</checked></field>a3 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a3</value></field>custom answer 2 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[14]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, new ConstrainedProperty("customquestion").defaultValue("a3"), new String[] {"a1", "a2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:customquestion", set_values.iterator().next());
		assertEquals("custom answer 1 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a1</value><checked>1</checked></field>a3 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a3</value></field>custom answer 2 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[14]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("customquestion").inList(null, "a1", null, "a3", "a2"), new String[] {"a4"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:customquestion", set_values.iterator().next());
		assertEquals("custom answer 1 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a1</value></field>a3 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a3</value></field>custom answer 2 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[14]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, "customquestion", new String[] {"a4"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:customquestion", set_values.iterator().next());
		assertEquals("custom answer 1 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a1</value></field>a3 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a3</value></field>custom answer 2 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[14]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:RADIO:customquestion", "already set");
		set_values = builder.generateField(template, new ConstrainedProperty("customquestion"), null, null);
		assertEquals(0, set_values.size());
		assertEquals("already set", StringUtils.splitToArray(template.getContent(), "\n")[14]);
		template.removeValue("FORM:RADIO:customquestion");
	}

	public void testGenerateFieldRadioCustomWithoutDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, "anothercustomquestion", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anothercustomquestion", set_values.iterator().next());
		assertEquals("<field><name>anothercustomquestion</name><type>radio</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[19]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("anothercustomquestion"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anothercustomquestion", set_values.iterator().next());
		assertEquals("<field><name>anothercustomquestion</name><type>radio</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[19]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("anothercustomquestion").inList(null, "a1", null, "a3", "a2"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anothercustomquestion", set_values.iterator().next());
		assertEquals("anothercustomquestion-a1:anothercustom answer 1 : <field><name>anothercustomquestion</name><type>radio</type><value>a1</value></field>anothercustomquestion-a3:a3 : <field><name>anothercustomquestion</name><type>radio</type><value>a3</value></field>anothercustomquestion-a2:anothercustom answer 2 : <field><name>anothercustomquestion</name><type>radio</type><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[19]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, "anothercustomquestion", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anothercustomquestion", set_values.iterator().next());
		assertEquals("anothercustomquestion-a1:anothercustom answer 1 : <field><name>anothercustomquestion</name><type>radio</type><value>a1</value></field>anothercustomquestion-a3:a3 : <field><name>anothercustomquestion</name><type>radio</type><value>a3</value></field>anothercustomquestion-a2:anothercustom answer 2 : <field><name>anothercustomquestion</name><type>radio</type><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[19]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("anothercustomquestion").inList(null, "a1", null, "a3", "a2").defaultValue("a2"), new String[] {null, "a1"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anothercustomquestion", set_values.iterator().next());
		assertEquals("anothercustomquestion-a1:anothercustom answer 1 : <field><name>anothercustomquestion</name><type>radio</type><value>a1</value></field>anothercustomquestion-a3:a3 : <field><name>anothercustomquestion</name><type>radio</type><value>a3</value></field>anothercustomquestion-a2:anothercustom answer 2 : <field><name>anothercustomquestion</name><type>radio</type><value>a2</value><checked>1</checked></field>", StringUtils.splitToArray(template.getContent(), "\n")[19]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, new ConstrainedProperty("anothercustomquestion").defaultValue("a2"), new String[] {null, "a1"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anothercustomquestion", set_values.iterator().next());
		assertEquals("anothercustomquestion-a1:anothercustom answer 1 : <field><name>anothercustomquestion</name><type>radio</type><value>a1</value></field>anothercustomquestion-a3:a3 : <field><name>anothercustomquestion</name><type>radio</type><value>a3</value></field>anothercustomquestion-a2:anothercustom answer 2 : <field><name>anothercustomquestion</name><type>radio</type><value>a2</value><checked>1</checked></field>", StringUtils.splitToArray(template.getContent(), "\n")[19]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("anothercustomquestion").inList(null, "a1", null, "a3", "a2"), new String[] {"a1", "a2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anothercustomquestion", set_values.iterator().next());
		assertEquals("anothercustomquestion-a1:anothercustom answer 1 : <field><name>anothercustomquestion</name><type>radio</type><value>a1</value><checked>1</checked></field>anothercustomquestion-a3:a3 : <field><name>anothercustomquestion</name><type>radio</type><value>a3</value></field>anothercustomquestion-a2:anothercustom answer 2 : <field><name>anothercustomquestion</name><type>radio</type><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[19]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, "anothercustomquestion", new String[] {"a1", "a2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anothercustomquestion", set_values.iterator().next());
		assertEquals("anothercustomquestion-a1:anothercustom answer 1 : <field><name>anothercustomquestion</name><type>radio</type><value>a1</value><checked>1</checked></field>anothercustomquestion-a3:a3 : <field><name>anothercustomquestion</name><type>radio</type><value>a3</value></field>anothercustomquestion-a2:anothercustom answer 2 : <field><name>anothercustomquestion</name><type>radio</type><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[19]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("anothercustomquestion").inList(null, "a1", null, "a3", "a2").defaultValue("a3"), new String[] {"a1", "a2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anothercustomquestion", set_values.iterator().next());
		assertEquals("anothercustomquestion-a1:anothercustom answer 1 : <field><name>anothercustomquestion</name><type>radio</type><value>a1</value><checked>1</checked></field>anothercustomquestion-a3:a3 : <field><name>anothercustomquestion</name><type>radio</type><value>a3</value></field>anothercustomquestion-a2:anothercustom answer 2 : <field><name>anothercustomquestion</name><type>radio</type><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[19]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, new ConstrainedProperty("anothercustomquestion").defaultValue("a3"), new String[] {"a1", "a2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anothercustomquestion", set_values.iterator().next());
		assertEquals("anothercustomquestion-a1:anothercustom answer 1 : <field><name>anothercustomquestion</name><type>radio</type><value>a1</value><checked>1</checked></field>anothercustomquestion-a3:a3 : <field><name>anothercustomquestion</name><type>radio</type><value>a3</value></field>anothercustomquestion-a2:anothercustom answer 2 : <field><name>anothercustomquestion</name><type>radio</type><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[19]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("anothercustomquestion").inList(null, "a1", null, "a3", "a2"), new String[] {"a4"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anothercustomquestion", set_values.iterator().next());
		assertEquals("anothercustomquestion-a1:anothercustom answer 1 : <field><name>anothercustomquestion</name><type>radio</type><value>a1</value></field>anothercustomquestion-a3:a3 : <field><name>anothercustomquestion</name><type>radio</type><value>a3</value></field>anothercustomquestion-a2:anothercustom answer 2 : <field><name>anothercustomquestion</name><type>radio</type><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[19]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, "anothercustomquestion", new String[] {"a4"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anothercustomquestion", set_values.iterator().next());
		assertEquals("anothercustomquestion-a1:anothercustom answer 1 : <field><name>anothercustomquestion</name><type>radio</type><value>a1</value></field>anothercustomquestion-a3:a3 : <field><name>anothercustomquestion</name><type>radio</type><value>a3</value></field>anothercustomquestion-a2:anothercustom answer 2 : <field><name>anothercustomquestion</name><type>radio</type><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[19]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:RADIO:anothercustomquestion", "already set");
		set_values = builder.generateField(template, new ConstrainedProperty("anothercustomquestion"), null, null);
		assertEquals(0, set_values.size());
		assertEquals("already set", StringUtils.splitToArray(template.getContent(), "\n")[19]);
		template.removeValue("FORM:RADIO:anothercustomquestion");
	}

	public void testGenerateFieldRadioCustomPrefix()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, new ConstrainedProperty("customquestion").inList(null, "a1", null, "a3", "a2"), null, "another");
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anothercustomquestion", set_values.iterator().next());
		assertEquals("anothercustomquestion-a1:anothercustom answer 1 : <field><name>anothercustomquestion</name><type>radio</type><value>a1</value></field>anothercustomquestion-a3:a3 : <field><name>anothercustomquestion</name><type>radio</type><value>a3</value></field>anothercustomquestion-a2:anothercustom answer 2 : <field><name>anothercustomquestion</name><type>radio</type><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[19]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, "customquestion", null, "another");
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anothercustomquestion", set_values.iterator().next());
		assertEquals("anothercustomquestion-a1:anothercustom answer 1 : <field><name>anothercustomquestion</name><type>radio</type><value>a1</value></field>anothercustomquestion-a3:a3 : <field><name>anothercustomquestion</name><type>radio</type><value>a3</value></field>anothercustomquestion-a2:anothercustom answer 2 : <field><name>anothercustomquestion</name><type>radio</type><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[19]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:RADIO:anothercustomquestion", "already set");
		set_values = builder.generateField(template, new ConstrainedProperty("customquestion"), null, "another");
		assertEquals(0, set_values.size());
		assertEquals("already set", StringUtils.splitToArray(template.getContent(), "\n")[19]);
		template.removeValue("FORM:RADIO:anothercustomquestion");
	}

	public void testGenerateFieldRadioCustomDynamic()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		template.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"customquestion:a1", "dynamic first"},
					{"customquestion:a2", "dynamic second"},
					{"customquestion:a3", "dynamic third"}
				};
			}});

		set_values = builder.generateField(template, new ConstrainedProperty("customquestion").inList("a1", "a3", "a2").defaultValue("a2"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:customquestion", set_values.iterator().next());
		assertEquals("dynamic first : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a1</value></field>dynamic third : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a3</value></field>dynamic second : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a2</value><checked>1</checked></field>", StringUtils.splitToArray(template.getContent(), "\n")[14]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, new ConstrainedProperty("customquestion").defaultValue("a2"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:customquestion", set_values.iterator().next());
		assertEquals("dynamic first : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a1</value></field>dynamic third : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a3</value></field>dynamic second : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a2</value><checked>1</checked></field>", StringUtils.splitToArray(template.getContent(), "\n")[14]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("customquestion").inList("a1", "a3", "a2").defaultValue("a2"), new String[] {"a1", "a3"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:customquestion", set_values.iterator().next());
		assertEquals("dynamic first : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a1</value><checked>1</checked></field>dynamic third : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a3</value></field>dynamic second : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[14]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, new ConstrainedProperty("customquestion").defaultValue("a2"), new String[] {"a1", "a3"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:customquestion", set_values.iterator().next());
		assertEquals("dynamic first : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a1</value><checked>1</checked></field>dynamic third : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a3</value></field>dynamic second : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[14]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.clear();

		template.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"customquestion:a2", "dynamic second"},
				};
			}});

		set_values = builder.generateField(template, new ConstrainedProperty("customquestion").inList("a1", "a3", "a2").defaultValue("a2"), new String[] {"a1", "a3"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:customquestion", set_values.iterator().next());
		assertEquals("custom answer 1 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a1</value><checked>1</checked></field>a3 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a3</value></field>dynamic second : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[14]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, new ConstrainedProperty("customquestion").defaultValue("a2"), new String[] {"a1", "a3"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:customquestion", set_values.iterator().next());
		assertEquals("custom answer 1 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a1</value><checked>1</checked></field>a3 : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a3</value></field>dynamic second : <field><name>customquestion</name><type>radio</type><alt>customtext</alt><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[14]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();
	}

	public void testGenerateFieldRadioCustomTemplateNameWithoutDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields_templatename");

		set_values = builder.replaceField(template, "templatenameanothercustomquestion", "anothercustomquestion", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:templatenameanothercustomquestion", set_values.iterator().next());
		assertEquals("<field><name>anothercustomquestion</name><type>radio</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[19]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameanothercustomquestion", new ConstrainedProperty("anothercustomquestion"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:templatenameanothercustomquestion", set_values.iterator().next());
		assertEquals("<field><name>anothercustomquestion</name><type>radio</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[19]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameanothercustomquestion", new ConstrainedProperty("anothercustomquestion").inList(null, "a1", null, "a3", "a2"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:templatenameanothercustomquestion", set_values.iterator().next());
		assertEquals("anothercustomquestion-a1:anothercustom answer 1 : <field><name>anothercustomquestion</name><type>radio</type><value>a1</value></field>anothercustomquestion-a3:a3 : <field><name>anothercustomquestion</name><type>radio</type><value>a3</value></field>anothercustomquestion-a2:anothercustom answer 2 : <field><name>anothercustomquestion</name><type>radio</type><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[19]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameanothercustomquestion", RadioInListEnum.class, "anothercustomquestion", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:templatenameanothercustomquestion", set_values.iterator().next());
		assertEquals("anothercustomquestion-a1:anothercustom answer 1 : <field><name>anothercustomquestion</name><type>radio</type><value>a1</value></field>anothercustomquestion-a3:a3 : <field><name>anothercustomquestion</name><type>radio</type><value>a3</value></field>anothercustomquestion-a2:anothercustom answer 2 : <field><name>anothercustomquestion</name><type>radio</type><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[19]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameanothercustomquestion", new ConstrainedProperty("anothercustomquestion").inList(null, "a1", null, "a3", "a2").defaultValue("a2"), new String[] {null, "a1"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:templatenameanothercustomquestion", set_values.iterator().next());
		assertEquals("anothercustomquestion-a1:anothercustom answer 1 : <field><name>anothercustomquestion</name><type>radio</type><value>a1</value></field>anothercustomquestion-a3:a3 : <field><name>anothercustomquestion</name><type>radio</type><value>a3</value></field>anothercustomquestion-a2:anothercustom answer 2 : <field><name>anothercustomquestion</name><type>radio</type><value>a2</value><checked>1</checked></field>", StringUtils.splitToArray(template.getContent(), "\n")[19]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameanothercustomquestion", RadioInListEnum.class, new ConstrainedProperty("anothercustomquestion").defaultValue("a2"), new String[] {null, "a1"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:templatenameanothercustomquestion", set_values.iterator().next());
		assertEquals("anothercustomquestion-a1:anothercustom answer 1 : <field><name>anothercustomquestion</name><type>radio</type><value>a1</value></field>anothercustomquestion-a3:a3 : <field><name>anothercustomquestion</name><type>radio</type><value>a3</value></field>anothercustomquestion-a2:anothercustom answer 2 : <field><name>anothercustomquestion</name><type>radio</type><value>a2</value><checked>1</checked></field>", StringUtils.splitToArray(template.getContent(), "\n")[19]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameanothercustomquestion", new ConstrainedProperty("anothercustomquestion").inList(null, "a1", null, "a3", "a2"), new String[] {"a1", "a2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:templatenameanothercustomquestion", set_values.iterator().next());
		assertEquals("anothercustomquestion-a1:anothercustom answer 1 : <field><name>anothercustomquestion</name><type>radio</type><value>a1</value><checked>1</checked></field>anothercustomquestion-a3:a3 : <field><name>anothercustomquestion</name><type>radio</type><value>a3</value></field>anothercustomquestion-a2:anothercustom answer 2 : <field><name>anothercustomquestion</name><type>radio</type><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[19]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameanothercustomquestion", RadioInListEnum.class, "anothercustomquestion", new String[] {"a1", "a2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:templatenameanothercustomquestion", set_values.iterator().next());
		assertEquals("anothercustomquestion-a1:anothercustom answer 1 : <field><name>anothercustomquestion</name><type>radio</type><value>a1</value><checked>1</checked></field>anothercustomquestion-a3:a3 : <field><name>anothercustomquestion</name><type>radio</type><value>a3</value></field>anothercustomquestion-a2:anothercustom answer 2 : <field><name>anothercustomquestion</name><type>radio</type><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[19]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameanothercustomquestion", new ConstrainedProperty("anothercustomquestion").inList(null, "a1", null, "a3", "a2").defaultValue("a3"), new String[] {"a1", "a2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:templatenameanothercustomquestion", set_values.iterator().next());
		assertEquals("anothercustomquestion-a1:anothercustom answer 1 : <field><name>anothercustomquestion</name><type>radio</type><value>a1</value><checked>1</checked></field>anothercustomquestion-a3:a3 : <field><name>anothercustomquestion</name><type>radio</type><value>a3</value></field>anothercustomquestion-a2:anothercustom answer 2 : <field><name>anothercustomquestion</name><type>radio</type><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[19]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameanothercustomquestion", RadioInListEnum.class, new ConstrainedProperty("anothercustomquestion").defaultValue("a3"), new String[] {"a1", "a2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:templatenameanothercustomquestion", set_values.iterator().next());
		assertEquals("anothercustomquestion-a1:anothercustom answer 1 : <field><name>anothercustomquestion</name><type>radio</type><value>a1</value><checked>1</checked></field>anothercustomquestion-a3:a3 : <field><name>anothercustomquestion</name><type>radio</type><value>a3</value></field>anothercustomquestion-a2:anothercustom answer 2 : <field><name>anothercustomquestion</name><type>radio</type><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[19]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameanothercustomquestion", new ConstrainedProperty("anothercustomquestion").inList(null, "a1", null, "a3", "a2"), new String[] {"a4"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:templatenameanothercustomquestion", set_values.iterator().next());
		assertEquals("anothercustomquestion-a1:anothercustom answer 1 : <field><name>anothercustomquestion</name><type>radio</type><value>a1</value></field>anothercustomquestion-a3:a3 : <field><name>anothercustomquestion</name><type>radio</type><value>a3</value></field>anothercustomquestion-a2:anothercustom answer 2 : <field><name>anothercustomquestion</name><type>radio</type><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[19]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameanothercustomquestion", RadioInListEnum.class, "anothercustomquestion", new String[] {"a4"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:templatenameanothercustomquestion", set_values.iterator().next());
		assertEquals("anothercustomquestion-a1:anothercustom answer 1 : <field><name>anothercustomquestion</name><type>radio</type><value>a1</value></field>anothercustomquestion-a3:a3 : <field><name>anothercustomquestion</name><type>radio</type><value>a3</value></field>anothercustomquestion-a2:anothercustom answer 2 : <field><name>anothercustomquestion</name><type>radio</type><value>a2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[19]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:RADIO:templatenameanothercustomquestion", "already set");
		set_values = builder.replaceField(template, "templatenameanothercustomquestion", new ConstrainedProperty("anothercustomquestion"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:templatenameanothercustomquestion", set_values.iterator().next());
		assertEquals("<field><name>anothercustomquestion</name><type>radio</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[19]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();
	}

	public void testGenerateFieldRadioEmptyCustomWithDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields_emptycustom");

		set_values = builder.generateField(template, "emptycustomquestion", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:emptycustomquestion", set_values.iterator().next());
		assertEquals("<field><name>emptycustomquestion</name><type>radio</type><alt>emptycustomtext</alt></field>", StringUtils.splitToArray(template.getContent(), "\n")[0]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("emptycustomquestion"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:emptycustomquestion", set_values.iterator().next());
		assertEquals("<field><name>emptycustomquestion</name><type>radio</type><alt>emptycustomtext</alt></field>", StringUtils.splitToArray(template.getContent(), "\n")[0]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("emptycustomquestion").inList(null, "a1", null, "a3", "a2"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:emptycustomquestion", set_values.iterator().next());
		assertEquals(" -  -  - ", StringUtils.splitToArray(template.getContent(), "\n")[0]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, "emptycustomquestion", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:emptycustomquestion", set_values.iterator().next());
		assertEquals(" -  -  - ", StringUtils.splitToArray(template.getContent(), "\n")[0]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:RADIO:emptycustomquestion", "already set");
		set_values = builder.generateField(template, new ConstrainedProperty("emptycustomquestion"), null, null);
		assertEquals(0, set_values.size());
		assertEquals("already set", StringUtils.splitToArray(template.getContent(), "\n")[0]);
		template.removeValue("FORM:RADIO:emptycustomquestion");
	}

	public void testGenerateFieldRadioEmptyCustomWithoutDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields_emptycustom");

		set_values = builder.generateField(template, "anotheremptycustomquestion", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anotheremptycustomquestion", set_values.iterator().next());
		assertEquals("<field><name>anotheremptycustomquestion</name><type>radio</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[2]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("anotheremptycustomquestion"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anotheremptycustomquestion", set_values.iterator().next());
		assertEquals("<field><name>anotheremptycustomquestion</name><type>radio</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[2]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("anotheremptycustomquestion").inList(null, "a1", null, "a3", "a2"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anotheremptycustomquestion", set_values.iterator().next());
		assertEquals(" -  -  - ", StringUtils.splitToArray(template.getContent(), "\n")[2]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, "anotheremptycustomquestion", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anotheremptycustomquestion", set_values.iterator().next());
		assertEquals(" -  -  - ", StringUtils.splitToArray(template.getContent(), "\n")[2]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:RADIO:anotheremptycustomquestion", "already set");
		set_values = builder.generateField(template, new ConstrainedProperty("anotheremptycustomquestion"), null, null);
		assertEquals(0, set_values.size());
		assertEquals("already set", StringUtils.splitToArray(template.getContent(), "\n")[2]);
		template.removeValue("FORM:RADIO:anotheremptycustomquestion");
	}

	public void testGenerateFieldRadioEmptyCustomPrefix()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields_emptycustom");

		set_values = builder.generateField(template, "emptycustomquestion", null, "another");
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anotheremptycustomquestion", set_values.iterator().next());
		assertEquals("<field><name>anotheremptycustomquestion</name><type>radio</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[2]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("emptycustomquestion").inList(null, "a1", null, "a3", "a2"), null, "another");
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anotheremptycustomquestion", set_values.iterator().next());
		assertEquals(" -  -  - ", StringUtils.splitToArray(template.getContent(), "\n")[2]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, "emptycustomquestion", null, "another");
		assertEquals(1, set_values.size());
		assertEquals("FORM:RADIO:anotheremptycustomquestion", set_values.iterator().next());
		assertEquals(" -  -  - ", StringUtils.splitToArray(template.getContent(), "\n")[2]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:RADIO:anotheremptycustomquestion", "already set");
		set_values = builder.generateField(template, new ConstrainedProperty("emptycustomquestion"), null, "another");
		assertEquals(0, set_values.size());
		assertEquals("already set", StringUtils.splitToArray(template.getContent(), "\n")[2]);
		template.removeValue("FORM:RADIO:anotheremptycustomquestion");
	}

	public void testRemoveFieldRadio()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		String raw_content = template.getContent();

		builder.generateField(template, new ConstrainedProperty("question").inList(null, "a1", null, "a3", "a2"), new String[] {"a4"}, null);
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "question", null);
		assertEquals(raw_content, template.getContent());

		builder.generateField(template, RadioInListEnum.class, "question", new String[] {"a4"}, null);
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "question", null);
		assertEquals(raw_content, template.getContent());
	}

	public void testRemoveFieldRadioTemplateName()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields_templatename");

		String raw_content = template.getContent();

		builder.replaceField(template, "templatenamequestion", new ConstrainedProperty("question").inList(null, "a1", null, "a3", "a2"), new String[] {"a4"}, null);
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "templatenamequestion");
		assertEquals(raw_content, template.getContent());

		builder.replaceField(template, "templatenamequestion", RadioInListEnum.class, "question", new String[] {"a4"}, null);
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "templatenamequestion");
		assertEquals(raw_content, template.getContent());
	}

	public void testRemoveFieldRadioPrefix()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		String raw_content = template.getContent();

		builder.generateField(template, new ConstrainedProperty("question").inList(null, "a1", null, "a3", "a2"), new String[] {"a4"}, "another");
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "question", "another");
		assertEquals(raw_content, template.getContent());

		builder.generateField(template, RadioInListEnum.class, "question", new String[] {"a4"}, "another");
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "question", "another");
		assertEquals(raw_content, template.getContent());
	}

	public void testGenerateFieldCheckboxWithDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, "options", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:options", set_values.iterator().next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt></field>", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("options"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:options", set_values.iterator().next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt></field>", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("options").inList(null, "1", null, "3", "2"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:options", set_values.iterator().next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>1</value></field>option1<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>3</value></field>3<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>2</value></field>option2", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, new ConstrainedProperty("options").inList(null, "1", null, "3", "2"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:options", set_values.iterator().next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>1</value></field>option1<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>3</value></field>3<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>2</value></field>option2", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, CheckboxInListEnum.class, "options", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:options", set_values.iterator().next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>1</value></field>option1<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>3</value></field>3<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>2</value></field>option2", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("options").inList(null, "1", null, "3", "2").defaultValue("2"), new String[] {null, "1"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:options", set_values.iterator().next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>1</value><checked>1</checked></field>option1<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>3</value></field>3<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>2</value></field>option2", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, CheckboxInListEnum.class, new ConstrainedProperty("options").defaultValue("2"), new String[] {null, "1"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:options", set_values.iterator().next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>1</value><checked>1</checked></field>option1<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>3</value></field>3<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>2</value></field>option2", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("options").inList(null, "1", null, "3", "2"), new String[] {"1", "2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:options", set_values.iterator().next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>1</value><checked>1</checked></field>option1<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>3</value></field>3<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>2</value><checked>1</checked></field>option2", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, CheckboxInListEnum.class, "options", new String[] {"1", "2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:options", set_values.iterator().next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>1</value><checked>1</checked></field>option1<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>3</value></field>3<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>2</value><checked>1</checked></field>option2", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("options").inList(null, "1", null, "3", "2").defaultValue("3"), new String[] {"1", "2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:options", set_values.iterator().next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>1</value><checked>1</checked></field>option1<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>3</value></field>3<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>2</value><checked>1</checked></field>option2", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, CheckboxInListEnum.class, new ConstrainedProperty("options").defaultValue("3"), new String[] {"1", "2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:options", set_values.iterator().next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>1</value><checked>1</checked></field>option1<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>3</value></field>3<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>2</value><checked>1</checked></field>option2", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("options").inList(null, "1", null, "3", "2"), new String[] {"a4"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:options", set_values.iterator().next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>1</value></field>option1<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>3</value></field>3<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>2</value></field>option2", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, CheckboxInListEnum.class, "options", new String[] {"a4"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:options", set_values.iterator().next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>1</value></field>option1<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>3</value></field>3<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>2</value></field>option2", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:CHECKBOX:options", "already set");
		set_values = builder.generateField(template, new ConstrainedProperty("options"), null, null);
		assertEquals(0, set_values.size());
		assertEquals("already set", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		template.removeValue("FORM:CHECKBOX:options");
	}

	public void testGenerateFieldCheckboxWithoutDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;
		Iterator<String>	set_values_it;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, "otheroptions", null, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:CHECKBOX:otheroptions", set_values_it.next());
		assertEquals("FORM:DISPLAY:otheroptions", set_values_it.next());
		assertEquals("<field><name>otheroptions</name><type>checkbox</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[28]);
		assertEquals("<field><value></value><id>otheroptions[!V 'FORM:VALUE'/]</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[57]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("otheroptions"), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:CHECKBOX:otheroptions", set_values_it.next());
		assertEquals("FORM:DISPLAY:otheroptions", set_values_it.next());
		assertEquals("<field><name>otheroptions</name><type>checkbox</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[28]);
		assertEquals("<field><value></value><id>otheroptions[!V 'FORM:VALUE'/]</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[57]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("otheroptions").inList(null, "1", null, "3", "2"), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:CHECKBOX:otheroptions", set_values_it.next());
		assertEquals("FORM:DISPLAY:otheroptions", set_values_it.next());
		assertEquals("<field><name>otheroptions</name><type>checkbox</type><value>1</value></field>other option 1<field><name>otheroptions</name><type>checkbox</type><value>3</value></field>3<field><name>otheroptions</name><type>checkbox</type><value>2</value></field>other option 2", StringUtils.splitToArray(template.getContent(), "\n")[28]);
		assertEquals("<field><value></value><id>otheroptions[!V 'FORM:VALUE'/]</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[57]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, CheckboxInListEnum.class, "otheroptions", null, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:CHECKBOX:otheroptions", set_values_it.next());
		assertEquals("FORM:DISPLAY:otheroptions", set_values_it.next());
		assertEquals("<field><name>otheroptions</name><type>checkbox</type><value>1</value></field>other option 1<field><name>otheroptions</name><type>checkbox</type><value>3</value></field>3<field><name>otheroptions</name><type>checkbox</type><value>2</value></field>other option 2", StringUtils.splitToArray(template.getContent(), "\n")[28]);
		assertEquals("<field><value></value><id>otheroptions[!V 'FORM:VALUE'/]</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[57]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("otheroptions").inList(null, "1", null, "3", "2").defaultValue("3"), new String[] {"1", "2"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:CHECKBOX:otheroptions", set_values_it.next());
		assertEquals("FORM:DISPLAY:otheroptions", set_values_it.next());
		assertEquals("<field><name>otheroptions</name><type>checkbox</type><value>1</value><checked>1</checked></field>other option 1<field><name>otheroptions</name><type>checkbox</type><value>3</value></field>3<field><name>otheroptions</name><type>checkbox</type><value>2</value><checked>1</checked></field>other option 2", StringUtils.splitToArray(template.getContent(), "\n")[28]);
		assertEquals("<field><value>other option 1</value><id>otheroptions1</id></field><field><value>other option 2</value><id>otheroptions2</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[57]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, CheckboxInListEnum.class, new ConstrainedProperty("otheroptions").defaultValue("3"), new String[] {"1", "2"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:CHECKBOX:otheroptions", set_values_it.next());
		assertEquals("FORM:DISPLAY:otheroptions", set_values_it.next());
		assertEquals("<field><name>otheroptions</name><type>checkbox</type><value>1</value><checked>1</checked></field>other option 1<field><name>otheroptions</name><type>checkbox</type><value>3</value></field>3<field><name>otheroptions</name><type>checkbox</type><value>2</value><checked>1</checked></field>other option 2", StringUtils.splitToArray(template.getContent(), "\n")[28]);
		assertEquals("<field><value>other option 1</value><id>otheroptions1</id></field><field><value>other option 2</value><id>otheroptions2</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[57]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();
	}

	public void testGenerateFieldCheckboxPrefix()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;
		Iterator<String>	set_values_it;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, "options", null, "other");
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:CHECKBOX:otheroptions", set_values_it.next());
		assertEquals("FORM:DISPLAY:otheroptions", set_values_it.next());
		assertEquals("FORM:CHECKBOX:otheroptions", set_values.iterator().next());
		assertEquals("<field><name>otheroptions</name><type>checkbox</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[28]);
		assertEquals("<field><value></value><id>otheroptions[!V 'FORM:VALUE'/]</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[57]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("options"), null, "other");
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:CHECKBOX:otheroptions", set_values_it.next());
		assertEquals("FORM:DISPLAY:otheroptions", set_values_it.next());
		assertEquals("<field><name>otheroptions</name><type>checkbox</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[28]);
		assertEquals("<field><value></value><id>otheroptions[!V 'FORM:VALUE'/]</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[57]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("options").inList(null, "1", null, "3", "2"), null, "other");
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:CHECKBOX:otheroptions", set_values_it.next());
		assertEquals("FORM:DISPLAY:otheroptions", set_values_it.next());
		assertEquals("<field><name>otheroptions</name><type>checkbox</type><value>1</value></field>other option 1<field><name>otheroptions</name><type>checkbox</type><value>3</value></field>3<field><name>otheroptions</name><type>checkbox</type><value>2</value></field>other option 2", StringUtils.splitToArray(template.getContent(), "\n")[28]);
		assertEquals("<field><value></value><id>otheroptions[!V 'FORM:VALUE'/]</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[57]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, CheckboxInListEnum.class, "options", null, "other");
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:CHECKBOX:otheroptions", set_values_it.next());
		assertEquals("FORM:DISPLAY:otheroptions", set_values_it.next());
		assertEquals("<field><name>otheroptions</name><type>checkbox</type><value>1</value></field>other option 1<field><name>otheroptions</name><type>checkbox</type><value>3</value></field>3<field><name>otheroptions</name><type>checkbox</type><value>2</value></field>other option 2", StringUtils.splitToArray(template.getContent(), "\n")[28]);
		assertEquals("<field><value></value><id>otheroptions[!V 'FORM:VALUE'/]</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[57]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("options").inList(null, "1", null, "3", "2").defaultValue("3"), new String[] {"1", "2"}, "other");
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:CHECKBOX:otheroptions", set_values_it.next());
		assertEquals("FORM:DISPLAY:otheroptions", set_values_it.next());
		assertEquals("<field><name>otheroptions</name><type>checkbox</type><value>1</value><checked>1</checked></field>other option 1<field><name>otheroptions</name><type>checkbox</type><value>3</value></field>3<field><name>otheroptions</name><type>checkbox</type><value>2</value><checked>1</checked></field>other option 2", StringUtils.splitToArray(template.getContent(), "\n")[28]);
		assertEquals("<field><value>other option 1</value><id>otheroptions1</id></field><field><value>other option 2</value><id>otheroptions2</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[57]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, CheckboxInListEnum.class, new ConstrainedProperty("options").defaultValue("3"), new String[] {"1", "2"}, "other");
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:CHECKBOX:otheroptions", set_values_it.next());
		assertEquals("FORM:DISPLAY:otheroptions", set_values_it.next());
		assertEquals("<field><name>otheroptions</name><type>checkbox</type><value>1</value><checked>1</checked></field>other option 1<field><name>otheroptions</name><type>checkbox</type><value>3</value></field>3<field><name>otheroptions</name><type>checkbox</type><value>2</value><checked>1</checked></field>other option 2", StringUtils.splitToArray(template.getContent(), "\n")[28]);
		assertEquals("<field><value>other option 1</value><id>otheroptions1</id></field><field><value>other option 2</value><id>otheroptions2</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[57]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		template.setValue("FORM:CHECKBOX:otheroptions", "already set");
		template.setValue("FORM:DISPLAY:otheroptions", "already set too");
		set_values = builder.generateField(template, new ConstrainedProperty("options"), null, "other");
		assertEquals(0, set_values.size());
		assertEquals("already set", StringUtils.splitToArray(template.getContent(), "\n")[28]);
		assertEquals("already set too", StringUtils.splitToArray(template.getContent(), "\n")[57]);
		template.removeValue("FORM:DISPLAY:otheroptions");
		template.removeValue("FORM:CHECKBOX:otheroptions");
	}

	public void testGenerateFieldCheckboxTemplateNameWithDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields_templatename");

		set_values = builder.replaceField(template, "templatenameoptions", "options", null, null);
		assertEquals(2, set_values.size());
		Iterator set_values_it = set_values.iterator();
		assertEquals("FORM:CHECKBOX:templatenameoptions", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenameoptions", set_values_it.next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt></field>", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		assertEquals("<field><value></value><id>options[!V 'FORM:VALUE'/]</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[57]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameoptions", new ConstrainedProperty("options"), null, null);
		assertEquals(2, set_values.size());
		set_values_it = set_values.iterator();
		assertEquals("FORM:CHECKBOX:templatenameoptions", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenameoptions", set_values_it.next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt></field>", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		assertEquals("<field><value></value><id>options[!V 'FORM:VALUE'/]</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[57]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameoptions", new ConstrainedProperty("options").inList(null, "1", null, "3", "2"), null, null);
		assertEquals(2, set_values.size());
		set_values_it = set_values.iterator();
		assertEquals("FORM:CHECKBOX:templatenameoptions", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenameoptions", set_values_it.next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>1</value></field>option1<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>3</value></field>3<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>2</value></field>option2", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		assertEquals("<field><value></value><id>options[!V 'FORM:VALUE'/]</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[57]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameoptions", CheckboxInListEnum.class, "options", null, null);
		assertEquals(2, set_values.size());
		set_values_it = set_values.iterator();
		assertEquals("FORM:CHECKBOX:templatenameoptions", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenameoptions", set_values_it.next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>1</value></field>option1<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>3</value></field>3<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>2</value></field>option2", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		assertEquals("<field><value></value><id>options[!V 'FORM:VALUE'/]</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[57]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameoptions", new ConstrainedProperty("options").inList(null, "1", null, "3", "2").defaultValue("2"), new String[] {null, "1"}, null);
		assertEquals(2, set_values.size());
		set_values_it = set_values.iterator();
		assertEquals("FORM:CHECKBOX:templatenameoptions", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenameoptions", set_values_it.next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>1</value><checked>1</checked></field>option1<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>3</value></field>3<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>2</value></field>option2", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		assertEquals("<field><value>option2</value><id>options2</id></field><field><value>option1</value><id>options1</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[57]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameoptions", CheckboxInListEnum.class, new ConstrainedProperty("options").defaultValue("2"), new String[] {null, "1"}, null);
		assertEquals(2, set_values.size());
		set_values_it = set_values.iterator();
		assertEquals("FORM:CHECKBOX:templatenameoptions", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenameoptions", set_values_it.next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>1</value><checked>1</checked></field>option1<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>3</value></field>3<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>2</value></field>option2", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		assertEquals("<field><value>option2</value><id>options2</id></field><field><value>option1</value><id>options1</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[57]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameoptions", new ConstrainedProperty("options").inList(null, "1", null, "3", "2"), new String[] {"1", "2"}, null);
		assertEquals(2, set_values.size());
		set_values_it = set_values.iterator();
		assertEquals("FORM:CHECKBOX:templatenameoptions", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenameoptions", set_values_it.next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>1</value><checked>1</checked></field>option1<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>3</value></field>3<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>2</value><checked>1</checked></field>option2", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		assertEquals("<field><value>option1</value><id>options1</id></field><field><value>option2</value><id>options2</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[57]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameoptions", CheckboxInListEnum.class, "options", new String[] {"1", "2"}, null);
		assertEquals(2, set_values.size());
		set_values_it = set_values.iterator();
		assertEquals("FORM:CHECKBOX:templatenameoptions", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenameoptions", set_values_it.next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>1</value><checked>1</checked></field>option1<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>3</value></field>3<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>2</value><checked>1</checked></field>option2", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		assertEquals("<field><value>option1</value><id>options1</id></field><field><value>option2</value><id>options2</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[57]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameoptions", new ConstrainedProperty("options").inList(null, "1", null, "3", "2").defaultValue("3"), new String[] {"1", "2"}, null);
		assertEquals(2, set_values.size());
		set_values_it = set_values.iterator();
		assertEquals("FORM:CHECKBOX:templatenameoptions", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenameoptions", set_values_it.next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>1</value><checked>1</checked></field>option1<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>3</value></field>3<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>2</value><checked>1</checked></field>option2", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		assertEquals("<field><value>option1</value><id>options1</id></field><field><value>option2</value><id>options2</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[57]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameoptions", CheckboxInListEnum.class, new ConstrainedProperty("options").defaultValue("3"), new String[] {"1", "2"}, null);
		assertEquals(2, set_values.size());
		set_values_it = set_values.iterator();
		assertEquals("FORM:CHECKBOX:templatenameoptions", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenameoptions", set_values_it.next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>1</value><checked>1</checked></field>option1<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>3</value></field>3<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>2</value><checked>1</checked></field>option2", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		assertEquals("<field><value>option1</value><id>options1</id></field><field><value>option2</value><id>options2</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[57]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameoptions", new ConstrainedProperty("options").inList(null, "1", null, "3", "2"), new String[] {"a4"}, null);
		assertEquals(2, set_values.size());
		set_values_it = set_values.iterator();
		assertEquals("FORM:CHECKBOX:templatenameoptions", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenameoptions", set_values_it.next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>1</value></field>option1<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>3</value></field>3<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>2</value></field>option2", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		assertEquals("<field><value>a4</value><id>optionsa4</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[57]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameoptions", CheckboxInListEnum.class, "options", new String[] {"a4"}, null);
		assertEquals(2, set_values.size());
		set_values_it = set_values.iterator();
		assertEquals("FORM:CHECKBOX:templatenameoptions", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenameoptions", set_values_it.next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>1</value></field>option1<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>3</value></field>3<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>2</value></field>option2", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		assertEquals("<field><value>a4</value><id>optionsa4</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[57]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:CHECKBOX:templatenameoptions", "already set");
		set_values = builder.replaceField(template, "templatenameoptions", new ConstrainedProperty("options"), null, null);
		assertEquals(2, set_values.size());
		set_values_it = set_values.iterator();
		assertEquals("FORM:CHECKBOX:templatenameoptions", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenameoptions", set_values_it.next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt></field>", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		assertEquals("<field><value></value><id>options[!V 'FORM:VALUE'/]</id></field>", StringUtils.splitToArray(template.getContent(), "\n")[57]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();
	}

	public void testGenerateFieldCheckboxDynamic()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		template.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"options:1", "dynamic first"},
					{"options:2", "dynamic second"},
					{"options:3", "dynamic third"}
				};
			}});

		set_values = builder.generateField(template, new ConstrainedProperty("options").inList("1", "3", "2").defaultValue("2"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:options", set_values.iterator().next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>1</value></field>dynamic first<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>3</value></field>dynamic third<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>2</value><checked>1</checked></field>dynamic second", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, CheckboxInListEnum.class, new ConstrainedProperty("options").defaultValue("2"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:options", set_values.iterator().next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>1</value></field>dynamic first<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>3</value></field>dynamic third<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>2</value><checked>1</checked></field>dynamic second", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("options").inList("1", "3", "2").defaultValue("2"), new String[] {"3", "1"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:options", set_values.iterator().next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>1</value><checked>1</checked></field>dynamic first<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>3</value><checked>1</checked></field>dynamic third<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>2</value></field>dynamic second", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, CheckboxInListEnum.class, new ConstrainedProperty("options").defaultValue("2"), new String[] {"3", "1"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:options", set_values.iterator().next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>1</value><checked>1</checked></field>dynamic first<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>3</value><checked>1</checked></field>dynamic third<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>2</value></field>dynamic second", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.clear();

		template.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"options:2", "dynamic second"},
				};
			}});

		set_values = builder.generateField(template, new ConstrainedProperty("options").inList("1", "3", "2").defaultValue("2"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:options", set_values.iterator().next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>1</value></field>option1<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>3</value></field>3<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>2</value><checked>1</checked></field>dynamic second", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, CheckboxInListEnum.class, new ConstrainedProperty("options").defaultValue("2"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:options", set_values.iterator().next());
		assertEquals("<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>1</value></field>option1<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>3</value></field>3<field><name>options</name><type>checkbox</type><alt>someblurp</alt><value>2</value><checked>1</checked></field>dynamic second", StringUtils.splitToArray(template.getContent(), "\n")[24]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();
	}

	public void testGenerateFieldCheckboxCustomWithDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, "customoptions", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:customoptions", set_values.iterator().next());
		assertEquals("<field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt></field>", StringUtils.splitToArray(template.getContent(), "\n")[32]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("customoptions"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:customoptions", set_values.iterator().next());
		assertEquals("<field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt></field>", StringUtils.splitToArray(template.getContent(), "\n")[32]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("customoptions").inList(null, "1", null, "3", "2"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:customoptions", set_values.iterator().next());
		assertEquals("custom option 1 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>1</value></field>3 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>3</value></field>custom option 2 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[32]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, CheckboxInListEnum.class, "customoptions", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:customoptions", set_values.iterator().next());
		assertEquals("custom option 1 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>1</value></field>3 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>3</value></field>custom option 2 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[32]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("customoptions").inList(null, "1", null, "3", "2").defaultValue("2"), new String[] {null, "1"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:customoptions", set_values.iterator().next());
		assertEquals("custom option 1 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>1</value><checked>1</checked></field>3 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>3</value></field>custom option 2 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[32]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, CheckboxInListEnum.class, new ConstrainedProperty("customoptions").defaultValue("2"), new String[] {null, "1"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:customoptions", set_values.iterator().next());
		assertEquals("custom option 1 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>1</value><checked>1</checked></field>3 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>3</value></field>custom option 2 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[32]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("customoptions").inList(null, "1", null, "3", "2"), new String[] {"1", "2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:customoptions", set_values.iterator().next());
		assertEquals("custom option 1 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>1</value><checked>1</checked></field>3 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>3</value></field>custom option 2 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>2</value><checked>1</checked></field>", StringUtils.splitToArray(template.getContent(), "\n")[32]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, CheckboxInListEnum.class, "customoptions", new String[] {"1", "2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:customoptions", set_values.iterator().next());
		assertEquals("custom option 1 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>1</value><checked>1</checked></field>3 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>3</value></field>custom option 2 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>2</value><checked>1</checked></field>", StringUtils.splitToArray(template.getContent(), "\n")[32]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("customoptions").inList(null, "1", null, "3", "2").defaultValue("3"), new String[] {"1", "2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:customoptions", set_values.iterator().next());
		assertEquals("custom option 1 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>1</value><checked>1</checked></field>3 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>3</value></field>custom option 2 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>2</value><checked>1</checked></field>", StringUtils.splitToArray(template.getContent(), "\n")[32]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, CheckboxInListEnum.class, new ConstrainedProperty("customoptions").defaultValue("3"), new String[] {"1", "2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:customoptions", set_values.iterator().next());
		assertEquals("custom option 1 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>1</value><checked>1</checked></field>3 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>3</value></field>custom option 2 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>2</value><checked>1</checked></field>", StringUtils.splitToArray(template.getContent(), "\n")[32]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("customoptions").inList(null, "1", null, "3", "2"), new String[] {"a4"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:customoptions", set_values.iterator().next());
		assertEquals("custom option 1 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>1</value></field>3 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>3</value></field>custom option 2 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[32]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, CheckboxInListEnum.class, "customoptions", new String[] {"a4"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:customoptions", set_values.iterator().next());
		assertEquals("custom option 1 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>1</value></field>3 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>3</value></field>custom option 2 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[32]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:CHECKBOX:customoptions", "already set");
		set_values = builder.generateField(template, new ConstrainedProperty("customoptions"), null, null);
		assertEquals(0, set_values.size());
		assertEquals("already set", StringUtils.splitToArray(template.getContent(), "\n")[32]);
		template.removeValue("FORM:CHECKBOX:customoptions");
	}

	public void testGenerateFieldCheckboxCustomWithoutDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, "othercustomoptions", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:othercustomoptions", set_values.iterator().next());
		assertEquals("<field><name>othercustomoptions</name><type>checkbox</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[37]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("othercustomoptions"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:othercustomoptions", set_values.iterator().next());
		assertEquals("<field><name>othercustomoptions</name><type>checkbox</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[37]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("othercustomoptions").inList(null, "1", null, "3", "2"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:othercustomoptions", set_values.iterator().next());
		assertEquals("othercustom option 1 : <field><name>othercustomoptions</name><type>checkbox</type><value>1</value></field>3 : <field><name>othercustomoptions</name><type>checkbox</type><value>3</value></field>othercustom option 2 : <field><name>othercustomoptions</name><type>checkbox</type><value>2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[37]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, CheckboxInListEnum.class, "othercustomoptions", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:othercustomoptions", set_values.iterator().next());
		assertEquals("othercustom option 1 : <field><name>othercustomoptions</name><type>checkbox</type><value>1</value></field>3 : <field><name>othercustomoptions</name><type>checkbox</type><value>3</value></field>othercustom option 2 : <field><name>othercustomoptions</name><type>checkbox</type><value>2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[37]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("othercustomoptions").inList(null, "1", null, "3", "2").defaultValue("2"), new String[] {null, "1"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:othercustomoptions", set_values.iterator().next());
		assertEquals("othercustom option 1 : <field><name>othercustomoptions</name><type>checkbox</type><value>1</value><checked>1</checked></field>3 : <field><name>othercustomoptions</name><type>checkbox</type><value>3</value></field>othercustom option 2 : <field><name>othercustomoptions</name><type>checkbox</type><value>2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[37]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, CheckboxInListEnum.class, new ConstrainedProperty("othercustomoptions").defaultValue("2"), new String[] {null, "1"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:othercustomoptions", set_values.iterator().next());
		assertEquals("othercustom option 1 : <field><name>othercustomoptions</name><type>checkbox</type><value>1</value><checked>1</checked></field>3 : <field><name>othercustomoptions</name><type>checkbox</type><value>3</value></field>othercustom option 2 : <field><name>othercustomoptions</name><type>checkbox</type><value>2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[37]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("othercustomoptions").inList(null, "1", null, "3", "2"), new String[] {"1", "2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:othercustomoptions", set_values.iterator().next());
		assertEquals("othercustom option 1 : <field><name>othercustomoptions</name><type>checkbox</type><value>1</value><checked>1</checked></field>3 : <field><name>othercustomoptions</name><type>checkbox</type><value>3</value></field>othercustom option 2 : <field><name>othercustomoptions</name><type>checkbox</type><value>2</value><checked>1</checked></field>", StringUtils.splitToArray(template.getContent(), "\n")[37]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, CheckboxInListEnum.class, "othercustomoptions", new String[] {"1", "2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:othercustomoptions", set_values.iterator().next());
		assertEquals("othercustom option 1 : <field><name>othercustomoptions</name><type>checkbox</type><value>1</value><checked>1</checked></field>3 : <field><name>othercustomoptions</name><type>checkbox</type><value>3</value></field>othercustom option 2 : <field><name>othercustomoptions</name><type>checkbox</type><value>2</value><checked>1</checked></field>", StringUtils.splitToArray(template.getContent(), "\n")[37]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("othercustomoptions").inList(null, "1", null, "3", "2").defaultValue("3"), new String[] {"1", "2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:othercustomoptions", set_values.iterator().next());
		assertEquals("othercustom option 1 : <field><name>othercustomoptions</name><type>checkbox</type><value>1</value><checked>1</checked></field>3 : <field><name>othercustomoptions</name><type>checkbox</type><value>3</value></field>othercustom option 2 : <field><name>othercustomoptions</name><type>checkbox</type><value>2</value><checked>1</checked></field>", StringUtils.splitToArray(template.getContent(), "\n")[37]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, CheckboxInListEnum.class, new ConstrainedProperty("othercustomoptions").defaultValue("3"), new String[] {"1", "2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:othercustomoptions", set_values.iterator().next());
		assertEquals("othercustom option 1 : <field><name>othercustomoptions</name><type>checkbox</type><value>1</value><checked>1</checked></field>3 : <field><name>othercustomoptions</name><type>checkbox</type><value>3</value></field>othercustom option 2 : <field><name>othercustomoptions</name><type>checkbox</type><value>2</value><checked>1</checked></field>", StringUtils.splitToArray(template.getContent(), "\n")[37]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("othercustomoptions").inList(null, "1", null, "3", "2"), new String[] {"a4"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:othercustomoptions", set_values.iterator().next());
		assertEquals("othercustom option 1 : <field><name>othercustomoptions</name><type>checkbox</type><value>1</value></field>3 : <field><name>othercustomoptions</name><type>checkbox</type><value>3</value></field>othercustom option 2 : <field><name>othercustomoptions</name><type>checkbox</type><value>2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[37]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, CheckboxInListEnum.class, "othercustomoptions", new String[] {"a4"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:othercustomoptions", set_values.iterator().next());
		assertEquals("othercustom option 1 : <field><name>othercustomoptions</name><type>checkbox</type><value>1</value></field>3 : <field><name>othercustomoptions</name><type>checkbox</type><value>3</value></field>othercustom option 2 : <field><name>othercustomoptions</name><type>checkbox</type><value>2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[37]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:CHECKBOX:othercustomoptions", "already set");
		set_values = builder.generateField(template, new ConstrainedProperty("othercustomoptions"), null, null);
		assertEquals(0, set_values.size());
		assertEquals("already set", StringUtils.splitToArray(template.getContent(), "\n")[37]);
		template.removeValue("FORM:CHECKBOX:othercustomoptions");
	}

	public void testGenerateFieldCheckboxTemplatNameCustomWithoutDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields_templatename");

		set_values = builder.replaceField(template, "templatenameothercustomoptions", "othercustomoptions", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:templatenameothercustomoptions", set_values.iterator().next());
		assertEquals("<field><name>othercustomoptions</name><type>checkbox</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[37]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameothercustomoptions", new ConstrainedProperty("othercustomoptions"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:templatenameothercustomoptions", set_values.iterator().next());
		assertEquals("<field><name>othercustomoptions</name><type>checkbox</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[37]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameothercustomoptions", new ConstrainedProperty("othercustomoptions").inList(null, "1", null, "3", "2"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:templatenameothercustomoptions", set_values.iterator().next());
		assertEquals("othercustom option 1 : <field><name>othercustomoptions</name><type>checkbox</type><value>1</value></field>3 : <field><name>othercustomoptions</name><type>checkbox</type><value>3</value></field>othercustom option 2 : <field><name>othercustomoptions</name><type>checkbox</type><value>2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[37]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameothercustomoptions", CheckboxInListEnum.class, "othercustomoptions", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:templatenameothercustomoptions", set_values.iterator().next());
		assertEquals("othercustom option 1 : <field><name>othercustomoptions</name><type>checkbox</type><value>1</value></field>3 : <field><name>othercustomoptions</name><type>checkbox</type><value>3</value></field>othercustom option 2 : <field><name>othercustomoptions</name><type>checkbox</type><value>2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[37]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameothercustomoptions", new ConstrainedProperty("othercustomoptions").inList(null, "1", null, "3", "2").defaultValue("2"), new String[] {null, "1"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:templatenameothercustomoptions", set_values.iterator().next());
		assertEquals("othercustom option 1 : <field><name>othercustomoptions</name><type>checkbox</type><value>1</value><checked>1</checked></field>3 : <field><name>othercustomoptions</name><type>checkbox</type><value>3</value></field>othercustom option 2 : <field><name>othercustomoptions</name><type>checkbox</type><value>2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[37]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameothercustomoptions", CheckboxInListEnum.class, new ConstrainedProperty("othercustomoptions").defaultValue("2"), new String[] {null, "1"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:templatenameothercustomoptions", set_values.iterator().next());
		assertEquals("othercustom option 1 : <field><name>othercustomoptions</name><type>checkbox</type><value>1</value><checked>1</checked></field>3 : <field><name>othercustomoptions</name><type>checkbox</type><value>3</value></field>othercustom option 2 : <field><name>othercustomoptions</name><type>checkbox</type><value>2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[37]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameothercustomoptions", new ConstrainedProperty("othercustomoptions").inList(null, "1", null, "3", "2"), new String[] {"1", "2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:templatenameothercustomoptions", set_values.iterator().next());
		assertEquals("othercustom option 1 : <field><name>othercustomoptions</name><type>checkbox</type><value>1</value><checked>1</checked></field>3 : <field><name>othercustomoptions</name><type>checkbox</type><value>3</value></field>othercustom option 2 : <field><name>othercustomoptions</name><type>checkbox</type><value>2</value><checked>1</checked></field>", StringUtils.splitToArray(template.getContent(), "\n")[37]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameothercustomoptions", CheckboxInListEnum.class, "othercustomoptions", new String[] {"1", "2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:templatenameothercustomoptions", set_values.iterator().next());
		assertEquals("othercustom option 1 : <field><name>othercustomoptions</name><type>checkbox</type><value>1</value><checked>1</checked></field>3 : <field><name>othercustomoptions</name><type>checkbox</type><value>3</value></field>othercustom option 2 : <field><name>othercustomoptions</name><type>checkbox</type><value>2</value><checked>1</checked></field>", StringUtils.splitToArray(template.getContent(), "\n")[37]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameothercustomoptions", new ConstrainedProperty("othercustomoptions").inList(null, "1", null, "3", "2").defaultValue("3"), new String[] {"1", "2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:templatenameothercustomoptions", set_values.iterator().next());
		assertEquals("othercustom option 1 : <field><name>othercustomoptions</name><type>checkbox</type><value>1</value><checked>1</checked></field>3 : <field><name>othercustomoptions</name><type>checkbox</type><value>3</value></field>othercustom option 2 : <field><name>othercustomoptions</name><type>checkbox</type><value>2</value><checked>1</checked></field>", StringUtils.splitToArray(template.getContent(), "\n")[37]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameothercustomoptions", CheckboxInListEnum.class, new ConstrainedProperty("othercustomoptions").defaultValue("3"), new String[] {"1", "2"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:templatenameothercustomoptions", set_values.iterator().next());
		assertEquals("othercustom option 1 : <field><name>othercustomoptions</name><type>checkbox</type><value>1</value><checked>1</checked></field>3 : <field><name>othercustomoptions</name><type>checkbox</type><value>3</value></field>othercustom option 2 : <field><name>othercustomoptions</name><type>checkbox</type><value>2</value><checked>1</checked></field>", StringUtils.splitToArray(template.getContent(), "\n")[37]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameothercustomoptions", new ConstrainedProperty("othercustomoptions").inList(null, "1", null, "3", "2"), new String[] {"a4"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:templatenameothercustomoptions", set_values.iterator().next());
		assertEquals("othercustom option 1 : <field><name>othercustomoptions</name><type>checkbox</type><value>1</value></field>3 : <field><name>othercustomoptions</name><type>checkbox</type><value>3</value></field>othercustom option 2 : <field><name>othercustomoptions</name><type>checkbox</type><value>2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[37]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameothercustomoptions", CheckboxInListEnum.class, "othercustomoptions", new String[] {"a4"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:templatenameothercustomoptions", set_values.iterator().next());
		assertEquals("othercustom option 1 : <field><name>othercustomoptions</name><type>checkbox</type><value>1</value></field>3 : <field><name>othercustomoptions</name><type>checkbox</type><value>3</value></field>othercustom option 2 : <field><name>othercustomoptions</name><type>checkbox</type><value>2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[37]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:CHECKBOX:templatenameothercustomoptions", "already set");
		set_values = builder.replaceField(template, "templatenameothercustomoptions", new ConstrainedProperty("othercustomoptions"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:templatenameothercustomoptions", set_values.iterator().next());
		assertEquals("<field><name>othercustomoptions</name><type>checkbox</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[37]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();
	}

	public void testGenerateFieldCheckboxCustomPrefix()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, new ConstrainedProperty("customoptions").inList(null, "1", null, "3", "2"), null, "other");
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:othercustomoptions", set_values.iterator().next());
		assertEquals("othercustom option 1 : <field><name>othercustomoptions</name><type>checkbox</type><value>1</value></field>3 : <field><name>othercustomoptions</name><type>checkbox</type><value>3</value></field>othercustom option 2 : <field><name>othercustomoptions</name><type>checkbox</type><value>2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[37]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, CheckboxInListEnum.class, "customoptions", null, "other");
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:othercustomoptions", set_values.iterator().next());
		assertEquals("othercustom option 1 : <field><name>othercustomoptions</name><type>checkbox</type><value>1</value></field>3 : <field><name>othercustomoptions</name><type>checkbox</type><value>3</value></field>othercustom option 2 : <field><name>othercustomoptions</name><type>checkbox</type><value>2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[37]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:CHECKBOX:othercustomoptions", "already set");
		set_values = builder.generateField(template, new ConstrainedProperty("customoptions"), null, "other");
		assertEquals(0, set_values.size());
		assertEquals("already set", StringUtils.splitToArray(template.getContent(), "\n")[37]);
		template.removeValue("FORM:CHECKBOX:othercustomoptions");
	}

	public void testGenerateFieldCheckboxCustomDynamic()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		template.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"customoptions:1", "dynamic first"},
					{"customoptions:2", "dynamic second"},
					{"customoptions:3", "dynamic third"}
				};
			}});

		set_values = builder.generateField(template, new ConstrainedProperty("customoptions").inList("1", "3", "2").defaultValue("2"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:customoptions", set_values.iterator().next());
		assertEquals("dynamic first : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>1</value></field>dynamic third : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>3</value></field>dynamic second : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>2</value><checked>1</checked></field>", StringUtils.splitToArray(template.getContent(), "\n")[32]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, CheckboxInListEnum.class, new ConstrainedProperty("customoptions").defaultValue("2"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:customoptions", set_values.iterator().next());
		assertEquals("dynamic first : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>1</value></field>dynamic third : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>3</value></field>dynamic second : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>2</value><checked>1</checked></field>", StringUtils.splitToArray(template.getContent(), "\n")[32]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("customoptions").inList("1", "3", "2").defaultValue("2"), new String[] {"1"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:customoptions", set_values.iterator().next());
		assertEquals("dynamic first : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>1</value><checked>1</checked></field>dynamic third : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>3</value></field>dynamic second : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[32]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, CheckboxInListEnum.class, new ConstrainedProperty("customoptions").defaultValue("2"), new String[] {"1"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:customoptions", set_values.iterator().next());
		assertEquals("dynamic first : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>1</value><checked>1</checked></field>dynamic third : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>3</value></field>dynamic second : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[32]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.clear();

		template.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"customoptions:2", "dynamic second"},
				};
			}});

		set_values = builder.generateField(template, new ConstrainedProperty("customoptions").inList("1", "3", "2").defaultValue("2"), new String[] {"1"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:customoptions", set_values.iterator().next());
		assertEquals("custom option 1 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>1</value><checked>1</checked></field>3 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>3</value></field>dynamic second : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[32]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, CheckboxInListEnum.class, new ConstrainedProperty("customoptions").defaultValue("2"), new String[] {"1"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:customoptions", set_values.iterator().next());
		assertEquals("custom option 1 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>1</value><checked>1</checked></field>3 : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>3</value></field>dynamic second : <field><name>customoptions</name><type>checkbox</type><alt>customblurp</alt><value>2</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[32]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();
	}

	public void testGenerateFieldCheckboxEmptyCustomWithDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields_emptycustom");

		set_values = builder.generateField(template, "emptycustomoptions", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:emptycustomoptions", set_values.iterator().next());
		assertEquals("<field><name>emptycustomoptions</name><type>checkbox</type><alt>emptycustomblurp</alt></field>", StringUtils.splitToArray(template.getContent(), "\n")[4]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("emptycustomoptions"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:emptycustomoptions", set_values.iterator().next());
		assertEquals("<field><name>emptycustomoptions</name><type>checkbox</type><alt>emptycustomblurp</alt></field>", StringUtils.splitToArray(template.getContent(), "\n")[4]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("emptycustomoptions").inList(null, "1", null, "3", "2"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:emptycustomoptions", set_values.iterator().next());
		assertEquals(" -  -  - ", StringUtils.splitToArray(template.getContent(), "\n")[4]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, CheckboxInListEnum.class, "emptycustomoptions", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:emptycustomoptions", set_values.iterator().next());
		assertEquals(" -  -  - ", StringUtils.splitToArray(template.getContent(), "\n")[4]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:CHECKBOX:emptycustomoptions", "already set");
		set_values = builder.generateField(template, new ConstrainedProperty("emptycustomoptions"), null, null);
		assertEquals(0, set_values.size());
		assertEquals("already set", StringUtils.splitToArray(template.getContent(), "\n")[4]);
		template.removeValue("FORM:CHECKBOX:emptycustomoptions");
	}

	public void testGenerateFieldCheckboxEmptyCustomWithoutDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields_emptycustom");

		set_values = builder.generateField(template, "otheremptycustomoptions", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:otheremptycustomoptions", set_values.iterator().next());
		assertEquals("<field><name>otheremptycustomoptions</name><type>checkbox</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("otheremptycustomoptions"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:otheremptycustomoptions", set_values.iterator().next());
		assertEquals("<field><name>otheremptycustomoptions</name><type>checkbox</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("otheremptycustomoptions").inList(null, "1", null, "3", "2"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:otheremptycustomoptions", set_values.iterator().next());
		assertEquals(" -  -  - ", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, CheckboxInListEnum.class, "otheremptycustomoptions", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:otheremptycustomoptions", set_values.iterator().next());
		assertEquals(" -  -  - ", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:CHECKBOX:otheremptycustomoptions", "already set");
		set_values = builder.generateField(template, new ConstrainedProperty("otheremptycustomoptions"), null, null);
		assertEquals(0, set_values.size());
		assertEquals("already set", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		template.removeValue("FORM:CHECKBOX:otheremptycustomoptions");
	}

	public void testGenerateFieldCheckboxEmptyCustomPrefix()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields_emptycustom");

		set_values = builder.generateField(template, "emptycustomoptions", null, "other");
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:otheremptycustomoptions", set_values.iterator().next());
		assertEquals("<field><name>otheremptycustomoptions</name><type>checkbox</type></field>", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("emptycustomoptions").inList(null, "1", null, "3", "2"), null, "other");
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:otheremptycustomoptions", set_values.iterator().next());
		assertEquals(" -  -  - ", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, CheckboxInListEnum.class, "emptycustomoptions", null, "other");
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:otheremptycustomoptions", set_values.iterator().next());
		assertEquals(" -  -  - ", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:CHECKBOX:otheremptycustomoptions", "already set");
		set_values = builder.generateField(template, new ConstrainedProperty("emptycustomoptions"), null, "other");
		assertEquals(0, set_values.size());
		assertEquals("already set", StringUtils.splitToArray(template.getContent(), "\n")[6]);
		template.removeValue("FORM:CHECKBOX:otheremptycustomoptions");
	}

	public void testGenerateFieldCheckboxBooleanWithDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, "invoice", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:invoice", set_values.iterator().next());
		assertEquals("<field><name>invoice</name><type>checkbox</type><alt>atext</alt></field>I want an invoice", StringUtils.splitToArray(template.getContent(), "\n")[42]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("invoice"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:invoice", set_values.iterator().next());
		assertEquals("<field><name>invoice</name><type>checkbox</type><alt>atext</alt></field>I want an invoice", StringUtils.splitToArray(template.getContent(), "\n")[42]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("invoice").defaultValue(true), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:invoice", set_values.iterator().next());
		assertEquals("<field><name>invoice</name><type>checkbox</type><alt>atext</alt><checked>1</checked></field>I want an invoice", StringUtils.splitToArray(template.getContent(), "\n")[42]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("invoice").defaultValue("false"), new String[] {"true"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:invoice", set_values.iterator().next());
		assertEquals("<field><name>invoice</name><type>checkbox</type><alt>atext</alt><checked>1</checked></field>I want an invoice", StringUtils.splitToArray(template.getContent(), "\n")[42]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("invoice").defaultValue("flam"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:invoice", set_values.iterator().next());
		assertEquals("<field><name>invoice</name><type>checkbox</type><alt>atext</alt></field>I want an invoice", StringUtils.splitToArray(template.getContent(), "\n")[42]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("invoice").defaultValue("false"), new String[] {"flum"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:invoice", set_values.iterator().next());
		assertEquals("<field><name>invoice</name><type>checkbox</type><alt>atext</alt></field>I want an invoice", StringUtils.splitToArray(template.getContent(), "\n")[42]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:CHECKBOX:invoice", "set : ");
		set_values = builder.generateField(template, new ConstrainedProperty("invoice"), null, null);
		assertEquals(0, set_values.size());
		assertEquals("set : I want an invoice", StringUtils.splitToArray(template.getContent(), "\n")[42]);
		template.removeValue("FORM:CHECKBOX:invoice");
	}

	public void testGenerateFieldCheckboxBooleanWithoutDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, "onemoreinvoice", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:onemoreinvoice", set_values.iterator().next());
		assertEquals("<field><name>onemoreinvoice</name><type>checkbox</type></field>I want one more invoice", StringUtils.splitToArray(template.getContent(), "\n")[43]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("onemoreinvoice"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:onemoreinvoice", set_values.iterator().next());
		assertEquals("<field><name>onemoreinvoice</name><type>checkbox</type></field>I want one more invoice", StringUtils.splitToArray(template.getContent(), "\n")[43]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("onemoreinvoice").defaultValue("false"), new String[] {"true"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:onemoreinvoice", set_values.iterator().next());
		assertEquals("<field><name>onemoreinvoice</name><type>checkbox</type><checked>1</checked></field>I want one more invoice", StringUtils.splitToArray(template.getContent(), "\n")[43]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:CHECKBOX:onemoreinvoice", "set : ");
		set_values = builder.generateField(template, new ConstrainedProperty("onemoreinvoice"), null, null);
		assertEquals(0, set_values.size());
		assertEquals("set : I want one more invoice", StringUtils.splitToArray(template.getContent(), "\n")[43]);
		template.removeValue("FORM:CHECKBOX:onemoreinvoice");
	}

	public void testGenerateFieldCheckboxTemplateNameBooleanWithDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields_templatename");

		set_values = builder.replaceField(template, "templatenameinvoice", "invoice", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:templatenameinvoice", set_values.iterator().next());
		assertEquals("<field><name>invoice</name><type>checkbox</type><alt>atext</alt></field>I want an invoice", StringUtils.splitToArray(template.getContent(), "\n")[42]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameinvoice", new ConstrainedProperty("invoice"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:templatenameinvoice", set_values.iterator().next());
		assertEquals("<field><name>invoice</name><type>checkbox</type><alt>atext</alt></field>I want an invoice", StringUtils.splitToArray(template.getContent(), "\n")[42]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameinvoice", new ConstrainedProperty("invoice").defaultValue(true), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:templatenameinvoice", set_values.iterator().next());
		assertEquals("<field><name>invoice</name><type>checkbox</type><alt>atext</alt><checked>1</checked></field>I want an invoice", StringUtils.splitToArray(template.getContent(), "\n")[42]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameinvoice", new ConstrainedProperty("invoice").defaultValue("false"), new String[] {"true"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:templatenameinvoice", set_values.iterator().next());
		assertEquals("<field><name>invoice</name><type>checkbox</type><alt>atext</alt><checked>1</checked></field>I want an invoice", StringUtils.splitToArray(template.getContent(), "\n")[42]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameinvoice", new ConstrainedProperty("invoice").defaultValue("flam"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:templatenameinvoice", set_values.iterator().next());
		assertEquals("<field><name>invoice</name><type>checkbox</type><alt>atext</alt></field>I want an invoice", StringUtils.splitToArray(template.getContent(), "\n")[42]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenameinvoice", new ConstrainedProperty("invoice").defaultValue("false"), new String[] {"flum"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:templatenameinvoice", set_values.iterator().next());
		assertEquals("<field><name>invoice</name><type>checkbox</type><alt>atext</alt></field>I want an invoice", StringUtils.splitToArray(template.getContent(), "\n")[42]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:CHECKBOX:templatenameinvoice", "set : ");
		set_values = builder.replaceField(template, "templatenameinvoice", new ConstrainedProperty("invoice"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:templatenameinvoice", set_values.iterator().next());
		assertEquals("<field><name>invoice</name><type>checkbox</type><alt>atext</alt></field>I want an invoice", StringUtils.splitToArray(template.getContent(), "\n")[42]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();
	}

	public void testGenerateFieldCheckboxBooleanPrefix()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, "invoice", null, "onemore");
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:onemoreinvoice", set_values.iterator().next());
		assertEquals("<field><name>onemoreinvoice</name><type>checkbox</type></field>I want one more invoice", StringUtils.splitToArray(template.getContent(), "\n")[43]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("invoice"), null, "onemore");
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:onemoreinvoice", set_values.iterator().next());
		assertEquals("<field><name>onemoreinvoice</name><type>checkbox</type></field>I want one more invoice", StringUtils.splitToArray(template.getContent(), "\n")[43]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("invoice").defaultValue("false"), new String[] {"true"}, "onemore");
		assertEquals(1, set_values.size());
		assertEquals("FORM:CHECKBOX:onemoreinvoice", set_values.iterator().next());
		assertEquals("<field><name>onemoreinvoice</name><type>checkbox</type><checked>1</checked></field>I want one more invoice", StringUtils.splitToArray(template.getContent(), "\n")[43]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:CHECKBOX:onemoreinvoice", "set : ");
		set_values = builder.generateField(template, new ConstrainedProperty("invoice"), null, "onemore");
		assertEquals(0, set_values.size());
		assertEquals("set : I want one more invoice", StringUtils.splitToArray(template.getContent(), "\n")[43]);
		template.removeValue("FORM:CHECKBOX:onemoreinvoice");
	}

	public void testRemoveFieldCheckbox()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		String raw_content = template.getContent();

		builder.generateField(template, new ConstrainedProperty("options").inList(null, "1", null, "3", "2"), new String[] {"a4"}, null);
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "options", null);
		assertEquals(raw_content, template.getContent());

		builder.generateField(template, CheckboxInListEnum.class, "options", new String[] {"a4"}, null);
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "options", null);
		assertEquals(raw_content, template.getContent());
	}

	public void testRemoveFieldCheckboxTemplateName()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields_templatename");

		String raw_content = template.getContent();

		builder.replaceField(template, "templatenameoptions", new ConstrainedProperty("options").inList(null, "1", null, "3", "2"), new String[] {"a4"}, null);
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "templatenameoptions");
		assertEquals(raw_content, template.getContent());

		builder.replaceField(template, "templatenameoptions", CheckboxInListEnum.class, "options", new String[] {"a4"}, null);
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "templatenameoptions");
		assertEquals(raw_content, template.getContent());
	}

	public void testRemoveFieldCheckboxPrefix()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		String raw_content = template.getContent();

		builder.generateField(template, new ConstrainedProperty("options").inList(null, "1", null, "3", "2"), new String[] {"a4"}, "other");
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "options", "other");
		assertEquals(raw_content, template.getContent());

		builder.generateField(template, CheckboxInListEnum.class, "options", new String[] {"a4"}, "other");
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "options", "other");
		assertEquals(raw_content, template.getContent());
	}

	public void testGenerateFieldSelectWithDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;
		Iterator<String>	set_values_it;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, "colors", null, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><options></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value></value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("colors"), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><options></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value></value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, RadioInListEnum.class, new ConstrainedProperty("colors").inList("black", "red", null, "green", "blue"), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label></option><option><value>red</value><label>red spots</label></option><option><value>green</value><label>green spots</label></option><option><value>blue</value><label>blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value></value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("colors").inList("black", "red", null, "green", "blue"), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label></option><option><value>red</value><label>red spots</label></option><option><value>green</value><label>green spots</label></option><option><value>blue</value><label>blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value></value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, SelectInListEnum.class, "colors", null, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label></option><option><value>red</value><label>red spots</label></option><option><value>green</value><label>green spots</label></option><option><value>blue</value><label>blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value></value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("colors").inList("black", "red", null, "green", "blue").defaultValue("green"), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label></option><option><value>red</value><label>red spots</label></option><option><value>green</value><label>green spots</label><selected>1</selected></option><option><value>blue</value><label>blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>green spots</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, SelectInListEnum.class, new ConstrainedProperty("colors").defaultValue("green"), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label></option><option><value>red</value><label>red spots</label></option><option><value>green</value><label>green spots</label><selected>1</selected></option><option><value>blue</value><label>blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>green spots</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("colors").inList("black", "red", null, "green", "blue").defaultValue("green"), new String[] {null, "red"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label></option><option><value>red</value><label>red spots</label><selected>1</selected></option><option><value>green</value><label>green spots</label></option><option><value>blue</value><label>blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>green spots</value></field><field><value>red spots</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, SelectInListEnum.class, new ConstrainedProperty("colors").defaultValue("green"), new String[] {null, "red"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label></option><option><value>red</value><label>red spots</label><selected>1</selected></option><option><value>green</value><label>green spots</label></option><option><value>blue</value><label>blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>green spots</value></field><field><value>red spots</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("colors").inList("black", "red", null, "green", "blue"), new String[] {"red", "blue"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label></option><option><value>red</value><label>red spots</label><selected>1</selected></option><option><value>green</value><label>green spots</label></option><option><value>blue</value><label>blue spots</label><selected>1</selected></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>red spots</value></field><field><value>blue spots</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, SelectInListEnum.class, "colors", new String[] {"red", "blue"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label></option><option><value>red</value><label>red spots</label><selected>1</selected></option><option><value>green</value><label>green spots</label></option><option><value>blue</value><label>blue spots</label><selected>1</selected></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>red spots</value></field><field><value>blue spots</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("colors").inList("black", "red", null, "green", "blue").defaultValue("green"), new String[] {"black", "red"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label><selected>1</selected></option><option><value>red</value><label>red spots</label><selected>1</selected></option><option><value>green</value><label>green spots</label></option><option><value>blue</value><label>blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>black</value></field><field><value>red spots</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, SelectInListEnum.class, new ConstrainedProperty("colors").defaultValue("green"), new String[] {"black", "red"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label><selected>1</selected></option><option><value>red</value><label>red spots</label><selected>1</selected></option><option><value>green</value><label>green spots</label></option><option><value>blue</value><label>blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>black</value></field><field><value>red spots</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("colors").inList("black", "red", null, "green", "blue"), new String[] {"orange"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label></option><option><value>red</value><label>red spots</label></option><option><value>green</value><label>green spots</label></option><option><value>blue</value><label>blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>orange</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, SelectInListEnum.class, "colors", new String[] {"orange"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label></option><option><value>red</value><label>red spots</label></option><option><value>green</value><label>green spots</label></option><option><value>blue</value><label>blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>orange</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		template.setValue("FORM:SELECT:colors", "already set");
		template.setValue("FORM:DISPLAY:colors", "already set too");
		set_values = builder.generateField(template, new ConstrainedProperty("colors"), null, null);
		assertEquals(0, set_values.size());
		assertEquals("already set", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("already set too", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		template.removeValue("FORM:SELECT:colors");
		template.removeValue("FORM:DISPLAY:colors");
	}

	public void testGenerateFieldSelectWithoutDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, "morecolors", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:SELECT:morecolors", set_values.iterator().next());
		assertEquals("<field><name>morecolors</name><type>select</type><options></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[48]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("morecolors"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:SELECT:morecolors", set_values.iterator().next());
		assertEquals("<field><name>morecolors</name><type>select</type><options></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[48]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("morecolors").inList("black", "red", null, "green", "blue"), null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:SELECT:morecolors", set_values.iterator().next());
		assertEquals("<field><name>morecolors</name><type>select</type><options><option><value>black</value><label>black</label></option><option><value>red</value><label>more red spots</label></option><option><value>green</value><label>more green spots</label></option><option><value>blue</value><label>more blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[48]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, SelectInListEnum.class, "morecolors", null, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:SELECT:morecolors", set_values.iterator().next());
		assertEquals("<field><name>morecolors</name><type>select</type><options><option><value>black</value><label>black</label></option><option><value>red</value><label>more red spots</label></option><option><value>green</value><label>more green spots</label></option><option><value>blue</value><label>more blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[48]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("morecolors").inList("black", "red", null, "green", "blue").defaultValue("green"), new String[] {"black", "red"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:SELECT:morecolors", set_values.iterator().next());
		assertEquals("<field><name>morecolors</name><type>select</type><options><option><value>black</value><label>black</label><selected>1</selected></option><option><value>red</value><label>more red spots</label><selected>1</selected></option><option><value>green</value><label>more green spots</label></option><option><value>blue</value><label>more blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[48]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, SelectInListEnum.class, new ConstrainedProperty("morecolors").defaultValue("green"), new String[] {"black", "red"}, null);
		assertEquals(1, set_values.size());
		assertEquals("FORM:SELECT:morecolors", set_values.iterator().next());
		assertEquals("<field><name>morecolors</name><type>select</type><options><option><value>black</value><label>black</label><selected>1</selected></option><option><value>red</value><label>more red spots</label><selected>1</selected></option><option><value>green</value><label>more green spots</label></option><option><value>blue</value><label>more blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[48]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:SELECT:morecolors", "already set");
		set_values = builder.generateField(template, new ConstrainedProperty("morecolors"), null, null);
		assertEquals(0, set_values.size());
		assertEquals("already set", StringUtils.splitToArray(template.getContent(), "\n")[48]);
		template.removeValue("FORM:SELECT:morecolors");
	}

	public void testGenerateFieldSelectTemplateNameWithDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;
		Iterator<String>	set_values_it;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields_templatename");

		set_values = builder.replaceField(template, "templatenamecolors", "colors", null, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:templatenamecolors", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamecolors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><options></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value></value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamecolors", new ConstrainedProperty("colors"), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:templatenamecolors", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamecolors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><options></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value></value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamecolors", new ConstrainedProperty("colors").inList("black", "red", null, "green", "blue"), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:templatenamecolors", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamecolors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label></option><option><value>red</value><label>red spots</label></option><option><value>green</value><label>green spots</label></option><option><value>blue</value><label>blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value></value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamecolors", SelectInListEnum.class, "colors", null, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:templatenamecolors", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamecolors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label></option><option><value>red</value><label>red spots</label></option><option><value>green</value><label>green spots</label></option><option><value>blue</value><label>blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value></value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamecolors", new ConstrainedProperty("colors").inList("black", "red", null, "green", "blue").defaultValue("green"), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:templatenamecolors", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamecolors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label></option><option><value>red</value><label>red spots</label></option><option><value>green</value><label>green spots</label><selected>1</selected></option><option><value>blue</value><label>blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>green spots</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamecolors", SelectInListEnum.class, new ConstrainedProperty("colors").defaultValue("green"), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:templatenamecolors", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamecolors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label></option><option><value>red</value><label>red spots</label></option><option><value>green</value><label>green spots</label><selected>1</selected></option><option><value>blue</value><label>blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>green spots</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamecolors", new ConstrainedProperty("colors").inList("black", "red", null, "green", "blue").defaultValue("green"), new String[] {null, "red"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:templatenamecolors", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamecolors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label></option><option><value>red</value><label>red spots</label><selected>1</selected></option><option><value>green</value><label>green spots</label></option><option><value>blue</value><label>blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>green spots</value></field><field><value>red spots</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamecolors", SelectInListEnum.class, new ConstrainedProperty("colors").defaultValue("green"), new String[] {null, "red"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:templatenamecolors", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamecolors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label></option><option><value>red</value><label>red spots</label><selected>1</selected></option><option><value>green</value><label>green spots</label></option><option><value>blue</value><label>blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>green spots</value></field><field><value>red spots</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamecolors", new ConstrainedProperty("colors").inList("black", "red", null, "green", "blue"), new String[] {"red", "blue"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:templatenamecolors", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamecolors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label></option><option><value>red</value><label>red spots</label><selected>1</selected></option><option><value>green</value><label>green spots</label></option><option><value>blue</value><label>blue spots</label><selected>1</selected></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>red spots</value></field><field><value>blue spots</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamecolors", SelectInListEnum.class, "colors", new String[] {"red", "blue"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:templatenamecolors", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamecolors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label></option><option><value>red</value><label>red spots</label><selected>1</selected></option><option><value>green</value><label>green spots</label></option><option><value>blue</value><label>blue spots</label><selected>1</selected></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>red spots</value></field><field><value>blue spots</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamecolors", new ConstrainedProperty("colors").inList("black", "red", null, "green", "blue").defaultValue("green"), new String[] {"black", "red"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:templatenamecolors", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamecolors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label><selected>1</selected></option><option><value>red</value><label>red spots</label><selected>1</selected></option><option><value>green</value><label>green spots</label></option><option><value>blue</value><label>blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>black</value></field><field><value>red spots</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamecolors", SelectInListEnum.class, new ConstrainedProperty("colors").defaultValue("green"), new String[] {"black", "red"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:templatenamecolors", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamecolors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label><selected>1</selected></option><option><value>red</value><label>red spots</label><selected>1</selected></option><option><value>green</value><label>green spots</label></option><option><value>blue</value><label>blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>black</value></field><field><value>red spots</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamecolors", new ConstrainedProperty("colors").inList("black", "red", null, "green", "blue"), new String[] {"orange"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:templatenamecolors", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamecolors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label></option><option><value>red</value><label>red spots</label></option><option><value>green</value><label>green spots</label></option><option><value>blue</value><label>blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>orange</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.replaceField(template, "templatenamecolors", SelectInListEnum.class, "colors", new String[] {"orange"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:templatenamecolors", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamecolors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label></option><option><value>red</value><label>red spots</label></option><option><value>green</value><label>green spots</label></option><option><value>blue</value><label>blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>orange</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		template.setValue("FORM:SELECT:templatenamecolors", "already set");
		template.setValue("FORM:DISPLAY:templatenamecolors", "already set too");
		set_values = builder.replaceField(template, "templatenamecolors", new ConstrainedProperty("colors"), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2, set_values.size());
		assertEquals("FORM:SELECT:templatenamecolors", set_values_it.next());
		assertEquals("FORM:DISPLAY:templatenamecolors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><options></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value></value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();
	}

	public void testGenerateFieldSelectWithOutOfListDefault()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;
		Iterator<String>	set_values_it;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, new ConstrainedProperty("colors").inList("black", "red", null, "blue").defaultValue("green"), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>green</value><label>green spots</label><selected>1</selected></option><option><value>black</value><label>black</label></option><option><value>red</value><label>red spots</label></option><option><value>blue</value><label>blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>green spots</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, SelectInListEnum2.class, new ConstrainedProperty("colors").defaultValue("green"), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>green</value><label>green spots</label><selected>1</selected></option><option><value>black</value><label>black</label></option><option><value>red</value><label>red spots</label></option><option><value>blue</value><label>blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>green spots</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("colors").inList("black", "red", null, "blue").defaultValue("green"), new String[] {null, "red"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>green</value><label>green spots</label></option><option><value>black</value><label>black</label></option><option><value>red</value><label>red spots</label><selected>1</selected></option><option><value>blue</value><label>blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>green spots</value></field><field><value>red spots</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, SelectInListEnum2.class, new ConstrainedProperty("colors").defaultValue("green"), new String[] {null, "red"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>green</value><label>green spots</label></option><option><value>black</value><label>black</label></option><option><value>red</value><label>red spots</label><selected>1</selected></option><option><value>blue</value><label>blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>green spots</value></field><field><value>red spots</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("colors").inList("black", "red", null, "blue"), new String[] {"red", "blue"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label></option><option><value>red</value><label>red spots</label><selected>1</selected></option><option><value>blue</value><label>blue spots</label><selected>1</selected></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>red spots</value></field><field><value>blue spots</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, SelectInListEnum2.class, "colors", new String[] {"red", "blue"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label></option><option><value>red</value><label>red spots</label><selected>1</selected></option><option><value>blue</value><label>blue spots</label><selected>1</selected></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>red spots</value></field><field><value>blue spots</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("colors").inList("black", "red", null, "blue").defaultValue("green"), new String[] {"black", "red"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>green</value><label>green spots</label></option><option><value>black</value><label>black</label><selected>1</selected></option><option><value>red</value><label>red spots</label><selected>1</selected></option><option><value>blue</value><label>blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>black</value></field><field><value>red spots</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, SelectInListEnum2.class, new ConstrainedProperty("colors").defaultValue("green"), new String[] {"black", "red"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>green</value><label>green spots</label></option><option><value>black</value><label>black</label><selected>1</selected></option><option><value>red</value><label>red spots</label><selected>1</selected></option><option><value>blue</value><label>blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>black</value></field><field><value>red spots</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("colors").inList("black", "red", null, "blue"), new String[] {"orange"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label></option><option><value>red</value><label>red spots</label></option><option><value>blue</value><label>blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>orange</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, SelectInListEnum2.class, "colors", new String[] {"orange"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label></option><option><value>red</value><label>red spots</label></option><option><value>blue</value><label>blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>orange</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();
	}

	public void testGenerateFieldSelectPrefix()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		set_values = builder.generateField(template, "colors", null, "more");
		assertEquals(1, set_values.size());
		assertEquals("FORM:SELECT:morecolors", set_values.iterator().next());
		assertEquals("<field><name>morecolors</name><type>select</type><options></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[48]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("colors"), null, "more");
		assertEquals(1, set_values.size());
		assertEquals("FORM:SELECT:morecolors", set_values.iterator().next());
		assertEquals("<field><name>morecolors</name><type>select</type><options></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[48]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("colors").inList("black", "red", null, "green", "blue"), null, "more");
		assertEquals(1, set_values.size());
		assertEquals("FORM:SELECT:morecolors", set_values.iterator().next());
		assertEquals("<field><name>morecolors</name><type>select</type><options><option><value>black</value><label>black</label></option><option><value>red</value><label>more red spots</label></option><option><value>green</value><label>more green spots</label></option><option><value>blue</value><label>more blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[48]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, SelectInListEnum.class, "colors", null, "more");
		assertEquals(1, set_values.size());
		assertEquals("FORM:SELECT:morecolors", set_values.iterator().next());
		assertEquals("<field><name>morecolors</name><type>select</type><options><option><value>black</value><label>black</label></option><option><value>red</value><label>more red spots</label></option><option><value>green</value><label>more green spots</label></option><option><value>blue</value><label>more blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[48]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("colors").inList("black", "red", null, "green", "blue").defaultValue("green"), new String[] {"black", "red"}, "more");
		assertEquals(1, set_values.size());
		assertEquals("FORM:SELECT:morecolors", set_values.iterator().next());
		assertEquals("<field><name>morecolors</name><type>select</type><options><option><value>black</value><label>black</label><selected>1</selected></option><option><value>red</value><label>more red spots</label><selected>1</selected></option><option><value>green</value><label>more green spots</label></option><option><value>blue</value><label>more blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[48]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		set_values = builder.generateField(template, SelectInListEnum.class, new ConstrainedProperty("colors").defaultValue("green"), new String[] {"black", "red"}, "more");
		assertEquals(1, set_values.size());
		assertEquals("FORM:SELECT:morecolors", set_values.iterator().next());
		assertEquals("<field><name>morecolors</name><type>select</type><options><option><value>black</value><label>black</label><selected>1</selected></option><option><value>red</value><label>more red spots</label><selected>1</selected></option><option><value>green</value><label>more green spots</label></option><option><value>blue</value><label>more blue spots</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[48]);
		template.removeValue(set_values.iterator().next());
		set_values.clear();

		template.setValue("FORM:SELECT:morecolors", "already set");
		set_values = builder.generateField(template, new ConstrainedProperty("colors"), null, "more");
		assertEquals(0, set_values.size());
		assertEquals("already set", StringUtils.splitToArray(template.getContent(), "\n")[48]);
		template.removeValue("FORM:SELECT:morecolors");
	}

	public void testGenerateFieldSelectDynamic()
	{
		FormBuilderXml	builder = new FormBuilderXml();
		Collection<String>	set_values;
		Iterator<String>	set_values_it;

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		template.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"colors:1", "one"},
					{"colors:3", "three"},
					{"colors:5", "five"},
					{"colors:9", "nine"},
				};
			}});

		set_values = builder.generateField(template, new ConstrainedProperty("colors").inList("1", "3", "5", "9").defaultValue("5"), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>1</value><label>one</label></option><option><value>3</value><label>three</label></option><option><value>5</value><label>five</label><selected>1</selected></option><option><value>9</value><label>nine</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>five</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, SelectInListEnum3.class, new ConstrainedProperty("colors").defaultValue("5"), null, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>1</value><label>one</label></option><option><value>3</value><label>three</label></option><option><value>5</value><label>five</label><selected>1</selected></option><option><value>9</value><label>nine</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>five</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, new ConstrainedProperty("colors").inList("1", "3", "5", "9").defaultValue("5"), new String[] {"3", "9"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>1</value><label>one</label></option><option><value>3</value><label>three</label><selected>1</selected></option><option><value>5</value><label>five</label></option><option><value>9</value><label>nine</label><selected>1</selected></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>three</value></field><field><value>nine</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, SelectInListEnum3.class, new ConstrainedProperty("colors").defaultValue("5"), new String[] {"3", "9"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>1</value><label>one</label></option><option><value>3</value><label>three</label><selected>1</selected></option><option><value>5</value><label>five</label></option><option><value>9</value><label>nine</label><selected>1</selected></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>three</value></field><field><value>nine</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		template.clear();

		template.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"colors:blue", "blue waves"},
					{"colors:red", "red waves"}
				};
			}});

		set_values = builder.generateField(template, new ConstrainedProperty("colors").inList("black", "red", null, "green", "blue").defaultValue("green"), new String[] {null, "red"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label></option><option><value>red</value><label>red waves</label><selected>1</selected></option><option><value>green</value><label>green spots</label></option><option><value>blue</value><label>blue waves</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>green spots</value></field><field><value>red waves</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();

		set_values = builder.generateField(template, SelectInListEnum.class, new ConstrainedProperty("colors").defaultValue("green"), new String[] {null, "red"}, null);
		set_values_it = set_values.iterator();
		assertEquals(2,  set_values.size());
		assertEquals("FORM:SELECT:colors", set_values_it.next());
		assertEquals("FORM:DISPLAY:colors", set_values_it.next());
		assertEquals("<field><name>colors</name><type>select</type><size>3</size><multiple>1</multiple><options><option><value>black</value><label>black</label></option><option><value>red</value><label>red waves</label><selected>1</selected></option><option><value>green</value><label>green spots</label></option><option><value>blue</value><label>blue waves</label></option></options></field>", StringUtils.splitToArray(template.getContent(), "\n")[44]);
		assertEquals("<field><value>green spots</value></field><field><value>red waves</value></field>", StringUtils.splitToArray(template.getContent(), "\n")[58]);
		set_values_it = set_values.iterator();
		template.removeValue(set_values_it.next());
		template.removeValue(set_values_it.next());
		set_values.clear();
	}

	public void testRemoveFieldSelect()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		String raw_content = template.getContent();

		builder.generateField(template, new ConstrainedProperty("colors").inList("black", "red", null, "green", "blue"), new String[] {"orange"}, null);
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "colors", null);
		assertEquals(raw_content, template.getContent());

		builder.generateField(template, SelectInListEnum.class, "colors", new String[] {"orange"}, null);
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "colors", null);
		assertEquals(raw_content, template.getContent());
	}

	public void testRemoveFieldSelectRTemplateName()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields_templatename");

		String raw_content = template.getContent();

		builder.generateField(template, new ConstrainedProperty("templatenamecolors").inList("black", "red", null, "green", "blue"), new String[] {"orange"}, null);
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "templatenamecolors");
		assertEquals(raw_content, template.getContent());

		builder.generateField(template, SelectInListEnum.class, "templatenamecolors", new String[] {"orange"}, null);
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "templatenamecolors");
		assertEquals(raw_content, template.getContent());
	}

	public void testRemoveFieldSelectPrefix()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		String raw_content = template.getContent();

		builder.generateField(template, new ConstrainedProperty("colors").inList("black", "red", null, "green", "blue"), new String[] {"orange"}, "more");
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "colors", "more");
		assertEquals(raw_content, template.getContent());

		builder.generateField(template, SelectInListEnum.class, "colors", new String[] {"orange"}, "more");
		assertFalse(raw_content.equals(template.getContent()));
		builder.removeField(template, "colors", "more");
		assertEquals(raw_content, template.getContent());
	}

	public void testGenerateFormClassInvalidArguments()
	{
		try
		{
			FormBuilderXml builder = new FormBuilderXml();
			assertEquals(0, builder.generateForm(null, null, null, null).size());

			Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");
			String raw_content = template.getContent();
			assertNotNull(template);
			assertEquals(0, builder.generateForm(template, null, null, null).size());
			assertEquals(raw_content, template.getContent());
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGenerateFormBeanInvalidArguments()
	{
		try
		{
			FormBuilderXml builder = new FormBuilderXml();
			assertEquals(0, builder.generateForm(null, (Object)null, null, null).size());
			assertEquals(0, builder.generateForm(null, new Object(), null, null).size());

			Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");
			String raw_content = template.getContent();
			assertNotNull(template);
			assertEquals(0, builder.generateForm(template, (Object)null, null, null).size());
			assertEquals(0, builder.generateForm(template, null, null, null).size());
			assertEquals(raw_content, template.getContent());

			try
			{
				builder.generateForm(template, (Object)Object.class, null, null);
				fail();
			}
			catch (IllegalArgumentException e)
			{
				assertTrue(true);
			}
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGenerateFormBeanNotInstantiatable()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		try
		{
			builder.generateForm(template, PrivateBeanImpl.class, null, null);
			assertEquals(TemplateFactory.ENGINEXML.get("formbuilder_fields_out_regular_empty").getContent(), template.getContent());
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testRemoveFormInvalidArguments()
	{
		try
		{
			FormBuilderXml builder = new FormBuilderXml();
			builder.removeForm(null, null, null);

			Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");
			String raw_content = template.getContent();
			assertNotNull(template);
			builder.removeForm(template, null, null);
			assertEquals(raw_content, template.getContent());
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testRemoveFormBeanNotInstantiatable()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		String raw_content = template.getContent();

		try
		{
			builder.generateForm(template, RegularBeanImpl.class, null, null);
			assertFalse(raw_content.equals(template.getContent()));
			builder.removeForm(template, PrivateBeanImpl.class, null);
			assertEquals(raw_content, template.getContent());
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	private static class PrivateBeanImpl extends RegularBeanImpl
	{
		private PrivateBeanImpl()
		{
		}
	}

	public void testGenerateFormConstrainedEmpty()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		try
		{
			builder.generateForm(template, ConstrainedBeanImpl.class, null, null);
			assertEquals(TemplateFactory.ENGINEXML.get("formbuilder_fields_out_constrained_empty").getContent(), template.getContent());
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testRemoveFormConstrainedEmpty()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		String raw_content = template.getContent();

		try
		{
			builder.generateForm(template, ConstrainedBeanImpl.class, null, null);
			assertFalse(raw_content.equals(template.getContent()));
			builder.removeForm(template, ConstrainedBeanImpl.class, null);
			assertEquals(raw_content, template.getContent());
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGenerateFormConstrainedExternalValues()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		HashMap<String, String[]> values = new HashMap<String, String[]>();
		values.put("hidden", new String[] {"canyouseeme"});
		values.put("anotherhidden", new String[] {"I can't see you"});
		values.put("login", new String[] {"ikke"});
		values.put("anotherlogin", new String[] {"jullie"});
		values.put("password", new String[] {"secret"});
		values.put("anotherpassword", new String[] {"real secret"});
		values.put("comment", new String[] {"één comment"});
		values.put("anothercomment", new String[] {"this comment"});
		values.put("question", new String[] {"a2"});
		values.put("anotherquestion", new String[] {"a3"});
		values.put("customquestion", new String[] {"a1"});
		values.put("anothercustomquestion", new String[] {"a2"});
		values.put("options", new String[] {"2"});
		values.put("otheroptions", new String[] {"2", "0"});
		values.put("customoptions", new String[] {"1"});
		values.put("othercustomoptions", new String[] {"2"});
		values.put("invoice", new String[] {"1"});
		values.put("onemoreinvoice", new String[] {"0"});
		values.put("colors", new String[] {"red", "green"});
		values.put("morecolors", new String[] {"black"});
		values.put("yourcolors", new String[] {"brown"});
		try
		{
			builder.generateForm(template, ConstrainedBeanImpl.class, values, null);
			assertEquals(TemplateFactory.ENGINEXML.get("formbuilder_fields_out_constrained_values").getContent(), template.getContent());
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGenerateFormConstrainedBeanValues()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.setHidden("canyouseeme");
		bean.setAnotherhidden("I can't see you");
		bean.setLogin("ikke");
		bean.setAnotherlogin("jullie");
		bean.setPassword("secret");
		bean.setAnotherpassword("real secret");
		bean.setComment("één comment");
		bean.setAnothercomment("this comment");
		bean.setQuestion(ConstrainedBeanImpl.Question.a2);
		bean.setAnotherquestion("a3");
		bean.setCustomquestion("a1");
		bean.setAnothercustomquestion("a2");
		bean.setOptions(new int[] {2});
		bean.setOtheroptions(new int[] {2, 0});
		bean.setCustomoptions(new int[] {1});
		bean.setOthercustomoptions(new int[] {2});
		bean.setInvoice(true);
		bean.setOnemoreinvoice(false);
		bean.setColors(new String[] {"red", "green"});
		bean.setMorecolors(new String[] {"black"});
		bean.setYourcolors(new String[] {"brown"});
		try
		{
			builder.generateForm(template, bean, null, null);
			assertEquals(TemplateFactory.ENGINEXML.get("formbuilder_fields_out_constrained_values").getContent(), template.getContent());
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGenerateFormConstrainedBeanValuesInvalid()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();

		bean.addValidationError(new ValidationError.INCOMPLETE("that"));
		Set<ValidationError> errors = bean.getValidationErrors();

		bean.setLogin("1234567");
		bean.setAnotherlogin(null);
		bean.setPassword(null);
		bean.setAnotherpassword("123456789abcd");
		bean.setComment(null);
		bean.setAnothercomment(null);
		bean.setQuestion(null);
		bean.setAnotherquestion("a5");
		bean.setCustomquestion(null);
		bean.setAnothercustomquestion("a6");
		bean.setOptions(new int[] {1});
		bean.setOtheroptions(null);
		bean.setCustomoptions(new int[] {1, 0});
		bean.setOthercustomoptions(new int[] {4});
		bean.setInvoice(false);
		bean.setOnemoreinvoice(true);
		bean.setColors(new String[] {"red", "green", "black"});
		bean.setMorecolors(null);
		bean.setYourcolors(new String[] {"white"});
		try
		{
			builder.generateForm(template, bean, null, null);
			assertEquals(TemplateFactory.ENGINEXML.get("formbuilder_fields_out_constrained_invalid").getContent(), template.getContent());
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		assertSame(errors, bean.getValidationErrors());
	}

	public void testGenerateFormConstrainedEmptyPrefix()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_form_prefix");
		try
		{
			builder.generateForm(template, ConstrainedBeanImpl.class, null, "prefix_");
			assertEquals(TemplateFactory.ENGINEXML.get("formbuilder_form_prefix_out_constrained_empty").getContent(), template.getContent());
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGenerateFormConstrainedExternalValuesPrefix()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_form_prefix");

		HashMap<String, String[]> values = new HashMap<String, String[]>();
		values.put("prefix_hidden", new String[] {"canyouseeme"});
		values.put("prefix_login", new String[] {"ikke"});
		values.put("prefix_password", new String[] {"secret"});
		values.put("prefix_comment", new String[] {"één comment"});
		values.put("prefix_question", new String[] {"a2"});
		values.put("prefix_options", new String[] {"2"});
		values.put("prefix_invoice", new String[] {"1"});
		values.put("prefix_colors", new String[] {"red", "green"});
		values.put("prefix_yourcolors", new String[] {"brown", "orange"});
		try
		{
			builder.generateForm(template, ConstrainedBeanImpl.class, values, "prefix_");
			assertEquals(TemplateFactory.ENGINEXML.get("formbuilder_form_prefix_out_constrained_values").getContent(), template.getContent());
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGenerateFormConstrainedBeanValuesPrefix()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_form_prefix");

		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.setHidden("canyouseeme");
		bean.setLogin("ikke");
		bean.setPassword("secret");
		bean.setComment("één comment");
		bean.setQuestion(ConstrainedBeanImpl.Question.a2);
		bean.setOptions(new int[] {2});
		bean.setInvoice(true);
		bean.setColors(new String[] {"red", "green"});
		bean.setYourcolors(new String[] {"orange", "brown"});
		try
		{
			builder.generateForm(template, bean, null, "prefix_");
			assertEquals(TemplateFactory.ENGINEXML.get("formbuilder_form_prefix_out_constrained_values").getContent(), template.getContent());
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testRemoveFormConstrainedBeanValuesPrefix()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_form_prefix");

		String raw_content = template.getContent();

		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.setHidden("canyouseeme");
		bean.setLogin("ikke");
		bean.setPassword("secret");
		bean.setComment("één comment");
		bean.setQuestion(ConstrainedBeanImpl.Question.a2);
		bean.setOptions(new int[] {2});
		bean.setInvoice(true);
		bean.setColors(new String[] {"red", "green"});
		bean.setYourcolors(new String[] {"orange", "brown"});
		try
		{
			builder.generateForm(template, bean, null, "prefix_");
			assertEquals(TemplateFactory.ENGINEXML.get("formbuilder_form_prefix_out_constrained_values").getContent(), template.getContent());
			assertFalse(raw_content.equals(template.getContent()));
			builder.removeForm(template, RegularBeanImpl.class, "prefix_");
			assertEquals(raw_content, template.getContent());
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testRemoveFormConstrainedValues()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		String raw_content = template.getContent();

		HashMap<String, String[]> values = new HashMap<String, String[]>();
		values.put("hidden", new String[] {"canyouseeme"});
		values.put("anotherhidden", new String[] {"I can't see you"});
		values.put("login", new String[] {"ikke"});
		values.put("anotherlogin", new String[] {"jullie"});
		values.put("password", new String[] {"secret"});
		values.put("anotherpassword", new String[] {"real secret"});
		values.put("comment", new String[] {"één comment"});
		values.put("anothercomment", new String[] {"this comment"});
		values.put("question", new String[] {"a2"});
		values.put("anotherquestion", new String[] {"a3"});
		values.put("customquestion", new String[] {"a1"});
		values.put("anothercustomquestion", new String[] {"a2"});
		values.put("options", new String[] {"2"});
		values.put("otheroptions", new String[] {"2", "0"});
		values.put("customoptions", new String[] {"1"});
		values.put("othercustomoptions", new String[] {"2"});
		values.put("invoice", new String[] {"1"});
		values.put("onemoreinvoice", new String[] {"0"});
		values.put("colors", new String[] {"red", "green"});
		values.put("morecolors", new String[] {"black"});
		try
		{
			builder.generateForm(template, ConstrainedBeanImpl.class, values, null);
			assertFalse(raw_content.equals(template.getContent()));
			builder.removeForm(template, ConstrainedBeanImpl.class, null);
			assertEquals(raw_content, template.getContent());
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGenerateFormRegularEmpty()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		try
		{
			builder.generateForm(template, RegularBeanImpl.class, null, null);
			assertEquals(TemplateFactory.ENGINEXML.get("formbuilder_fields_out_regular_empty").getContent(), template.getContent());
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testRemoveFormRegularEmpty()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		String raw_content = template.getContent();

		try
		{
			builder.generateForm(template, RegularBeanImpl.class, null, null);
			assertFalse(raw_content.equals(template.getContent()));
			builder.removeForm(template, RegularBeanImpl.class, null);
			assertEquals(raw_content, template.getContent());
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGenerateFormRegularExternalValues()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		HashMap<String, String[]> values = new HashMap<String, String[]>();
		values.put("hidden", new String[] {"canyouseeme"});
		values.put("anotherhidden", new String[] {"I can't see you"});
		values.put("login", new String[] {"ikke"});
		values.put("anotherlogin", new String[] {"jullie"});
		values.put("password", new String[] {"secret"});
		values.put("anotherpassword", new String[] {"real secret"});
		values.put("comment", new String[] {"één comment"});
		values.put("anothercomment", new String[] {"this comment"});
		values.put("question", new String[] {"a2"});
		values.put("anotherquestion", new String[] {"a3"});
		values.put("customquestion", new String[] {"a1"});
		values.put("anothercustomquestion", new String[] {"a2"});
		values.put("options", new String[] {"2"});
		values.put("otheroptions", new String[] {"2", "0"});
		values.put("customoptions", new String[] {"1"});
		values.put("othercustomoptions", new String[] {"2"});
		values.put("invoice", new String[] {"1"});
		values.put("onemoreinvoice", new String[] {"0"});
		values.put("colors", new String[] {"red", "green"});
		values.put("morecolors", new String[] {"black"});
		values.put("yourcolors", new String[] {"brown"});
		try
		{
			builder.generateForm(template, RegularBeanImpl.class, values, null);
			assertEquals(TemplateFactory.ENGINEXML.get("formbuilder_fields_out_regular_values").getContent(), template.getContent());
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGenerateFormRegularBeanValues()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		RegularBeanImpl bean = new RegularBeanImpl();
		bean.setHidden("canyouseeme");
		bean.setAnotherhidden("I can't see you");
		bean.setLogin("ikke");
		bean.setAnotherlogin("jullie");
		bean.setPassword("secret");
		bean.setAnotherpassword("real secret");
		bean.setComment("één comment");
		bean.setAnothercomment("this comment");
		bean.setQuestion("a2");
		bean.setAnotherquestion("a3");
		bean.setCustomquestion("a1");
		bean.setAnothercustomquestion("a2");
		bean.setOptions(new int[] {2});
		bean.setOtheroptions(new int[] {2, 0});
		bean.setCustomoptions(new int[] {1});
		bean.setOthercustomoptions(new int[] {2});
		bean.setInvoice(true);
		bean.setOnemoreinvoice(false);
		bean.setColors(new RegularBeanImpl.Color[] {RegularBeanImpl.Color.red, RegularBeanImpl.Color.green});
		bean.setMorecolors(new String[] {"black"});
		bean.setYourcolors(new String[] {"brown"});
		try
		{
			builder.generateForm(template, bean, null, null);
			assertEquals(TemplateFactory.ENGINEXML.get("formbuilder_fields_out_regular_values").getContent(), template.getContent());
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGenerateFormRegularEmptyPrefix()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_form_prefix");
		try
		{
			builder.generateForm(template, RegularBeanImpl.class, null, "prefix_");
			assertEquals(TemplateFactory.ENGINEXML.get("formbuilder_form_prefix_out_regular_empty").getContent(), template.getContent());
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGenerateFormRegularExternalValuesPrefix()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_form_prefix");

		HashMap<String, String[]> values = new HashMap<String, String[]>();
		values.put("prefix_hidden", new String[] {"canyouseeme"});
		values.put("prefix_login", new String[] {"ikke"});
		values.put("prefix_password", new String[] {"secret"});
		values.put("prefix_comment", new String[] {"één comment"});
		values.put("prefix_question", new String[] {"a2"});
		values.put("prefix_options", new String[] {"2"});
		values.put("prefix_invoice", new String[] {"1"});
		values.put("prefix_colors", new String[] {"red", "green"});
		values.put("prefix_yourcolors", new String[] {"brown"});
		try
		{
			builder.generateForm(template, RegularBeanImpl.class, values, "prefix_");
			assertEquals(TemplateFactory.ENGINEXML.get("formbuilder_form_prefix_out_regular_values").getContent(), template.getContent());
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGenerateFormRegularBeanValuesPrefix()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_form_prefix");

		RegularBeanImpl bean = new RegularBeanImpl();
		bean.setHidden("canyouseeme");
		bean.setLogin("ikke");
		bean.setPassword("secret");
		bean.setComment("één comment");
		bean.setQuestion("a2");
		bean.setOptions(new int[] {2});
		bean.setInvoice(true);
		bean.setColors(new RegularBeanImpl.Color[] {RegularBeanImpl.Color.red, RegularBeanImpl.Color.green});
		bean.setYourcolors(new String[] {"brown"});
		try
		{
			builder.generateForm(template, bean, null, "prefix_");
			assertEquals(TemplateFactory.ENGINEXML.get("formbuilder_form_prefix_out_regular_values").getContent(), template.getContent());
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testRemoveFormRegularBeanValuesPrefix()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_form_prefix");

		String raw_content = template.getContent();

		RegularBeanImpl bean = new RegularBeanImpl();
		bean.setHidden("canyouseeme");
		bean.setLogin("ikke");
		bean.setPassword("secret");
		bean.setComment("één comment");
		bean.setQuestion("a2");
		bean.setOptions(new int[] {2});
		bean.setInvoice(true);
		bean.setColors(new RegularBeanImpl.Color[] {RegularBeanImpl.Color.red, RegularBeanImpl.Color.green});
		bean.setYourcolors(new String[] {"brown"});
		try
		{
			builder.generateForm(template, bean, null, "prefix_");
			assertEquals(TemplateFactory.ENGINEXML.get("formbuilder_form_prefix_out_regular_values").getContent(), template.getContent());
			assertFalse(raw_content.equals(template.getContent()));
			builder.removeForm(template, RegularBeanImpl.class, "prefix_");
			assertEquals(raw_content, template.getContent());
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testRemoveFormRegularValues()
	{
		FormBuilderXml	builder = new FormBuilderXml();

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_fields");

		String raw_content = template.getContent();

		HashMap<String, String[]> values = new HashMap<String, String[]>();
		values.put("hidden", new String[] {"canyouseeme"});
		values.put("anotherhidden", new String[] {"I can't see you"});
		values.put("login", new String[] {"ikke"});
		values.put("anotherlogin", new String[] {"jullie"});
		values.put("password", new String[] {"secret"});
		values.put("anotherpassword", new String[] {"real secret"});
		values.put("comment", new String[] {"één comment"});
		values.put("anothercomment", new String[] {"this comment"});
		values.put("question", new String[] {"a2"});
		values.put("anotherquestion", new String[] {"a3"});
		values.put("customquestion", new String[] {"a1"});
		values.put("anothercustomquestion", new String[] {"a2"});
		values.put("options", new String[] {"2"});
		values.put("otheroptions", new String[] {"2", "0"});
		values.put("customoptions", new String[] {"1"});
		values.put("othercustomoptions", new String[] {"2"});
		values.put("invoice", new String[] {"1"});
		values.put("onemoreinvoice", new String[] {"0"});
		values.put("colors", new String[] {"red", "green"});
		values.put("morecolors", new String[] {"black"});
		try
		{
			builder.generateForm(template, RegularBeanImpl.class, values, null);
			assertFalse(raw_content.equals(template.getContent()));
			builder.removeForm(template, RegularBeanImpl.class, null);
			assertEquals(raw_content, template.getContent());
		}
		catch (BeanUtilsException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testSelectParameterInvalidArguments()
	{
		FormBuilderXml builder = new FormBuilderXml();
		assertEquals(0, builder.selectParameter(null, null, null).size());

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_parameters");
		String raw_content = template.getContent();
		assertNotNull(template);
		assertEquals(0, builder.selectParameter(template, null, null).size());
		assertEquals(raw_content, template.getContent());
		assertEquals(0, builder.selectParameter(template, "", null).size());
		assertEquals(raw_content, template.getContent());
		assertEquals(0, builder.selectParameter(template, "name", null).size());
		assertEquals(raw_content, template.getContent());
		assertEquals(0, builder.selectParameter(template, "name", new String[0]).size());
		assertEquals(raw_content, template.getContent());
	}

	public void testSelectParameterChecked()
	{
		FormBuilderXml builder = new FormBuilderXml();
		Template template = TemplateFactory.ENGINEXML.get("formbuilder_parameters");
		assertEquals(0, builder.selectParameter(template, "wantsupdates", new String[] {"false", null}).size());
		Collection<String> set_values = builder.selectParameter(template, "wantsupdates", new String[] {"true"});
		assertEquals(1, set_values.size());
		assertEquals("wantsupdates:CHECKED", set_values.iterator().next());
		assertEquals("wantsupdates<checked>1</checked>\n" +
					 "orange\n" +
					 "blue\n" +
					 "red\n" +
					 "lastname\n" +
					 "lastname\n", template.getContent());
	}

	public void testSelectParameterCheckedValues()
	{
		FormBuilderXml builder = new FormBuilderXml();
		Template template = TemplateFactory.ENGINEXML.get("formbuilder_parameters");
		assertEquals(0, builder.selectParameter(template, "colors", new String[] {"green"}).size());
		Collection<String> set_values = builder.selectParameter(template, "colors", new String[] {"orange", "red", null, "black"});
		assertEquals(2, set_values.size());
		Iterator<String> it = set_values.iterator();
		assertEquals("colors:orange:CHECKED", it.next());
		assertEquals("colors:red:CHECKED", it.next());
		assertEquals("wantsupdates\n" +
					 "orange<checked>1</checked>\n" +
					 "blue\n" +
					 "red<checked>1</checked>\n" +
					 "lastname\n" +
					 "lastname\n", template.getContent());
	}

	public void testSelectParameterSelectedValues()
	{
		FormBuilderXml builder = new FormBuilderXml();
		Template template = TemplateFactory.ENGINEXML.get("formbuilder_parameters");
		assertEquals(0, builder.selectParameter(template, "lastname", new String[] {"Smith"}).size());
		Collection<String> set_values = builder.selectParameter(template, "lastname", new String[] {"Smith", null, "Mafessoni"});
		assertEquals(1, set_values.size());
		Iterator<String> it = set_values.iterator();
		assertEquals("lastname:Mafessoni:SELECTED", it.next());
		assertEquals("wantsupdates\n" +
					 "orange\n" +
					 "blue\n" +
					 "red\n" +
					 "lastname\n" +
					 "lastname<selected>1</selected>\n", template.getContent());
	}

	public void testUnselectParameterInvalidArguments()
	{
		FormBuilderXml builder = new FormBuilderXml();
		builder.unselectParameter(null, null, null);

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_parameters");
		template.setValue("wantsupdates:CHECKED", "1");
		template.setValue("colors:orange:CHECKED", "1");
		template.setValue("colors:blue:CHECKED", "1");
		template.setValue("colors:red:CHECKED", "1");
		template.setValue("lastname:Bevin:SELECTED", "1");
		template.setValue("lastname:Mafessoni:SELECTED", "1");

		String raw_content = template.getContent();
		assertEquals("wantsupdates1\n"+
			"orange1\n"+
			"blue1\n"+
			"red1\n"+
			"lastname1\n"+
			"lastname1\n", raw_content);

		assertNotNull(template);
		builder.unselectParameter(template, null, null);
		assertEquals(raw_content, template.getContent());
		builder.unselectParameter(template, "", null);
		assertEquals(raw_content, template.getContent());
		builder.unselectParameter(template, "name", null);
		assertEquals(raw_content, template.getContent());
		builder.unselectParameter(template, "name", new String[0]);
		assertEquals(raw_content, template.getContent());
	}

	public void testUnselectParameterChecked()
	{
		FormBuilderXml builder = new FormBuilderXml();
		builder.unselectParameter(null, null, null);

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_parameters");
		template.setValue("wantsupdates:CHECKED", "1");
		template.setValue("colors:orange:CHECKED", "1");
		template.setValue("colors:blue:CHECKED", "1");
		template.setValue("colors:red:CHECKED", "1");
		template.setValue("lastname:Bevin:SELECTED", "1");
		template.setValue("lastname:Mafessoni:SELECTED", "1");

		String raw_content = template.getContent();
		assertEquals("wantsupdates1\n"+
			"orange1\n"+
			"blue1\n"+
			"red1\n"+
			"lastname1\n"+
			"lastname1\n", raw_content);

		builder.unselectParameter(template, "wantsupdates", new String[] {"false", null});
		builder.unselectParameter(template, "wantsupdates", new String[] {"true"});
		assertEquals("wantsupdates\n"+
			"orange1\n"+
			"blue1\n"+
			"red1\n"+
			"lastname1\n"+
			"lastname1\n", template.getContent());
	}

	public void testUnselectParameterCheckedValues()
	{
		FormBuilderXml builder = new FormBuilderXml();
		builder.unselectParameter(null, null, null);

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_parameters");
		template.setValue("wantsupdates:CHECKED", "1");
		template.setValue("colors:orange:CHECKED", "1");
		template.setValue("colors:blue:CHECKED", "1");
		template.setValue("colors:red:CHECKED", "1");
		template.setValue("lastname:Bevin:SELECTED", "1");
		template.setValue("lastname:Mafessoni:SELECTED", "1");

		String raw_content = template.getContent();
		assertEquals("wantsupdates1\n"+
			"orange1\n"+
			"blue1\n"+
			"red1\n"+
			"lastname1\n"+
			"lastname1\n", raw_content);

		builder.unselectParameter(template, "colors", new String[] {"green"});
		builder.unselectParameter(template, "colors", new String[] {"orange", "red", null, "black"});
		assertEquals("wantsupdates1\n"+
			"orange\n"+
			"blue1\n"+
			"red\n"+
			"lastname1\n"+
			"lastname1\n", template.getContent());
	}

	public void testUnselectParameterSelectedValues()
	{
		FormBuilderXml builder = new FormBuilderXml();
		builder.unselectParameter(null, null, null);

		Template template = TemplateFactory.ENGINEXML.get("formbuilder_parameters");
		template.setValue("wantsupdates:CHECKED", "1");
		template.setValue("colors:orange:CHECKED", "1");
		template.setValue("colors:blue:CHECKED", "1");
		template.setValue("colors:red:CHECKED", "1");
		template.setValue("lastname:Bevin:SELECTED", "1");
		template.setValue("lastname:Mafessoni:SELECTED", "1");

		String raw_content = template.getContent();
		assertEquals("wantsupdates1\n"+
			"orange1\n"+
			"blue1\n"+
			"red1\n"+
			"lastname1\n"+
			"lastname1\n", raw_content);

		builder.unselectParameter(template, "lastname", new String[] {"Smith"});
		builder.unselectParameter(template, "lastname", new String[] {"Smith", null, "Mafessoni"});
		assertEquals("wantsupdates1\n"+
			"orange1\n"+
			"blue1\n"+
			"red1\n"+
			"lastname1\n"+
			"lastname\n", template.getContent());
	}
}