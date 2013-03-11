/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AutoScrollElementDrag.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.old;

import java.awt.*;
import java.awt.event.MouseEvent;
import javax.swing.*;

class AutoScrollElementDrag extends AutoScroll
{
	private Point	mElementDragReference = null;
	private Point	mElementDragOffset = null;
	private Point	mElementDragDestination = null;
	
	AutoScrollElementDrag(StructurePanel structurePanel, Point dragElementStartPoint, Point firstDestination, JComponent element)
	{
		super(structurePanel);
		mElementDragReference = new Point(dragElementStartPoint);
		mElementDragOffset = new Point(0, 0);
		initializeAutoScroll(firstDestination, element);
	}
	
	Point convertDestinationPoint(Point destination)
	{
		destination.x += getMouseEventGeneratingComponent().getX();
		destination.y += getMouseEventGeneratingComponent().getY();
		
		return destination;
	}

	void prepareVisualAssistanceCustom()
	{
	}
	
	void calculateVisualAssistanceCustom(Point destination, int horizontalId, int verticalId)
	{
		mElementDragDestination = new Point(destination);
	}
	
	void eraseVisualAssistanceCustom(Graphics2D g2d)
	{
	}

	void updateVisualAssistanceAfterScroll(int horizontalOffset, int verticalOffset)
	{
		mElementDragOffset.x = horizontalOffset;
		mElementDragOffset.y = verticalOffset;
	}

	void drawVisualAssistanceCustom(Graphics2D g2d)
	{
		if(mElementDragDestination != null)
		{
			int drag_x = (mElementDragDestination.x-getMouseEventGeneratingComponent().getX())-mElementDragReference.x;
			int drag_y = (mElementDragDestination.y-getMouseEventGeneratingComponent().getY())-mElementDragReference.y;
			mElementDragOffset.x += drag_x;
			mElementDragOffset.y += drag_y;
		}
		getStructurePanel().repositionElementsDuringDrag((Element)getMouseEventGeneratingComponent(), mElementDragOffset.x, mElementDragOffset.y);
		mElementDragOffset.x = 0;
		mElementDragOffset.y = 0;
		mElementDragDestination = null;
	}
	
	void cleanupVisualAssistanceCustom()
	{
	}
	
	public void mouseEntered(MouseEvent e)
	{
		Point location = convertDestinationPoint(new Point(e.getPoint()));
		if(getStructurePanel().isLocationOutsideStructurepanelView(location.x, location.y))
		{
			calculateDifferences(e.getPoint());
		}
		else
		{
			finishAutoScroll();
		}
	}
	
	public void mouseDragged(MouseEvent e)
	{
		Point location = convertDestinationPoint(new Point(e.getPoint()));
		if(getStructurePanel().isLocationOutsideStructurepanelView(location.x, location.y))
		{
			calculateDifferences(e.getPoint());
		}
		else
		{
			finishAutoScroll();
		}
	}
}

