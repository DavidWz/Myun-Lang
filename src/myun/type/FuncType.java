package myun.type;

import java.util.List;

/**
 * Represents a function type.
 */
public class FuncType implements MyunType {
    private final List<MyunType> parameterTypes;
    private final MyunType returnType;

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
        if (!returnType.isFullyKnown()) {
            return false;
        }
        return parameterTypes.stream().allMatch(MyunType::isFullyKnown);
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

        if (!parameterTypes.equals(funcType.parameterTypes)) {
            return false;
        }
        return returnType.equals(funcType.returnType);

    }

    @Override
    public int hashCode() {
        int result = parameterTypes.hashCode();
        result = (31 * result) + returnType.hashCode();
        return result;
    }
}