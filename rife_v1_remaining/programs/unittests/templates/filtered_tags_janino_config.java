//This config value expression is true '/*V 'JANINO:CONFIG:value1'-*/'.
//This config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.
//This config value expression is dynamic '[!V 'JANINO:CONFIG:value3'/]'.
///*B 'JANINO:CONFIG:value1:[[ config.getBool("EXPRESSION_CONFIG_VALUE_BOOL") ]]'*/true value/*-B*/
//[!B 'JANINO:CONFIG:value2:[[ !config.getString("EXPRESSION_CONFIG_VALUE").equals("the value") ]]']false value[!/B]
///*B 'JANINO:CONFIG:value3:[[ config.getString("EXPRESSION_CONFIG_VALUE").equals(context.get("thevalue").toString()) ]]'*/dynamic value/*-B*/
