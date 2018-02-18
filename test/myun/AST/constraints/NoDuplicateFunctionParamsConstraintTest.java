package myun.AST.constraints;

import myun.AST.ASTGenerator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class NoDuplicateFunctionParamsConstraintTest {
    private ASTGenerator generator;
    private String resPath;

    @Before
    public void setUp() {
        generator = new ASTGenerator();

        File resources = new File("testData/myun/AST/constraints");
        resPath = resources.getAbsolutePath()+'/';
    }

    @Test
    public void noDuplicateParamsSucceeds() throws IOException {
        generator.parseFile(resPath+"noDuplicateParam.myun");
    }

    @Test(expected = DuplicateParametersException.class)
    public void duplicateParamsFails() throws IOException {
        generator.parseFile(resPath+"duplicateParam.myun");
    }

    @Test(expected = DuplicateParametersException.class)
    public void duplicateParamsDifferentTypesFails() throws IOException {
        generator.parseFile(resPath+"duplicateParamDifferentType.myun");
    }
}