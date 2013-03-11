/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDatabaseRawStore.java 3936 2008-04-26 12:05:37Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentstores;

import com.uwyn.rife.cmf.dam.contentstores.exceptions.*;

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
import com.uwyn.rife.tools.FileUtils;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import com.uwyn.rife.tools.exceptions.FileUtilsErrorException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Date;
import java.sql.Timestamp;

import junit.framework.TestCase;

public class TestDatabaseRawStore extends TestCase
{
	private Datasource 	mDatasource = null;

	public TestDatabaseRawStore(Datasource datasource, String datasourceName, String name)
	{
		super(name);

		mDatasource = datasource;
	}

	private byte[] getSmallRaw()
	{
		int size = 60*1024; // 60kb
		byte[] binary = new byte[size];
		for (int i = 0; i < size; i++)
		{
			binary[i] = (byte)(i%127);
		}
		
		return binary;
	}
			
	private byte[] getLargeRaw()
	{
		int size = 1024*1024*4; // 4Mb
		byte[] binary = new byte[size];
		for (int i = 0; i < size; i++)
		{
			binary[i] = (byte)(i%255);
		}
		
		return binary;
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
		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);
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
		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);
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
		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);
		assertTrue(store.getSupportedMimeTypes().size() > 0);
	}

	public void testGetContentType()
	{
		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);
		ContentInfo info = new ContentInfo();
		info.setMimeType(MimeType.RAW.toString());
		assertNull(store.getContentType(info));
		info.setAttributes(new HashMap<String, String>() {{ put("content-type", "application/something"); }});
		assertNotNull(store.getContentType(info));
		info.setAttributes(null);
		assertNull(store.getContentType(info));
		info.setName("test.ogg");
		assertNotNull(store.getContentType(info));
		info.setName("test.unknown");
		assertNull(store.getContentType(info));
		info.setMimeType(MimeType.APPLICATION_XHTML.toString());
		assertNull(store.getContentType(info));
	}

	public void testGetFormatter()
	{
		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);
		assertNotNull(store.getFormatter(MimeType.RAW, false));
		assertNull(store.getFormatter(MimeType.APPLICATION_XHTML, true));
	}

	public void testStoreContentDataStream()
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
					content_info.setMimeType(MimeType.RAW.toString());
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

		final byte[] raw = getSmallRaw();

		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);
		Content content = new Content(MimeType.RAW, new ByteArrayInputStream(raw));
        assertTrue(store.storeContentData(id[0], content, null));

		store.useContentData(id[0], new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					try
					{
						byte[] received = FileUtils.readBytes((InputStream)contentData);
						assertTrue(Arrays.equals(raw, received));
					}
					catch (FileUtilsErrorException e)
					{
						throwException(e);
					}
					return null;
				}
			});
	}
	
	public void testStoreContentDataBytes()
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
					content_info.setMimeType(MimeType.RAW.toString());
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
		
		final byte[] raw = getSmallRaw();
		
		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);
		Content content = new Content(MimeType.RAW, raw);
        assertTrue(store.storeContentData(id[0], content, null));
		
		store.useContentData(id[0], new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					try
					{
						byte[] received = FileUtils.readBytes((InputStream)contentData);
						assertTrue(Arrays.equals(raw, received));
					}
					catch (FileUtilsErrorException e)
					{
						throwException(e);
					}
					return null;
				}
			});
	}
	
	public void testStoreContentDataLargeStream()
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
					content_info.setMimeType(MimeType.RAW.toString());
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

		final byte[] raw = getLargeRaw();

		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);
		Content content = new Content(MimeType.RAW, new ByteArrayInputStream(raw));
        assertTrue(store.storeContentData(id[0], content, null));

		store.useContentData(id[0], new ContentDataUser() {
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
	
	public void testStoreContentDataLargeBytes()
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
					content_info.setMimeType(MimeType.RAW.toString());
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
		
		final byte[] raw = getLargeRaw();
		
		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);
		Content content = new Content(MimeType.RAW, raw);
        assertTrue(store.storeContentData(id[0], content, null));
		
		store.useContentData(id[0], new ContentDataUser() {
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
					content_info.setMimeType(MimeType.RAW.toString());
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

		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);

		Content content = new Content(MimeType.RAW, null);
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
					content_info.setMimeType(MimeType.RAW.toString());
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

		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);

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
					content_info.setMimeType(MimeType.IMAGE_GIF.toString());
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

		final byte[] raw = getSmallRaw();

		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);
		Content content = new Content(MimeType.IMAGE_GIF, new ByteArrayInputStream(raw));
        assertTrue(store.storeContentData(id[0], content, null));

		store.useContentData(id[0], new ContentDataUser() {
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

	public void testStoreContentDataIllegalArgument()
	throws Exception
	{
		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);

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
			store.storeContentData(1, new Content(MimeType.RAW, new Object()), null);
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
		byte[] raw = getSmallRaw();

		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);
		Content content = new Content(MimeType.RAW, new ByteArrayInputStream(raw));
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

		final byte[] raw = getSmallRaw();

		Content content = new Content(MimeType.RAW, new ByteArrayInputStream(raw));
		assertTrue(manager.storeContent("/rawdata", content, null));
		DatabaseContentInfo content_info = manager.getContentInfo("/rawdata");

		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);

		store.useContentData(content_info.getContentId(), new ContentDataUser() {
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

	public void testUseContentDataUnknown()
	throws Exception
	{
		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);

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
		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);

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
			store.useContentData(-1, null);
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
		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);
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

		byte[] raw = getSmallRaw();

		Content content = new Content(MimeType.RAW, new ByteArrayInputStream(raw));
		assertTrue(manager.storeContent("/rawdata", content, null));
		DatabaseContentInfo content_info = manager.getContentInfo("/rawdata");

		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);
		assertTrue(store.hasContentData(content_info.getContentId()));
	}

	public void testHasContentDataContentEmpty()
	throws Exception
	{
		DatabaseContent manager = DatabaseContentFactory.getInstance(mDatasource);

		Content content = new Content(MimeType.RAW, null);
		assertTrue(manager.storeContent("/rawdata", content, null));
		DatabaseContentInfo content_info = manager.getContentInfo("/rawdata");

		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);
		assertFalse(store.hasContentData(content_info.getContentId()));
	}

	public void testHasContentDataUnknown()
	throws Exception
	{
		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);
		assertFalse(store.hasContentData(3));
	}

	public void testHasContentDataIllegalArgument()
	throws Exception
	{
		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);
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
		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);
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

		byte[] raw = getSmallRaw();

		Content content = new Content(MimeType.RAW, new ByteArrayInputStream(raw));
		assertTrue(manager.storeContent("/rawdata", content, null));
		DatabaseContentInfo content_info = manager.getContentInfo("/rawdata");

		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);
		assertTrue(store.hasContentData(content_info.getContentId()));
		assertTrue(store.deleteContentData(content_info.getContentId()));
		assertFalse(store.hasContentData(content_info.getContentId()));
		assertFalse(store.deleteContentData(content_info.getContentId()));
	}

	public void testDeleteContentDataUnknown()
	throws Exception
	{
		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);
		assertFalse(store.deleteContentData(3));
	}

	public void testDeleteContentDataIllegalArgument()
	throws Exception
	{
		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);
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
		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);
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

		byte[] raw = getSmallRaw();

		Content content = new Content(MimeType.RAW, new ByteArrayInputStream(raw));
		assertTrue(manager.storeContent("/rawdata", content, null));
		DatabaseContentInfo content_info = manager.getContentInfo("/rawdata");

		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);

		assertEquals(store.getSize(content_info.getContentId()), raw.length);
	}

	public void testRetrieveSizeIllegalArgument()
	throws Exception
	{
		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);

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
		DatabaseRawStore store = DatabaseRawStoreFactory.getInstance(mDatasource);
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
