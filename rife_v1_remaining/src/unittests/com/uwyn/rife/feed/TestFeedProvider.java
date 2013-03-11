/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestFeedProvider.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.feed;

import com.uwyn.rife.TestCaseServerside;
import com.uwyn.rife.tools.HttpUtils;

public class TestFeedProvider extends TestCaseServerside
{
	public TestFeedProvider(int siteType, String name)
	{
		super(siteType, name);
	}

	public void testFeedProviderRss()
	throws Exception
	{
		setupSite("site/feed.xml");

		HttpUtils.Request 	request = new HttpUtils.Request("http://localhost:8181/rss");
		HttpUtils.Page 		page = request.retrieve();

		assertEquals(page.getContent(), "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
			"<rss version=\"2.0\">\n"+
			"	<channel>\n"+
			"		<title>feed_title</title>\n"+
			"		<link>feed_link</link>\n"+
			"		<description>feed_description</description>\n"+
			"		<language>feed_language</language>\n"+
			"		<copyright>feed_copyright</copyright>\n"+
			"		<pubDate>Sat, 01 Jan 2005 02:00:00 +0100</pubDate>\n"+
			"		<managingEditor>feed_author</managingEditor>\n"+
			"		\n"+
			"			<item>\n"+
			"				<title>entry_title1</title>\n"+
			"				<link>entry_link1</link>\n"+
			"				<description>entry_content1</description>\n"+
			"				<pubDate>Sat, 01 Jan 2005 01:00:00 +0100</pubDate>\n"+
			"				<author>entry_author1</author>\n"+
			"				<guid>entry_link1</guid>\n"+
			"			</item>\n"+
			"		\n"+
			"			<item>\n"+
			"				<title>entry_title2</title>\n"+
			"				<link>entry_link2</link>\n"+
			"				<description>entry_content2</description>\n"+
			"				<pubDate>Sat, 01 Jan 2005 02:00:00 +0100</pubDate>\n"+
			"				<author>entry_author2</author>\n"+
			"				<guid>entry_link2</guid>\n"+
			"			</item>\n"+
			"		\n"+
			"		\n"+
			"	</channel>\n"+
			"</rss>");
	}

	public void testFeedProviderAtom()
	throws Exception
	{
		setupSite("site/feed.xml");

		HttpUtils.Request 	request = new HttpUtils.Request("http://localhost:8181/atom");
		HttpUtils.Page 		page = request.retrieve();

		assertEquals(page.getContent(), "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
			"<feed xmlns=\"http://purl.org/atom/ns#\" xmlns:taxo=\"http://purl.org/rss/1.0/modules/taxonomy/\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:sy=\"http://purl.org/rss/1.0/modules/syndication/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" version=\"0.3\">\n"+
			"	<title>feed_title</title>\n"+
			"	<link rel=\"alternate\" href=\"feed_link\" type=\"text/html\" />\n"+
			"	<author>\n"+
			"		<name>feed_author</name>\n"+
			"	</author>\n"+
			"	<copyright>feed_copyright</copyright>\n"+
			"	<info>feed_description</info>\n"+
			"	<modified>2005-01-01T02:00:00+0100</modified>\n"+
			"	<dc:creator>feed_author</dc:creator>\n"+
			"	<dc:date>2005-01-01T02:00:00+0100</dc:date>\n"+
			"	<dc:language>feed_language</dc:language>\n"+
			"	<dc:rights>feed_copyright</dc:rights>\n"+
			"	\n"+
			"	\n"+
			"		<entry>\n"+
			"			<title>entry_title1</title>\n"+
			"			<link rel=\"alternate\" href=\"entry_link1\" type=\"text/html\" />\n"+
			"			<author>\n"+
			"				<name>entry_author1</name>\n"+
			"			</author>\n"+
			"			<modified>2005-01-01T01:00:00+0100</modified>\n"+
			"			<content type=\"text/html\" mode=\"escaped\">entry_content1</content>\n"+
			"			<id>entry_link1</id>\n"+
			"			<issued>2005-01-01T01:00:00+0100</issued>\n"+
			"			<dc:creator>entry_author1</dc:creator>\n"+
			"			<dc:date>2005-01-01T01:00:00+0100</dc:date>\n"+
			"		</entry>\n"+
			"	\n"+
			"		<entry>\n"+
			"			<title>entry_title2</title>\n"+
			"			<link rel=\"alternate\" href=\"entry_link2\" type=\"text/html\" />\n"+
			"			<author>\n"+
			"				<name>entry_author2</name>\n"+
			"			</author>\n"+
			"			<modified>2005-01-01T02:00:00+0100</modified>\n"+
			"			<content type=\"text/html\" mode=\"escaped\">entry_content2</content>\n"+
			"			<id>entry_link2</id>\n"+
			"			<issued>2005-01-01T02:00:00+0100</issued>\n"+
			"			<dc:creator>entry_author2</dc:creator>\n"+
			"			<dc:date>2005-01-01T02:00:00+0100</dc:date>\n"+
			"		</entry>\n"+
			"	\n"+
			"	\n"+
			"</feed>");
	}

