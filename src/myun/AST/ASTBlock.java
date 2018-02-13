package myun.AST;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a block of code.
 */
public class ASTBlock extends ASTNode {
    private List<ASTStatement> statements;
    private ASTFuncReturn funcReturn;
    private ASTLoopBreak loopBreak;

    /**
     * Creates a new AST block with no statements.
     *
     * @param lineNumber         The line in the source code where this node starts
     * @param charPositionInLine The character position of this node on its line
     */
    public ASTBlock(int lineNumber, int charPositionInLine) {
        super(lineNumber, charPositionInLine);
        this.statements = new ArrayList<>();
        this.funcReturn = null;
        this.loopBreak = null;
    }

    /**
     * Creates a new AST block.
     *
     * @param lineNumber         The line in the source code where this node starts
     * @param charPositionInLine The character position of this node on its line
     * @param statements         The statements in this block
     * @param funcReturn         A possible function return statement (can be null)
     * @param loopBreak          A possible loop break (can be null)
     */
    public ASTBlock(int lineNumber, int charPositionInLine, List<ASTStatement> statements, ASTFuncReturn funcReturn,
                    ASTLoopBreak loopBreak) {
        super(lineNumber, charPositionInLine);
        this.statements = statements;
        this.funcReturn = funcReturn;
        this.loopBreak = loopBreak;
    }

    public List<ASTStatement> getStatements() {
        return statements;
    }

    public Optional<ASTFuncReturn> getFuncReturn() {
        return Optional.ofNullable(funcReturn);
    }

    public Optional<ASTLoopBreak> getLoopBreak() {
        return Optional.ofNullable(loopBreak);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
