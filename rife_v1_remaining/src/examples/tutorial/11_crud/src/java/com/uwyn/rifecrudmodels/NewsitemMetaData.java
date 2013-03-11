/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: NewsitemMetaData.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rifecrudmodels;

import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.site.ConstrainedBean;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.MetaData;

public class NewsitemMetaData extends MetaData
{
	public void activateMetaData()
	{
		addConstraint(new ConstrainedBean()
					  .defaultOrder("moment", ConstrainedBean.DESC)
					  .defaultOrder("title"));
		
		addConstraint(new ConstrainedProperty("id")
					  .editable(false)
					  .identifier(true));
		
		addConstraint(new ConstrainedProperty("moment")
					  .editable(false)
					  .listed(true)
					  .notNull(true)
					  .format(RifeConfig.Tools.getDefaultLongDateFormat()));
		
		addConstraint(new ConstrainedProperty("image")
					  .notNull(true)
					  .file(true)
					  .listed(true)
					  .mimeType(MimeType.IMAGE_PNG)
					  .contentAttribute("width", 150));
		
		addConstraint(new ConstrainedProperty("title")
					  .notNull(true)
					  .notEmpty(true)
					  .maxLength(100)
					  .listed(true));
		
		addConstraint(new ConstrainedProperty("body")
					  .notNull(true)
					  .notEmpty(true)
					  .mimeType(MimeType.APPLICATION_XHTML)
					  .autoRetrieved(true)
					  .fragment(true));
		
		addConstraint(new ConstrainedProperty("excerpt")
					  .mimeType(MimeType.APPLICATION_XHTML)
					  .autoRetrieved(true)
					  .fragment(true));
		
		addConstraint(new ConstrainedProperty("extended")
					  .mimeType(MimeType.APPLICATION_XHTML)
					  .autoRetrieved(true)
					  .fragment(true));
		
		addConstraint(new ConstrainedProperty("draft")
					  .notNull(true)
					  .defaultValue(false));
		
		addConstraint(new ConstrainedProperty("source")
					  .url(true)
					  .maxLength(150));
	}
}
