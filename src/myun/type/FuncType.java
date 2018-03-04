package myun.type;

import java.util.List;

/**
 * Represents a function type.
 */
public class FuncType implements MyunType {
    private final List<MyunType> parameterTypes;
    private MyunType returnType;

    /**
     * Creates a new AST type.
     *
     * @param parameterTypes the types of the parameters
     * @param returnType the return type
     */
    public FuncType(List<MyunType> parameterTypes, MyunType returnType) {
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
    }

    public List<MyunType> getParameterTypes() {
        return parameterTypes;
    }

    public MyunType getReturnType() {
        return returnType;
    }

    @Override
    public boolean isFullyKnown() {
        return returnType.isFullyKnown() && parameterTypes.stream().allMatch(MyunType::isFullyKnown);
    }

    @Override
    public <T> T accept(TypeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }

        FuncType funcType = (FuncType) o;

        return parameterTypes.equals(funcType.parameterTypes) && returnType.equals(funcType.returnType);

    }

    @Override
    public int hashCode() {
        int result = parameterTypes.hashCode();
        result = (31 * result) + returnType.hashCode();
        return result;
    }

    public void setReturnType(MyunType returnType) {
        this.returnType = returnType;
    }
}
