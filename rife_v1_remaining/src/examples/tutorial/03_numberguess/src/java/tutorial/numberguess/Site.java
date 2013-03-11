/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Site.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package tutorial.numberguess;

import com.uwyn.rife.engine.SiteBuilder;
import com.uwyn.rife.rep.BlockingParticipant;

/*
 * This is a site definition in Java instead of in XML. It defines exactly the
 * same structure as the one that you can find in the sites/numberguess.xml
 * file.
 * To run the example with the Java site structure, you have to edit the
 * rep/participants.xml file. Instructions can be found there.
 */
public class Site extends BlockingParticipant {
	private Object	site;
	
	protected void initialize() {
		SiteBuilder	builder = new SiteBuilder("numberguess", getResourceFinder());
		builder
			.setArrival("Start")
			
			.enterElement("Start")
				.setImplementation(Start.class)
				.setUrl("start")
				
				.addInput("gameid")
				
				.enterFlowLink("started")
					.destId("Guess")
					.addDataLink("gameid", "gameid")
				.leaveFlowLink()
			.leaveElement()
			
			.enterElement("Guess")
				.setImplementation(Guess.class)
				.setUrl("guess")
				
				.addInput("gameid")
				.enterSubmission("performGuess")
					.addParameter("guess")
				.leaveSubmission()
				
				.enterFlowLink("start")
					.destId("Start")
					.addDataLink("gameid", "gameid")
				.leaveFlowLink()
				
				.enterFlowLink("success")
					.destId("Success")
					.addDataLink("gameid", "gameid")
				.leaveFlowLink()
			.leaveElement()
			
			.enterElement("Success")
				.setImplementation(Success.class)
				
				.addInput("gameid")
				
				.addFlowLink("start", "Guess")
			.leaveElement();
		
		site = builder.getSite();
	}
	
	protected Object _getObject(Object key) {
		return site;
	}
}

