/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContentQueryManagerContentForHtml.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.elements;

import com.uwyn.rife.cmf.dam.ContentImage;
import com.uwyn.rife.cmf.dam.ContentQueryManager;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.Datasources;
import com.uwyn.rife.engine.Element;

public class ContentQueryManagerContentForHtml extends Element
{
	public void processElement()
	{
		String datasource_name = getPropertyString("datasource");
		Datasource datasource = Datasources.getRepInstance().getDatasource(datasource_name);
		int id = getInputInt("id");

		ContentQueryManager<ContentImage> manager = new ContentQueryManager<ContentImage>(datasource, ContentImage.class);
		ContentImage content = manager.restore(id);
		print(manager.getContentForHtml(id, "image", this, "serve"));
		print(manager.getContentForHtml(content, "image", this, "serve"));
	}
}
