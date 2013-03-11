processor.site(fallbackid:"ELEMENT2") {
	globalexit(name:"globalexit3", destid:"ELEMENT3")
	globalvar(name:"globalvar3")
	
	departure(srcid:"ELEMENT3")

	element(id:"ELEMENT2", file:"xml/test_xml2elementinfo2.xml", url:"/test/element2")
	element(id:"ELEMENT3", file:"xml/test_xml2elementinfo3.xml", url:"/test/element3")
}

