package myun.AST;

/**
 * Prints the AST in a readable format.
 */
public class MyunPrettyPrinter implements ASTVisitor<String> {
    private int indentLevel;

    public MyunPrettyPrinter() {
        super();
        init();
    }

    private void init() {
        indentLevel = 0;
    }

    public String toString(ASTNode node) {
        init();
        return node.accept(this);
    }

    /**
     * Adds tabs to the current prettyPrint to keep the current indent level.
     */
    private void indent(StringBuilder sb) {
        for (int i = 0; i < indentLevel; i++) {
            sb.append("\t");
        }
    }

    @Override
    public String visit(ASTAssignment node) {
        StringBuilder sb = new StringBuilder();
        indent(sb);

        sb.append(node.getVariable().accept(this));
        sb.append(" = ");
        sb.append(node.getExpr().accept(this));
        sb.append("\n");

        return sb.toString();
    }

    @Override
    public String visit(ASTBasicType node) {
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
        if (node.hasElse()) {
            indent(sb);
            sb.append("else\n").append(node.getElseBlock().accept(this));
        }
        indent(sb);
        sb.append("end\n");

        return sb.toString();
    }

    @Override
    public String visit(ASTCompileUnit node) {
        StringBuilder sb = new StringBuilder();
        node.getFuncDefs().forEach(funcDef -> sb.append(funcDef.accept(this)).append("\n"));
        sb.append(node.getScript().accept(this));
        return sb.toString();
    }

    @Override
    public String visit(ASTConstant node) {
        return node.getValue().toString();
    }

    @Override
    public String visit(ASTDeclaration node) {
        StringBuilder sb = new StringBuilder();
        indent(sb);

        sb.append(node.getVariable().accept(this));
        sb.append(" := ");
        sb.append(node.getExpr().accept(this));
        sb.append("\n");

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

        sb.append(node.getFunction()).append("(");
        for (int i = 0; i < node.getArgs().size(); i++) {
            ASTExpression expr = node.getArgs().get(i);
            sb.append(expr.accept(this));

            if (i < (node.getArgs().size() - 1)) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public String visit(ASTFuncDef node) {
        StringBuilder sb = new StringBuilder();

        indent(sb);
        sb.append(node.getName()).append("(");
        for (int i = 0; i < node.getParameters().size(); i++) {
            ASTExpression expr = node.getParameters().get(i);
            sb.append(expr.accept(this));

            if (i < (node.getParameters().size() - 1)) {
                sb.append(", ");
            }
        }
        sb.append(")");
        node.getReturnType().ifPresent(type -> sb.append("::").append(type.accept(this)));

        sb.append("\n").append(node.getBlock().accept(this));
        indent(sb);
        sb.append("end\n");
        return sb.toString();
    }

    @Override
    public String visit(ASTFuncReturn node) {
        StringBuilder sb = new StringBuilder();
        indent(sb);
        sb.append("return ").append(node.getExpr().accept(this)).append("\n");
        return sb.toString();
    }

    @Override
    public String visit(ASTFuncType node) {
        StringBuilder sb = new StringBuilder();
        if (node.getParameterTypes().isEmpty()) {
            sb.append("()");
        }
        else if (1 == node.getParameterTypes().size()) {
            sb.append(node.getParameterTypes().get(0).accept(this));
        }
        else {
            sb.append("(").append(node.getParameterTypes().get(0).accept(this));
            for (int i = 1; i < node.getParameterTypes().size(); i++) {
                sb.append(", ").append(node.getParameterTypes().get(i).accept(this));
            }
            sb.append(")");
        }
        sb.append(" -> ").append(node.getReturnType().accept(this));
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
        return "script " + node.getName() + "\n" + node.getBlock().accept(this) + "end\n";
    }

    @Override
    public String visit(ASTVariable node) {
        StringBuilder sb = new StringBuilder();
        sb.append(node.getName());
        node.getType().ifPresent(type -> sb.append("::").append(type.accept(this)));
        return sb.toString();
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
