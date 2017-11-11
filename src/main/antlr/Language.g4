grammar Language;

// Basics

IF: 'if';
ELSE: 'else';
WHILE: 'while';
FOR: 'for';
FUN: 'fun';
VAR: 'var';
RETURN: 'return';

OR: '||';
AND: '&&';
EQ: '=='
  | '!=';
CMP: '<'
   | '>'
   | '<='
   | '>=';
ADD: '+'
   | '-';
MUL: '*'
   | '/'
   | '%';

NEWLINE: ('\n' | '\r') -> skip;
WS: (' ' | '\t') -> skip;
COMMENT: '//' .*? NEWLINE -> skip;

identifier: IDENTIFIER;
IDENTIFIER: (LETTER | UNDERSCORE) (LETTER | UNDERSCORE | DIGIT)*;

literal: LITERAL;
LITERAL: NON_ZERO_DIGIT DIGIT*;

LETTER: 'a'..'z'
      | 'A'..'Z';

NON_ZERO_DIGIT: '1'..'9';
DIGIT: '0' | NON_ZERO_DIGIT;

UNDERSCORE: '_';
COMMA: ',';
ASSIGN: '=';

LPAREN: '(';
RPAREN: ')';
LBRACE: '{';
RBRACE: '}';

// File

file: b = block EOF;

block: statement*;

block_with_braces: LBRACE block RBRACE;

// Statements

statement: function
         | whileStatement
         | ifStatement
         | assignment
         | returnStatement
         | expression
         | variable;

function: FUN name = IDENTIFIER LPAREN parameters = parameter_names RPAREN body = block_with_braces;

variable: VAR name = IDENTIFIER (ASSIGN init = expression)?;

parameter_names: (IDENTIFIER COMMA)* IDENTIFIER
               | ;

whileStatement: WHILE LPAREN expression RPAREN block_with_braces;

ifStatement: IF LPAREN expression RPAREN block_with_braces (ELSE block_with_braces)?;

assignment: IDENTIFIER ASSIGN expression;

returnStatement: RETURN expression;

// Expressions

expression: LPAREN expression RPAREN
          | IDENTIFIER
          | functionCall
          | literal
          | left = expression MUL right = expression
          | left = expression ADD right = expression
          | left = expression CMP right = expression
          | left = expression EQ right = expression
          | left = expression AND right = expression
          | left = expression OR right = expression
          ;

functionCall: identifier LPAREN arguments RPAREN;

arguments: expression (COMMA expression)*
         | ;

