/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Counter.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.embedding.embedded;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.annotations.Elem;
import com.uwyn.rife.engine.annotations.InputProperty;
import com.uwyn.rife.engine.annotations.OutputProperty;
import com.uwyn.rife.engine.annotations.SubmissionHandler;
import com.uwyn.rife.template.Template;

@Elem(
	url=""
)
public class Counter extends Element {
	private int counter = 0;
	@InputProperty	public void setCounter(int counter) { this.counter = counter; }
	@OutputProperty	public int getCounter() { return counter; }
	
	@SubmissionHandler
	public void doDecrease() {
		counter--;
		processElement();
	}
	
	@SubmissionHandler
	public void doIncrease() {
		counter++;
		processElement();
	}
	
	public void processElement() {
		Template t = getHtmlTemplate("engine_embedding_counter");
		t.setValue("counternumber", getEmbedDifferentiator());
		t.setValue("counter", counter);
		print(t);
	}
}
