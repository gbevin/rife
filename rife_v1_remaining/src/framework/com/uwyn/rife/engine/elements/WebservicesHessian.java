/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: WebservicesHessian.java 3933 2008-04-25 20:41:45Z gbevin $
 */
package com.uwyn.rife.engine.elements;

import com.caucho.hessian.io.*;
import com.caucho.services.server.ServiceContext;
import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.ElementService;
import com.uwyn.rife.engine.RequestMethod;
import com.uwyn.rife.engine.annotations.Elem;
import com.uwyn.rife.engine.exceptions.EngineException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Elem
public class WebservicesHessian extends Element
{
	public Class getDeploymentClass()
	{
		return WebservicesHessianDeployer.class;
	}

	public void processElement()
	{
		if (!getMethod().equals(RequestMethod.POST))
		{
			setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			setContentType("text/html");
			print("<h1>Hessian Requires POST</h1>");

			return;
		}

		WebservicesHessianDeployer deployer = (WebservicesHessianDeployer) getDeployer();
		String service_id = getHttpServletRequest().getPathInfo();
		String object_id = getHttpServletRequest().getParameter("id");
		if (object_id == null)
		{
			object_id = getHttpServletRequest().getParameter("ejbid");
		}

		try
		{
			ServiceContext.begin(getHttpServletRequest(), service_id, object_id);

			try
			{
				InputStream is = getHttpServletRequest().getInputStream();
				OutputStream os = getOutputStream();

				Hessian2Input in = new Hessian2Input(is);

				try
				{
					SerializerFactory serializer_factory = deployer.getSerializerFactory();

					in.setSerializerFactory(serializer_factory);

					int code = in.read();
					if (code != 'c')
					{
						// XXX: deflate
						throw new IOException("expected 'c' in hessian input at " + code);
					}

					int major = in.read();
					int minor = in.read();

					AbstractHessianOutput out;
					if (major >= 2)
					{
						out = new Hessian2Output(os);
					}
					else
					{
						out = new HessianOutput(os);
					}

					try
					{
						out.setSerializerFactory(serializer_factory);

						if (object_id != null)
						{
							Object service = deployer.getObject();
							if (service instanceof ElementService)
							{
								((ElementService) service).setRequestElement(this);
							}

							deployer.getObjectSkeleton().invoke(in, out);
						}
						else
						{
							Object service = deployer.getHome();
							if (service instanceof ElementService)
							{
								((ElementService) service).setRequestElement(this);
							}

							deployer.getHomeSkeleton().invoke(in, out);
						}
					}
					finally
					{
						out.close();
					}
				}
				finally
				{
					in.close();
					is.close();
				}
			}
			catch (RuntimeException e)
			{
				throw e;
			}
			catch (Throwable e)
			{
				throw new EngineException(e);
			}
			finally
			{
				ServiceContext.end();
			}
		}
		catch (ServletException e)
		{
			throw new EngineException(e);
		}
	}

	public boolean prohibitRawAccess()
	{
		return false;
	}
}

