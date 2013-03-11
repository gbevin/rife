/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestContentManager.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam;

import com.uwyn.rife.cmf.dam.contentstores.*;

import com.uwyn.rife.cmf.Content;
import com.uwyn.rife.cmf.ContentInfo;
import com.uwyn.rife.cmf.ContentRepository;
import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.cmf.UnsupportedMimeType;
import com.uwyn.rife.cmf.dam.contentmanagers.DatabaseContent;
import com.uwyn.rife.cmf.dam.contentmanagers.DatabaseContentFactory;
import com.uwyn.rife.cmf.dam.contentmanagers.DatabaseContentInfo;
import com.uwyn.rife.cmf.dam.contentmanagers.exceptions.InstallContentErrorException;
import com.uwyn.rife.cmf.dam.contentmanagers.exceptions.UnknownContentRepositoryException;
import com.uwyn.rife.cmf.dam.contentmanagers.exceptions.UnsupportedMimeTypeException;
import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.tools.FileUtils;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import com.uwyn.rife.tools.exceptions.FileUtilsErrorException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import junit.framework.TestCase;

public class TestContentManager extends TestCase
{
	private Datasource mDatasource = new Datasource();
	
	public TestContentManager(Datasource datasource, String datasourceName, String name)
	{
		super(name);
		
		mDatasource = datasource;
	}
	
    public void setUp()
	throws Exception
	{
		DatabaseContentFactory.getInstance(mDatasource).install();
	}
	
    public void tearDown()
	throws Exception
	{
		try
		{
			DatabaseContentFactory.getInstance(mDatasource).remove();
		}
		catch (Throwable e)
		{
			// discart errors
		}
	}
	
