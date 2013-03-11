/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: HelloWorld.java 3918 2008-04-14 17:35:35Z gbevin $
 */
import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.Template;

/**
 * This element simply outputs the <code>Hello world.</code> message.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 */
public class HelloWorld extends Element {
	/**
	 * The element's entry point.
	 */
    public void processElement() {
		Template template = getHtmlTemplate("helloworld");
		template.setValue("hello", "Hello world.");
        print(template);
    }
}
