/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TemplateTestCase.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.resources.exceptions.ResourceFinderErrorException;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.FileUtils;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import com.uwyn.rife.tools.InputStreamUser;
import com.uwyn.rife.tools.exceptions.FileUtilsErrorException;
import java.io.InputStream;
import junit.framework.TestCase;

public abstract class TemplateTestCase extends TestCase
{
	public TemplateTestCase(String name)
	{
		super(name);
	}

	String getTemplateContent(final String filename, Parser parser)
	{
		ResourceFinderClasspath resource_finder = ResourceFinderClasspath.getInstance();
		String template_path = filename + parser.getExtension();
		try
		{
			return (String)resource_finder.useStream(template_path, new InputStreamUser() {
					public String useInputStream(InputStream stream)
					throws InnerClassException
					{
						if (null != stream)
						{
							String template_content = null;
							try
							{
								template_content = FileUtils.readString(stream);
							}
							catch (FileUtilsErrorException e)
							{
								assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
							}
							
							return template_content;
						}
						assertTrue("Could not find file '" + filename + "'.", false);
						
						return null;
					}
				});
		}
		catch (ResourceFinderErrorException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		return null;
	}
}