	public void testInstallError()
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);

		try
		{
			manager.install();
			fail();
		}
		catch (InstallContentErrorException e)
		{
		    assertNotNull(e.getCause());
		}
	}

	public void testRemoveError()
	throws Exception
	{
		DatabaseContentFactory.getInstance(mDatasource).remove();

		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);

		try
		{
			manager.remove();
			fail();
		}
		catch (ContentManagerException e)
		{
		    assertNotNull(e.getCause());
		}
	}

	public void testStoreContentXhtml()
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);

		// text data
		final String data_text = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n"+
						   "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"+
						   "<html><head><title>my title</title></head><body></body></html>";
		Content content = new Content(MimeType.APPLICATION_XHTML, data_text);
		assertTrue(manager.storeContent("/textcontent", content, null));
		
		manager.useContentData("/textcontent", new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					assertEquals(data_text, contentData);
					return null;
				}
			});
	}
	
	public void testStoreContentGif()
	throws Exception
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);

		// image data
		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn.png");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);
		final byte[] data_image_png = FileUtils.readBytes(image_resource_png);

		Content content = new Content(MimeType.IMAGE_PNG, data_image_gif);
		assertTrue(manager.storeContent("/imagegif", content, null));

		manager.useContentData("/imagegif", new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					assertTrue(Arrays.equals(data_image_png, (byte[])contentData));
					return null;
				}
			});
	}

	public void testStoreContentTif()
	throws Exception
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);

		// image data
		URL image_resource_tif = ResourceFinderClasspath.getInstance().getResource("uwyn.tif");
		URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn-noalpha.png");
		byte[] data_image_tif = FileUtils.readBytes(image_resource_tif);
		final byte[] data_image_png = FileUtils.readBytes(image_resource_png);

		Content content = new Content(MimeType.IMAGE_PNG, data_image_tif);
		assertTrue(manager.storeContent("/imagetif", content, null));

		manager.useContentData("/imagetif", new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					assertTrue(Arrays.equals(data_image_png, (byte[])contentData));
					return null;
				}
			});
	}

	public void testStoreContentGifResized()
	throws Exception
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);

		// image data
		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn_resized-width_20.png");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);
		final byte[] data_image_png = FileUtils.readBytes(image_resource_png);

		Content content_image = new Content(MimeType.IMAGE_PNG, data_image_gif);
		content_image.attribute("width", 20);
		assertTrue(manager.storeContent("/imagegif", content_image, null));

		manager.useContentData("/imagegif", new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					assertTrue(Arrays.equals(data_image_png, (byte[])contentData));
					return null;
				}
			});
	}

	public void testStoreContentTifResized()
	throws Exception
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);

		// image data
		URL image_resource_tif = ResourceFinderClasspath.getInstance().getResource("uwyn.tif");
		URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn_resized-width_20-noalpha.png");
		byte[] data_image_tif = FileUtils.readBytes(image_resource_tif);
		final byte[] data_image_png = FileUtils.readBytes(image_resource_png);

		Content content = new Content(MimeType.IMAGE_PNG, data_image_tif);
		content.attribute("width", 20);
		assertTrue(manager.storeContent("/imagetif", content, null));

		manager.useContentData("/imagetif", new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					assertTrue(Arrays.equals(data_image_png, (byte[])contentData));
					return null;
				}
			});
	}

	public void testStoreContentRaw()
	throws Exception
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);

		int size = 1024*1024*4; // 4Mb
		final byte[] raw = new byte[size];
		for (int i = 0; i < size; i++)
		{
			raw[i] = (byte)(i%255);
		}
		
		Content content = new Content(MimeType.RAW, new ByteArrayInputStream(raw));
		assertTrue(manager.storeContent("/rawdata", content, null));

		manager.useContentData("/rawdata", new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					try
					{
						assertTrue(Arrays.equals(raw, FileUtils.readBytes((InputStream)contentData)));
					}
					catch (FileUtilsErrorException e)
					{
						throwException(e);
					}
					return null;
				}
			});
	}

	public void testStoreContentRepository()
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);
		
		manager.createRepository("mynewrep");

		// text data
		final String data_text = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n"+
						   "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"+
						   "<html><head><title>my title</title></head><body></body></html>";
		Content content = new Content(MimeType.APPLICATION_XHTML, data_text);
		assertTrue(manager.storeContent("mynewrep:/textcontent", content, null));
		
		manager.useContentData("mynewrep:/textcontent", new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					assertEquals(data_text, contentData);
					return null;
				}
			});

		manager.useContentData("/textcontent", new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					assertNull(contentData);
					return null;
				}
			});

		manager.useContentData(ContentRepository.DEFAULT+":/textcontent", new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					assertNull(contentData);
					return null;
				}
			});
	}

	public void testStoreUnknownContentRepository()
	throws Exception
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);
		
		// text data
		final String data_text = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n"+
						   "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"+
						   "<html><head><title>my title</title></head><body></body></html>";
		Content content = new Content(MimeType.APPLICATION_XHTML, data_text);
		try
		{
			manager.storeContent("mynewrep:/textcontent", content, null);
			fail();
		}
		catch (UnknownContentRepositoryException e)
		{
			assertEquals(e.getRepositoryName(), "mynewrep");
		}
	}

	public void testCreateRepositoryIllegalArguments()
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);

		try
		{
			manager.createRepository(null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}

		try
		{
			manager.createRepository("");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}
	
	public void testContainsContentRepository()
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);
		
		assertTrue(manager.containsRepository(""));
		assertTrue(manager.containsRepository(ContentRepository.DEFAULT));
		assertFalse(manager.containsRepository("mynewrep"));
		
		manager.createRepository("mynewrep");
		assertTrue(manager.containsRepository("mynewrep"));
	}
	
	public void testContainsRepositoryIllegalArguments()
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);
		
		try
		{
			manager.containsRepository(null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}
	
	public void testStoreContentIllegalArguments()
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);

		// text data
		String data_text = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n"+
						   "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"+
						   "<html><head><title>my title</title></head><body></body></html>";
		Content content_text = new Content(MimeType.APPLICATION_XHTML, data_text);

		try
		{
			manager.storeContent(null, content_text, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}

		try
		{
			manager.storeContent("", content_text, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}

		try
		{
			manager.storeContent("notabsolute", content_text, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}

		try
		{
			manager.storeContent("default:", content_text, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}

		try
		{
			manager.storeContent("default:notabsolute", content_text, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}

		try
		{
			manager.storeContent("/nocontent", null, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testStoreContentUnsupportedMimeType()
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);

		Content content = new Content(UnsupportedMimeType.UNSUPPORTED, new Object());
		try
		{
			manager.storeContent("/thepath", content, null);
			fail();
		}
		catch (UnsupportedMimeTypeException e)
		{
			assertSame(UnsupportedMimeType.UNSUPPORTED, e.getMimeType());
		}
	}
	
	public void testUseContent()
	throws Exception
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);

		// image data
		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn.png");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);
		final byte[] data_image_png = FileUtils.readBytes(image_resource_png);

		manager.storeContent("/the/logo/of", new Content(MimeType.IMAGE_PNG, data_image_gif), null);

		manager.useContentData("/the/logo/of", new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					assertTrue(Arrays.equals(data_image_png, (byte[])contentData));
					return null;
				}
			});

		assertNull(manager.useContentData("/the/logo/of/uwyn.png", new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					fail();
					return null;
				}
			}));

		assertNull(manager.useContentData("/the/logo/of/wrongname.png", new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					fail();
					return null;
				}
			}));

		assertNull(manager.useContentData("/the/wrong/path/uwyn.png", new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					fail();
					return null;
				}
			}));
	}
	
	public void testUseContentName()
	throws Exception
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);

		// image data
		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn.png");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);
		final byte[] data_image_png = FileUtils.readBytes(image_resource_png);

		manager.storeContent("/the/logo/of", new Content(MimeType.IMAGE_PNG, data_image_gif).name("uwyn.png"), null);

		manager.useContentData("/the/logo/of", new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					assertTrue(Arrays.equals(data_image_png, (byte[])contentData));
					return null;
				}
			});

		manager.useContentData("/the/logo/of/uwyn.png", new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					assertTrue(Arrays.equals(data_image_png, (byte[])contentData));
					return null;
				}
			});

		assertNull(manager.useContentData("/the/logo/of/wrongname.png", new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					fail();
					return null;
				}
			}));

		assertNull(manager.useContentData("/the/wrong/path/uwyn.png", new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					fail();
					return null;
				}
			}));
	}

	public void testUseContentDataIllegalArguments()
	throws Exception
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);

		try
		{
			manager.useContentData(null, new ContentDataUser() {
					public Object useContentData(Object contentData)
					throws InnerClassException
					{
						return null;
					}
				});
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}

		try
		{
			manager.useContentData("", new ContentDataUser() {
					public Object useContentData(Object contentData)
					throws InnerClassException
					{
						return null;
					}
				});
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}

		try
		{
			manager.useContentData("notabsolute", new ContentDataUser() {
					public Object useContentData(Object contentData)
					throws InnerClassException
					{
						return null;
					}
				});
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}

		try
		{
			manager.useContentData("/url", null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testUseContentDataUnknown()
	throws Exception
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);

		manager.useContentData("/unknown", new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					assertNull(contentData);
					return null;
				}
			});
	}

	public void testHasContentData()
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);

		assertFalse(manager.hasContentData("/textcontent"));

		Content content = new Content(MimeType.APPLICATION_XHTML, "<p>some text</p>").fragment(true);
		manager.storeContent("/textcontent", content, null);

		assertTrue(manager.hasContentData("/textcontent"));
		assertFalse(manager.hasContentData("/textcontent/mytext.xhtml"));
	}

	public void testHasContentDataName()
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);

		assertFalse(manager.hasContentData("/textcontent"));

		Content content = new Content(MimeType.APPLICATION_XHTML, "<p>some text</p>").name("mytext.xhtml").fragment(true);
		manager.storeContent("/textcontent", content, null);

		assertTrue(manager.hasContentData("/textcontent"));
		assertTrue(manager.hasContentData("/textcontent/mytext.xhtml"));
		assertFalse(manager.hasContentData("/textcontent/unknowntext.xhtml"));
		assertFalse(manager.hasContentData("/unknowncontent/mytext.xhtml"));
	}

	public void testHasContentDataIllegalArguments()
	throws Exception
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);

		try
		{
			manager.hasContentData(null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}

		try
		{
			manager.hasContentData("");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}

		try
		{
			manager.hasContentData("notabsolute");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testDeleteContentImage()
	throws Exception
	{
		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

		DatabaseContent manager = DatabaseContentFactory.getInstance(mDatasource);

		assertFalse(manager.hasContentData("/imagecontent"));

		Content content = new Content(MimeType.IMAGE_PNG, data_image_gif);
		manager.storeContent("/imagecontent", content, null);

		DatabaseImageStore store = DatabaseImageStoreFactory.getInstance(mDatasource);
		DatabaseContentInfo info = manager.getContentInfo("/imagecontent");
		assertNotNull(info);
		manager.useContentData("/imagecontent", new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					assertNotNull(contentData);
					return null;
				}
			});
		assertTrue(manager.hasContentData("/imagecontent"));
		assertTrue(store.hasContentData(info.getContentId()));
		assertTrue(manager.deleteContent("/imagecontent"));

		assertNull(manager.getContentInfo("/imagecontent"));
		manager.useContentData("/imagecontent", new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					assertNull(contentData);
					return null;
				}
			});
 		assertFalse(manager.hasContentData("/imagecontent"));
		assertFalse(store.hasContentData(info.getContentId()));
		assertFalse(manager.deleteContent("/imagecontent"));
	}

	public void testDeleteContentText()
	{
		DatabaseContent manager = DatabaseContentFactory.getInstance(mDatasource);

		assertFalse(manager.hasContentData("/textcontent"));

		Content content = new Content(MimeType.APPLICATION_XHTML, "<p>some text</p>").fragment(true);
		manager.storeContent("/textcontent", content, null);

		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);
		DatabaseContentInfo info = manager.getContentInfo("/textcontent");
		assertNotNull(info);
		manager.useContentData("/textcontent", new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					assertNotNull(contentData);
					return null;
				}
			});
		assertTrue(manager.hasContentData("/textcontent"));
		assertTrue(store.hasContentData(info.getContentId()));
		assertTrue(manager.deleteContent("/textcontent"));

		assertNull(manager.getContentInfo("/textcontent"));
		manager.useContentData("/textcontent", new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					assertNull(contentData);
					return null;
				}
			});
 		assertFalse(manager.hasContentData("/textcontent"));
		assertFalse(store.hasContentData(info.getContentId()));
		assertFalse(manager.deleteContent("/textcontent"));
	}

	public void testDeleteContentRaw()
	throws Exception
	{
		DatabaseContent manager = DatabaseContentFactory.getInstance(mDatasource);

		int size = 1024*1024*4; // 4Mb
		final byte[] raw = new byte[size];
		for (int i = 0; i < size; i++)
		{
			raw[i] = (byte)(i%255);
		}

		assertFalse(manager.hasContentData("/rawcontent"));

		Content content = new Content(MimeType.RAW, new ByteArrayInputStream(raw));
		manager.storeContent("/rawcontent", content, null);

		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);
		DatabaseContentInfo info = manager.getContentInfo("/rawcontent");
		assertNotNull(info);
		manager.useContentData("/rawcontent", new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					assertNotNull(contentData);
					return null;
				}
			});
		assertTrue(manager.hasContentData("/rawcontent"));
		assertTrue(store.hasContentData(info.getContentId()));
		assertTrue(manager.deleteContent("/rawcontent"));

		assertNull(manager.getContentInfo("/rawcontent"));
		manager.useContentData("/rawcontent", new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					assertNull(contentData);
					return null;
				}
			});
 		assertFalse(manager.hasContentData("/rawcontent"));
		assertFalse(store.hasContentData(info.getContentId()));
		assertFalse(manager.deleteContent("/rawcontent"));
	}

	public void testDeleteContentIllegalArguments()
	throws Exception
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);

		try
		{
			manager.deleteContent(null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}

		try
		{
			manager.deleteContent("");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}

		try
		{
			manager.deleteContent("notabsolute");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testGetContentInfo()
	{
		DatabaseContent manager = DatabaseContentFactory.getInstance(mDatasource);

		assertNull(manager.getContentInfo("/textcontent"));

		Content content_text1 = new Content(MimeType.APPLICATION_XHTML, "<p>some text</p>")
			.fragment(true)
			.attribute("attr1", "value1")
			.attribute("attr2", "value2");
		manager.storeContent("/textcontent", content_text1, null);
		
		DatabaseContentInfo info1a = manager.getContentInfo("/textcontent");
		assertNotNull(info1a);
		assertTrue(info1a.getContentId() >= 0);
		assertNotNull(info1a.getCreated());
		assertTrue(info1a.getCreated().getTime() <= System.currentTimeMillis());
		assertEquals("/textcontent", info1a.getPath());
		assertEquals(MimeType.APPLICATION_XHTML.toString(), info1a.getMimeType());
		assertEquals(0, info1a.getVersion());
		assertNotNull(info1a.getAttributes());
		assertEquals(2, info1a.getAttributes().size());
		assertEquals("value1", info1a.getAttributes().get("attr1"));
		assertEquals("value2", info1a.getAttributes().get("attr2"));
		
		DatabaseContentInfo info1b = manager.getContentInfo("/textcontent/mytext.html");
		assertNull(info1b);

		Content content_text2 = new Content(MimeType.APPLICATION_XHTML, "<p>some other text</p>")
			.fragment(true);
		manager.storeContent("/textcontent", content_text2, null);
		
		DatabaseContentInfo info2a = manager.getContentInfo("/textcontent");
		assertNotNull(info2a);
		assertEquals(info1a.getContentId()+1, info2a.getContentId());
		assertNotNull(info2a.getCreated());
		assertTrue(info2a.getCreated().getTime() <= System.currentTimeMillis());
		assertEquals("/textcontent", info2a.getPath());
		assertEquals(MimeType.APPLICATION_XHTML.toString(), info2a.getMimeType());
		assertEquals(1, info2a.getVersion());
		assertNull(info2a.getAttributes());
		
		ContentInfo info2b = manager.getContentInfo("/textcontent/mytext.html");
		assertNull(info2b);
	}

	public void testGetContentInfoName()
	{
		DatabaseContent manager = DatabaseContentFactory.getInstance(mDatasource);

		assertNull(manager.getContentInfo("/textcontent"));

		Content content_text1 = new Content(MimeType.APPLICATION_XHTML, "<p>some text</p>")
			.fragment(true)
			.attribute("attr1", "value1")
			.attribute("attr2", "value2")
			.name("mytext.html");
		manager.storeContent("/textcontent", content_text1, null);
		
		DatabaseContentInfo info1a = manager.getContentInfo("/textcontent");
		assertNotNull(info1a);
		assertTrue(info1a.getContentId() >= 0);
		assertNotNull(info1a.getCreated());
		assertTrue(info1a.getCreated().getTime() <= System.currentTimeMillis());
		assertEquals("/textcontent", info1a.getPath());
		assertEquals(MimeType.APPLICATION_XHTML.toString(), info1a.getMimeType());
		assertEquals(0, info1a.getVersion());
		assertNotNull(info1a.getAttributes());
		assertEquals(2, info1a.getAttributes().size());
		assertEquals("value1", info1a.getAttributes().get("attr1"));
		assertEquals("value2", info1a.getAttributes().get("attr2"));
		
		DatabaseContentInfo info1b = manager.getContentInfo("/textcontent/mytext.html");
		assertNotNull(info1b);
		assertTrue(info1b.getContentId() >= 0);
		assertNotNull(info1b.getCreated());
		assertTrue(info1b.getCreated().getTime() <= System.currentTimeMillis());
		assertEquals("/textcontent", info1b.getPath());
		assertEquals(MimeType.APPLICATION_XHTML.toString(), info1b.getMimeType());
		assertEquals(0, info1b.getVersion());
		assertNotNull(info1b.getAttributes());
		assertEquals(2, info1b.getAttributes().size());
		assertEquals("value1", info1b.getAttributes().get("attr1"));
		assertEquals("value2", info1b.getAttributes().get("attr2"));
		
		DatabaseContentInfo info1c = manager.getContentInfo("/textcontent/unknown.html");
		assertNull(info1c);
		
		DatabaseContentInfo info1d = manager.getContentInfo("/unknowncontent/mytext.html");
		assertNull(info1d);

		Content content_text2 = new Content(MimeType.APPLICATION_XHTML, "<p>some other text</p>")
			.fragment(true);
		manager.storeContent("/textcontent", content_text2, null);
		DatabaseContentInfo info2 = manager.getContentInfo("/textcontent");
		assertNotNull(info2);
		assertEquals(info1a.getContentId()+1, info2.getContentId());
		assertNotNull(info2.getCreated());
		assertTrue(info2.getCreated().getTime() <= System.currentTimeMillis());
		assertEquals("/textcontent", info2.getPath());
		assertEquals(MimeType.APPLICATION_XHTML.toString(), info2.getMimeType());
		assertEquals(1, info2.getVersion());
		assertNull(info2.getAttributes());

		DatabaseContentInfo info2b = manager.getContentInfo("/textcontent/mytext.html");
		assertNotNull(info2b);
		
		DatabaseContentInfo info2c = manager.getContentInfo("/textcontent/unknown.html");
		assertNull(info2c);
		
		DatabaseContentInfo info2d = manager.getContentInfo("/unknowncontent/mytext.html");
		assertNull(info2d);
	}

	public void testGetContentInfoIllegalArguments()
	throws Exception
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);

		try
		{
			manager.getContentInfo(null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}

		try
		{
			manager.getContentInfo("");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}

		try
		{
			manager.getContentInfo("notabsolute");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testServeContentIllegalArgument()
	throws Exception
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);

		try
		{
			manager.serveContentData(null, "/apath");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testGetContentForHtmlInvalidPath()
	throws Exception
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);

		assertEquals("", manager.getContentForHtml(null, null, null));
		assertEquals("", manager.getContentForHtml("", null, null));
		assertEquals("", manager.getContentForHtml("notabsolute", null, null));
	}

	public void testGetContentForHtmlUnknownPath()
	throws Exception
	{
		ContentManager manager = DatabaseContentFactory.getInstance(mDatasource);

		assertEquals("", manager.getContentForHtml("/unknown", null, null));
	}
}

