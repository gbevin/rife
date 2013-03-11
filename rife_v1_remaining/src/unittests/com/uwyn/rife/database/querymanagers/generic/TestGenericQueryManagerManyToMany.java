/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestGenericQueryManagerManyToMany.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbQueryManager;
import com.uwyn.rife.database.queries.Select;
import com.uwyn.rife.database.querymanagers.generic.beans.MMFirstBean;
import com.uwyn.rife.database.querymanagers.generic.beans.MMSecondBean;
import com.uwyn.rife.site.ValidationError;
import java.util.ArrayList;
import java.util.Collection;
import junit.framework.TestCase;

public class TestGenericQueryManagerManyToMany extends TestCase
{
    private Datasource 	mDatasource = null;
	
	private GenericQueryManager<MMFirstBean>	mFirstManager = null;
	private GenericQueryManager<MMSecondBean> 	mSecondManager = null;
	
	public TestGenericQueryManagerManyToMany(Datasource datasource, String datasourceName, String name)
	{
		super(name);
        mDatasource = datasource;
	}
	
	protected void setUp()
	throws Exception
	{
		mFirstManager = GenericQueryManagerFactory.getInstance(mDatasource, MMFirstBean.class);
		mSecondManager = GenericQueryManagerFactory.getInstance(mDatasource, MMSecondBean.class);
		
		int poolsize = mDatasource.getPoolsize();
		// disabling pool for firebird
		if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
		{
			mDatasource.setPoolsize(0);
		}
		try
		{
			mSecondManager.install();
			mFirstManager.install();
		}
		finally
		{
			if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
			{
				mDatasource.setPoolsize(poolsize);
			}
		}
 	}
	
	protected void tearDown()
	throws Exception
	{
		int poolsize = mDatasource.getPoolsize();
		// disabling pool for firebird
		if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
		{
			mDatasource.setPoolsize(0);
		}
		try
		{
			mFirstManager.remove();
			mSecondManager.remove();
		}
		finally
		{
			if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
			{
				mDatasource.setPoolsize(poolsize);
			}
		}
	}
	
	public void testGetBaseClass()
	{
		assertSame(MMFirstBean.class, mFirstManager.getBaseClass());
		assertSame(MMSecondBean.class, mSecondManager.getBaseClass());
	}
	
	public void testInstallCustomQuery()
	{
		int poolsize = mDatasource.getPoolsize();
		// disabling pool for firebird
		if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
		{
			mDatasource.setPoolsize(0);
		}
		try
		{
			mFirstManager.remove();
			mSecondManager.remove();
			
			mSecondManager.install(mSecondManager.getInstallTableQuery());
			mFirstManager.install(mFirstManager.getInstallTableQuery());
		}
		finally
		{
			if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
			{
				mDatasource.setPoolsize(poolsize);
			}
		}
	}
	
