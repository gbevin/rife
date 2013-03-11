/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CounterContinuations.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.embedding.embedded;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.annotations.Elem;
import com.uwyn.rife.engine.annotations.Submission;
import com.uwyn.rife.template.Template;

@Elem(
	url="",
	submissions = {@Submission(name="increase"), @Submission(name="decrease")}
)
public class CounterContinuations extends Element {
	public void processElement() {
		int counter = 0;
		while (true)
		{
			Template t = getHtmlTemplate("engine_embedding_counter");
			t.setValue("counternumber", getEmbedDifferentiator());
			t.setValue("counter", counter);
			print(t);
			pause();
			
			if (hasSubmission("increase"))	counter++;
			if (hasSubmission("decrease"))	counter--;
		}
	}
}
