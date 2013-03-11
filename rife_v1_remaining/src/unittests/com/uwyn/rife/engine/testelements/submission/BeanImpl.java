/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BeanImpl.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.submission;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.Validation;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BeanImpl extends Validation
{
	public enum Day { SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY }
	
	private Day					mEnum;
	private String				mString;
	private StringBuffer		mStringbuffer;
	private int					mInt;
	private Integer				mInteger;
	private char				mChar;
	private Character			mCharacter;
	private boolean				mBoolean;
	private Boolean				mBooleanObject;
	private byte				mByte;
	private Byte				mByteObject;
	private double				mDouble;
	private Double				mDoubleObject;
	private float				mFloat;
	private Float				mFloatObject;
	private long				mLong;
	private Long				mLongObject;
	private short				mShort = -24;
	private Short				mShortObject;
	private String				mStringFile;
	private byte[]				mBytesFile;
	private InputStream			mStreamFile;
	private Date				mDate;
	private Date				mDateFormatted;
	private Date[]				mDatesFormatted;
	private SerializableParam	mSerializableParam;
	private SerializableParam[]	mSerializableParams;
	
	public void activateValidation()
	{
		addConstraint(new ConstrainedProperty("character").editable(false));
		addConstraint(new ConstrainedProperty("byte").editable(false));
		addConstraint(new ConstrainedProperty("stringFile").file(true));
		addConstraint(new ConstrainedProperty("bytesFile").file(true));
		addConstraint(new ConstrainedProperty("streamFile").file(true));

		addGroup("somegroup")
			.addConstraint(new ConstrainedProperty("enum"))
			.addConstraint(new ConstrainedProperty("string"))
			.addConstraint(new ConstrainedProperty("int"))
			.addConstraint(new ConstrainedProperty("longObject"))
			.addConstraint(new ConstrainedProperty("short"));
		
		addGroup("anothergroup")
			.addConstraint(new ConstrainedProperty("double"))
			.addConstraint(new ConstrainedProperty("long"))
			.addConstraint(new ConstrainedProperty("shortObject"));
		
		SimpleDateFormat sf = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss");
		sf.setTimeZone(RifeConfig.Tools.getDefaultTimeZone());
		addConstraint(new ConstrainedProperty("dateFormatted").format(sf));
		addConstraint(new ConstrainedProperty("datesFormatted").format(sf));
	}
	
	public void setEnum(Day day)
	{
		mEnum = day;
	}
	
	public Day getEnum()
	{
		return mEnum;
	}
	
	public String getString()
	{
		return mString;
	}

	public void setString(String string)
	{
		mString = string;
	}

	public StringBuffer getStringbuffer()
	{
		return mStringbuffer;
	}

	public void setStringbuffer(StringBuffer stringbuffer)
	{
		mStringbuffer = stringbuffer;
	}

	public int getInt()
	{
		return mInt;
	}

	public void setInt(int anInt)
	{
		mInt = anInt;
	}

	public Integer getInteger()
	{
		return mInteger;
	}

	public void setInteger(Integer integer)
	{
		mInteger = integer;
	}

	public char getChar()
	{
		return mChar;
	}

	public void setChar(char aChar)
	{
		mChar = aChar;
	}

	public Character getCharacter()
	{
		return mCharacter;
	}

	public void setCharacter(Character character)
	{
		mCharacter = character;
	}

	public boolean isBoolean()
	{
		return mBoolean;
	}

	public void setBoolean(boolean aBoolean)
	{
		mBoolean = aBoolean;
	}

	public Boolean getBooleanObject()
	{
		return mBooleanObject;
	}

	public void setBooleanObject(Boolean aBooleanObject)
	{
		mBooleanObject = aBooleanObject;
	}

	public byte getByte()
	{
		return mByte;
	}

	public void setByte(byte aByte)
	{
		mByte = aByte;
	}

	public Byte getByteObject()
	{
		return mByteObject;
	}

	public void setByteObject(Byte byteObject)
	{
		mByteObject = byteObject;
	}

	public double getDouble()
	{
		return mDouble;
	}

	public void setDouble(double aDouble)
	{
		mDouble = aDouble;
	}

	public Double getDoubleObject()
	{
		return mDoubleObject;
	}

	public void setDoubleObject(Double doubleObject)
	{
		mDoubleObject = doubleObject;
	}

	public float getFloat()
	{
		return mFloat;
	}

	public void setFloat(float aFloat)
	{
		mFloat = aFloat;
	}

	public Float getFloatObject()
	{
		return mFloatObject;
	}

	public void setFloatObject(Float floatObject)
	{
		mFloatObject = floatObject;
	}

	public long getLong()
	{
		return mLong;
	}

	public void setLong(long aLong)
	{
		mLong = aLong;
	}

	public Long getLongObject()
	{
		return mLongObject;
	}

	public void setLongObject(Long longObject)
	{
		mLongObject = longObject;
	}

	public short getShort()
	{
		return mShort;
	}

	public void setShort(short aShort)
	{
		mShort = aShort;
	}

	public Short getShortObject()
	{
		return mShortObject;
	}

	public void setShortObject(Short shortObject)
	{
		mShortObject = shortObject;
	}
	
	public void setStringFile(String stringFile)
	{
		mStringFile = stringFile;
	}
	
	public String getStringFile()
	{
		return mStringFile;
	}
	
	public void setBytesFile(byte[] bytesFile)
	{
		mBytesFile = bytesFile;
	}
	
	public byte[] getBytesFile()
	{
		return mBytesFile;
	}
	
	public void setStreamFile(InputStream streamFile)
	{
		mStreamFile = streamFile;
	}
	
	public InputStream getStreamFile()
	{
		return mStreamFile;
	}
	
	public void setDate(Date date)
	{
		mDate = date;
	}
	
	public Date getDate()
	{
		return mDate;
	}
	
	public void setDateFormatted(Date dateFormatted)
	{
		mDateFormatted = dateFormatted;
	}
	
	public Date getDateFormatted()
	{
		return mDateFormatted;
	}
	
	public void setDatesFormatted(Date[] datesFormatted)
	{
		mDatesFormatted = datesFormatted;
	}
	
	public Date[] getDatesFormatted()
	{
		return mDatesFormatted;
	}
	
	public void setSerializableParam(SerializableParam serializableParam)
	{
		mSerializableParam = serializableParam;
	}
	
	public SerializableParam getSerializableParam()
	{
		return mSerializableParam;
	}
	
	public void setSerializableParams(SerializableParam[] serializableParams)
	{
		mSerializableParams = serializableParams;
	}
	
	public SerializableParam[] getSerializableParams()
	{
		return mSerializableParams;
	}
	
	public static class SerializableParam implements Serializable
	{
		private int		mNumber = -1;
		private String	mString = null;
		
		public SerializableParam(int number, String string)
		{
			mNumber = number;
			mString = string;
		}
		
		public void setNumber(int number)
		{
			mNumber = number;
		}
		
		public int getNumber()
		{
			return mNumber;
		}
		
		public void setString(String string)
		{
			mString = string;
		}
		
		public String getString()
		{
			return mString;
		}
		
		public String toString()
		{
			return mNumber+":"+mString;
		}
		
		public boolean equals(Object other)
		{
			if (this == other)
			{
				return true;
			}
			
			if (null == other)
			{
				return false;
			}
			
			if (!(other instanceof SerializableParam))
			{
				return false;
			}
			
			SerializableParam other_datalink = (SerializableParam)other;
			if (!other_datalink.getString().equals(getString()))
			{
				return false;
			}
			if (other_datalink.getNumber() != getNumber())
			{
				return false;
			}
			
			return true;
		}
	}
}

