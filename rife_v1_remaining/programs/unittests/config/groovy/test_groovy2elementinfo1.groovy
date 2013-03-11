processor.element(implementation:"com.uwyn.rife.engine.testelements.engine.Simple") {

	// PROPERTIES
	property(name:"property1", "value1")
	property(name:"property2", "value2")
	property(name:"property3", "value3")
	property(name:"property4", "value4")

	// INPUTS
	input(name:"input1")
	input(name:"input2")
	input(name:"input3")
	
	// OUTPUTS
	output(name:"output1")
	output(name:"output2")
	output(name:"output3")
	output(name:"output4")
	
	// INCOOKIES
	incookie(name:"incookie1")
	incookie(name:"incookie2")
	
	// OUTCOOKIES
	outcookie(name:"outcookie1")
	outcookie(name:"outcookie2")
	outcookie(name:"outcookie3")
	outcookie(name:"outcookie4")
	
	// INBEANS
	inbean(name:"inbean1", classname:"com.uwyn.rife.engine.testelements.exits.BeanImpl1")
	inbean(classname:"com.uwyn.rife.engine.testelements.exits.BeanImpl2")
	inbean(classname:"com.uwyn.rife.engine.testelements.exits.BeanImpl1", prefix:"prefix_")
	inbean(name:"inbean2", classname:"com.uwyn.rife.engine.testelements.submission.BeanImpl", prefix:"prefixgroup_", group:"somegroup")
	inbean(classname:"com.uwyn.rife.engine.testelements.submission.BeanImpl", group:"somegroup")
	
	// OUTBEANS
	outbean(classname:"com.uwyn.rife.engine.testelements.exits.BeanImpl1")
	outbean(classname:"com.uwyn.rife.engine.testelements.exits.BeanImpl2")
	outbean(name:"outbean1", classname:"com.uwyn.rife.engine.testelements.exits.BeanImpl2", prefix:"prefix_")
	outbean(classname:"com.uwyn.rife.engine.testelements.submission.BeanImpl", prefix:"prefixgroup_", group:"somegroup")
	outbean(name:"outbean2", classname:"com.uwyn.rife.engine.testelements.submission.BeanImpl", group:"somegroup")
	
	// CHILD TRIGGERS
	childtrigger(name:"input1")
	childtrigger(name:"input2")
	
	// EXITS
	exit(name:"exit1")
	exit(name:"exit2")
	exit(name:"exit3")
	exit(name:"exit4")

    // SUBMISSIONS
    submission(name:"submission1") {
        param(name:"param1")
        param(name:"param2")
        param(name:"param3")
        param(regexp:"paramA(\\d+)")
        param(regexp:"paramB(\\d+)")
        file(name:"file1")
        file(name:"file2")
    }
	
    submission(name:"submission2") {
        param(name:"param1") {
			defaultvalue("default1")
			defaultvalue("default2")
		}
        param(name:"param2")
        param(regexp:"paramC(.*)")
        file(name:"file1")
    }
}

