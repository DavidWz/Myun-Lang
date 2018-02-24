package myun.AST;

import myun.AST.MyunParser.AssignmentContext;
import myun.AST.MyunParser.BasicContext;
import myun.AST.MyunParser.BasicTypeContext;
import myun.AST.MyunParser.BlockContext;
import myun.AST.MyunParser.BranchContext;
import myun.AST.MyunParser.CompileUnitContext;
import myun.AST.MyunParser.DeclarationContext;
import myun.AST.MyunParser.ForLoopContext;
import myun.AST.MyunParser.FuncCallContext;
import myun.AST.MyunParser.FuncDefContext;
import myun.AST.MyunParser.FuncTypeContext;
import myun.AST.MyunParser.OperatorExprContext;
import myun.AST.MyunParser.ParenthesisExprContext;
import myun.AST.MyunParser.ParenthesisTypeContext;
import myun.AST.MyunParser.PrefixExprContext;
import myun.AST.MyunParser.ScriptContext;
import myun.AST.MyunParser.StatementContext;
import myun.AST.MyunParser.VariableContext;
import myun.AST.MyunParser.WhileLoopContext;
import myun.AST.constraints.ConstraintChecker;
import myun.type.BasicType;
import myun.type.FuncType;
import myun.type.MyunType;
import myun.type.UnknownType;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import java.io.IOException;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates the AST structure from the ANTLR parser.
 */
public class ASTGenerator implements ANTLRErrorListener {
    // to check context-sensitive constraints
    private final ConstraintChecker constraintChecker;

    public ASTGenerator() {
        constraintChecker = new ConstraintChecker();
    }

    /**
     * Parses a file with Myun code.
     *
     * @param fileName the file name
     * @return an AST representing the program code
     * @throws IOException                 thrown when the file could not be loaded
     */
    public ASTCompileUnit parseFile(String fileName) throws IOException {
        CharStream fileStream = CharStreams.fromFileName(fileName);
        return parse(fileStream);
    }

