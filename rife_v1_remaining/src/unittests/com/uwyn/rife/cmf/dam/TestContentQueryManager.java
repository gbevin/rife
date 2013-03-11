/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestContentQueryManager.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.cmf.dam.contentmanagers.DatabaseContent;
import com.uwyn.rife.cmf.dam.contentmanagers.DatabaseContentFactory;
import com.uwyn.rife.cmf.dam.contentmanagers.DatabaseContentInfo;
import com.uwyn.rife.cmf.dam.exceptions.*;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbQueryManager;
import com.uwyn.rife.database.queries.Select;
import com.uwyn.rife.database.querymanagers.generic.GenericQueryManager;
import com.uwyn.rife.database.querymanagers.generic.GenericQueryManagerFactory;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.tools.FileUtils;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import com.uwyn.rife.tools.exceptions.FileUtilsErrorException;

public class TestContentQueryManager extends TestCase
{
	private Datasource mDatasource = new Datasource();
	
	public TestContentQueryManager(Datasource datasource, String datasourceName, String name)
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
		DatabaseContentFactory.getInstance(mDatasource).remove();
	}

	public void testInstantiation()
	{
	    ContentQueryManager<ContentImage> manager = new ContentQueryManager<ContentImage>(mDatasource, ContentImage.class);
		assertNotNull(manager);
		assertNotNull(manager.getContentManager());
		assertTrue(manager.getContentManager() instanceof ContentManager);
	}

	public void testBuildCmfPathBean()
	throws Exception
	{
		ContentImage content = new ContentImage()
			.name("the content name");
		content
			.setId(3);

	    ContentQueryManager<ContentImage> manager = new ContentQueryManager<ContentImage>(mDatasource, ContentImage.class);
		assertEquals("/contentimage/3/image", manager.buildCmfPath(content, "image"));
	}

	public void testBuildCmfPathId()
	throws Exception
	{
	    ContentQueryManager<ContentImage> manager = new ContentQueryManager<ContentImage>(mDatasource, ContentImage.class);
		assertEquals("/contentimage/4/image", manager.buildCmfPath(4, "image"));
	}

	public void testBuildCmfPathBeanRepository()
	throws Exception
	{
		ContentImageRepository content = new ContentImageRepository()
			.name("the content name");
		content
			.setId(3);

	    ContentQueryManager<ContentImageRepository> manager = new ContentQueryManager<ContentImageRepository>(mDatasource, ContentImageRepository.class);
		assertEquals("testrep:/contentimagerepository/3/image", manager.buildCmfPath(content, "image"));
	}

	public void testBuildCmfPathIdRepository()
	throws Exception
	{
	    ContentQueryManager<ContentImageRepository> manager = new ContentQueryManager<ContentImageRepository>(mDatasource, ContentImageRepository.class);
		assertEquals("testrep:/contentimagerepository/4/image", manager.buildCmfPath(4, "image"));
	}
	
	public void testBuildServeContentPathBean()
	throws Exception
	{
		ContentImage content = new ContentImage()
			.name("the content name");
		content
			.setId(3);
		
	    ContentQueryManager<ContentImage> manager = new ContentQueryManager<ContentImage>(mDatasource, ContentImage.class);
		assertEquals("/contentimage/3/image", manager.buildServeContentPath(content, "image"));
	}
	
	public void testBuildServeContentPathId()
	throws Exception
	{
	    ContentQueryManager<ContentImage> manager = new ContentQueryManager<ContentImage>(mDatasource, ContentImage.class);
		assertEquals("/contentimage/4/image", manager.buildServeContentPath(4, "image"));
	}
	
	public void testBuildServeContentPathBeanRepository()
	throws Exception
	{
		ContentImageRepository content = new ContentImageRepository()
			.name("the content name");
		content
			.setId(3);
		
	    ContentQueryManager<ContentImageRepository> manager = new ContentQueryManager<ContentImageRepository>(mDatasource, ContentImageRepository.class);
		assertEquals("/contentimagerepository/3/image", manager.buildServeContentPath(content, "image"));
	}
	
	public void testBuildServeContentPathIdRepository()
	throws Exception
	{
	    ContentQueryManager<ContentImageRepository> manager = new ContentQueryManager<ContentImageRepository>(mDatasource, ContentImageRepository.class);
		assertEquals("/contentimagerepository/4/image", manager.buildServeContentPath(4, "image"));
	}
	
	public void testSaveContent()
	throws Exception
	{
	    ContentQueryManager<ContentImage> manager = new ContentQueryManager<ContentImage>(mDatasource, ContentImage.class);
		manager.install();
		try
		{
			URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
			byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);
			ContentImage content = new ContentImage()
				.name("the content name")
				.image(data_image_gif);

			int id = manager.save(content);
			assertTrue(id >= 0);

			DatabaseContent content_manager = DatabaseContentFactory.getInstance(mDatasource);
			String path = manager.buildCmfPath(content, "image");

			GenericQueryManager<ContentImage> gqm = GenericQueryManagerFactory.getInstance(mDatasource, ContentImage.class);
			ContentImage restored = gqm.restore(id);
			assertEquals(content.getId(), restored.getId());
			assertEquals(content.getName(), restored.getName());
			assertNull(restored.getImage());

			DatabaseContentInfo info = content_manager.getContentInfo(path);
			assertEquals(id, info.getContentId());
			assertEquals(MimeType.IMAGE_PNG, info.getMimeType());
			assertEquals("myimage.png", info.getName());

			URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn.png");
			final byte[] data_image_png = FileUtils.readBytes(image_resource_png);

			content_manager.useContentData(path, new ContentDataUser() {
					public Object useContentData(Object contentData)
					throws InnerClassException
					{
						assertTrue(Arrays.equals(data_image_png, (byte[])contentData));
						return null;
					}
				});
		}
		finally
		{
			manager.remove();
		}
	}
	
	public void testSaveContentOtherTable()
	throws Exception
	{
	    ContentQueryManager<ContentImage> manager = new ContentQueryManager<ContentImage>(mDatasource, ContentImage.class, "othercontentimage");
		manager.install();
		try
		{
			URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
			byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);
			ContentImage content = new ContentImage()
				.name("the content name")
				.image(data_image_gif);
			
			int id = manager.save(content);
			assertTrue(id >= 0);
			
			DatabaseContent content_manager = DatabaseContentFactory.getInstance(mDatasource);
			String path = manager.buildCmfPath(content, "image");
			assertTrue(path.startsWith("/contentimage/"));
			
			GenericQueryManager<ContentImage> gqm = GenericQueryManagerFactory.getInstance(mDatasource, ContentImage.class, "othercontentimage");
			ContentImage restored = gqm.restore(id);
			assertEquals(content.getId(), restored.getId());
			assertEquals(content.getName(), restored.getName());
			assertNull(restored.getImage());
			assertEquals(1, new DbQueryManager(mDatasource)
				.executeGetFirstInt(new Select(mDatasource)
											 .field("count(*)")
											 .from("othercontentimage")));
			
			DatabaseContentInfo info = content_manager.getContentInfo(path);
			assertEquals(id, info.getContentId());
			assertEquals(MimeType.IMAGE_PNG, info.getMimeType());
			assertEquals("myimage.png", info.getName());
			
			URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn.png");
			final byte[] data_image_png = FileUtils.readBytes(image_resource_png);
			
			content_manager.useContentData(path, new ContentDataUser() {
					public Object useContentData(Object contentData)
					throws InnerClassException
					{
						assertTrue(Arrays.equals(data_image_png, (byte[])contentData));
						return null;
					}
				});
		}
		finally
		{
			manager.remove();
		}
	}
	
	public void testSaveContentRepository()
	throws Exception
	{
	    ContentQueryManager<ContentImageRepository> manager = new ContentQueryManager<ContentImageRepository>(mDatasource, ContentImageRepository.class);
		manager.install();
		manager.getContentManager().createRepository("testrep");
		try
		{
			URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
			byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);
			ContentImageRepository content = new ContentImageRepository()
				.name("the content name")
				.image(data_image_gif);

			int id = manager.save(content);
			assertTrue(id >= 0);

			checkContentRepository(id, "testrep");

			DatabaseContent content_manager = DatabaseContentFactory.getInstance(mDatasource);
			String path = manager.buildCmfPath(content, "image");
			assertTrue(path.startsWith("testrep:"));

			GenericQueryManager<ContentImageRepository> gqm = GenericQueryManagerFactory.getInstance(mDatasource, ContentImageRepository.class);
			ContentImageRepository restored = gqm.restore(id);
			assertEquals(content.getId(), restored.getId());
			assertEquals(content.getName(), restored.getName());
			assertNull(restored.getImage());

			DatabaseContentInfo info = content_manager.getContentInfo(path);
			assertEquals(id, info.getContentId());
			assertEquals(MimeType.IMAGE_PNG, info.getMimeType());
			assertEquals("myimage.png", info.getName());

			URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn.png");
			final byte[] data_image_png = FileUtils.readBytes(image_resource_png);

			content_manager.useContentData(path, new ContentDataUser() {
					public Object useContentData(Object contentData)
					throws InnerClassException
					{
						assertTrue(Arrays.equals(data_image_png, (byte[])contentData));
						return null;
					}
				});
		}
		finally
		{
			manager.remove();
		}
	}

	public void testSaveContentRaw()
	throws Exception
	{
	    ContentQueryManager<ContentRaw> manager = new ContentQueryManager<ContentRaw>(mDatasource, ContentRaw.class);
		manager.install();
		try
		{
			int size = 1024*1024*4; // 4Mb
			final byte[] raw = new byte[size];
			for (int i = 0; i < size; i++)
			{
				raw[i] = (byte)(i%255);
			}

			ContentRaw content = new ContentRaw()
				.name("the content name")
				.raw(new ByteArrayInputStream(raw));

			int id = manager.save(content);
			assertTrue(id >= 0);

			DatabaseContent content_manager = DatabaseContentFactory.getInstance(mDatasource);
			String path = manager.buildCmfPath(content, "raw");

			GenericQueryManager<ContentRaw> gqm = GenericQueryManagerFactory.getInstance(mDatasource, ContentRaw.class);
			ContentRaw restored = gqm.restore(id);
			assertEquals(content.getId(), restored.getId());
			assertEquals(content.getName(), restored.getName());
			assertNull(restored.getRaw());

			DatabaseContentInfo info = content_manager.getContentInfo(path);
			assertEquals(id, info.getContentId());
			assertEquals(MimeType.RAW, info.getMimeType());

			content_manager.useContentData(path, new ContentDataUser() {
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
		finally
		{
			manager.remove();
		}
	}

	public void testSaveContentUpdate()
	throws Exception
	{
		ContentQueryManager<ContentImage> manager = new ContentQueryManager<ContentImage>(mDatasource, ContentImage.class);
		manager.install();
		try
		{
			URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
			byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

			ContentImage content = new ContentImage()
				.name("the content name")
				.image(data_image_gif);

			int id = manager.save(content);
			assertTrue(id >= 0);

			URL rife_resource_tif= ResourceFinderClasspath.getInstance().getResource("rife-logo_small.tif");
			byte[] rife_image_tif = FileUtils.readBytes(rife_resource_tif);
			content
				.name("updated content name")
				.image(rife_image_tif);

			manager.save(content);

			DatabaseContent content_manager = DatabaseContentFactory.getInstance(mDatasource);
			String path = manager.buildCmfPath(content, "image");

			GenericQueryManager<ContentImage> gqm = GenericQueryManagerFactory.getInstance(mDatasource, ContentImage.class);
			ContentImage restored = gqm.restore(id);
			assertEquals(content.getId(), restored.getId());
			assertEquals(content.getName(), restored.getName());
			assertNull(restored.getImage());

			DatabaseContentInfo info = content_manager.getContentInfo(path);
			assertEquals(id+1, info.getContentId());
			assertEquals(MimeType.IMAGE_PNG, info.getMimeType());

			URL rife_resource_png = ResourceFinderClasspath.getInstance().getResource("rife-logo_small.png");
			final byte[] rife_image_png = FileUtils.readBytes(rife_resource_png);

			content_manager.useContentData(path, new ContentDataUser() {
					public Object useContentData(Object contentData)
					throws InnerClassException
					{
						assertTrue(Arrays.equals(rife_image_png, (byte[])contentData));
						return null;
					}
				});
		}
		finally
		{
			manager.remove();
		}
	}

	public void testSaveContentUpdateRepository()
	throws Exception
	{
		ContentQueryManager<ContentImageRepository> manager = new ContentQueryManager<ContentImageRepository>(mDatasource, ContentImageRepository.class);
		manager.install();
		manager.getContentManager().createRepository("testrep");
		try
		{
			URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
			byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

			ContentImageRepository content = new ContentImageRepository()
				.name("the content name")
				.image(data_image_gif);

			int id = manager.save(content);
			assertTrue(id >= 0);

			checkContentRepository(id, "testrep");

			URL rife_resource_tif= ResourceFinderClasspath.getInstance().getResource("rife-logo_small.tif");
			byte[] rife_image_tif = FileUtils.readBytes(rife_resource_tif);
			content
				.name("updated content name")
				.image(rife_image_tif);

			manager.save(content);

			DatabaseContent content_manager = DatabaseContentFactory.getInstance(mDatasource);
			String path = manager.buildCmfPath(content, "image");
			assertTrue(path.startsWith("testrep:"));

			GenericQueryManager<ContentImageRepository> gqm = GenericQueryManagerFactory.getInstance(mDatasource, ContentImageRepository.class);
			ContentImageRepository restored = gqm.restore(id);
			assertEquals(content.getId(), restored.getId());
			assertEquals(content.getName(), restored.getName());
			assertNull(restored.getImage());

			DatabaseContentInfo info = content_manager.getContentInfo(path);
			assertEquals(id+1, info.getContentId());
			assertEquals(MimeType.IMAGE_PNG, info.getMimeType());

			checkContentRepository(info.getContentId(), "testrep");

			URL rife_resource_png = ResourceFinderClasspath.getInstance().getResource("rife-logo_small.png");
			final byte[] rife_image_png = FileUtils.readBytes(rife_resource_png);

			content_manager.useContentData(path, new ContentDataUser() {
					public Object useContentData(Object contentData)
					throws InnerClassException
					{
						assertTrue(Arrays.equals(rife_image_png, (byte[])contentData));
						return null;
					}
				});
		}
		finally
		{
			manager.remove();
		}
	}

	public void testSavePojo()
	throws Exception
	{
	    ContentQueryManager<RegularPojo> manager = new ContentQueryManager<RegularPojo>(mDatasource, RegularPojo.class);
		manager.install();
		try
		{
			RegularPojo content = new RegularPojo()
				.name("the regular pojo name");

			int id = manager.save(content);
			assertTrue(id >= 0);

			GenericQueryManager<RegularPojo> gqm = GenericQueryManagerFactory.getInstance(mDatasource, RegularPojo.class);
			RegularPojo restored = gqm.restore(id);
			assertEquals(content.getId(), restored.getId());
			assertEquals(content.getName(), restored.getName());
		}
		finally
		{
			manager.remove();
		}
	}

	public void testStoreEmptyContent()
	throws Exception
	{
	    ContentQueryManager<ContentImage> manager = new ContentQueryManager<ContentImage>(mDatasource, ContentImage.class);
		manager.install();
		try
		{
			URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
			byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);
			ContentImage content = new ContentImage()
				.name("the content name")
				.image(data_image_gif);

			int id = manager.save(content);
			assertTrue(id >= 0);

			DatabaseContent content_manager = DatabaseContentFactory.getInstance(mDatasource);
			String path = manager.buildCmfPath(content, "image");

			assertTrue(content_manager.hasContentData(path));
			content_manager.useContentData(path, new ContentDataUser() {
					public Object useContentData(Object contentData)
					throws InnerClassException
					{
						assertNotNull(contentData);
						return null;
					}
				});

			assertTrue(manager.storeEmptyContent(content, "image"));

			GenericQueryManager<ContentImage> gqm = GenericQueryManagerFactory.getInstance(mDatasource, ContentImage.class);
			ContentImage restored = gqm.restore(id);
			assertEquals(content.getId(), restored.getId());
			assertEquals(content.getName(), restored.getName());
			assertNull(restored.getImage());

			DatabaseContentInfo info = content_manager.getContentInfo(path);
			assertEquals(id+1, info.getContentId());
			assertEquals(MimeType.IMAGE_PNG, info.getMimeType());

			assertFalse(content_manager.hasContentData(path));
			content_manager.useContentData(path, new ContentDataUser() {
					public Object useContentData(Object contentData)
					throws InnerClassException
					{
						assertNull(contentData);
						return null;
					}
				});
		}
		finally
		{
			manager.remove();
		}
	}

	public void testStoreEmptyContentRepository()
	throws Exception
	{
	    ContentQueryManager<ContentImageRepository> manager = new ContentQueryManager<ContentImageRepository>(mDatasource, ContentImageRepository.class);
		manager.install();
		manager.getContentManager().createRepository("testrep");
		try
		{
			URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
			byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);
			ContentImageRepository content = new ContentImageRepository()
				.name("the content name")
				.image(data_image_gif);

			int id = manager.save(content);
			assertTrue(id >= 0);

			checkContentRepository(id, "testrep");

			DatabaseContent content_manager = DatabaseContentFactory.getInstance(mDatasource);
			String path = manager.buildCmfPath(content, "image");
			assertTrue(path.startsWith("testrep:"));

			assertTrue(content_manager.hasContentData(path));
			content_manager.useContentData(path, new ContentDataUser() {
					public Object useContentData(Object contentData)
					throws InnerClassException
					{
						assertNotNull(contentData);
						return null;
					}
				});

			assertTrue(manager.storeEmptyContent(content, "image"));

			GenericQueryManager<ContentImageRepository> gqm = GenericQueryManagerFactory.getInstance(mDatasource, ContentImageRepository.class);
			ContentImageRepository restored = gqm.restore(id);
			assertEquals(content.getId(), restored.getId());
			assertEquals(content.getName(), restored.getName());
			assertNull(restored.getImage());

			DatabaseContentInfo info = content_manager.getContentInfo(path);
			assertEquals(id+1, info.getContentId());
			assertEquals(MimeType.IMAGE_PNG, info.getMimeType());

			assertFalse(content_manager.hasContentData(path));
			content_manager.useContentData(path, new ContentDataUser() {
					public Object useContentData(Object contentData)
					throws InnerClassException
					{
						assertNull(contentData);
						return null;
					}
				});
		}
		finally
		{
			manager.remove();
		}
	}

	private void checkContentRepository(int id, String repository)
	{
		assertEquals(repository, new DbQueryManager(mDatasource).executeGetFirstString(new Select(mDatasource)
			.from(RifeConfig.Cmf.getTableContentInfo()+" i")
			.join(RifeConfig.Cmf.getTableContentRepository()+" r")
			.field("r.name")
			.where("contentId", "=", id)
			.whereAnd("i.repositoryId = r.repositoryId")));
	}

	public void testStoreEmptyContentIllegalArguments()
	throws Exception
	{
	    ContentQueryManager<ContentImage> manager = new ContentQueryManager<ContentImage>(mDatasource, ContentImage.class);
		manager.install();
		try
		{
			try
			{
				manager.storeEmptyContent(null, "image");
				fail();
			}
			catch (IllegalArgumentException e)
			{
				assertTrue(true);
			}

			try
			{
				manager.storeEmptyContent(new ContentImage(), null);
				fail();
			}
			catch (IllegalArgumentException e)
			{
				assertTrue(true);
			}

			try
			{
				manager.storeEmptyContent(new ContentImage(), "");
				fail();
			}
			catch (IllegalArgumentException e)
			{
				assertTrue(true);
			}
		}
		finally
		{
			manager.remove();
		}
	}

	public void testStoreEmptyContentPojo()
	throws Exception
	{
	    ContentQueryManager<RegularPojo> manager = new ContentQueryManager<RegularPojo>(mDatasource, RegularPojo.class);
		manager.install();
		try
		{
			RegularPojo content = new RegularPojo()
				.name("the regular pojo name");

			assertFalse(manager.storeEmptyContent(content, "name"));
		}
		finally
		{
			manager.remove();
		}
	}

	public void testStoreEmptyContentMissingIdentifier()
	throws Exception
	{
	    ContentQueryManager<ContentImage> manager = new ContentQueryManager<ContentImage>(mDatasource, ContentImage.class);
		manager.install();
		try
		{
			try
			{
				manager.storeEmptyContent(new ContentImage(), "image");
				fail();
			}
			catch (MissingIdentifierValueException e)
			{
				assertSame(ContentImage.class, e.getBeanClass());
				assertEquals("id", e.getIdentifierName());
			}
		}
		finally
		{
			manager.remove();
		}
	}

	public void testStoreEmptyContentUnknownProperty()
	throws Exception
	{
	    ContentQueryManager<ContentImage> manager = new ContentQueryManager<ContentImage>(mDatasource, ContentImage.class);
		manager.install();
		try
		{
			URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
			byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);
			ContentImage content = new ContentImage()
				.name("the content name")
				.image(data_image_gif);

			manager.save(content);

			try
			{
				manager.storeEmptyContent(content, "imageunknown");
				fail();
			}
			catch (UnknownConstrainedPropertyException e)
			{
				assertSame(ContentImage.class, e.getBeanClass());
				assertEquals("imageunknown", e.getProperty());
			}
		}
		finally
		{
			manager.remove();
		}
	}

	public void testStoreEmptyContentMimeTypeExpected()
	throws Exception
	{
	    ContentQueryManager<ContentImage> manager = new ContentQueryManager<ContentImage>(mDatasource, ContentImage.class);
		manager.install();
		try
		{
			URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
			byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);
			ContentImage content = new ContentImage()
				.name("the content name")
				.image(data_image_gif);

			manager.save(content);

			try
			{
				manager.storeEmptyContent(content, "name");
				fail();
			}
			catch (ExpectedMimeTypeConstraintException e)
			{
				assertSame(ContentImage.class, e.getBeanClass());
				assertEquals("name", e.getProperty());
			}
		}
		finally
		{
			manager.remove();
		}
	}

	public void testDeleteContent()
	throws Exception
	{
	    ContentQueryManager<ContentImage> manager = new ContentQueryManager<ContentImage>(mDatasource, ContentImage.class);
		manager.install();
		try
		{
			URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
			byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);
			ContentImage content = new ContentImage()
				.name("the content name")
				.image(data_image_gif);

			int id = manager.save(content);
			assertTrue(id >= 0);

			assertTrue(manager.delete(id));

			ContentManager content_manager = DatabaseContentFactory.getInstance(mDatasource);
			String path = manager.buildCmfPath(content, "image");

			GenericQueryManager<ContentImage> gqm = GenericQueryManagerFactory.getInstance(mDatasource, ContentImage.class);
			assertNull(gqm.restore(id));
			assertNull(content_manager.getContentInfo(path));
			assertFalse(content_manager.hasContentData(path));
			content_manager.useContentData(path, new ContentDataUser() {
					public Object useContentData(Object contentData)
					throws InnerClassException
					{
						assertNull(contentData);
						return null;
					}
				});
		}
		finally
		{
			manager.remove();
		}
	}

	public void testDeleteContentRepository()
	throws Exception
	{
	    ContentQueryManager<ContentImageRepository> manager = new ContentQueryManager<ContentImageRepository>(mDatasource, ContentImageRepository.class);
		manager.install();
		manager.getContentManager().createRepository("testrep");
		try
		{
			URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
			byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);
			ContentImageRepository content = new ContentImageRepository()
				.name("the content name")
				.image(data_image_gif);

			int id = manager.save(content);
			assertTrue(id >= 0);

			checkContentRepository(id, "testrep");

			assertTrue(manager.delete(id));

			ContentManager content_manager = DatabaseContentFactory.getInstance(mDatasource);
			String path = manager.buildCmfPath(content, "image");
			assertTrue(path.startsWith("testrep:"));

			GenericQueryManager<ContentImageRepository> gqm = GenericQueryManagerFactory.getInstance(mDatasource, ContentImageRepository.class);
			assertNull(gqm.restore(id));
			assertNull(content_manager.getContentInfo(path));
			assertFalse(content_manager.hasContentData(path));
			content_manager.useContentData(path, new ContentDataUser() {
					public Object useContentData(Object contentData)
					throws InnerClassException
					{
						assertNull(contentData);
						return null;
					}
				});
		}
		finally
		{
			manager.remove();
		}
	}

	public void testDeleteContentNonCmfProperty()
	throws Exception
	{
	    ContentQueryManager<ContentImageNonCmfProps> manager = new ContentQueryManager<ContentImageNonCmfProps>(mDatasource, ContentImageNonCmfProps.class);
		manager.install();
		try
		{
			URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
			byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);
			ContentImageNonCmfProps content = new ContentImageNonCmfProps()
				.name("the content name")
				.image(data_image_gif);

			int id = manager.save(content);
			assertTrue(id >= 0);

			assertTrue(manager.delete(id));

			ContentManager content_manager = DatabaseContentFactory.getInstance(mDatasource);
			String path = manager.buildCmfPath(content, "image");

			GenericQueryManager<ContentImageNonCmfProps> gqm = GenericQueryManagerFactory.getInstance(mDatasource, ContentImageNonCmfProps.class);
			assertNull(gqm.restore(id));
			assertNull(content_manager.getContentInfo(path));
			assertFalse(content_manager.hasContentData(path));
			content_manager.useContentData(path, new ContentDataUser() {
					public Object useContentData(Object contentData)
					throws InnerClassException
					{
						assertNull(contentData);
						return null;
					}
				});
		}
		finally
		{
			manager.remove();
		}
	}

	public void testDeletePojo()
	throws Exception
	{
	    ContentQueryManager<RegularPojo> manager = new ContentQueryManager<RegularPojo>(mDatasource, RegularPojo.class);
		manager.install();
		try
		{
			RegularPojo content = new RegularPojo()
				.name("the regular pojo name");

			int id = manager.save(content);
			assertTrue(id >= 0);

			assertTrue(manager.delete(id));

			GenericQueryManager<RegularPojo> gqm = GenericQueryManagerFactory.getInstance(mDatasource, RegularPojo.class);
			assertNull(gqm.restore(id));
		}
		finally
		{
			manager.remove();
		}
	}

	public void testDeleteOrdinal()
	throws Exception
	{
	    ContentQueryManager<Ordered> manager = new ContentQueryManager<Ordered>(mDatasource, Ordered.class);
		manager.install();
		try
		{
			Ordered content1 = new Ordered()
				.name("the content name");

			int id1 = manager.save(content1);
			assertTrue(id1 >= 0);

			Ordered content2 = new Ordered()
				.name("another content name");

			int id2 = manager.save(content2);
			assertTrue(id2 > id1);

			Ordered content3 = new Ordered()
				.name("one more content name");

			int id3 = manager.save(content3);
			assertTrue(id3 > id2);

			assertTrue(manager.delete(id2));

			GenericQueryManager<Ordered> gqm = GenericQueryManagerFactory.getInstance(mDatasource, Ordered.class);
			content1 = gqm.restore(id1);
			assertEquals(0, content1.getPriority());
			assertNull(gqm.restore(id2));
			content3 = gqm.restore(id3);
			assertEquals(1, content3.getPriority());
		}
		finally
		{
			manager.remove();
		}
	}

	public void testDeleteOrdinalRestricted()
	throws Exception
	{
	    ContentQueryManager<OrderedRestricted> manager = new ContentQueryManager<OrderedRestricted>(mDatasource, OrderedRestricted.class);
		manager.install();
		try
		{
			OrderedRestricted content1 = new OrderedRestricted()
				.name("the content name")
				.restricted(3);

			int id1 = manager.save(content1);
			assertTrue(id1 >= 0);

			OrderedRestricted content2 = new OrderedRestricted()
				.name("another content name")
				.restricted(5);

			int id2 = manager.save(content2);
			assertTrue(id2 > id1);

			OrderedRestricted content3 = new OrderedRestricted()
				.name("some other content name")
				.restricted(3);

			int id3 = manager.save(content3);
			assertTrue(id3 > id2);

			OrderedRestricted content4 = new OrderedRestricted()
				.name("yet one more content name")
				.restricted(3);

			int id4 = manager.save(content4);
			assertTrue(id4 > id3);

			OrderedRestricted content5 = new OrderedRestricted()
				.name("the last content name")
				.restricted(5);

			int id5 = manager.save(content5);
			assertTrue(id5 > id4);

			assertTrue(manager.delete(id3));

			GenericQueryManager<OrderedRestricted> gqm = GenericQueryManagerFactory.getInstance(mDatasource, OrderedRestricted.class);
			content1 = gqm.restore(id1);
			assertEquals(0, content1.getPriority());
			content2 = gqm.restore(id2);
			assertEquals(0, content2.getPriority());
			assertNull(gqm.restore(id3));
			content4 = gqm.restore(id4);
			assertEquals(1, content4.getPriority());
			content5 = gqm.restore(id5);
			assertEquals(1, content5.getPriority());
		}
		finally
		{
			manager.remove();
		}
	}

	public void testDeleteNotPresent()
	throws Exception
	{
	    ContentQueryManager<RegularPojo> manager = new ContentQueryManager<RegularPojo>(mDatasource, RegularPojo.class);
		manager.install();
		try
		{
			assertFalse(manager.delete(3));
		}
		finally
		{
			manager.remove();
		}
	}

	public void testDeleteContentUnknownId()
	throws Exception
	{
	    ContentQueryManager<ContentImage> manager = new ContentQueryManager<ContentImage>(mDatasource, ContentImage.class);
		manager.install();
		try
		{
			assertFalse(manager.delete(3));
		}
		finally
		{
			manager.remove();
		}
	}

	public void testSaveOrdinal()
	throws Exception
	{
	    ContentQueryManager<Ordered> manager = new ContentQueryManager<Ordered>(mDatasource, Ordered.class);
		manager.install();
		try
		{
			Ordered content1 = new Ordered()
				.name("the content name");

			int id1 = manager.save(content1);
			assertTrue(id1 >= 0);

			Ordered content2 = new Ordered()
				.name("another content name");

			int id2 = manager.save(content2);
			assertTrue(id2 > id1);

			Ordered content3 = new Ordered()
				.name("one more content name");

			int id3 = manager.save(content3);
			assertTrue(id3 > id2);

			GenericQueryManager<Ordered> gqm = GenericQueryManagerFactory.getInstance(mDatasource, Ordered.class);
			content1 = gqm.restore(id1);
			assertEquals(0, content1.getPriority());
			content2 = gqm.restore(id2);
			assertEquals(1, content2.getPriority());
			content3 = gqm.restore(id3);
			assertEquals(2, content3.getPriority());
		}
		finally
		{
			manager.remove();
		}
	}

	public void testSaveOrdinalRestricted()
	throws Exception
	{
	    ContentQueryManager<OrderedRestricted> manager = new ContentQueryManager<OrderedRestricted>(mDatasource, OrderedRestricted.class);
		manager.install();
		try
		{
			OrderedRestricted content1 = new OrderedRestricted()
				.name("the content name")
				.restricted(3);

			int id1 = manager.save(content1);
			assertTrue(id1 >= 0);

			OrderedRestricted content2 = new OrderedRestricted()
				.name("another content name")
				.restricted(5);

			int id2 = manager.save(content2);
			assertTrue(id2 > id1);

			OrderedRestricted content3 = new OrderedRestricted()
				.name("some other content name")
				.restricted(3);

			int id3 = manager.save(content3);
			assertTrue(id3 > id2);

			OrderedRestricted content4 = new OrderedRestricted()
				.name("yet one more content name")
				.restricted(3);

			int id4 = manager.save(content4);
			assertTrue(id4 > id3);

			OrderedRestricted content5 = new OrderedRestricted()
				.name("the last content name")
				.restricted(5);

			int id5 = manager.save(content5);
			assertTrue(id5 > id4);


			GenericQueryManager<OrderedRestricted> gqm = GenericQueryManagerFactory.getInstance(mDatasource, OrderedRestricted.class);
			content1 = gqm.restore(id1);
			assertEquals(0, content1.getPriority());
			content2 = gqm.restore(id2);
			assertEquals(0, content2.getPriority());
			content3 = gqm.restore(id3);
			assertEquals(1, content3.getPriority());
			content4 = gqm.restore(id4);
			assertEquals(2, content4.getPriority());
			content5 = gqm.restore(id5);
			assertEquals(1, content5.getPriority());
		}
		finally
		{
			manager.remove();
		}
	}

	public void testMoveOrdinal()
	throws Exception
	{
	    ContentQueryManager<Ordered> manager = new ContentQueryManager<Ordered>(mDatasource, Ordered.class);
		manager.install();
		try
		{
			Ordered content1 = new Ordered()
				.name("the content name");
			Ordered content2 = new Ordered()
				.name("another content name");
			Ordered content3 = new Ordered()
				.name("one more content name");

			manager.save(content1);
			manager.save(content2);
			manager.save(content3);

			GenericQueryManager<Ordered> gqm = GenericQueryManagerFactory.getInstance(mDatasource, Ordered.class);
			content1 = gqm.restore(content1.getId());
			assertEquals(0, content1.getPriority());
			content2 = gqm.restore(content2.getId());
			assertEquals(1, content2.getPriority());
			content3 = gqm.restore(content3.getId());
			assertEquals(2, content3.getPriority());

			assertTrue(manager.move(content1, "priority", OrdinalManager.DOWN));

			content1 = gqm.restore(content1.getId());
			assertEquals(1, content1.getPriority());
			content2 = gqm.restore(content2.getId());
			assertEquals(0, content2.getPriority());
			content3 = gqm.restore(content3.getId());
			assertEquals(2, content3.getPriority());

			assertTrue(manager.move(content3, "priority", OrdinalManager.UP));

			content1 = gqm.restore(content1.getId());
			assertEquals(2, content1.getPriority());
			content2 = gqm.restore(content2.getId());
			assertEquals(0, content2.getPriority());
			content3 = gqm.restore(content3.getId());
			assertEquals(1, content3.getPriority());

			manager.up(content2, "priority");

			content1 = gqm.restore(content1.getId());
			assertEquals(2, content1.getPriority());
			content2 = gqm.restore(content2.getId());
			assertEquals(0, content2.getPriority());
			content3 = gqm.restore(content3.getId());
			assertEquals(1, content3.getPriority());

			manager.down(content1, "priority");

			content1 = gqm.restore(content1.getId());
			assertEquals(2, content1.getPriority());
			content2 = gqm.restore(content2.getId());
			assertEquals(0, content2.getPriority());
			content3 = gqm.restore(content3.getId());
			assertEquals(1, content3.getPriority());
		}
		finally
		{
			manager.remove();
		}
	}

	public void testMoveIllegalArguments()
	throws Exception
	{
	    ContentQueryManager<Ordered> manager = new ContentQueryManager<Ordered>(mDatasource, Ordered.class);
		manager.install();
		try
		{
			try
			{
				manager.move(null, "priority", OrdinalManager.UP);
				fail();
			}
			catch (IllegalArgumentException e)
			{
				assertTrue(true);
			}

			try
			{
				manager.move(new Ordered(), null, OrdinalManager.UP);
				fail();
			}
			catch (IllegalArgumentException e)
			{
				assertTrue(true);
			}

			try
			{
				manager.move(new Ordered(), "", OrdinalManager.UP);
				fail();
			}
			catch (IllegalArgumentException e)
			{
				assertTrue(true);
			}
		}
		finally
		{
			manager.remove();
		}
	}

	public void testMoveUnknownProperty()
	throws Exception
	{
	    ContentQueryManager<Ordered> manager = new ContentQueryManager<Ordered>(mDatasource, Ordered.class);
		manager.install();
		try
		{
			try
			{
				manager.move(new Ordered(), "priorityunknown", OrdinalManager.UP);
				fail();
			}
			catch (UnknownConstrainedPropertyException e)
			{
				assertSame(Ordered.class, e.getBeanClass());
				assertEquals("priorityunknown", e.getProperty());
			}
		}
		finally
		{
			manager.remove();
		}
	}

	public void testMoveNotOrdinalConstraint()
	throws Exception
	{
	    ContentQueryManager<ContentImage> manager = new ContentQueryManager<ContentImage>(mDatasource, ContentImage.class);
		manager.install();
		try
		{
			try
			{
				manager.move(new ContentImage(), "name", OrdinalManager.UP);
				fail();
			}
			catch (ExpectedOrdinalConstraintException e)
			{
				assertSame(ContentImage.class, e.getBeanClass());
				assertEquals("name", e.getProperty());
			}
		}
		finally
		{
			manager.remove();
		}
	}

	public void testMoveNotOrdinalInvalidOrdinalType()
	throws Exception
	{
	    ContentQueryManager<OrderedInvalidType> manager = new ContentQueryManager<OrderedInvalidType>(mDatasource, OrderedInvalidType.class);
		manager.install();
		try
		{
			try
			{
				manager.move(new OrderedInvalidType(), "priority", OrdinalManager.UP);
				fail();
			}
			catch (InvalidOrdinalTypeException e)
			{
				assertSame(OrderedInvalidType.class, e.getBeanClass());
				assertEquals("priority", e.getProperty());
			}
		}
		finally
		{
			manager.remove();
		}
	}

	public void testMoveOrdinalRestricted()
	throws Exception
	{
	    ContentQueryManager<OrderedRestricted> manager = new ContentQueryManager<OrderedRestricted>(mDatasource, OrderedRestricted.class);
		manager.install();
		try
		{
			OrderedRestricted content1 = new OrderedRestricted()
				.name("the content name")
				.restricted(3);

			OrderedRestricted content2 = new OrderedRestricted()
				.name("another content name")
				.restricted(5);

			OrderedRestricted content3 = new OrderedRestricted()
				.name("some other content name")
				.restricted(3);

			OrderedRestricted content4 = new OrderedRestricted()
				.name("yet one more content name")
				.restricted(3);

			OrderedRestricted content5 = new OrderedRestricted()
				.name("the last content name")
				.restricted(5);

			manager.save(content1);
			manager.save(content2);
			manager.save(content3);
			manager.save(content4);
			manager.save(content5);

			GenericQueryManager<OrderedRestricted> gqm = GenericQueryManagerFactory.getInstance(mDatasource, OrderedRestricted.class);
			content1 = gqm.restore(content1.getId());
			assertEquals(0, content1.getPriority());
			content2 = gqm.restore(content2.getId());
			assertEquals(0, content2.getPriority());
			content3 = gqm.restore(content3.getId());
			assertEquals(1, content3.getPriority());
			content4 = gqm.restore(content4.getId());
			assertEquals(2, content4.getPriority());
			content5 = gqm.restore(content5.getId());
			assertEquals(1, content5.getPriority());

			assertTrue(manager.move(content1, "priority", OrdinalManager.DOWN));

			content1 = gqm.restore(content1.getId());
			assertEquals(1, content1.getPriority());
			content2 = gqm.restore(content2.getId());
			assertEquals(0, content2.getPriority());
			content3 = gqm.restore(content3.getId());
			assertEquals(0, content3.getPriority());
			content4 = gqm.restore(content4.getId());
			assertEquals(2, content4.getPriority());
			content5 = gqm.restore(content5.getId());
			assertEquals(1, content5.getPriority());

			assertTrue(manager.move(content4, "priority", OrdinalManager.UP));

			content1 = gqm.restore(content1.getId());
			assertEquals(2, content1.getPriority());
			content2 = gqm.restore(content2.getId());
			assertEquals(0, content2.getPriority());
			content3 = gqm.restore(content3.getId());
			assertEquals(0, content3.getPriority());
			content4 = gqm.restore(content4.getId());
			assertEquals(1, content4.getPriority());
			content5 = gqm.restore(content5.getId());
			assertEquals(1, content5.getPriority());

			assertTrue(manager.up(content5, "priority"));

			content1 = gqm.restore(content1.getId());
			assertEquals(2, content1.getPriority());
			content2 = gqm.restore(content2.getId());
			assertEquals(1, content2.getPriority());
			content3 = gqm.restore(content3.getId());
			assertEquals(0, content3.getPriority());
			content4 = gqm.restore(content4.getId());
			assertEquals(1, content4.getPriority());
			content5 = gqm.restore(content5.getId());
			assertEquals(0, content5.getPriority());

			assertTrue(manager.down(content5, "priority"));

			content1 = gqm.restore(content1.getId());
			assertEquals(2, content1.getPriority());
			content2 = gqm.restore(content2.getId());
			assertEquals(0, content2.getPriority());
			content3 = gqm.restore(content3.getId());
			assertEquals(0, content3.getPriority());
			content4 = gqm.restore(content4.getId());
			assertEquals(1, content4.getPriority());
			content5 = gqm.restore(content5.getId());
			assertEquals(1, content5.getPriority());

			manager.up(content2, "priority");

			content1 = gqm.restore(content1.getId());
			assertEquals(2, content1.getPriority());
			content2 = gqm.restore(content2.getId());
			assertEquals(0, content2.getPriority());
			content3 = gqm.restore(content3.getId());
			assertEquals(0, content3.getPriority());
			content4 = gqm.restore(content4.getId());
			assertEquals(1, content4.getPriority());
			content5 = gqm.restore(content5.getId());
			assertEquals(1, content5.getPriority());

			manager.down(content1, "priority");

			content1 = gqm.restore(content1.getId());
			assertEquals(2, content1.getPriority());
			content2 = gqm.restore(content2.getId());
			assertEquals(0, content2.getPriority());
			content3 = gqm.restore(content3.getId());
			assertEquals(0, content3.getPriority());
			content4 = gqm.restore(content4.getId());
			assertEquals(1, content4.getPriority());
			content5 = gqm.restore(content5.getId());
			assertEquals(1, content5.getPriority());
		}
		finally
		{
			manager.remove();
		}
	}

	public void testSaveOrdinalRestrictedInvalidType()
	throws Exception
	{
	    ContentQueryManager<OrdrdRestrInvalidType> manager = new ContentQueryManager<OrdrdRestrInvalidType>(mDatasource, OrdrdRestrInvalidType.class);
		manager.install();
		try
		{
			OrdrdRestrInvalidType content = new OrdrdRestrInvalidType()
				.name("the content name")
				.restricted("3");

			try
			{
				manager.save(content);
				fail();
			}
			catch (InvalidOrdinalRestrictionTypeException e)
			{
                assertSame(OrdrdRestrInvalidType.class, e.getBeanClass());
				assertEquals("priority", e.getProperty());
				assertEquals("restricted", e.getRestriction());
			}
		}
		finally
		{
			manager.remove();
		}
	}

	public void testMoveUnknownOrdinal()
	throws Exception
	{
	    ContentQueryManager<OrdrdUnknown> manager = new ContentQueryManager<OrdrdUnknown>(mDatasource, OrdrdUnknown.class);
		manager.install();
		try
		{
			try
			{
				manager.move(new OrdrdUnknown(), "unknown", OrdinalManager.UP);
				fail();
			}
			catch (UnknownOrdinalException e)
			{
				assertSame(OrdrdUnknown.class, e.getBeanClass());
				assertEquals("unknown", e.getProperty());
			}
		}
		finally
		{
			manager.remove();
		}
	}

	public void testMoveUnknownRestriction()
	throws Exception
	{
	    ContentQueryManager<OrdrdRestrUnknown> manager = new ContentQueryManager<OrdrdRestrUnknown>(mDatasource, OrdrdRestrUnknown.class);
		manager.install();
		try
		{
			try
			{
				manager.move(new OrdrdRestrUnknown(), "priority", OrdinalManager.UP);
				fail();
			}
			catch (UnknownOrdinalRestrictionException e)
			{
				assertSame(OrdrdRestrUnknown.class, e.getBeanClass());
				assertEquals("priority", e.getProperty());
				assertEquals("restrictedunknown", e.getRestriction());
			}
		}
		finally
		{
			manager.remove();
		}
	}

	public void testMoveRestrictionInvalidType()
	throws Exception
	{
	    ContentQueryManager<OrdrdRestrInvalidType> manager = new ContentQueryManager<OrdrdRestrInvalidType>(mDatasource, OrdrdRestrInvalidType.class);
		manager.install();
		try
		{
			try
			{
				manager.move(new OrdrdRestrInvalidType().restricted("restricted"), "priority", OrdinalManager.UP);
				fail();
			}
			catch (InvalidOrdinalRestrictionTypeException e)
			{
                assertSame(OrdrdRestrInvalidType.class, e.getBeanClass());
				assertEquals("priority", e.getProperty());
				assertEquals("restricted", e.getRestriction());
			}
		}
		finally
		{
			manager.remove();
		}
	}

	public void testMoveRestrictionNull()
	throws Exception
	{
	    ContentQueryManager<OrdrdRestrInvalidType> manager = new ContentQueryManager<OrdrdRestrInvalidType>(mDatasource, OrdrdRestrInvalidType.class);
		manager.install();
		try
		{
			try
			{
				manager.move(new OrdrdRestrInvalidType(), "priority", OrdinalManager.UP);
				fail();
			}
			catch (OrdinalRestrictionCantBeNullException e)
			{
                assertSame(OrdrdRestrInvalidType.class, e.getBeanClass());
				assertEquals("priority", e.getProperty());
				assertEquals("restricted", e.getRestriction());
			}
		}
		finally
		{
			manager.remove();
		}
	}

	public void testSaveOrdinalRestrictedUnknownRestriction()
	throws Exception
	{
	    ContentQueryManager<OrdrdRestrUnknown> manager = new ContentQueryManager<OrdrdRestrUnknown>(mDatasource, OrdrdRestrUnknown.class);
		manager.install();
		try
		{
			OrdrdRestrUnknown content = new OrdrdRestrUnknown()
				.name("the content name")
				.restricted(3);

			try
			{
				manager.save(content);
				fail();
			}
			catch (UnknownOrdinalRestrictionException e)
			{
                assertSame(OrdrdRestrUnknown.class, e.getBeanClass());
				assertEquals("priority", e.getProperty());
				assertEquals("restrictedunknown", e.getRestriction());
			}
		}
		finally
		{
			manager.remove();
		}
	}

	public void testHasContent()
	throws Exception
	{
	    ContentQueryManager<ContentImage> manager = new ContentQueryManager<ContentImage>(mDatasource, ContentImage.class);
		manager.install();
		try
		{
			URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
			byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);
			ContentImage content = new ContentImage()
				.name("the content name")
				.image(data_image_gif);

			int id = manager.save(content);

			assertTrue(manager.hasContent(id, "image"));
			assertFalse(manager.hasContent(id, "unknown"));
			assertFalse(manager.hasContent(34, "image"));

			assertTrue(manager.hasContent(content, "image"));
			assertFalse(manager.hasContent(content, "unknown"));
			content.setId(334);
			assertFalse(manager.hasContent(content, "image"));
		}
		finally
		{
			manager.remove();
		}
	}

	public void testHasContentRepository()
	throws Exception
	{
	    ContentQueryManager<ContentImageRepository> manager = new ContentQueryManager<ContentImageRepository>(mDatasource, ContentImageRepository.class);
		manager.install();
		manager.getContentManager().createRepository("testrep");
		try
		{
			URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
			byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);
			ContentImageRepository content = new ContentImageRepository()
				.name("the content name")
				.image(data_image_gif);

			int id = manager.save(content);

			checkContentRepository(id, "testrep");

			assertTrue(manager.hasContent(id, "image"));
			assertFalse(manager.hasContent(id, "unknown"));
			assertFalse(manager.hasContent(34, "image"));

			assertTrue(manager.hasContent(content, "image"));
			assertFalse(manager.hasContent(content, "unknown"));
			content.setId(334);
			assertFalse(manager.hasContent(content, "image"));
		}
		finally
		{
			manager.remove();
		}
	}

	public void testRestoreById()
	throws Exception
	{
	    ContentQueryManager<ContentImage> manager = new ContentQueryManager<ContentImage>(mDatasource, ContentImage.class);
		manager.install();
		try
		{
			URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
			byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);
			ContentImage content = new ContentImage()
				.name("the content name")
				.image(data_image_gif);

			int id = manager.save(content);

			ContentImage restored = manager.restore(id);
			assertEquals(content.getId(), restored.getId());
			assertEquals(content.getName(), restored.getName());
			assertNull(restored.getImage());
		}
		finally
		{
			manager.remove();
		}
	}

	public void testRestoreByIdAutoRetrieved()
	throws Exception
	{
	    ContentQueryManager<ContentImageAutoRetrieved> manager = new ContentQueryManager<ContentImageAutoRetrieved>(mDatasource, ContentImageAutoRetrieved.class);
		manager.install();
		try
		{
			URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
			byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);
			ContentImageAutoRetrieved content = new ContentImageAutoRetrieved()
				.name("the content name")
				.image(data_image_gif);

			int id = manager.save(content);

			ContentImageAutoRetrieved restored = manager.restore(id);
			assertEquals(content.getId(), restored.getId());
			assertEquals(content.getName(), restored.getName());
			URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn.png");
			byte[] data_image_png = FileUtils.readBytes(image_resource_png);
			assertTrue(Arrays.equals(data_image_png, restored.getImage()));
		}
		finally
		{
			manager.remove();
		}
	}

	public void testRestoreByIdPojo()
	throws Exception
	{
	    ContentQueryManager<RegularPojo> manager = new ContentQueryManager<RegularPojo>(mDatasource, RegularPojo.class);
		manager.install();
		try
		{
			RegularPojo content = new RegularPojo()
				.name("the regular pojo name");

			int id = manager.save(content);
			assertTrue(id >= 0);

			RegularPojo restored = manager.restore(id);
			assertEquals(content.getId(), restored.getId());
			assertEquals(content.getName(), restored.getName());
		}
		finally
		{
			manager.remove();
		}
	}

	public void testRestoreByIdNonCmfProperties()
	throws Exception
	{
		ContentQueryManager<ContentImageNonCmfProps> manager = new ContentQueryManager<ContentImageNonCmfProps>(mDatasource, ContentImageNonCmfProps.class);
		manager.install();
		try
		{
			URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
			byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);
			ContentImageNonCmfProps content = new ContentImageNonCmfProps()
				.name("the content name")
				.image(data_image_gif);

			int id = manager.save(content);

			ContentImageNonCmfProps restored = manager.restore(id);
			assertEquals(content.getId(), restored.getId());
			assertEquals(content.getName(), restored.getName());
			assertNull(restored.getImage());
		}
		finally
		{
			manager.remove();
		}
	}

	public void testRestoreFirst()
	throws Exception
	{
	    ContentQueryManager<ContentImageAutoRetrieved> manager = new ContentQueryManager<ContentImageAutoRetrieved>(mDatasource, ContentImageAutoRetrieved.class);
		manager.install();
		try
		{
			URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
			byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

			ContentImageAutoRetrieved content1 = new ContentImageAutoRetrieved()
				.name("the content name")
				.image(data_image_gif);
			manager.save(content1);

			URL image_resource_tif= ResourceFinderClasspath.getInstance().getResource("uwyn.tif");
			byte[] data_image_tif = FileUtils.readBytes(image_resource_tif);

			ContentImageAutoRetrieved content2 = new ContentImageAutoRetrieved()
				.name("another content name")
				.image(data_image_tif);
			manager.save(content2);

			ContentImageAutoRetrieved restored = manager.restoreFirst(manager.getRestoreQuery().orderBy("id", Select.DESC));
			assertEquals(content2.getId(), restored.getId());
			assertEquals(content2.getName(), restored.getName());
			URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn-noalpha.png");
			byte[] data_image_png = FileUtils.readBytes(image_resource_png);
			assertTrue(Arrays.equals(data_image_png, restored.getImage()));
		}
		finally
		{
			manager.remove();
		}
	}

	public void testRestore()
	throws Exception
	{
	    ContentQueryManager<ContentImageAutoRetrieved> manager = new ContentQueryManager<ContentImageAutoRetrieved>(mDatasource, ContentImageAutoRetrieved.class);
		manager.install();
		try
		{
			URL uwyn_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
			byte[] uwyn_image_gif = FileUtils.readBytes(uwyn_resource_gif);

			ContentImageAutoRetrieved content1 = new ContentImageAutoRetrieved()
				.name("the content name")
				.image(uwyn_image_gif);
			manager.save(content1);

			URL rife_resource_tif= ResourceFinderClasspath.getInstance().getResource("rife-logo_small.tif");
			byte[] rife_image_tif = FileUtils.readBytes(rife_resource_tif);

			ContentImageAutoRetrieved content2 = new ContentImageAutoRetrieved()
				.name("another content name")
				.image(rife_image_tif);
			manager.save(content2);

			URL uwyn_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn.png");
			byte[] uwyn_image_png = FileUtils.readBytes(uwyn_resource_png);

			URL rife_resource_png = ResourceFinderClasspath.getInstance().getResource("rife-logo_small.png");
			byte[] rife_image_png = FileUtils.readBytes(rife_resource_png);

			List<ContentImageAutoRetrieved> restored_list = manager.restore();
			ContentImageAutoRetrieved restored = null;
			Iterator<ContentImageAutoRetrieved> restored_it = restored_list.iterator();
			restored = restored_it.next();
			assertEquals(content1.getId(), restored.getId());
			assertEquals(content1.getName(), restored.getName());
			assertTrue(Arrays.equals(uwyn_image_png, restored.getImage()));
			restored = restored_it.next();
			assertEquals(content2.getId(), restored.getId());
			assertEquals(content2.getName(), restored.getName());
			assertTrue(Arrays.equals(rife_image_png, restored.getImage()));
			assertFalse(restored_it.hasNext());
		}
		finally
		{
			manager.remove();
		}
	}

	public void testRestoreRepository()
	throws Exception
	{
	    ContentQueryManager<ContentImageAutoRetrRep> manager = new ContentQueryManager<ContentImageAutoRetrRep>(mDatasource, ContentImageAutoRetrRep.class);
		manager.install();
		manager.getContentManager().createRepository("testrep");
		try
		{
			URL uwyn_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
			byte[] uwyn_image_gif = FileUtils.readBytes(uwyn_resource_gif);

			ContentImageAutoRetrRep content1 = new ContentImageAutoRetrRep()
				.name("the content name")
				.image(uwyn_image_gif);
			int id1 = manager.save(content1);

			checkContentRepository(id1, "testrep");

			URL rife_resource_tif= ResourceFinderClasspath.getInstance().getResource("rife-logo_small.tif");
			byte[] rife_image_tif = FileUtils.readBytes(rife_resource_tif);

			ContentImageAutoRetrRep content2 = new ContentImageAutoRetrRep()
				.name("another content name")
				.image(rife_image_tif);
			int id2 = manager.save(content2);

			checkContentRepository(id2, "testrep");

			URL uwyn_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn.png");
			byte[] uwyn_image_png = FileUtils.readBytes(uwyn_resource_png);

			URL rife_resource_png = ResourceFinderClasspath.getInstance().getResource("rife-logo_small.png");
			byte[] rife_image_png = FileUtils.readBytes(rife_resource_png);

			List<ContentImageAutoRetrRep> restored_list = manager.restore();
			ContentImageAutoRetrRep restored = null;
			Iterator<ContentImageAutoRetrRep> restored_it = restored_list.iterator();
			restored = restored_it.next();
			assertEquals(content1.getId(), restored.getId());
			assertEquals(content1.getName(), restored.getName());
			assertTrue(Arrays.equals(uwyn_image_png, restored.getImage()));
			restored = restored_it.next();
			assertEquals(content2.getId(), restored.getId());
			assertEquals(content2.getName(), restored.getName());
			assertTrue(Arrays.equals(rife_image_png, restored.getImage()));
			assertFalse(restored_it.hasNext());
		}
		finally
		{
			manager.remove();
		}
	}

	public void testRestoreQuery()
	throws Exception
	{
	    ContentQueryManager<ContentImageAutoRetrieved> manager = new ContentQueryManager<ContentImageAutoRetrieved>(mDatasource, ContentImageAutoRetrieved.class);
		manager.install();
		try
		{
			URL uwyn_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
			byte[] uwyn_image_gif = FileUtils.readBytes(uwyn_resource_gif);

			ContentImageAutoRetrieved content1 = new ContentImageAutoRetrieved()
				.name("the content name")
				.image(uwyn_image_gif);
			manager.save(content1);

			URL rife_resource_tif= ResourceFinderClasspath.getInstance().getResource("rife-logo_small.tif");
			byte[] rife_image_tif = FileUtils.readBytes(rife_resource_tif);

			ContentImageAutoRetrieved content2 = new ContentImageAutoRetrieved()
				.name("another content name")
				.image(rife_image_tif);
			manager.save(content2);

			URL uwyn_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn.png");
			byte[] uwyn_image_png = FileUtils.readBytes(uwyn_resource_png);

			URL rife_resource_png = ResourceFinderClasspath.getInstance().getResource("rife-logo_small.png");
			byte[] rife_image_png = FileUtils.readBytes(rife_resource_png);

			List<ContentImageAutoRetrieved> restored_list = manager.restore(manager.getRestoreQuery().orderBy("id", Select.DESC));
			ContentImageAutoRetrieved restored = null;
			Iterator<ContentImageAutoRetrieved> restored_it = restored_list.iterator();
			restored = restored_it.next();
			assertEquals(content2.getId(), restored.getId());
			assertEquals(content2.getName(), restored.getName());
			assertTrue(Arrays.equals(rife_image_png, restored.getImage()));
			restored = restored_it.next();
			assertEquals(content1.getId(), restored.getId());
			assertEquals(content1.getName(), restored.getName());
			assertTrue(Arrays.equals(uwyn_image_png, restored.getImage()));
			assertFalse(restored_it.hasNext());
		}
		finally
		{
			manager.remove();
		}
	}
}
