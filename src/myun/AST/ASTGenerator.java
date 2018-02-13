package myun.AST;

import myun.AST.constraints.ConstraintChecker;
import myun.AST.constraints.ViolatedConstraintException;
import org.antlr.v4.runtime.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates the AST structure from the ANTLR parser.
 */
public class ASTGenerator {
    // to check context-sensitive constraints
    private ConstraintChecker constraintChecker;

    public ASTGenerator() {
        constraintChecker = new ConstraintChecker();
    }

    /**
     * Parses a file with Myun code.
     *
     * @param fileName the file name
     * @return an AST representing the program code
     * @throws IOException                 thrown when the file could not be loaded
     * @throws ViolatedConstraintException thrown when a constraint is violated
     */
    public ASTCompileUnit parseFile(String fileName) throws IOException, ViolatedConstraintException {
        ANTLRFileStream fileStream;
        fileStream = new ANTLRFileStream(fileName);
        return parse(fileStream);
    }

    /**
     * Parses a char stream of Myun code.
     *
     * @param charStream the char stream
     * @return an AST representing the program code
     * @throws ViolatedConstraintException thrown when a constraint is violated
     */
    private ASTCompileUnit parse(CharStream charStream) throws ViolatedConstraintException {
        MyunLexer lexer = new MyunLexer(charStream);
        TokenStream tokens = new CommonTokenStream(lexer);
        MyunParser parser = new MyunParser(tokens);
        ASTCompileUnit compileUnit = new CompileUnitVisitor().visit(parser.compileUnit());
        constraintChecker.check(compileUnit);
        return compileUnit;
    }

    private static class CompileUnitVisitor extends MyunBaseVisitor<ASTCompileUnit> {
        @Override
        public ASTCompileUnit visitCompileUnit(MyunParser.CompileUnitContext ctx) {
            List<ASTFuncDef> funcDefs = ctx.funcDef().stream().
                    map(funcDef -> funcDef.accept(new FuncDefVisitor())).
                    collect(Collectors.toList());
            ASTScript script = ctx.script().accept(new ScriptVisitor());
            return new ASTCompileUnit(ctx.start.getLine(), ctx.start.getCharPositionInLine(), funcDefs, script);
        }
    }

    private static class ScriptVisitor extends MyunBaseVisitor<ASTScript> {
        @Override
        public ASTScript visitScript(MyunParser.ScriptContext ctx) {
            ASTBlock block = ctx.block().accept(new BlockVisitor());
            return new ASTScript(ctx.start.getLine(), ctx.start.getCharPositionInLine(), ctx.name.getText(), block);
        }
    }

    private static class BlockVisitor extends MyunBaseVisitor<ASTBlock> {
        @Override
        public ASTBlock visitBlock(MyunParser.BlockContext ctx) {
            List<ASTStatement> statements = ctx.statement().stream().
                    map(stmt -> stmt.accept(new StatementVisitor())).
                    collect(Collectors.toList());
            ASTFuncReturn funcReturn = null;
            if (ctx.funcReturn() != null) {
                funcReturn = new ASTFuncReturn(ctx.funcReturn().start.getLine(),
                        ctx.funcReturn().start.getCharPositionInLine(),
                        ctx.funcReturn().expr().accept(new ExprVisitor()));
            }
            ASTLoopBreak loopBreak = null;
            if (ctx.loopBreak() != null) {
                loopBreak = new ASTLoopBreak(ctx.loopBreak().start.getLine(), ctx.loopBreak().start
                        .getCharPositionInLine());
            }
            return new ASTBlock(ctx.start.getLine(), ctx.start.getCharPositionInLine(), statements, funcReturn,
                    loopBreak);
        }
    }

    private static class StatementVisitor extends MyunBaseVisitor<ASTStatement> {
        @Override
        public ASTStatement visitStatement(MyunParser.StatementContext ctx) {
            if (ctx.assignment() != null) {
                return ctx.assignment().accept(new AssignmentVisitor());
            } else if (ctx.branch() != null) {
                return ctx.branch().accept(new BranchVisitor());
            } else if (ctx.loop() != null) {
                return ctx.loop().accept(new LoopVisitor());
            } else {
                return null;
            }
        }
    }

