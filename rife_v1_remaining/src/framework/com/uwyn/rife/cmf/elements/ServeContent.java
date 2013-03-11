/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ServeContent.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.elements;

import com.uwyn.rife.cmf.dam.ContentManager;
import com.uwyn.rife.cmf.dam.contentmanagers.DatabaseContentFactory;
import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.config.Config;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.Datasources;
import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.annotations.Elem;
import com.uwyn.rife.tools.ExceptionUtils;
import java.net.URLDecoder;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;

@Elem
public class ServeContent extends Element
{
	private String	mRepositoryName = null;
	
	public void setRepositoryName(String repositoryName)
	{
		mRepositoryName = repositoryName;
	}
	
	public String getRepositoryName()
	{
		return mRepositoryName;
	}
	
	public void processElement()
	{
		Datasource datasource = getPropertyTyped("datasource", Datasource.class);
		if (null == datasource)
		{
			String datasource_name = Config.getRepInstance().getString("DATASOURCE", "datasource");
			datasource = Datasources.getRepInstance().getDatasource(datasource_name);
			if (null == datasource)
			{
				throw new MissingDatasourceException(getElementInfo().getId());
			}
		}

		// obtain the optional repository name
		setRepositoryName(getPropertyString("repository"));

		// retrieve and output the content that corresponds to the path info
		ContentManager	manager = DatabaseContentFactory.getInstance(datasource);

		// get the content path
		String content_path = getEmbedValue();
		try
		{
			// get the content path from the embed value
			if (content_path != null)
			{
				print(manager.getContentForHtml(content_path, this, "serve"));
				return;
			}

			// get the content path from the path info
			content_path = getPathInfo();
			if (content_path != null)
			{
				content_path = URLDecoder.decode(content_path);
			}

			// filter the content path
			content_path = filterPath(content_path);
			
			// prepend the repository name if it was provided
			if (mRepositoryName != null)
			{
				StringBuilder buffer = new StringBuilder(mRepositoryName);
				buffer.append(":");
				buffer.append(content_path);
				content_path = buffer.toString();
			}
			
			// serve the content for the path, if the path is valid
			if (content_path != null &&
				!content_path.equals("/"))
			{
				manager.serveContentData(this, content_path);
				return;
			}
		}
		catch (ContentManagerException e)
		{
			Logger.getLogger("com.uwyn.rife.cmf").severe(ExceptionUtils.getExceptionStackTrace(e));
			setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		defer();
	}

	public String filterPath(String path)
	{
		return path;
	}
}

