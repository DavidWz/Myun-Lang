grammar Myun;

/* A compilation unit consists of a function definition part and a script part. */
compileUnit
    :   funcDef* script EOF
    ;

/* A block is a series of statements, possibly with a break or return at the end. */
block: statement* (funcReturn | loopBreak)?;

/* A script has a name and code block. */
script: 'script' name=ID block 'end';

/* Types of statements */
statement
    : declaration
    | assignment
    | branch
    | loop
    | funcCall
    ;

/* Variable declarations */
declaration: variable ':=' expr;

/* Assignments */
assignment: variable '=' expr;

/* Branches */
branch
    : 'if' expr 'then' block
      ('elseif' expr 'then' block)*
      ('else' block)?
      'end'
    ;

/* Loops */
loop
    : 'while' expr 'do' block 'end'                                  # whileLoop
    | 'for' variable 'from' from=expr 'to' to=expr 'do' block 'end'  # forLoop
    ;

loopBreak: 'break';

/* Function Definition (params and result can be annotated with a type) */
funcDef
    : name=ID '(' (funcParam (',' funcParam)*)? ')' ('::' returnType=type)?
        block
      'end'
    ;

/* Function parameters can be annotated with a type */
funcParam: variable ('::' type)?;

/* Function Calls */
funcCall
    : func=ID '(' (expr (',' expr)*)? ')'
    ;

funcReturn
    : 'return' expr
    ;

/* Variables */
variable: name=ID;

/* Types */
type
    : '(' type ')'                                         # parenthesisType
    /* Basic data types */
    | name=TYPE_ID                                         # basicType
    /* Function types */
    | <assoc=right> type '->' type                         # funcType
    | '(' (type (',' type)*)? ')' '->' type                # funcType
    ;

/* Expressions */
expr
    : '(' expr ')'                                          # parenthesisExpr
    /* Function Calls */
    | funcCall                                              # functionCall
    /* Primitives */
    | bool                                                  # basic
    | integer                                               # basic
    | floating                                              # basic
    | variable                                              # basic
    /* Arithmetic Expressions */
    | <assoc=right> left=expr op=OP_EXP right=expr          # operatorExpr
    | prefix=OP_SUB          expr                           # prefixExpr
    | left=expr op=(OP_MUL | OP_DIV) right=expr             # operatorExpr
    | left=expr op=(OP_ADD | OP_SUB) right=expr             # operatorExpr
    | left=expr op=OP_MOD right=expr                        # operatorExpr
    /* Boolean Expressions */
    | left=expr op=(OP_LT|OP_LEQ|OP_GT|OP_GEQ) right=expr   # operatorExpr
    | left=expr op=OP_EQ right=expr                         # operatorExpr
    | prefix=OP_NOT expr                                    # prefixExpr
    | left=expr op=(OP_AND | OP_OR) right=expr              # operatorExpr
    ;

bool: value=(BOOL_TRUE | BOOL_FALSE);
integer: value=NUM_INT;
floating: value=NUM_FLOAT;

/* Infix Operators */
OP_NOT: 'not';
OP_AND: 'and';
OP_OR: 'or';
OP_EQ: 'is';
OP_LT: '<';
OP_LEQ: '<=';
OP_GT: '>';
OP_GEQ: '>=';
OP_ADD: '+';
OP_SUB: '-';
OP_MUL: '*';
OP_DIV: '/';
OP_EXP: '^';
OP_MOD: 'mod';

/* Basic Data Values */
BOOL_TRUE: 'true';
BOOL_FALSE: 'false';
NUM_INT: [0-9]+;
NUM_FLOAT: [0-9]+ '.' [0-9]+ ([eE] [+-]? [0-9]+)?;

/* Identifiers */
ID: [a-z][a-zA-Z0-9_]*;
TYPE_ID: [A-Z][a-zA-Z0-9_]*;

/* Ignored whitespaces and comments */
WS: [ \t\r\n] -> channel(HIDDEN);
COMMENT: '/*' .*? '*/' -> skip;
LINE_COMMENT: '//' ~[\r\n]* -> skip;