    private static class FuncDefVisitor extends MyunBaseVisitor<ASTFuncDef> {
        @Override
        public ASTFuncDef visitFuncDef(MyunParser.FuncDefContext ctx) {
            String name = ctx.name.getText();

            List<ASTVariable> params = ctx.funcParam().stream().map(param -> {
                ASTVariable var = param.variable().accept(new VariableVisitor());
                if (param.type() != null) {
                    var.setType(param.type().accept(new TypeVisitor()));
                }
                return var;
            }).collect(Collectors.toList());

            ASTType returnType = null;
            if (ctx.returnType != null) {
                returnType = ctx.returnType.accept(new TypeVisitor());
            }

            ASTBlock block = ctx.block().accept(new BlockVisitor());

            return new ASTFuncDef(ctx.start.getLine(), ctx.start.getCharPositionInLine(), name, params, returnType,
                    block);
        }
    }

    private static class AssignmentVisitor extends MyunBaseVisitor<ASTAssignment> {
        @Override
        public ASTAssignment visitAssignment(MyunParser.AssignmentContext ctx) {
            ASTVariable var = ctx.variable().accept(new VariableVisitor());
            ASTExpression expr = ctx.expr().accept(new ExprVisitor());
            return new ASTAssignment(ctx.start.getLine(), ctx.start.getCharPositionInLine(), var, expr);
        }
    }

    private static class BranchVisitor extends MyunBaseVisitor<ASTBranch> {
        @Override
        public ASTBranch visitBranch(MyunParser.BranchContext ctx) {
            List<ASTExpression> conditions = ctx.expr().stream().
                    map(expr -> expr.accept(new ExprVisitor())).
                    collect(Collectors.toList());
            List<ASTBlock> blocks = ctx.block().stream().
                    map(block -> block.accept(new BlockVisitor())).
                    collect(Collectors.toList());

            return new ASTBranch(ctx.start.getLine(), ctx.start.getCharPositionInLine(), conditions, blocks);
        }
    }

    private static class LoopVisitor extends MyunBaseVisitor<ASTStatement> {
        @Override
        public ASTWhileLoop visitWhileLoop(MyunParser.WhileLoopContext ctx) {
            ASTExpression condition = ctx.expr().accept(new ExprVisitor());
            ASTBlock block = ctx.block().accept(new BlockVisitor());
            return new ASTWhileLoop(ctx.start.getLine(), ctx.start.getCharPositionInLine(), condition, block);
        }

        @Override
        public ASTForLoop visitForLoop(MyunParser.ForLoopContext ctx) {
            ASTVariable variable = ctx.variable().accept(new VariableVisitor());
            ASTExpression from = ctx.from.accept(new ExprVisitor());
            ASTExpression to = ctx.to.accept(new ExprVisitor());
            ASTBlock block = ctx.block().accept(new BlockVisitor());
            return new ASTForLoop(ctx.start.getLine(), ctx.start.getCharPositionInLine(), variable, from, to, block);
        }
    }

    private static class ExprVisitor extends MyunBaseVisitor<ASTExpression> {
        @Override
        public ASTExpression visitParenthesisExpr(MyunParser.ParenthesisExprContext ctx) {
            return ctx.expr().accept(this);
        }

        @Override
        public ASTExpression visitFuncCall(MyunParser.FuncCallContext ctx) {
            return ctx.accept(new FuncCallVisitor());
        }

        @Override
        public ASTExpression visitBasic(MyunParser.BasicContext ctx) {
            if (ctx.bool() != null) {
                return new ASTConstant<>(ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                        ctx.bool().getText().equals("true"));
            } else if (ctx.integer() != null) {
                return new ASTConstant<>(ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                        Integer.parseInt(ctx.integer().getText()));
            } else if (ctx.floating() != null) {
                return new ASTConstant<>(ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                        Float.parseFloat(ctx.floating().getText()));
            } else if (ctx.variable() != null) {
                return ctx.variable().accept(new VariableVisitor());
            } else {
                throw new RuntimeException("Unknown basic expression type.");
            }
        }

