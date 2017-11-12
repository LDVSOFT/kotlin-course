grammar Language;

@header {
    package ru.spbau.mit;
}

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

baseExpression: LPAREN braced = expression RPAREN
          | functionCall
          | variable
          | literal;

variable: name = identifier;

functionCall: name = identifier LPAREN args = arguments RPAREN;

arguments: expression (COMMA expression)*
         | ;

orExpression: left = andExpression op = OR right = orExpression
            | sole = andExpression;

andExpression: left = eqExpression op = AND right = andExpression
             | sole = eqExpression;

eqExpression: left = cmpExpression op = EQ right = eqExpression
            | sole = cmpExpression;

cmpExpression: left = addExpression op = CMP right = cmpExpression
             | sole = addExpression;

addExpression: left = mulExpression op = ADD right = addExpression
             | sole = mulExpression;

mulExpression: left = baseExpression op = ADD right = mulExpression
             | sole = baseExpression;