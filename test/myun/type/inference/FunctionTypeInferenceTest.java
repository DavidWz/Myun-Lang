package myun.type.inference;

import myun.AST.*;
import myun.scope.MyunCoreScope;
import myun.scope.Scope;
import myun.scope.ScopeInitializer;
import myun.type.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests the inference of function parameter and return types.
 */
public class FunctionTypeInferenceTest {
    private ASTGenerator generator;
    private String resPath;
    private MyunPrettyPrinter prettyPrinter;

    @Before
    public void setUp() {
        generator = new ASTGenerator();
        prettyPrinter = new MyunPrettyPrinter();

        File resources = new File("testData/myun/type/inference");
        resPath = resources.getAbsolutePath()+'/';
    }

    private void assertFunctionType(Scope scope, String name, List<MyunType> params, MyunType returnType) {
        FuncHeader header = new FuncHeader(name, params);
        MyunType type = new FuncType(params, returnType);
        assertTrue("Function "+name+" should be declared.", scope.isDeclared(header));
        MyunType inferedType = scope.getFunctionInfo(header, new SourcePosition()).getType();
        assertEquals(name+"'s type should be "+prettyPrinter.toString(type), type, inferedType);
    }

    @Test
    public void inferFunctionTypeFromConstantReturnSucceeds() throws IOException {
        ASTCompileUnit program = generator.parseFile(resPath+"constantReturn.myun");
        ScopeInitializer.initScopes(program, MyunCoreScope.getInstance());
        TypeInferrer typeInferrer = new TypeInferrer();
        typeInferrer.inferTypes(program);

        assertEquals("There should be 3 function definitions.", 3, program.getFuncDefs().size());

        // test foo's type
        List<MyunType> fooParams = Collections.singletonList(PrimitiveTypes.MYUN_INT);
        assertFunctionType(program.getScope(), "foo", fooParams, PrimitiveTypes.MYUN_INT);

        // test bar's type
        List<MyunType> barParams = Arrays.asList(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT);
        assertFunctionType(program.getScope(), "bar", barParams, PrimitiveTypes.MYUN_FLOAT);

        // test foo's type
        List<MyunType> fizzParams = Collections.emptyList();
        assertFunctionType(program.getScope(), "fizz", fizzParams, PrimitiveTypes.MYUN_BOOL);
    }

    @Test
    public void inferRecursiveFunctionTypeSucceeds() throws IOException {
        ASTCompileUnit program = generator.parseFile(resPath+"recursiveFunctionType.myun");
        ScopeInitializer.initScopes(program, MyunCoreScope.getInstance());
        TypeInferrer typeInferrer = new TypeInferrer();
        typeInferrer.inferTypes(program);

        assertEquals("There should be 1 function definition.", 1, program.getFuncDefs().size());

        List<MyunType> fibParams = Collections.singletonList(PrimitiveTypes.MYUN_INT);
        assertFunctionType(program.getScope(), "fib", fibParams, PrimitiveTypes.MYUN_INT);
    }

    @Test
    public void inferCyclicDependentFunctionTypeSucceeds() throws IOException {
        ASTCompileUnit program = generator.parseFile(resPath+"cyclicFunctionDefinition.myun");
        ScopeInitializer.initScopes(program, MyunCoreScope.getInstance());
        TypeInferrer typeInferrer = new TypeInferrer();
        typeInferrer.inferTypes(program);

        assertEquals("There should be 2 function definitions.", 2, program.getFuncDefs().size());
        List<MyunType> params = Collections.singletonList(PrimitiveTypes.MYUN_INT);

        assertFunctionType(program.getScope(), "even", params, PrimitiveTypes.MYUN_BOOL);
        assertFunctionType(program.getScope(), "odd", params, PrimitiveTypes.MYUN_BOOL);
    }

    @Test
    public void inferTypeOfRedefinedFunction() throws IOException {
        ASTCompileUnit program = generator.parseFile(resPath+"redefineForOtherType.myun");
        ScopeInitializer.initScopes(program, MyunCoreScope.getInstance());
        TypeInferrer typeInferrer = new TypeInferrer();
        typeInferrer.inferTypes(program);

        assertEquals("There should be 4 function definitions.", 4, program.getFuncDefs().size());

        List<MyunType> oneInt = Collections.singletonList(PrimitiveTypes.MYUN_INT);
        List<MyunType> oneFloat = Collections.singletonList(PrimitiveTypes.MYUN_FLOAT);

        assertFunctionType(program.getScope(), "inc", oneInt, PrimitiveTypes.MYUN_INT);
        assertFunctionType(program.getScope(), "inc", oneFloat, PrimitiveTypes.MYUN_FLOAT);
        assertFunctionType(program.getScope(), "approx", oneInt, PrimitiveTypes.MYUN_FLOAT);
        assertFunctionType(program.getScope(), "approx", oneFloat, PrimitiveTypes.MYUN_INT);
    }

    @Test
    public void inferMultipleTypes() throws IOException {
        ASTCompileUnit program = generator.parseFile(resPath+"multiplePossibleTypes.myun");
        ScopeInitializer.initScopes(program, MyunCoreScope.getInstance());
        TypeInferrer typeInferrer = new TypeInferrer();
        typeInferrer.inferTypes(program);

        assertFunctionType(program.getScope(), "identity",
                Collections.singletonList(PrimitiveTypes.MYUN_INT), PrimitiveTypes.MYUN_INT);
        assertFunctionType(program.getScope(), "identity",
                Collections.singletonList(PrimitiveTypes.MYUN_FLOAT), PrimitiveTypes.MYUN_FLOAT);
        assertFunctionType(program.getScope(), "identity",
                Collections.singletonList(PrimitiveTypes.MYUN_BOOL), PrimitiveTypes.MYUN_BOOL);

        assertFunctionType(program.getScope(), "myAdd",
                Arrays.asList(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT), PrimitiveTypes.MYUN_INT);
        assertFunctionType(program.getScope(), "myAdd",
                Arrays.asList(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT), PrimitiveTypes.MYUN_FLOAT);
    }
}