    /**
     * Parses a char stream of Myun code.
     *
     * @param charStream the char stream
     * @return an AST representing the program code
     */
    private ASTCompileUnit parse(CharStream charStream) {
        MyunLexer lexer = new MyunLexer(charStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(this);

        TokenStream tokens = new CommonTokenStream(lexer);

        MyunParser parser = new MyunParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(this);

        ASTCompileUnit compileUnit = new CompileUnitVisitor().visit(parser.compileUnit());
        constraintChecker.check(compileUnit);
        return compileUnit;
    }

    /**
     * Encapsules the line and position of the given token in an SourcePosition object.
     * @param token the token
     * @return the SourcePosition object
     */
    private static SourcePosition getSourcePos(Token token) {
        return new SourcePosition(token.getLine(), token.getCharPositionInLine());
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object o, int line, int pos, String s, RecognitionException e) {
        throw new ParserException(s, new SourcePosition(line, pos));
    }

    @Override
    public void reportAmbiguity(Parser parser, DFA dfa, int i, int i1, boolean b, BitSet bitSet, ATNConfigSet
            atnConfigSet) {
    }

    @Override
    public void reportAttemptingFullContext(Parser parser, DFA dfa, int i, int i1, BitSet bitSet, ATNConfigSet
            atnConfigSet) {
    }

    @Override
    public void reportContextSensitivity(Parser parser, DFA dfa, int i, int i1, int i2, ATNConfigSet atnConfigSet) {
    }

    private static class CompileUnitVisitor extends MyunBaseVisitor<ASTCompileUnit> {
        @Override
        public ASTCompileUnit visitCompileUnit(CompileUnitContext ctx) {
            List<ASTFuncDef> funcDefs = ctx.funcDef().stream().
                    map(funcDef -> funcDef.accept(new FuncDefVisitor())).
                    collect(Collectors.toList());
            ASTScript script = ctx.script().accept(new ScriptVisitor());
            return new ASTCompileUnit(getSourcePos(ctx.start), funcDefs, script);
        }
    }

    private static class ScriptVisitor extends MyunBaseVisitor<ASTScript> {
        @Override
        public ASTScript visitScript(ScriptContext ctx) {
            ASTBlock block = ctx.block().accept(new BlockVisitor());
            return new ASTScript(getSourcePos(ctx.start), ctx.name.getText(), block);
        }
    }

    private static class BlockVisitor extends MyunBaseVisitor<ASTBlock> {
        @Override
        public ASTBlock visitBlock(BlockContext ctx) {
            List<ASTStatement> statements = ctx.statement().stream().
                    map(stmt -> stmt.accept(new StatementVisitor())).
                    collect(Collectors.toList());
            ASTFuncReturn funcReturn = null;
            if (ctx.funcReturn() != null) {
                funcReturn = new ASTFuncReturn(getSourcePos(ctx.funcReturn().start),
                        ctx.funcReturn().expr().accept(new ExprVisitor()));
            }
            ASTLoopBreak loopBreak = null;
            if (ctx.loopBreak() != null) {
                loopBreak = new ASTLoopBreak(getSourcePos(ctx.loopBreak().start));
            }
            return new ASTBlock(getSourcePos(ctx.start), statements, funcReturn, loopBreak);
        }
    }

    private static class StatementVisitor extends MyunBaseVisitor<ASTStatement> {
        @Override
        public ASTStatement visitStatement(StatementContext ctx) {
            if (ctx.declaration() != null) {
                return ctx.declaration().accept(new DeclarationVisitor());
            }
            if (ctx.assignment() != null) {
                return ctx.assignment().accept(new AssignmentVisitor());
            }
            if (ctx.branch() != null) {
                return ctx.branch().accept(new BranchVisitor());
            }
            if (ctx.loop() != null) {
                return ctx.loop().accept(new LoopVisitor());
            }
            if (ctx.funcCall() != null) {
                return new ASTProcCall(ctx.funcCall().accept(new FuncCallVisitor()));
            }
            throw new ParserException("Unknown statement " + ctx.getText(), getSourcePos(ctx.start));
        }
    }

    private static class FuncDefVisitor extends MyunBaseVisitor<ASTFuncDef> {
        @Override
        public ASTFuncDef visitFuncDef(FuncDefContext ctx) {
            String name = ctx.name.getText();

            List<ASTVariable> params = ctx.funcParam().stream().map(param -> {
                ASTVariable var = param.variable().accept(new VariableVisitor());
                if (param.type() != null) {
                    var.setType(param.type().accept(new TypeVisitor()));
                }
                return var;
            }).collect(Collectors.toList());

            MyunType returnType = new UnknownType();
            if (ctx.returnType != null) {
                returnType = ctx.returnType.accept(new TypeVisitor());
            }

            ASTBlock block = ctx.block().accept(new BlockVisitor());

            return new ASTFuncDef(getSourcePos(ctx.start), name, params, returnType, block);
        }
    }

    private static class DeclarationVisitor extends MyunBaseVisitor<ASTDeclaration> {
        @Override
        public ASTDeclaration visitDeclaration(DeclarationContext ctx) {
            ASTVariable var = ctx.variable().accept(new VariableVisitor());
            ASTExpression expr = ctx.expr().accept(new ExprVisitor());
            return new ASTDeclaration(getSourcePos(ctx.start), var, expr);
        }
    }

    private static class AssignmentVisitor extends MyunBaseVisitor<ASTAssignment> {
        @Override
        public ASTAssignment visitAssignment(AssignmentContext ctx) {
            ASTVariable var = ctx.variable().accept(new VariableVisitor());
            ASTExpression expr = ctx.expr().accept(new ExprVisitor());
            return new ASTAssignment(getSourcePos(ctx.start), var, expr);
        }
    }

    private static class BranchVisitor extends MyunBaseVisitor<ASTBranch> {
        @Override
        public ASTBranch visitBranch(BranchContext ctx) {
            List<ASTExpression> conditions = ctx.expr().stream().
                    map(expr -> expr.accept(new ExprVisitor())).
                    collect(Collectors.toList());
            List<ASTBlock> blocks = ctx.block().stream().
                    map(block -> block.accept(new BlockVisitor())).
                    collect(Collectors.toList());

            return new ASTBranch(getSourcePos(ctx.start), conditions, blocks);
        }
    }

    private static class LoopVisitor extends MyunBaseVisitor<ASTStatement> {
        @Override
        public ASTWhileLoop visitWhileLoop(WhileLoopContext ctx) {
            ASTExpression condition = ctx.expr().accept(new ExprVisitor());
            ASTBlock block = ctx.block().accept(new BlockVisitor());
            return new ASTWhileLoop(getSourcePos(ctx.start), condition, block);
        }

        @Override
        public ASTForLoop visitForLoop(ForLoopContext ctx) {
            ASTVariable variable = ctx.variable().accept(new VariableVisitor());
            ASTExpression from = ctx.from.accept(new ExprVisitor());
            ASTExpression to = ctx.to.accept(new ExprVisitor());
            ASTBlock block = ctx.block().accept(new BlockVisitor());
            return new ASTForLoop(getSourcePos(ctx.start), variable, from, to, block);
        }
    }

    private static class ExprVisitor extends MyunBaseVisitor<ASTExpression> {
        @Override
        public ASTExpression visitParenthesisExpr(ParenthesisExprContext ctx) {
            return ctx.expr().accept(this);
        }

        @Override
        public ASTExpression visitFuncCall(FuncCallContext ctx) {
            return ctx.accept(new FuncCallVisitor());
        }

        @Override
        public ASTExpression visitBasic(BasicContext ctx) {
            if (ctx.bool() != null) {
                return new ASTConstant<>(getSourcePos(ctx.start),
                        "true".equals(ctx.bool().getText()));
            }
            if (ctx.integer() != null) {
                return new ASTConstant<>(getSourcePos(ctx.start),
                        Integer.parseInt(ctx.integer().getText()));
            }
            if (ctx.floating() != null) {
                return new ASTConstant<>(getSourcePos(ctx.start),
                        Float.parseFloat(ctx.floating().getText()));
            }
            if (ctx.variable() != null) {
                return ctx.variable().accept(new VariableVisitor());
            }
            throw new ParserException("Unknown basic expression " + ctx.getText(), getSourcePos(ctx.start));
        }

        /** @noinspection OverlyComplexMethod, OverlyLongMethod */
        @Override
        public ASTExpression visitOperatorExpr(OperatorExprContext ctx) {
            ASTExpression left = ctx.left.accept(this);
            SourcePosition sourcePos = getSourcePos(ctx.start);
            String op;
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
                case MyunLexer.OP_MOD:
                    op = "mod";
                    break;
                default:
                    throw new ParserException("Unknown operator " + ctx.op.getText(), sourcePos);
            }
            ASTExpression right = ctx.right.accept(this);
            return new ASTFuncCall(sourcePos, op, left, right);
        }

        @Override
        public ASTExpression visitPrefixExpr(PrefixExprContext ctx) {
            SourcePosition sourcePos = getSourcePos(ctx.start);
            String op;
            switch (ctx.prefix.getType()) {
                case MyunLexer.OP_NOT:
                    op = "not";
                    break;
                case MyunLexer.OP_SUB:
                    op = "negate";
                    break;
                default:
                    throw new ParserException("Unknwon prefix expression " + ctx.prefix.getText(), sourcePos);
            }
            ASTExpression expr = ctx.expr().accept(new ExprVisitor());
            return new ASTFuncCall(sourcePos, op, expr);
        }
    }

