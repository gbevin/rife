/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Element.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.old;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.LineMetrics;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.uwyn.rife.config.Config;
import com.uwyn.rife.gui.Rife;
import com.uwyn.rife.swing.JDialogSystemError;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.Localization;

public class Element extends JComponent implements MouseListener, MouseMotionListener
{
	private boolean					mSelected = false;

	private Color					mTitleBackgroundColor = new Color(220, 220, 220);
	private StructurePanel			mStructurePanel = null;
	private	ElementStyle			mElementStyleOrig = null;
	private	ElementStyle			mElementStyleScaled = null;

	private ArrayList<ElementListener>	mElementListeners = null;
	private ArrayList<ElementProperty>	mElementProperties = null;

	private float					mXOrig = -1f;
	private float					mYOrig = -1f;
	private float					mWidthOrig = -1f;
	private float					mHeightOrig = -1f;
	private float					mBodyXOffsetOrig = -1f;
	private float					mBodyYOffsetOrig = -1f;
	private Area					mBoundingOrig = null;
	private Area					mBodyAreaOutsideOrig = null;
	private Area					mBodyAreaInsideOrig = null;
	private GeneralPath				mUsedParameterLinesOrig = null;
	private Area					mTitleRectangleOutsideOrig = null;
	private Area					mTitleRectangleInsideOrig = null;

	private float					mScaleFactor = 1f;

	private float					mXScaled = -1f;
	private float					mYScaled = -1f;
	private float					mWidthScaled = -1f;
	private float					mHeightScaled = -1f;
	private Area					mBoundingScaled = null;
	private Area					mBodyAreaOutsideScaled = null;
	private Area					mBodyAreaInsideScaled = null;
	private GeneralPath				mUsedParameterLinesScaled = null;
	private Area					mTitleRectangleOutsideScaled = null;
	private Area					mTitleRectangleInsideScaled = null;

	private boolean					mDragActive = false;
	private Point					mDragStartPoint = null;
	private BufferedImage			mDragBufferedImage = null;

	private boolean					mIsTransparent = false;

	public Element(StructurePanel structurePanel, ElementStyle styleOrig, ElementStyle styleScaled)
	{
		this(structurePanel, "", styleOrig, styleScaled);
	}

	public Element(StructurePanel structurePanel, String title, ElementStyle styleOrig, ElementStyle styleScaled)
	{
		super();

		mStructurePanel = structurePanel;
		mElementStyleOrig = styleOrig;
		mElementStyleScaled = styleScaled;

		mElementListeners = new ArrayList<ElementListener>();
		mElementProperties = new ArrayList<ElementProperty>();
		addProperty(ElementPropertyTitle.class, title);

		setOpaque(false);
		setLayout(null);
	}

	public StructurePanel getStructurePanel()
	{
		return mStructurePanel;
	}

	public int getWidth()
	{
		if(mWidthScaled == -1)
		{
			createPrecalculatedAreas();
		}
		return (int)mWidthScaled;
	}

	public int getHeight()
	{
		if(mHeightScaled == -1)
		{
			createPrecalculatedAreas();
		}
		return (int)mHeightScaled;
	}

	public Area getBoundingAreaOrig()
	{
		return mBoundingOrig;
	}

	public Area getBoundingAreaScaled()
	{
		return mBoundingScaled;
	}

	public Area getTitleRectangleOutside()
	{
		return mTitleRectangleOutsideScaled;
	}

	public Area getTitleRectangleInside()
	{
		return mTitleRectangleInsideScaled;
	}

	public void setLocation(Point p)
	{
		setLocation(p.x, p.y);
	}

	public void setLocation(int x, int y)
	{
		setBounds(x, y, getWidth(), getHeight());
	}

	public void setBounds(Rectangle r)
	{
		setBounds(r.x, r.x, r.width, r.height);
	}

	public void setBounds(int x, int y, int width, int height)
	{
		mXOrig = x/mScaleFactor;
		mYOrig = y/mScaleFactor;
		if(Config.getRepInstance().getBool("GRID_SNAP"))
		{
			int grid_size = Config.getRepInstance().getInt("GRID_SIZE");
			mXOrig = mXOrig-((mXOrig+mBodyXOffsetOrig)%grid_size);
			mYOrig = mYOrig-((mYOrig+mBodyYOffsetOrig)%grid_size);
		}
		mXScaled = mXOrig*mScaleFactor;
		mYScaled = mYOrig*mScaleFactor;
		super.setBounds((int)mXScaled, (int)mYScaled, width, height);
	}

	public int getX()
	{
		return (int)mXScaled;
	}

	public int getY()
	{
		return (int)mYScaled;
	}

	public Rectangle getBounds(Rectangle rv)
	{
		if(rv == null)
		{
			rv = new Rectangle();
		}

		rv.x = getX();
		rv.y = getY();
		rv.width = getWidth();
		rv.height = getHeight();

		return rv;
	}

	public void setElementColor(Color bodyColor)
	{
		mTitleBackgroundColor = bodyColor;
		repaint();
	}

	public void selectElement()
	{
		if(!mSelected)
		{
			mSelected = true;
		}
	}

	public void deselectElement()
	{
		if(mSelected)
		{
			mSelected = false;
		}
	}

	public boolean isSelected()
	{
		return mSelected;
	}

	public ElementStyle getElementStyleScaled()
	{
		return mElementStyleScaled;
	}

	protected void resetPrecalculatedAreas()
	{
		mWidthOrig = -1f;
		mHeightOrig = -1f;
		mBodyXOffsetOrig = -1f;
		mBodyYOffsetOrig = -1f;
		mBoundingOrig = null;
		mBodyAreaOutsideOrig = null;
		mBodyAreaInsideOrig = null;
		mUsedParameterLinesOrig = null;

		mWidthScaled = -1f;
		mHeightScaled = -1f;
		mBoundingScaled = null;
		mBodyAreaOutsideScaled = null;
		mBodyAreaInsideScaled = null;
		mUsedParameterLinesScaled = null;
	}

	ElementProperty createProperty(Class propertyClass, String name)
	{
		ElementProperty property = null;
		try
		{
			property = (ElementProperty)propertyClass.getConstructor(new Class[] {Element.class, String.class}).newInstance(new Object[] {this, name});
		}
		catch (Throwable e)
		{
			JDialogSystemError dialog = new JDialogSystemError(Rife.getMainFrame(),
							   "Element.createProperty() : Error while creating an element property with name '"+name+"' and class '"+
							   propertyClass.getName()+"': "+ExceptionUtils.getExceptionStackTrace(e));
			dialog.setVisible(true);
			Rife.quit();
		}

		return property;
	}

