/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: StaticProperties.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.engine;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.tools.Convert;

public class StaticProperties extends Element
{
	public void processElement()
	{
		if (hasProperty("property1"))
		{
			print("property1:"+getPropertyString("property1"));
		}
		if (hasProperty("property2"))
		{
			print("property2:"+getPropertyString("property2"));
		}
		if (!isPropertyEmpty("property3"))
		{
			print("property3:"+getPropertyString("property3"));
		}
		if (isPropertyEmpty("property4"))
		{
			print("property4:emptyproperty4");
		}
		print("propertystring1:"+getPropertyString("propertystring1", "stringdefault"));
		print("propertystring2:"+getPropertyString("propertystring2"));
		print("propertystring2default:"+getPropertyString("propertystring2", "stringdefault"));
		print("propertyboolean1:"+Convert.toBoolean(getElementInfo().getProperty("propertyboolean1"), false));
		print("propertyboolean2:"+Convert.toBoolean(getElementInfo().getProperty("propertyboolean2"), false));
		print("propertyboolean2default:"+Convert.toBoolean(getElementInfo().getProperty("propertyboolean2"), false));
		print("propertyint1:"+Convert.toInt(getElementInfo().getProperty("propertyint1"), 123));
		print("propertyint2:"+Convert.toInt(getElementInfo().getProperty("propertyint2"), 0));
		print("propertyint2default:"+Convert.toInt(getElementInfo().getProperty("propertyint2"), 123));
		print("propertylong1:"+Convert.toLong(getElementInfo().getProperty("propertylong1"), 983749876L));
		print("propertylong2:"+Convert.toLong(getElementInfo().getProperty("propertylong2"), 0L));
		print("propertylong2default:"+Convert.toLong(getElementInfo().getProperty("propertylong2"), 983749876L));
		print("propertydouble1:"+Convert.toDouble(getElementInfo().getProperty("propertydouble1"), 34778.34));
		print("propertydouble2:"+Convert.toDouble(getElementInfo().getProperty("propertydouble2"), 0));
		print("propertydouble2default:"+Convert.toDouble(getElementInfo().getProperty("propertydouble2"), 34778.34));
		print("propertyfloat1:"+Convert.toFloat(getElementInfo().getProperty("propertyfloat1"), 324.34f));
		print("propertyfloat2:"+Convert.toFloat(getElementInfo().getProperty("propertyfloat2"), 0));
		print("propertyfloat2default:"+Convert.toFloat(getElementInfo().getProperty("propertyfloat2"), 324.34f));
		print("propertyconfig:"+getPropertyString("propertyconfig"));
	}
}

