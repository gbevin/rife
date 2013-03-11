/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Typed.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.submission;

import com.uwyn.rife.engine.Element;

public class Typed extends Element
{
	public void processElement()
	{
		print("paramstring1:"+getParameter("paramstring1", "stringdefault"));
		print("paramstring2:"+getParameter("paramstring2"));
		print("paramstring2default:"+getParameter("paramstring2", "stringdefault"));
		print("paramint1:"+getParameterInt("paramint1", 123));
		print("paramint2:"+getParameterInt("paramint2"));
		print("paramint2default:"+getParameterInt("paramint2", 123));
		print("paramlong1:"+getParameterLong("paramlong1", 983749876L));
		print("paramlong2:"+getParameterLong("paramlong2"));
		print("paramlong2default:"+getParameterLong("paramlong2", 983749876L));
		print("paramdouble1:"+getParameterDouble("paramdouble1", 34778.34));
		print("paramdouble2:"+getParameterDouble("paramdouble2"));
		print("paramdouble2default:"+getParameterDouble("paramdouble2", 34778.34));
		print("paramfloat1:"+getParameterFloat("paramfloat1", 324.34f));
		print("paramfloat2:"+getParameterFloat("paramfloat2"));
		print("paramfloat2default:"+getParameterFloat("paramfloat2", 324.34f));
	}
}

