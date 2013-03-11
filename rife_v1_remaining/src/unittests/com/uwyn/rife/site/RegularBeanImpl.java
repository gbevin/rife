/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RegularBeanImpl.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

public class RegularBeanImpl
{
	public enum Color {red, blue, green}
	
	private String		mHidden = null;
	private String		mAnotherhidden = null;
	private String		mLogin = null;
	private String		mAnotherlogin = null;
	private String		mPassword = null;
	private String		mAnotherpassword = null;
	private String		mComment = null;
	private String		mAnothercomment = null;
	private String		mQuestion = null;
	private String		mAnotherquestion = null;
	private String		mCustomquestion = null;
	private String		mAnothercustomquestion = null;
	private int[]		mOptions = null;
	private int[]		mOtheroptions = null;
	private int[]		mCustomoptions = null;
	private int[]		mOthercustomoptions = null;
	private boolean		mInvoice = false;
	private boolean		mOnemoreinvoice = false;
	private Color[]		mColors = null;
	private String[]	mMorecolors = null;
	private String[]	mYourcolors = null;
	
	public void setHidden(String hidden)
	{
		mHidden = hidden;
	}
	
	public String getHidden()
	{
		return mHidden;
	}
	
	public void setAnotherhidden(String anotherhidden)
	{
		mAnotherhidden = anotherhidden;
	}
	
	public String getAnotherhidden()
	{
		return mAnotherhidden;
	}
	
	public void setLogin(String login)
	{
		mLogin = login;
	}
	
	public String getLogin()
	{
		return mLogin;
	}
	
	public void setAnotherlogin(String anotherlogin)
	{
		mAnotherlogin = anotherlogin;
	}
	
	public String getAnotherlogin()
	{
		return mAnotherlogin;
	}
	
	public void setPassword(String password)
	{
		mPassword = password;
	}
	
	public String getPassword()
	{
		return mPassword;
	}
	
	public void setAnotherpassword(String anotherpassword)
	{
		mAnotherpassword = anotherpassword;
	}
	
	public String getAnotherpassword()
	{
		return mAnotherpassword;
	}
	
	public void setComment(String comment)
	{
		mComment = comment;
	}
	
	public String getComment()
	{
		return mComment;
	}
	
	public void setAnothercomment(String anothercomment)
	{
		mAnothercomment = anothercomment;
	}
	
	public String getAnothercomment()
	{
		return mAnothercomment;
	}
	
	public void setQuestion(String question)
	{
		mQuestion = question;
	}
	
	public String getQuestion()
	{
		return mQuestion;
	}
	
	public void setAnotherquestion(String anotherquestion)
	{
		mAnotherquestion = anotherquestion;
	}
	
	public String getAnotherquestion()
	{
		return mAnotherquestion;
	}
	
	public void setCustomquestion(String customquestion)
	{
		mCustomquestion = customquestion;
	}
	
	public String getCustomquestion()
	{
		return mCustomquestion;
	}
	
	public void setAnothercustomquestion(String anothercustomquestion)
	{
		mAnothercustomquestion = anothercustomquestion;
	}
	
	public String getAnothercustomquestion()
	{
		return mAnothercustomquestion;
	}
	
	public void setOptions(int[] options)
	{
		mOptions = options;
	}
	
	public int[] getOptions()
	{
		return mOptions;
	}
	
	public void setOtheroptions(int[] otheroptions)
	{
		mOtheroptions = otheroptions;
	}
	
	public int[] getOtheroptions()
	{
		return mOtheroptions;
	}
	
	public void setCustomoptions(int[] customoptions)
	{
		mCustomoptions = customoptions;
	}
	
	public int[] getCustomoptions()
	{
		return mCustomoptions;
	}
	
	public void setOthercustomoptions(int[] othercustomoptions)
	{
		mOthercustomoptions = othercustomoptions;
	}
	
	public int[] getOthercustomoptions()
	{
		return mOthercustomoptions;
	}
	
	public void setInvoice(boolean invoice)
	{
		mInvoice = invoice;
	}
	
	public boolean isInvoice()
	{
		return mInvoice;
	}
	
	public void setOnemoreinvoice(boolean onemoreinvoice)
	{
		mOnemoreinvoice = onemoreinvoice;
	}
	
	public boolean isOnemoreinvoice()
	{
		return mOnemoreinvoice;
	}
	
	public void setColors(Color[] colors)
	{
		mColors = colors;
	}
	
	public Color[] getColors()
	{
		return mColors;
	}
	
	public void setMorecolors(String[] morecolors)
	{
		mMorecolors = morecolors;
	}
	
	public String[] getMorecolors()
	{
		return mMorecolors;
	}
	
	public void setYourcolors(String[] yourcolors)
	{
		mYourcolors = yourcolors;
	}
	
	public String[] getYourcolors()
	{
		return mYourcolors;
	}
}

