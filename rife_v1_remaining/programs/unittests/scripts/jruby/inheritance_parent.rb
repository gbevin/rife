def childTriggered(name, value)
	if name == "trigger" && value[0] == "ok"
		return true
	else
		return false
	end
end

if $element.hasSubmission("activatechild")
	$element.setOutput("trigger", "ok")
end

$element.print(<<END)
<html>
<body>
<a href="#{$element.getSubmissionQueryUrl("activatechild")}">activate child</a>
</body>
</html>
END
