/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: StructurePanel.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.old;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.uwyn.rife.config.Config;
import com.uwyn.rife.swing.Cursors;

public class StructurePanel extends JLayeredPane implements ElementListener, MouseListener, MouseMotionListener, KeyListener, PopupMenuListener
{
	private static final float	SCALE_FACTOR_UPPER_LIMIT = 4f;
	private static final float	SCALE_FACTOR_LOWER_LIMIT = 0.125f;

	public static final	int		SELECTION_TOOL = 0;
	public static final	int		ZOOMIN_TOOL = 1;
	public static final	int		ZOOMOUT_TOOL = 2;
	public static final	int		ELEMENT_TOOL = 3;
	public static final	int		CONNECTOR_TOOL = 4;

	private float					mScaleFactor = 1f;

	private JScrollPane				mScrollPane = null;

	private int						mActiveTool = SELECTION_TOOL;

	private int 					mWidth = 0;
	private int 					mHeight = 0;

	private ElementStyle			mElementStyleOrig = new ElementStyle(1f);
	private ElementStyle			mElementStyleScaled = new ElementStyle(mScaleFactor);

	private ArrayList<Element>		mElements = new ArrayList<Element>();
	private ArrayList<Element>		mSelectedElements = new ArrayList<Element>();
	private ElementPropertyEditor	mElementPropertyEditor = null;
	private ElementProperty			mHighlightedProperty = null;
	private ElementProperty			mHighlightedPropertyHidden = null;

	private Point					mDragElementStartPoint = null;
	private Element					mDragElement = null;
	private Element					mDragElementLeftMost = null;
	private Element					mDragElementTopMost = null;

