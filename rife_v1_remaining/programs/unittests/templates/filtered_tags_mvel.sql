This expression is true '/*V 'MVEL:value1'-*/'.
This expression is false '[!V 'MVEL:value2'/]'.
This expression is dynamic '[!V 'MVEL:value3'/]'.
/*B 'MVEL:value1:[[ true ]]'*/true value/*-B*/
[!B 'MVEL:value2:[[ false ]]']false value[!/B]
[!B 'MVEL:value3:[[ thevalue ]]']dynamic value[!/B]
