/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestRifeConfig.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.config;

import com.uwyn.rife.config.exceptions.DateFormatInitializationException;
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.rep.Participant;
import com.uwyn.rife.rep.Rep;
import com.uwyn.rife.rep.Repository;
import com.uwyn.rife.rep.SingleObjectParticipant;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;

public class TestRifeConfig extends TestCase
{
	private Repository mDefaultRep = null;

	public TestRifeConfig(String name)
	{
		super(name);
	}

	public void setUp()
	{
		mDefaultRep = Rep.getDefaultRepository();
	}

	public void tearDown()
	{
		Rep.setDefaultRepository(mDefaultRep);
	}

	public void testShortDateFormat()
	throws Exception
	{
		switchLocale(USLocaleConfig.class);

		DateFormat sf = RifeConfig.Tools.getDefaultShortDateFormat();
		String formatted = sf.format(new GregorianCalendar(2004, 7, 31, 15, 53).getTime());

		assertEquals(formatted, "8/31/04");

		switchLocale(BelgiumNLLocaleConfig.class);

		sf = RifeConfig.Tools.getDefaultShortDateFormat();
		formatted = sf.format(new GregorianCalendar(2004, 7, 31, 15, 53).getTime());

		assertEquals(formatted, "31/08/04");

		switchLocale(GeneralESLocaleConfig.class);

		sf = RifeConfig.Tools.getDefaultShortDateFormat();
		formatted = sf.format(new GregorianCalendar(2004, 7, 31, 15, 53).getTime());

		assertEquals(formatted, "31/08/04");

		switchLocale(FormattedDateFormatConfig.class);

		sf = RifeConfig.Tools.getDefaultShortDateFormat();
		formatted = sf.format(new GregorianCalendar(2004, 7, 31, 15, 53).getTime());

		assertEquals(formatted, "Tue, Aug 31, 2004");

		try
		{
			switchLocale(BadlyFormattedDateFormatConfig.class);

			sf = RifeConfig.Tools.getDefaultShortDateFormat();
			formatted = sf.format(new GregorianCalendar(2004, 7, 31, 15, 53).getTime());
			assertFalse(true);
		}
		catch (DateFormatInitializationException e)
		{
			assertTrue(true);
		}
	}

	public void testLongDateFormat()
	throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException
	{
		switchLocale(USLocaleConfig.class);

		DateFormat sf = RifeConfig.Tools.getDefaultLongDateFormat();
		String formatted = sf.format(new GregorianCalendar(2004, 7, 31, 15, 53).getTime());

		assertEquals(formatted, "Aug 31, 2004 3:53 PM");

		switchLocale(BelgiumNLLocaleConfig.class);

		sf = RifeConfig.Tools.getDefaultLongDateFormat();
		formatted = sf.format(new GregorianCalendar(2004, 7, 31, 15, 53).getTime());

		assertEquals(formatted, "31-aug-2004 15:53");

		switchLocale(GeneralESLocaleConfig.class);

		sf = RifeConfig.Tools.getDefaultLongDateFormat();
		formatted = sf.format(new GregorianCalendar(2004, 7, 31, 15, 53).getTime());

		assertEquals(formatted, "31-ago-2004 15:53");

		switchLocale(FormattedDateFormatConfig.class);

		sf = RifeConfig.Tools.getDefaultLongDateFormat();
		formatted = sf.format(new GregorianCalendar(2004, 7, 31, 15, 53).getTime());

		assertEquals(formatted, "Tue, 31 Aug 2004 15:53:00");

		try
		{
			switchLocale(BadlyFormattedDateFormatConfig.class);

			sf = RifeConfig.Tools.getDefaultLongDateFormat();
			formatted = sf.format(new GregorianCalendar(2004, 7, 31, 15, 53).getTime());
			assertFalse(true);
		}
		catch (DateFormatInitializationException e)
		{
			assertTrue(true);
		}
	}

	private void switchLocale(Class klazz)
	throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException
	{
		MockRepository rep = new MockRepository();
		rep.addParticipant("ParticipantConfig", klazz);
		Rep.setDefaultRepository(rep);
	}

	public class USLocaleConfig extends SingleObjectParticipant
	{
		public Object getObject()
		{
			Config config = new Config();
			config.setParameter(RifeConfig.Tools.PARAM_L10N_DEFAULT_COUNTRY, "US");
			config.setParameter(RifeConfig.Tools.PARAM_L10N_DEFAULT_LANGUAGE, "EN");

			return config;
		}
	}

	public class BelgiumNLLocaleConfig extends SingleObjectParticipant
	{
		public Object getObject()
		{
			Config config = new Config();
			config.setParameter(RifeConfig.Tools.PARAM_L10N_DEFAULT_COUNTRY, "BE");
			config.setParameter(RifeConfig.Tools.PARAM_L10N_DEFAULT_LANGUAGE, "NL");

			return config;
		}
	}

	public class GeneralESLocaleConfig extends SingleObjectParticipant
	{
		public Object getObject()
		{
			Config config = new Config();
			config.setParameter(RifeConfig.Tools.PARAM_L10N_DEFAULT_LANGUAGE, "ES");

			return config;
		}
	}

	public class FormattedDateFormatConfig extends SingleObjectParticipant
	{
		public Object getObject()
		{
			Config config = new Config();
			config.setParameter(RifeConfig.Tools.PARAM_L10N_DEFAULT_SHORT_DATEFORMAT, "EEE, MMM d, yyyy");
			config.setParameter(RifeConfig.Tools.PARAM_L10N_DEFAULT_LONG_DATEFORMAT, "EEE, d MMM yyyy HH:mm:ss");

			return config;
		}
	}

	public class BadlyFormattedDateFormatConfig extends SingleObjectParticipant
	{
		public Object getObject()
		{
			Config config = new Config();
			config.setParameter(RifeConfig.Tools.PARAM_L10N_DEFAULT_SHORT_DATEFORMAT, "wwww 999 uuuu");
			config.setParameter(RifeConfig.Tools.PARAM_L10N_DEFAULT_LONG_DATEFORMAT, "vvvv, 82.2 cccc");

			return config;
		}
	}

	public class MockRepository implements Repository
	{
		private Map mParticipants = new HashMap();

		public MockRepository()
		{
		}

		public void addParticipant(String name, Class klazz)
		throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException
		{
			Constructor localeConstructor = klazz.getDeclaredConstructor(new Class[] {TestRifeConfig.class});
			TestRifeConfig t = new TestRifeConfig("Workaround for inner class instanciation");

			mParticipants.put(name, localeConstructor.newInstance(new Object[] { t }));
		}

		public boolean hasParticipant(String name)
		{
			return mParticipants.containsKey(name);
		}

		public Participant getParticipant(String name)
		{
			return (Participant)mParticipants.get(name);
		}

		public Collection<? extends Participant> getParticipants(String name)
		{
			return mParticipants.values();
		}

		public boolean isFinished() { return true; }
		public void cleanup() {}

		public HierarchicalProperties getProperties()
		{
			HierarchicalProperties properties = new HierarchicalProperties();
			for (Map.Entry property : System.getProperties().entrySet())
			{
				properties.put((String)property.getKey(), property.getValue());
			}
			return properties;
		}

		public Object getContext()
		{
			return null;
		}
	}
}
