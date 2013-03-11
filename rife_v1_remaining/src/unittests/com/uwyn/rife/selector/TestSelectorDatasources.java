/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSelectorDatasources.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.selector;

import com.uwyn.rife.selector.XmlSelector;

public class TestSelectorDatasources implements XmlSelector
{
	public String getXmlPath(String prefix)
	{
		return "xml/test_xml2datasources.xml";
	}
}
