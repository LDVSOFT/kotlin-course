package ru.spbau.mit

val loopFactorialProgram = Program(Block(
        FunctionDefinition("factorial", listOf("n"), Block(
                VariableDefinition("x", 1.l),
                VariableDefinition("i", 1.l),
                WhileStatement(BinaryExpression("i".v, "n".v, Operator.LEQ), Block(
                        AssignmentStatement("x", BinaryExpression("x".v, "i".v, Operator.MUL)),
                        AssignmentStatement("i", BinaryExpression("i".v, 1.l, Operator.ADD))
                )),
                ReturnStatement("x".v)
        )),
        VariableDefinition("i", 1.l),
        WhileStatement(BinaryExpression("i".v, 5.l, Operator.LEQ), Block(
                ExpressionStatement(FunctionCall("log",
                        FunctionCall("factorial", "i".v)
                )),
                AssignmentStatement("i", BinaryExpression("i".v, 1.l, Operator.ADD))
        ))
))

val recursiveFactorialProgram = Program(Block(
        FunctionDefinition("factorial", listOf("n"), Block(
                IfStatement(BinaryExpression("n".v, 1.l, Operator.LEQ), Block(
                        ReturnStatement(1.l)
                )),
                ReturnStatement(BinaryExpression("n".v, FunctionCall("factorial",
                        BinaryExpression("n".v, 1.l, Operator.SUB)
                ), Operator.MUL))
        )),
        VariableDefinition("i", 1.l),
        WhileStatement(BinaryExpression("i".v, 5.l, Operator.LEQ), Block(
                ExpressionStatement(FunctionCall("log",
                        FunctionCall("factorial", "i".v)
                )),
                AssignmentStatement("i", BinaryExpression("i".v, 1.l, Operator.ADD))
        ))
))

val scopeSumProgram = Program(Block(
        VariableDefinition("n", 0.l),
        VariableDefinition("m"),
        IfStatement(1.l, Block(
                VariableDefinition("n", 4.l),
                AssignmentStatement("m", 5.l)
        ), Block(
                ExpressionStatement(FunctionCall("log", 999.l)),
                AssignmentStatement("m", 11111.l)
        )),
        WhileStatement(BinaryExpression("m".v, 6.l, Operator.LET), Block(
                AssignmentStatement("m", BinaryExpression("m".v, 1.l, Operator.ADD)),
                AssignmentStatement("n", BinaryExpression("n".v, "m".v, Operator.ADD)),
                VariableDefinition("m", 1000.l),
                AssignmentStatement("n", BinaryExpression("n".v, "m".v, Operator.SUB))
        )),
        ExpressionStatement(FunctionCall("log", "n".v)),
        ExpressionStatement(FunctionCall("log", "m".v))
))