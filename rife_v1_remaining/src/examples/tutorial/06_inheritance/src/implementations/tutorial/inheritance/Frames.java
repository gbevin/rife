/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Frames.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package tutorial.inheritance;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.Template;

/**
 * This element ensures that all elements that inherit it are surrounded with
 * frames and become visible as the content part of the frameset.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 */
public class Frames extends Element {
	/**
	 * The element's entry point.
	 */
	public void processElement() {
		// obtain the template that contains the main frameset
		Template template = getHtmlTemplate("frames.main");
		
		// set the url in the frameset for the content frame
		setExitQuery(template, "content", new String[] {"show_content", "1"});
		
		// obtain the template of the target content element, extract the title
		// from it and set it as the title of the main frameset
		String template_name = getTarget().getPropertyString("name");
		if (template_name != null) {
			Template target_template = getHtmlTemplate(template_name);
			template.setValue("title", target_template.getBlock("title"));
		}
		
		print(template);
	}
	
	/**
	 * This method is called when the <code>show_content</code> child trigger
	 * variable is set. When it's presence is detected, the processing will
	 * simply be forwarded to the child element, otherwise this element's
	 * (this main frameset) content will be generated.
	 */
	public boolean childTriggered(String name, String[] values) {
		if (name.equals("show_content")) {
			return true;
		}
		
		return false;
	}
}


