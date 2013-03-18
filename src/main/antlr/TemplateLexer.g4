lexer grammar TemplateLexer;
@header {
package com.uwyn.rife.template.antlr;
}

OPEN            :   '<!--'      -> pushMode(INSIDE) ;
SLASH_OPEN      :   '<!--/'     -> pushMode(INSIDE) ;
SEA_WS          :   (' '|'\t'|'\r'? '\n') ;
TEXT            :   .+?;

mode INSIDE;

CLOSE           :   '-->'       -> popMode ;
SLASH_CLOSE     :   '/-->'      -> popMode ;
S               :   [ \t\r\n]   -> skip ;
GAP             :   'gap' ;
SNIP            :   'snip' ;
LOAD            :   'load' ;
TAGNAME         :   GAP
                |   SNIP
                |   LOAD
                ;

NAME            :   NAMESTARTCHAR NAMECHAR* ;

fragment    
DIGIT           :   [0-9] ;
    
fragment    
NAMECHAR        :   NAMESTARTCHAR
                |   '-' | '.' | DIGIT 
                |   '\u00B7'
                |   '\u0300'..'\u036F'
                |   '\u203F'..'\u2040'
                ;

fragment
NAMESTARTCHAR
                :   [:a-zA-Z]
                |   '\u2070'..'\u218F' 
                |   '\u2C00'..'\u2FEF' 
                |   '\u3001'..'\uD7FF' 
                |   '\uF900'..'\uFDCF' 
                |   '\uFDF0'..'\uFFFD'
                ;
