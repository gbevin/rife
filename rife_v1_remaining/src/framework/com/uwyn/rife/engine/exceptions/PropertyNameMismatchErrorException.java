package com.uwyn.rife.engine.exceptions;

import com.uwyn.rife.tools.ClassUtils;

import java.lang.reflect.Method;

public class PropertyNameMismatchErrorException extends ElementAnnotationErrorException
{
	private static final long serialVersionUID = 6625193529545213419L;

	private Class	mAnnotationType = null;
	private Method	mMethod = null;
	private String	mExpectedPropertyName = null;
	private String	mActualPropertyName = null;

	public PropertyNameMismatchErrorException(String implementationName, String siteDeclarationName, Method method, Class annotationType, String expected, String actual)
	{
		super(implementationName, siteDeclarationName, "@"+ ClassUtils.simpleClassName(annotationType)+" on method '"+method.getName()+"' declares the property name to be '"+expected+"', while it is '"+actual+"'.", null);

		mAnnotationType = annotationType;
		mMethod = method;
		mExpectedPropertyName = expected;
		mActualPropertyName = actual;
	}

	public Method getMethod()
	{
		return mMethod;
	}

	public Class getAnnotationType()
	{
		return mAnnotationType;
	}

	public String getExpectedPropertyName()
	{
		return mExpectedPropertyName;
	}

	public String getActualPropertyName()
	{
		return mActualPropertyName;
	}
}