	public void testFeedProviderNamespacesRss()
	throws Exception
	{
		setupSite("site/feed.xml");

		HttpUtils.Request 	request = new HttpUtils.Request("http://localhost:8181/namespaces_rss");
		HttpUtils.Page 		page = request.retrieve();

		assertEquals(page.getContent(), "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
			"<rss xmlns:doap=\"http://usefulinc.com/ns/doap#\" xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" version=\"2.0\">\n"+
			"	<channel>\n"+
			"		<title>feed_title_namespace</title>\n"+
			"		<link>feed_link_namespace</link>\n"+
			"		<description>feed_description_namespace</description>\n"+
			"		<language>feed_language_namespace</language>\n"+
			"		<copyright>feed_copyright_namespace</copyright>\n"+
			"		<pubDate>Sat, 01 Jan 2005 02:00:00 +0100</pubDate>\n"+
			"		<managingEditor>feed_author_namespace</managingEditor>\n"+
			"		\n"+
			"			<item>\n"+
			"				<title>entry_title_namespace1</title>\n"+
			"				<link>entry_link_namespace1</link>\n"+
			"				<description><doap:Project>entry_content_namespace1</doap:Project></description>\n"+
			"				<pubDate>Sat, 01 Jan 2005 01:00:00 +0100</pubDate>\n"+
			"				<author>entry_author_namespace1</author>\n"+
			"				<guid>entry_link_namespace1</guid>\n"+
			"			</item>\n"+
			"		\n"+
			"			<item>\n"+
			"				<title>entry_title_namespace2</title>\n"+
			"				<link>entry_link_namespace2</link>\n"+
			"				<description><doap:Project>entry_content_namespace2</doap:Project></description>\n"+
			"				<pubDate>Sat, 01 Jan 2005 02:00:00 +0100</pubDate>\n"+
			"				<author>entry_author_namespace2</author>\n"+
			"				<guid>entry_link_namespace2</guid>\n"+
			"			</item>\n"+
			"		\n"+
			"		\n"+
			"	</channel>\n"+
			"</rss>");
	}

	public void testFeedProviderNamespacesAtom()
	throws Exception
	{
		setupSite("site/feed.xml");

		HttpUtils.Request 	request = new HttpUtils.Request("http://localhost:8181/namespaces_atom");
		HttpUtils.Page 		page = request.retrieve();

		assertEquals(page.getContent(), "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
			"<feed xmlns=\"http://purl.org/atom/ns#\" xmlns:taxo=\"http://purl.org/rss/1.0/modules/taxonomy/\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:sy=\"http://purl.org/rss/1.0/modules/syndication/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:doap=\"http://usefulinc.com/ns/doap#\" xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" version=\"0.3\">\n"+
			"	<title>feed_title_namespace</title>\n"+
			"	<link rel=\"alternate\" href=\"feed_link_namespace\" type=\"text/html\" />\n"+
			"	<author>\n"+
			"		<name>feed_author_namespace</name>\n"+
			"	</author>\n"+
			"	<copyright>feed_copyright_namespace</copyright>\n"+
			"	<info>feed_description_namespace</info>\n"+
			"	<modified>2005-01-01T02:00:00+0100</modified>\n"+
			"	<dc:creator>feed_author_namespace</dc:creator>\n"+
			"	<dc:date>2005-01-01T02:00:00+0100</dc:date>\n"+
			"	<dc:language>feed_language_namespace</dc:language>\n"+
			"	<dc:rights>feed_copyright_namespace</dc:rights>\n"+
			"	\n"+
			"	\n"+
			"		<entry>\n"+
			"			<title>entry_title_namespace1</title>\n"+
			"			<link rel=\"alternate\" href=\"entry_link_namespace1\" type=\"text/html\" />\n"+
			"			<author>\n"+
			"				<name>entry_author_namespace1</name>\n"+
			"			</author>\n"+
			"			<modified>2005-01-01T01:00:00+0100</modified>\n"+
			"			<content type=\"application/rdf+xml\"><doap:Project>entry_content_namespace1</doap:Project></content>\n"+
			"			<id>entry_link_namespace1</id>\n"+
			"			<issued>2005-01-01T01:00:00+0100</issued>\n"+
			"			<dc:creator>entry_author_namespace1</dc:creator>\n"+
			"			<dc:date>2005-01-01T01:00:00+0100</dc:date>\n"+
			"		</entry>\n"+
			"	\n"+
			"		<entry>\n"+
			"			<title>entry_title_namespace2</title>\n"+
			"			<link rel=\"alternate\" href=\"entry_link_namespace2\" type=\"text/html\" />\n"+
			"			<author>\n"+
			"				<name>entry_author_namespace2</name>\n"+
			"			</author>\n"+
			"			<modified>2005-01-01T02:00:00+0100</modified>\n"+
			"			<content type=\"application/rdf+xml\"><doap:Project>entry_content_namespace2</doap:Project></content>\n"+
			"			<id>entry_link_namespace2</id>\n"+
			"			<issued>2005-01-01T02:00:00+0100</issued>\n"+
			"			<dc:creator>entry_author_namespace2</dc:creator>\n"+
			"			<dc:date>2005-01-01T02:00:00+0100</dc:date>\n"+
			"		</entry>\n"+
			"	\n"+
			"	\n"+
			"</feed>");
	}
}
