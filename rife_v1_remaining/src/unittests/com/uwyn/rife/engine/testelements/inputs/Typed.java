/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Typed.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.inputs;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.engine.Element;
import com.uwyn.rife.tools.BeanUtils;
import java.util.Calendar;

public class Typed extends Element
{
	public void processElement()
	{
		print("inputstring1:"+getInput("inputstring1", "stringdefault"));
		print("inputstring2:"+getInput("inputstring2"));
		print("inputstring2default:"+getInput("inputstring2", "stringdefault"));
		print("inputint1:"+getInputInt("inputint1", 123));
		print("inputint2:"+getInputInt("inputint2"));
		print("inputint2default:"+getInputInt("inputint2", 123));
		print("inputlong1:"+getInputLong("inputlong1", 983749876L));
		print("inputlong2:"+getInputLong("inputlong2"));
		print("inputlong2default:"+getInputLong("inputlong2", 983749876L));
		print("inputdouble1:"+getInputDouble("inputdouble1", 34778.34));
		print("inputdouble2:"+getInputDouble("inputdouble2"));
		print("inputdouble2default:"+getInputDouble("inputdouble2", 34778.34));
		print("inputfloat1:"+getInputFloat("inputfloat1", 324.34f));
		print("inputfloat2:"+getInputFloat("inputfloat2"));
		print("inputfloat2default:"+getInputFloat("inputfloat2", 324.34f));
		Calendar cal = Calendar.getInstance();
		cal.set(2006, 5, 10, 11, 57, 11);
		cal.set(Calendar.MILLISECOND, 555);
		cal.setTimeZone(RifeConfig.Tools.getDefaultTimeZone());
		print("inputdate1:"+BeanUtils.getConcisePreciseDateFormat().format(getInputDate("inputdate1", cal.getTime())));
		print("inputdate2:"+getInputDate("inputdate2"));
		print("inputdate2default:"+BeanUtils.getConcisePreciseDateFormat().format(getInputDate("inputdate2", cal.getTime())));
	}
}
