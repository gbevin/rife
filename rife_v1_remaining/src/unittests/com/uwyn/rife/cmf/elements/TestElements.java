/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestElements.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.elements;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;

import com.meterware.httpunit.*;
import com.uwyn.rife.cmf.Content;
import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.cmf.dam.ContentImage;
import com.uwyn.rife.cmf.dam.ContentManager;
import com.uwyn.rife.cmf.dam.ContentQueryManager;
import com.uwyn.rife.cmf.dam.contentmanagers.DatabaseContentFactory;
import com.uwyn.rife.cmf.dam.contentstores.TestsuiteDatabaseContentStores;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.Datasources;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.tools.FileUtils;
import com.uwyn.rife.tools.HttpUtils;

public class TestElements extends TestsuiteDatabaseContentStores
{
	private Datasource	mDatasource = null;
	
	public TestElements(String datasourceName, int siteType, String name)
	{
		super(datasourceName, siteType, name);
		
		mDatasource = Datasources.getRepInstance().getDatasource(datasourceName);
	}

	public void setUp()
	throws Exception
	{
		super.setUp();
		DatabaseContentFactory.getInstance(mDatasource).install();
	}

	public void tearDown()
	throws Exception
	{
		DatabaseContentFactory.getInstance(mDatasource).remove();
		super.tearDown();
	}

	public void testServeContentRaw()
	throws Exception
	{
		int size = (int)(65535*5.8);
		byte[] binary = new byte[size];
		for (int i = 0; i < size; i++)
		{
			binary[i] = (byte)(i%255);
		}

		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);
		Content content = new Content(MimeType.RAW, new ByteArrayInputStream(binary)).name("mycoollib.so");
		manager.storeContent("/rawdata", content, null);

		setupSite("site/cmf.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/serve/rawdata");
		response = conversation.getResponse(request);

		assertEquals("application/octet-stream", response.getContentType());
		assertEquals(size, response.getContentLength());

		assertTrue(Arrays.equals(binary, FileUtils.readBytes(response.getInputStream())));

		request = new GetMethodWebRequest("http://localhost:8181/serve/rawdata/mycoollib.so");
		response = conversation.getResponse(request);

		assertEquals("application/octet-stream", response.getContentType());
		assertEquals(size, response.getContentLength());

