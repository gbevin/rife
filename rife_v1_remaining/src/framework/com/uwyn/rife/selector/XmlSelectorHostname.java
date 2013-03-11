/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: XmlSelectorHostname.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.selector;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class XmlSelectorHostname implements XmlSelector
{
	public String getXmlPath(String prefix)
	{
		StringBuilder xml_path = new StringBuilder(prefix);
		try
		{
			InetAddress address = InetAddress.getLocalHost();
			xml_path.append(address.getHostName().toLowerCase().replace('.', '_').replace(' ', '_'));
		}
		catch (UnknownHostException e)
		{
			// do nothing
		}

		xml_path.append(".xml");

		return xml_path.toString();
	}
}