	private Point					mSelectionRectangleStartPoint = null;
	private GeneralPath				mSelectionRectanglePath = null;
	private Point					mSelectionRectangleCurrentPoint = null;
	private Stroke					mSelectionRectangleDashedStroke = new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0f, new float[] {3f}, 0f);

	private boolean					mScrollActive = false;

	private boolean					mPopupMenuActive = false;


	public StructurePanel(JScrollPane scrollPane)
	{
		setDoubleBuffered(true);
		setScrollPane(scrollPane);
		setOpaque(true);
		setBackground(Color.white);
		setLayout(null);
		addMouseListener(this);
		addMouseMotionListener(this);

		calculateDimension();
	}

	public void setScrollPane(JScrollPane scrollPane)
	{
		mScrollPane = scrollPane;
	}

	public JScrollPane getScrollPane()
	{
		return mScrollPane;
	}

	public Collection<Element> getElements()
	{
		return mElements;
	}

	public int getCalculatedWidth()
	{
		return mWidth;
	}

	public int getCalculatedHeight()
	{
		return mHeight;
	}

	public Stroke getSelectionRectangleStroke()
	{
		return mSelectionRectangleDashedStroke;
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if(Config.getRepInstance().getBool("GRID_SHOW"))
		{
			Graphics2D g2d = (Graphics2D)g;
			g2d.setColor(Color.gray);
			Rectangle clip_bounds = g2d.getClipBounds();
			int grid_size = Config.getRepInstance().getInt("GRID_SIZE");
			double grid_size_scaled = grid_size*mScaleFactor;
			if(grid_size_scaled > 0)
			{
				while(grid_size_scaled < 5)
				{
					grid_size_scaled = grid_size_scaled*2;
				}
				double offset_x = clip_bounds.x-(clip_bounds.x%grid_size_scaled);
				double offset_y = clip_bounds.y-(clip_bounds.y%grid_size_scaled);
				double new_clip_width = clip_bounds.width+(clip_bounds.x%grid_size_scaled);
				double new_clip_height = clip_bounds.height+(clip_bounds.y%grid_size_scaled);
				int real_x = 0;
				int real_y = 0;
				for(double x = 0; x <= new_clip_width; x += grid_size_scaled)
				{
					for(double y = 0; y <= new_clip_height; y += grid_size_scaled)
					{
						real_x = (int)(offset_x+x);
						real_y = (int)(offset_y+y);
						g2d.drawLine(real_x, real_y, real_x, real_y);
					}
				}
			}
		}
	}

	private void addElementMouseListeners()
	{
		for (Element element : mElements)
		{
			element.addMouseListener(element);
			element.addMouseMotionListener(element);
		}
	}

	private void removeElementMouseListeners()
	{
		for (Element element : mElements)
		{
			element.removeMouseListener(element);
			element.removeMouseMotionListener(element);
		}
	}

	public void setActiveTool(int tool)
	{
		mActiveTool = tool;
		switch(mActiveTool)
		{
			case SELECTION_TOOL:
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				addElementMouseListeners();
				break;
			case ZOOMIN_TOOL:
				setCursor(Cursors.getRepInstance().getCursor("zoomin"));
				removeElementMouseListeners();
				break;
			case ZOOMOUT_TOOL:
				setCursor(Cursors.getRepInstance().getCursor("zoomout"));
				removeElementMouseListeners();
				break;
			case ELEMENT_TOOL:
				setCursor(Cursors.getRepInstance().getCursor("element"));
				removeElementMouseListeners();
				break;
			case CONNECTOR_TOOL:
				setCursor(Cursors.getRepInstance().getCursor("connector"));
				removeElementMouseListeners();
				break;
		}
	}

	public int getActiveTool()
	{
		return mActiveTool;
	}

	public void setScaleFactor(float scaleFactor)
	{
		if(scaleFactor > SCALE_FACTOR_UPPER_LIMIT)
		{
			scaleFactor = SCALE_FACTOR_UPPER_LIMIT;
		}
		if(scaleFactor < SCALE_FACTOR_LOWER_LIMIT)
		{
			scaleFactor = SCALE_FACTOR_LOWER_LIMIT;
		}
		mScaleFactor = scaleFactor;
	}

	public float getScaleFactor()
	{
		return mScaleFactor;
	}

	public void addElement(String name)
	{
		Element element = new Element(this, name, mElementStyleOrig, mElementStyleScaled);
		if(mActiveTool == SELECTION_TOOL)
		{
			element.addMouseListener(element);
			element.addMouseMotionListener(element);
		}

		int exits = (int)(Math.random()*6);
		int consumeds = (int)(Math.random()*6);
//		int useds = (int)(Math.random()*6);
		int addeds = (int)(Math.random()*6);
		for(int i = 0; i < exits; i++)
		{
			element.addExit("exit"+i);
		}
		for(int i = 0; i < consumeds; i++)
		{
			element.addConsumedParameter("input"+i);
		}
//		for(int i=0; i < useds; i++)
//		{
//			element.addUsedParameter("used"+i);
//		}
		for(int i = 0; i < addeds; i++)
		{
			element.addAddedParameter("output"+i);
		}
		Color body_color = new Color(155+(int)(Math.random()*100), 155+(int)(Math.random()*100), 155+(int)(Math.random()*100));
		element.setElementColor(body_color);
		element.addElementListener(this);
		mElements.add(element);

		this.add(element);
		element.setBounds((int)(Math.random()*800), (int)(Math.random()*600), element.getWidth(), element.getHeight());

		calculateDimension();
	}

	public void removeElement(Element element)
	{
		int index = mElements.indexOf(element);
		if(index != -1)
		{
			mElements.remove(index);
		}
		index = mSelectedElements.indexOf(element);
		if(index != -1)
		{
			mSelectedElements.remove(index);
		}
		remove(element);
		calculateDimension();
		mScrollPane.revalidate();
		repaint();
	}

	void editElementProperty(ElementProperty property, Point location)
	{
		mElementPropertyEditor = new ElementPropertyEditor(property, location);
		elementPropertyHighlighted(property);
	}

 	void removeElementPropertyEditor()
	{
		if(mElementPropertyEditor != null)
		{
			mElementPropertyEditor.destroy();
			mElementPropertyEditor = null;
			synchronizeHighlightedProperty();
		}
	}

	boolean isElementPropertyBeingEdited()
	{
		if(mElementPropertyEditor == null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	private void calculateDimension()
	{
		mWidth = 0;
		mHeight = 0;

		int tmp_width = 0;
		for (Element element : mElements)
		{
			tmp_width = element.getWidth()+element.getX();
			if(tmp_width > mWidth)
			{
				mWidth = tmp_width;
			}
		}

		int tmp_height = 0;
		for (Element element : mElements)
		{
			tmp_height = element.getHeight()+element.getY();
			if(tmp_height > mHeight)
			{
				mHeight = tmp_height;
			}
		}

		if(mWidth == 0 || mHeight == 0)
		{
			mWidth = 800;
			mHeight = 600;
		}
	}

	public Dimension getMinimumSize()
	{
		return new Dimension(mWidth, mHeight);
	}

	public Dimension getPreferredSize()
	{
		return new Dimension(mWidth, mHeight);
	}

	private void deselectOtherElements(Element selected_element)
	{
		for (Element element : mSelectedElements)
		{
			if (element != selected_element)
			{
				element.deselectElement();
			}
		}

		mSelectedElements = new ArrayList<Element>();
	}

	public void elementRepositioned(Element element)
	{
		calculateDimension();
		mScrollPane.revalidate();
	}

	public void elementRaised(Element element)
	{
		if(mActiveTool == SELECTION_TOOL)
		{
			this.moveToFront(element);
		}
	}

	public void elementSelected(Element selectedElement, int modifiers)
	{
		removeElementPropertyEditor();

		if(mActiveTool == SELECTION_TOOL)
		{
			if((modifiers & MouseEvent.SHIFT_MASK) == 0)
			{
				deselectOtherElements(selectedElement);
				selectedElement.fireElementRaised();
			}

			selectedElement.selectElement();
			mSelectedElements.add(selectedElement);

			this.repaint();
		}
	}

	public void elementDeselected(Element deselectedElement, int modifiers)
	{
		removeElementPropertyEditor();

		if((modifiers & MouseEvent.SHIFT_MASK) != 0 &&
		   mSelectedElements.size() > 1)
		{
			mSelectedElements.remove(deselectedElement);
			deselectedElement.deselectElement();
			deselectedElement.repaint();
		}
	}

	private void findLeftTopMostDragElements(int startX, int startY)
	{
		int left_most_x	= startX;
		int top_most_y	= startY;

		for (Element element : mSelectedElements)
		{
			element.startDrag();
			if(element.getX() <= left_most_x)
			{
				left_most_x = element.getX();
				mDragElementLeftMost = element;
			}
			if(element.getY() <= top_most_y)
			{
				top_most_y = element.getY();
				mDragElementTopMost = element;
			}
		}
	}

	public void elementDragStart(Element initiatingElement, Point dragStartPoint)
	{
		this.setCursor(new Cursor(Cursor.MOVE_CURSOR));

		mDragElement = initiatingElement;
		mDragElementStartPoint = dragStartPoint;

		findLeftTopMostDragElements(initiatingElement.getX(), initiatingElement.getY());
	}

	public void repositionElementsDuringDrag(Element initiatingElement, int offsetX, int offsetY)
	{
		if(mDragElementLeftMost.getX()+offsetX < 0)
		{
			offsetX = -1*mDragElementLeftMost.getX();
		}
		if(mDragElementTopMost.getY()+offsetY < 0)
		{
			offsetY = -1*mDragElementTopMost.getY();
		}
		initiatingElement.repositionElementDuringDrag(offsetX, offsetY);
		if(mSelectedElements.size() > 0)
		{
			for (Element element : mSelectedElements)
			{
				if (initiatingElement != element)
				{
					element.repositionElementDuringDrag(offsetX, offsetY);
				}
			}
		}
	}

	public void elementDragged(Element initiatingElement, int dragX, int dragY)
	{
		if(mDragElement == initiatingElement)
		{
			Point element_location = initiatingElement.getLocation();
			Point offset = new Point(dragX-mDragElementStartPoint.x, dragY-mDragElementStartPoint.y);

			repositionElementsDuringDrag(initiatingElement, offset.x, offset.y);

			if(isLocationOutsideStructurepanelView(element_location.x+dragX, element_location.y+dragY))
			{
				new AutoScrollElementDrag(this, mDragElementStartPoint, new Point(dragX, dragY), initiatingElement);
			}
		}
	}

	public boolean isLocationOutsideStructurepanelView(int locationOnStructurePanelX, int locationOnStructurePanelY)
	{
		Rectangle visible_rect = mScrollPane.getViewport().getViewRect();
		if(locationOnStructurePanelX < visible_rect.x ||
		   locationOnStructurePanelX > visible_rect.x+visible_rect.width ||
		   locationOnStructurePanelY < visible_rect.y ||
		   locationOnStructurePanelY > visible_rect.y+visible_rect.height)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public void elementDragEnd()
	{
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

		mDragElement = null;
		mDragElementStartPoint = null;

		for (Element element : mSelectedElements)
		{
			element.endDrag();
		}

		this.repaint();
	}

	public void elementPropertyHighlighted(ElementProperty property)
	{
		if(!isDragActive() &&
		   !isElementPropertyBeingEdited() &&
		   !isPopupMenuActive())
		{
			ElementProperty previous_highlight = mHighlightedProperty;
			mHighlightedProperty = property;
			if(previous_highlight != null)
			{
				previous_highlight.getElement().repaint(previous_highlight.getHotSpot().getBounds());
			}
			if(mHighlightedProperty != null)
			{
				mHighlightedProperty.getElement().repaint(mHighlightedProperty.getHotSpot().getBounds());
			}
		}

		if(isElementPropertyBeingEdited() ||
		   isPopupMenuActive())
		{
			mHighlightedPropertyHidden = property;
		}
	}

	void drawHighlightedProperty(Element element, Graphics2D g2d)
	{
		if(mHighlightedProperty != null &&
		   mHighlightedProperty.getElement() == element &&
		   !isDragActive() &&
		   !isScrollActive())
		{
			mHighlightedProperty.drawActive(g2d);
		}
	}

	private void synchronizeHighlightedProperty()
	{
		elementPropertyHighlighted(mHighlightedPropertyHidden);
		mHighlightedPropertyHidden = null;
	}

	public void changeZoom(float multiplier)
	{
		changeZoom(multiplier, null);
	}

	public void changeZoom(float multiplier, Point centerPoint)
	{
		Cursor previous_cursor = this.getCursor();

		if(mScaleFactor*multiplier > SCALE_FACTOR_UPPER_LIMIT)
		{
			multiplier = SCALE_FACTOR_UPPER_LIMIT/mScaleFactor;
		}
		if(mScaleFactor*multiplier < SCALE_FACTOR_LOWER_LIMIT)
		{
			multiplier = SCALE_FACTOR_LOWER_LIMIT/mScaleFactor;
		}

		if(multiplier != 1)
		{
			this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

			JPanel panel = new JPanel();
			panel.setBackground(getBackground());
			mScrollPane.getViewport().setView(panel);
			setScaleFactor(mScaleFactor*multiplier);

			mElementStyleScaled.calculateStyle(mScaleFactor);

			for (Element element : mElements)
			{
				element.scalePrecalculatedAreas(multiplier);
			}

			mWidth *= multiplier;
			mHeight *= multiplier;

			revalidate();

			if(centerPoint != null)
			{
				centerPoint.x *= multiplier;
				centerPoint.y *= multiplier;
			}
			SwingUtilities.invokeLater(new StructurePanelRepositioner(this, centerPoint, previous_cursor));
		}
	}

	public boolean isDragActive()
	{
		if(mDragElement != null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean isSelectionRectangleActive()
	{
		if(mSelectionRectangleStartPoint != null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean isPopupMenuActive()
	{
		return mPopupMenuActive;
	}

	void resetSelectionRectangle()
	{
		mSelectionRectanglePath = null;
	}

	private void initializeSelectionRectangle()
	{
		mSelectionRectanglePath = new GeneralPath();
	}

	void eraseSelectionRectangle()
	{
		Graphics2D g2d = (Graphics2D)getGraphics();
		if(g2d != null &&
		   mSelectionRectanglePath != null)
		{
			g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			g2d.setStroke(mSelectionRectangleDashedStroke);
			g2d.setXORMode(Color.white);

			g2d.draw(mSelectionRectanglePath);
		}
	}

	private void drawSelectionRectangle(Point currentPoint)
	{
		Graphics2D g2d = (Graphics2D)getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g2d.setStroke(mSelectionRectangleDashedStroke);
		g2d.setXORMode(Color.white);

		createSelectionRectanglePath(currentPoint);
		g2d.draw(mSelectionRectanglePath);
	}

	private void createSelectionRectanglePath(Point currentPoint)
	{
		mSelectionRectangleCurrentPoint = currentPoint;

		mSelectionRectanglePath.reset();
		mSelectionRectanglePath.moveTo(mSelectionRectangleStartPoint.x, mSelectionRectangleStartPoint.y);
		mSelectionRectanglePath.lineTo(mSelectionRectangleCurrentPoint.x, mSelectionRectangleStartPoint.y);
		mSelectionRectanglePath.lineTo(mSelectionRectangleCurrentPoint.x, mSelectionRectangleCurrentPoint.y);
		mSelectionRectanglePath.lineTo(mSelectionRectangleStartPoint.x, mSelectionRectangleCurrentPoint.y);
		mSelectionRectanglePath.lineTo(mSelectionRectangleStartPoint.x, mSelectionRectangleStartPoint.y);
	}

	public void setScrollActive(boolean active)
	{
		mScrollActive = active;
	}

	public boolean isScrollActive()
	{
		return mScrollActive;
	}

	public void mouseClicked(MouseEvent e)
	{
		if((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
		{
			switch(mActiveTool)
			{
				case SELECTION_TOOL:
					deselectOtherElements(null);
					this.repaint();
					break;
				case ZOOMIN_TOOL:
				case ZOOMOUT_TOOL:
					Point center_point = e.getPoint();
					if(mActiveTool==ZOOMIN_TOOL)
					{
						changeZoom(2f, center_point);
					}
					else
					{
						changeZoom(0.5f, center_point);
					}
					break;
			}
		}
	}

	public void mousePressed(MouseEvent e)
	{
		if((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
		{
			switch(mActiveTool)
			{
				case SELECTION_TOOL:
					removeElementMouseListeners();
				case ZOOMIN_TOOL:
				case ZOOMOUT_TOOL:
					mSelectionRectangleStartPoint = e.getPoint();
					break;
			}
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		removeElementPropertyEditor();
		if((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
		{
			switch(mActiveTool)
			{
				case SELECTION_TOOL:
					addElementMouseListeners();
				case ZOOMIN_TOOL:
				case ZOOMOUT_TOOL:
					if(mSelectionRectangleStartPoint != null &&
					   mSelectionRectangleCurrentPoint == null)
					{
						mSelectionRectangleStartPoint = null;
						mSelectionRectanglePath = null;
					}
					else if(mSelectionRectangleStartPoint != null && mSelectionRectangleCurrentPoint != null)
					{
						float rect_x = 0;
						float rect_y = 0;
						float rect_width = 0;
						float rect_height = 0;
						if(mSelectionRectangleStartPoint.x <= mSelectionRectangleCurrentPoint.x && mSelectionRectangleStartPoint.y <= mSelectionRectangleCurrentPoint.y)
						{
							rect_x = mSelectionRectangleStartPoint.x;
							rect_y = mSelectionRectangleStartPoint.y;
							rect_width = mSelectionRectangleCurrentPoint.x-mSelectionRectangleStartPoint.x;
							rect_height = mSelectionRectangleCurrentPoint.y-mSelectionRectangleStartPoint.y;
						}
						else if(mSelectionRectangleStartPoint.x >= mSelectionRectangleCurrentPoint.x && mSelectionRectangleStartPoint.y <= mSelectionRectangleCurrentPoint.y)
						{
							rect_x = mSelectionRectangleCurrentPoint.x;
							rect_y = mSelectionRectangleStartPoint.y;
							rect_width = mSelectionRectangleStartPoint.x-mSelectionRectangleCurrentPoint.x;
							rect_height = mSelectionRectangleCurrentPoint.y-mSelectionRectangleStartPoint.y;
						}
						else if(mSelectionRectangleStartPoint.x <= mSelectionRectangleCurrentPoint.x && mSelectionRectangleStartPoint.y >= mSelectionRectangleCurrentPoint.y)
						{
							rect_x = mSelectionRectangleStartPoint.x;
							rect_y = mSelectionRectangleCurrentPoint.y;
							rect_width = mSelectionRectangleCurrentPoint.x-mSelectionRectangleStartPoint.x;
							rect_height = mSelectionRectangleStartPoint.y-mSelectionRectangleCurrentPoint.y;
						}
						else if(mSelectionRectangleStartPoint.x >= mSelectionRectangleCurrentPoint.x && mSelectionRectangleStartPoint.y >= mSelectionRectangleCurrentPoint.y)
						{
							rect_x = mSelectionRectangleCurrentPoint.x;
							rect_y = mSelectionRectangleCurrentPoint.y;
							rect_width = mSelectionRectangleStartPoint.x-mSelectionRectangleCurrentPoint.x;
							rect_height = mSelectionRectangleStartPoint.y-mSelectionRectangleCurrentPoint.y;
						}

						switch(mActiveTool)
						{
							case SELECTION_TOOL:
								if((e.getModifiers() & MouseEvent.SHIFT_MASK) == 0)
								{
									deselectOtherElements(null);
								}

								// get elements in the correct Z order
								Component[] components = getComponents();
								Element element = null;
								for(int i = components.length-1; i >= 0; i--)
								{
									if(components[i] instanceof Element)
									{
										element = (Element)components[i];
										if(element.getBoundingAreaScaled().intersects(new Rectangle2D.Float(rect_x-element.getX(), rect_y-element.getY(), rect_width, rect_height)) && !element.isSelected())
										{
											mSelectedElements.add(element);
											element.selectElement();
										}
									}
								}
								break;
							case ZOOMIN_TOOL:
								{
									float width_factor = this.getParent().getWidth()/rect_width;
									float height_factor = this.getParent().getHeight()/rect_height;
									float factor = 0;
									if(width_factor < height_factor)
									{
										factor = width_factor;
									}
									else
									{
										factor = height_factor;
									}
									changeZoom(factor, new Point((int)(rect_x+rect_width/2), (int)(rect_y+rect_height/2)));
								}
								break;
							case ZOOMOUT_TOOL:
								{
									float width_factor = rect_width/this.getParent().getWidth();
									float height_factor = rect_height/this.getParent().getHeight();
									float factor = 0;
									if(width_factor < height_factor)
									{
										factor = width_factor;
									}
									else
									{
										factor = height_factor;
									}
									changeZoom(factor, new Point((int)(rect_x+rect_width/2), (int)(rect_y+rect_height/2)));
								}
								break;
						}

						eraseSelectionRectangle();

						mSelectionRectangleStartPoint = null;
						mSelectionRectangleCurrentPoint = null;
						mSelectionRectanglePath = null;

						this.repaint();
					}
					break;
			}
		}
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
		if(isSelectionRectangleActive())
		{
			new AutoScrollSelectionRectangle(this, e.getPoint(), mSelectionRectangleStartPoint);
		}
	}

	public void mouseDragged(MouseEvent e)
	{
		Point current_point = e.getPoint();

		if(isSelectionRectangleActive())
		{
			Rectangle view_rect = mScrollPane.getViewport().getViewRect();

			if(current_point.x-view_rect.getX() > view_rect.getWidth() ||
			   current_point.y-view_rect.getY() > view_rect.getHeight())
			{
				new AutoScrollSelectionRectangle(this, e.getPoint(), mSelectionRectangleStartPoint);
			}
			else
			{
				if(mSelectionRectanglePath == null)
				{
					initializeSelectionRectangle();
				}
				else
				{
					eraseSelectionRectangle();
				}


				drawSelectionRectangle(current_point);
			}
		}
	}

	public void mouseMoved(MouseEvent e)
	{
		elementPropertyHighlighted(null);
	}

	public void keyTyped(KeyEvent e)
	{
	}

	public void keyPressed(KeyEvent e)
	{
		if(mActiveTool == ZOOMIN_TOOL &&
		   e.getKeyCode() == KeyEvent.VK_SHIFT)
		{
			setActiveTool(ZOOMOUT_TOOL);
		}
	}

	public void keyReleased(KeyEvent e)
	{
		if(mActiveTool == ZOOMOUT_TOOL &&
		   e.getKeyCode() == KeyEvent.VK_SHIFT)
		{
			setActiveTool(ZOOMIN_TOOL);
		}
	}

	public void popupMenuWillBecomeVisible(PopupMenuEvent e)
	{
		mPopupMenuActive = true;
	}

	public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
	{
		mPopupMenuActive = false;
		synchronizeHighlightedProperty();
	}

	public void popupMenuCanceled(PopupMenuEvent e)
	{
	}
}
