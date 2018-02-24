package myun.AST;

import java.util.Collection;

/**
 * Top-level program AST node.
 */
public class ASTCompileUnit extends ASTNode {
    private final Collection<ASTFuncDef> funcDefs;
    private final ASTScript script;

    /**
     * Creates a new AST compile unit.
     *
     * @param sourcePos the position of this node in the source code
     * @param funcDefs           The function declarations
     * @param script             The script part of this compile unit.
     */
    public ASTCompileUnit(SourcePosition sourcePos, Collection<ASTFuncDef> funcDefs, ASTScript script) {
        super(sourcePos);
        this.funcDefs = funcDefs;
        this.script = script;
    }

    public Collection<ASTFuncDef> getFuncDefs() {
        return funcDefs;
    }

    public ASTScript getScript() {
        return script;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public void accept(ASTNonExpressionVisitor visitor) {
        visitor.visit(this);
    }
}
