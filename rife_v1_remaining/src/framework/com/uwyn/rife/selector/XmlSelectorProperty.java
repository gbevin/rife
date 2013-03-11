/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: XmlSelectorProperty.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.selector;

import com.uwyn.rife.ioc.exceptions.PropertyValueException;
import com.uwyn.rife.rep.Rep;
import com.uwyn.rife.xml.exceptions.XmlErrorException;

/**
 * Selects an XML file according to the <code>rife.application</code>
 * application property. The filename will be
 * <code>${prefix}-${rife.application}.xml</code>.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see XmlSelector
 * @since 1.0
 */
public class XmlSelectorProperty implements XmlSelector
{
	public String getXmlPath(String prefix)
	{
		try
		{
			return prefix + Rep.getProperties().get("rife.application").getValueString() + ".xml";
		}
		catch (PropertyValueException e)
		{
			throw new XmlErrorException("Error while obtain the String value of property 'rife.application'.", e);
		}
	}
}
