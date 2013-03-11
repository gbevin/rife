/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDatabaseTextStore.java 3936 2008-04-26 12:05:37Z gbevin $
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
import com.uwyn.rife.tools.exceptions.InnerClassException;
import junit.framework.TestCase;

import java.sql.Timestamp;
import java.util.Date;

public class TestDatabaseTextStore extends TestCase
{
	private Datasource 	mDatasource = null;

	public TestDatabaseTextStore(Datasource datasource, String datasourceName, String name)
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
		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);
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
		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);
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
		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);
		assertTrue(store.getSupportedMimeTypes().size() > 0);
	}

	public void testGetContentType()
	{
		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);
		ContentInfo info = new ContentInfo();
		info.setMimeType(MimeType.APPLICATION_XHTML.toString());
		assertNotNull(store.getContentType(info));
		info.setMimeType(MimeType.IMAGE_GIF.toString());
		assertNull(store.getContentType(info));
	}

	public void testGetFormatter()
	{
		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);
		assertNotNull(store.getFormatter(MimeType.APPLICATION_XHTML, false));
		assertNotNull(store.getFormatter(MimeType.TEXT_PLAIN, false));
		assertNull(store.getFormatter(MimeType.IMAGE_GIF, true));
	}

	public void testStoreContentDataXhtml()
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

		final String data = "<i>cool beans</i><p>hôt <a href=\"http://uwyn.com\">chili</a></p>";

		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);
		Content content = new Content(MimeType.APPLICATION_XHTML, data).fragment(true);
        assertTrue(store.storeContentData(id[0], content, null));

		store.useContentData(id[0], new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					String data_encoded = "<i>cool beans</i><p>h&ocirc;t <a href=\"http://uwyn.com\">chili</a></p>";
					assertEquals(data_encoded, contentData);
					return null;
				}
			});
	}

	public void testStoreContentDataXhtmlLarge()
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

		String data_part = "<i>cool beans</i><p>hôt <a href=\"http://uwyn.com\">chili</a></p>";
		final StringBuffer data = new StringBuffer();
		for (int i = 0; i < 14000; i++)
		{
			data.append(data_part);
		}

		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);
		Content content = new Content(MimeType.APPLICATION_XHTML, data.toString()).fragment(true);
        assertTrue(store.storeContentData(id[0], content, null));

		store.useContentData(id[0], new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					String data_encoded_part = "<i>cool beans</i><p>h&ocirc;t <a href=\"http://uwyn.com\">chili</a></p>";
					StringBuffer data_encoded = new StringBuffer();
					for (int i = 0; i < 14000; i++)
					{
						data_encoded.append(data_encoded_part);
					}
					assertEquals(data_encoded.toString(), contentData);
					return null;
				}
			});
	}

	public void testStoreContentDataTextPlain()
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
					content_info.setMimeType(MimeType.TEXT_PLAIN.toString());
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

		final String data = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Suspendisse lacinia, neque eget euismod scelerisque, arcu est accumsan lectus, id accumsan elit nunc eget elit.";

		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);
		Content content = new Content(MimeType.TEXT_PLAIN, data).fragment(true);
        assertTrue(store.storeContentData(id[0], content, null));

		store.useContentData(id[0], new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					assertTrue(data.equals(contentData));
					return null;
				}
			});
	}

	public void testStoreContentDataTextPlainLarge()
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
					content_info.setMimeType(MimeType.TEXT_PLAIN.toString());
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

		String data_part = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Suspendisse lacinia, neque eget euismod scelerisque, arcu est accumsan lectus, id accumsan elit nunc eget elit.";
		final StringBuffer data = new StringBuffer();
		for (int i = 0; i < 5000; i++)
		{
			data.append(data_part);
		}

		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);
		Content content = new Content(MimeType.TEXT_PLAIN, data.toString()).fragment(true);
        assertTrue(store.storeContentData(id[0], content, null));

		store.useContentData(id[0], new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					assertTrue(data.toString().equals(contentData));
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

		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);
		Content content = new Content(MimeType.APPLICATION_XHTML, null);
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

		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);
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

		final String data = "<i>cool beans</i><p>hot <a href=\"http://uwyn.com\">chili</a></p>";

		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);
		Content content = new Content(MimeType.IMAGE_PNG, data).fragment(true);
        assertTrue(store.storeContentData(id[0], content, null));

		store.useContentData(id[0], new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					assertEquals(data, contentData);
					return null;
				}
			});
	}

	public void testStoreContentDataIllegalArgument()
	throws Exception
	{
		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);

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
			store.storeContentData(1, new Content(MimeType.APPLICATION_XHTML, new Object()), null);
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
		String data = "<i>cool beans</i><p>hot <a href=\"http://uwyn.com\">chili</a></p>";
		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);
		Content content = new Content(MimeType.APPLICATION_XHTML, data).fragment(true);
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

		final String data = "<i>cool beans</i><p>hot <a href=\"http://uwyn.com\">chili</a></p>";

		Content content = new Content(MimeType.APPLICATION_XHTML, data).fragment(true);
		assertTrue(manager.storeContent("/testxhtml", content, null));
		DatabaseContentInfo content_info = manager.getContentInfo("/testxhtml");

		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);

		store.useContentData(content_info.getContentId(), new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					assertEquals(data, contentData);
					return null;
				}
			});
	}

	public void testUseContentDataUnknown()
	throws Exception
	{
		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);

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
		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);

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
		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);
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

		String data = "<i>cool beans</i><p>hot <a href=\"http://uwyn.com\">chili</a></p>";
		Content content = new Content(MimeType.APPLICATION_XHTML, data).fragment(true);
		assertTrue(manager.storeContent("/testxhtml", content, null));
		DatabaseContentInfo content_info = manager.getContentInfo("/testxhtml");

		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);
		assertTrue(store.hasContentData(content_info.getContentId()));
	}

	public void testHasContentDataContentEmpty()
	throws Exception
	{
		DatabaseContent manager = DatabaseContentFactory.getInstance(mDatasource);

		Content content = new Content(MimeType.APPLICATION_XHTML, null);
		assertTrue(manager.storeContent("/testxhtml", content, null));
		DatabaseContentInfo content_info = manager.getContentInfo("/testxhtml");

		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);
		assertFalse(store.hasContentData(content_info.getContentId()));
	}

	public void testHasContentDataUnknown()
	throws Exception
	{
		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);
		assertFalse(store.hasContentData(3));
	}

	public void testHasContentDataIllegalArgument()
	throws Exception
	{
		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);
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
		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);
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

		String data = "<i>cool beans</i><p>hot <a href=\"http://uwyn.com\">chili</a></p>";
		Content content = new Content(MimeType.APPLICATION_XHTML, data).fragment(true);
		assertTrue(manager.storeContent("/testxhtml", content, null));
		DatabaseContentInfo content_info = manager.getContentInfo("/testxhtml");

		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);
		assertTrue(store.hasContentData(content_info.getContentId()));
		assertTrue(store.deleteContentData(content_info.getContentId()));
		assertFalse(store.hasContentData(content_info.getContentId()));
		assertFalse(store.deleteContentData(content_info.getContentId()));
	}

	public void testDeleteContentDataUnknown()
	throws Exception
	{
		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);
		assertFalse(store.deleteContentData(3));
	}

	public void testDeleteContentDataIllegalArgument()
	throws Exception
	{
		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);
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
		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);
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

		String data = "<i>cool beans</i><p>hot <a href=\"http://uwyn.com\">chili</a></p>";
		Content content = new Content(MimeType.APPLICATION_XHTML, data).fragment(true);
		assertTrue(manager.storeContent("/testxhtml", content, null));
		DatabaseContentInfo content_info = manager.getContentInfo("/testxhtml");

		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);

		assertEquals(store.getSize(content_info.getContentId()), data.length());
	}

	public void testRetrieveSizeIllegalArgument()
	throws Exception
	{
		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);

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
		DatabaseTextStore store = DatabaseTextStoreFactory.getInstance(mDatasource);
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