	public void testSaveRestoreConstrained()
	{
		MMFirstBean bean = new MMFirstBean();
		MMFirstBean newbean = null;
		
		bean.setFirstString("This is my test string");
		
		// add the many to many relations to the bean instance
		MMSecondBean bean2a = new MMSecondBean();
		bean2a.setSecondString("MMSecondBeanA");
		
		MMSecondBean bean2b = new MMSecondBean();
		bean2b.setSecondString("MMSecondBeanB");
		
		MMSecondBean bean2c = new MMSecondBean();
		bean2c.setSecondString("MMSecondBeanC");
		
		Collection<MMSecondBean> secondbeans = new ArrayList<MMSecondBean>();
		secondbeans.add(bean2a);
		secondbeans.add(bean2b);
		secondbeans.add(bean2c);
		bean.setSecondBeans(secondbeans);
		
		// assert that the many to many relations have not been saved too
		assertNull(bean2a.getIdentifier());
		assertNull(bean2b.getIdentifier());
		assertNull(bean2c.getIdentifier());
		
		// save the bean instance
		Integer id = mFirstManager.save(bean);
		
		// assert that the many to many relations have been saved too
		assertNotNull(bean2a.getIdentifier());
		assertNotNull(bean2b.getIdentifier());
		assertNotNull(bean2c.getIdentifier());
		
		// restore the bean instance
		newbean = mFirstManager.restore(id);
		
		// assert that the bean has correctly been restored
		assertTrue(newbean != null);
		assertTrue(newbean != bean);
		assertEquals(newbean.getFirstString(), bean.getFirstString());
		assertEquals(newbean.getIdentifier(), id);
		
		// assert that the many to many relationships have correctly been restored
		Collection<MMSecondBean> secondbeans_restored = newbean.getSecondBeans();
		boolean bean2a_found = false;
		boolean bean2b_found = false;
		boolean bean2c_found = false;
		for (MMSecondBean secondbean : secondbeans_restored)
		{
			if ("MMSecondBeanA".equals(secondbean.getSecondString()))
			{
				assertFalse(bean2a_found);
				assertEquals(bean2a.getIdentifier(), secondbean.getIdentifier());
				assertEquals(bean2a.getSecondString(), secondbean.getSecondString());
				bean2a_found = true;
			}
			else if ("MMSecondBeanB".equals(secondbean.getSecondString()))
			{
				assertFalse(bean2b_found);
				assertEquals(bean2b.getIdentifier(), secondbean.getIdentifier());
				assertEquals(bean2b.getSecondString(), secondbean.getSecondString());
				bean2b_found = true;
			}
			else if ("MMSecondBeanC".equals(secondbean.getSecondString()))
			{
				assertFalse(bean2c_found);
				assertEquals(bean2c.getIdentifier(), secondbean.getIdentifier());
				assertEquals(bean2c.getSecondString(), secondbean.getSecondString());
				bean2c_found = true;
			}
			
			assertNotNull(secondbean.getFirstBeans());
			assertEquals(1, secondbean.getFirstBeans().size());
			
			MMFirstBean firstbean = secondbean.getFirstBeans().iterator().next();
			assertEquals(newbean.getIdentifier(), firstbean.getIdentifier());
			assertEquals(newbean.getFirstString(), firstbean.getFirstString());
		}
		assertTrue(bean2a_found);
		assertTrue(bean2b_found);
		assertTrue(bean2c_found);
		
		// perform update with changed many to many relationships
		// only the data of those that haven't been saved before will
		// be stored
		bean.setIdentifier(id);
		bean.setFirstString("This is a new test string");
		
		bean2a.setSecondString("MMSecondBeanAUpdated");
		
		MMSecondBean bean2d = new MMSecondBean();
		bean2d.setSecondString("MMSecondBeanD");
		secondbeans = new ArrayList<MMSecondBean>();
		secondbeans.add(bean2a);
		secondbeans.add(bean2c);
		secondbeans.add(bean2d);
		bean.setSecondBeans(secondbeans);
		
		assertEquals(mFirstManager.save(bean), id.intValue());
		assertEquals(bean.getIdentifier(), id);
		
		// restore the updated bean
		newbean = mFirstManager.restore(id);
		
		assertTrue(newbean != null);
		assertTrue(newbean != bean);
		
		// assert that the updated bean has been stored correctly
		assertEquals(newbean.getFirstString(), "This is a new test string");
		
		// assert that the many to many relationships have correctly been stored and restored
		secondbeans_restored = newbean.getSecondBeans();
		bean2a_found = false;
		bean2b_found = false;
		bean2c_found = false;
		boolean bean2d_found = false;
		for (MMSecondBean secondbean : secondbeans_restored)
		{
			if ("MMSecondBeanA".equals(secondbean.getSecondString()))
			{
				assertFalse(bean2a_found);
				assertEquals(bean2a.getIdentifier(), secondbean.getIdentifier());
				// the data of this many to many association hasn't been updated since the entity already was saved before
				assertFalse(bean2a.getSecondString().equals(secondbean.getSecondString()));
				bean2a_found = true;
			}
			else if ("MMSecondBeanB".equals(secondbean.getSecondString()))
			{
				bean2b_found = true;
			}
			else if ("MMSecondBeanC".equals(secondbean.getSecondString()))
			{
				assertFalse(bean2c_found);
				assertEquals(bean2c.getIdentifier(), secondbean.getIdentifier());
				assertEquals(bean2c.getSecondString(), secondbean.getSecondString());
				bean2c_found = true;
			}
			else if ("MMSecondBeanD".equals(secondbean.getSecondString()))
			{
				assertFalse(bean2d_found);
				assertEquals(bean2d.getIdentifier(), secondbean.getIdentifier());
				assertEquals(bean2d.getSecondString(), secondbean.getSecondString());
				bean2d_found = true;
			}
			
			assertNotNull(secondbean.getFirstBeans());
			assertEquals(1, secondbean.getFirstBeans().size());
			
			MMFirstBean firstbean = secondbean.getFirstBeans().iterator().next();
			assertEquals(newbean.getIdentifier(), firstbean.getIdentifier());
			assertEquals(newbean.getFirstString(), firstbean.getFirstString());
		}
		assertTrue(bean2a_found);
		assertFalse(bean2b_found);
		assertTrue(bean2c_found);
		assertTrue(bean2d_found);
	}
	
