package myun.AST.constraints;

import myun.AST.ASTGenerator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class FunctionAndScriptDifferentNameConstraintTest {
    private ASTGenerator generator;
    private String resPath;

    @Before
    public void setUp() {
        generator = new ASTGenerator();

        File resources = new File("testData/myun/AST/constraints");
        resPath = resources.getAbsolutePath()+'/';
    }

    @Test(expected = FunctionAndScriptSameNameException.class)
    public void funcAndScriptSameNameFails() throws IOException {
        generator.parseFile(resPath+"funcAndScriptSameName.myun");
    }
}