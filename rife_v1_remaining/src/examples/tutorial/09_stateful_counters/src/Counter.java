/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Counter.java 3918 2008-04-14 17:35:35Z gbevin $
 */
import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.annotations.Datalink;
import com.uwyn.rife.engine.annotations.Elem;
import com.uwyn.rife.engine.annotations.InputProperty;
import com.uwyn.rife.engine.annotations.OutputProperty;
import com.uwyn.rife.engine.annotations.SubmissionHandler;
import com.uwyn.rife.template.Template;

@Elem(
	// Setting an empty URL, makes the submissions target the element that embeds
	// the embedded Counter elements. Without this declaration, each embedded
	// element would become the main element after the submission (since it has
	// its own URL).
	url="",
	// This data link connects the 'counter' output to the 'counter' value. The
	// element might have changed the counter property value in the meantime
	// (after an 'increase' or 'decrease' submission). The reflective datalink
	// will pass the output value to the input at the next submission.
	datalinks = {
		@Datalink(srcOutput = "counter", destInput = "counter", destClass = Counter.class)
	}
)
public class Counter extends Element {
	private int counter;
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
		Template t = getHtmlTemplate("counter");
		t.setValue("counter", counter);
		print(t);
	}
}
