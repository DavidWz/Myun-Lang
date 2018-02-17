package myun.AST.constraints;

import myun.AST.ASTGenerator;
import org.junit.Before;
import org.junit.Test;

public class FunctionHasReturnConstraintTest {
    private ASTGenerator generator;

    @Before
    public void setUp() {
        generator = new ASTGenerator();
    }

    @Test
    public void returnAtEndOfFunctionBodyShouldSucceed() {
        String code = "foo(x, y, z)\n" +
                "    z = z + x*y\n" +
                "    if z is 5 then\n" +
                "        z = 0\n" +
                "    end\n" +
                "    return z\n" +
                "end\n script foo end";
        generator.parseString(code);
    }

    @Test
    public void returnInEveryPathSucceeds() {
        String code = "fib(n)\n" +
                "    if n <= 2 then\n" +
                "        return 1\n" +
                "    else\n" +
                "        return fib(n-2)+fib(n-1)\n" +
                "    end\n" +
                "    x := 1\n" +
                "end\n script foo end";
        generator.parseString(code);
    }

    @Test(expected = ReturnMissingException.class)
    public void notAllBranchesReturnFails() {
        String code = "fib(n)\n" +
                "    if n <= 2 then\n" +
                "        return 1\n" +
                "    else\n" +
                "        y := 5\n" +
                "    end\n" +
                "    x := 1\n" +
                "end\n script foo end";
        generator.parseString(code);
    }

    @Test(expected = ReturnMissingException.class)
    public void returnInLoopBodyFails() {
        String code = "hmm()\n" +
                "    while dunno() do\n" +
                "        return 2\n" +
                "    end\n" +
                "end\n script foo end";
        generator.parseString(code);
    }
}