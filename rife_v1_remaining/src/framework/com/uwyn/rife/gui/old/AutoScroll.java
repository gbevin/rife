/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AutoScroll.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.old;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.*;

abstract class AutoScroll extends AbstractAction implements MouseListener, MouseMotionListener
{
	static final int	AUTO_SCROLL_DELAY = 40;
	static final int	VISUAL_ASSISTANCE_HORIZONTAL_LEFT = 0;
	static final int	VISUAL_ASSISTANCE_HORIZONTAL_MIDDLE = 1;
	static final int	VISUAL_ASSISTANCE_HORIZONTAL_RIGHT = 2;
	static final int	VISUAL_ASSISTANCE_VERTICAL_TOP = 3;
	static final int	VISUAL_ASSISTANCE_VERTICAL_MIDDLE = 4;
	static final int	VISUAL_ASSISTANCE_VERTICAL_BOTTOM = 5;

	private StructurePanel	mStructurePanel = null;
	private JScrollPane		mScrollPane = null;
	
	private Point			mFirstDragDestination = null;
	private JComponent		mMouseEventGeneratingComponent = null;
	
	private Timer	  		mTimer = null;
	
	private Rectangle 		mViewRect = null;
	private int 	  		mViewTopLeftX = 0;
	private int 	  		mViewTopLeftY = 0;
	private int 	  		mViewBottomRightX = 0;
	private int 	  		mViewBottomRightY = 0;
	
	private int		  		mScrollOffsetX = 0;
	private int		  		mScrollOffsetY = 0;
	
	private boolean	  		mContinueToScroll = true;
	
	private int		  		mScrollpaneLocationOnGlasspaneX = 0;
	private int		  		mScrollpaneLocationOnGlasspaneY = 0;
	private Rectangle 		mGlasspaneClip = null;

	AutoScroll(StructurePanel structurePanel)
	{
		mStructurePanel = structurePanel;
		mScrollPane = structurePanel.getScrollPane();
	}

	AutoScroll(StructurePanel structurePanel, Point firstDestination, JComponent mouseEventGeneratingComponent)
	{
		mStructurePanel = structurePanel;
		mScrollPane = structurePanel.getScrollPane();
		AutoScroll.this.initializeAutoScroll(firstDestination, mouseEventGeneratingComponent);
	}

	final void initializeAutoScroll(Point firstDestination, JComponent mouseEventGeneratingComponent)
	{
		mStructurePanel.setScrollActive(true);
		mStructurePanel.repaint();

		mMouseEventGeneratingComponent = mouseEventGeneratingComponent;
		mMouseEventGeneratingComponent.removeMouseListener((MouseListener)mMouseEventGeneratingComponent);
		mMouseEventGeneratingComponent.removeMouseMotionListener((MouseMotionListener)mMouseEventGeneratingComponent);
		mMouseEventGeneratingComponent.addMouseListener(AutoScroll.this);
		mMouseEventGeneratingComponent.addMouseMotionListener(AutoScroll.this);

		mFirstDragDestination = convertDestinationPoint(new Point(firstDestination));
		
		prepareVisualAssistance();
		
		SwingUtilities.invokeLater(new StartAutoScroll(firstDestination));
	}

	private class StartAutoScroll extends Thread
	{
		private Point	mFirstDragDestination = null;

		public StartAutoScroll(Point firstDestination)
		{
			mFirstDragDestination = firstDestination;
		}

		public void run()
		{
			if(mContinueToScroll)
			{
				calculateDifferences(mFirstDragDestination);

				drawVisualAssistance();
				mTimer = new Timer(AUTO_SCROLL_DELAY, AutoScroll.this);
				mTimer.start();
			}
		}
	}

	abstract void prepareVisualAssistanceCustom();
	abstract void calculateVisualAssistanceCustom(Point destination, int horizontalId, int verticalId);
	abstract void eraseVisualAssistanceCustom(Graphics2D g2d);
	abstract void updateVisualAssistanceAfterScroll(int horizontalOffset, int verticalOffset);
	abstract void drawVisualAssistanceCustom(Graphics2D g2d);
	abstract void cleanupVisualAssistanceCustom();
	Point convertDestinationPoint(Point destination)
	{
        return destination;
	}


