package myun.type;

import java.util.List;

/**
 * Wrapper class for function/procedure name + parameter types.
 */
public class FuncHeader {
    private final String name;
    private final List<MyunType> parameterTypes;

    public FuncHeader(String name, List<MyunType> parameterTypes) {
        this.name = name;
        this.parameterTypes = parameterTypes;
    }

    public String getName() {
        return name;
    }

    public List<MyunType> getParameterTypes() {
        return parameterTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }

        FuncHeader that = (FuncHeader) o;

        return name.equals(that.name) && parameterTypes.equals(that.parameterTypes);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = (31 * result) + parameterTypes.hashCode();
        return result;
    }
}
