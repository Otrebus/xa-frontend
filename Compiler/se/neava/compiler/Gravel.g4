grammar Gravel ;


@header {

import java.util.LinkedList;
import java.util.List;
}

@members {
    private List<String> errors = new LinkedList<String>();
    public void displayRecognitionError(String[] tokenNames,
                                        RecognitionException e) {
        String hdr = getErrorHeader(e);
        //String msg = getErrorMessage(e, tokenNames);
        errors.add(hdr);
    }
    public List<String> getErrors() {
        return errors;
    }
    public void emitErrorMessage(String msg) {
    }
}

program : externDeclaration* classInstanceDeclaration* classDefinition* ;

externDeclaration : 'extern' type identifier '(' (type (',' type)*)? ')' ';' ;

type : baseType brackets? ;

brackets : ('[' (NUM)? ']') ;

baseType : 'bool' | 'int' | 'char' | 'long' | identifier | 'void' | functionPtr ;

functionPtr : 'function' '(' ((type (',' type)*))? ')' '->' type ;

classInstanceDeclaration : identifier identifier ';' ;

classDefinition : classType identifier '{' classVariableDeclaration* methodDefinition* '}' ;

classType : 'object' | 'class' ;

classVariableDeclaration : type identifier ';' ;

methodDefinition : type identifier '(' (type identifier (',' type identifier )*)? ')' '{' methodBody '}' ; 

methodBody : methodVariableDefinition* statement* returnStatement;

methodVariableDefinition : type identifier ';' ;

statement : assignment | ifStatement | whileStatement | returnStatement | '{' statement* '}' | functionCallStatement | asyncStatement ; 

assignment : lvalue '=' expression ';' ;

ifStatement : 'if' '(' expression ')' statement elseClause? ;

elseClause : ('else' statement) ;

whileStatement : 'while' '(' expression ')' statement ;

asyncStatement : after expression time before expression time functionCall ';' | after expression time functionCall ';' | before expression time functionCall ';' ;

after : 'after' ;

before : 'before' ;

time : 'sec' | 'msec' | 'usec' ;

lvalue : identifier | identifier '[' expression ']' ;

expression :
    identifier #identifierExp |
    'true' #trueExp |
    'false' #falseExp |
    NUM suffix #numExp |
    '(' baseType ')' expression #castExp |
    expression ('[' expression ']') #arrayLookupExp
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

suffix : identifier ;

identifier : TEXTNUM ;

TEXTNUM : (CHAR)+(NUM|CHAR)* ;

number : NUM ;

string : STRING ;

CHAR : ('a'..'z' | 'A'..'Z' | '_' );

NUM : ('0'..'9')+ ;

STRING : '"' ( ~('\n'|'\r') )*? '"';

WS    :    (' '|'\t'|'\f'|'\n'|'\r')+ -> skip ;

MULTICOMMENT : '/*' .*? '*/' -> skip ;

SINGLECOMMENT : '//' ~('\r' | '\n')* -> skip ;