	final void prepareVisualAssistance()
	{
		mViewRect = mScrollPane.getViewport().getViewRect();
		mScrollpaneLocationOnGlasspaneX = mScrollPane.getLocationOnScreen().x-((JFrame)mStructurePanel.getTopLevelAncestor()).getContentPane().getLocationOnScreen().x+1;
		mScrollpaneLocationOnGlasspaneY = mScrollPane.getLocationOnScreen().y-((JFrame)mStructurePanel.getTopLevelAncestor()).getContentPane().getLocationOnScreen().y+1;
		JMenuBar menubar = ((JFrame)mStructurePanel.getTopLevelAncestor()).getJMenuBar();
		if(menubar != null)
		{
			mScrollpaneLocationOnGlasspaneY += menubar.getHeight();
		}
		mGlasspaneClip = new Rectangle(mScrollpaneLocationOnGlasspaneX, mScrollpaneLocationOnGlasspaneY, (int)mViewRect.getWidth(), (int)mViewRect.getHeight());
		prepareVisualAssistanceCustom();
	}

	final int getScrollpaneLocationOnGlasspaneX()
	{
		return mScrollpaneLocationOnGlasspaneX;
	}

	final int getScrollpaneLocationOnGlasspaneY()
	{
		return mScrollpaneLocationOnGlasspaneY;
	}

	final int getViewRectWidth()
	{
		return (int)mViewRect.getWidth();
	}

	final int getViewRectHeight()
	{
		return (int)mViewRect.getHeight();
	}

	final StructurePanel getStructurePanel()
	{
		return mStructurePanel;
	}
	
	final JScrollPane getScrollPane()
	{
		return mScrollPane;
	}
	
	final Point getFirstDragDestination()
	{
		return mFirstDragDestination;
	}
	
	final JComponent getMouseEventGeneratingComponent()
	{
		return mMouseEventGeneratingComponent;
	}
	
	final void calculateDifferences(Point destination)
	{
		convertDestinationPoint(destination);
		calculateOffsets(destination);
		calculateVisualAssistance(destination);
	}
	
	final void calculateOffsets(Point destination)
	{
		mViewTopLeftX = (int)mViewRect.getX();
		mViewTopLeftY = (int)mViewRect.getY();
		mViewBottomRightX = (int)(mViewRect.getX()+mViewRect.getWidth()-1);
		mViewBottomRightY = (int)(mViewRect.getY()+mViewRect.getHeight()-1);

		mScrollOffsetX = 0;
		if(destination.x < mViewTopLeftX)
		{
			mScrollOffsetX = destination.x-mViewTopLeftX;
		}
		else if(destination.x > mViewBottomRightX)
		{
			mScrollOffsetX = destination.x-mViewBottomRightX;
		}

		mScrollOffsetY = 0;
		if(destination.y < mViewTopLeftY)
		{
			mScrollOffsetY = destination.y-mViewTopLeftY;
		}
		else if(destination.y > mViewBottomRightY)
		{
			mScrollOffsetY = destination.y-mViewBottomRightY;
		}
	}

