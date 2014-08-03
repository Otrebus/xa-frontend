grammar Gravel ;

@header {package se.neava.compiler; }

program : externDeclaration* classDeclaration* classDefinition* ;

externDeclaration : 'extern' argType identifier '(' declarationArgList? ')' ';' ;

declarationArgList : argType (',' argType)*;

argType : 'void' | type | functionPtr ;

identifier : TEXTNUM ;

TEXTNUM : (TEXT)+(NUM | TEXT)* ;

type : baseType ('[' ']')? ;

baseType : 'bool' | 'int' | 'char' | 'long' | identifier ;

functionPtr : 'function' '(' declarationArgList ')' '->' argType ;

classDeclaration : identifier identifier ';' ;

classDefinition : 'class' identifier '{' classVariableDeclaration* methodDefinition* '}' ;

classVariableDeclaration : type identifier ('=' classVariableInitializer)? ';' ;

classVariableInitializer : NUM | STRING | 'false' | 'true' ;

methodDefinition : argType identifier '(' argList ')' '{' methodBody '}' ;

argList : (argType identifier (',' argType identifier )*)?;

methodBody : methodVariableDefinition* statement* ;

methodVariableDefinition : type identifier ( '=' expression )? ';' ;

statement : assignment | ifStatement | whileStatement | returnStatement | '{' statement* '}' | functionCall | asyncStatement; 

assignment : lvalue '=' expression ';' ;

ifStatement : 'if' '(' expression ')' statement ;

whileStatement : 'while' '(' expression ')' statement ;

asyncStatement : 'after' expression time 'before' expression time functionCall | 'after' expression time functionCall | 'before' expression time functionCall ;

time : 'sec' | 'msec' | 'usec' ;

lvalue : identifier | identifier '[' expression ']' ;

expression : 
    'true' #trueExp |
    'false' #falseExp |
    NUM #numExp |
    identifier ('[' expression ']')? #arrayLookupExp
    | identifier '.' identifier #indirectionExp
    | identifier '(' expression ')' #functionCallExp
    | '(' expression ')' #parExp
    | expression '*' expression #mulExp
    | expression '/' expression #divExp
    | expression '+' expression #addExp
    | expression '-' expression #subExp
    | expression '&&' expression #logAndExp
    | expression '||' expression #logOrExp
    | expression '>' expression #gtExp
    | expression '>=' expression #gteExp
    | expression '<=' expression #lteExp
    | expression '<' expression #ltExp
    | expression '==' expression #eqExp ;

functionCall : (identifier.)?identifier '(' expression? (',' expression)* ')' ';' ;

returnStatement : 'return' expression ';' ;

TEXT : ('a'..'z' | 'A'..'Z' | '_' );

NUM : ('0'..'9')+ ;

STRING : '"' ( ~('\n'|'\r') )*? '"';

WS	:	(' '|'\t'|'\f'|'\n'|'\r')+{ skip(); };