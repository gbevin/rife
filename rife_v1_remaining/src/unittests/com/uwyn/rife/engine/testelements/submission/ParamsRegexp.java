/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParamsRegexp.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.submission;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.tools.SortListComparables;
import com.uwyn.rife.tools.StringUtils;
import java.util.ArrayList;

public class ParamsRegexp extends Element
{
	public void processElement()
	{
		SortListComparables	sort = new SortListComparables();
		ArrayList<String>	parameters = new ArrayList<String>(getParameterNames());
		sort.sort(parameters);
		for (String parameter : parameters)
		{
			print(StringUtils.join(getParameterValues(parameter),"|"));
			print(",");
		}

		parameters = new ArrayList<String>(getParameterNames(".*wo.*"));
		sort.sort(parameters);
		for (String parameter : parameters)
		{
			print(StringUtils.join(getParameterValues(parameter),"|"));
			print(",");
		}
	}
}

