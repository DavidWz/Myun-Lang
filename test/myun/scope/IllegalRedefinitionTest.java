package myun.scope;

import myun.AST.ASTCompileUnit;
import myun.AST.ASTGenerator;
import myun.type.inference.TypeInferrer;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Tests illegal redefinitions in the same scope.
 */
public class IllegalRedefinitionTest {
    private ASTGenerator generator;
    private TypeInferrer typeInferrer;
    private String resPath;

    @Before
    public void setUp() {
        generator = new ASTGenerator();
        typeInferrer = new TypeInferrer();

        File resources = new File("testData/myun/scope");
        resPath = resources.getAbsolutePath()+'/';
    }

    @Test(expected = IllegalRedefineException.class)
    public void redefinedFunctionFails() throws IOException, InterruptedException {
        ASTCompileUnit program = generator.parseFile(resPath+"functionRedefinition.myun");
        ScopeInitializer.initScopes(program, MyunCoreScope.getInstance());
        typeInferrer.inferTypes(program);
    }

    @Test(expected = IllegalRedefineException.class)
    public void redefinedVariableFails() throws IOException, InterruptedException {
        ASTCompileUnit program = generator.parseFile(resPath+"variableRedefinition.myun");
        ScopeInitializer.initScopes(program, MyunCoreScope.getInstance());
        typeInferrer.inferTypes(program);
    }
}