	ElementProperty getProperty(Class propertyClass, String name)
	{
		int index = mElementProperties.indexOf(createProperty(propertyClass, name));
		if(index == -1)
		{
			return null;
		}
		else
		{
			return mElementProperties.get(index);
		}
	}

	ElementProperty addProperty(Class propertyClass, String name)
	{
		ElementProperty property = createProperty(propertyClass, name);

		if(property != null)
		{
			if(!mElementProperties.contains(property))
			{
				mElementProperties.add(property);
				resetPrecalculatedAreas();
				return property;
			}
		}

		return null;
	}

	boolean removeProperty(ElementProperty property)
	{
		if(mElementProperties.remove(property))
		{
			resetPrecalculatedAreas();
			return true;
		}
		else
		{
			return false;
		}
	}

	boolean renameProperty(ElementProperty property, String newName)
	{
		if(mElementProperties.contains(property))
		{
			if(property.isValidName(newName))
			{
				property.setName(newName);
				resetPrecalculatedAreas();
				return true;
			}
			else
			{
				return false;
			}
		}

		return false;
	}

	ElementPropertyExit addExit(String name)
	{
		return (ElementPropertyExit)addProperty(ElementPropertyExit.class, name);
	}

	ElementPropertyParameterConsumed addConsumedParameter(String name)
	{
		return (ElementPropertyParameterConsumed)addProperty(ElementPropertyParameterConsumed.class, name);
	}

	ElementPropertyParameterUsed addUsedParameter(String name)
	{
		return (ElementPropertyParameterUsed)addProperty(ElementPropertyParameterUsed.class, name);
	}

	ElementPropertyParameterAdded addAddedParameter(String name)
	{
		return (ElementPropertyParameterAdded)addProperty(ElementPropertyParameterAdded.class, name);
	}

	ElementProperty getTitle()
	{
		return getProperties(ElementPropertyTitle.class).get(0);
	}

	ArrayList<ElementProperty> getExits()
	{
		return getProperties(ElementPropertyExit.class);
	}

	ArrayList<ElementProperty> getConsumedParameters()
	{
		return getProperties(ElementPropertyParameterConsumed.class);
	}

	ArrayList<ElementProperty> getUsedParameters()
	{
		return getProperties(ElementPropertyParameterUsed.class);
	}

	ArrayList<ElementProperty> getAddedParameters()
	{
		return getProperties(ElementPropertyParameterAdded.class);
	}

	ArrayList<ElementProperty> getProperties(Class propertyClass)
	{
		ArrayList<ElementProperty> result = new ArrayList<ElementProperty>();
		
		for (ElementProperty property : mElementProperties)
		{
			if(propertyClass.isInstance(property))
			{
				result.add(property);
			}
		}
		return result;
	}

	int countExits()
	{
		return countProperties(ElementPropertyExit.class);
	}

	int countConsumedParameters()
	{
		return countProperties(ElementPropertyParameterConsumed.class);
	}

	int countUsedParameters()
	{
		return countProperties(ElementPropertyParameterUsed.class);
	}

	int countAddedParameters()
	{
		return countProperties(ElementPropertyParameterAdded.class);
	}

	private int countProperties(Class propertyClass)
	{
		int result = 0;
		
		for (ElementProperty property : mElementProperties)
		{
			if(propertyClass.isInstance(property))
			{
				result++;
			}
		}
		return result;
	}

	public Dimension getMinimumSize()
	{
		return new Dimension((int)mWidthScaled, (int)mHeightScaled);
	}

	public Dimension getPreferredSize()
	{
		return getMinimumSize();
	}

	public Dimension getMaximumSize()
	{
		return getMinimumSize();
	}

