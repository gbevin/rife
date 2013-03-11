/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParsedBlockData.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import java.util.ArrayList;

class ParsedBlockData extends ArrayList<ParsedBlockPart>
{
	private static final long serialVersionUID = -6957434329992948164L;

	ParsedBlockData()
	{
		super();
	}

	void addPart(ParsedBlockPart part)
	{
		assert part != null;

		add(part);
	}

	int countParts()
	{
		return size();
	}

	ParsedBlockPart getPart(int index)
	{
		assert index >=0;

		return get(index);
	}
}
