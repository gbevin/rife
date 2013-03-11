/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TypedInjection.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.submission;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.tools.StringUtils;

public class TypedInjection extends Element
{
	private String		mParamstring1 = "stringdefault";
	private String		mParamstring2;
	private int			mParamint1 = 123;
	private int			mParamint2;
	private long		mParamlong1 = 983749876L;
	private long		mParamlong2;
	private double		mParamdouble1 = 34778.34;
	private double		mParamdouble2;
	private float		mParamfloat1 = 324.34f;
	private float		mParamfloat2;
	private String[]	mParammultiple;
	
	public void setParamstring1(String paramstring1)		{ mParamstring1 = paramstring1; }
	public void setParamstring2(String paramstring2)		{ mParamstring2 = paramstring2; }
	public void setParamint1(int paramint1)					{ mParamint1 = paramint1; }
	public void setParamint2(int paramint2)					{ mParamint2 = paramint2; }
	public void setParamlong1(long paramlong1)				{ mParamlong1 = paramlong1; }
	public void setParamlong2(long paramlong2)				{ mParamlong2 = paramlong2; }
	public void setParamdouble1(double paramdouble1)		{ mParamdouble1 = paramdouble1; }
	public void setParamdouble2(double paramdouble2)		{ mParamdouble2 = paramdouble2; }
	public void setParamfloat1(float paramfloat1)			{ mParamfloat1 = paramfloat1; }
	public void setParamfloat2(float paramfloat2)			{ mParamfloat2 = paramfloat2; }
	public void setParammultiple(String[] parammultiple)	{ mParammultiple = parammultiple; }
	
	public void processElement()
	{
		print("paramstring1:"+mParamstring1);
		print("paramstring2:"+mParamstring2);
		print("paramint1:"+mParamint1);
		print("paramint2:"+mParamint2);
		print("paramlong1:"+mParamlong1);
		print("paramlong2:"+mParamlong2);
		print("paramdouble1:"+mParamdouble1);
		print("paramdouble2:"+mParamdouble2);
		print("paramfloat1:"+mParamfloat1);
		print("paramfloat2:"+mParamfloat2);
		print("parammultiple:"+StringUtils.join(mParammultiple, "-"));
	}
}