	public boolean contains(int x, int y)
	{
		if(mBoundingScaled != null&&
		   x >= 0 && y >= 0 &&
		   x < mWidthScaled && y < mHeightScaled &&
		   mBoundingScaled.contains(x, y))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public synchronized void scalePrecalculatedAreas(float multiplier)
	{
		mScaleFactor *= multiplier;

		mXScaled = mXOrig * mScaleFactor;
		mYScaled = mYOrig * mScaleFactor;
		mWidthScaled = mWidthOrig * mScaleFactor;
		mHeightScaled = mHeightOrig * mScaleFactor;

		AffineTransform scale_transform = AffineTransform.getScaleInstance(mScaleFactor, mScaleFactor);
		mBoundingScaled = mBoundingOrig.createTransformedArea(scale_transform);
		mBodyAreaOutsideScaled = mBodyAreaOutsideOrig.createTransformedArea(scale_transform);
		mBodyAreaInsideScaled = mBodyAreaInsideOrig.createTransformedArea(scale_transform);
		mTitleRectangleOutsideScaled = mTitleRectangleOutsideOrig.createTransformedArea(scale_transform);
		mTitleRectangleInsideScaled = mTitleRectangleInsideOrig.createTransformedArea(scale_transform);
		mUsedParameterLinesScaled = (GeneralPath)mUsedParameterLinesOrig.clone();
		mUsedParameterLinesScaled.transform(scale_transform);

		for (ElementProperty property : mElementProperties)
		{
			property.scale(multiplier);
		}

		super.setBounds((int)mXScaled, (int)mYScaled, (int)mWidthScaled, (int)mHeightScaled);
	}

	private synchronized void createPrecalculatedAreas()
	{
        int grid_size = Config.getRepInstance().getInt("GRID_SIZE");
		LineMetrics line_metrics = null;
		Rectangle2D	string_bounds = null;
		Rectangle2D name_bounds = null;
		Area hotspot = null;

		//
		// calculate parameters dimensions
		//
		line_metrics = mElementStyleOrig.mParamFont.getLineMetrics("M", mElementStyleOrig.mFontRenderContext);
		float parameter_text_width = (float)(mElementStyleOrig.mParamFont.getStringBounds("M", mElementStyleOrig.mFontRenderContext).getHeight());
		float parameter_text_middle = line_metrics.getDescent()-line_metrics.getStrikethroughOffset();

		// calculate the parameter rectangle width, this is the distance that one parameter needs to draw its components
        // and to be aligned to the grid
		float parameter_rectangle_width_minimum = parameter_text_width+
			(mElementStyleOrig.mParamMarginWidth*2)+
			mElementStyleOrig.mParamLineWidth;
		float parameter_rectangle_width_unit = grid_size*2;
        float parameter_rectangle_width = parameter_rectangle_width_unit;
        if(parameter_rectangle_width_minimum > parameter_rectangle_width_unit)
        {
            parameter_rectangle_width = (float)(Math.ceil(parameter_rectangle_width_minimum/parameter_rectangle_width_unit)*parameter_rectangle_width_unit);
        }

		// calculate to FINAL WIDTH of the complete consumed parameters rectangle
		float consumed_parameters_rectangle_width = 0;
		float number_of_consumed_parameters = countConsumedParameters();
		if(number_of_consumed_parameters > 0)
		{
			consumed_parameters_rectangle_width = (number_of_consumed_parameters*parameter_rectangle_width)+
				((number_of_consumed_parameters+1)*(parameter_rectangle_width/2));
		}

		// calculate to FINAL WIDTH of the complete used parameters rectangle
		float used_parameters_rectangle_width = 0;
		float number_of_used_parameters = countUsedParameters();
		if(number_of_used_parameters > 0)
		{
			used_parameters_rectangle_width = (number_of_used_parameters*parameter_rectangle_width)+
				((number_of_used_parameters+1)*(parameter_rectangle_width/2));

			if(number_of_consumed_parameters == 0)
			{
				used_parameters_rectangle_width += mElementStyleOrig.mElementBorderWidth;
			}
		}

		// calculate to FINAL WIDTH of the complete added parameters rectangle
		float added_parameters_rectangle_width = 0;
		float number_of_added_parameters = countAddedParameters();
		if(number_of_added_parameters > 0)
		{
			added_parameters_rectangle_width = number_of_added_parameters*parameter_rectangle_width+
				number_of_added_parameters*(parameter_rectangle_width/2);
		}


		//
		// calculate title dimensions
		//
		line_metrics = mElementStyleOrig.mTitleFont.getLineMetrics("M", mElementStyleOrig.mFontRenderContext);
		Rectangle2D name_text_bounds = mElementStyleOrig.mTitleFont.getStringBounds(getTitle().getName(), mElementStyleOrig.mFontRenderContext);
		float title_text_height = (float)(name_text_bounds.getHeight());
		float title_text_width = (float)(name_text_bounds.getWidth());

		// calculate the MINIMAL WIDTH of the TITLE RECTANGLE so that it contains at least the title text
		float name_rectangle_width = title_text_width+
			mElementStyleOrig.mElementBorderWidth*2+
			mElementStyleOrig.mTitleMarginWidth*2;
        // snap it to grid units
        name_rectangle_width = (float)(Math.ceil(name_rectangle_width/grid_size)*grid_size);
		// calculate the FINAL HEIGHT of the TITLE RECTANGLE
		float name_rectangle_height = title_text_height+
			mElementStyleOrig.mElementBorderWidth*2+
			mElementStyleOrig.mTitleMarginHeight*2;
        // snap it to grid units
        name_rectangle_height = (float)(Math.ceil(name_rectangle_height/grid_size)*grid_size);

		// calculate the temporary width of the complete element rectangle and take space for the right edge
		float element_rectangle_width = consumed_parameters_rectangle_width+
			used_parameters_rectangle_width+
            parameter_rectangle_width+
            name_rectangle_width+
            mElementStyleOrig.mElementBorderWidth;

		// calculate the FINAL X OFFSET to start the TITLE BOX immediatly right of the used parameters rectangle
		float name_rectangle_x_offset = element_rectangle_width-name_rectangle_width;


		//
		// make sure that the element is wide enough to contain all the parameters and exit widths
		//

		// calculate the maximum width of all the exit texts
		float exits_width = 0;
		float exit_string_width = 0;
		
		for (ElementProperty exit : getExits())
		{
			exit_string_width = (float)(mElementStyleOrig.mExitFont.getStringBounds(exit.getName(), mElementStyleOrig.mFontRenderContext).getWidth());
			if(exit_string_width > exits_width)
			{
				exits_width = exit_string_width;
			}
		}

		// calculate the minimal body rectangle width to known what size has to be at least available to display all the items
		// inside the body (consumed parameter texts, used parameter texts and lines, added parameters texts, exit texts)
		float minimal_body_rectangle_width = consumed_parameters_rectangle_width+
			used_parameters_rectangle_width+
			added_parameters_rectangle_width+
            parameter_rectangle_width+
			mElementStyleOrig.mExitMarginWidth+exits_width+mElementStyleOrig.mExitMarginWidth;
        // snap yhe minimal body rectangle width to grid units and take space for the right edge
        minimal_body_rectangle_width = (float)(Math.ceil(minimal_body_rectangle_width/grid_size)*grid_size)+mElementStyleOrig.mElementBorderWidth;

		if(element_rectangle_width < minimal_body_rectangle_width)
		{
			// calculate the FINAL WIDTH of the ELEMENT RECTANGLE
			element_rectangle_width = minimal_body_rectangle_width;
			// calculate the FINAL WIDTH of the TITLE RECTANGLE so that it's wide enough to fill all the space
			// right of the used parameters rectangle
			name_rectangle_width = element_rectangle_width-
				(consumed_parameters_rectangle_width+used_parameters_rectangle_width+parameter_rectangle_width+mElementStyleOrig.mElementBorderWidth);
		}

		//
		// calculate body dimensions
		//
		line_metrics = mElementStyleOrig.mExitFont.getLineMetrics("M", mElementStyleOrig.mFontRenderContext);
		float exit_text_height = (float)(mElementStyleOrig.mExitFont.getStringBounds("M", mElementStyleOrig.mFontRenderContext).getHeight());
		// calculate the exit rectangle height, this is the distance that one exit needs to draw its text and rectangle
		float exit_rectangle_height = exit_text_height+
			(mElementStyleOrig.mElementBorderWidth*2)+
			(mElementStyleOrig.mExitMarginHeight*2);
        // snap it to grid units
        exit_rectangle_height = (float)(Math.ceil(exit_rectangle_height/grid_size)*grid_size);
		// calculate the width of the exit rectangle
		float exit_rectangle_width = grid_size*4+mElementStyleOrig.mElementBorderWidth;
		// calculate the vertical offset from one exit to the next one
		float exit_rectangle_y_interval = exit_rectangle_height+(exit_rectangle_height/2);
        // take space for the bottom edge
        exit_rectangle_height += mElementStyleOrig.mElementBorderWidth;

		// calculate the MINIMUM HEIGHT of the BODY RECTANGLE
		float body_rectangle_height = name_rectangle_height*2;
		// calculate the FINAL WIDTH of the BODY RECTANGLE
		float body_rectangle_width = element_rectangle_width;
		// calculate the temporary height of the body rectangle, this is large enough to display the title and all the exits
		float number_of_exits = countExits();
		if(number_of_exits > 0)
		{
			body_rectangle_height = name_rectangle_height+
				number_of_exits*exit_rectangle_y_interval+
                exit_rectangle_y_interval/3;
		}

		//
		// make sure that the body is high enough to contain all the parameters in height
		//

		// calculate the maximum height of all the consumed parameter texts
		float parameters_height = 0;
		float parameter_string_width = 0;
		
		for (ElementProperty parameter : getConsumedParameters())
		{
			parameter_string_width = (float)(mElementStyleOrig.mParamFont.getStringBounds(parameter.getName(), mElementStyleOrig.mFontRenderContext).getWidth());
			if(parameter_string_width > parameters_height)
			{
				parameters_height = parameter_string_width;
			}
		}
		// calculate the minimal body rectangle height that is required to correctly display the longest consumed parameter
		float minimal_body_rectangle_height_for_parameters = mElementStyleOrig.mParamLineLength+
			mElementStyleOrig.mParamMarginHeight+
			parameters_height+
			(mElementStyleOrig.mElementBorderWidth*2);
		// calculate the height of the body rectangle and make it at least as big as the previously calculated minimal body height
		if(body_rectangle_height < minimal_body_rectangle_height_for_parameters)
		{
			body_rectangle_height = minimal_body_rectangle_height_for_parameters;
		}

		// calculate the maximum height of all the used parameter texts
		parameters_height = 0;
		parameter_string_width = 0;
		
		for (ElementProperty parameter : getUsedParameters())
		{
			parameter_string_width = (float)(mElementStyleOrig.mParamFont.getStringBounds(parameter.getName(), mElementStyleOrig.mFontRenderContext).getWidth());
			if(parameter_string_width > parameters_height)
			{
				parameters_height = parameter_string_width;
			}
		}
		// calculate the minimal body rectangle height that is required to correctly display the longest used parameter
		minimal_body_rectangle_height_for_parameters = mElementStyleOrig.mParamLineLength+
			mElementStyleOrig.mParamMarginHeight+
			parameters_height+
			(mElementStyleOrig.mElementBorderWidth*2);
		// calculate the height of the body rectangle and make it at least as big as the previously calculated minimal body height
		if(body_rectangle_height < minimal_body_rectangle_height_for_parameters)
		{
			body_rectangle_height = minimal_body_rectangle_height_for_parameters;
		}

		// calculate the maximum height of all the added parameter texts
		parameters_height = 0;
		
		for (ElementProperty parameter : getAddedParameters())
		{
			parameter_string_width	= (float)(mElementStyleOrig.mParamFont.getStringBounds(parameter.getName(), mElementStyleOrig.mFontRenderContext).getWidth());
			if(parameter_string_width > parameters_height)
			{
				parameters_height = parameter_string_width;
			}
		}
		// calculate the minimal body rectangle height that is required to correctly display the longest added parameter
		minimal_body_rectangle_height_for_parameters = name_rectangle_height+
			(mElementStyleOrig.mParamLineLength/2)+
			parameters_height+
			mElementStyleOrig.mParamMarginHeight+
			(mElementStyleOrig.mElementBorderWidth*2);
		// calculate the height of the body rectangle and make it at least as big as the previously calculated minimal body height
		if(body_rectangle_height < minimal_body_rectangle_height_for_parameters)
		{
			body_rectangle_height = minimal_body_rectangle_height_for_parameters;
		}
        // snap the height of the body to grid units and take space for the bottom edge
        body_rectangle_height = (float)(Math.ceil(body_rectangle_height/grid_size)*grid_size)+mElementStyleOrig.mElementBorderWidth;

		//
		// calculate the width and height of the buffered image
		//
		mBodyXOffsetOrig = 0;
		mBodyYOffsetOrig = 0;
		// calculate the required vertical offset that is needed for the optional top parameter handles (consumed and used)
		if(number_of_consumed_parameters > 0 || number_of_used_parameters > 0)
		{
			mBodyYOffsetOrig += mElementStyleOrig.mParamLineLength/2;
		}
		// calculate the body width, taking into account the existance of exits
		if(number_of_exits > 0)
		{
			mWidthOrig = element_rectangle_width+exit_rectangle_width+1;
		}
		else
		{
			mWidthOrig = element_rectangle_width+1;
		}
		// calculate the body height, taking into account the space taken by optional parameter handles
		if(number_of_consumed_parameters > 0 || number_of_used_parameters > 0 || number_of_added_parameters > 0)
		{
			mHeightOrig = body_rectangle_height+mElementStyleOrig.mParamLineLength+1;
		}
		else
		{
			mHeightOrig = body_rectangle_height+1;
		}

		//
		// precalculate the body shape
		//

		// create the body area, including the outside stroke
		Area body_area_outside = new Area(new Rectangle2D.Float(mBodyXOffsetOrig, mBodyYOffsetOrig,
																body_rectangle_width, body_rectangle_height));
		// create the body area, excluding the outside stroke
		Area body_area_inside = new Area(new Rectangle2D.Float(mBodyXOffsetOrig+mElementStyleOrig.mElementBorderWidth, mBodyYOffsetOrig+mElementStyleOrig.mElementBorderWidth,
															   body_rectangle_width-mElementStyleOrig.mElementBorderWidth*2, body_rectangle_height-mElementStyleOrig.mElementBorderWidth*2));
		// if there are exits, add them to the body shape
		if(number_of_exits > 0)
		{
			// create the base exit shape, including the outside stroke
			Area exit_area_outside = new Area(
				new Rectangle2D.Float(mBodyXOffsetOrig+element_rectangle_width-mElementStyleOrig.mElementBorderWidth,
									  mBodyYOffsetOrig+name_rectangle_height+exit_rectangle_y_interval/3,
									  exit_rectangle_width,
									  exit_rectangle_height));
			// create the base exit shape, excluding the outside stroke
			Area exit_area_inside = new Area(
				new Rectangle2D.Float(mBodyXOffsetOrig+element_rectangle_width-mElementStyleOrig.mElementBorderWidth,
									  mBodyYOffsetOrig+name_rectangle_height+exit_rectangle_y_interval/3+mElementStyleOrig.mElementBorderWidth,
									  exit_rectangle_width-mElementStyleOrig.mElementBorderWidth,
									  exit_rectangle_height-mElementStyleOrig.mElementBorderWidth*2));
			// create the transformation that will translate the previous exit shape to make it suit for the next one
			AffineTransform next_exit_transform = AffineTransform.getTranslateInstance(0, exit_rectangle_y_interval);
			// process each exit, add its shapes to the body shapes and transform the exit shapes for the optional next exit
			
			Rectangle2D exit_area_outside_bounds = null;
			float hotspot_width = exits_width+
				mElementStyleOrig.mExitMarginWidth*2;
			if(hotspot_width < exit_rectangle_width)
			{
				hotspot_width = exit_rectangle_width;
			}
			float hotspot_x = mWidthOrig-
				mElementStyleOrig.mElementBorderWidth*2-
				hotspot_width;
			for (ElementProperty exit : getExits())
			{
				body_area_outside.add(exit_area_outside);
				body_area_inside.add(exit_area_inside);
				exit_area_outside_bounds = exit_area_outside.getBounds2D();
				exit.setHotSpot(new Rectangle2D.Float(hotspot_x, (float)exit_area_outside_bounds.getY(),
													  hotspot_width, (float)exit_area_outside_bounds.getHeight()));
				exit_area_outside = exit_area_outside.createTransformedArea(next_exit_transform);
				exit_area_inside = exit_area_inside.createTransformedArea(next_exit_transform);
			}
		}

		// create the bounding area by stroking the body area
		mBoundingOrig = new Area((new BasicStroke(1)).createStrokedShape(body_area_outside));
		mBoundingOrig.add(body_area_outside);

		// store the body areas and create the outside shape so that it only contains what is needed to draw the stroke
		mBodyAreaInsideOrig = body_area_inside;
		mBodyAreaOutsideOrig = (Area)body_area_outside.clone();
		mBodyAreaOutsideOrig.subtract(mBodyAreaInsideOrig);

		//
		// precalculate exit name texts
		//
		if(number_of_exits > 0)
		{
			// calculate the base location for the first exit text
			float base_exit_text_x_offset =
				mBodyXOffsetOrig+element_rectangle_width+exit_rectangle_width-mElementStyleOrig.mExitMarginWidth*2-mElementStyleOrig.mElementBorderWidth;
			float base_exit_text_y_offset =
				mBodyYOffsetOrig+name_rectangle_height+exit_rectangle_y_interval/3+
				mElementStyleOrig.mElementBorderWidth+mElementStyleOrig.mExitMarginHeight+exit_text_height;
			// iterate over all the exit texts
			for (ElementProperty exit : getExits())
			{
				// store each exit text with it's precalculated location in a dedicated glyph vector
				string_bounds = mElementStyleOrig.mExitFont.getStringBounds(exit.getName(), mElementStyleOrig.mFontRenderContext);
				exit.setNameBounds(new Rectangle2D.Float((float)(base_exit_text_x_offset-string_bounds.getWidth()),
														 (float)(base_exit_text_y_offset-string_bounds.getHeight()),
														 (float)string_bounds.getWidth(), (float)string_bounds.getHeight()));

				// move the vertical position downwards
				base_exit_text_y_offset += exit_rectangle_y_interval;
			}
		}

		//
		// precalculate title shape
		//
		mTitleRectangleOutsideOrig = new Area(new Rectangle2D.Float(mBodyXOffsetOrig+name_rectangle_x_offset, mBodyYOffsetOrig,
																	name_rectangle_width,
																	name_rectangle_height));
		mTitleRectangleInsideOrig = new Area(new Rectangle2D.Float(mBodyXOffsetOrig+name_rectangle_x_offset+mElementStyleOrig.mElementBorderWidth,
																   mBodyYOffsetOrig+mElementStyleOrig.mElementBorderWidth,
																   name_rectangle_width-mElementStyleOrig.mElementBorderWidth*2,
																   name_rectangle_height-mElementStyleOrig.mElementBorderWidth*2));
		mBodyAreaOutsideOrig.add(mTitleRectangleOutsideOrig);
		mBodyAreaOutsideOrig.subtract(mTitleRectangleInsideOrig);
		mBodyAreaInsideOrig.subtract(mTitleRectangleOutsideOrig);

		//
		// precalculate consumed parameters and texts
		//

		// set the initial horizontal offset for the drawing of the parameter handles, this is both used by all parameters
		float parameter_x_cursor = mBodyXOffsetOrig+(parameter_rectangle_width/2);
		if(number_of_consumed_parameters > 0)
		{
			// move the initial parameter line offset to the position for consumed parameters
			parameter_x_cursor += parameter_rectangle_width;
			// calculate the vertical start and end positions of the top parameter handles
			float consumed_parameter_handle_top_y1 = mBodyYOffsetOrig-(mElementStyleOrig.mParamLineLength/2);
			float consumed_parameter_handle_top_y2 = mBodyYOffsetOrig+(mElementStyleOrig.mParamLineLength/2);
			// create the first parameter handle area
			Area consumed_parameter_handle_top_area = new Area(new Rectangle2D.Float(parameter_x_cursor-(mElementStyleOrig.mParamLineWidth/2),
																					 consumed_parameter_handle_top_y1,
																					 mElementStyleOrig.mParamLineWidth, mElementStyleOrig.mParamLineLength));

			// calculate the horizontal interval between the parameters and create the transform object to perform the translation
			float consumed_parameter_x_interval = parameter_rectangle_width+(parameter_rectangle_width/2);
			AffineTransform next_consumed_parameter_transform = AffineTransform.getTranslateInstance(consumed_parameter_x_interval, 0);

			// process all the consumed parameters
			for (ElementProperty parameter : getConsumedParameters())
			{
				// add the consumed parameter handle to the body area and bounding area
				mBodyAreaOutsideOrig.add(consumed_parameter_handle_top_area);
				mBoundingOrig.add(consumed_parameter_handle_top_area);

				// add the consumed parameter texts together with their locations as glyph vectors to the collection of parameter glyph vectors
				string_bounds = mElementStyleOrig.mParamFont.getStringBounds(parameter.getName(), mElementStyleOrig.mFontRenderContext);
				name_bounds = new Rectangle2D.Float((float)(parameter_x_cursor+parameter_text_middle-string_bounds.getHeight()),
													consumed_parameter_handle_top_y2+mElementStyleOrig.mParamMarginHeight,
													(float)string_bounds.getHeight(), (float)string_bounds.getWidth());
				parameter.setNameBounds(name_bounds);
				hotspot = new Area(new Rectangle2D.Float((float)name_bounds.getX(), consumed_parameter_handle_top_y1, (float)name_bounds.getWidth(), (float)(name_bounds.getY()+name_bounds.getHeight())));
				hotspot.intersect(mBoundingOrig);
				parameter.setHotSpot(hotspot);

				// update the horizontal offset and translate the parameter handle area
				parameter_x_cursor += consumed_parameter_x_interval;
				consumed_parameter_handle_top_area = consumed_parameter_handle_top_area.createTransformedArea(next_consumed_parameter_transform);
			}
		}
		else
		{
			// adapt the initial horizontal offset in case there are no consumed parameters to make the used parameters appear
			// on the correct location
			parameter_x_cursor += parameter_rectangle_width/2;
		}

		//
		// precalculate used parameters lines and texts
		//
		mUsedParameterLinesOrig = new GeneralPath();
		if(number_of_used_parameters > 0)
		{
			// move the initial parameter line offset to the position for used parameters
			parameter_x_cursor += parameter_rectangle_width/2;
			// calculate the vertical start and end positions of the top and bottom parameter handles
			float used_parameter_handle_top_y1 = mBodyYOffsetOrig-(mElementStyleOrig.mParamLineLength/2);
			float used_parameter_handle_top_y2 = mBodyYOffsetOrig+(mElementStyleOrig.mParamLineLength/2);
			float used_parameter_handle_bottom_y1 = used_parameter_handle_top_y1+body_rectangle_height;
			float used_parameter_handle_bottom_y2 = used_parameter_handle_top_y2+body_rectangle_height;
			// create the first parameter top and bottom handle areas with their connection line
			Area used_parameter_handle_top_area = new Area(new Rectangle2D.Float(parameter_x_cursor-(mElementStyleOrig.mParamLineWidth/2),
																				 used_parameter_handle_top_y1,
																				 mElementStyleOrig.mParamLineWidth, mElementStyleOrig.mParamLineLength));
			Area used_parameter_handle_bottom_area = new Area(new Rectangle2D.Float(parameter_x_cursor-(mElementStyleOrig.mParamLineWidth/2),
																					used_parameter_handle_bottom_y1,
																					mElementStyleOrig.mParamLineWidth, mElementStyleOrig.mParamLineLength));
			Line2D used_parameter_handle_connect = new Line2D.Float(parameter_x_cursor, used_parameter_handle_top_y2,
																	parameter_x_cursor, used_parameter_handle_bottom_y1);

			// calculate the horizontal interval between the parameters and create the transform object to perform the translation
			float used_parameter_x_interval = parameter_rectangle_width+(parameter_rectangle_width/2);
			AffineTransform next_used_parameter_transform = AffineTransform.getTranslateInstance(used_parameter_x_interval, 0);

			// process all the used parameters
			for (ElementProperty parameter : getUsedParameters())
			{
				// add the used parameter top and bottom handles to the body area and bounding area
				mBodyAreaOutsideOrig.add(used_parameter_handle_top_area);
				mBodyAreaOutsideOrig.add(used_parameter_handle_bottom_area);
				mBoundingOrig.add(used_parameter_handle_top_area);
				mBoundingOrig.add(used_parameter_handle_bottom_area);

				// add the connection line between handles to the collection of used parameter connection lines
				mUsedParameterLinesOrig.moveTo(parameter_x_cursor, used_parameter_handle_top_y2);
				mUsedParameterLinesOrig.lineTo(parameter_x_cursor, used_parameter_handle_bottom_y1);

				// add the used parameter texts together with their locations as glyph vectors to the collection of parameter glyph vectors
				string_bounds = mElementStyleOrig.mParamFont.getStringBounds(parameter.getName(), mElementStyleOrig.mFontRenderContext);
				name_bounds = new Rectangle2D.Float((float)(parameter_x_cursor-mElementStyleOrig.mParamMarginWidth-string_bounds.getHeight()),
													used_parameter_handle_top_y2+mElementStyleOrig.mParamMarginHeight,
													(float)string_bounds.getHeight(), (float)string_bounds.getWidth());
				parameter.setNameBounds(name_bounds);
				hotspot = new Area(new Rectangle2D.Float((float)name_bounds.getX(), used_parameter_handle_top_y1, (float)(parameter_x_cursor+mElementStyleOrig.mParamLineWidth-name_bounds.getX()), used_parameter_handle_bottom_y2-used_parameter_handle_top_y1));
				hotspot.intersect(mBoundingOrig);
				parameter.setHotSpot(hotspot);

				// update the horizontal offset and translate the parameter top and bottom handle areas and their connection line
				parameter_x_cursor += used_parameter_x_interval;
				used_parameter_handle_top_area = used_parameter_handle_top_area.createTransformedArea(next_used_parameter_transform);
				used_parameter_handle_bottom_area = used_parameter_handle_bottom_area.createTransformedArea(next_used_parameter_transform);
				used_parameter_handle_connect.setLine(parameter_x_cursor, used_parameter_handle_top_y2,
													parameter_x_cursor, used_parameter_handle_bottom_y1);
			}
		}

		//
		// precalculate added parameters lines and texts
		//
		if(number_of_added_parameters > 0)
		{
			// move the initial parameter line offset to the position for added parameters
			parameter_x_cursor += parameter_rectangle_width/2;
			// calculate the vertical start and end positions of the bottom parameter handles
			float added_parameter_line_y1 = mBodyYOffsetOrig+body_rectangle_height-(mElementStyleOrig.mParamLineLength/2);
			float added_parameter_line_y2 = mBodyYOffsetOrig+body_rectangle_height+(mElementStyleOrig.mParamLineLength/2);
			// create the first parameter handle area
			Area added_parameter_line_area = new Area(new Rectangle2D.Float(parameter_x_cursor-(mElementStyleOrig.mParamLineWidth/2),
																			added_parameter_line_y1,
																			mElementStyleOrig.mParamLineWidth, mElementStyleOrig.mParamLineLength));

			// calculate the horizontal interval between the parameters and create the transform object to perform the translation
			float added_parameter_x_interval = parameter_rectangle_width+(parameter_rectangle_width/2);
			AffineTransform next_added_parameter_transform = AffineTransform.getTranslateInstance(added_parameter_x_interval, 0);

			// process all the added parameters
			for (ElementProperty parameter : getAddedParameters())
			{
				// add the added parameter handle to the body area and bounding area
				mBodyAreaOutsideOrig.add(added_parameter_line_area);
				mBoundingOrig.add(added_parameter_line_area);

				// add the added parameter texts together with their locations as glyph vectors to the collection of parameter glyph vectors
				string_bounds = mElementStyleOrig.mParamFont.getStringBounds(parameter.getName(), mElementStyleOrig.mFontRenderContext);
				name_bounds = new Rectangle2D.Float((float)(parameter_x_cursor+parameter_text_middle-string_bounds.getHeight()),
													(float)(added_parameter_line_y1-mElementStyleOrig.mParamMarginHeight-string_bounds.getWidth()),
													(float)string_bounds.getHeight(), (float)string_bounds.getWidth());
				parameter.setNameBounds(name_bounds);
				hotspot = new Area(new Rectangle2D.Float((float)name_bounds.getX(), (float)name_bounds.getY(), (float)name_bounds.getWidth(), (float)(added_parameter_line_y2-name_bounds.getY())));
				hotspot.intersect(mBoundingOrig);
				parameter.setHotSpot(hotspot);

				// update the horizontal offset and translate the parameter handle area
				parameter_x_cursor += added_parameter_x_interval;
				added_parameter_line_area = added_parameter_line_area.createTransformedArea(next_added_parameter_transform);
			}
		}

		//
		// precalculate name text
		//
		ElementProperty title = getTitle();
		title.setNameBounds(new Rectangle2D.Float(mBodyXOffsetOrig+name_rectangle_x_offset+(name_rectangle_width-title_text_width)/2,
												  mBodyYOffsetOrig+mElementStyleOrig.mTitleMarginHeight+mElementStyleOrig.mElementBorderWidth,
												  title_text_width, title_text_height));
		title.setHotSpot(mTitleRectangleOutsideOrig);

		//
		// create the scaled version, keeping the current scale factor
		//
		scalePrecalculatedAreas(1);
	}

	public void drawElement(Graphics2D g2d)
	{
		Rectangle clip = g2d.getClipBounds();
		
		if(mBoundingScaled == null)
		{
			createPrecalculatedAreas();
		}

		if(clip == null ||
		   mBoundingScaled.intersects(clip))
		{
			boolean drag_outline = Config.getRepInstance().getBool("DRAG_OUTLINE");
			boolean scrolling_fast = Config.getRepInstance().getBool("SCROLLING_FAST");
			boolean scrolling_outline = Config.getRepInstance().getBool("SCROLLING_OUTLINE");
			boolean scroll_active = ((StructurePanel)getParent()).isScrollActive();

			if((!mDragActive && !scroll_active) ||
			   (mDragActive && !drag_outline) ||
			   (scroll_active && !scrolling_outline))
			{
				if(!mSelected)
				{
					g2d.setColor(mElementStyleScaled.mBodyBackgroundColor);
				}
				else
				{
					g2d.setColor(mElementStyleScaled.mBodyBackgroundColorSelected);
				}
				g2d.fill(mBodyAreaInsideScaled);
			}
			g2d.setColor(mElementStyleScaled.mElementBorderColor);
			g2d.fill(mBodyAreaOutsideScaled);

			if((!mDragActive && !scroll_active) ||
			   (mDragActive && !drag_outline) ||
			   (scroll_active && !scrolling_outline))
			{
				if(!scrolling_fast || !scroll_active)
				{
					g2d.setColor(mElementStyleScaled.mElementBorderColor);
					g2d.setStroke(mElementStyleScaled.mParamLineDashedStroke);
					g2d.draw(mUsedParameterLinesScaled);
				}
				if(clip == null ||
				   mTitleRectangleInsideScaled.intersects(clip))
				{
					g2d.setColor(mTitleBackgroundColor);
					g2d.fill(mTitleRectangleInsideScaled);
				}
				getTitle().draw(g2d);
				if(!scrolling_fast || !scroll_active)
				{
					for (ElementProperty property : mElementProperties)
					{
						if (!(property instanceof ElementPropertyTitle))
						{
							property.draw(g2d);
						}
					}
				}

				mStructurePanel.drawHighlightedProperty(this, g2d);
			}
		}
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D)g;

		if(mDragBufferedImage == null)
		{
			g2d	= (Graphics2D)g2d.create();
			ElementStyle.setRenderingHints(g2d, mScaleFactor);
			drawElement(g2d);
		}
		else
		{
			if(mIsTransparent)
			{
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			}

			g2d.drawImage(mDragBufferedImage, null, 0, 0);
		}
	}

