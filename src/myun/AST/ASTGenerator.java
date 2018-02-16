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
        super();
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
        CharStream fileStream = new ANTLRFileStream(fileName);
        return parse(fileStream);
    }

    /**
     * Parses a string of Myun code.
     *
     * @param code the myun code
     * @return an AST representing the program code
     * @noinspection ElementOnlyUsedFromTestCode
     */
    public ASTCompileUnit parseString(String code) {
        return parse(new ANTLRInputStream(code));
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

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object o, int i, int i1, String s, RecognitionException e) {
        throw new ParserException("Parse Error: Expected " +
                e.getExpectedTokens().toString(MyunParser.VOCABULARY) +
                " but found \"" + e.getOffendingToken().getText() + "\" on line " +
                e.getOffendingToken().getLine() + " at " + e.getOffendingToken().getCharPositionInLine());
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
            return new ASTCompileUnit(ctx.start.getLine(), ctx.start.getCharPositionInLine(), funcDefs, script);
        }
    }

    private static class ScriptVisitor extends MyunBaseVisitor<ASTScript> {
        @Override
        public ASTScript visitScript(ScriptContext ctx) {
            ASTBlock block = ctx.block().accept(new BlockVisitor());
            return new ASTScript(ctx.start.getLine(), ctx.start.getCharPositionInLine(), ctx.name.getText(), block);
        }
    }

    private static class BlockVisitor extends MyunBaseVisitor<ASTBlock> {
        @Override
        public ASTBlock visitBlock(BlockContext ctx) {
            List<ASTStatement> statements = ctx.statement().stream().
                    map(stmt -> stmt.accept(new StatementVisitor())).
                    collect(Collectors.toList());
            ASTFuncReturn funcReturn = null;
            if (null != ctx.funcReturn()) {
                funcReturn = new ASTFuncReturn(ctx.funcReturn().start.getLine(),
                        ctx.funcReturn().start.getCharPositionInLine(),
                        ctx.funcReturn().expr().accept(new ExprVisitor()));
            }
            ASTLoopBreak loopBreak = null;
            if (null != ctx.loopBreak()) {
                loopBreak = new ASTLoopBreak(ctx.loopBreak().start.getLine(), ctx.loopBreak().start
                        .getCharPositionInLine());
            }
            return new ASTBlock(ctx.start.getLine(), ctx.start.getCharPositionInLine(), statements, funcReturn,
                    loopBreak);
        }
    }

    private static class StatementVisitor extends MyunBaseVisitor<ASTStatement> {
        @Override
        public ASTStatement visitStatement(StatementContext ctx) {
            if (null != ctx.declaration()) {
                return ctx.declaration().accept(new DeclarationVisitor());
            }
            if (null != ctx.assignment()) {
                return ctx.assignment().accept(new AssignmentVisitor());
            }
            if (null != ctx.branch()) {
                return ctx.branch().accept(new BranchVisitor());
            }
            if (null != ctx.loop()) {
                return ctx.loop().accept(new LoopVisitor());
            }
            throw new RuntimeException("Unknown statement on line " +
                    ctx.start.getLine() + " at " + ctx.start.getCharPositionInLine() + ": " + ctx.getText());
        }
    }

    private static class FuncDefVisitor extends MyunBaseVisitor<ASTFuncDef> {
        @Override
        public ASTFuncDef visitFuncDef(FuncDefContext ctx) {
            String name = ctx.name.getText();

            List<ASTVariable> params = ctx.funcParam().stream().map(param -> {
                ASTVariable var = param.variable().accept(new VariableVisitor());
                if (null != param.type()) {
                    var.setType(param.type().accept(new TypeVisitor()));
                }
                return var;
            }).collect(Collectors.toList());

            ASTType returnType = null;
            if (null != ctx.returnType) {
                returnType = ctx.returnType.accept(new TypeVisitor());
            }

            ASTBlock block = ctx.block().accept(new BlockVisitor());

            return new ASTFuncDef(ctx.start.getLine(), ctx.start.getCharPositionInLine(), name, params, returnType,
                    block);
        }
    }

    private static class DeclarationVisitor extends MyunBaseVisitor<ASTDeclaration> {
        @Override
        public ASTDeclaration visitDeclaration(DeclarationContext ctx) {
            ASTVariable var = ctx.variable().accept(new VariableVisitor());
            ASTExpression expr = ctx.expr().accept(new ExprVisitor());
            return new ASTDeclaration(ctx.start.getLine(), ctx.start.getCharPositionInLine(), var, expr);
        }
    }

    private static class AssignmentVisitor extends MyunBaseVisitor<ASTAssignment> {
        @Override
        public ASTAssignment visitAssignment(AssignmentContext ctx) {
            ASTVariable var = ctx.variable().accept(new VariableVisitor());
            ASTExpression expr = ctx.expr().accept(new ExprVisitor());
            return new ASTAssignment(ctx.start.getLine(), ctx.start.getCharPositionInLine(), var, expr);
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

            return new ASTBranch(ctx.start.getLine(), ctx.start.getCharPositionInLine(), conditions, blocks);
        }
    }

    private static class LoopVisitor extends MyunBaseVisitor<ASTStatement> {
        @Override
        public ASTWhileLoop visitWhileLoop(WhileLoopContext ctx) {
            ASTExpression condition = ctx.expr().accept(new ExprVisitor());
            ASTBlock block = ctx.block().accept(new BlockVisitor());
            return new ASTWhileLoop(ctx.start.getLine(), ctx.start.getCharPositionInLine(), condition, block);
        }

        @Override
        public ASTForLoop visitForLoop(ForLoopContext ctx) {
            ASTVariable variable = ctx.variable().accept(new VariableVisitor());
            ASTExpression from = ctx.from.accept(new ExprVisitor());
            ASTExpression to = ctx.to.accept(new ExprVisitor());
            ASTBlock block = ctx.block().accept(new BlockVisitor());
            return new ASTForLoop(ctx.start.getLine(), ctx.start.getCharPositionInLine(), variable, from, to, block);
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
            if (null != ctx.bool()) {
                return new ASTConstant<>(ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                        "true".equals(ctx.bool().getText()));
            }
            if (null != ctx.integer()) {
                return new ASTConstant<>(ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                        Integer.parseInt(ctx.integer().getText()));
            }
            if (null != ctx.floating()) {
                return new ASTConstant<>(ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                        Float.parseFloat(ctx.floating().getText()));
            }
            if (null != ctx.variable()) {
                return ctx.variable().accept(new VariableVisitor());
            }
            throw new RuntimeException("Unknown basic expression type.");
        }

        @Override
        public ASTExpression visitOperatorExpr(OperatorExprContext ctx) {
            ASTExpression left = ctx.left.accept(this);
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
                case MyunLexer.OP_EXP:
                    op = "exp";
                    break;
                case MyunLexer.OP_MOD:
                    op = "mod";
                    break;
                default:
                    throw new RuntimeException("Unknown operator expression.");
            }
            ASTExpression right = ctx.right.accept(this);
            return new ASTFuncCall(ctx.start.getLine(), ctx.start.getCharPositionInLine(), op, left, right);
        }

        @Override
        public ASTExpression visitPrefixExpr(PrefixExprContext ctx) {
            String op;
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
            ASTExpression expr = ctx.expr().accept(new ExprVisitor());
            return new ASTFuncCall(ctx.start.getLine(), ctx.start.getCharPositionInLine(), op, expr);
        }
    }

    private static class FuncCallVisitor extends MyunBaseVisitor<ASTFuncCall> {
        @Override
        public ASTFuncCall visitFuncCall(FuncCallContext ctx) {
            List<ASTExpression> args = ctx.expr().stream().
                    map(arg -> arg.accept(new ExprVisitor())).
                    collect(Collectors.toList());
            return new ASTFuncCall(ctx.start.getLine(), ctx.start.getCharPositionInLine(), ctx.func.getText(), args);
        }
    }

    private static class VariableVisitor extends MyunBaseVisitor<ASTVariable> {
        @Override
        public ASTVariable visitVariable(VariableContext ctx) {
            String name = ctx.name.getText();
            return new ASTVariable(ctx.start.getLine(), ctx.start.getCharPositionInLine(), name);
        }
    }

    private static class TypeVisitor extends MyunBaseVisitor<ASTType> {
        @Override
        public ASTType visitParenthesisType(ParenthesisTypeContext ctx) {
            return ctx.type().accept(this);
        }

        @Override
        public ASTType visitBasicType(BasicTypeContext ctx) {
            String name = ctx.name.getText();
            return new ASTBasicType(ctx.start.getLine(), ctx.start.getCharPositionInLine(), name);
        }

        @Override
        public ASTType visitFuncType(FuncTypeContext ctx) {
            List<ASTType> types = ctx.type().stream().map(t -> t.accept(this)).collect(Collectors.toList());
            ASTType returnType = types.get(types.size() - 1);
            types.remove(types.size() - 1);
            return new ASTFuncType(ctx.start.getLine(), ctx.start.getCharPositionInLine(), types, returnType);
        }
    }
}
