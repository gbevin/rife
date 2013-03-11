/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementStyle.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.old;

import java.awt.*;

import com.uwyn.rife.config.Config;
import com.uwyn.rife.gui.Rife;
import com.uwyn.rife.swing.JDialogSystemError;
import com.uwyn.rife.tools.ExceptionUtils;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.RasterFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class ElementStyle
{
	public static final String	MAX_PROPERTY_STRING = "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM";
	public static final int		MAX_PROPERTY_STRING_LENGTH = MAX_PROPERTY_STRING.length();

	private BufferedImage		mParamImage = null;
	private BufferedImageOp		mParamImageOp = null;
	private Graphics2D			mParamGraphics = null;

	private	Font				mPlainFont = null;
	private	Font				mBoldFont = null;

	public FontRenderContext	mFontRenderContext = null;

	public Color				mBodyBackgroundColor = null;
	public Color				mBodyBackgroundColorSelected = null;
	public Color				mElementBorderColor = null;
	public Color				mElementBorderColorSelected = null;
	public Color				mTitleBorderColor = null;
	public Color				mTitleTextColor = null;
	public Color				mExitTextColor = null;
	public Color				mParamBorderColor = null;
	public Color				mParamTextColor = null;
	public Font					mTitleFont = null;
	public LineMetrics			mTitleFontLineMetrics = null;
	public Font					mParamFont = null;
	public LineMetrics			mParamFontLineMetrics = null;
	public Font					mExitFont = null;
	public LineMetrics			mExitFontLineMetrics = null;
	public float				mElementBorderWidth = 0;
	public float				mTitleMarginWidth = 0;
	public float				mTitleMarginHeight = 0;
	public float				mExitMarginWidth = 0;
	public float				mExitMarginHeight = 0;
	public float				mParamMarginWidth = 0;
	public float				mParamMarginHeight = 0;
	public float				mParamLineWidth = 0;
	public float				mParamLineLength = 0;
	public float				mParamLineDashedWidth = 0;
	public BasicStroke			mParamLineDashedStroke = null;

	public ElementStyle(float scaleFactor)
	{
		getBaseFonts();
		setColors();
		calculateStyle(scaleFactor);
	}

	public static void setRenderingHints(Graphics2D g2d, float scaleFactor)
	{
		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
		if(scaleFactor < 1)
		{
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
		else
		{
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		}
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
	}

	private void getBaseFonts()
	{
		mPlainFont = loadFont("/fonts/trebuchet.ttf");
		mBoldFont = loadFont("/fonts/trebuchetbold.ttf");
	}

	private Font loadFont(String path)
	{
		InputStream			input_stream = null;
		InputStreamReader	input_stream_reader = null;
		Font 				font = null;

		input_stream = this.getClass().getResourceAsStream(path);

		if(input_stream != null)
		{
			try
			{
				input_stream_reader = new InputStreamReader(input_stream, "ISO8859_1");

				if(input_stream_reader != null)
				{
					try
					{
						font = Font.createFont(Font.TRUETYPE_FONT, input_stream);
					}
					catch (IOException e)
					{
						(new JDialogSystemError(Rife.getMainFrame(), "ElementStyle.loadFont() : IO error while creating the font from resource at '"+path+"' : "+ExceptionUtils.getExceptionStackTrace(e))).setVisible(true);
					}
					catch (FontFormatException e)
					{
						(new JDialogSystemError(Rife.getMainFrame(), "ElementStyle.loadFont() : Font format error while creating the font from resource at '"+path+"' : "+ExceptionUtils.getExceptionStackTrace(e))).setVisible(true);
					}
				}
				else
				{
					(new JDialogSystemError(Rife.getMainFrame(), "ElementStyle.loadFont() : Couldn't create the inputstream reader for the font resource at '"+path+"'.")).setVisible(true);
				}
			}
			catch (UnsupportedEncodingException e)
			{
				(new JDialogSystemError(Rife.getMainFrame(), "ElementStyle.loadFont() : Error while creating the inputstream reader for the font resource at '"+path+"' : "+ExceptionUtils.getExceptionStackTrace(e))).setVisible(true);
			}
		}
		else
		{
			(new JDialogSystemError(Rife.getMainFrame(), "ElementStyle.loadFont() : Couldn't open the font resource at '"+path+"'.")).setVisible(true);
		}

		return font;
	}

	public void drawParameterText(Graphics2D g2d, String text, Color backgroundColor, int x, int y)
	{
		mParamGraphics.setBackground(backgroundColor);
		Rectangle2D text_size = mParamFont.getStringBounds(text, mFontRenderContext);
		int baseline = (int)(mParamFontLineMetrics.getAscent());
		mParamGraphics.setClip(0, 0, (int)text_size.getWidth(), mParamImage.getHeight());
		mParamGraphics.clearRect(0, 0, (int)text_size.getWidth(), mParamImage.getHeight());
		mParamGraphics.drawString(text, 0, baseline);
		Shape previous_clip = g2d.getClip();
		g2d.clipRect(x-baseline, y-(int)text_size.getWidth(), x-baseline+mParamImage.getHeight(), (int)text_size.getWidth());
		try
		{
			g2d.drawImage(mParamImage, mParamImageOp, x, y);
		}
		catch (RasterFormatException e)
		{
			// do nothing
		}
		g2d.setClip(previous_clip);
	}

	private void setColors()
	{
		mBodyBackgroundColor = new Color(220, 255, 200);
		mBodyBackgroundColorSelected = new Color(255, 255, 200);
 		mElementBorderColor = new Color(0, 0, 0);
 		mElementBorderColorSelected = new Color(0, 0, 0);
		mTitleTextColor = new Color(0, 0, 0);
		mTitleBorderColor = new Color(0, 0, 0);
		mExitTextColor = new Color(0, 0, 0);
		mParamTextColor = new Color(0, 0, 0);
		mParamBorderColor = new Color(0, 0, 0);
	}

	public void calculateStyle(float scaleFactor)
	{
		mFontRenderContext = new FontRenderContext(null, false, false);

		mTitleFont = mBoldFont.deriveFont(Font.PLAIN, (int)(13*scaleFactor));
		mTitleFontLineMetrics = mTitleFont.getLineMetrics(MAX_PROPERTY_STRING, mFontRenderContext);
		mParamFont = mPlainFont.deriveFont(Font.PLAIN, (int)(11*scaleFactor));
		mParamFontLineMetrics = mParamFont.getLineMetrics(MAX_PROPERTY_STRING, mFontRenderContext);
		mExitFont = mPlainFont.deriveFont(Font.PLAIN, (int)(11*scaleFactor));
		mExitFontLineMetrics = mExitFont.getLineMetrics(MAX_PROPERTY_STRING, mFontRenderContext);
		mElementBorderWidth = Math.round(scaleFactor);
		mTitleMarginWidth = Math.round(5*scaleFactor);
		mTitleMarginHeight = Math.round(3*scaleFactor);
		mExitMarginWidth = Math.round(2*scaleFactor);
		mExitMarginHeight = Math.round(2*scaleFactor);

		mParamMarginWidth = Math.round(scaleFactor);
		mParamMarginHeight = Math.round(5*scaleFactor);
		mParamLineWidth = Math.round(3*scaleFactor);
		mParamLineLength = Math.round(Config.getRepInstance().getInt("GRID_SIZE")*2*scaleFactor);
		mParamLineDashedWidth = Math.round(scaleFactor);
		mParamLineDashedStroke = new BasicStroke(mParamLineDashedWidth, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0f, new float[] {3f*scaleFactor}, 0f);

		Rectangle2D work_image_size = mParamFont.getStringBounds(MAX_PROPERTY_STRING, mFontRenderContext);
		mParamImage = new BufferedImage((int)work_image_size.getWidth(), (int)work_image_size.getHeight(), BufferedImage.TYPE_INT_RGB);
		mParamGraphics = mParamImage.createGraphics();
		setRenderingHints(mParamGraphics, scaleFactor);
		mParamGraphics.setColor(mParamTextColor);
		mParamGraphics.setFont(mParamFont);
		AffineTransform image_op_transform = AffineTransform.getRotateInstance(Math.toRadians(-90));
		image_op_transform.concatenate(AffineTransform.getTranslateInstance(0, -1*(mParamFontLineMetrics.getAscent())));
		mParamImageOp = new AffineTransformOp(image_op_transform, mParamGraphics.getRenderingHints());
	}
}
