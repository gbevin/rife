/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestGenericQueryManagerCallbacks.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.querymanagers.generic.beans.CallbacksBean;
import com.uwyn.rife.database.querymanagers.generic.beans.CallbacksProviderBean;
import com.uwyn.rife.database.querymanagers.generic.beans.CallbacksSparseBean;
import com.uwyn.rife.tools.StringUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;

public class TestGenericQueryManagerCallbacks extends TestCase
{
    private Datasource 	mDatasource = null;

	private GenericQueryManager<CallbacksBean>			mCallbacksManager = null;
	private GenericQueryManager<CallbacksSparseBean>	mCallbacksSparseManager = null;
	private GenericQueryManager<CallbacksProviderBean>	mCallbacksProviderManager = null;

	public TestGenericQueryManagerCallbacks(Datasource datasource, String datasourceName, String name)
	{
		super(name);
        mDatasource = datasource;
	}

	protected void setUp()
	throws Exception
	{
		mCallbacksManager = GenericQueryManagerFactory.getInstance(mDatasource, CallbacksBean.class);
		mCallbacksSparseManager = GenericQueryManagerFactory.getInstance(mDatasource, CallbacksSparseBean.class);
		mCallbacksProviderManager = GenericQueryManagerFactory.getInstance(mDatasource, CallbacksProviderBean.class);

		int poolsize = mDatasource.getPoolsize();
		// disabling pool for firebird
		if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
		{
			mDatasource.setPoolsize(0);
		}
		try
		{
			AggregatingCallbacksBeanListener listener = new AggregatingCallbacksBeanListener();
			mCallbacksManager.addListener(listener);
			mCallbacksManager.install();
			assertEquals(1, listener.getHistory().size());
			assertEquals("installed0", listener.getHistory().entrySet().iterator().next().getKey());
			
			mCallbacksSparseManager.install();
			mCallbacksProviderManager.install();
		}
		finally
		{
			mCallbacksManager.removeListeners();
			
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
			AggregatingCallbacksBeanListener listener = new AggregatingCallbacksBeanListener();
			mCallbacksManager.addListener(listener);
			mCallbacksManager.remove();
			assertEquals(1, listener.getHistory().size());
			assertEquals("removed0", listener.getHistory().entrySet().iterator().next().getKey());

			mCallbacksSparseManager.remove();
			mCallbacksProviderManager.remove();
		}
		finally
		{
			mCallbacksManager.removeListeners();
			
			if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
			{
				mDatasource.setPoolsize(poolsize);
			}
		}
	}
	
