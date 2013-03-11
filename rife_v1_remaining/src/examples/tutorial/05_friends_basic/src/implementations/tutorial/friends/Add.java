/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Add.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package tutorial.friends;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.site.Validated;
import com.uwyn.rife.template.Template;
import tutorial.friends.backend.Friend;
import tutorial.friends.backend.FriendManager;

/**
 * Adds a new <code>Friend</code> to the database.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 */
public class Add extends Element {
	/**
	 * The element's entry point.
	 */
    public void processElement() {
        Template template = getHtmlTemplate("add");
		
		// only if friend data has been submitted, add it to the database
		Friend friend = (Friend)getNamedSubmissionBean("friend_data", "friend");
		if (friend != null) {
			// check the validity of the bean
			if (!((Validated)friend).validate()) {
				generateForm(template, friend);
			} else {
				// obtain the manager of the bean
				FriendManager manager = new FriendManager();
				// add the new friend
				manager.add(friend);
				template.setBlock("content", "content_added");
			}
		}
        
        print(template);
    }
}
