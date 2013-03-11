/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PrintCrudTemplate.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud.elements.admin;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.engine.elements.PrintTemplate;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.tools.Localization;

public class PrintCrudTemplate extends PrintTemplate
{
	public Template getTemplate()
	{
		Template template = super.getTemplate();
		if (template != null)
		{
			String language = RifeConfig.Tools.getDefaultLanguage();
			template.addResourceBundle(Localization.getResourceBundle("l10n/crud/admin", language));
		}
		
		return template;
	}
}

