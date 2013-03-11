/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Install.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package tutorial.friends;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.Template;
import tutorial.friends.backend.FriendManager;

/**
 * Installs the database structure and populates it with example data.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 */
public class Install extends Element {
	private Template template;
	
	/**
	 * The element's initialization method.
	 */
    public void initialize() {
        template = getHtmlTemplate("install");
    }
	
	/**
	 * The element's entry point.
	 */
    public void processElement() {
        print(template);
    }
	
	/**
	 * The element's confirmation submission.
	 */
    public void doConfirmation() {
		FriendManager manager = new FriendManager();
		manager.install();
		template.setBlock("content", "content_installed");
		
		print(template);
    }
}
