package myun.type.inference;

import myun.AST.*;
import myun.scope.MyunCoreScope;
import myun.scope.ScopeInitializer;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class ExpressionTypeInferrerTest {
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

    @Test
    public void test() throws IOException {
        ASTCompileUnit program = generator.parseFile(resPath+"constantReturn.myun");
        ScopeInitializer.initScopes(program, MyunCoreScope.getInstance());
        TypeInferrer typeInferrer = new TypeInferrer();
        typeInferrer.inferTypes(program);

        System.out.println(prettyPrinter.debug(program));
    }
}