/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ReflexiveSubmission.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.inputs;

import com.uwyn.rife.engine.Element;

public class ReflexiveSubmission extends Element
{
	public void processElement()
	{
		setOutput("outputreflexive", "outputreflexivevalue");
		
		print("<html><body>\n");
		print("<form action=\""+getSubmissionQueryUrl("submit")+"\" method=\"post\">\n");
		print("<div id=\"inputreflexive\">"+getInput("inputreflexive")+"</div>\n");
		print("<div id=\"inputnormal\">"+getInput("inputnormal")+"</div>\n");
		print("<input name=\"param\" type=\"text\">\n");
		print("<input type=\"submit\">\n");
		print("</form>\n");
		print("</body></html>\n");
	}
	
	public void doSubmit()
	{
		print(getParameter("param")+"\n");
		print(getInput("inputreflexive")+"\n");
		print(getInput("inputnormal"));
	}
}