	final void calculateVisualAssistance(Point destination)
	{
		mViewRect = mScrollPane.getViewport().getViewRect();
		mViewTopLeftX = (int)mViewRect.getX();
		mViewTopLeftY = (int)mViewRect.getY();
		mViewBottomRightX = (int)(mViewRect.getX()+mViewRect.getWidth()-1);
		mViewBottomRightY = (int)(mViewRect.getY()+mViewRect.getHeight()-1);

		int horizontal_id = -1;
		if(destination.x < mViewTopLeftX)
		{
			horizontal_id = VISUAL_ASSISTANCE_HORIZONTAL_LEFT;
		}
		else if(destination.x > mViewBottomRightX)
		{
			horizontal_id = VISUAL_ASSISTANCE_HORIZONTAL_RIGHT;
		}
		else
		{
			horizontal_id = VISUAL_ASSISTANCE_HORIZONTAL_MIDDLE;
		}

		int vertical_id = -1;
		if(destination.y < mViewTopLeftY)
		{
			vertical_id = VISUAL_ASSISTANCE_VERTICAL_TOP;
		}
		else if(destination.y > mViewBottomRightY)
		{
			vertical_id = VISUAL_ASSISTANCE_VERTICAL_BOTTOM;
		}
		else
		{
			vertical_id = VISUAL_ASSISTANCE_VERTICAL_MIDDLE;
		}

		calculateVisualAssistanceCustom(destination, horizontal_id, vertical_id);
	}

	final void eraseVisualAssistance()
	{
		Graphics2D g2d = (Graphics2D)((JFrame)mStructurePanel.getTopLevelAncestor()).getGlassPane().getGraphics();
		g2d.setClip(mGlasspaneClip);
		eraseVisualAssistanceCustom(g2d);
	}

	final void drawVisualAssistance()
	{
		Graphics2D g2d = (Graphics2D)((JFrame)mStructurePanel.getTopLevelAncestor()).getGlassPane().getGraphics();
		g2d.setClip(mGlasspaneClip);
		drawVisualAssistanceCustom(g2d);
	}

	final public void actionPerformed(ActionEvent e)
	{
		if(mContinueToScroll)
		{
			reposition();
		}
	}

	final void reposition()
	{
		Point current_position = mScrollPane.getViewport().getViewPosition();
		Point new_position = new Point(current_position.x+mScrollOffsetX, current_position.y+mScrollOffsetY);
		if(new_position.x+mViewRect.width > mStructurePanel.getWidth())
		{
			new_position.x = mStructurePanel.getWidth()-mViewRect.width;
		}
		if(new_position.x < 0)
		{
			new_position.x = 0;
		}
		if(new_position.y+mViewRect.height > mStructurePanel.getHeight())
		{
			new_position.y = mStructurePanel.getHeight()-mViewRect.height;
		}
		if(new_position.y < 0)
		{
			new_position.y = 0;
		}

		eraseVisualAssistance();
		mScrollPane.getViewport().setViewPosition(new_position);
		updateVisualAssistanceAfterScroll(new_position.x-current_position.x, new_position.y-current_position.y);
		drawVisualAssistance();
	}

	final void finishAutoScroll()
	{
		if(mTimer != null)
		{
			mContinueToScroll = false;
			mTimer.stop();
			mTimer = null;
			
			cleanupVisualAssistanceCustom();
			mStructurePanel.resetSelectionRectangle();
			mMouseEventGeneratingComponent.removeMouseListener(AutoScroll.this);
			mMouseEventGeneratingComponent.removeMouseMotionListener(AutoScroll.this);
			mStructurePanel.setScrollActive(false);
			mStructurePanel.repaint();
			SwingUtilities.invokeLater(
				new Runnable()
				{
					public void run()
					{
						mMouseEventGeneratingComponent.addMouseListener((MouseListener)mMouseEventGeneratingComponent);
						mMouseEventGeneratingComponent.addMouseMotionListener((MouseMotionListener)mMouseEventGeneratingComponent);
					}
				}
			);
		}
	}

	public void mouseClicked(MouseEvent e)
	{
	}
	
	public void mousePressed(MouseEvent e)
	{
	}
	
	public void mouseReleased(MouseEvent e)
	{
		finishAutoScroll();
		((MouseListener)mMouseEventGeneratingComponent).mouseReleased(e);
	}
	
	public void mouseEntered(MouseEvent e)
	{
		finishAutoScroll();
	}
	
	public void mouseExited(MouseEvent e)
	{
		calculateDifferences(e.getPoint());
	}

	public void mouseDragged(MouseEvent e)
	{
		calculateDifferences(e.getPoint());
	}
	
	public void mouseMoved(MouseEvent e)
	{
	}
}
