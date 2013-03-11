/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FriendMetaData.java 3943 2008-04-27 09:09:02Z gbevin $
 */
package tutorial.friends.backend;

import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.MetaData;

public class FriendMetaData extends MetaData {
	public void activateMetaData() {
		addConstraint(new ConstrainedProperty("firstname").notNull(true).notEmpty(true).maxLength(50));
		addConstraint(new ConstrainedProperty("lastname").notNull(true).notEmpty(true).maxLength(50));
		addConstraint(new ConstrainedProperty("description").notNull(true));
		addConstraint(new ConstrainedProperty("url").notNull(true).notEmpty(true).maxLength(255).url(true));
	}
}