        @Override
        public ASTExpression visitOperatorExpr(MyunParser.OperatorExprContext ctx) {
            ASTExpression left = ctx.left.accept(this);
            String op = null;
            switch (ctx.op.getType()) {
                case MyunLexer.OP_AND:
                    op = "and";
                    break;
                case MyunLexer.OP_OR:
                    op = "or";
                    break;
                case MyunLexer.OP_EQ:
                    op = "is";
                    break;
                case MyunLexer.OP_LT:
                    op = "isLess";
                    break;
                case MyunLexer.OP_LEQ:
                    op = "isLessEq";
                    break;
                case MyunLexer.OP_GT:
                    op = "isGreater";
                    break;
                case MyunLexer.OP_GEQ:
                    op = "isGreaterEq";
                    break;
                case MyunLexer.OP_ADD:
                    op = "plus";
                    break;
                case MyunLexer.OP_SUB:
                    op = "minus";
                    break;
                case MyunLexer.OP_MUL:
                    op = "mult";
                    break;
                case MyunLexer.OP_DIV:
                    op = "div";
                    break;
                case MyunLexer.OP_EXP:
                    op = "exp";
                    break;
                case MyunLexer.OP_MOD:
                    op = "mod";
                    break;
                default:
                    throw new RuntimeException("Unknown operator expression.");
            }
            ASTVariable opVar = new ASTVariable(ctx.op.getLine(), ctx.op.getCharPositionInLine(), op);
            ASTExpression right = ctx.right.accept(this);
            return new ASTFuncCall(ctx.start.getLine(), ctx.start.getCharPositionInLine(), opVar, left, right);
        }

        @Override
        public ASTExpression visitPrefixExpr(MyunParser.PrefixExprContext ctx) {
            String op = null;
            switch (ctx.prefix.getType()) {
                case MyunLexer.OP_NOT:
                    op = "not";
                    break;
                case MyunLexer.OP_SUB:
                    op = "negate";
                    break;
                default:
                    throw new RuntimeException("Unknwon prefix expression.");
            }
            ASTVariable opVar = new ASTVariable(ctx.prefix.getLine(), ctx.prefix.getCharPositionInLine(), op);
            ASTExpression expr = ctx.expr().accept(new ExprVisitor());
            return new ASTFuncCall(ctx.start.getLine(), ctx.start.getCharPositionInLine(), opVar, expr);
        }
    }

    private static class FuncCallVisitor extends MyunBaseVisitor<ASTFuncCall> {
        @Override
        public ASTFuncCall visitFuncCall(MyunParser.FuncCallContext ctx) {
            ASTVariable var = ctx.variable().accept(new VariableVisitor());
            List<ASTExpression> args = ctx.expr().stream().
                    map(arg -> arg.accept(new ExprVisitor())).
                    collect(Collectors.toList());
            return new ASTFuncCall(ctx.start.getLine(), ctx.start.getCharPositionInLine(), var, args);
        }
    }

    private static class VariableVisitor extends MyunBaseVisitor<ASTVariable> {
        @Override
        public ASTVariable visitVariable(MyunParser.VariableContext ctx) {
            String name = ctx.name.getText();
            return new ASTVariable(ctx.start.getLine(), ctx.start.getCharPositionInLine(), name);
        }
    }

    private static class TypeVisitor extends MyunBaseVisitor<ASTType> {
        @Override
        public ASTType visitParenthesisType(MyunParser.ParenthesisTypeContext ctx) {
            return ctx.type().accept(this);
        }

        @Override
        public ASTType visitBasicType(MyunParser.BasicTypeContext ctx) {
            String name = ctx.name.getText();
            return new ASTBasicType(ctx.start.getLine(), ctx.start.getCharPositionInLine(), name);
        }

        @Override
        public ASTType visitFuncType(MyunParser.FuncTypeContext ctx) {
            List<ASTType> types = ctx.type().stream().map(t -> t.accept(this)).collect(Collectors.toList());
            ASTType returnType = types.get(types.size() - 1);
            types.remove(types.size() - 1);
            return new ASTFuncType(ctx.start.getLine(), ctx.start.getCharPositionInLine(), types, returnType);
        }
    }
}
