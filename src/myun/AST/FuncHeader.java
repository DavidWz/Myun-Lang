package myun.AST;

/**
 * Wrapper class for function name + type.
 */
public class FuncHeader {
    private String name;
    private ASTFuncType type;

    public FuncHeader(String name, ASTFuncType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public ASTFuncType getType() {
        return type;
    }

    /**
     * Checks if this function has the same name and parameter types as some other function header.
     *
     * @param other the other function header
     * @return true iff both function headers have the same name and parameter types
     */
    public boolean hasSameNameAndParamTypes(FuncHeader other) {
        if (other == null) return false;
        return name.equals(other.name) && type.getParameterTypes().equals(other.getType().getParameterTypes());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FuncHeader that = (FuncHeader) o;

        return getName().equals(that.getName()) && getType().equals(that.getType());

    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getType().hashCode();
        return result;
    }
}
