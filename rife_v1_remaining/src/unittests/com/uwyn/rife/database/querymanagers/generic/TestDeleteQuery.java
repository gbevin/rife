package com.uwyn.rife.database.querymanagers.generic;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.queries.Delete;
import com.uwyn.rife.database.queries.Select;
import com.uwyn.rife.database.querymanagers.generic.beans.BeanImpl;
import com.uwyn.rife.database.querymanagers.generic.beans.LinkBean;
import com.uwyn.rife.database.querymanagers.generic.beans.SimpleBean;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import junit.framework.TestCase;

public class TestDeleteQuery extends TestCase
{
	private Datasource 						mDatasource = null;
	private GenericQueryManager<BeanImpl> 	mBigBeanManager = null;
	private GenericQueryManager<SimpleBean>	mManager = null;
	private GenericQueryManager<LinkBean>	mLinkManager = null;
	
	public TestDeleteQuery(Datasource datasource, String datasourceName, String name)
	{
		super(name);
		mDatasource = datasource;
	}
    
	protected void setUp()
	{
		mManager = GenericQueryManagerFactory.getInstance(mDatasource, SimpleBean.class);
		mLinkManager = GenericQueryManagerFactory.getInstance(mDatasource, LinkBean.class);
		mBigBeanManager = GenericQueryManagerFactory.getInstance(mDatasource, BeanImpl.class);
		
		int poolsize = mDatasource.getPoolsize();
		// disabling pool for firebird
		if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
		{
			mDatasource.setPoolsize(0);
		}
		try
		{
			mManager.install();
			mLinkManager.install();
			mBigBeanManager.install();
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
	{
		int poolsize = mDatasource.getPoolsize();
		// disabling pool for firebird
		if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
		{
			mDatasource.setPoolsize(0);
		}
		try
		{
			mManager.remove();
			mLinkManager.remove();
			mBigBeanManager.remove();
		}
		finally
		{
			if ("org.firebirdsql.jdbc.FBDriver".equals(mDatasource.getAliasedDriver()))
			{
				mDatasource.setPoolsize(poolsize);
			}
		}
	}
	
	public void testCloneToStringAndClear()
	{
		DeleteQuery query = mBigBeanManager.getDeleteQuery().where("propertyString", "=", "bean set 1");
		
		assertEquals(query.toString(), "DELETE FROM beanimpl WHERE propertyString = 'bean set 1'");
		
		DeleteQuery queryclone = query.clone();
		
		assertEquals(queryclone.toString(), "DELETE FROM beanimpl WHERE propertyString = 'bean set 1'");
		
		queryclone.where("propertyString", "!=", "bean set 2");
		
		assertEquals(queryclone.toString(), "DELETE FROM beanimpl WHERE propertyString = 'bean set 1' AND propertyString != 'bean set 2'");
		
		queryclone.clear();
		
		assertEquals(queryclone.toString(), "DELETE FROM beanimpl WHERE propertyString = 'bean set 1'");
		
		query.clear();
		
		assertEquals(query.toString(), "DELETE FROM beanimpl");
	}
	
	public void testGetParameters()
	{
		Delete delete = new Delete(mDatasource);
		delete
			.from("simplebean")
			.whereParameter("testString", "=");
		
		DeleteQuery query = new DeleteQuery(delete);
		
		assertEquals(query.getParameters().getOrderedNames().size(), 1);
		assertTrue(query.getParameters().getOrderedNames().contains("testString"));
		
		assertEquals(query.getParameters().getOrderedNamesArray().length, 1);
		assertEquals(query.getParameters().getOrderedNamesArray()[0], "testString");
	}
	
	public void testGetDatasource()
	{
		assertTrue(mDatasource.equals(mBigBeanManager.getDeleteQuery().getDatasource()));
	}
	
	public void testGetFrom()
	{
		assertTrue(mBigBeanManager
					   .getDeleteQuery()
					   .getFrom()
					   .equals(BeanImpl.class
								   .getName()
								   .replaceAll(BeanImpl.class
												   .getPackage()
												   .getName()+".", "")
								   .toLowerCase()));
	}
	
	public void testWhere()
	{
		BeanImpl bean1 = new BeanImpl();
		
		Calendar cal = Calendar.getInstance();
		cal.set(2004, 6, 19, 16, 27, 15);
		cal.set(Calendar.MILLISECOND, 765);
		bean1.setPropertyBigDecimal(new BigDecimal("384834838434.38483"));
		bean1.setPropertyBoolean(false);
		bean1.setPropertyBooleanObject(true);
		bean1.setPropertyByte((byte)90);
		bean1.setPropertyByteObject((byte)35);
		bean1.setPropertyCalendar(cal);
		bean1.setPropertyChar('w');
		bean1.setPropertyCharacterObject('s');
		bean1.setPropertyDate(cal.getTime());
		bean1.setPropertyDouble(37478.34d);
		bean1.setPropertyDoubleObject(384724.692d);
		bean1.setPropertyFloat(34241.2f);
		bean1.setPropertyFloatObject(3432.7f);
		bean1.setPropertyLong(23432L);
		bean1.setPropertyLongObject(23423L);
		bean1.setPropertyShort((short)44);
		bean1.setPropertyShortObject((short)69);
		bean1.setPropertyIntegerObject(421);
		bean1.setPropertySqlDate(new java.sql.Date(cal.getTime().getTime()));
		bean1.setPropertyString("nostringhere");
		bean1.setPropertyStringbuffer(new StringBuffer("buffbuffbuff"));
		bean1.setPropertyTime(new Time(cal.getTime().getTime()));
		bean1.setPropertyTimestamp(new Timestamp(cal.getTime().getTime()));
		
		int bean1id = mBigBeanManager.save(bean1);
		mBigBeanManager.save(BeanImpl.getPopulatedBean());
		
		mBigBeanManager.delete(mBigBeanManager.getDeleteQuery().where("propertyString = 'nostringhere'"));
		List<BeanImpl> list = mBigBeanManager.restore();
		assertEquals(list.size(), 1);
		bean1id = mBigBeanManager.save(bean1);
		
		mBigBeanManager.delete(mBigBeanManager.getDeleteQuery().where("propertyBoolean", "=", false));
		list = mBigBeanManager.restore();
		assertEquals(list.size(), 1);
		bean1id = mBigBeanManager.save(bean1);
		
		mBigBeanManager.delete(mBigBeanManager.getDeleteQuery().where("propertyByte", "=", (byte)90));
		list = mBigBeanManager.restore();
		assertEquals(list.size(), 1);
		bean1id = mBigBeanManager.save(bean1);
		
		mBigBeanManager.delete(mBigBeanManager.getDeleteQuery().where("propertyChar", "=", 'w'));
		list = mBigBeanManager.restore();
		assertEquals(list.size(), 1);
		bean1id = mBigBeanManager.save(bean1);
		
		mBigBeanManager.delete(mBigBeanManager.getDeleteQuery().where("propertyDouble", "=", 37478.34d));
		list = mBigBeanManager.restore();
		assertEquals(list.size(), 1);
		bean1id = mBigBeanManager.save(bean1);
		
		if (!("org.postgresql.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("com.mysql.jdbc.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("org.hsqldb.jdbcDriver".equals(mDatasource.getAliasedDriver())) &&
			!("org.h2.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("org.apache.derby.jdbc.EmbeddedDriver".equals(mDatasource.getAliasedDriver()))) // skip this for postgres, mysql, hsqldb, h2 and derby since it doesn't work
		{
			mBigBeanManager.delete(mBigBeanManager.getDeleteQuery().where("propertyFloat", "=", 34241.2f));
			list = mBigBeanManager.restore();
			assertEquals(list.size(), 1);
			bean1id = mBigBeanManager.save(bean1);
		}
				
		mBigBeanManager.delete(mBigBeanManager.getDeleteQuery().where("propertyLong", "=", 23432L));
		list = mBigBeanManager.restore();
		assertEquals(list.size(), 1);
		bean1id = mBigBeanManager.save(bean1);
		
		mBigBeanManager.delete(mBigBeanManager.getDeleteQuery().where("propertyShort", "=", (short)44));
		list = mBigBeanManager.restore();
		assertEquals(list.size(), 1);
		
		DeleteQuery query = new DeleteQuery(mBigBeanManager.getDeleteQuery().where("id", "=", bean1id).getDelegate());
		
		mBigBeanManager.delete(query.where("propertyString = 'nostringhere'"));
		list = mBigBeanManager.restore();
		assertEquals(list.size(), 1);
		bean1id = mBigBeanManager.save(bean1);
		query = new DeleteQuery(mBigBeanManager.getDeleteQuery().where("id", "=", bean1id).getDelegate());
		
		mBigBeanManager.delete(query.where("propertyBoolean", "=", false));
		list = mBigBeanManager.restore();
		assertEquals(list.size(), 1);
		bean1id = mBigBeanManager.save(bean1);
		query = new DeleteQuery(mBigBeanManager.getDeleteQuery().where("id", "=", bean1id).getDelegate());
		
		mBigBeanManager.delete(query.where("propertyByte", "=", (byte)90));
		list = mBigBeanManager.restore();
		assertEquals(list.size(), 1);
		bean1id = mBigBeanManager.save(bean1);
		query = new DeleteQuery(mBigBeanManager.getDeleteQuery().where("id", "=", bean1id).getDelegate());
		
		mBigBeanManager.delete(query.where("propertyChar", "=", 'w'));
		list = mBigBeanManager.restore();
		assertEquals(list.size(), 1);
		bean1id = mBigBeanManager.save(bean1);
		query = new DeleteQuery(mBigBeanManager.getDeleteQuery().where("id", "=", bean1id).getDelegate());
		
		mBigBeanManager.delete(query.where("propertyDouble", "=", 37478.34d));
		list = mBigBeanManager.restore();
		assertEquals(list.size(), 1);
		bean1id = mBigBeanManager.save(bean1);
		query = new DeleteQuery(mBigBeanManager.getDeleteQuery().where("id", "=", bean1id).getDelegate());
		
		if (!("org.postgresql.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("com.mysql.jdbc.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("org.hsqldb.jdbcDriver".equals(mDatasource.getAliasedDriver())) &&
			!("org.h2.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("org.apache.derby.jdbc.EmbeddedDriver".equals(mDatasource.getAliasedDriver()))) // skip this for postgres, mysql, hsqldb, h2 and derby since it doesn't work
		{
			mBigBeanManager.delete(query.where("propertyFloat", "=", 34241.2f));
			list = mBigBeanManager.restore();
			assertEquals(list.size(), 1);
			bean1id = mBigBeanManager.save(bean1);
			query = new DeleteQuery(mBigBeanManager.getDeleteQuery().where("id", "=", bean1id).getDelegate());
		}
		
		mBigBeanManager.delete(query.where("id", "=", bean1id)); // primary key
		list = mBigBeanManager.restore();
		assertEquals(list.size(), 1);
		bean1id = mBigBeanManager.save(bean1);
		query = new DeleteQuery(mBigBeanManager.getDeleteQuery().where("id", "=", bean1id).getDelegate());
		
		mBigBeanManager.delete(query.where("propertyLong", "=", 23432L));
		list = mBigBeanManager.restore();
		assertEquals(list.size(), 1);
		bean1id = mBigBeanManager.save(bean1);
		query = new DeleteQuery(mBigBeanManager.getDeleteQuery().where("id", "=", bean1id).getDelegate());
		
		mBigBeanManager.delete(query.where("propertyShort", "=", (short)44));
		list = mBigBeanManager.restore();
		assertEquals(list.size(), 1);
	}
	
	public void testWhereAnd()
	{
		BeanImpl bean1 = new BeanImpl();
		
		Calendar cal = Calendar.getInstance();
		cal.set(2004, 6, 19, 16, 27, 15);
		cal.set(Calendar.MILLISECOND, 765);
		bean1.setPropertyBigDecimal(new BigDecimal("384834838434.38483"));
		bean1.setPropertyBoolean(false);
		bean1.setPropertyBooleanObject(true);
		bean1.setPropertyByte((byte)90);
		bean1.setPropertyByteObject((byte)35);
		bean1.setPropertyCalendar(cal);
		bean1.setPropertyChar('w');
		bean1.setPropertyCharacterObject('s');
		bean1.setPropertyDate(cal.getTime());
		bean1.setPropertyDouble(37478.34d);
		bean1.setPropertyDoubleObject(384724.692d);
		bean1.setPropertyFloat(34241.2f);
		bean1.setPropertyFloatObject(3432.7f);
		bean1.setPropertyLong(23432L);
		bean1.setPropertyLongObject(23423L);
		bean1.setPropertyShort((short)44);
		bean1.setPropertyShortObject((short)69);
		bean1.setPropertyIntegerObject(421);
		bean1.setPropertySqlDate(new java.sql.Date(cal.getTime().getTime()));
		bean1.setPropertyString("nostringhere");
		bean1.setPropertyStringbuffer(new StringBuffer("buffbuffbuff"));
		bean1.setPropertyTime(new Time(cal.getTime().getTime()));
		bean1.setPropertyTimestamp(new Timestamp(cal.getTime().getTime()));
		
		int bean1id = mBigBeanManager.save(bean1);
		mBigBeanManager.save(BeanImpl.getPopulatedBean());
		
		mBigBeanManager.delete(
			mBigBeanManager.getDeleteQuery()
				.where("id", "=", bean1id)
				.whereAnd("propertyString = 'nostringhere'")
				.whereAnd("propertyBoolean", "=", false)
				.whereAnd("propertyByte", "=", (byte)90)
				.whereAnd("propertyChar", "=", 'w')
				.whereAnd("propertyDouble", "=", 37478.34d)
				.whereAnd("propertyLong", "=", 23432L)
				.whereAnd("propertyString", "=", "nostringhere")
				.whereAnd("propertyIntegerObject", "=", 421)
				.whereAnd("propertyShort", "=", (short)44)
		);
		
		List<BeanImpl> list = mBigBeanManager.restore();
		assertEquals(list.size(), 1);
		
		if (!("org.postgresql.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("com.mysql.jdbc.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("org.hsqldb.jdbcDriver".equals(mDatasource.getAliasedDriver())) &&
			!("org.h2.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("org.apache.derby.jdbc.EmbeddedDriver".equals(mDatasource.getAliasedDriver()))) // skip this for postgres, mysql, hsqldb, h2 and derby since it doesn't work
		{
			bean1id = mBigBeanManager.save(bean1);
			mBigBeanManager.delete(mBigBeanManager.getDeleteQuery().where("id", "=", bean1id).whereAnd("propertyFloat", "=", 34241.2f));
			list = mBigBeanManager.restore();
			assertEquals(list.size(), 1);
		}
	}
	
	public void testWhereOr()
	{
		BeanImpl bean1 = new BeanImpl();
		
		Calendar cal = Calendar.getInstance();
		cal.set(2004, 6, 19, 16, 27, 15);
		cal.set(Calendar.MILLISECOND, 765);
		bean1.setPropertyBigDecimal(new BigDecimal("384834838434.38483"));
		bean1.setPropertyBoolean(false);
		bean1.setPropertyBooleanObject(true);
		bean1.setPropertyByte((byte)90);
		bean1.setPropertyByteObject((byte)35);
		bean1.setPropertyCalendar(cal);
		bean1.setPropertyChar('w');
		bean1.setPropertyCharacterObject('s');
		bean1.setPropertyDate(cal.getTime());
		bean1.setPropertyDouble(37478.34d);
		bean1.setPropertyDoubleObject(384724.692d);
		bean1.setPropertyFloat(34241.2f);
		bean1.setPropertyFloatObject(3432.7f);
		bean1.setPropertyLong(23432L);
		bean1.setPropertyLongObject(23423L);
		bean1.setPropertyShort((short)44);
		bean1.setPropertyShortObject((short)69);
		bean1.setPropertyIntegerObject(421);
		bean1.setPropertySqlDate(new java.sql.Date(cal.getTime().getTime()));
		bean1.setPropertyString("nostringhere");
		bean1.setPropertyStringbuffer(new StringBuffer("buffbuffbuff"));
		bean1.setPropertyTime(new Time(cal.getTime().getTime()));
		bean1.setPropertyTimestamp(new Timestamp(cal.getTime().getTime()));
		
		int bean1id = mBigBeanManager.save(bean1);
		mBigBeanManager.save(BeanImpl.getPopulatedBean());
		
		mBigBeanManager.delete(
			mBigBeanManager.getDeleteQuery()
				.where("id", "=", bean1id)
				.whereOr("propertyString = 'nostringhere'")
				.whereOr("propertyBoolean", "=", false)
				.whereOr("propertyByte", "=", (byte)90)
				.whereOr("propertyChar", "=", 'w')
				.whereOr("propertyDouble", "=", 37478.34d)
				.whereOr("propertyLong", "=", 23432L)
				.whereOr("propertyIntegerObject", "=", 421)
				.whereOr("propertyShort", "=", (short)44)
				.whereOr("propertyString", "=", "nostringhere")
		);
		
		List<BeanImpl> list = mBigBeanManager.restore();
		assertEquals(list.size(), 1);
		
		if (!("com.mysql.jdbc.Driver".equals(mDatasource.getAliasedDriver())) &&
			!("org.hsqldb.jdbcDriver".equals(mDatasource.getAliasedDriver()))) // skip this for mysql and hsqldb since it doesn't work
		{
			bean1id = mBigBeanManager.save(bean1);
			
			mBigBeanManager.delete(mBigBeanManager.getDeleteQuery().where("id", "=", bean1id).whereOr("propertyFloat", "=", 34241.2f));
			list = mBigBeanManager.restore();
			assertEquals(list.size(), 1);
		}
	}
	
	public void testWhereSubselect()
	{
		SimpleBean bean1 = new SimpleBean();
		SimpleBean bean2 = new SimpleBean();
		SimpleBean bean3 = new SimpleBean();
		SimpleBean bean4 = new SimpleBean();
		SimpleBean bean5 = new SimpleBean();
		
		LinkBean linkbean1 = new LinkBean();
		LinkBean linkbean2 = new LinkBean();
		
		linkbean1.setTestString("linkbean 1");
		linkbean2.setTestString("linkbean 2");
		
		mLinkManager.save(linkbean1);
		mLinkManager.save(linkbean2);
		
		bean1.setTestString("bean set 1");
		bean2.setTestString("bean set 1");
		bean3.setTestString("bean set 1");
		bean4.setTestString("bean set 2");
		bean5.setTestString("bean set 2");
		
		bean1.setLinkBean(linkbean1.getId());
		bean2.setLinkBean(linkbean1.getId());
		bean3.setLinkBean(linkbean1.getId());
		bean4.setLinkBean(linkbean2.getId());
		bean5.setLinkBean(linkbean2.getId());
		
		mManager.save(bean1);
		mManager.save(bean2);
		mManager.save(bean3);
		mManager.save(bean4);
		mManager.save(bean5);
		
		Select select = new Select(mDatasource);
		select
			.from(mLinkManager.getTable())
			.field("id")
			.where("id", "=", linkbean1.getId());
		
		DeleteQuery query = mManager.getDeleteQuery();
		query
			.where("linkBean = ("+select.getSql()+")")
			.whereSubselect(select);
		
		if (!("com.mysql.jdbc.Driver".equals(mDatasource.getAliasedDriver()))) // skip this for mysql since it doesn't work
		{
			mManager.delete(query);
			List<SimpleBean> list = mManager.restore();
			assertEquals(list.size(), 2);
		}
	}
}
