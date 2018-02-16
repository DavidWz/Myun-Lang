package myun.AST;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a function definition.
 */
public class ASTFuncDef extends ASTNode {
    private final String name;
    private final List<ASTVariable> parameters;
    private final ASTType returnType;
    private final ASTBlock block;

    /**
     * Creates a new AST function definition.
     *
     * @param lineNumber         The line in the source code where this node starts
     * @param charPositionInLine The character position of this node on its line
     * @param name               The name of the defined function
     * @param params             The parameters of the function
     * @param returnType         The return type of this function
     * @param block              The function body
     */
    public ASTFuncDef(int lineNumber, int charPositionInLine, String name, List<ASTVariable> params, ASTType
            returnType, ASTBlock block) {
        super(lineNumber, charPositionInLine);
        this.name = name;
        parameters = params;
        this.returnType = returnType;
        this.block = block;
    }

    public String getName() {
        return name;
    }

    public List<ASTVariable> getParameters() {
        return parameters;
    }

    public Optional<ASTType> getReturnType() {
        return Optional.ofNullable(returnType);
    }

    public ASTBlock getBlock() {
        return block;
    }

    /**
     * Returns the type of this function.
     * All parameter types and the return type have
     *
     * @return the type of this function
     */
    public Optional<ASTFuncType> getType() {
        List<ASTType> paramTypes = new ArrayList<>();
        for (ASTVariable param : parameters) {
            Optional<ASTType> pT = param.getType();
            if (pT.isPresent()) {
                paramTypes.add(pT.get());
            }
            else {
                return Optional.empty();
            }
        }

        if (null == returnType) {
            return Optional.empty();
        }
        else {
            return Optional.of(new ASTFuncType(getLine(), getCharPositionInLine(), paramTypes, returnType));
        }
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
