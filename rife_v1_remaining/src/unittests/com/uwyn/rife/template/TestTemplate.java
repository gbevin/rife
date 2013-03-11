/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestTemplate.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.template.exceptions.BlockUnknownException;
import com.uwyn.rife.template.exceptions.TemplateException;
import com.uwyn.rife.template.exceptions.ValueUnknownException;
import com.uwyn.rife.tools.ExceptionUtils;
import java.util.ArrayList;
import java.util.Collection;

public class TestTemplate extends TemplateTestCase
{
	public TestTemplate(String name)
	{
		super(name);
	}

	public void setUp()
	{
	}

	public void testInstantiation()
	{
		Template template = TemplateFactory.HTML.get("empty");
		assertNotNull(template);
		assertTrue(template.getModificationTime() <= System.currentTimeMillis());
		assertEquals("", template.getContent());
        assertEquals("empty", template.getName());
        assertEquals("empty", template.getFullName());
		try
		{
			template.getBlock("TEST");
			fail();
		}
		catch (BlockUnknownException e)
		{
			assertEquals("TEST", e.getId());
		}
		try
		{
			template.getValue("TEST");
			fail();
		}
		catch (ValueUnknownException e)
		{
			assertEquals("TEST", e.getId());
		}
		assertEquals(template.countValues(), 0);
		assertEquals(template.getAvailableValueIds().length, 0);
		assertEquals(template.getFilteredValues("empty").size(), 0);
		assertEquals(template.getUnsetValueIds().size(), 0);
	}

