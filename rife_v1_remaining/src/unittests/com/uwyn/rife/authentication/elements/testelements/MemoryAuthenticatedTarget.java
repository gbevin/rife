/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MemoryAuthenticatedTarget.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.elements.testelements;

import com.uwyn.rife.authentication.credentialsmanagers.RoleUserIdentity;
import com.uwyn.rife.authentication.elements.Identified;
import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.Template;

public class MemoryAuthenticatedTarget extends Element
{
	public void processElement()
	{
		if (hasSubmission("logout_passthrough"))
		{
			exit("logout_passthrough");
		}
		
		Template template = getHtmlTemplate("authentication_target");
		RoleUserIdentity identity = (RoleUserIdentity) getRequestAttribute(Identified.IDENTITY_ATTRIBUTE_NAME);
		if (identity != null)
		{
			template.setValue("userLogin", identity.getLogin());
		}
		else
		{
			template.setValue("userLogin", "(none)");
		}
		print(template);
	}
}

