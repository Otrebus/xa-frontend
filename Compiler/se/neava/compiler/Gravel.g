grammar Gravel ;

@header {package se.neava.compiler; }

declaration : (dataType) (identifier) | identifier ;

dataType : 'int' | 'char' | 'long' | identifier ;
identifier : TEXT ;

TEXT : ('a'..'z')+ ;

PIPE : '|' ;

WS : SPACE+ {skip();};

fragment SPACE : (' ' | '\t' | '\f' | '\n' | '\r') ;