	public void testClone()
	{
		Template template1 = TemplateFactory.HTML.get("values");
        assertEquals("values", template1.getName());
        assertEquals("values", template1.getFullName());
		String value1 = "aaab";
		String value2 = "bbbc";
		String value3 = "ccccd";
		try
		{
			template1.setValue("VALUE1", value1);
			template1.setValue("VALUE2", value2);
			template1.setValue("VALUE3", value3);
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		Template template2 = (Template)template1.clone();
		assertNotNull(template2);
		assertNotSame(template1, template2);
		assertEquals(template1.getContent(), template2.getContent());
	}

	public void testSetValues()
	{
		Template template = TemplateFactory.HTML.get("values");
		assertNull(template.getValue("VALUE1"));
		assertNull(template.getValue("VALUE2"));
		assertNull(template.getValue("VALUE3"));
		assertEquals(template.countValues(), 0);

		try
		{
			template.getValue("VALUE4");
			fail();
		}
		catch (ValueUnknownException e)
		{
			assertEquals("VALUE4", e.getId());
		}
		
		String value1 = "aaab";
		String value2 = "bbbc";
		String value3 = "ccccd";
		try
		{
			template.setValue("VALUE1", value1);
			assertEquals(template.countValues(), 1);
			template.setValue("VALUE2", value2);
			assertEquals(template.countValues(), 2);
			template.setValue("VALUE3", value3);
			assertEquals(template.countValues(), 3);
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(template.getValue("VALUE1"), value1);
		assertEquals(template.getValue("VALUE2"), value2);
		assertEquals(template.getValue("VALUE3"), value3);
	}

	public void testSetValuesTyped()
	{
		Template template = TemplateFactory.HTML.get("values_typed");

		String		value_string = "abcde";
		boolean 	value_boolean = false;
		char		value_char = 'k';
		char[]		value_chararray = "abcdefgh".toCharArray();
		double		value_double = 7483.343d;
		float		value_float = 233.45f;
		int			value_int = 34878;
		long		value_long = 938649837L;
		Object		value_object = new Boolean(true);
		Template	value_template = TemplateFactory.HTML.get("values");

		value_template.setValue("VALUE1", "thevalue1");
		value_template.setValue("VALUE3", "thevalue3");
		
		try
		{
			template.setValue("STRING", value_string);
			assertEquals(template.countValues(), 1);
			template.setValue("BOOLEAN", value_boolean);
			assertEquals(template.countValues(), 2);
			template.setValue("CHAR", value_char);
			assertEquals(template.countValues(), 3);
			template.setValue("CHAR[]", value_chararray, 3, 2);
			assertEquals(template.countValues(), 4);
			template.setValue("DOUBLE", value_double);
			assertEquals(template.countValues(), 5);
			template.setValue("FLOAT", value_float);
			assertEquals(template.countValues(), 6);
			template.setValue("INT", value_int);
			assertEquals(template.countValues(), 7);
			template.setValue("LONG", value_long);
			assertEquals(template.countValues(), 8);
			template.setValue("OBJECT", value_object);
			assertEquals(template.countValues(), 9);
			template.setValue("TEMPLATE", value_template);
			assertEquals(template.countValues(), 10);
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(template.getValue("STRING"), value_string);
		assertEquals(template.getValue("BOOLEAN"), ""+value_boolean);
		assertEquals(template.getValue("CHAR"), ""+value_char);
		assertEquals(template.getValue("CHAR[]").toString(), "de");
		assertEquals(template.getValue("DOUBLE"), ""+value_double);
		assertEquals(template.getValue("FLOAT"), ""+value_float);
		assertEquals(template.getValue("INT"), ""+value_int);
		assertEquals(template.getValue("LONG"), ""+value_long);
		assertEquals(template.getValue("OBJECT"), ""+value_object);
		assertEquals(template.getValue("TEMPLATE"), "thevalue1<!--V VALUE2/-->thevalue3\n");
	}

	public void testAppendValuesTyped()
	{
		Template template = TemplateFactory.HTML.get("values_typed");

		String	value_string = "abcde";
		boolean value_boolean = false;
		char	value_char = 'k';
		char[]	value_chararray = "abcdefgh".toCharArray();
		double	value_double = 7483.343d;
		float	value_float = 233.45f;
		int		value_int = 34878;
		long	value_long = 938649837L;
		Object	value_object = new Boolean(true);

		try
		{
			template.appendValue("VALUE", value_string);
			template.appendValue("VALUE", value_boolean);
			template.appendValue("VALUE", value_char);
			template.appendValue("VALUE", value_chararray, 3, 2);
			template.appendValue("VALUE", value_double);
			template.appendValue("VALUE", value_float);
			template.appendValue("VALUE", value_int);
			template.appendValue("VALUE", value_long);
			template.appendValue("VALUE", value_object);
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertEquals(template.getValue("VALUE"),
			value_string+
			value_boolean+
			value_char+
			"de"+
			value_double+
			value_float+
			value_int+
			value_long+
			value_object);
	}

	public void testRemoveValues()
	{
		Template template = TemplateFactory.HTML.get("values");
		String value1 = "aaab";
		String value2 = "bbbc";
		String value3 = "ccccd";
		try
		{
			template.setValue("VALUE1", value1);
			template.setValue("VALUE2", value2);
			template.setValue("VALUE3", value3);
			assertEquals(template.countValues(), 3);
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		template.removeValue("VALUE1");
		assertEquals(template.countValues(), 2);
		template.removeValue("VALUE2");
		assertEquals(template.countValues(), 1);
		template.removeValue("VALUE3");
		assertEquals(template.countValues(), 0);

		try
		{
			template.removeValue("VALUE4");
			fail();
		}
		catch (ValueUnknownException e)
		{
			assertEquals("VALUE4", e.getId());
		}
	}

	public void testBlankValues()
	{
		Template template = TemplateFactory.HTML.get("values");
		String value1 = "aaab";
		String value2 = "bbbc";
		String value3 = "ccccd";
		try
		{
			template.setValue("VALUE1", value1);
			template.setValue("VALUE2", value2);
			template.setValue("VALUE3", value3);
			assertEquals(template.countValues(), 3);
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		assertFalse(template.getValue("VALUE1").equals(""));
		assertFalse(template.getValue("VALUE2").equals(""));
		assertFalse(template.getValue("VALUE3").equals(""));

		template.blankValue("VALUE1");
		template.blankValue("VALUE2");
		template.blankValue("VALUE3");
		assertEquals(template.countValues(), 3);
		
		assertEquals(template.getValue("VALUE1"), "");
		assertEquals(template.getValue("VALUE2"), "");
		assertEquals(template.getValue("VALUE3"), "");

		try
		{
			template.blankValue("VALUE4");
			fail();
		}
		catch (ValueUnknownException e)
		{
			assertEquals("VALUE4", e.getId());
		}
	}
	
	public void testClearValues()
	{
		Template template = TemplateFactory.HTML.get("values");
		String value1 = "aaab";
		String value2 = "bbbc";
		String value3 = "ccccd";
		try
		{
			template.setValue("VALUE1", value1);
			template.setValue("VALUE2", value2);
			template.setValue("VALUE3", value3);
			assertEquals(template.countValues(), 3);
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		template.clear();
		assertEquals(template.countValues(), 0);
	}

	public void testDefaultValues()
	{
		Template template = TemplateFactory.HTML.get("values_default");
		String defaultvalue1 = "azerty";
		assertTrue(template.hasDefaultValue("DEFAULTVALUE"));
		assertEquals(template.getDefaultValue("DEFAULTVALUE"), defaultvalue1);
		assertEquals(template.getValue("DEFAULTVALUE"), defaultvalue1);
		String value1 = "hdijjk";
		try
		{
			template.setValue("DEFAULTVALUE", value1);
		}
		catch (TemplateException e)
		{
            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		assertTrue(template.hasDefaultValue("DEFAULTVALUE"));
		assertEquals(template.getDefaultValue("DEFAULTVALUE"), defaultvalue1);
		assertEquals(template.getValue("DEFAULTVALUE"), value1);
		template.removeValue("DEFAULTVALUE");
		assertTrue(template.hasDefaultValue("DEFAULTVALUE"));
		assertEquals(template.getDefaultValue("DEFAULTVALUE"), defaultvalue1);
		assertEquals(template.getValue("DEFAULTVALUE"), defaultvalue1);
	}
	
	public void testUnsetValues()
	{
		Template template = null;
		Collection<String> unset_valueids = null;
		try
		{
			template = TemplateFactory.HTML.get("values_short_in");

			unset_valueids = template.getUnsetValueIds();
			assertNotNull(unset_valueids);
			assertEquals(unset_valueids.size(), 7);
			assertTrue(unset_valueids.contains("VALUE1"));
			assertTrue(unset_valueids.contains("VALUE2"));
			assertTrue(unset_valueids.contains("VALUE3"));
			assertTrue(unset_valueids.contains("VALUE4"));
			assertTrue(unset_valueids.contains("VALUE5"));
			assertTrue(unset_valueids.contains("VALUE6"));
			assertTrue(unset_valueids.contains("VALUE7"));

			template.setValue("VALUE1", "value");
			unset_valueids = template.getUnsetValueIds();
			assertEquals(unset_valueids.size(), 6);
			assertTrue(!unset_valueids.contains("VALUE1"));
			assertTrue(unset_valueids.contains("VALUE2"));
			assertTrue(unset_valueids.contains("VALUE3"));
			assertTrue(unset_valueids.contains("VALUE4"));
			assertTrue(unset_valueids.contains("VALUE5"));
			assertTrue(unset_valueids.contains("VALUE6"));
			assertTrue(unset_valueids.contains("VALUE7"));

			template.setValue("VALUE2", "value");
			unset_valueids = template.getUnsetValueIds();
			assertEquals(unset_valueids.size(), 5);
			assertTrue(!unset_valueids.contains("VALUE1"));
			assertTrue(!unset_valueids.contains("VALUE2"));
			assertTrue(unset_valueids.contains("VALUE3"));
			assertTrue(unset_valueids.contains("VALUE4"));
			assertTrue(unset_valueids.contains("VALUE5"));
			assertTrue(unset_valueids.contains("VALUE6"));
			assertTrue(unset_valueids.contains("VALUE7"));

			template.setValue("VALUE3", "value");
			unset_valueids = template.getUnsetValueIds();
			assertEquals(unset_valueids.size(), 4);
			assertTrue(!unset_valueids.contains("VALUE1"));
			assertTrue(!unset_valueids.contains("VALUE2"));
			assertTrue(!unset_valueids.contains("VALUE3"));
			assertTrue(unset_valueids.contains("VALUE4"));
			assertTrue(unset_valueids.contains("VALUE5"));
			assertTrue(unset_valueids.contains("VALUE6"));
			assertTrue(unset_valueids.contains("VALUE7"));

			template.setValue("VALUE4", "value");
			unset_valueids = template.getUnsetValueIds();
			assertEquals(unset_valueids.size(), 3);
			assertTrue(!unset_valueids.contains("VALUE1"));
			assertTrue(!unset_valueids.contains("VALUE2"));
			assertTrue(!unset_valueids.contains("VALUE3"));
			assertTrue(!unset_valueids.contains("VALUE4"));
			assertTrue(unset_valueids.contains("VALUE5"));
			assertTrue(unset_valueids.contains("VALUE6"));
			assertTrue(unset_valueids.contains("VALUE7"));

			template.setValue("VALUE5", "value");
			unset_valueids = template.getUnsetValueIds();
			assertEquals(unset_valueids.size(), 2);
			assertTrue(!unset_valueids.contains("VALUE1"));
			assertTrue(!unset_valueids.contains("VALUE2"));
			assertTrue(!unset_valueids.contains("VALUE3"));
			assertTrue(!unset_valueids.contains("VALUE4"));
			assertTrue(!unset_valueids.contains("VALUE5"));
			assertTrue(unset_valueids.contains("VALUE6"));
			assertTrue(unset_valueids.contains("VALUE7"));

			template.setValue("VALUE6", "value");
			unset_valueids = template.getUnsetValueIds();
			assertEquals(unset_valueids.size(), 1);
			assertTrue(!unset_valueids.contains("VALUE1"));
			assertTrue(!unset_valueids.contains("VALUE2"));
			assertTrue(!unset_valueids.contains("VALUE3"));
			assertTrue(!unset_valueids.contains("VALUE4"));
			assertTrue(!unset_valueids.contains("VALUE5"));
			assertTrue(!unset_valueids.contains("VALUE6"));
			assertTrue(unset_valueids.contains("VALUE7"));

			template.setValue("VALUE7", "value");
			unset_valueids = template.getUnsetValueIds();
			assertEquals(unset_valueids.size(), 0);
			assertTrue(!unset_valueids.contains("VALUE1"));
			assertTrue(!unset_valueids.contains("VALUE2"));
			assertTrue(!unset_valueids.contains("VALUE3"));
			assertTrue(!unset_valueids.contains("VALUE4"));
			assertTrue(!unset_valueids.contains("VALUE5"));
			assertTrue(!unset_valueids.contains("VALUE6"));
			assertTrue(!unset_valueids.contains("VALUE7"));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testUnsetValuesDefaults()
	{
		Template template = null;
		Collection<String> unset_valueids = null;
		try
		{
			template = TemplateFactory.HTML.get("values_long_in");

			unset_valueids = template.getUnsetValueIds();
			assertNotNull(unset_valueids);
			assertEquals(unset_valueids.size(), 0);

			template.setValue("VALUE1", "value");
			unset_valueids = template.getUnsetValueIds();
			assertEquals(unset_valueids.size(), 0);

			template.setValue("VALUE2", "value");
			unset_valueids = template.getUnsetValueIds();
			assertEquals(unset_valueids.size(), 0);

			template.setValue("VALUE3", "value");
			unset_valueids = template.getUnsetValueIds();
			assertEquals(unset_valueids.size(), 0);

			template.setValue("VALUE4", "value");
			unset_valueids = template.getUnsetValueIds();
			assertEquals(unset_valueids.size(), 0);

			template.setValue("VALUE5", "value");
			unset_valueids = template.getUnsetValueIds();
			assertEquals(unset_valueids.size(), 0);

			template.setValue("VALUE6", "value");
			unset_valueids = template.getUnsetValueIds();
			assertEquals(unset_valueids.size(), 0);

			template.setValue("VALUE7", "value");
			unset_valueids = template.getUnsetValueIds();
			assertEquals(unset_valueids.size(), 0);
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testConditionalBlockRetrieval()
	{
		Template template = null;
		try
		{
			template = TemplateFactory.TXT.get("construction_simple_in");
			assertTrue(template.hasBlock("BLOCK1"));
			assertFalse(template.hasBlock("BLOCK1b"));
			try
			{
				template.getBlock("BLOCK1b");
				fail();
			}
			catch (BlockUnknownException e)
			{
				assertTrue(true);
			}
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testConstructionSimple()
	{
		Template template = null;
		try
		{
			template = TemplateFactory.TXT.get("construction_simple_in");
			template.appendBlock("CONTENT", "BLOCK1");
			template.appendBlock("CONTENT", "BLOCK3");
			template.appendBlock("CONTENT", "BLOCK2");
			template.appendBlock("CONTENT", "BLOCK4");
			template.appendBlock("CONTENT", "BLOCK1");
			template.setValue("VALUE3", "value 3 early");	// will be overridden
			template.appendBlock("CONTENT", "BLOCK3");
			template.appendBlock("CONTENT", "BLOCK2");
			template.setValue("VALUE4", "value 4 early");	// will be removed
			template.appendBlock("CONTENT", "BLOCK4");
			template.removeValue("VALUE4");
			template.setValue("VALUE1", "value 1 late");	// late setting
			template.setValue("VALUE3", "value 3 late");	// late setting
			assertEquals(template.getContent(), getTemplateContent("construction_simple_out", TemplateFactory.TXT.getParser()));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testConstructionRepeated()
	{
		Template template = null;
		try
		{
			template = TemplateFactory.TXT.get("construction_repeated_in");
			template.setBlock("VALUE2", "BLOCK1");
			template.setBlock("VALUE3", "BLOCK2");
			template.setBlock("VALUE4", "BLOCK3");
			template.setBlock("CONTENT", "BLOCK4");
			template.setValue("VALUE1", "value 1 late");
			template.setValue("VALUE2", "value 2 late");	// has no influence
			template.setValue("VALUE3", "value 3 late");	// has no influence
			template.setValue("VALUE4", "value 4 late");	// has no influence
			assertEquals(template.getContent(), getTemplateContent("construction_repeated_out", TemplateFactory.TXT.getParser()));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testConstructionOverriding()
	{
		Template template = null;
		try
		{
			template = TemplateFactory.TXT.get("construction_overriding_in");

			assertTrue(!template.isValueSet("VALUE2"));
			template.setValue("VALUE2", "value2");
			assertTrue(template.isValueSet("VALUE2"));
			template.setBlock("VALUE2", "BLOCK1");
			assertTrue(template.isValueSet("VALUE2"));
			template.setValue("VALUE1", "value1");
			assertEquals(template.getBlock("BLOCK2"), getTemplateContent("construction_overriding_out_1", TemplateFactory.TXT.getParser()));
			template.clear();

			assertTrue(!template.isValueSet("VALUE2"));
			template.setBlock("VALUE2", "BLOCK1");
			assertTrue(template.isValueSet("VALUE2"));
			template.setValue("VALUE2", "value2");
			assertTrue(template.isValueSet("VALUE2"));
			template.setValue("VALUE1", "value1");
			assertEquals(template.getBlock("BLOCK2"), getTemplateContent("construction_overriding_out_2", TemplateFactory.TXT.getParser()));
			template.clear();

			assertTrue(!template.isValueSet("VALUE2"));
			template.setValue("VALUE2", "value2 ");
			assertTrue(template.isValueSet("VALUE2"));
			template.appendBlock("VALUE2", "BLOCK1");
			assertTrue(template.isValueSet("VALUE2"));
			template.setValue("VALUE1", "value1");
			assertEquals(template.getBlock("BLOCK2"), getTemplateContent("construction_overriding_out_3", TemplateFactory.TXT.getParser()));
			template.clear();

			assertTrue(!template.isValueSet("VALUE2"));
			template.setBlock("VALUE2", "BLOCK1");
			assertTrue(template.isValueSet("VALUE2"));
			template.appendValue("VALUE2", " value2");
			assertTrue(template.isValueSet("VALUE2"));
			template.setValue("VALUE1", "value1");
			assertEquals(template.getBlock("BLOCK2"), getTemplateContent("construction_overriding_out_4", TemplateFactory.TXT.getParser()));
			template.clear();
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testConstructionDefaultValue()
	{
		Template template = null;
		try
		{
			template = TemplateFactory.TXT.get("construction_defaultvalue_in");

			template.setValue("VALUE1", "value1");
			assertEquals(template.getBlock("BLOCK2"), getTemplateContent("construction_defaultvalue_out", TemplateFactory.TXT.getParser()));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testConstructionEmbedded()
	{
		Template template = null;
		try
		{
			template = TemplateFactory.TXT.get("contruction_embedded_in");

			template.setValue("member_value1", 1);
			template.appendBlock("rows", "row_first");
			template.setValue("member_value1", 2);
			template.appendBlock("rows", "row_second");
			template.setValue("member_value1", 3);
			template.appendBlock("rows", "row_first");
			template.setValue("member_value1", 4);
			template.appendBlock("rows", "row_second");
			template.appendBlock("rows", "row_first");
			template.setValue("member_value2", 5);
			assertEquals(template.getContent(), getTemplateContent("contruction_embedded_out", TemplateFactory.TXT.getParser()));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testConstructionInternalValues()
	{
		Template template = null;
		try
		{
			template = TemplateFactory.TXT.get("contruction_internalvalues_in");

			TreeNode tree = new TreeNode(template);
			TreeNode node1 = new TreeNode(tree, "node1");
			TreeNode node2 = new TreeNode(tree, "node2");
			TreeNode node3 = new TreeNode(tree, "node3");
			new TreeNode(node1, "node1a");
			new TreeNode(node1, "node1b");
			new TreeNode(node1, "node1c");
			TreeNode node2a = new TreeNode(node2, "node2a");
			new TreeNode(node2, "node2b");
			new TreeNode(node3, "node3a");
			new TreeNode(node3, "node3b");
			new TreeNode(node2a, "node2a1");
			new TreeNode(node2a, "node2a2");

			tree.output();
			assertEquals(template.getContent(), getTemplateContent("contruction_internalvalues_out", TemplateFactory.TXT.getParser()));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testConstructionInternalBlocksNameHashcodeConflicts()
	{
		try
		{
			assertTrue("DMn0".hashCode() == "Cln0".hashCode());
			assertTrue("DMn0".hashCode() == "DNNO".hashCode());
			assertTrue("FMmO".hashCode() == "EmMn".hashCode());
			assertTrue("DMn0".hashCode() != "FMmO".hashCode());
			assertTrue("DMn0".hashCode() != "HNMn".hashCode());
			assertTrue("FMmO".hashCode() != "HNMn".hashCode());
			Template template = TemplateFactory.HTML.get("blocks_stringconflicts_in");
			InternalValue internal = template.createInternalValue();
			internal.appendBlock("DMn0");
			internal.appendBlock("Cln0");
			internal.appendBlock("DNNO");
			internal.appendBlock("FMmO");
			internal.appendBlock("EmMn");
			internal.appendBlock("HNMn");
			template.setValue("result", internal);
			assertEquals(template.getValue("result"),
						"1 : the first block"+
						"1 : the second block"+
						"1 : the third block"+
						"2 : the first block"+
						"2 : the second block"+
						"3 : the first block");
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	class TreeNode
	{
		private Template			mTemplate = null;
		private String				mText = null;
		private ArrayList<TreeNode>	mChildren = new ArrayList<TreeNode>();
		private TreeNode			mParent = null;

		public TreeNode(Template template)
		{
			mTemplate = template;
		}

		public TreeNode(TreeNode parent, String text)
		{
			if (null == text) throw new IllegalArgumentException("title can't be null.");

			parent.addChild(this);
			mText = text;
			mTemplate = parent.mTemplate;
		}

		public void output()
		{
			if (0 == mChildren.size())
			{
				mTemplate.setValue("level", "");
			}
			else
			{
				InternalValue	nodes = mTemplate.createInternalValue();
				InternalValue	text = null;
				int				depth = 0;
				int				counter = 0;
				
				for (TreeNode child : mChildren)
				{
					child.output();
					depth = child.getDepth();
					mTemplate.removeValue("indent");
					if (1 == depth)
					{
						mTemplate.setValue("indent", "");
					}
					else
					{
						for (int i = 1; i < depth; i++)
						{
							mTemplate.appendBlock("indent", "indent");
						}
					}
					mTemplate.setValue("depth", depth);
					text = mTemplate.createInternalValue();
					text.appendValue(++counter);
					text.appendValue("-");
					text.appendValue(child.getText());
					mTemplate.setValue("text", text);
					nodes.appendBlock("node");
				}
				mTemplate.setValue("nodes", nodes);
				mTemplate.setBlock("level", "level");
			}
		}

		private void addChild(TreeNode child)
		{
			child.mParent = this;
			mChildren.add(child);
		}

		public String getText()
		{
			return mText;
		}

		public TreeNode getParent()
		{
			return mParent;
		}

		public int getDepth()
		{
			TreeNode parent = getParent();
			int depth = 0;
			while (parent != null)
			{
				parent = parent.getParent();
				depth++;
			}

			return depth;
		}
	}

	public void testHasValues()
	{
		Template template = null;
		try
		{
			template = TemplateFactory.HTML.get("defaultvalues_in");
			assertTrue(template.hasValueId("VALUE1"));
			assertTrue(template.hasValueId("VALUE2"));
			assertTrue(template.hasValueId("VALUE3"));
			assertTrue(false == template.hasValueId("VALUE4"));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testUnsetValuesOutput()
	{
		Template template = TemplateFactory.HTML.get("unsetvalues_output_in");
		assertTrue(template.hasValueId("VALUE1"));
		assertTrue(template.hasValueId("VALUE2"));
		assertTrue(template.hasValueId("VALUE3"));
		assertTrue(template.hasValueId("VALUE4"));
		assertTrue(template.hasValueId("VALUE5"));
		assertTrue(template.hasValueId("VALUE6"));
		assertTrue(template.hasValueId("VALUE7"));
		assertEquals(template.getContent(), getTemplateContent("unsetvalues_output_in", TemplateFactory.HTML.getParser()));
	}

	public void testSetBeanValues()
	{
		try
		{
			Template template = TemplateFactory.HTML.get("values_bean_in");
			BeanImpl bean = new BeanImpl();
			template.setBean(bean);
			assertEquals(template.getContent(), getTemplateContent("values_bean_out", TemplateFactory.HTML.getParser()));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testRemoveBeanValues()
	{
		try
		{
			Template template = TemplateFactory.HTML.get("values_bean_in");
			BeanImpl bean = new BeanImpl();
			template.setBean(bean);
			assertEquals(template.getContent(), getTemplateContent("values_bean_out", TemplateFactory.HTML.getParser()));
			template.removeBean(bean);
			assertEquals(template.getContent(), getTemplateContent("values_bean_in", TemplateFactory.HTML.getParser()));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testSetBeanValuesPrefix()
	{
		try
		{
			Template template = TemplateFactory.HTML.get("values_bean_prefix_in");
			BeanImpl bean = new BeanImpl();
			template.setBean(bean, "PREFIX:");
			assertEquals(template.getContent(), getTemplateContent("values_bean_prefix_out", TemplateFactory.HTML.getParser()));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testRemoveBeanValuesPrefix()
	{
		try
		{
			Template template = TemplateFactory.HTML.get("values_bean_prefix_in");
			BeanImpl bean = new BeanImpl();
			template.setBean(bean, "PREFIX:");
			assertEquals(template.getContent(), getTemplateContent("values_bean_prefix_out", TemplateFactory.HTML.getParser()));
			template.removeBean(bean, "WRONGPREFIX:");
			assertEquals(template.getContent(), getTemplateContent("values_bean_prefix_out", TemplateFactory.HTML.getParser()));
			template.removeBean(bean, "PREFIX:");
			assertEquals(template.getContent(), getTemplateContent("values_bean_prefix_in", TemplateFactory.HTML.getParser()));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testSetBeanValuesHtml()
	{
		try
		{
			Template		template = null;
			HtmlBeanImpl	bean = new HtmlBeanImpl();

			template = TemplateFactory.HTML.get("values_bean_html_in");
			template.setBean(bean, "PARAM:");
			assertEquals(template.getContent(), getTemplateContent("values_bean_html_out_emptyproperty", TemplateFactory.HTML.getParser()));

			bean = new HtmlBeanImpl();
			bean.setColors(new String[] {"red", "blue", "yellow"});
			bean.setWantsupdates(true);
			bean.setFirstname("Geert");
			bean.setLastname("Bevin");
			template = TemplateFactory.HTML.get("values_bean_html_in");
			template.setBean(bean, "PARAM:");
			assertEquals(template.getContent(), getTemplateContent("values_bean_html_out_content1", TemplateFactory.HTML.getParser()));

			bean = new HtmlBeanImpl();
			bean.setColors(new String[] {"red", "orange", "white"});
			bean.setWantsupdates(false);
			bean.setFirstname("Nathalie");
			bean.setLastname("&<>");
			template = TemplateFactory.HTML.get("values_bean_html_in");
			template.setBean(bean, "PARAM:");
			assertEquals(template.getContent(), getTemplateContent("values_bean_html_out_content2", TemplateFactory.HTML.getParser()));
			
			bean.addConstraint(new ConstrainedProperty("lastname").displayedRaw(true));
			template = TemplateFactory.HTML.get("values_bean_html_in");
			template.setBean(bean, "PARAM:");
			assertEquals(template.getContent(), getTemplateContent("values_bean_html_out_content3", TemplateFactory.HTML.getParser()));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testRemoveBeanValuesHtml()
	{
		try
		{
			Template		template = null;
			HtmlBeanImpl	bean = new HtmlBeanImpl();

			bean = new HtmlBeanImpl();
			bean.setColors(new String[] {"red", "blue", "yellow"});
			bean.setWantsupdates(true);
			bean.setFirstname("Geert");
			bean.setLastname("Bevin");
			template = TemplateFactory.HTML.get("values_bean_html_in");
			template.setBean(bean, "PARAM:");
			assertEquals(template.getContent(), getTemplateContent("values_bean_html_out_content1", TemplateFactory.HTML.getParser()));
			template.removeBean(bean, "WRONGPARAM:");
			assertEquals(template.getContent(), getTemplateContent("values_bean_html_out_content1", TemplateFactory.HTML.getParser()));
			template.removeBean(bean, "PARAM:");
			assertEquals(template.getContent(), getTemplateContent("values_bean_html_out_empty", TemplateFactory.HTML.getParser()));

			bean = new HtmlBeanImpl();
			bean.setColors(new String[] {"red", "orange", "white"});
			bean.setWantsupdates(false);
			bean.setFirstname("Nathalie");
			bean.setLastname("&<>");
			template = TemplateFactory.HTML.get("values_bean_html_in");
			template.setBean(bean, "PARAM:");
			assertEquals(template.getContent(), getTemplateContent("values_bean_html_out_content2", TemplateFactory.HTML.getParser()));
			template.removeBean(bean, "WRONGPARAM:");
			assertEquals(template.getContent(), getTemplateContent("values_bean_html_out_content2", TemplateFactory.HTML.getParser()));
			template.removeBean(bean, "PARAM:");
			assertEquals(template.getContent(), getTemplateContent("values_bean_html_out_empty", TemplateFactory.HTML.getParser()));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
}

