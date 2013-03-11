/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSuiteSite.java 3947 2008-05-05 12:23:39Z gbevin $
 */
package com.uwyn.rife.site;

import com.uwyn.rife.RifeTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

public class TestSuiteSite extends TestSuite
{
	public static Test suite()
	{
		RifeTestSuite suite = new RifeTestSuite("Site API test suite");

		suite.addTestSuite(com.uwyn.rife.site.TestAbstractValidationRule.class);
		suite.addTestSuite(com.uwyn.rife.site.TestConstrainedProperty.class);
		suite.addTestSuite(com.uwyn.rife.site.TestTextualIdentifierGenerator.class);
		suite.addTestSuite(com.uwyn.rife.site.TestConstrainedBean.class);
		suite.addTestSuite(com.uwyn.rife.site.TestConstrainedUtils.class);
		suite.addTestSuite(com.uwyn.rife.site.TestCookieComparator.class);
		suite.addTestSuite(com.uwyn.rife.site.TestFormBuilderXhtml.class);
		suite.addTestSuite(com.uwyn.rife.site.TestFormBuilderXml.class);
		suite.addTestSuite(com.uwyn.rife.site.TestMetaData.class);
		suite.addServersideTestSuite(com.uwyn.rife.site.TestPagedNavigation.class);
		suite.addTestSuite(com.uwyn.rife.site.TestPropertyValidationRule.class);
		suite.addTestSuite(com.uwyn.rife.site.TestSelectResourceBundle.class);
		suite.addTestSuite(com.uwyn.rife.site.TestValidationError.class);
		suite.addTestSuite(com.uwyn.rife.site.TestValidation.class);
		suite.addTestSuite(com.uwyn.rife.site.TestValidationBuilderXhtml.class);
		suite.addTestSuite(com.uwyn.rife.site.TestValidationGroup.class);
		suite.addTestSuite(com.uwyn.rife.site.TestValidationRuleEmail.class);
		suite.addTestSuite(com.uwyn.rife.site.TestValidationRuleFormat.class);
		suite.addTestSuite(com.uwyn.rife.site.TestValidationRuleInList.class);
		suite.addTestSuite(com.uwyn.rife.site.TestValidationRuleLimitedDate.class);
		suite.addTestSuite(com.uwyn.rife.site.TestValidationRuleLimitedLength.class);
		suite.addTestSuite(com.uwyn.rife.site.TestValidationRuleNotEmpty.class);
		suite.addTestSuite(com.uwyn.rife.site.TestValidationRuleNotEqual.class);
		suite.addTestSuite(com.uwyn.rife.site.TestValidationRuleNotNull.class);
		suite.addTestSuite(com.uwyn.rife.site.TestValidationRuleRange.class);
		suite.addTestSuite(com.uwyn.rife.site.TestValidationRuleRegexp.class);
		suite.addTestSuite(com.uwyn.rife.site.TestValidationRuleSameAs.class);
		suite.addTestSuite(com.uwyn.rife.site.TestValidationRuleUrl.class);
		suite.addTestSuite(com.uwyn.rife.site.TestValidityChecks.class);

		return suite;
	}
}

