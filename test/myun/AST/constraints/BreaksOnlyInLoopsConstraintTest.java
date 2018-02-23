package myun.AST.constraints;

import myun.AST.ASTGenerator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Tests whether break statements are only allowed within loops.
 */
public class BreaksOnlyInLoopsConstraintTest {
    private ASTGenerator generator;
    private String resPath;

    @Before
    public void setUp() {
        generator = new ASTGenerator();

        File resources = new File("testData/myun/AST/constraints");
        resPath = resources.getAbsolutePath()+'/';
    }

    @Test
    public void breakWithinLoopSucceeds() throws IOException {
        generator.parseFile(resPath+"breakInLoop.myun");
    }

    @Test(expected = BreakOutsideLoopException.class)
    public void funcAndScriptSameNameFails() throws IOException {
        generator.parseFile(resPath+"breakOutsideLoop.myun");
    }
}