package myun.AST;

import myun.type.*;

/**
 * Prints the AST in a readable format.
 */
public class MyunPrettyPrinter implements ASTVisitor<String>, TypeVisitor<String> {
    private int indentLevel;
    private boolean printTypes;

    public MyunPrettyPrinter() {
        init();
        printTypes = false;
    }

    private void init() {
        indentLevel = 0;
        printTypes = false;
    }

    public String toString(ASTNode node) {
        init();
        return node.accept(this);
    }

    public String debug(ASTNode node) {
        init();
        printTypes = true;
        return node.accept(this);
    }

    public String toString(MyunType node) {
        init();
        return node.accept(this);
    }

    /**
     * Adds tabs to the current prettyPrint to keep the current indent level.
     */
    private void indent(StringBuilder sb) {
        for (int i = 0; i < indentLevel; i++) {
            sb.append('\t');
        }
    }

    @Override
    public String visit(ASTAssignment node) {
        StringBuilder sb = new StringBuilder();
        indent(sb);

        sb.append(node.getVariable().accept(this));
        sb.append(" = ");
        sb.append(node.getExpr().accept(this));
        sb.append('\n');

        return sb.toString();
    }

    @Override
    public String visit(BasicType node) {
        return node.getName();
    }

    @Override
    public String visit(ASTBlock node) {
        indentLevel++;
        StringBuilder sb = new StringBuilder();
        node.getStatements().forEach(stmt -> sb.append(stmt.accept(this)));
        node.getFuncReturn().ifPresent(fR -> sb.append(fR.accept(this)));
        node.getLoopBreak().ifPresent(lB -> sb.append(lB.accept(this)));
        indentLevel--;
        return sb.toString();
    }

    @Override
    public String visit(ASTBranch node) {
        StringBuilder sb = new StringBuilder();

        // print the if condition
        indent(sb);
        sb.append("if ").append(node.getConditions().get(0).accept(this)).append(" then\n");
        sb.append(node.getBlocks().get(0).accept(this));

        // print the elseif conditions
        for (int i = 1; i < node.getConditions().size(); i++) {
            indent(sb);
            sb.append("elseif ").append(node.getConditions().get(i).accept(this)).append(" then\n");
            sb.append(node.getBlocks().get(i).accept(this));
        }

        // print the else
        node.getElseBlock().ifPresent(b -> {
            indent(sb);
            sb.append("else\n").append(b.accept(this));
        });
        indent(sb);
        sb.append("end\n");

        return sb.toString();
    }

    @Override
    public String visit(ASTCompileUnit node) {
        StringBuilder sb = new StringBuilder();
        node.getFuncDefs().forEach(funcDef -> sb.append(funcDef.accept(this)).append('\n'));
        sb.append(node.getScript().accept(this));
        return sb.toString();
    }

    @Override
    public <CT> String visit(ASTConstant<CT> node) {
        String result = node.getValue().toString();
        if (printTypes) {
            result += "::" + node.getType().accept(this);
        }
        return result;
    }

    @Override
    public String visit(ASTDeclaration node) {
        StringBuilder sb = new StringBuilder();
        indent(sb);

        sb.append(node.getVariable().accept(this));
        sb.append(" := ");
        sb.append(node.getExpr().accept(this));
        sb.append('\n');

        return sb.toString();
    }

    @Override
    public String visit(ASTForLoop node) {
        StringBuilder sb = new StringBuilder();
        indent(sb);

        sb.append("for ").append(node.getVariable().accept(this));
        sb.append(" from ").append(node.getFrom().accept(this));
        sb.append(" to ").append(node.getTo().accept(this));
        sb.append(" do\n").append(node.getBlock().accept(this));
        indent(sb);
        sb.append("end\n");
        return sb.toString();
    }

    @Override
    public String visit(ASTFuncCall node) {
        StringBuilder sb = new StringBuilder();

        sb.append(node.getFunction()).append('(');
        for (int i = 0; i < node.getArgs().size(); i++) {
            ASTExpression expr = node.getArgs().get(i);
            sb.append(expr.accept(this));

            if (i < (node.getArgs().size() - 1)) {
                sb.append(", ");
            }
        }
        sb.append(')');
        if (printTypes) {
            sb.append("::").append(node.getType().accept(this));
        }
        return sb.toString();
    }

    @Override
    public String visit(ASTFuncDef node) {
        StringBuilder sb = new StringBuilder();

        indent(sb);
        sb.append(node.getName()).append('(');
        for (int i = 0; i < node.getParameters().size(); i++) {
            ASTExpression expr = node.getParameters().get(i);
            sb.append(expr.accept(this));

            if (i < (node.getParameters().size() - 1)) {
                sb.append(", ");
            }
        }
        sb.append(')');

        if (printTypes) {
            sb.append("::").append(node.getReturnType().accept(this));
        }

        sb.append('\n').append(node.getBlock().accept(this));
        indent(sb);
        sb.append("end\n");
        return sb.toString();
    }

    @Override
    public String visit(ASTFuncReturn node) {
        StringBuilder sb = new StringBuilder();
        indent(sb);
        sb.append("return ").append(node.getExpr().accept(this)).append('\n');
        return sb.toString();
    }

    @Override
    public String visit(FuncType node) {
        StringBuilder sb = new StringBuilder();
        if (node.getParameterTypes().isEmpty()) {
            sb.append("()");
        }
        else if (node.getParameterTypes().size() == 1) {
            sb.append(node.getParameterTypes().get(0).accept(this));
        }
        else {
            sb.append('(').append(node.getParameterTypes().get(0).accept(this));
            for (int i = 1; i < node.getParameterTypes().size(); i++) {
                sb.append(", ").append(node.getParameterTypes().get(i).accept(this));
            }
            sb.append(')');
        }
        sb.append(" -> ").append(node.getReturnType().accept(this));
        return sb.toString();
    }

    @Override
    public String visit(UnknownType type) {
        return "?";
    }

    @Override
    public String visit(VariantType type) {
        StringBuilder sb = new StringBuilder();
        sb.append('<');
        for (int i = 0; i < type.getVariants().size(); i++) {
            sb.append(type.getVariants().get(i).accept(this));

            if (i < (type.getVariants().size() - 1)) {
                sb.append(" | ");
            }
        }
        sb.append('>');
        return sb.toString();
    }

    @Override
    public String visit(ASTLoopBreak node) {
        StringBuilder sb = new StringBuilder();
        indent(sb);
        sb.append("break\n");
        return sb.toString();
    }

    @Override
    public String visit(ASTScript node) {
        return "script " + node.getName() + '\n' + node.getBlock().accept(this) + "end\n";
    }

    @Override
    public String visit(ASTProcCall node) {
        return node.getFuncCall().accept(this);
    }

    @Override
    public String visit(ASTVariable node) {
        String result = node.getName();
        if (printTypes) {
            result += "::" + node.getType().accept(this);
        }
        return result;
    }

    @Override
    public String visit(ASTWhileLoop node) {
        StringBuilder sb = new StringBuilder();
        indent(sb);
        sb.append("while ").append(node.getCondition().accept(this));
        sb.append(" do\n").append(node.getBlock().accept(this));
        indent(sb);
        sb.append("end\n");
        return sb.toString();
    }
}
