parser grammar TemplateParser;
@header {
package com.uwyn.rife.template.antlr;
}
options { tokenVocab=TemplateLexer; }

document    :   content;

content     :   chardata?
                ((element) chardata?)* ;

element     :   OPEN S* TAGNAME S* NAME S* CLOSE content SLASH_OPEN S* TAGNAME S* CLOSE
            |   OPEN S* TAGNAME S* NAME S* SLASH_CLOSE
            ;

chardata    :   TEXT | SEA_WS ;