	public BufferedImage getDragBufferedImage()
	{
		return mDragBufferedImage;
	}

	public void addElementListener(ElementListener listener)
	{
        mElementListeners.add(listener);
	}

	public void removeElementListener(ElementListener listener)
	{
        mElementListeners.remove(listener);
	}

	protected void fireElementRepositioned()
	{
		for (ElementListener listener : mElementListeners)
		{
			listener.elementRepositioned(this);
		}
	}

	protected void fireElementRaised()
	{
		for (ElementListener listener : mElementListeners)
		{
			listener.elementRaised(this);
		}
	}

	protected void fireElementSelected(int modifiers)
	{
		for (ElementListener listener : mElementListeners)
		{
			listener.elementSelected(this, modifiers);
		}
	}

	protected void fireElementDeselected(int modifiers)
	{
		for (ElementListener listener : mElementListeners)
		{
			listener.elementDeselected(this, modifiers);
		}
	}

	protected void fireElementDragged(int x, int y)
	{
		for (ElementListener listener : mElementListeners)
		{
			listener.elementDragged(this, x, y);
		}
	}

	protected void fireElementDragStart(Point dragStartPoint)
	{
		for (ElementListener listener : mElementListeners)
		{
			listener.elementDragStart(this, dragStartPoint);
		}
	}

