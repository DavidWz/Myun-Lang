package myun.scope;

import myun.AST.ASTFuncType;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FuncHeader that = (FuncHeader) o;

        if (!getName().equals(that.getName())) return false;
        return getType().equals(that.getType());

    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getType().hashCode();
        return result;
    }
}
