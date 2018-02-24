package myun.scope;

import myun.AST.ASTCompileUnit;
import myun.AST.ASTGenerator;
import myun.compiler.CodeRunner;
import myun.compiler.MyunCompiler;
import myun.type.inference.TypeInferrer;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Tests (illegal) redefinitions of variables or functions.
 */
public class RedefinitionTest {
    private ASTGenerator generator;
    private TypeInferrer typeInferrer;
    private MyunCompiler compiler;
    private String resPath;

    @Before
    public void setUp() {
        generator = new ASTGenerator();
        typeInferrer = new TypeInferrer();
        compiler = MyunCompiler.getDefaultMyunCompiler();

        File resources = new File("testData/myun/scope");
        resPath = resources.getAbsolutePath()+'/';
    }

    @Test(expected = IllegalRedefineException.class)
    public void illegallyRedefinedFunctionFails() throws IOException, InterruptedException {
        ASTCompileUnit program = generator.parseFile(resPath+"functionRedefinition.myun");
        ScopeInitializer.initScopes(program, MyunCoreScope.getInstance());
        typeInferrer.inferTypes(program);
    }

    @Test(expected = IllegalRedefineException.class)
    public void illegallyRedefinedVariableFails() throws IOException, InterruptedException {
        ASTCompileUnit program = generator.parseFile(resPath+"variableRedefinition.myun");
        ScopeInitializer.initScopes(program, MyunCoreScope.getInstance());
        typeInferrer.inferTypes(program);
    }

    @Test
    public void redeclareInNextScopeSucceeds() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath+"redeclareVariableInNextScope.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        assertEquals("There should be one output line.", 1, lines.length);
        assertEquals("The output should be 48.", "48", lines[0]);
    }
}