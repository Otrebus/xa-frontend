grammar Gravel ;

program : externDeclaration* classInstanceDeclaration* classDefinition* ;

externDeclaration : 'extern' type identifier '(' type (',' type)* ')' ';' ;

identifier : TEXTNUM ;

TEXTNUM : (TEXT)+(NUM | TEXT)* ;

type : baseType brackets? ;

brackets : ('[' ']') ;

baseType : 'bool' | 'int' | 'char' | 'long' | identifier | 'void' | functionPtr;

functionPtr : 'function' '(' ((type (',' type)*))? ')' '->' type ;

classInstanceDeclaration : identifier identifier ';' ;

classDefinition : 'class' identifier '{' classVariableDeclaration* methodDefinition* '}' ;

classVariableDeclaration : type identifier ';' ;

methodDefinition : type identifier '(' (type identifier (',' type identifier )*)? ')' '{' methodBody '}' ;

methodBody : methodVariableDefinition* statement* returnStatement;

methodVariableDefinition : type identifier ';' ;

statement : assignment | ifStatement | whileStatement | returnStatement | '{' statement* '}' | functionCallStatement | asyncStatement; 

assignment : lvalue '=' expression ';' ;

ifStatement : 'if' '(' expression ')' statement ('else' statement)? ;

whileStatement : 'while' '(' expression ')' statement ;

asyncStatement : 'after' expression time 'before' expression time functionCallStatement | 'after' expression time functionCallStatement | 'before' expression time functionCallStatement ;

time : 'sec' | 'msec' | 'usec' ;

lvalue : identifier | identifier '[' expression ']' ;

expression : 
    'true' #trueExp |
    'false' #falseExp |
    NUM #numExp |
    identifier ('[' expression ']')? #arrayLookupExp
    | identifier '.' identifier #indirectionExp
    | functionCall #functionCallExp
    | '(' expression ')' #parExp
    | expression '*' expression #mulExp
    | expression '/' expression #divExp
    | expression '+' expression #addExp
    | expression '-' expression #subExp
    | expression '>' expression #gtExp
    | expression '>=' expression #gteExp
    | expression '<=' expression #lteExp
    | expression '<' expression #ltExp
    | expression '==' expression #eqExp
    | expression '&&' expression #logAndExp
    | expression '||' expression #logOrExp ;

functionCall : (identifier.)?identifier '(' (expression (',' expression)*)? ')' ;

functionCallStatement : functionCall ';' ;

returnStatement : 'return' expression? ';' ;

number : NUM ;

string : STRING ;

TEXT : ('a'..'z' | 'A'..'Z' | '_' );

NUM : ('0'..'9')+ ;

STRING : '"' ( ~('\n'|'\r') )*? '"';

WS    :    (' '|'\t'|'\f'|'\n'|'\r')+{ skip(); };