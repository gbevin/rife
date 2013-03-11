processor.site(fallbackid:"ELEMENT4") {
	globalexit(name:"globalexit1", destid:"ELEMENT6")
	globalexit(name:"globalexit2", destid:"SUBSITE.ELEMENT2")

	globalvar(name:"globalvar1") {
			defaultvalue("default1")
		}
	globalvar(name:"globalvar2")

	globalbean(name:"globalbean1", classname:"com.uwyn.rife.engine.testelements.submission.BeanImpl", prefix:"onemoreprefixgroup_", group:"anothergroup")
	globalbean(classname:"com.uwyn.rife.engine.testelements.submission.BeanImpl", group:"anothergroup")
	
	subsite(id:"SUBSITE", file:"groovy/test_groovy2sitesubsite.groovy", urlprefix:"/subsite", inherits:"ELEMENT4")
	
	group(inherits:"ELEMENT5") {
		globalvar(name:"globalvar4")
 	
		element(id:"ELEMENT6", file:"groovy/test_groovy2elementinfo2.groovy", url:"/test/element6", inherits:"ELEMENT3")
		element(file:"groovy/test_groovy2elementinfo3.groovy", url:"/test/element7")
	}
}
builder
	.enterElement("groovy/test_groovy2elementinfo1.groovy")
		.setId("ELEMENT1")
		.setUrl("/test/element1")
		.addDataLink("output1", "ELEMENT2", "input1")
		.addDataLink("output1", "ELEMENT2", "input2")
		.addFlowLink("exit1", "ELEMENT2")
		.addSnapbackFlowLink("exit2", false)
		.addSnapbackDataLink("output2", "input2")
		.enterFlowLink("exit3")
			.destId("ELEMENT2")
			.addDataLink("output3", "input1")
		.leaveFlowLink()
		.enterFlowLink("exit4")
			.destId("ELEMENT2")
			.addDataLink("output4", "input1")
		.leaveFlowLink()
		.addOutput("value1")
		.addOutput("value2")
		.addAutoLink("ELEMENT2")
	.leaveElement()
	
	.enterElement("groovy/test_groovy2elementinfo2.groovy")
		.setId("ELEMENT2")
		.setInherits("ELEMENT3")
		.setUrl("/test/element2")
		.addInput("value1")
		.addInput("value2")
	.leaveElement()
	
	.enterElement("groovy/test_groovy2elementinfo3.groovy")
		.setId("ELEMENT3")
	.leaveElement()
	
	.enterElement("groovy/test_groovy2elementinfo4.groovy")
		.setId("ELEMENT4")
	.leaveElement()
	
	.enterElement("groovy/test_groovy2elementinfo5.groovy")
		.setId("ELEMENT5")
	.leaveElement()
