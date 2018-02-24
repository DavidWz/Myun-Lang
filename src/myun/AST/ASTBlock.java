package myun.AST;

import java.util.List;
import java.util.Optional;

/**
 * Represents a block of code.
 */
public class ASTBlock extends ASTNode {
    private final List<ASTStatement> statements;
    private final ASTFuncReturn funcReturn;
    private final ASTLoopBreak loopBreak;

    /**
     * Creates a new AST block.
     *
     * @param sourcePos the position of this node in the source code
     * @param statements         The statements in this block
     * @param funcReturn         A possible function return statement (can be null)
     * @param loopBreak          A possible loop break (can be null)
     */
    public ASTBlock(SourcePosition sourcePos, List<ASTStatement> statements, ASTFuncReturn funcReturn,
                    ASTLoopBreak loopBreak) {
        super(sourcePos);
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

    public void accept(ASTNonExpressionVisitor visitor) {
        visitor.visit(this);
    }
}
