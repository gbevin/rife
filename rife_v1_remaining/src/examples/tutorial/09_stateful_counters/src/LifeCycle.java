/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: LifeCycle.java 3918 2008-04-14 17:35:35Z gbevin $
 */
import com.uwyn.rife.rep.BlockingRepository;
import com.uwyn.rife.rep.Rep;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.servlet.RifeLifecycle;

/**
 * Instead of providing a repository participant.xml file, you can create a
 * custom implementation of the entire lifecycle of your application.
 * RIFE's standard web.xml file will have to be modified and instead of having
 * a rep.path init-param, you use a lifecycle.classname init-param and provide
 * the classname of your custom lifecycle implementation.
 *
 * Don't forget to set the default repository in your custom lifecycle, since
 * much of RIFE relies on the presence of that.
 */
public class LifeCycle extends RifeLifecycle {
	public LifeCycle() {
		BlockingRepository rep = new BlockingRepository();
		rep.addParticipant(ParticipantSite.class);
		rep.runParticipants();
		Rep.setDefaultRepository(rep);
	}
}
