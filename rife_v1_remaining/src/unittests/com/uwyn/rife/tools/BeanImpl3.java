package com.uwyn.rife.tools;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.site.ConstrainedBean;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.MetaData;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BeanImpl3 extends MetaData<ConstrainedBean, ConstrainedProperty>
{
	private Date 			mPropertyDate = null;
	private byte			mPropertyByte = 0;
	private double			mPropertyDouble = 0.0d;
	private float			mPropertyFloat = 0.0f;
	private int				mPropertyInt = 0;
	private long			mPropertyLong = 0;
	private short			mPropertyShort = 0;
	private Byte			mPropertyByteObject = null;
	private Double			mPropertyDoubleObject = null;
	private Float			mPropertyFloatObject = null;
	private Integer			mPropertyIntegerObject = null;
	private Long			mPropertyLongObject = null;
	private Short			mPropertyShortObject = null;
	private BigDecimal		mPropertyBigDecimal = null;
	private Date[] 			mPropertyDateArray = null;
	private byte[]			mPropertyByteArray = null;
	private double[]		mPropertyDoubleArray = null;
	private float[]			mPropertyFloatArray = null;
	private int[]			mPropertyIntArray = null;
	private long[]			mPropertyLongArray = null;
	private short[]			mPropertyShortArray = null;
	private Byte[]			mPropertyByteObjectArray = null;
	private Double[]		mPropertyDoubleObjectArray = null;
	private Float[]			mPropertyFloatObjectArray = null;
	private Integer[]		mPropertyIntegerObjectArray = null;
	private Long[]			mPropertyLongObjectArray = null;
	private Short[]			mPropertyShortObjectArray = null;
	private BigDecimal[]	mPropertyBigDecimalArray = null;
	
	public void activateMetaData()
	{
		DateFormat date_format = new SimpleDateFormat("'custom format' yyyy-MM-dd HH:mm");
		date_format.setTimeZone(RifeConfig.Tools.getDefaultTimeZone());
		NumberFormat int_format = NumberFormat.getCurrencyInstance(Locale.US);
		NumberFormat double_format = NumberFormat.getNumberInstance(Locale.US);
		NumberFormat byte_format = NumberFormat.getPercentInstance(Locale.US);
		NumberFormat float_format = NumberFormat.getNumberInstance(Locale.FRANCE);
		NumberFormat long_format = NumberFormat.getCurrencyInstance(Locale.FRANCE);
		NumberFormat short_format = NumberFormat.getCurrencyInstance(Locale.ENGLISH);
		DecimalFormat bigdecimal_format = (DecimalFormat)NumberFormat.getNumberInstance(Locale.FRANCE);
		bigdecimal_format.setParseBigDecimal(true);
		
		addConstraint(new ConstrainedProperty("propertyDate").format(date_format));
		addConstraint(new ConstrainedProperty("propertyInt").format(int_format));
		addConstraint(new ConstrainedProperty("propertyIntegerObject").format(int_format));
		addConstraint(new ConstrainedProperty("propertyDouble").format(double_format));
		addConstraint(new ConstrainedProperty("propertyDoubleObject").format(double_format));
		addConstraint(new ConstrainedProperty("propertyByte").format(byte_format));
		addConstraint(new ConstrainedProperty("propertyByteObject").format(byte_format));
		addConstraint(new ConstrainedProperty("propertyFloat").format(float_format));
		addConstraint(new ConstrainedProperty("propertyFloatObject").format(float_format));
		addConstraint(new ConstrainedProperty("propertyLong").format(long_format));
		addConstraint(new ConstrainedProperty("propertyLongObject").format(long_format));
		addConstraint(new ConstrainedProperty("propertyShort").format(short_format));
		addConstraint(new ConstrainedProperty("propertyShortObject").format(short_format));
		addConstraint(new ConstrainedProperty("propertyBigDecimal").format(bigdecimal_format));
		
		addConstraint(new ConstrainedProperty("propertyDateArray").format(date_format));
		addConstraint(new ConstrainedProperty("propertyIntArray").format(int_format));
		addConstraint(new ConstrainedProperty("propertyIntegerObjectArray").format(int_format));
		addConstraint(new ConstrainedProperty("propertyDoubleArray").format(double_format));
		addConstraint(new ConstrainedProperty("propertyDoubleObjectArray").format(double_format));
		addConstraint(new ConstrainedProperty("propertyByteArray").format(byte_format));
		addConstraint(new ConstrainedProperty("propertyByteObjectArray").format(byte_format));
		addConstraint(new ConstrainedProperty("propertyFloatArray").format(float_format));
		addConstraint(new ConstrainedProperty("propertyFloatObjectArray").format(float_format));
		addConstraint(new ConstrainedProperty("propertyLongArray").format(long_format));
		addConstraint(new ConstrainedProperty("propertyLongObjectArray").format(long_format));
		addConstraint(new ConstrainedProperty("propertyShortArray").format(short_format));
		addConstraint(new ConstrainedProperty("propertyShortObjectArray").format(short_format));
		addConstraint(new ConstrainedProperty("propertyBigDecimalArray").format(bigdecimal_format));
	}

	public BeanImpl3()
	{
	}

	public int getPropertyInt()
	{
		return mPropertyInt;
	}

	public void setPropertyInt(int propertyInt)
	{
		mPropertyInt = propertyInt;
	}

	public double getPropertyDouble()
	{
		return mPropertyDouble;
	}

	public void setPropertyDouble(double propertyDouble)
	{
		mPropertyDouble = propertyDouble;
	}

	public Date getPropertyDate()
	{
		return mPropertyDate;
	}

	public void setPropertyDate(Date propertyDate)
	{
		mPropertyDate = propertyDate;
	}

	public byte getPropertyByte()
	{
		return mPropertyByte;
	}

	public void setPropertyByte(byte propertyByte)
	{
		mPropertyByte = propertyByte;
	}

	public float getPropertyFloat()
	{
		return mPropertyFloat;
	}

	public void setPropertyFloat(float propertyFloat)
	{
		mPropertyFloat = propertyFloat;
	}

	public long getPropertyLong()
	{
		return mPropertyLong;
	}

	public void setPropertyLong(long propertyLong)
	{
		mPropertyLong = propertyLong;
	}

	public short getPropertyShort()
	{
		return mPropertyShort;
	}

	public void setPropertyShort(short propertyShort)
	{
		mPropertyShort = propertyShort;
	}

	public Short getPropertyShortObject()
	{
		return mPropertyShortObject;
	}

	public void setPropertyShortObject(Short propertyShortObject)
	{
		mPropertyShortObject = propertyShortObject;
	}
	
	public void setPropertyBigDecimal(BigDecimal propertyBigDecimal)
	{
		mPropertyBigDecimal = propertyBigDecimal;
	}
	
	public BigDecimal getPropertyBigDecimal()
	{
		return mPropertyBigDecimal;
	}
	
	public Byte getPropertyByteObject()
	{
		return mPropertyByteObject;
	}

	public void setPropertyByteObject(Byte propertyByteObject)
	{
		mPropertyByteObject = propertyByteObject;
	}

	public Double getPropertyDoubleObject()
	{
		return mPropertyDoubleObject;
	}

	public void setPropertyDoubleObject(Double propertyDoubleObject)
	{
		mPropertyDoubleObject = propertyDoubleObject;
	}

	public Float getPropertyFloatObject()
	{
		return mPropertyFloatObject;
	}

	public void setPropertyFloatObject(Float propertyFloatObject)
	{
		mPropertyFloatObject = propertyFloatObject;
	}

	public Integer getPropertyIntegerObject()
	{
		return mPropertyIntegerObject;
	}

	public void setPropertyIntegerObject(Integer propertyIntegerObject)
	{
		mPropertyIntegerObject = propertyIntegerObject;
	}

	public Long getPropertyLongObject()
	{
		return mPropertyLongObject;
	}

	public void setPropertyLongObject(Long propertyLongObject)
	{
		mPropertyLongObject = propertyLongObject;
	}

	public Date[] getPropertyDateArray()
	{
		return mPropertyDateArray;
	}

	public void setPropertyDateArray(Date[] propertyDateArray)
	{
		mPropertyDateArray = propertyDateArray;
	}

	public byte[] getPropertyByteArray()
	{
		return mPropertyByteArray;
	}

	public void setPropertyByteArray(byte[] propertyByteArray)
	{
		mPropertyByteArray = propertyByteArray;
	}

	public double[] getPropertyDoubleArray()
	{
		return mPropertyDoubleArray;
	}

	public void setPropertyDoubleArray(double[] propertyDoubleArray)
	{
		mPropertyDoubleArray = propertyDoubleArray;
	}

	public float[] getPropertyFloatArray()
	{
		return mPropertyFloatArray;
	}

	public void setPropertyFloatArray(float[] propertyFloatArray)
	{
		mPropertyFloatArray = propertyFloatArray;
	}

	public int[] getPropertyIntArray()
	{
		return mPropertyIntArray;
	}

	public void setPropertyIntArray(int[] propertyIntArray)
	{
		mPropertyIntArray = propertyIntArray;
	}

	public long[] getPropertyLongArray()
	{
		return mPropertyLongArray;
	}

	public void setPropertyLongArray(long[] propertyLongArray)
	{
		mPropertyLongArray = propertyLongArray;
	}

	public short[] getPropertyShortArray()
	{
		return mPropertyShortArray;
	}

	public void setPropertyShortArray(short[] propertyShortArray)
	{
		mPropertyShortArray = propertyShortArray;
	}
	
	public void setPropertyBigDecimalArray(BigDecimal[] propertyBigDecimalArray)
	{
		mPropertyBigDecimalArray = propertyBigDecimalArray;
	}
	
	public BigDecimal[] getPropertyBigDecimalArray()
	{
		return mPropertyBigDecimalArray;
	}
	
 	public Byte[] getPropertyByteObjectArray()
	{
		return mPropertyByteObjectArray;
	}

	public void setPropertyByteObjectArray(Byte[] propertyByteObjectArray)
	{
		mPropertyByteObjectArray = propertyByteObjectArray;
	}

	public Double[] getPropertyDoubleObjectArray()
	{
		return mPropertyDoubleObjectArray;
	}

	public void setPropertyDoubleObjectArray(Double[] propertyDoubleObjectArray)
	{
		mPropertyDoubleObjectArray = propertyDoubleObjectArray;
	}

	public Float[] getPropertyFloatObjectArray()
	{
		return mPropertyFloatObjectArray;
	}

	public void setPropertyFloatObjectArray(Float[] propertyFloatObjectArray)
	{
		mPropertyFloatObjectArray = propertyFloatObjectArray;
	}

	public Integer[] getPropertyIntegerObjectArray()
	{
		return mPropertyIntegerObjectArray;
	}

	public void setPropertyIntegerObjectArray(Integer[] propertyIntegerObjectArray)
	{
		mPropertyIntegerObjectArray = propertyIntegerObjectArray;
	}

	public Long[] getPropertyLongObjectArray()
	{
		return mPropertyLongObjectArray;
	}

	public void setPropertyLongObjectArray(Long[] propertyLongObjectArray)
	{
		mPropertyLongObjectArray = propertyLongObjectArray;
	}

	public Short[] getPropertyShortObjectArray()
	{
		return mPropertyShortObjectArray;
	}

	public void setPropertyShortObjectArray(Short[] propertyShortObjectArray)
	{
		mPropertyShortObjectArray = propertyShortObjectArray;
	}
}