	protected void fireElementDragEnd()
	{
		for (ElementListener listener : mElementListeners)
		{
			listener.elementDragEnd();
		}
	}

	protected void fireElementPropertyHighlighted(ElementProperty property)
	{
		for (ElementListener listener : mElementListeners)
		{
			listener.elementPropertyHighlighted(property);
		}
	}

	protected void repositionElementDuringDrag(int offsetX, int offsetY)
	{
		setLocation(offsetX+getX(), offsetY+getY());
	}

	protected void startDrag()
	{
		mDragActive = true;
		mDragBufferedImage = new BufferedImage((int)mWidthScaled, (int)mHeightScaled, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = mDragBufferedImage.createGraphics();
		ElementStyle.setRenderingHints(g2d, mScaleFactor);
		drawElement(g2d);
		fireElementRaised();
		if(Config.getRepInstance().getBool("DRAG_TRANSPARENT"))
		{
			mIsTransparent = true;
		}
	}

	protected void endDrag()
	{
		mDragBufferedImage = null;
		mDragActive = false;
		if(Config.getRepInstance().getBool("DRAG_TRANSPARENT"))
		{
			mIsTransparent = false;
		}
		fireElementRepositioned();
	}

	private ElementProperty locateElementProperty(Point location)
	{
		Shape hotspot = null;
		for (ElementProperty property : mElementProperties)
		{
			hotspot = property.getHotSpot();
			if(hotspot.contains(location))
			{
				return property;
			}
		}

		return null;
	}

	private void triggerPopup(Point location)
	{
		fireElementRaised();
		ElementProperty property = locateElementProperty(location);
		if(property != null)
		{
			property.showPopupMenu(location);
		}
		else
		{
			JPopupMenu popup = new JPopupMenu();
			DynamicMenuBuilder menu_builder = new DynamicMenuBuilder();
			JMenu menu_add = menu_builder.addMenu(popup, Localization.getString("rife.element.popupmenu.add"), Localization.getChar("rife.element.popupmenu.add.mnemonic"));
			menu_builder.addMenuItem(menu_add, Localization.getString("rife.element.popupmenu.add.exit"), new AddExit(location), Localization.getChar("rife.element.popupmenu.add.exit.mnemonic"));
			menu_builder.addMenuItem(menu_add, Localization.getString("rife.element.popupmenu.add.consumedparameter"), new AddConsumedParameter(location), Localization.getChar("rife.element.popupmenu.add.consumedparameter.mnemonic"));
			menu_builder.addMenuItem(menu_add, Localization.getString("rife.element.popupmenu.add.usedparameter"), new AddUsedParameter(location), Localization.getChar("rife.element.popupmenu.add.usedparameter.mnemonic"));
			menu_builder.addMenuItem(menu_add, Localization.getString("rife.element.popupmenu.add.addedparameter"), new AddAddedParameter(location), Localization.getChar("rife.element.popupmenu.add.addedparameter.mnemonic"));
			menu_builder.addMenuItem(popup, Localization.getString("rife.element.popupmenu.delete"), new Delete(), Localization.getChar("rife.element.popupmenu.delete.mnemonic"));
			
			popup.show(this, location.x, location.y);
			popup.addPopupMenuListener(getStructurePanel());
		}
	}

	protected class AddExit implements DynamicMenuAction
	{
		private Point	mClickedPoint = null;

		public AddExit(Point clickedPoint)
		{
			mClickedPoint = clickedPoint;
		}

		public void execute(JMenuItem menuItem)
		{
			ElementPropertyExit exit = addExit("");
			createPrecalculatedAreas();
			getStructurePanel().editElementProperty(exit, mClickedPoint);
		}
	}

	protected class AddConsumedParameter implements DynamicMenuAction
	{
		private Point	mClickedPoint = null;

		public AddConsumedParameter(Point clickedPoint)
		{
			mClickedPoint = clickedPoint;
		}

		public void execute(JMenuItem menuItem)
		{
			ElementPropertyParameterConsumed parameter = addConsumedParameter("");
			createPrecalculatedAreas();
			getStructurePanel().editElementProperty(parameter, mClickedPoint);
		}
	}

	protected class AddUsedParameter implements DynamicMenuAction
	{
		private Point	mClickedPoint = null;

		public AddUsedParameter(Point clickedPoint)
		{
			mClickedPoint = clickedPoint;
		}

		public void execute(JMenuItem menuItem)
		{
			ElementPropertyParameterUsed parameter = addUsedParameter("");
			createPrecalculatedAreas();
			getStructurePanel().editElementProperty(parameter, mClickedPoint);
		}
	}

	protected class AddAddedParameter implements DynamicMenuAction
	{
		private Point	mClickedPoint = null;

		public AddAddedParameter(Point clickedPoint)
		{
			mClickedPoint = clickedPoint;
		}

		public void execute(JMenuItem menuItem)
		{
			ElementPropertyParameterAdded parameter = addAddedParameter("");
			createPrecalculatedAreas();
			getStructurePanel().editElementProperty(parameter, mClickedPoint);
		}
	}

	protected class Delete implements DynamicMenuAction
	{
		public void execute(JMenuItem menuItem)
		{
			JComponent parent = (JComponent)Element.this.getParent();
			if(parent instanceof StructurePanel)
			{
				((StructurePanel)parent).removeElement(Element.this);
			}
		}
	}

	private void updateHighlightedProperty(Point location)
	{
		ElementProperty property = locateElementProperty(location);
		fireElementPropertyHighlighted(property);
	}

	public void mouseClicked(MouseEvent e)
	{
		if((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
		{
			if(e.getClickCount() == 2)
			{
				ElementProperty property = locateElementProperty(e.getPoint());
				if(property != null)
				{
					getStructurePanel().editElementProperty(property, e.getPoint());
				}
			}
		}
	}

	public void mousePressed(MouseEvent e)
	{
		getStructurePanel().removeElementPropertyEditor();

		if(e.isPopupTrigger())
		{
			triggerPopup(e.getPoint());
		}
		else if((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
		{
			mDragStartPoint = e.getPoint();
			if(!mSelected)
			{
				fireElementSelected(e.getModifiers());
			}
			else
			{
				fireElementDeselected(e.getModifiers());
			}
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		if(e.isPopupTrigger())
		{
			triggerPopup(e.getPoint());
		}
		else if((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
		{
			mDragStartPoint = null;
			if(mDragActive)
			{
				fireElementDragEnd();
			}
		}
	}

	public void mouseEntered(MouseEvent e)
	{
		updateHighlightedProperty(e.getPoint());
	}

	public void mouseExited(MouseEvent e)
	{
		if(!mStructurePanel.isDragActive() &&
		   !mStructurePanel.isElementPropertyBeingEdited() &&
		   !mStructurePanel.isPopupMenuActive())
		{
			updateHighlightedProperty(new Point(-1, -1));
		}
		if(mDragActive)
		{
			fireElementDragged(e.getPoint().x,  e.getPoint().y);
		}
	}

	public void mouseDragged(MouseEvent e)
	{
		if(e.getComponent() == this)
		{
			if((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
			{
				if(mSelected)
				{
					if(!mDragActive)
					{
						fireElementDragStart(mDragStartPoint);
					}
					else
					{
						fireElementDragged(e.getPoint().x,  e.getPoint().y);
					}
				}
			}
		}
	}

	public void mouseMoved(MouseEvent e)
	{
		updateHighlightedProperty(e.getPoint());
	}
}