	public void testGetBaseClass()
	{
		assertSame(CallbacksBean.class, mCallbacksManager.getBaseClass());
		assertSame(CallbacksSparseBean.class, mCallbacksSparseManager.getBaseClass());
		assertSame(CallbacksProviderBean.class, mCallbacksProviderManager.getBaseClass());
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
			mCallbacksManager.remove();
			mCallbacksSparseManager.remove();
			mCallbacksProviderManager.remove();

			mCallbacksManager.install(mCallbacksManager.getInstallTableQuery());
			mCallbacksSparseManager.install(mCallbacksSparseManager.getInstallTableQuery());
			mCallbacksProviderManager.install(mCallbacksProviderManager.getInstallTableQuery());
		}
		finally
		{
			if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
			{
				mDatasource.setPoolsize(poolsize);
			}
		}
	}

	public void testValidateCallbacks()
	{
		CallbacksBean.clearExecuteCallbacks();

		CallbacksBean bean = new CallbacksBean();

		bean.setTestString("This is my test string");

		mCallbacksManager.validate(bean);
		assertEquals("beforeValidate -1;This is my test string\n"+
					 "afterValidate -1;This is my test string", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		CallbacksBean.clearExecuteCallbacks();
		assertEquals("", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		bean.setTestString("This is a new test string");

		mCallbacksManager.validate(bean);
		assertEquals("beforeValidate -1;This is a new test string\n"+
					 "afterValidate -1;This is a new test string", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		CallbacksBean.clearExecuteCallbacks();
		assertEquals("", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		bean.setId(999999);
		bean.setTestString("This is another test string");

		mCallbacksManager.validate(bean);
		assertEquals("beforeValidate 999999;This is another test string\n"+
					 "afterValidate 999999;This is another test string", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		CallbacksBean.clearExecuteCallbacks();
	}

	public void testValidateCallbacksReturns()
	{
		CallbacksBean.clearExecuteCallbacks();

		CallbacksBean bean = new CallbacksBean();

		bean.setTestString("This is my test string");

		bean.setBeforeValidateReturn(false);
		mCallbacksManager.validate(bean);
		assertEquals("beforeValidate -1;This is my test string", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		CallbacksBean.clearExecuteCallbacks();
		assertEquals("", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		bean.setBeforeValidateReturn(true);
		bean.setAfterValidateReturn(false);
		mCallbacksManager.validate(bean);
		assertEquals("beforeValidate -1;This is my test string\n"+
					 "afterValidate -1;This is my test string", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		CallbacksBean.clearExecuteCallbacks();
	}

	public void testSaveCallbacks()
	{
		CallbacksBean.clearExecuteCallbacks();

		CallbacksBean bean = new CallbacksBean();

		bean.setTestString("This is my test string");

		int id = mCallbacksManager.save(bean);
		assertEquals("beforeSave -1;This is my test string\n"+
					 "beforeInsert -1;This is my test string\n"+
					 "afterInsert true "+id+";This is my test string\n"+
					 "afterSave true "+id+";This is my test string", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		CallbacksBean.clearExecuteCallbacks();
		assertEquals("", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		bean.setTestString("This is a new test string");

		id = mCallbacksManager.save(bean);
		assertEquals("beforeSave "+id+";This is a new test string\n"+
					 "beforeUpdate "+id+";This is a new test string\n"+
					 "afterUpdate true "+id+";This is a new test string\n"+
					 "afterSave true "+id+";This is a new test string", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		CallbacksBean.clearExecuteCallbacks();
		assertEquals("", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		bean.setId(999999);
		bean.setTestString("This is another test string");

		id = mCallbacksManager.save(bean);
		assertEquals("beforeSave 999999;This is another test string\n"+
					 "beforeUpdate 999999;This is another test string\n"+
					 "afterUpdate false 999999;This is another test string\n"+
					 "beforeInsert 999999;This is another test string\n"+
					 "afterInsert true "+id+";This is another test string\n"+
					 "afterSave true "+id+";This is another test string", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));
	}

	public void testSaveCallbacksReturns()
	{
		CallbacksBean.clearExecuteCallbacks();

		CallbacksBean bean = new CallbacksBean();

		bean.setTestString("This is my test string");

		// test immediate inserts
		bean.setBeforeSaveReturn(false);
		mCallbacksManager.save(bean);
		assertEquals("beforeSave -1;This is my test string", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));
		assertNull(mCallbacksManager.restore(1));

		CallbacksBean.clearExecuteCallbacks();
		assertEquals("", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		bean.setBeforeSaveReturn(true);
		bean.setBeforeInsertReturn(false);
		mCallbacksManager.save(bean);
		assertEquals("beforeSave -1;This is my test string\n"+
					 "beforeInsert -1;This is my test string", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));
		assertNull(mCallbacksManager.restore(1));

		CallbacksBean.clearExecuteCallbacks();
		assertEquals("", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		bean.setBeforeInsertReturn(true);
		bean.setAfterInsertReturn(false);
		int id = mCallbacksManager.save(bean);
		assertEquals("beforeSave -1;This is my test string\n"+
					 "beforeInsert -1;This is my test string\n"+
					 "afterInsert true "+id+";This is my test string", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));
		assertNotNull(mCallbacksManager.restore(id));

		// test updates
		CallbacksBean.clearExecuteCallbacks();
		assertEquals("", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		bean.setTestString("This is a new test string");

		bean.setBeforeUpdateReturn(false);
		bean.setAfterInsertReturn(true);
		assertEquals(-1, mCallbacksManager.save(bean));
		assertEquals("beforeSave "+id+";This is a new test string\n"+
					 "beforeUpdate "+id+";This is a new test string", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));
		assertEquals("This is my test string", mCallbacksManager.restore(id).getTestString());

		CallbacksBean.clearExecuteCallbacks();
		assertEquals("", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		bean.setBeforeUpdateReturn(true);
		bean.setAfterUpdateReturn(false);
		assertEquals(id, mCallbacksManager.save(bean));
		assertEquals("beforeSave "+id+";This is a new test string\n"+
					 "beforeUpdate "+id+";This is a new test string\n"+
					 "afterUpdate true "+id+";This is a new test string", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));
		assertEquals("This is a new test string", mCallbacksManager.restore(id).getTestString());

		// test insert after failed update
		CallbacksBean.clearExecuteCallbacks();
		assertEquals("", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		bean.setId(999999);
		bean.setTestString("This is another test string");

		bean.setAfterUpdateReturn(true);
		bean.setBeforeInsertReturn(false);

		assertEquals(-1, mCallbacksManager.save(bean));
		assertEquals("beforeSave 999999;This is another test string\n"+
					 "beforeUpdate 999999;This is another test string\n"+
					 "afterUpdate false 999999;This is another test string\n"+
					 "beforeInsert 999999;This is another test string", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));
		assertNull(mCallbacksManager.restore(999999));
		CallbacksBean.clearExecuteCallbacks();
		assertEquals("", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		bean.setBeforeInsertReturn(true);
		bean.setAfterInsertReturn(false);

		assertEquals(id+1, mCallbacksManager.save(bean));
		id = id+1;
		assertEquals("beforeSave 999999;This is another test string\n"+
					 "beforeUpdate 999999;This is another test string\n"+
					 "afterUpdate false 999999;This is another test string\n"+
					 "beforeInsert 999999;This is another test string\n"+
					 "afterInsert true "+id+";This is another test string", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));
		assertNotNull(mCallbacksManager.restore(id));
	}

	public void testSaveCallbacksSparse()
	{
		CallbacksSparseBean bean = new CallbacksSparseBean();

		bean.setId(1000);
		bean.setTestString("Test String");

		int id = mCallbacksSparseManager.save(bean);
		assertEquals("beforeSave "+id+";Test String\n"+
					 "beforeInsert "+id+";Test String\n"+
					 "afterInsert true "+id+";Test String\n"+
					 "afterSave true "+id+";Test String", StringUtils.join(bean.getExecutedCallbacks(), "\n"));

		bean.clearExecuteCallbacks();
		assertEquals("", StringUtils.join(bean.getExecutedCallbacks(), "\n"));

		bean.setTestString("This is a new test string");

		id = mCallbacksSparseManager.save(bean);
		assertEquals("beforeSave "+id+";This is a new test string\n"+
					 "beforeInsert "+id+";This is a new test string\n"+
					 "afterInsert false "+id+";This is a new test string\n"+
					 "beforeUpdate "+id+";This is a new test string\n"+
					 "afterUpdate true "+id+";This is a new test string\n"+
					 "afterSave true "+id+";This is a new test string", StringUtils.join(bean.getExecutedCallbacks(), "\n"));
	}

	public void testSaveCallbacksSparseReturns()
	{
		CallbacksSparseBean bean = new CallbacksSparseBean();

		bean.setId(1000);
		bean.setTestString("Test String");

		int id = 1000;

		// test immediate insert
		bean.setBeforeSaveReturn(false);
		assertEquals(-1, mCallbacksSparseManager.save(bean));
		assertEquals("beforeSave "+id+";Test String", StringUtils.join(bean.getExecutedCallbacks(), "\n"));

		bean.clearExecuteCallbacks();
		assertEquals("", StringUtils.join(bean.getExecutedCallbacks(), "\n"));

		bean.setBeforeSaveReturn(true);
		bean.setBeforeInsertReturn(false);

		assertEquals(-1, mCallbacksSparseManager.save(bean));
		assertEquals("beforeSave "+id+";Test String\n"+
					 "beforeInsert "+id+";Test String", StringUtils.join(bean.getExecutedCallbacks(), "\n"));

		bean.clearExecuteCallbacks();
		assertEquals("", StringUtils.join(bean.getExecutedCallbacks(), "\n"));

		bean.setBeforeInsertReturn(true);
		bean.setAfterInsertReturn(false);

		assertEquals(id, mCallbacksSparseManager.save(bean));
		assertEquals("beforeSave "+id+";Test String\n"+
					 "beforeInsert "+id+";Test String\n"+
					 "afterInsert true "+id+";Test String", StringUtils.join(bean.getExecutedCallbacks(), "\n"));

		bean.clearExecuteCallbacks();
		assertEquals("", StringUtils.join(bean.getExecutedCallbacks(), "\n"));


		// test update after failed insert
		bean.setTestString("This is a new test string");

		bean.clearExecuteCallbacks();
		assertEquals("", StringUtils.join(bean.getExecutedCallbacks(), "\n"));

		bean.setAfterInsertReturn(true);
		bean.setBeforeUpdateReturn(false);

		assertEquals(-1, mCallbacksSparseManager.save(bean));
		assertEquals("beforeSave "+id+";This is a new test string\n"+
					 "beforeInsert "+id+";This is a new test string\n"+
					 "afterInsert false "+id+";This is a new test string\n"+
					 "beforeUpdate "+id+";This is a new test string", StringUtils.join(bean.getExecutedCallbacks(), "\n"));

		bean.clearExecuteCallbacks();
		assertEquals("", StringUtils.join(bean.getExecutedCallbacks(), "\n"));

		bean.setBeforeUpdateReturn(true);
		bean.setAfterUpdateReturn(false);

		assertEquals(id, mCallbacksSparseManager.save(bean));
		assertEquals("beforeSave "+id+";This is a new test string\n"+
					 "beforeInsert "+id+";This is a new test string\n"+
					 "afterInsert false "+id+";This is a new test string\n"+
					 "beforeUpdate "+id+";This is a new test string\n"+
					 "afterUpdate true "+id+";This is a new test string", StringUtils.join(bean.getExecutedCallbacks(), "\n"));
	}

	public void testSaveListeners()
	{
		CallbacksBean.clearExecuteCallbacks();

		AggregatingCallbacksBeanListener listener = new AggregatingCallbacksBeanListener();
		mCallbacksManager.addListener(listener);
		try
		{
			CallbacksBean bean = new CallbacksBean();

			bean.setTestString("This is my test string");

			int id = mCallbacksManager.save(bean);
			Map<String, Object> history = listener.getHistory();
			assertEquals(1, history.size());
			Map.Entry<String, Object> entry = history.entrySet().iterator().next();
			assertEquals("inserted0", entry.getKey());
			assertSame(bean, entry.getValue());
			assertEquals("beforeSave -1;This is my test string\n"+
						 "beforeInsert -1;This is my test string\n"+
						 "afterInsert true "+id+";listener inserted\n"+
						 "afterSave true "+id+";listener inserted", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

			CallbacksBean.clearExecuteCallbacks();
			listener.clearHistory();
			assertEquals("", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

			bean.setTestString("This is a new test string");

			id = mCallbacksManager.save(bean);
			assertEquals(1, history.size());
			entry = history.entrySet().iterator().next();
			assertEquals("updated0", entry.getKey());
			assertSame(bean, entry.getValue());
			assertEquals("beforeSave "+id+";This is a new test string\n"+
						 "beforeUpdate "+id+";This is a new test string\n"+
						 "afterUpdate true "+id+";listener updated\n"+
						 "afterSave true "+id+";listener updated", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

			CallbacksBean.clearExecuteCallbacks();
			listener.clearHistory();
			assertEquals("", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

			bean.setId(999999);
			bean.setTestString("This is another test string");

			id = mCallbacksManager.save(bean);
			assertEquals(1, history.size());
			entry = history.entrySet().iterator().next();
			assertEquals("inserted0", entry.getKey());
			assertSame(bean, entry.getValue());
			assertEquals("beforeSave 999999;This is another test string\n"+
						 "beforeUpdate 999999;This is another test string\n"+
						 "afterUpdate false 999999;This is another test string\n"+
						 "beforeInsert 999999;This is another test string\n"+
						 "afterInsert true "+id+";listener inserted\n"+
						 "afterSave true "+id+";listener inserted", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));
		}
		finally
		{
			mCallbacksManager.removeListeners();
		}
	}

	public void testInsertCallbacks()
	{
		CallbacksBean.clearExecuteCallbacks();

		CallbacksBean bean = new CallbacksBean();

		bean.setTestString("This is my test string");

		int id = mCallbacksManager.insert(bean);
		assertEquals("beforeInsert -1;This is my test string\n"+
					 "afterInsert true "+id+";This is my test string", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));
	}

	public void testInsertCallbacksReturns()
	{
		CallbacksBean.clearExecuteCallbacks();

		CallbacksBean bean = new CallbacksBean();

		bean.setTestString("This is my test string");

		bean.setBeforeInsertReturn(false);
		mCallbacksManager.insert(bean);
		assertEquals("beforeInsert -1;This is my test string", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		assertNull(mCallbacksManager.restore(1));
	}

	public void testUpdateCallbacks()
	{
		CallbacksBean.clearExecuteCallbacks();

		CallbacksBean bean = new CallbacksBean();

		bean.setTestString("This is my test string");

		mCallbacksManager.update(bean);
		assertEquals("beforeUpdate -1;This is my test string\n"+
					 "afterUpdate false -1;This is my test string", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		int id = mCallbacksManager.insert(bean);
		CallbacksBean.clearExecuteCallbacks();
		assertEquals("", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		bean.setTestString("This is a new test string");

		mCallbacksManager.update(bean);
		assertEquals("beforeUpdate "+id+";This is a new test string\n"+
					 "afterUpdate true "+id+";This is a new test string", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));
	}

	public void testUpdateCallbacksReturns()
	{
		CallbacksBean.clearExecuteCallbacks();

		CallbacksBean bean = new CallbacksBean();
		bean.setTestString("This is my test string");

		int id = mCallbacksManager.insert(bean);
		CallbacksBean.clearExecuteCallbacks();
		assertEquals("", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		bean.setTestString("This is a new test string");

		bean.setBeforeUpdateReturn(false);
		mCallbacksManager.update(bean);
		assertEquals("beforeUpdate "+id+";This is a new test string", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		assertEquals("This is my test string", mCallbacksManager.restore(id).getTestString());
	}

	public void testDeleteCallbacks()
	{
		CallbacksBean.clearExecuteCallbacks();

		CallbacksBean bean = new CallbacksBean();

		bean.setTestString("This is my test string");

		int id = mCallbacksManager.save(bean);

		CallbacksBean.clearExecuteCallbacks();
		assertEquals("", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		mCallbacksManager.delete(id);
		assertEquals("beforeDelete "+id+"\n"+
					 "afterDelete true "+id+"", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));
	}

	public void testDeleteCallbacksReturns()
	{
		CallbacksBean.clearExecuteCallbacks();

		CallbacksBean bean = new CallbacksBean();

		bean.setTestString("This is my test string");

		int id = mCallbacksManager.save(bean);

		CallbacksBean.clearExecuteCallbacks();
		assertEquals("", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		CallbacksBean.setBeforeDeleteReturn(false);
		mCallbacksManager.delete(id);
		assertEquals("beforeDelete "+id+"", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		assertNotNull(mCallbacksManager.restore(id));

		CallbacksBean.setBeforeDeleteReturn(true);
	}

	public void testDeleteListeners()
	{
		CallbacksBean.clearExecuteCallbacks();

		CallbacksBean bean = new CallbacksBean();

		bean.setTestString("This is my test string");

		int id = mCallbacksManager.save(bean);

		CallbacksBean.clearExecuteCallbacks();
		assertEquals("", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		AggregatingCallbacksBeanListener listener = new AggregatingCallbacksBeanListener();
		try
		{
			mCallbacksManager.addListener(listener);
			mCallbacksManager.delete(id);
			Map<String, Object> history = listener.getHistory();
			assertEquals(1, history.size());
			Map.Entry<String, Object> entry = history.entrySet().iterator().next();
			assertEquals("deleted0", entry.getKey());
			assertEquals(id, entry.getValue());
			assertEquals("beforeDelete "+id+"\n"+
						 "afterDelete true "+id+"", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));
		}
		finally
		{
			mCallbacksManager.removeListeners();
		}
	}

	public void testRestoreCallbacks()
	{
		CallbacksBean.clearExecuteCallbacks();

		CallbacksBean bean1 = new CallbacksBean();
		CallbacksBean bean2 = new CallbacksBean();
		CallbacksBean bean3 = new CallbacksBean();

		bean1.setTestString("This is bean1");
		bean2.setTestString("This is bean2");
		bean3.setTestString("This is bean3");

		int id1 = mCallbacksManager.save(bean1);
		int id2 = mCallbacksManager.save(bean2);
		int id3 = mCallbacksManager.save(bean3);

		CallbacksBean.clearExecuteCallbacks();

		// restore all beans
		assertEquals(3, mCallbacksManager.restore(mCallbacksManager.getRestoreQuery().orderBy("id")).size());

		assertEquals("afterRestore "+id1+";This is bean1\n"+
					 "afterRestore "+id2+";This is bean2\n"+
					 "afterRestore "+id3+";This is bean3", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		CallbacksBean.clearExecuteCallbacks();
		assertEquals("", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		// restore a specific bean
		mCallbacksManager.restore(bean2.getId());

		assertEquals("afterRestore "+id2+";This is bean2", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		CallbacksBean.clearExecuteCallbacks();
		assertEquals("", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		// restore the first bean
		mCallbacksManager.restoreFirst(mCallbacksManager.getRestoreQuery().orderBy("id"));

		assertEquals("afterRestore "+id1+";This is bean1", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		CallbacksBean.clearExecuteCallbacks();
		CallbacksBean.setAfterRestoreReturn(true);
	}

	public void testRestoreCallbacksReturns()
	{
		CallbacksBean.clearExecuteCallbacks();

		CallbacksBean bean1 = new CallbacksBean();
		CallbacksBean bean2 = new CallbacksBean();
		CallbacksBean bean3 = new CallbacksBean();

		bean1.setTestString("This is bean1");
		bean2.setTestString("This is bean2");
		bean3.setTestString("This is bean3");

		int id = mCallbacksManager.save(bean1);
		mCallbacksManager.save(bean2);
		mCallbacksManager.save(bean3);

		CallbacksBean.clearExecuteCallbacks();
		CallbacksBean.setAfterRestoreReturn(false);

		assertEquals(1, mCallbacksManager.restore(mCallbacksManager.getRestoreQuery().orderBy("id")).size());

		assertEquals("afterRestore "+id+";This is bean1", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));

		CallbacksBean.setAfterRestoreReturn(true);
	}

	public void testRestoreListeners()
	{
		CallbacksBean.clearExecuteCallbacks();
		
		CallbacksBean bean1 = new CallbacksBean();
		CallbacksBean bean2 = new CallbacksBean();
		CallbacksBean bean3 = new CallbacksBean();
		
		bean1.setTestString("This is bean1");
		bean2.setTestString("This is bean2");
		bean3.setTestString("This is bean3");
		
		int id1 = mCallbacksManager.save(bean1);
		int id2 = mCallbacksManager.save(bean2);
		int id3 = mCallbacksManager.save(bean3);
		
		CallbacksBean.clearExecuteCallbacks();
		
		AggregatingCallbacksBeanListener listener = new AggregatingCallbacksBeanListener();
		mCallbacksManager.addListener(listener);
		try
		{
			// restore all beans
			List<CallbacksBean> restored = mCallbacksManager.restore(mCallbacksManager.getRestoreQuery().orderBy("id"));
			assertEquals(3, restored.size());
			
			Map<String, Object> history;
			Iterator<Map.Entry<String, Object>> it;
			Map.Entry<String, Object> entry;

			history = listener.getHistory();
			assertEquals(3, history.size());
			it = history.entrySet().iterator();
			entry = it.next();
			assertEquals("restored0", entry.getKey());
			assertSame(restored.get(0), entry.getValue());
			entry = it.next();
			assertEquals("restored1", entry.getKey());
			assertSame(restored.get(1), entry.getValue());
			entry = it.next();
			assertEquals("restored2", entry.getKey());
			assertSame(restored.get(2), entry.getValue());
			assertEquals("afterRestore "+id1+";listener restored\n"+
						 "afterRestore "+id2+";listener restored\n"+
						 "afterRestore "+id3+";listener restored", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));
			
			CallbacksBean.clearExecuteCallbacks();
			listener.clearHistory();
			assertEquals("", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));
			
			// restore a specific bean
			CallbacksBean restored_specific = mCallbacksManager.restore(bean2.getId());
			
			history = listener.getHistory();
			assertEquals(1, history.size());
			it = history.entrySet().iterator();
			entry = it.next();
			assertEquals("restored0", entry.getKey());
			assertSame(restored_specific, entry.getValue());
			assertEquals("afterRestore "+id2+";listener restored", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));
			
			CallbacksBean.clearExecuteCallbacks();
			listener.clearHistory();
			assertEquals("", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));
			
			// restore the first bean
			CallbacksBean restored_first = mCallbacksManager.restoreFirst(mCallbacksManager.getRestoreQuery().orderBy("id"));
			
			history = listener.getHistory();
			assertEquals(1, history.size());
			it = history.entrySet().iterator();
			entry = it.next();
			assertEquals("restored0", entry.getKey());
			assertSame(restored_first, entry.getValue());
			assertEquals("afterRestore "+id1+";listener restored", StringUtils.join(CallbacksBean.getExecutedCallbacks(), "\n"));
			
			CallbacksBean.clearExecuteCallbacks();
			listener.clearHistory();
			CallbacksBean.setAfterRestoreReturn(true);
		}
		finally
		{
			mCallbacksManager.removeListeners();
		}
	}

	public void testCallbacksProvider()
	{
		CallbacksProviderBean.clearExecuteCallbacks();

		CallbacksProviderBean bean = new CallbacksProviderBean();
		CallbacksProviderBean newbean = null;

		bean.setTestString("This is my test string");

		int id1 = mCallbacksProviderManager.save(bean);

		newbean = mCallbacksProviderManager.restore(id1);

		assertTrue(newbean != null);
		assertTrue(newbean != bean);
		assertEquals(newbean.getTestString(), bean.getTestString());
		assertEquals(newbean.getId(), id1);

		bean.setId(id1);
		bean.setTestString("This is a new test string");

		assertEquals(mCallbacksProviderManager.save(bean), id1);
		assertEquals(bean.getId(), id1);

		newbean = mCallbacksProviderManager.restore(id1);

		assertTrue(newbean != null);
		assertTrue(newbean != bean);

		assertEquals(newbean.getTestString(), "This is a new test string");

		bean.setId(999999);
		bean.setTestString("This is another test string");

		int id2 = id1+1;

		assertEquals(id2, mCallbacksProviderManager.save(bean));
		assertFalse(999999 == id2);
		assertEquals(bean.getId(), id1+1);

		bean.setId(76876);
		bean.setTestString("This is a last test string");

		int id3 = id2+1;
		assertEquals(id3, mCallbacksProviderManager.insert(bean));

		bean.setTestString("This is an updated test string");
		assertEquals(id3, mCallbacksProviderManager.update(bean));

		assertTrue(mCallbacksProviderManager.delete(id2));
		assertEquals(2, mCallbacksProviderManager.restore().size());

		assertEquals("beforeSave -1;This is my test string\n"+
					 "beforeInsert -1;This is my test string\n"+
					 "afterInsert true "+id1+";This is my test string\n"+
					 "afterSave true "+id1+";This is my test string\n"+
					 "afterRestore "+id1+";This is my test string\n"+
					 "beforeSave "+id1+";This is a new test string\n"+
					 "beforeUpdate "+id1+";This is a new test string\n"+
					 "afterUpdate true "+id1+";This is a new test string\n"+
					 "afterSave true "+id1+";This is a new test string\n"+
					 "afterRestore "+id1+";This is a new test string\n"+
					 "beforeSave 999999;This is another test string\n"+
					 "beforeUpdate 999999;This is another test string\n"+
					 "afterUpdate false 999999;This is another test string\n"+
					 "beforeInsert 999999;This is another test string\n"+
					 "afterInsert true "+id2+";This is another test string\n"+
					 "afterSave true "+id2+";This is another test string\n"+
					 "beforeInsert 76876;This is a last test string\n"+
					 "afterInsert true "+id3+";This is a last test string\n"+
					 "beforeUpdate "+id3+";This is an updated test string\n"+
					 "afterUpdate true "+id3+";This is an updated test string\n"+
					 "beforeDelete "+id2+"\n"+
					 "afterDelete true "+id2+"\n"+
					 "afterRestore "+id1+";This is a new test string\n"+
					 "afterRestore "+id3+";This is an updated test string", StringUtils.join(CallbacksProviderBean.getExecutedCallbacks(), "\n"));
	}
}
