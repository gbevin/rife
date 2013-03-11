/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDatabaseImageStore.java 3936 2008-04-26 12:05:37Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentstores;

import com.uwyn.rife.cmf.dam.contentstores.exceptions.*;
import java.awt.*;

import com.uwyn.rife.cmf.Content;
import com.uwyn.rife.cmf.ContentInfo;
import com.uwyn.rife.cmf.ContentRepository;
import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.cmf.dam.ContentDataUser;
import com.uwyn.rife.cmf.dam.contentmanagers.DatabaseContent;
import com.uwyn.rife.cmf.dam.contentmanagers.DatabaseContentFactory;
import com.uwyn.rife.cmf.dam.contentmanagers.DatabaseContentInfo;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.DbPreparedStatementHandler;
import com.uwyn.rife.database.queries.Insert;
import com.uwyn.rife.database.queries.Select;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.tools.FileUtils;
import com.uwyn.rife.tools.ImageWaiter;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Date;
import java.sql.Timestamp;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;
import junit.framework.TestCase;

public class TestDatabaseImageStore extends TestCase
{
	private Datasource 	mDatasource = null;

	private static byte[] sLargeImagePng = null;
	static
	{
		int edge = 1200;
		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		ImageIcon icon = new ImageIcon(image_resource_gif);
		Image image_scaled = icon.getImage().getScaledInstance(edge, edge, Image.SCALE_FAST);
		ImageWaiter.wait(image_scaled);
		BufferedImage   buffer  = new BufferedImage(image_scaled.getWidth(null), image_scaled.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = buffer.createGraphics();
		g2.drawImage(image_scaled, 0, 0, null);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		GradientPaint gradient1 = new GradientPaint(0, 0, Color.red, 175, 175, Color.yellow, true);
		g2.setPaint(gradient1);
    	g2.fill(new Rectangle(0, 0, edge, edge));
		GradientPaint gradient2 = new GradientPaint(100, 50, Color.green, 205, 375, Color.blue, true);
		g2.setPaint(gradient2);
    	g2.fill(new Rectangle(0, 0, edge, edge));
		g2.dispose();
		
		try
		{
			Iterator<ImageWriter>   writers = ImageIO.getImageWritersByMIMEType("image/png");
			ImageWriter             writer = writers.next();
			ByteArrayOutputStream	byte_out = new ByteArrayOutputStream();
			ImageOutputStream		image_out = ImageIO.createImageOutputStream(byte_out);
			writer.setOutput(image_out);
			writer.write(buffer);
			writer.dispose();
			byte_out.flush();
			byte_out.close();
			
			sLargeImagePng = byte_out.toByteArray();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public TestDatabaseImageStore(Datasource datasource, String datasourceName, String name)
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
	throws Exception
	{
		DatabaseImageStore store = DatabaseImageStoreFactory.getInstance(mDatasource);
		try
		{
			store.install();
			fail();
		}
		catch (InstallContentStoreErrorException e)
		{
			assertNotNull(e.getCause());
		}
	}

	public void testRemoveError()
	throws Exception
	{
		DatabaseContentFactory.getInstance(mDatasource).remove();
		DatabaseImageStore store = DatabaseImageStoreFactory.getInstance(mDatasource);
		try
		{
			store.remove();
			fail();
		}
		catch (RemoveContentStoreErrorException e)
		{
			assertNotNull(e.getCause());
		}
	}

	public void testGetSupportedMimeTypes()
	{
		DatabaseImageStore store = DatabaseImageStoreFactory.getInstance(mDatasource);
		assertTrue(store.getSupportedMimeTypes().size() > 0);
	}

	public void testGetContentType()
	{
		DatabaseImageStore store = DatabaseImageStoreFactory.getInstance(mDatasource);
		ContentInfo info = new ContentInfo();
		info.setMimeType(MimeType.IMAGE_GIF.toString());
		assertNotNull(store.getContentType(info));
		info.setMimeType(MimeType.APPLICATION_XHTML.toString());
		assertNull(store.getContentType(info));
	}

	public void testGetFormatter()
	{
		DatabaseImageStore store = DatabaseImageStoreFactory.getInstance(mDatasource);
		assertNotNull(store.getFormatter(MimeType.IMAGE_GIF, false));
		assertNull(store.getFormatter(MimeType.APPLICATION_XHTML, true));
	}

	public void testStoreContentData()
	throws Exception
	{
		final int[] id = new int[] {1};
		final DatabaseContent manager = DatabaseContentFactory.getInstance(mDatasource);
		Insert insert = null;
		if ("org.apache.derby.jdbc.EmbeddedDriver".equals(mDatasource.getAliasedDriver()))
		{
			insert = new Insert(mDatasource)
				.into(RifeConfig.Cmf.getTableContentInfo())
				.fieldsParametersExcluded(DatabaseContentInfo.class, new String[] {"contentId"})
				.fieldParameter("version")
				.fieldParameter("repositoryId");
		}
		else if ("in.co.daffodil.db.jdbc.DaffodilDBDriver".equals(mDatasource.getAliasedDriver()))
		{
			insert = new Insert(mDatasource)
				.into(RifeConfig.Cmf.getTableContentInfo())
				.fieldsParametersExcluded(DatabaseContentInfo.class, new String[] {"path"})
				.fieldParameter("\"path\"", "path")
				.fieldParameter("version")
				.fieldParameter("repositoryId");
		}
		else
		{
			insert = new Insert(mDatasource)
				.into(RifeConfig.Cmf.getTableContentInfo())
				.fieldsParameters(DatabaseContentInfo.class)
				.fieldParameter("version")
				.fieldParameter("repositoryId");
		}
		if ("com.mysql.jdbc.Driver".equals(mDatasource.getAliasedDriver()))
		{
			insert.fieldParameter("created");
		}
		manager.executeUpdate(insert, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					DatabaseContentInfo content_info = new DatabaseContentInfo();
					if (!"org.apache.derby.jdbc.EmbeddedDriver".equals(mDatasource.getAliasedDriver()))
					{
						content_info.setContentId(id[0]);
					}
					content_info.setFragment(false);
					content_info.setPath("/testpath");
					content_info.setMimeType(MimeType.IMAGE_PNG.toString());
					content_info.setCreated(new Timestamp(new Date().getTime()));
					statement
						.setInt("version", 1)
						.setInt("repositoryId", manager.executeGetFirstInt(new Select(mDatasource)
							.from(RifeConfig.Cmf.getTableContentRepository())
							.field("repositoryId")
							.where("name", "=", ContentRepository.DEFAULT)))
						.setBean(content_info);
				}
			});

		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

		DatabaseImageStore store = DatabaseImageStoreFactory.getInstance(mDatasource);
		Content content = new Content(MimeType.IMAGE_PNG, data_image_gif);
        assertTrue(store.storeContentData(id[0], content, null));

		URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn.png");
		final byte[] data_image_png = FileUtils.readBytes(image_resource_png);

		store.useContentData(id[0], new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					assertTrue(Arrays.equals(data_image_png, (byte[])contentData));
					return null;
				}
			});
	}

	public void testStoreContentDataLarge()
	throws Exception
	{
		final int[] id = new int[] {1};
		final DatabaseContent manager = DatabaseContentFactory.getInstance(mDatasource);
		Insert insert = null;
		if ("org.apache.derby.jdbc.EmbeddedDriver".equals(mDatasource.getAliasedDriver()))
		{
			insert = new Insert(mDatasource)
				.into(RifeConfig.Cmf.getTableContentInfo())
				.fieldsParametersExcluded(DatabaseContentInfo.class, new String[] {"contentId"})
				.fieldParameter("version")
				.fieldParameter("repositoryId");
		}
		else if ("in.co.daffodil.db.jdbc.DaffodilDBDriver".equals(mDatasource.getAliasedDriver()))
		{
			insert = new Insert(mDatasource)
				.into(RifeConfig.Cmf.getTableContentInfo())
				.fieldsParametersExcluded(DatabaseContentInfo.class, new String[] {"path"})
				.fieldParameter("\"path\"", "path")
				.fieldParameter("version")
				.fieldParameter("repositoryId");
		}
		else
		{
			insert = new Insert(mDatasource)
				.into(RifeConfig.Cmf.getTableContentInfo())
				.fieldsParameters(DatabaseContentInfo.class)
				.fieldParameter("version")
				.fieldParameter("repositoryId");
		}
		if ("com.mysql.jdbc.Driver".equals(mDatasource.getAliasedDriver()))
		{
			insert.fieldParameter("created");
		}
		manager.executeUpdate(insert, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					DatabaseContentInfo content_info = new DatabaseContentInfo();
					if (!"org.apache.derby.jdbc.EmbeddedDriver".equals(mDatasource.getAliasedDriver()))
					{
						content_info.setContentId(id[0]);
					}
					content_info.setFragment(false);
					content_info.setPath("/testpath");
					content_info.setMimeType(MimeType.IMAGE_PNG.toString());
					content_info.setCreated(new Timestamp(new Date().getTime()));
					statement
						.setInt("version", 1)
						.setInt("repositoryId", manager.executeGetFirstInt(new Select(mDatasource)
							.from(RifeConfig.Cmf.getTableContentRepository())
							.field("repositoryId")
							.where("name", "=", ContentRepository.DEFAULT)))
						.setBean(content_info);
				}
			});

		DatabaseImageStore store = DatabaseImageStoreFactory.getInstance(mDatasource);
		Content content = new Content(MimeType.IMAGE_PNG, sLargeImagePng);
        assertTrue(store.storeContentData(id[0], content, null));

		store.useContentData(id[0], new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					assertTrue(Arrays.equals(sLargeImagePng, (byte[])contentData));
					return null;
				}
			});
	}

	public void testStoreContentDataContentEmpty()
	throws Exception
	{
		final int[] id = new int[] {1};
		final DatabaseContent manager = DatabaseContentFactory.getInstance(mDatasource);
		Insert insert = null;
		if ("org.apache.derby.jdbc.EmbeddedDriver".equals(mDatasource.getAliasedDriver()))
		{
			insert = new Insert(mDatasource)
				.into(RifeConfig.Cmf.getTableContentInfo())
				.fieldsParametersExcluded(DatabaseContentInfo.class, new String[] {"contentId"})
				.fieldParameter("version")
				.fieldParameter("repositoryId");
		}
		else if ("in.co.daffodil.db.jdbc.DaffodilDBDriver".equals(mDatasource.getAliasedDriver()))
		{
			insert = new Insert(mDatasource)
				.into(RifeConfig.Cmf.getTableContentInfo())
				.fieldsParametersExcluded(DatabaseContentInfo.class, new String[] {"path"})
				.fieldParameter("\"path\"", "path")
				.fieldParameter("version")
				.fieldParameter("repositoryId");
		}
		else
		{
			insert = new Insert(mDatasource)
				.into(RifeConfig.Cmf.getTableContentInfo())
				.fieldsParameters(DatabaseContentInfo.class)
				.fieldParameter("version")
				.fieldParameter("repositoryId");
		}
		if ("com.mysql.jdbc.Driver".equals(mDatasource.getAliasedDriver()))
		{
			insert.fieldParameter("created");
		}
		manager.executeUpdate(insert, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					DatabaseContentInfo content_info = new DatabaseContentInfo();
					if (!"org.apache.derby.jdbc.EmbeddedDriver".equals(mDatasource.getAliasedDriver()))
					{
						content_info.setContentId(id[0]);
					}
					content_info.setFragment(false);
					content_info.setPath("/testpath");
					content_info.setMimeType(MimeType.IMAGE_PNG.toString());
					content_info.setCreated(new Timestamp(new Date().getTime()));
					statement
						.setInt("version", 1)
						.setInt("repositoryId", manager.executeGetFirstInt(new Select(mDatasource)
							.from(RifeConfig.Cmf.getTableContentRepository())
							.field("repositoryId")
							.where("name", "=", ContentRepository.DEFAULT)))
						.setBean(content_info);
				}
			});

		DatabaseImageStore store = DatabaseImageStoreFactory.getInstance(mDatasource);

		Content content = new Content(MimeType.IMAGE_PNG, null);
        assertTrue(store.storeContentData(id[0], content, null));
		store.useContentData(id[0], new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					assertNull(contentData);
					return null;
				}
			});
	}

	public void testStoreContentDataContentNull()
	throws Exception
	{
		final int[] id = new int[] {1};
		final DatabaseContent manager = DatabaseContentFactory.getInstance(mDatasource);
		Insert insert = null;
		if ("org.apache.derby.jdbc.EmbeddedDriver".equals(mDatasource.getAliasedDriver()))
		{
			insert = new Insert(mDatasource)
				.into(RifeConfig.Cmf.getTableContentInfo())
				.fieldsParametersExcluded(DatabaseContentInfo.class, new String[] {"contentId"})
				.fieldParameter("version")
				.fieldParameter("repositoryId");
		}
		else if ("in.co.daffodil.db.jdbc.DaffodilDBDriver".equals(mDatasource.getAliasedDriver()))
		{
			insert = new Insert(mDatasource)
				.into(RifeConfig.Cmf.getTableContentInfo())
				.fieldsParametersExcluded(DatabaseContentInfo.class, new String[] {"path"})
				.fieldParameter("\"path\"", "path")
				.fieldParameter("version")
				.fieldParameter("repositoryId");
		}
		else
		{
			insert = new Insert(mDatasource)
				.into(RifeConfig.Cmf.getTableContentInfo())
				.fieldsParameters(DatabaseContentInfo.class)
				.fieldParameter("version")
				.fieldParameter("repositoryId");
		}
		if ("com.mysql.jdbc.Driver".equals(mDatasource.getAliasedDriver()))
		{
			insert.fieldParameter("created");
		}
		manager.executeUpdate(insert, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					DatabaseContentInfo content_info = new DatabaseContentInfo();
					if (!"org.apache.derby.jdbc.EmbeddedDriver".equals(mDatasource.getAliasedDriver()))
					{
						content_info.setContentId(id[0]);
					}
					content_info.setFragment(false);
					content_info.setPath("/testpath");
					content_info.setMimeType(MimeType.IMAGE_PNG.toString());
					content_info.setCreated(new Timestamp(new Date().getTime()));
					statement
						.setInt("version", 1)
						.setInt("repositoryId", manager.executeGetFirstInt(new Select(mDatasource)
							.from(RifeConfig.Cmf.getTableContentRepository())
							.field("repositoryId")
							.where("name", "=", ContentRepository.DEFAULT)))
						.setBean(content_info);
				}
			});

		DatabaseImageStore store = DatabaseImageStoreFactory.getInstance(mDatasource);

		assertTrue(store.storeContentData(id[0], null, null));
		store.useContentData(id[0], new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					assertNull(contentData);
					return null;
				}
			});
	}

	public void testStoreContentDataMimeTypeWithoutFormatter()
	throws Exception
	{
		final int[] id = new int[] {1};
		final DatabaseContent manager = DatabaseContentFactory.getInstance(mDatasource);
		Insert insert = null;
		if ("org.apache.derby.jdbc.EmbeddedDriver".equals(mDatasource.getAliasedDriver()))
		{
			insert = new Insert(mDatasource)
				.into(RifeConfig.Cmf.getTableContentInfo())
				.fieldsParametersExcluded(DatabaseContentInfo.class, new String[] {"contentId"})
				.fieldParameter("version")
				.fieldParameter("repositoryId");
		}
		else if ("in.co.daffodil.db.jdbc.DaffodilDBDriver".equals(mDatasource.getAliasedDriver()))
		{
			insert = new Insert(mDatasource)
				.into(RifeConfig.Cmf.getTableContentInfo())
				.fieldsParametersExcluded(DatabaseContentInfo.class, new String[] {"path"})
				.fieldParameter("\"path\"", "path")
				.fieldParameter("version")
				.fieldParameter("repositoryId");
		}
		else
		{
			insert = new Insert(mDatasource)
				.into(RifeConfig.Cmf.getTableContentInfo())
				.fieldsParameters(DatabaseContentInfo.class)
				.fieldParameter("version")
				.fieldParameter("repositoryId");
		}
		if ("com.mysql.jdbc.Driver".equals(mDatasource.getAliasedDriver()))
		{
			insert.fieldParameter("created");
		}
		manager.executeUpdate(insert, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					DatabaseContentInfo content_info = new DatabaseContentInfo();
					if (!"org.apache.derby.jdbc.EmbeddedDriver".equals(mDatasource.getAliasedDriver()))
					{
						content_info.setContentId(id[0]);
					}
					content_info.setFragment(false);
					content_info.setPath("/testpath");
					content_info.setMimeType(MimeType.APPLICATION_XHTML.toString());
					content_info.setCreated(new Timestamp(new Date().getTime()));
					statement
						.setInt("version", 1)
						.setInt("repositoryId", manager.executeGetFirstInt(new Select(mDatasource)
							.from(RifeConfig.Cmf.getTableContentRepository())
							.field("repositoryId")
							.where("name", "=", ContentRepository.DEFAULT)))
						.setBean(content_info);
				}
			});

		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		final byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

		DatabaseImageStore store = DatabaseImageStoreFactory.getInstance(mDatasource);
		Content content = new Content(MimeType.APPLICATION_XHTML, data_image_gif);
        assertTrue(store.storeContentData(id[0], content, null));

		store.useContentData(id[0], new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					assertTrue(Arrays.equals(data_image_gif, (byte[])contentData));
					return null;
				}
			});
	}

	public void testStoreContentDataIllegalArgument()
	throws Exception
	{
		DatabaseImageStore store = DatabaseImageStoreFactory.getInstance(mDatasource);

		try
		{
			store.storeContentData(-1, null, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}

		try
		{
			store.storeContentData(1, new Content(MimeType.IMAGE_GIF, new Object()), null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testStoreContentDataError()
	throws Exception
	{
		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

		DatabaseImageStore store = DatabaseImageStoreFactory.getInstance(mDatasource);
		Content content = new Content(MimeType.IMAGE_PNG, data_image_gif);
		try
		{
			store.storeContentData(2, content, null);
			assertTrue("com.mysql.jdbc.Driver".equals(mDatasource.getAliasedDriver()));
		}
		catch (StoreContentDataErrorException e)
		{
			assertEquals(2, e.getId());
		}
	}

	public void testUseContentData()
	throws Exception
	{
		DatabaseContent manager = DatabaseContentFactory.getInstance(mDatasource);

		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

		Content content = new Content(MimeType.IMAGE_PNG, data_image_gif);
		assertTrue(manager.storeContent("/imagegif", content, null));
		DatabaseContentInfo content_info = manager.getContentInfo("/imagegif");

		DatabaseImageStore store = DatabaseImageStoreFactory.getInstance(mDatasource);

		URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn.png");
		final byte[] data_image_png = FileUtils.readBytes(image_resource_png);

		store.useContentData(content_info.getContentId(), new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					assertTrue(Arrays.equals(data_image_png, (byte[])contentData));
					return null;
				}
			});
	}

	public void testUseContentDataUnknown()
	throws Exception
	{
		DatabaseImageStore store = DatabaseImageStoreFactory.getInstance(mDatasource);

		store.useContentData(232, new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					assertNull(contentData);
					return null;
				}
			});
	}

	public void testUseContentDataIllegalArgument()
	throws Exception
	{
		DatabaseImageStore store = DatabaseImageStoreFactory.getInstance(mDatasource);

		try
		{
			store.useContentData(-1, new ContentDataUser() {
					public Object useContentData(Object contentData)
					throws InnerClassException
					{
						fail();
						return null;
					}
				});
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}

		try
		{
			store.useContentData(23, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testUseContentDataError()
	throws Exception
	{
		DatabaseContentFactory.getInstance(mDatasource).remove();
		DatabaseImageStore store = DatabaseImageStoreFactory.getInstance(mDatasource);
		try
		{
			store.useContentData(2, new ContentDataUser() {
					public Object useContentData(Object contentData)
					throws InnerClassException
					{
						fail();
						return null;
					}
				});
		}
		catch (UseContentDataErrorException e)
		{
			assertEquals(2, e.getId());
		}
	}

	public void testHasContentData()
	throws Exception
	{
		DatabaseContent manager = DatabaseContentFactory.getInstance(mDatasource);

		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

		Content content = new Content(MimeType.IMAGE_PNG, data_image_gif);
		assertTrue(manager.storeContent("/imagegif", content, null));
		DatabaseContentInfo content_info = manager.getContentInfo("/imagegif");

		DatabaseImageStore store = DatabaseImageStoreFactory.getInstance(mDatasource);
		assertTrue(store.hasContentData(content_info.getContentId()));
	}

	public void testHasContentDataContentEmpty()
	throws Exception
	{
		DatabaseContent manager = DatabaseContentFactory.getInstance(mDatasource);

		Content content = new Content(MimeType.IMAGE_PNG, null);
		assertTrue(manager.storeContent("/imagegif", content, null));
		DatabaseContentInfo content_info = manager.getContentInfo("/imagegif");

		DatabaseImageStore store = DatabaseImageStoreFactory.getInstance(mDatasource);
		assertFalse(store.hasContentData(content_info.getContentId()));
	}

	public void testHasContentDataUnknown()
	throws Exception
	{
		DatabaseImageStore store = DatabaseImageStoreFactory.getInstance(mDatasource);
		assertFalse(store.hasContentData(3));
	}

	public void testHasContentDataIllegalArgument()
	throws Exception
	{
		DatabaseImageStore store = DatabaseImageStoreFactory.getInstance(mDatasource);
		try
		{
			store.hasContentData(-1);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testHasContentDataError()
	throws Exception
	{
		DatabaseContentFactory.getInstance(mDatasource).remove();
		DatabaseImageStore store = DatabaseImageStoreFactory.getInstance(mDatasource);
		try
		{
			store.hasContentData(2);
			fail();
		}
		catch (HasContentDataErrorException e)
		{
			assertEquals(2, e.getId());
		}
	}

	public void testDeleteContentData()
	throws Exception
	{
		DatabaseContent manager = DatabaseContentFactory.getInstance(mDatasource);

		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

		Content content = new Content(MimeType.IMAGE_PNG, data_image_gif);
		manager.storeContent("/imagegif", content, null);
		DatabaseContentInfo content_info = manager.getContentInfo("/imagegif");

		DatabaseImageStore store = DatabaseImageStoreFactory.getInstance(mDatasource);
		assertTrue(store.hasContentData(content_info.getContentId()));
		assertTrue(store.deleteContentData(content_info.getContentId()));
		assertFalse(store.hasContentData(content_info.getContentId()));
		assertFalse(store.deleteContentData(content_info.getContentId()));
	}

	public void testDeleteContentDataUnknown()
	throws Exception
	{
		DatabaseImageStore store = DatabaseImageStoreFactory.getInstance(mDatasource);
		assertFalse(store.deleteContentData(3));
	}

	public void testDeleteContentDataIllegalArgument()
	throws Exception
	{
		DatabaseImageStore store = DatabaseImageStoreFactory.getInstance(mDatasource);
		try
		{
			store.deleteContentData(-1);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testDeleteContentDataError()
	throws Exception
	{
		DatabaseContentFactory.getInstance(mDatasource).remove();
		DatabaseImageStore store = DatabaseImageStoreFactory.getInstance(mDatasource);
		try
		{
			store.deleteContentData(2);
			fail();
		}
		catch (DeleteContentDataErrorException e)
		{
			assertEquals(2, e.getId());
		}
	}

	public void testRetrieveSize()
	throws Exception
	{
		DatabaseContent manager = DatabaseContentFactory.getInstance(mDatasource);

		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

		Content content = new Content(MimeType.IMAGE_PNG, data_image_gif);
		assertTrue(manager.storeContent("/imagegif", content, null));
		DatabaseContentInfo content_info = manager.getContentInfo("/imagegif");

		DatabaseImageStore store = DatabaseImageStoreFactory.getInstance(mDatasource);

		URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn.png");
		byte[] data_image_png = FileUtils.readBytes(image_resource_png);

		assertEquals(store.getSize(content_info.getContentId()), data_image_png.length);
	}

	public void testRetrieveSizeIllegalArgument()
	throws Exception
	{
		DatabaseImageStore store = DatabaseImageStoreFactory.getInstance(mDatasource);

		try
		{
			store.getSize(-1);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testRetrieveSizeError()
	throws Exception
	{
		DatabaseContentFactory.getInstance(mDatasource).remove();
		DatabaseImageStore store = DatabaseImageStoreFactory.getInstance(mDatasource);
		try
		{
			store.getSize(2);
			fail();
		}
		catch (RetrieveSizeErrorException e)
		{
			assertEquals(2, e.getId());
		}
	}
}
