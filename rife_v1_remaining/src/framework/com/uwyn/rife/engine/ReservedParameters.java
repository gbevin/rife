/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ReservedParameters.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ReservedParameters
{
	public final static String	SUBMISSION = "submission";
	public final static String	SUBMISSIONCONTEXT = "submissioncontext";
	public final static String	INPUTS = "inputs";
	public final static String	CTXT = "ctxt";
	public final static String	CHILDREQUEST = "childrequest";
	public final static String	TRIGGERLIST = "triggerlist";
	public final static String	CONTID = "contid";
	public final static String	STATEID = "stateid";
	public final static String	JSESSIONID = "jsessionid";
	
	static final String[]		RESERVED_NAMES = new String[]
													{
														ReservedParameters.SUBMISSION,
														ReservedParameters.SUBMISSIONCONTEXT,
														ReservedParameters.INPUTS,
														ReservedParameters.CTXT,
														ReservedParameters.CHILDREQUEST,
														ReservedParameters.TRIGGERLIST,
														ReservedParameters.CONTID,
														ReservedParameters.STATEID,
														ReservedParameters.JSESSIONID
													};
	static final List<String>	RESERVED_NAMES_LIST = new ArrayList<String>(Arrays.asList(RESERVED_NAMES));
	
}
