/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestParsedBlockData.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import junit.framework.TestCase;

public class TestParsedBlockData extends TestCase
{
	public TestParsedBlockData(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
		ParsedBlockData block_data = new ParsedBlockData();

		assertEquals(block_data.countParts(), 0);
	}

	public void testParts()
	{
		ParsedBlockData block_data = new ParsedBlockData();
		ParsedBlockPart block_part1 = new ParsedBlockText("text");
		ParsedBlockPart block_part2 = new ParsedBlockText("text");
		ParsedBlockPart block_part3 = new ParsedBlockText("text");

		block_data.addPart(block_part1);
		block_data.addPart(block_part2);
		block_data.addPart(block_part3);

		assertEquals(block_data.countParts(), 3);
		assertSame(block_data.getPart(0), block_part1);
		assertSame(block_data.getPart(1), block_part2);
		assertSame(block_data.getPart(2), block_part3);
	}
}
