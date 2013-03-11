/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AutoScrollSelectionRectangle.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.old;

import java.awt.*;
import java.awt.geom.GeneralPath;

class AutoScrollSelectionRectangle extends AutoScroll
{
	private Point				mStructurePanelSelectionStartPoint = null;

	private GeneralPath			mAutoScrollSelectionRectangle = null;
	private Point				mAutoScrollSelectionStartPoint = null;
	private Point				mAutoScrollSelectionEndPoint = null;

	AutoScrollSelectionRectangle(StructurePanel structurePanel, Point firstDestination, Point dragStartPoint)
	{
		super(structurePanel);

		getStructurePanel().eraseSelectionRectangle();
		
		mStructurePanelSelectionStartPoint = dragStartPoint;

		mAutoScrollSelectionRectangle = new GeneralPath();
		mAutoScrollSelectionStartPoint = new Point();
		mAutoScrollSelectionEndPoint = new Point();
		
		initializeAutoScroll(firstDestination, structurePanel);
	}
	
	void prepareVisualAssistanceCustom()
	{
		mAutoScrollSelectionStartPoint.x = getScrollpaneLocationOnGlasspaneX()+mStructurePanelSelectionStartPoint.x-getScrollPane().getViewport().getViewPosition().x;
		mAutoScrollSelectionStartPoint.y = getScrollpaneLocationOnGlasspaneY()+mStructurePanelSelectionStartPoint.y-getScrollPane().getViewport().getViewPosition().y;
	}
	
	void calculateVisualAssistanceCustom(Point destination, int horizontalId, int verticalId)
	{
		switch(horizontalId)
		{
			case VISUAL_ASSISTANCE_HORIZONTAL_LEFT:
				mAutoScrollSelectionEndPoint.x = getScrollpaneLocationOnGlasspaneX();
				break;
			case VISUAL_ASSISTANCE_HORIZONTAL_MIDDLE:
				mAutoScrollSelectionEndPoint.x = getScrollpaneLocationOnGlasspaneX()+destination.x-getScrollPane().getViewport().getViewPosition().x;
				break;
			case VISUAL_ASSISTANCE_HORIZONTAL_RIGHT:
				mAutoScrollSelectionEndPoint.x = getScrollpaneLocationOnGlasspaneX()+getViewRectWidth()-1;
				break;
		}
		switch(verticalId)
		{
			case VISUAL_ASSISTANCE_VERTICAL_TOP:
				mAutoScrollSelectionEndPoint.y = getScrollpaneLocationOnGlasspaneY();
				break;
			case VISUAL_ASSISTANCE_VERTICAL_MIDDLE:
				mAutoScrollSelectionEndPoint.y = getScrollpaneLocationOnGlasspaneY()+destination.y-getScrollPane().getViewport().getViewPosition().y;
				break;
			case VISUAL_ASSISTANCE_VERTICAL_BOTTOM:
				mAutoScrollSelectionEndPoint.y = getScrollpaneLocationOnGlasspaneY()+getViewRectHeight()-1;
				break;
		}
	}
	
	void eraseVisualAssistanceCustom(Graphics2D g2d)
	{
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g2d.setStroke(getStructurePanel().getSelectionRectangleStroke());
		g2d.setXORMode(Color.white);
		g2d.draw(mAutoScrollSelectionRectangle);
	}

	void updateVisualAssistanceAfterScroll(int horizontalOffset, int verticalOffset)
	{
		mAutoScrollSelectionStartPoint.x -= horizontalOffset;
		mAutoScrollSelectionStartPoint.y -= verticalOffset;
	}

	void drawVisualAssistanceCustom(Graphics2D g2d)
	{
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g2d.setStroke(getStructurePanel().getSelectionRectangleStroke());
		g2d.setXORMode(Color.white);
		mAutoScrollSelectionRectangle.reset();
		mAutoScrollSelectionRectangle.moveTo(mAutoScrollSelectionStartPoint.x, mAutoScrollSelectionStartPoint.y);
		mAutoScrollSelectionRectangle.lineTo(mAutoScrollSelectionEndPoint.x, mAutoScrollSelectionStartPoint.y);
		mAutoScrollSelectionRectangle.lineTo(mAutoScrollSelectionEndPoint.x, mAutoScrollSelectionEndPoint.y);
		mAutoScrollSelectionRectangle.lineTo(mAutoScrollSelectionStartPoint.x, mAutoScrollSelectionEndPoint.y);
		mAutoScrollSelectionRectangle.lineTo(mAutoScrollSelectionStartPoint.x, mAutoScrollSelectionStartPoint.y);
		g2d.draw(mAutoScrollSelectionRectangle);
	}
	
	void cleanupVisualAssistanceCustom()
	{
	}
}

