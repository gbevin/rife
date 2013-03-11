/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FilteredTagsMap.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import java.util.HashMap;

public class FilteredTagsMap extends HashMap<String, FilteredTags>
{
	private static final long serialVersionUID = 1574243153804359271L;

	public FilteredTagsMap()
	{
		super();
	}
	
	public boolean containsFilter(String filter)
	{
		if (null == filter)			throw new IllegalArgumentException("filter can't be null.");
		if (0 == filter.length())	throw new IllegalArgumentException("filter can't be empty.");

		return containsKey(filter);
	}
	
	public FilteredTags getFilteredTag(String filter)
	{
		if (null == filter)			throw new IllegalArgumentException("filter can't be null.");
		if (0 == filter.length())	throw new IllegalArgumentException("filter can't be empty.");

		return get(filter);
	}
	
	void addFilteredTag(String filter, String[] capturedGroups)
	{
		assert filter != null;
		assert filter.length() > 0;
		assert capturedGroups != null;

		FilteredTags filtered_values = getFilteredTag(filter);
		if (null == filtered_values)
		{
			filtered_values = new FilteredTags();
			put(filter, filtered_values);
		}
		
		filtered_values.add(capturedGroups);
	}
}
