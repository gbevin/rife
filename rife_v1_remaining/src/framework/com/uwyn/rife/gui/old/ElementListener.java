/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementListener.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.old;

import java.awt.*;

public interface ElementListener
{
	public void elementRepositioned(Element element);
	public void elementRaised(Element element);
	public void elementSelected(Element selectedElement, int modifiers);
	public void elementDeselected(Element deselectedElement, int modifiers);
	public void elementDragged(Element initiatingElement, int x, int y);
	public void elementDragStart(Element initiatingElement, Point dragStartPoint);
	public void elementDragEnd();
	public void elementPropertyHighlighted(ElementProperty property);
}
