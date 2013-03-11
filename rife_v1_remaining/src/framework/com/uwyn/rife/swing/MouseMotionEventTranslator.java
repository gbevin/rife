/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MouseMotionEventTranslator.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.swing;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
	
public class MouseMotionEventTranslator implements MouseMotionListener
{
	private Component	mTargetComponent = null;

	public MouseMotionEventTranslator(Component targetComponent)
	{
		mTargetComponent = targetComponent;
	}

	public void mouseDragged(MouseEvent e)
	{
		if (mTargetComponent instanceof MouseMotionListener)
		{
			MouseMotionListener target_listener = (MouseMotionListener)mTargetComponent;
			target_listener.mouseDragged(translateMouseEvent(e));
		}
	}

	public void mouseMoved(MouseEvent e)
	{
		if (mTargetComponent instanceof MouseMotionListener)
		{
			MouseMotionListener target_listener = (MouseMotionListener)mTargetComponent;
			target_listener.mouseMoved(translateMouseEvent(e));
		}
	}

	private MouseEvent translateMouseEvent(MouseEvent e)
	{
		MouseEvent new_mouse_event = null;
		Point target_location = mTargetComponent.getLocationOnScreen();
		Component source_component = (Component)e.getSource();

		int new_x = (int)(source_component.getX()-target_location.getX()+e.getX());
		int new_y = (int)(source_component.getY()-target_location.getY()+e.getY());

		new_mouse_event = new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), new_x, new_y, e.getClickCount(), e.isPopupTrigger());

		return new_mouse_event;
	}
}
