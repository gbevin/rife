This config value expression is true '/*V 'OGNL:CONFIG:value1'-*/'.
This config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.
This config value expression is dynamic '[!V 'OGNL:CONFIG:value3'/]'.
/*B 'OGNL:CONFIG:value1:[[ getBool("EXPRESSION_CONFIG_VALUE_BOOL") ]]'*/true value/*-B*/
[!B 'OGNL:CONFIG:value2:[[ getString("EXPRESSION_CONFIG_VALUE") != "the value" ]]']false value[!/B]
/*B 'OGNL:CONFIG:value3:[[ getString("EXPRESSION_CONFIG_VALUE") == #thevalue ]]'*/dynamic value/*-B*/
