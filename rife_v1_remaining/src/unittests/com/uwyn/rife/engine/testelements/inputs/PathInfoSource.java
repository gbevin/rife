/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PathInfoSource.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.inputs;

import com.uwyn.rife.engine.Element;

public class PathInfoSource extends Element
{
	public void processElement()
	{
		setOutput("exitoutput5", "exitsourcevalue5");
		switch (getInputInt("switch"))
		{
			case 1:
				setOutput("exitoutput1", "exitsourcevalue1");
				setOutput("exitoutput2", "523");
				print("<a href=\""+getExitQueryUrl("exit")+"\">go</a>");
				break;
			case 2:
				setOutput("exitoutput1", "exitsourcevalue1");
				setOutput("exitoutput2", "exitsourcevalue2");
				print("<a href=\""+getExitQueryUrl("exit")+"\">go</a>");
				break;
			case 3:
				setOutput("exitoutput1", "value1");
				setOutput("exitoutput2", "222122");
				setOutput("exitoutput3", "baca");
				setOutput("exitoutput4", "cdd");
				print("<a href=\""+getExitQueryUrl("exit")+"\">go</a>");
				break;
			case 4:
				setOutput("exitoutput1", "exitsourcevalue1");
				setOutput("exitoutput2", "523");
				print("<form action=\""+getExitFormUrl("exit")+"\" method=\"post\">"+getExitFormParameters("exit")+"</form>");
				break;
			case 5:
				setOutput("exitoutput1", "exitsourcevalue1");
				setOutput("exitoutput2", "exitsourcevalue2");
				print("<form action=\""+getExitFormUrl("exit")+"\" method=\"post\">"+getExitFormParameters("exit")+"</form>");
				break;
			case 6:
				setOutput("exitoutput1", "value1");
				setOutput("exitoutput2", "222122");
				setOutput("exitoutput3", "baca");
				setOutput("exitoutput4", "cdd");
				print("<form action=\""+getExitFormUrl("exit")+"\" method=\"post\">"+getExitFormParameters("exit")+"</form>");
				break;
		}
	}
}

