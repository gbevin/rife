/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: OrderData.java 3918 2008-04-14 17:35:35Z gbevin $
 */
import java.util.Date;

public class OrderData {
	enum ShippingMethod {ground, express, air}
	enum CreditCardType {amex, visa, mastercard}
	
	private ShippingMethod	shippingMethod;
	private CreditCardType	creditCardType;
	private String	creditCardNumber;
	private Date	creditCardExpiration;
	
	public void				setShippingMethod(ShippingMethod shippingMethod)		{ this.shippingMethod = shippingMethod; }
	public ShippingMethod	getShippingMethod()										{ return shippingMethod; }
	public void				setCreditCardType(CreditCardType creditCardType)		{ this.creditCardType = creditCardType; }
	public CreditCardType	getCreditCardType()										{ return creditCardType; }
	public void				setCreditCardNumber(String creditCardNumber)			{ this.creditCardNumber = creditCardNumber; }
	public String			getCreditCardNumber()									{ return creditCardNumber; }
	public void				setCreditCardExpiration(Date creditCardExpiration)		{ this.creditCardExpiration = creditCardExpiration; }
	public Date				getCreditCardExpiration()								{ return creditCardExpiration; }
}

