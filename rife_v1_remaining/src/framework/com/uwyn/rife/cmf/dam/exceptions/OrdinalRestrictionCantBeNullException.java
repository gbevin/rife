package com.uwyn.rife.cmf.dam.exceptions;

public class OrdinalRestrictionCantBeNullException extends ContentManagerException
{
	private static final long serialVersionUID = -7956803702851520790L;

	private Class	mBeanClass = null;
	private String	mProperty = null;
	private String	mRestriction = null;

	public OrdinalRestrictionCantBeNullException(Class beanClass, String property, String restriction)
	{
		super("The property '"+property+"' of bean '"+beanClass.getName()+"' declares itself as being a restricted ordinal, but the value restriction property '"+restriction+"' is null.");

		mBeanClass = beanClass;
		mProperty = property;
		mRestriction = restriction;
	}
	
	public Class getBeanClass()
	{
		return mBeanClass;
	}

	public String getProperty()
	{
		return mProperty;
	}

	public String getRestriction()
	{
		return mRestriction;
	}
}