    private static class FuncCallVisitor extends MyunBaseVisitor<ASTFuncCall> {
        @Override
        public ASTFuncCall visitFuncCall(FuncCallContext ctx) {
            List<ASTExpression> args = ctx.expr().stream().
                    map(arg -> arg.accept(new ExprVisitor())).
                    collect(Collectors.toList());
            return new ASTFuncCall(getSourcePos(ctx.start), ctx.func.getText(), args);
        }
    }

    private static class VariableVisitor extends MyunBaseVisitor<ASTVariable> {
        @Override
        public ASTVariable visitVariable(VariableContext ctx) {
            String name = ctx.name.getText();
            return new ASTVariable(getSourcePos(ctx.start), name);
        }
    }

    private static class TypeVisitor extends MyunBaseVisitor<MyunType> {
        @Override
        public MyunType visitParenthesisType(ParenthesisTypeContext ctx) {
            return ctx.type().accept(this);
        }

        @Override
        public MyunType visitBasicType(BasicTypeContext ctx) {
            String name = ctx.name.getText();
            return new BasicType(name);
        }

        @Override
        public MyunType visitFuncType(FuncTypeContext ctx) {
            List<MyunType> types = ctx.type().stream().map(t -> t.accept(this)).collect(Collectors.toList());
            MyunType returnType = types.get(types.size() - 1);
            types.remove(types.size() - 1);
            return new FuncType(types, returnType);
        }
    }
}
