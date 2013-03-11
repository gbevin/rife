/*B 'QUERY'*/SELECT/*V 'DISTINCT'*//*-V*/ /*V 'FIELDS'-*//*V 'FROM'*//*-V*//*V 'JOINS'*//*-V*//*V 'WHERE'*//*-V*//*V 'GROUPBY'*//*-V*//*V 'HAVING'*//*-V*//*V 'UNION'*//*-V*//*V 'ORDERBY'*//*-V*//*V 'LIMIT'*//*-V*//*-B*/
/*B 'SEPERATOR'*/, /*-B*/
/*B 'FROM'*/ FROM /*V 'TABLE'-*//*-B*/
/*B 'DISTINCT'*/ DISTINCT/*-B*/
/*B 'DISTINCTON'*//*-B*/
/*B 'ALLFIELDS'*/*/*-B*/
/*B 'JOIN_DEFAULT'*/, /*V 'TABLE'-*//*-B*/
/*B 'JOIN_CROSS'*/ CROSS JOIN /*V 'TABLE'-*//*-B*/
/*B 'JOIN_INNER'*/ /*V 'JOIN_INNER_NATURAL'*//*-V*/INNER JOIN /*V 'TABLE'-*//*V 'JOIN_INNER_ON'*//*-V*//*V 'JOIN_INNER_USING'*//*-V*//*-B*/
/*B 'JOIN_INNER_NATURAL'*//*-B*/
/*B 'JOIN_INNER_ON'*/ ON (/*V 'EXPRESSION'-*/)/*-B*/
/*B 'JOIN_INNER_USING'*//*-B*/
/*B 'JOIN_OUTER'*/ /*V 'JOIN_OUTER_NATURAL'*//*-V*//*V 'JOIN_OUTER_TYPE'*//*-V*/OUTER JOIN /*V 'TABLE'-*//*V 'JOIN_OUTER_ON'*//*-V*//*V 'JOIN_OUTER_USING'*//*-V*//*-B*/
/*B 'JOIN_OUTER_NATURAL'*//*-B*/
/*B 'JOIN_OUTER_ON'*/ ON (/*V 'EXPRESSION'-*/)/*-B*/
/*B 'JOIN_OUTER_USING'*//*-B*/
/*B 'JOIN_OUTER_FULL'*//*-B*/
/*B 'JOIN_OUTER_LEFT'*/LEFT /*-B*/
/*B 'JOIN_OUTER_RIGHT'*/RIGHT /*-B*/
/*B 'WHERE'*/ WHERE /*V 'CONDITION'-*//*-B*/
/*B 'GROUPBY'*/ GROUP BY /*V 'EXPRESSION'-*//*-B*/
/*B 'HAVING'*/ HAVING /*V 'EXPRESSION'-*//*-B*/
/*B 'UNION'*/ UNION /*V 'EXPRESSION'-*//*-B*/
/*B 'UNION_ALL'*/ UNION ALL /*V 'EXPRESSION'-*//*-B*/
/*B 'ORDERBY'*/ ORDER BY /*V 'ORDERBY_PARTS'-*//*-B*/
/*B 'ORDERBY_PART'*//*V 'COLUMN'-*/ /*V 'DIRECTION'-*//*-B*/
/*B 'ORDERBY_ASC'*/ASC/*-B*/
/*B 'ORDERBY_DESC'*/DESC/*-B*/
/*B 'LIMIT'*/ LIMIT /*V 'LIMIT_VALUE'-*//*V 'OFFSET'*//*-V*//*-B*/
/*B 'OFFSET'*/ OFFSET /*V 'OFFSET_VALUE'-*//*-B*/
