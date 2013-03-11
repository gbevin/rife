/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestGenericQueryManagerManyToOne.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.site.ValidationError;
import com.uwyn.rifetestmodels.MOFirstBean;
import com.uwyn.rifetestmodels.MOSecondBean;
import com.uwyn.rifetestmodels.MOThirdBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import junit.framework.TestCase;

public class TestGenericQueryManagerManyToOne extends TestCase
{
    private Datasource 	mDatasource = null;
	
	private GenericQueryManager<MOFirstBean>	mFirstManager = null;
	private GenericQueryManager<MOSecondBean> 	mSecondManager = null;
	private GenericQueryManager<MOThirdBean> 	mThirdManager = null;
	
	public TestGenericQueryManagerManyToOne(Datasource datasource, String datasourceName, String name)
	{
		super(name);
        mDatasource = datasource;
	}
	
	protected void setUp()
	throws Exception
	{
		mFirstManager = GenericQueryManagerFactory.getInstance(mDatasource, MOFirstBean.class);
		mSecondManager = GenericQueryManagerFactory.getInstance(mDatasource, MOSecondBean.class);
		mThirdManager = GenericQueryManagerFactory.getInstance(mDatasource, MOThirdBean.class);
		
		int poolsize = mDatasource.getPoolsize();
		// disabling pool for firebird
		if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
		{
			mDatasource.setPoolsize(0);
		}
		try
		{
			mThirdManager.install();
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
			mThirdManager.remove();
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
		assertSame(MOFirstBean.class, mFirstManager.getBaseClass());
		assertSame(MOSecondBean.class, mSecondManager.getBaseClass());
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
		MOFirstBean beana = new MOFirstBean();
		MOFirstBean beanb = new MOFirstBean();
		MOFirstBean newbeana = null;
		MOFirstBean newbeanb = null;

		beana.setFirstString("This is my test string");
		beanb.setFirstString("This is my test string B");

		// add the many-to-one relations to the bean instance
		MOSecondBean bean2a = new MOSecondBean();
		bean2a.setSecondString("MOSecondBeanA");

		MOSecondBean bean2b = new MOSecondBean();
		bean2b.setSecondString("MOSecondBeanB");

		MOSecondBean bean2c = new MOSecondBean();
		bean2c.setSecondString("MOSecondBeanC");

		MOThirdBean bean3a = new MOThirdBean();
		bean3a.setThirdString("MOThirdBeanA");

		MOThirdBean bean3b = new MOThirdBean();
		bean3a.setThirdString("MOThirdBeanB");

		beana.setSecondBean(bean2a);
		beana.setSecondBean2(bean2b);
		beana.setThirdBean(bean3a);

		beanb.setSecondBean(bean2b);
		beanb.setSecondBean2(bean2c);
		beanb.setThirdBean(bean3b);

		// assert that the many-to-one relations have not been saved too
		assertNull(bean2a.getIdentifier());
		assertNull(bean2b.getIdentifier());
		assertNull(bean2c.getIdentifier());
		assertNull(bean3a.getId());
		assertNull(bean3b.getId());

		// save the bean instances
		Integer ida = mFirstManager.save(beana);
		Integer idb = mFirstManager.save(beanb);

		// assert that the many-to-one relations have been saved too
		assertNotNull(bean2a.getIdentifier());
		assertNotNull(bean2b.getIdentifier());
		assertNotNull(bean2c.getIdentifier());
		assertNotNull(bean3a.getId());
		assertNotNull(bean3b.getId());

		// restore the bean instances
		newbeana = mFirstManager.restore(ida);
		newbeanb = mFirstManager.restore(idb);

		// assert that the first bean has correctly been restored
		assertTrue(newbeana != null);
		assertTrue(newbeana != beana);
		assertEquals(newbeana.getFirstString(), beana.getFirstString());
		assertEquals(newbeana.getIdentifier(), ida);

		// assert that the first bean's many-to-one relationships have correctly been restored
		MOSecondBean secondbeana = newbeana.getSecondBean();
		assertNotNull(secondbeana);
		assertEquals(bean2a.getIdentifier(), secondbeana.getIdentifier());
		assertEquals(bean2a.getSecondString(), secondbeana.getSecondString());

		// assert that the second bean has correctly been restored
		assertTrue(newbeanb != null);
		assertTrue(newbeanb != beanb);
		assertEquals(newbeanb.getFirstString(), beanb.getFirstString());
		assertEquals(newbeanb.getIdentifier(), idb);

		// assert that the second bean's many-to-one relationships have correctly been restored
		MOSecondBean secondbeanb = newbeanb.getSecondBean();
		assertNotNull(secondbeanb);
		assertEquals(bean2b.getIdentifier(), secondbeanb.getIdentifier());
		assertEquals(bean2b.getSecondString(), secondbeanb.getSecondString());

		// assert that exactly the same instance is returned the next time the property is retrieved
		assertSame(secondbeana, newbeana.getSecondBean());
		assertSame(secondbeana, newbeana.getSecondBean());
		assertSame(secondbeanb, newbeanb.getSecondBean());
		assertSame(secondbeanb, newbeanb.getSecondBean());

		// set the property to null to cause a new instance to be fetched
		newbeana.setSecondBean(null);
		MOSecondBean secondbeanc = newbeana.getSecondBean();
		assertNotNull(secondbeanc);
		assertEquals(secondbeanc.getIdentifier(), secondbeana.getIdentifier());
		assertEquals(secondbeanc.getSecondString(), secondbeana.getSecondString());
		assertNotSame(secondbeanc, secondbeana);

		newbeanb.setSecondBean(null);
		MOSecondBean secondbeand = newbeanb.getSecondBean();
		assertNotNull(secondbeand);
		assertEquals(secondbeand.getIdentifier(), secondbeanb.getIdentifier());
		assertEquals(secondbeand.getSecondString(), secondbeanb.getSecondString());
		assertNotSame(secondbeand, secondbeanb);

		// assert that the other many-to-one relationships have correctly been restored
		MOSecondBean secondbean2a = newbeana.getSecondBean2();
		assertNotNull(secondbean2a);
		assertEquals(bean2b.getIdentifier(), secondbean2a.getIdentifier());
		assertEquals(bean2b.getSecondString(), secondbean2a.getSecondString());

		MOThirdBean thirdbeana = newbeana.getThirdBean();
		assertNotNull(thirdbeana);
		assertEquals(bean3a.getId(), thirdbeana.getId());
		assertEquals(bean3a.getThirdString(), thirdbeana.getThirdString());

		MOSecondBean secondbean2b = newbeanb.getSecondBean2();
		assertNotNull(secondbean2b);
		assertEquals(bean2c.getIdentifier(), secondbean2b.getIdentifier());
		assertEquals(bean2c.getSecondString(), secondbean2b.getSecondString());

		MOThirdBean thirdbeanb = newbeanb.getThirdBean();
		assertNotNull(thirdbeanb);
		assertEquals(bean3b.getId(), thirdbeanb.getId());
		assertEquals(bean3b.getThirdString(), thirdbeanb.getThirdString());

		// perform update with changed many-to-one relationships
		// only the data of those that haven't been saved before will
		// be stored
		beana.setIdentifier(ida);
		beana.setFirstString("This is a new test string");

		bean2a.setSecondString("MOSecondBeanAUpdated");

		MOSecondBean bean2d = new MOSecondBean();
		bean2d.setSecondString("MOSecondBeanD");
		beana.setSecondBean2(bean2d);

		assertEquals(mFirstManager.save(beana), ida.intValue());
		assertEquals(beana.getIdentifier(), ida);

		// restore the updated bean
		newbeana = mFirstManager.restore(ida);

		assertTrue(newbeana != null);
		assertTrue(newbeana != beana);

		// assert that the updated bean has been stored correctly
		assertEquals(newbeana.getFirstString(), "This is a new test string");

		// assert that the many-to-one relationships have correctly been stored and restored
		secondbeana = newbeana.getSecondBean();
		assertNotNull(secondbeana);
		assertEquals(bean2a.getIdentifier(), secondbeana.getIdentifier());
		// the data of this many-to-one association hasn't been updated since the entity already was saved before
		assertFalse(bean2a.getSecondString().equals(secondbeana.getSecondString()));

		MOSecondBean secondbean3 = newbeana.getSecondBean2();
		assertNotNull(secondbean3);
		assertEquals(bean2d.getIdentifier(), secondbean3.getIdentifier());
		assertEquals(bean2d.getSecondString(), secondbean3.getSecondString());

		thirdbeana = newbeana.getThirdBean();
		assertNotNull(thirdbeana);
		assertEquals(bean3a.getId(), thirdbeana.getId());
		assertEquals(bean3a.getThirdString(), thirdbeana.getThirdString());
	}

	public void testSaveRestoreConstrainedAssociation()
	{
		MOFirstBean bean1a = new MOFirstBean();
		bean1a.setFirstString("This is my test string");
		MOSecondBean bean2 = new MOSecondBean();
		bean2.setSecondString("MOSecondBeanA");
		bean1a.setSecondBean(bean2);

		// save the bean instance
		Integer id = mFirstManager.save(bean1a);

		// save a second instance of the first bean type
		MOFirstBean bean1b = new MOFirstBean();
		bean1b.setFirstString("This is my test string B");
		bean1b.setSecondBean(bean2);
		mFirstManager.save(bean1b);

		// restore the second bean
		MOSecondBean secondbean = mSecondManager.restore(bean2.getIdentifier());

		// assert that the second bean association links are correct
		Collection<MOFirstBean> firstbeans = secondbean.getFirstBeans();
		assertNotNull(firstbeans);
		assertEquals(2, firstbeans.size());
		for (MOFirstBean bean_assoc_restored : firstbeans)
		{
			if (bean_assoc_restored.getIdentifier().equals(bean1a.getIdentifier()))
			{
				assertEquals(bean_assoc_restored.getFirstString(), bean1a.getFirstString());
			}
			else if (bean_assoc_restored.getIdentifier().equals(bean1b.getIdentifier()))
			{
				assertEquals(bean_assoc_restored.getFirstString(), bean1b.getFirstString());
			}
			else
			{
				fail();
			}
			assertEquals(bean2.getIdentifier(), bean_assoc_restored.getSecondBean().getIdentifier());
			assertEquals(bean2.getSecondString(), bean_assoc_restored.getSecondBean().getSecondString());
		}

		// store the second bean with updated links
		firstbeans.remove(firstbeans.iterator().next());
		mSecondManager.save(secondbean);
		secondbean = mSecondManager.restore(bean2.getIdentifier());
		firstbeans = secondbean.getFirstBeans();
		assertNotNull(firstbeans);
		assertEquals(1, firstbeans.size());

		// save a third instance of the first bean type and an updated
		// version of the first instance, which will not be saved
		MOFirstBean bean1c = new MOFirstBean();
		bean1c.setFirstString("This is my test string C");
		assertNull(bean1c.getIdentifier());
		List<MOFirstBean> firstbeans2 = new ArrayList<MOFirstBean>();
		firstbeans2.add(bean1a);
		bean1a.setFirstString("This is my test string updated");
		firstbeans2.add(bean1c);
		secondbean.setFirstBeans(firstbeans2);
		mSecondManager.save(secondbean);
		assertNotNull(bean1c.getIdentifier());

		secondbean = mSecondManager.restore(bean2.getIdentifier());
		firstbeans = secondbean.getFirstBeans();
		assertNotNull(firstbeans);
		assertEquals(2, firstbeans.size());
		for (MOFirstBean bean_assoc_restored : firstbeans)
		{
			if (bean_assoc_restored.getIdentifier().equals(bean1a.getIdentifier()))
			{
				assertEquals(bean_assoc_restored.getFirstString(), "This is my test string");
				assertFalse(bean_assoc_restored.getFirstString().equals(bean1a.getFirstString()));
			}
			else if (bean_assoc_restored.getIdentifier().equals(bean1c.getIdentifier()))
			{
				assertEquals(bean_assoc_restored.getFirstString(), bean1c.getFirstString());
			}
			else
			{
				fail();
			}
			assertEquals(bean2.getIdentifier(), bean_assoc_restored.getSecondBean().getIdentifier());
			assertEquals(bean2.getSecondString(), bean_assoc_restored.getSecondBean().getSecondString());
		}
	}

	public void testDelete()
	{
		assertEquals(0, mSecondManager.count());
		assertEquals(0, mThirdManager.count());

		MOFirstBean bean = new MOFirstBean();

		bean.setFirstString("This is my test string");

		// add the many-to-one relations to the bean instance
		MOSecondBean bean2a = new MOSecondBean();
		bean2a.setSecondString("MOSecondBeanA");

		MOThirdBean bean3 = new MOThirdBean();
		bean3.setThirdString("MOThirdBean");

		bean.setSecondBean(bean2a);
		bean.setThirdBean(bean3);

		// save the bean instance
		Integer id1 = mFirstManager.save(bean);

		// ensure that everything was saved correctly
		assertTrue(mFirstManager.restore(id1) != null);
		assertEquals(1, mSecondManager.count());
		assertEquals(1, mThirdManager.count());

		// delete the first bean
		mFirstManager.delete(id1);

		// ensure that everything was deleted correctly
		assertNull(mFirstManager.restore(id1));
		assertEquals(1, mSecondManager.count());
		assertEquals(1, mThirdManager.count());

		// add another many-to-one relationship
		MOSecondBean bean2c = new MOSecondBean();
		bean2c.setSecondString("MOSecondBeanC");

		bean.setSecondBean2(bean2c);

		// save the bean instance again
		Integer id2 = mFirstManager.save(bean);

		// ensure that everything was saved correctly
		assertTrue(mFirstManager.restore(id2) != null);
		assertEquals(2, mSecondManager.count());
		assertEquals(1, mThirdManager.count());

		// delete the second bean
		mFirstManager.delete(id2);

		// ensure that everything was deleted correctly
		assertNull(mFirstManager.restore(id2));
		assertEquals(2, mSecondManager.count());
		assertEquals(1, mThirdManager.count());
	}

	public void testDeleteAssociation()
	{
		final MOFirstBean bean1a = new MOFirstBean();
		bean1a.setFirstString("This is my test string");

		final MOFirstBean bean1b = new MOFirstBean();
		bean1b.setFirstString("This is my test string B");

		MOSecondBean bean2 = new MOSecondBean();
		bean2.setSecondString("MOSecondBeanA");
		bean2.setFirstBeans(new ArrayList<MOFirstBean>() {{ add(bean1a); add(bean1b); }});

		// save the second bean
		assertTrue(mSecondManager.save(bean2) > -1);

		// restore the second bean
		MOSecondBean secondbean = mSecondManager.restore(bean2.getIdentifier());
		assertEquals(2, secondbean.getFirstBeans().size());

		// delete the second bean
		assertTrue(mSecondManager.delete(bean2.getIdentifier()));
	}
	
	public void testValidationContextManyToOne()
	{
		MOFirstBean bean = new MOFirstBean();
		
		bean.setFirstString("This is my test string");
		
		// add the many-to-one relations to the bean instance
		MOSecondBean bean2a = new MOSecondBean();
		bean2a.setIdentifier(23);
		bean2a.setSecondString("MOSecondBeanA");
		
		MOSecondBean bean2b = new MOSecondBean();
		bean2b.setIdentifier(24);
		bean2b.setSecondString("MOSecondBeanB");
		
		bean.setSecondBean(bean2a);
		bean.setSecondBean2(bean2b);
		
		// validate the bean instance
		ValidationError error;
		assertFalse(bean.validate(mFirstManager));
		assertEquals(2, bean.getValidationErrors().size());
		Iterator<ValidationError> error_it = bean.getValidationErrors().iterator();
		error = (ValidationError)error_it.next();
		assertEquals(error.getSubject(), "secondBean");
		assertEquals(error.getIdentifier(), ValidationError.IDENTIFIER_INVALID);
		error = (ValidationError)error_it.next();
		assertEquals(error.getSubject(), "secondBean2");
		assertEquals(error.getIdentifier(), ValidationError.IDENTIFIER_INVALID);
		assertFalse(error_it.hasNext());
		
		bean.resetValidation();
		
		// store the first associated bean
		mSecondManager.save(bean2a);
		
		// validate the bean instance again
		assertFalse(bean.validate(mFirstManager));
		assertEquals(1, bean.getValidationErrors().size());
		error_it = bean.getValidationErrors().iterator();
		error = (ValidationError)error_it.next();
		assertEquals(error.getSubject(), "secondBean2");
		assertEquals(error.getIdentifier(), ValidationError.IDENTIFIER_INVALID);
		assertFalse(error_it.hasNext());
		
		// store the second associated bean
		mSecondManager.save(bean2b);
		
		bean.resetValidation();
		
		// validate the bean instance a last time
		assertTrue(bean.validate(mFirstManager));
	}
	
	public void testValidationContextManyToOneAssociation()
	{
		final MOFirstBean bean1a = new MOFirstBean();
		bean1a.setIdentifier(23);
		bean1a.setFirstString("This is my test string");

		final MOFirstBean bean1b = new MOFirstBean();
		bean1b.setIdentifier(27);
		bean1b.setFirstString("This is my test string B");

		MOSecondBean bean2 = new MOSecondBean();
		bean2.setSecondString("MOSecondBeanA");
		bean2.setFirstBeans(new ArrayList<MOFirstBean>() {{ add(bean1a); add(bean1b); }});

		// validate the bean instance
		ValidationError error;
		assertFalse(bean2.validate(mSecondManager));
		assertEquals(1, bean2.getValidationErrors().size());
		Iterator<ValidationError> error_it = bean2.getValidationErrors().iterator();
		error = (ValidationError)error_it.next();
		assertEquals(error.getSubject(), "firstBeans");
		assertEquals(error.getIdentifier(), ValidationError.IDENTIFIER_INVALID);
		assertFalse(error_it.hasNext());

		bean2.resetValidation();

		// store the first associated bean
		mFirstManager.save(bean1a);

		// validate the bean instance
		assertFalse(bean2.validate(mSecondManager));
		assertEquals(1, bean2.getValidationErrors().size());
		error_it = bean2.getValidationErrors().iterator();
		error = (ValidationError)error_it.next();
		assertEquals(error.getSubject(), "firstBeans");
		assertEquals(error.getIdentifier(), ValidationError.IDENTIFIER_INVALID);
		assertFalse(error_it.hasNext());

		bean2.resetValidation();

		// store the first associated bean
		mFirstManager.save(bean1b);

		// validate the bean instance a last time
		assertTrue(bean2.validate(mSecondManager));
	}
}
