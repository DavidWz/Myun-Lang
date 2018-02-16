package myun.AST;

import java.util.List;

/**
 * Wrapper class for function name + parameter types.
 */
public class FuncHeader {
    private final String name;
    private final List<ASTType> parameterTypes;

    public FuncHeader(String name, List<ASTType> parameterTypes) {
        super();
        this.name = name;
        this.parameterTypes = parameterTypes;
    }

    public String getName() {
        return name;
    }

    public List<ASTType> getParameterTypes() {
        return parameterTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((null == o) || (getClass() != o.getClass())) {
            return false;
        }

        FuncHeader that = (FuncHeader) o;

        return getName().equals(that.getName()) && getParameterTypes().equals(that.getParameterTypes());

    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = (31 * result) + getParameterTypes().hashCode();
        return result;
    }
}
