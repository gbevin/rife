/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Remove.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package tutorial.friends;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.Template;
import tutorial.friends.backend.FriendManager;

/**
 * Removes the database structure.
 *
 * We deliberately didn't split up the submission in a separate 'do' method
 * (as we did in the Install element) to show both approaches
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 */
public class Remove extends Element {
	/**
	 * The element's entry point.
	 */
    public void processElement() {
        Template template = getHtmlTemplate("remove");
		
		// only if the user confirmed, execute the removal
		if (hasSubmission("confirmation")) {
			FriendManager manager = new FriendManager();
			manager.remove();
			template.setBlock("content", "content_removed");
		}
        
        print(template);
    }
}
