package myun.AST;

import myun.type.FuncType;
import myun.type.MyunType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a function definition.
 */
public class ASTFuncDef extends ASTNode {
    private final String name;
    private final List<ASTVariable> parameters;
    private final MyunType returnType;
    private final ASTBlock block;

    /**
     * Creates a new AST function definition.
     *
     * @param sourcePos the position of this node in the source code
     * @param name               The name of the defined function
     * @param params             The parameters of the function
     * @param returnType         The return type of this function
     * @param block              The function body
     */
    public ASTFuncDef(SourcePosition sourcePos,
                      String name, List<ASTVariable> params, MyunType returnType,
                      ASTBlock block) {
        super(sourcePos);
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

    public MyunType getReturnType() {
        return returnType;
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
    public FuncType getType() {
        List<MyunType> paramTypes = parameters.stream().map(ASTExpression::getType).collect(Collectors.toList());
        return new FuncType(paramTypes, returnType);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
