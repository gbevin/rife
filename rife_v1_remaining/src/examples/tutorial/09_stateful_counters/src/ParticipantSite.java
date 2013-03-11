/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParticipantSite.java 3918 2008-04-14 17:35:35Z gbevin $
 */
import com.uwyn.rife.engine.Site;
import com.uwyn.rife.engine.SiteBuilder;
import com.uwyn.rife.engine.elements.PrintTemplate;
import com.uwyn.rife.rep.BlockingParticipant;

public class ParticipantSite extends BlockingParticipant {
	private Site site;
	
	protected void initialize() {
		SiteBuilder builder = new SiteBuilder("main");
		builder
			.setArrival("PrintTemplate")
			
			.enterElement()
				.setImplementation(PrintTemplate.class)
				.setUrl("home")
				.addProperty("name", "home")
			.leaveElement()
			
			.enterElement()
				.setImplementation(Counter.class)
			.leaveElement();
		
		site = builder.getSite();
	}
	
	protected Object _getObject() {
		return site;
	}
}

