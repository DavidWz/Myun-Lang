package myun.AST.constraints;

import myun.AST.ASTGenerator;
import org.junit.Before;
import org.junit.Test;

public class NoDuplicateFunctionParamsConstraintTest {
    private ASTGenerator generator;

    @Before
    public void setUp() {
        generator = new ASTGenerator();
    }

    @Test
    public void noDuplicateParamsSucceeds() {
        String code = "foo(x, y)\n" +
                "    return y+2\n" +
                "end script bar end";
        generator.parseString(code);
    }

    @Test(expected = DuplicateParametersException.class)
    public void duplicateParamsFails() {
        String code = "foo(x, x)\n" +
                "    return x\n" +
                "end script bar end";
        generator.parseString(code);
    }

    @Test(expected = DuplicateParametersException.class)
    public void duplicateParamsDifferentTypesFails() {
        String code = "foo(x::Int, x::Float)\n" +
                "    return x\n" +
                "end script bar end";
        generator.parseString(code);
    }
}