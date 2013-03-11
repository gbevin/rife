/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Credentials.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package tutorial.authentication;

import com.uwyn.rife.authentication.credentialsmanagers.RoleUserIdentity;
import com.uwyn.rife.authentication.elements.Identified;
import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.Template;

/**
 * Outputs the credentials of an authenticated user.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 */
public class Credentials extends Element {
	/**
	 * The element's entry point.
	 */
	public void processElement() {
		// if the logout submission action was executed, activate the
		// corresponding exit to actually perform the log out
		if (hasSubmission("logout")) {
			exit("logout");
		}
		
		// display the credentials of an authenticated user
		Template template = getHtmlTemplate("credentials");
		
		// output the login
		RoleUserIdentity identity = (RoleUserIdentity)getRequestAttribute(Identified.IDENTITY_ATTRIBUTE_NAME);
		template.setValue("login", encodeHtml(identity.getLogin()));
		
		// print the template
		print(template);
	}
}
