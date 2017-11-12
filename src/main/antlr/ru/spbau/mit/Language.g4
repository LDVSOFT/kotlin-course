grammar Language;

@header {
    package ru.spbau.mit;
}

// Basics

COMMENT_START: '//';

NEWLINE: ('\n' | '\r') -> skip;
WS: (' ' | '\t') -> skip;
COMMENT: COMMENT_START .*? (NEWLINE | EOF) -> skip;

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

identifier: IDENTIFIER;
IDENTIFIER: (LETTER | UNDERSCORE) (LETTER | UNDERSCORE | DIGIT)*;

literal: LITERAL;
LITERAL: NON_ZERO_DIGIT DIGIT*
       | ZERO_DIGIT;

LETTER: 'a'..'z'
      | 'A'..'Z';

NON_ZERO_DIGIT: '1'..'9';
ZERO_DIGIT: '0';
DIGIT: ZERO_DIGIT | NON_ZERO_DIGIT;

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

blockWithBraces: LBRACE block RBRACE;

// Statements

statement: functionDefinition
         | whileStatement
         | ifStatement
         | assignment
         | returnStatement
         | variableDefinition
         | expressionStatement;

functionDefinition: FUN name = identifier LPAREN parameters = parameterNames RPAREN body = blockWithBraces;

variableDefinition: VAR name = identifier (ASSIGN init = expression)?;

parameterNames: (identifier COMMA)* identifier
              | ;

whileStatement: WHILE LPAREN condition = expression RPAREN body = blockWithBraces;

ifStatement: IF LPAREN condition = expression RPAREN thenBranch = blockWithBraces (ELSE elseBranch = blockWithBraces)?;

assignment: name = identifier ASSIGN value = expression;

returnStatement: RETURN value = expression;

expressionStatement: e = expression;

// Expressions

expression: orExpression;

baseExpression: bracedExpression
              | literal
              | functionCall
              | variable;

bracedExpression: LPAREN braced = expression RPAREN;

variable: name = identifier;

functionCall: name = identifier LPAREN args = arguments RPAREN;

arguments: expression (COMMA expression)*
         | ;

orExpression: left = orExpression op = OR right = andExpression
            | sole = andExpression;

andExpression: left = andExpression op = AND right = eqExpression
             | sole = eqExpression;

eqExpression: left = eqExpression op = EQ right = cmpExpression
            | sole = cmpExpression;

cmpExpression: left = cmpExpression op = CMP right = addExpression
             | sole = addExpression;

addExpression: left = addExpression op = ADD right = mulExpression
             | sole = mulExpression;

mulExpression: left = mulExpression op = MUL right = baseExpression
             | sole = baseExpression;