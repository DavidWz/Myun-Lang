package myun.AST;

import java.util.List;

/**
 * Represents a function type.
 */
public class ASTFuncType extends ASTType {
    private List<ASTType> parameterTypes;
    private ASTType returnType;

    /**
     * Creates a new AST type.
     *
     * @param lineNumber         The line in the source code where this node starts
     * @param charPositionInLine The character position of this node on its line
     */
    public ASTFuncType(int lineNumber, int charPositionInLine, List<ASTType> parameterTypes, ASTType returnType) {
        super(lineNumber, charPositionInLine);
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
    }

    public List<ASTType> getParameterTypes() {
        return parameterTypes;
    }

    public ASTType getReturnType() {
        return returnType;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ASTFuncType that = (ASTFuncType) o;

        return getParameterTypes().equals(that.getParameterTypes()) && (getReturnType() != null ? getReturnType()
                .equals(that.getReturnType()) : that.getReturnType() == null);
    }

    @Override
    public int hashCode() {
        int result = getParameterTypes().hashCode();
        result = 31 * result + (getReturnType() != null ? getReturnType().hashCode() : 0);
        return result;
    }
}
