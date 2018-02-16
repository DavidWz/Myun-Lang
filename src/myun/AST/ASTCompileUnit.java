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
     * @param lineNumber         The line in the source code where this node starts
     * @param charPositionInLine The character position of this node on its line
     * @param funcDefs           The function declarations
     * @param script             The script part of this compile unit.
     */
    public ASTCompileUnit(int lineNumber, int charPositionInLine, Collection<ASTFuncDef> funcDefs, ASTScript script) {
        super(lineNumber, charPositionInLine);
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
}
