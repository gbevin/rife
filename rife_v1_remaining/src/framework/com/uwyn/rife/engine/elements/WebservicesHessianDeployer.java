/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: WebservicesHessianDeployer.java 3933 2008-04-25 20:41:45Z gbevin $
 */
package com.uwyn.rife.engine.elements;

import com.caucho.hessian.io.SerializerFactory;
import com.caucho.hessian.server.HessianSkeleton;
import com.caucho.services.server.GenericService;
import com.caucho.services.server.Service;
import com.uwyn.rife.engine.ElementDeployer;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.engine.exceptions.PropertyRequiredException;
import com.uwyn.rife.tools.IteratorEnumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Enumeration;

public class WebservicesHessianDeployer extends ElementDeployer
{
	private Class	mHomeAPI;
	private Object	mHomeImpl;

	private Class	mObjectAPI;
	private Object	mObjectImpl;

	private HessianSkeleton	mHomeSkeleton;
	private HessianSkeleton	mObjectSkeleton;

	private SerializerFactory	mSerializerFactory;

	/**
	 * Initialize the service, including the service object.
	 */
	public void deploy()
	throws EngineException
	{
		try
		{
			if (mHomeImpl != null)
			{
			}
			else
			{
				if (getElementInfo().containsProperty("home-class"))
				{
					String className = getElementInfo().getPropertyString("home-class");

					Class homeClass = loadClass(className);

					mHomeImpl = homeClass.newInstance();

					init(mHomeImpl);
				}
				else
				{
					if (getElementInfo().containsProperty("service-class"))
					{
						String className = getElementInfo().getPropertyString("service-class");

						Class homeClass = loadClass(className);

						mHomeImpl = homeClass.newInstance();

						init(mHomeImpl);
					}
					else
					{
						if (!getElementInfo().containsProperty("service-class"))
						{
							throw new PropertyRequiredException(getElementInfo().getDeclarationName(), "service-class");
						}
					}
				}
			}

			if (mHomeAPI != null)
			{
			}
			else
			{
				if (getElementInfo().containsProperty("home-api"))
				{
					String className = getElementInfo().getPropertyString("home-api");

					mHomeAPI = loadClass(className);
				}
				else
				{
					if (getElementInfo().containsProperty("api-class"))
					{
						String className = getElementInfo().getPropertyString("api-class");

						mHomeAPI = loadClass(className);
					}
					else
					{
						if (mHomeImpl != null)
						{
							mHomeAPI = findRemoteAPI(mHomeImpl.getClass());

							if (mHomeAPI == null)
							{
								mHomeAPI = mHomeImpl.getClass();
							}
						}
					}
				}
			}

			if (mObjectImpl != null)
			{
			}
			else
			{
				if (getElementInfo().containsProperty("object-class"))
				{
					String className = getElementInfo().getPropertyString("object-class");

					Class objectClass = loadClass(className);

					mObjectImpl = objectClass.newInstance();

					init(mObjectImpl);
				}
			}

			if (mObjectAPI != null)
			{
			}
			else
			{
				if (getElementInfo().containsProperty("object-api"))
				{
					String className = getElementInfo().getPropertyString("object-api");

					mObjectAPI = loadClass(className);
				}
				else
				{
					if (mObjectImpl != null)
					{
						mObjectAPI = mObjectImpl.getClass();
					}
				}
			}

			mHomeSkeleton = new HessianSkeleton(mHomeImpl, mHomeAPI);
			if (mObjectAPI != null)
			{
				mHomeSkeleton.setObjectClass(mObjectAPI);
			}

			if (mObjectImpl != null)
			{
				mObjectSkeleton = new HessianSkeleton(mObjectImpl, mObjectAPI);
				mObjectSkeleton.setHomeClass(mHomeAPI);
			}
			else
			{
				mObjectSkeleton = mHomeSkeleton;
			}

			if ("false".equals(getElementInfo().getPropertyString("send-collection-type", "false")))
			{
				setSendCollectionType(false);
			}
		}
		catch (Exception e)
		{
			throw new EngineException(e);
		}
	}

	private Class findRemoteAPI(Class implClass)
	{
		if (implClass == null || implClass.equals(GenericService.class))
		{
			return null;
		}

		Class[] interfaces = implClass.getInterfaces();

		if (interfaces.length == 1)
		{
			return interfaces[0];
		}

		return findRemoteAPI(implClass.getSuperclass());
	}

	private Class loadClass(String className)
	throws ClassNotFoundException
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		if (loader != null)
		{
			return Class.forName(className, false, loader);
		}
		else
		{
			return Class.forName(className);
		}
	}

	private void init(Object service)
	throws ServletException
	{
		if (service instanceof Service)
		{
			((Service)service).init(new ServletConfig() {
						public String getServletName()
						{
							return getElementInfo().getDeclarationName();
						}

						public ServletContext getServletContext()
						{
							return null;
						}

						public String getInitParameter(String key)
						{
							return getElementInfo().getPropertyString(key);
						}

						public Enumeration getInitParameterNames()
						{
							return new IteratorEnumeration(getElementInfo().getPropertyNames().iterator());
						}
					});
		}
	}

	/**
	 * Gets the home skeleton.
	 */
	public HessianSkeleton getHomeSkeleton()
	{
		return mHomeSkeleton;
	}

	/**
	 * Gets the object skeleton.
	 */
	public HessianSkeleton getObjectSkeleton()
	{
		return mObjectSkeleton;
	}

	/**
	 * Sets the home api.
	 */
	public void setHomeAPI(Class api)
	{
		mHomeAPI = api;
	}

	/**
	 * Sets the home implementation
	 */
	public void setHome(Object home)
	{
		mHomeImpl = home;
	}

	/**
	 * Gets the home implementation
	 */
	Object getHome()
	{
		return mHomeImpl;
	}

	/**
	 * Sets the object api.
	 */
	public void setObjectAPI(Class api)
	{
		mObjectAPI = api;
	}

	/**
	 * Sets the object implementation
	 */
	public void setObject(Object object)
	{
		mObjectImpl = object;
	}

	/**
	 * Gets the object implementation
	 */
	Object getObject()
	{
		return mObjectImpl;
	}

	/**
	 * Sets the service class.
	 */
	public void setService(Object service)
	{
		setHome(service);
	}

	/**
	 * Sets the api-class.
	 */
	public void setAPIClass(Class api)
	{
		setHomeAPI(api);
	}

	/**
	 * Gets the api-class.
	 */
	public Class getAPIClass()
	{
		return mHomeAPI;
	}

	/**
	 * Sets the serializer factory.
	 */
	public void setSerializerFactory(SerializerFactory factory)
	{
		mSerializerFactory = factory;
	}

	/**
	 * Gets the serializer factory.
	 */
	public SerializerFactory getSerializerFactory()
	{
		if (mSerializerFactory == null)
		{
			mSerializerFactory = new SerializerFactory();
		}

		return mSerializerFactory;
	}

	/**
	 * Sets the serializer send collection java type.
	 */
	public void setSendCollectionType(boolean sendType)
	{
		getSerializerFactory().setSendCollectionType(sendType);
	}
}
