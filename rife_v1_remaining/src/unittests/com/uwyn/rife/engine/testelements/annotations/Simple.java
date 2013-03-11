/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Simple.java 3961 2008-07-11 11:35:59Z gbevin $
 */
package com.uwyn.rife.engine.testelements.annotations;

import com.uwyn.rife.engine.UploadedFile;
import com.uwyn.rife.engine.annotations.*;
import com.uwyn.rife.engine.testelements.exits.BeanImpl1;
import com.uwyn.rife.engine.testelements.exits.BeanImpl2;
import com.uwyn.rife.engine.testelements.submission.BeanImpl;

@Elem(
	id = "ELEMENT1",
	url = "test/element1",
	contentType = "text/xhtml",
	inputs = {
		@Input(name = "input3")
	},
	inbeans = {
		@InBean(beanclass = BeanImpl2.class),
		@InBean(beanclass = BeanImpl1.class, prefix = "prefix_"),
		@InBean(beanclass = BeanImpl.class, group = "somegroup")
	},
	incookies = {
		@InCookie(name = "incookie2")
	},
	outputs = {
		@Output(name = "output2"),
		@Output(name = "output3")
	},
	outbeans = {
		@OutBean(beanclass = BeanImpl1.class),
		@OutBean(beanclass = BeanImpl2.class),
		@OutBean(beanclass = BeanImpl.class, prefix="prefixgroup_", group = "somegroup")
	},
	outcookies = {
		@OutCookie(name = "outcookie1"),
		@OutCookie(name = "outcookie4")
	},
	childTriggers = {
		@ChildTrigger(name = "input1"),
		@ChildTrigger(name = "input2")
	},
	exits = {
		@Exit(name = "exit1"),
		@Exit(name = "exit2")
	},
	submissions = {
		@Submission(
			name = "submission1",
			params = {
				@Param(name = "param1"),
				@Param(name = "param3")
			},
			paramRegexps = {
				@ParamRegexp("paramA(\\d+)"),
				@ParamRegexp("paramB(\\d+)")
			},
			files = {
				@File(name = "file2")
			},
			beans = {
				@SubmissionBean(beanclass=BeanImpl.class, prefix = "subm_", group = "somegroup"),
				@SubmissionBean(beanclass=BeanImpl1.class, prefix = "subm_")
			}
		)
	},
	pathinfo = @Pathinfo(
		mappings = {
			@Mapping("$key1/name/stuff"),
			@Mapping("$key1/$key2")
		}
	)
)
public class Simple extends BaseSimple
{
	private int mOutput1;
	private String mOutput4;
	private BeanImpl2 mOutbean1;
	private BeanImpl mOutbean2;
	private int mOutcookie2;
	private String mOutcookie3;
	
	@ExitField
	public static final String EXIT_3 = "exit3";

	@AutolinkExitField(destClass = Target.class)
	public static final String EXIT_4 = "exit4";

	@InBeanProperty(name="inbean1")
	public void setInbean1(BeanImpl1 inbean1)
	{
	}

	@InBeanProperty(prefix = "prefixgroup_", group = "somegroup")
	public void setInbean2(BeanImpl inbean2)
	{
	}

	@InCookieProperty
	public void setIncookie1(String incookie1)
	{
	}

	@OutputProperty(name="output1")
	public int getOutput1()
	{
		return mOutput1;
	}

	@OutputProperty
	public String getOutput4()
	{
		return mOutput4;
	}

	@OutBeanProperty(prefix="prefix_")
	public BeanImpl2 getOutbean1()
	{
		return mOutbean1;
	}

	@OutBeanProperty(name="outbean2", group="somegroup")
	public BeanImpl getOutbean2()
	{
		return mOutbean2;
	}

	@OutCookieProperty
	public int getOutcookie2()
	{
		return mOutcookie2;
	}
	
	@Priority({2, 1})
	@ParamProperty
	public void setParam4(String param4)
	{
	}
	
	@OutCookieProperty
	public String getOutcookie3()
	{
		return mOutcookie3;
	}

	@FileProperty(name="file1")
	public void setFile1(UploadedFile file1)
	{
	}
	
	@Priority({1})
	@SubmissionHandler(
		params = {
			@Param(name = "param1"),
			@Param(name = "param2")
		},
		paramRegexps = {
			@ParamRegexp("paramC(.*)")
		},
		files = {
			@File(name = "file1")
		}
	)
	public void doSubmission2()
	{
	}

	@Priority({1, 1})
	@ParamProperty
	public void setParam3(String param3)
	{
	}

	@Priority({1, 1})
	@FileProperty
	public void setFile2(UploadedFile file2)
	{
	}

	@Priority({2})
	@SubmissionHandler
	public void doAnotherSubmission()
	{
	}
	
	@ParamProperty
	public void setParam2(String param2)
	{
	}
	
	public void processElement()
	{
		pause();
	}
}
