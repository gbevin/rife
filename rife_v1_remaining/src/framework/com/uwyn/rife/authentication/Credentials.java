/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Credentials.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication;

import com.uwyn.rife.site.Validated;

/**
 * This interface defines the methods that classes with
 * {@code Credentials} functionalities have to implement.
 * <p>Credentials contain the information that is required to verify if a user
 * is valid and known to the system. They are the basic building blocks of the
 * authentication mechanism.
 * <p>{@code Credentials} can contain any data, they just have to be able
 * to validate the contents of the provided data. For example enforce limits
 * on the length of passwords, verify the accepted characters in logins, check
 * the numeric requirements of identifiers, and so on.
 * <p>This is different from the validation of the credentials themselves (ie.
 * checking if they're known by the system and can be authenticated) since
 * that is performed by a {@code CredentialsManager}.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see CredentialsManager
 * @since 1.0
 */
public interface Credentials extends Validated
{
}

