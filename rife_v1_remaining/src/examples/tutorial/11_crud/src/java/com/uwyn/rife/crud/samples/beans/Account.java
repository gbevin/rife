/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Account.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud.samples.beans;

import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.site.ConstrainedBean;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.MetaData;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Account extends MetaData
{
	private SimpleDateFormat mInputDateFormat = new SimpleDateFormat("dd/MM/yyyy");

	private int		mId = -1;
	private String	mLogin = null;
	private String	mEmail = null;
	private String	mFirstname = null;
	private String	mLastname = null;
	private String	mAddress1 = null;
	private String	mAddress2 = null;
	private String	mPostalcode = null;
	private String	mLocation = null;
	private String	mCountry = null;
	private String	mPhonenumber = null;
	private String	mFaxnumber = null;
	private String	mMobilenumber = null;
	private Date   	mBirthdate = null;
	private Date	mTimeRegistered = null;
	private byte[]	mAvatar = null;
	
	public void activateMetaData()
	{
		addConstraint(new ConstrainedBean()
			.defaultOrder("login"));

		addConstraint(new ConstrainedProperty("id")
			.notNull(true)
			.identifier(true)
			.editable(false));
		addConstraint(new ConstrainedProperty("login")
			.notNull(true)
			.minLength(RifeConfig.Authentication.getLoginMinimumLength())
			.maxLength(RifeConfig.Authentication.getLoginMaximumLength())
			.unique(true)
			.listed(true));
		addConstraint(new ConstrainedProperty("email")
			.notNull(true)
			.email(true)
			.maxLength(100)
			.unique(true)
			.listed(true));
		addConstraint(new ConstrainedProperty("firstname")
			.notNull(true)
			.maxLength(50)
			.listed(true));
		addConstraint(new ConstrainedProperty("lastname")
			.notNull(true)
			.maxLength(50)
			.listed(true));
		addConstraint(new ConstrainedProperty("address1")
			.notNull(true)
			.maxLength(150));
		addConstraint(new ConstrainedProperty("address2")
			.maxLength(150));
		addConstraint(new ConstrainedProperty("postalcode")
			.notNull(true)
			.maxLength(8));
		addConstraint(new ConstrainedProperty("location")
			.notNull(true)
			.maxLength(100));
		addConstraint(new ConstrainedProperty("country")
			.notNull(true)
			.maxLength(30));
		addConstraint(new ConstrainedProperty("phonenumber")
			.maxLength(30));
		addConstraint(new ConstrainedProperty("faxnumber")
			.maxLength(30));
		addConstraint(new ConstrainedProperty("mobilenumber")
			.maxLength(30));
		addConstraint(new ConstrainedProperty("birthdate")
			.maxLength(10)
			.format(mInputDateFormat));
		addConstraint(new ConstrainedProperty("timeRegistered")
			.editable(false));
		addConstraint(new ConstrainedProperty("avatar")
			.file(true)
			.mimeType(MimeType.IMAGE_PNG)
			.contentAttribute("width", 60));
	}
	
	public Account	id(int id)		{ setId(id); return this; }
	public void		setId(int id)	{ mId = id; }
	public int		getId()			{ return mId; }
	
	public Account	login(String login)		{ setLogin(login); return this; }
	public void		setLogin(String login)	{ mLogin = login; }
	public String	getLogin()				{ return mLogin; }
	
	public Account	email(String email)		{ setEmail(email); return this; }
	public void		setEmail(String email)	{ mEmail = email; }
	public String	getEmail()				{ return mEmail; }
	
	public Account	firstname(String name)		{ setFirstname(name); return this; }
	public void		setFirstname(String name)	{ mFirstname = name; }
	public String	getFirstname()				{ return mFirstname; }
	
	public Account	lastname(String lastname)		{ setLastname(lastname); return this; }
	public void		setLastname(String lastname)	{ mLastname = lastname; }
	public String	getLastname()					{ return mLastname; }
	
	public Account	address1(String address1)		{ setAddress1(address1); return this; }
	public void		setAddress1(String address1)	{ mAddress1 = address1; }
	public String	getAddress1()					{ return mAddress1; }
	
	public Account	address2(String address2)		{ setAddress2(address2); return this; }
	public void		setAddress2(String address2)	{ mAddress2 = address2; }
	public String	getAddress2()					{ return mAddress2; }
	
	public Account	postalcode(String postalcode)		{ setPostalcode(postalcode); return this; }
	public void		setPostalcode(String postalcode)	{ mPostalcode = postalcode; }
	public String	getPostalcode()						{ return mPostalcode; }
	
	public Account	location(String location)		{ setLocation(location); return this; }
	public void		setLocation(String location)	{ mLocation = location; }
	public String	getLocation()					{ return mLocation; }
	
	public Account	country(String country)		{ setCountry(country); return this; }
	public void		setCountry(String country)	{ mCountry = country; }
	public String	getCountry()				{ return mCountry; }
	
	public Account	phonenumber(String phonenumber)		{ setPhonenumber(phonenumber); return this; }
	public void		setPhonenumber(String phonenumber)	{ mPhonenumber = phonenumber; }
	public String	getPhonenumber()					{ return mPhonenumber; }
	
	public Account	mobilenumber(String mobilenumber)		{ setMobilenumber(mobilenumber); return this; }
	public void		setMobilenumber(String mobilenumber)	{ mMobilenumber = mobilenumber; }
	public String	getMobilenumber()						{ return mMobilenumber; }
	
	public Account	faxnumber(String faxnumber)		{ setFaxnumber(faxnumber); return this; }
	public void		setFaxnumber(String faxnumber)	{ mFaxnumber = faxnumber; }
	public String	getFaxnumber()					{ return mFaxnumber; }
	
	public Account	birthdate(Date birthdate)		{ setBirthdate(birthdate); return this; }
	public Date		getBirthdate()					{ return mBirthdate; }
	public void		setBirthdate(Date birthdate)	{ mBirthdate = birthdate; }
	
	public Account	timeRegistered(Date timeRegistered)		{ setTimeRegistered(timeRegistered); return this; }
	public void		setTimeRegistered(Date timeRegistered)	{ mTimeRegistered = timeRegistered; }
	public Date		getTimeRegistered()						{ return mTimeRegistered; }
	
	public Account	avatar(byte[] avatar)		{ setAvatar(avatar); return this; }
	public void		setAvatar(byte[] avatar)	{ mAvatar = avatar; }
	public byte[]	getAvatar()					{ return mAvatar; }
}

