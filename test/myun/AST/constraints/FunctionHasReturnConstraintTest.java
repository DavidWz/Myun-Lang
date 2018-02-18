package myun.AST.constraints;

import myun.AST.ASTGenerator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class FunctionHasReturnConstraintTest {
    private ASTGenerator generator;
    private String resPath;

    @Before
    public void setUp() {
        generator = new ASTGenerator();

        File resources = new File("testData/myun/AST/constraints");
        resPath = resources.getAbsolutePath()+'/';
    }

    @Test
    public void returnAtEndOfFunctionBodyShouldSucceed() throws IOException {
        generator.parseFile(resPath+"returnAtEndOfFunction.myun");
    }

    @Test
    public void returnInEveryPathSucceeds() throws IOException {
        generator.parseFile(resPath+"returnInEveryPath.myun");
    }

    @Test(expected = ReturnMissingException.class)
    public void notAllBranchesReturnFails() throws IOException {
        generator.parseFile(resPath+"returnMissesInBranches.myun");
    }

    @Test(expected = ReturnMissingException.class)
    public void returnInLoopBodyFails() throws IOException {
        generator.parseFile(resPath+"returnInLoopBody.myun");
    }
}