		assertTrue(Arrays.equals(binary, FileUtils.readBytes(response.getInputStream())));
	}

	public void testServeContentImage()
	throws Exception
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);

		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

		Content content = new Content(MimeType.IMAGE_PNG, data_image_gif).name("uwyn.png");
		manager.storeContent("/imagegif", content, null);

		setupSite("site/cmf.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn.png");
		byte[] data_image_png = FileUtils.readBytes(image_resource_png);

		request = new GetMethodWebRequest("http://localhost:8181/serve/imagegif");
		response = conversation.getResponse(request);

		assertEquals(MimeType.IMAGE_PNG.toString(), response.getContentType());
		assertEquals(data_image_png.length, response.getContentLength());
		assertTrue(Arrays.equals(data_image_png, FileUtils.readBytes(response.getInputStream())));

		request = new GetMethodWebRequest("http://localhost:8181/serve/imagegif/uwyn.png");
		response = conversation.getResponse(request);

		assertEquals(MimeType.IMAGE_PNG.toString(), response.getContentType());
		assertEquals(data_image_png.length, response.getContentLength());
		assertTrue(Arrays.equals(data_image_png, FileUtils.readBytes(response.getInputStream())));
	}

	public void testServeContentImageEmbedded()
	throws Exception
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);

		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

		Content content = new Content(MimeType.IMAGE_PNG, data_image_gif);
		manager.storeContent("/imagegif", content, null);

		setupSite("site/cmf.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/serve_embedded_image");
		response = conversation.getResponse(request);
		assertEquals("The content \"<img src=\"/serve/imagegif\" width=\"88\" height=\"33\" alt=\"\" />\" is served embedded.\n", response.getText());


		request = new GetMethodWebRequest("http://localhost:8181"+response.getImages()[0].getSource());
		response = conversation.getResponse(request);

		URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn.png");
		assertTrue(Arrays.equals(FileUtils.readBytes(image_resource_png), FileUtils.readBytes(response.getInputStream())));
	}

	public void testServeContentText()
	throws Exception
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);

		String data = "<i>cool beans</i><p>hot <a href=\"http://uwyn.com\">chili</a></p>";
		Content content = new Content(MimeType.APPLICATION_XHTML, data).fragment(true).name("mytext.html");
		manager.storeContent("/textxhtml", content, null);

		setupSite("site/cmf.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/serve/textxhtml");
		request.setHeaderField("Accept-Encoding", "");
		response = conversation.getResponse(request);

		assertEquals(0, response.getContentType().indexOf(MimeType.APPLICATION_XHTML.toString()));
		assertEquals(data.length(), response.getContentLength());
		assertEquals(data, response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/serve/textxhtml");
		request.setHeaderField("Accept-Encoding", "gzip");
		response = conversation.getResponse(request);

		assertEquals(0, response.getContentType().indexOf(MimeType.APPLICATION_XHTML.toString()));
		assertTrue(data.length() < response.getContentLength());
		assertEquals(data, response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/serve/textxhtml/mytext.html");
		request.setHeaderField("Accept-Encoding", "");
		response = conversation.getResponse(request);

		assertEquals(0, response.getContentType().indexOf(MimeType.APPLICATION_XHTML.toString()));
		assertEquals(data.length(), response.getContentLength());
		assertEquals(data, response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/serve/textxhtml/mytext.html");
		request.setHeaderField("Accept-Encoding", "gzip");
		response = conversation.getResponse(request);

		assertEquals(0, response.getContentType().indexOf(MimeType.APPLICATION_XHTML.toString()));
		assertTrue(data.length() < response.getContentLength());
		assertEquals(data, response.getText());
	}

	public void testServeContentTextEmbedded()
	throws Exception
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);

		String data = "<i>cool beans</i><p>hot <a href=\"http://uwyn.com\">chili</a></p>";
		Content content = new Content(MimeType.APPLICATION_XHTML, data).fragment(true);
		manager.storeContent("/textxhtml", content, null);

		setupSite("site/cmf.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/serve_embedded_xhtml");
		response = conversation.getResponse(request);
		assertEquals("The content \"<i>cool beans</i><p>hot <a href=\"http://uwyn.com\">chili</a></p>\" is served embedded.\n", response.getText());
	}

	public void testServeContentRepository()
	throws Exception
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);

		manager.createRepository("nondefault");

		String data = "<i>cool beans</i><p>hot <a href=\"http://uwyn.com\">chili</a></p>";
		Content content = new Content(MimeType.APPLICATION_XHTML, data).fragment(true).name("mytext.html");
		manager.storeContent("nondefault:/textxhtml", content, null);

		setupSite("site/cmf.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/serve_repository/textxhtml");
		request.setHeaderField("Accept-Encoding", "");
		response = conversation.getResponse(request);

		assertEquals(0, response.getContentType().indexOf(MimeType.APPLICATION_XHTML.toString()));
		assertEquals(data.length(), response.getContentLength());
		assertEquals(data, response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/serve_repository/textxhtml");
		request.setHeaderField("Accept-Encoding", "gzip");
		response = conversation.getResponse(request);

		assertEquals(0, response.getContentType().indexOf(MimeType.APPLICATION_XHTML.toString()));
		assertTrue(data.length() < response.getContentLength());
		assertEquals(data, response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/serve_repository/textxhtml/mytext.html");
		request.setHeaderField("Accept-Encoding", "");
		response = conversation.getResponse(request);

		assertEquals(0, response.getContentType().indexOf(MimeType.APPLICATION_XHTML.toString()));
		assertEquals(data.length(), response.getContentLength());
		assertEquals(data, response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/serve_repository/textxhtml/mytext.html");
		request.setHeaderField("Accept-Encoding", "gzip");
		response = conversation.getResponse(request);

		assertEquals(0, response.getContentType().indexOf(MimeType.APPLICATION_XHTML.toString()));
		assertTrue(data.length() < response.getContentLength());
		assertEquals(data, response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/serve/textxhtml");
		try
		{
			response = conversation.getResponse(request);
			fail();
		}
		catch (HttpNotFoundException e)
		{
			assertEquals(404, e.getResponseCode());
		}

		request = new GetMethodWebRequest("http://localhost:8181/serve/textxhtml/mytext.html");
		try
		{
			response = conversation.getResponse(request);
			fail();
		}
		catch (HttpNotFoundException e)
		{
			assertEquals(404, e.getResponseCode());
		}
	}

	public void testServeContentUnknown()
	throws Exception
	{
		setupSite("site/cmf.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;

		request = new GetMethodWebRequest("http://localhost:8181/serve/imageunknown");
		try
		{
			conversation.getResponse(request);
			fail();
		}
		catch (HttpNotFoundException e)
		{
			assertEquals(404, e.getResponseCode());
		}
	}

	public void testServeContentNoPathinfo()
	throws Exception
	{
		setupSite("site/cmf.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;

		request = new GetMethodWebRequest("http://localhost:8181/serve");
		try
		{
			conversation.getResponse(request);
			fail();
		}
		catch (HttpNotFoundException e)
		{
			assertEquals(404, e.getResponseCode());
		}
	}

	public void testServeContentNoDatasource()
	throws Exception
	{
		setupSite("site/cmf.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;

		request = new GetMethodWebRequest("http://localhost:8181/serve_nodatasource/dummy");
		try
		{
			conversation.getResponse(request);
			fail();
		}
		catch (Throwable e)
		{
			assertTrue(getLogSink().getInternalException() instanceof MissingDatasourceException);
			assertEquals(".SERVE_NO_DATASOURCE", ((MissingDatasourceException)getLogSink().getInternalException()).getId());
		}
	}

	public void testContentQueryManagerContentForHtml()
	throws Exception
	{
	    ContentQueryManager manager = new ContentQueryManager<ContentImage>(mDatasource, ContentImage.class);
		manager.install();
		try
		{
			URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
			byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);
			ContentImage content = new ContentImage()
				.name("the content name")
				.image(data_image_gif);

			int id = manager.save(content);

			setupSite("site/cmf.xml");

			HttpUtils.Page page = HttpUtils.retrievePage("http://localhost:8181/contentforhtml?id="+id);

			assertEquals("<img src=\"/serve/contentimage/"+id+"/image\" width=\"88\" height=\"33\" alt=\"\" />"+
						 "<img src=\"/serve/contentimage/"+id+"/image\" width=\"88\" height=\"33\" alt=\"\" />", page.getContent());
		}
		finally
		{
			manager.remove();
		}
	}

	public void testServeContentModifiedSince()
	throws Exception
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);

		String data = "<i>cool beans</i><p>hot <a href=\"http://uwyn.com\">chili</a></p>";
		Content content = new Content(MimeType.APPLICATION_XHTML, data).fragment(true);
		manager.storeContent("/textxhtml", content, null);

		setupSite("site/cmf.xml");

		// doing this with a raw sockets since there's some bug in JDK 1.4 that
		// triggers an exception otherwise
		StringBuffer buffer = new StringBuffer();
		try
		{
			InetAddress addr = InetAddress.getByName("localhost");
			int port = 8181;

			Socket socket = new Socket(addr, port);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
			writer.write("GET /serve/textxhtml HTTP/1.1\r\n");
			writer.write("Host: localhost\r\n");
			writer.write("User-Agent: RIFE\r\n");
			writer.write("Accept: text/html\r\n");
			writer.write("If-Modified-Since: 24 Aug 2204 15:14:06 GMT\r\n");
			writer.write("\r\n");
			writer.flush();

			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null)
			{
				buffer.append(line);
				if (0 == line.length())
				{
					break;
				}
			}
			writer.close();
			reader.close();
			socket.close();
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}

		assertEquals(0, buffer.indexOf("HTTP/1.1 304 Not Modified"));
	}
}
