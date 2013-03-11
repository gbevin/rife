/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Display.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package tutorial.friends;

import com.uwyn.rife.database.DbBeanFetcher;
import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.Template;
import tutorial.friends.backend.Friend;
import tutorial.friends.backend.FriendManager;

/**
 * Display the list of friends with their sites.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 */
public class Display extends Element {
	/**
	 * The element's entry point.
	 */
    public void processElement() {
		final Template template = getHtmlTemplate("display");
		
		FriendManager manager = new FriendManager();
		manager.display(new DbBeanFetcher<Friend>(manager.getDatasource(), Friend.class) {
				public boolean gotBeanInstance(Friend friend) {
					template.setBean(friend);
					template.appendBlock("rows", "row");
					return true;
				}
			});
		
        print(template);
	}
}
