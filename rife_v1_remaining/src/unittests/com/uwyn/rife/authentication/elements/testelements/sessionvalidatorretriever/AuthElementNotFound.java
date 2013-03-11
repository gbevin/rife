/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AuthElementNotFound.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.elements.testelements.sessionvalidatorretriever;

import com.uwyn.rife.authentication.SessionValidatorRetriever;
import com.uwyn.rife.engine.Element;

public class AuthElementNotFound extends Element
{
	public void processElement()
	{
		SessionValidatorRetriever.getSessionValidator(getSite(), ".UNAVAILABLE_ELEMENT", null);
	}
}

