/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ConstrainedBeanImpl.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

public class ConstrainedBeanImpl extends Validation
{
	public enum Question {a1, a2, a3}
	
	private String		mHidden = null;
	private String		mAnotherhidden = null;
	private String		mLogin = null;
	private String		mAnotherlogin = null;
	private String		mPassword = null;
	private String		mAnotherpassword = null;
	private String		mComment = null;
	private String		mAnothercomment = null;
	private Question	mQuestion = null;
	private String		mAnotherquestion = null;
	private String		mCustomquestion = null;
	private String		mAnothercustomquestion = null;
	private int[]		mOptions = null;
	private int[]		mOtheroptions = null;
	private int[]		mCustomoptions = null;
	private int[]		mOthercustomoptions = null;
	private boolean		mInvoice = false;
	private boolean		mOnemoreinvoice = true;
	private String[]	mColors = null;
	private String[]	mMorecolors = null;
	private String[]	mYourcolors = null;
	
	public ConstrainedBeanImpl()
	{
		addConstraint(new ConstrainedProperty("hidden").maxLength(16).notNull(true));
		addConstraint(new ConstrainedProperty("anotherhidden").maxLength(20).notNull(true).defaultValue("weg").editable(false));
		addConstraint(new ConstrainedProperty("login").maxLength(6).notNull(true));
		addConstraint(new ConstrainedProperty("anotherlogin").maxLength(10).notNull(true).defaultValue("jij").editable(false));
		addConstraint(new ConstrainedProperty("password").maxLength(8).notNull(true));
		addConstraint(new ConstrainedProperty("anotherpassword").maxLength(12).notNull(true).defaultValue("secrettoo").editable(false));
		addConstraint(new ConstrainedProperty("comment").editable(false));
		addConstraint(new ConstrainedProperty("anothercomment").defaultValue("the comment"));
		addConstraint(new ConstrainedProperty("question").notNull(true));
		addConstraint(new ConstrainedProperty("anotherquestion").inList("a1", "a3", "a2").defaultValue("a1").editable(false));
		addConstraint(new ConstrainedProperty("customquestion").notNull(true).inList("a1", "a2"));
		addConstraint(new ConstrainedProperty("anothercustomquestion").notNull(true).inList("a2", "a3").defaultValue("a3"));
		addConstraint(new ConstrainedProperty("options").notNull(true).inList("0", "2", "3"));
		addConstraint(new ConstrainedProperty("otheroptions").notNull(true).inList("0", "2").defaultValue("0").editable(false));
		addConstraint(new ConstrainedProperty("customoptions").inList("1", "2"));
		addConstraint(new ConstrainedProperty("othercustomoptions").notNull(true).inList("0", "2", "1").defaultValue("0"));
		addConstraint(new ConstrainedProperty("invoice").editable(false));
		addConstraint(new ConstrainedProperty("onemoreinvoice").defaultValue(true));
		addConstraint(new ConstrainedProperty("colors").notNull(true).inList("red", "blue", "green"));
		addConstraint(new ConstrainedProperty("morecolors").notNull(true).inList("blue", "red", "black", "green").defaultValue("green").editable(false));
		addConstraint(new ConstrainedProperty("yourcolors").notNull(true).inList("purple", "yellow", "brown").defaultValue("orange"));
	}
	
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
	
	public void setQuestion(Question question)
	{
		mQuestion = question;
	}
	
	public Question getQuestion()
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
	
	public void setColors(String[] colors)
	{
		mColors = colors;
	}
	
	public String[] getColors()
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