	public void testDelete()
	{
		MMFirstBean bean = new MMFirstBean();
		
		bean.setFirstString("This is my test string");
		
		// add the many to many relations to the bean instance
		MMSecondBean bean2a = new MMSecondBean();
		bean2a.setSecondString("MMSecondBeanA");
		
		MMSecondBean bean2b = new MMSecondBean();
		bean2b.setSecondString("MMSecondBeanB");
		
		Collection<MMSecondBean> secondbeans = new ArrayList<MMSecondBean>();
		secondbeans.add(bean2a);
		secondbeans.add(bean2b);
		bean.setSecondBeans(secondbeans);
		
		// save the bean instance
		Integer id1 = mFirstManager.save(bean);
		
		// ensure that everything was saved correctly
		assertTrue(mFirstManager.restore(id1) != null);
		assertEquals(2, new DbQueryManager(mDatasource)
					 .executeGetFirstInt(new Select(mDatasource)
										 .field("count(*)")
										 .from("mmfirstbean_mmsecondbean")
										 .where("mmfirstbean_identifier", "=", id1)));
		assertEquals(2, mSecondManager.count());
		
		// delete the first bean
		mFirstManager.delete(id1);
		
		// ensure that everything was deleted correctly
		assertNull(mFirstManager.restore(id1));
		assertEquals(0, new DbQueryManager(mDatasource)
					 .executeGetFirstInt(new Select(mDatasource)
										 .field("count(*)")
										 .from("mmfirstbean_mmsecondbean")
										 .where("mmfirstbean_identifier", "=", id1)));
		assertEquals(2, mSecondManager.count());

		// add another many to many relationship
		MMSecondBean bean2c = new MMSecondBean();
		bean2b.setSecondString("MMSecondBeanC");
		secondbeans.add(bean2c);
		
		// save the bean instance again
		Integer id2 = mFirstManager.save(bean);
		
		// ensure that everything was saved correctly
		assertTrue(mFirstManager.restore(id2) != null);
		assertEquals(3, new DbQueryManager(mDatasource)
					 .executeGetFirstInt(new Select(mDatasource)
										 .field("count(*)")
										 .from("mmfirstbean_mmsecondbean")
										 .where("mmfirstbean_identifier", "=", id2)));
		assertEquals(3, mSecondManager.count());
		
		// delete the second bean
		mFirstManager.delete(id2);
		
		// ensure that everything was deleted correctly
		assertNull(mFirstManager.restore(id2));
		assertEquals(0, new DbQueryManager(mDatasource)
					 .executeGetFirstInt(new Select(mDatasource)
										 .field("count(*)")
										 .from("mmfirstbean_mmsecondbean")
										 .where("mmfirstbean_identifier", "=", id2)));
		assertEquals(3, mSecondManager.count());
	}
	
	public void testValidationContextManyToMany()
	{
		MMFirstBean bean = new MMFirstBean();
		
		bean.setFirstString("This is my test string");
		
		// add the many to many relations to the bean instance
		MMSecondBean bean2a = new MMSecondBean();
		bean2a.setIdentifier(23);
		bean2a.setSecondString("MMSecondBeanA");
		
		MMSecondBean bean2b = new MMSecondBean();
		bean2b.setIdentifier(24);
		bean2b.setSecondString("MMSecondBeanB");
		
		Collection<MMSecondBean> secondbeans = new ArrayList<MMSecondBean>();
		secondbeans.add(bean2a);
		secondbeans.add(bean2b);
		bean.setSecondBeans(secondbeans);
		
		// validate the bean instance
		ValidationError error;
		assertFalse(bean.validate(mFirstManager));
		error = (ValidationError)bean.getValidationErrors().iterator().next();
		assertEquals(error.getSubject(), "secondBeans");
		assertEquals(error.getIdentifier(), ValidationError.IDENTIFIER_INVALID);

		bean.resetValidation();

		// store the first associated bean
		mSecondManager.save(bean2a);
		
		// validate the bean instance again
		assertFalse(bean.validate(mFirstManager));
		error = (ValidationError)bean.getValidationErrors().iterator().next();
		assertEquals(error.getSubject(), "secondBeans");
		assertEquals(error.getIdentifier(), ValidationError.IDENTIFIER_INVALID);
		
		// store the second associated bean
		mSecondManager.save(bean2b);
		
		bean.resetValidation();
		
		// validate the bean instance a last time
		assertTrue(bean.validate(mFirstManager));
	}
	
	public void testValidationContextManyToManyAssociation()
	{
		MMSecondBean bean2 = new MMSecondBean();		
		bean2.setSecondString("This is my test string");
		
		// add the many to many association relations to the bean instance
		MMFirstBean bean1a = new MMFirstBean();
		bean1a.setIdentifier(23);
		bean1a.setFirstString("MMFirstBeanA");
		
		MMFirstBean bean1b = new MMFirstBean();
		bean1b.setIdentifier(24);
		bean1b.setFirstString("MMFirstBeanB");
		
		Collection<MMFirstBean> firstbeans = new ArrayList<MMFirstBean>();
		firstbeans.add(bean1a);
		firstbeans.add(bean1b);
		bean2.setFirstBeans(firstbeans);
		
		// validate the bean instance
		ValidationError error;
		assertFalse(bean2.validate(mSecondManager));
		error = (ValidationError)bean2.getValidationErrors().iterator().next();
		assertEquals(error.getSubject(), "firstBeans");
		assertEquals(error.getIdentifier(), ValidationError.IDENTIFIER_INVALID);

		bean2.resetValidation();

		// store the first associated bean
		mFirstManager.save(bean1a);
		
		// validate the bean instance again
		assertFalse(bean2.validate(mSecondManager));
		error = (ValidationError)bean2.getValidationErrors().iterator().next();
		assertEquals(error.getSubject(), "firstBeans");
		assertEquals(error.getIdentifier(), ValidationError.IDENTIFIER_INVALID);
		
		// store the second associated bean
		mFirstManager.save(bean1b);
		
		bean2.resetValidation();
		
		// validate the bean instance a last time
		assertTrue(bean2.validate(mSecondManager));
	}
}
