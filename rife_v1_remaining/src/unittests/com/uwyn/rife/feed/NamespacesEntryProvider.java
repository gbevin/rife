/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: NamespacesEntryProvider.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.feed;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.engine.ElementSupport;
import java.util.Calendar;
import java.util.LinkedHashMap;

public class NamespacesEntryProvider implements EntryProvider
{
	private Calendar mCalendar = null;

	public NamespacesEntryProvider()
	{
		mCalendar = Calendar.getInstance();
		mCalendar.setTimeZone(RifeConfig.Tools.getDefaultTimeZone());
		mCalendar.set(2005, Calendar.JANUARY, 1, 0, 0, 0);
		mCalendar.set(Calendar.AM_PM, Calendar.AM);
	}

	public Feed getFeedDescriptor(ElementSupport element)
	{
		Feed feed = new Feed();
		feed
			.title("feed_title_namespace")
			.author("feed_author_namespace")
			.copyright("feed_copyright_namespace")
			.description("feed_description_namespace")
			.language("feed_language_namespace")
			.link("feed_link_namespace")
			.publishedDate(mCalendar.getTime())
			.namespaces(new LinkedHashMap<String, String>() {{
				put("doap", "http://usefulinc.com/ns/doap#");
				put("foaf", "http://xmlns.com/foaf/0.1/");
			}});

		return feed;
	}

	public void provideEntries(ElementSupport element, EntryProcessor processor)
	{
		for (int i = 0; i < 2; i++)
		{
			mCalendar.set(Calendar.HOUR, i+1);
			Entry entry = new Entry();
			entry
				.author("entry_author_namespace"+(i+1))
				.content("<doap:Project>entry_content_namespace"+(i+1)+"</doap:Project>")
				.link("entry_link_namespace"+(i+1))
				.publishedDate(mCalendar.getTime())
				.title("entry_title_namespace"+(i+1))
				.type("application/rdf+xml")
				.escaped(false);

			processor.setEntry(entry);
		}
	}
}
