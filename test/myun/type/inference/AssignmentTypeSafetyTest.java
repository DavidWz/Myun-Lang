package myun.type.inference;

import myun.AST.ASTCompileUnit;
import myun.AST.ASTGenerator;
import myun.AST.ASTVariable;
import myun.AST.SourcePosition;
import myun.scope.MyunCoreScope;
import myun.scope.ScopeInitializer;
import myun.scope.VariableInfo;
import myun.type.BasicType;
import myun.type.PrimitiveTypes;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Tests the type safety of assignments and declarations.
 */
public class AssignmentTypeSafetyTest {
    private ASTGenerator generator;
    private String resPath;

    @Before
    public void setUp() {
        generator = new ASTGenerator();

        File resources = new File("testData/myun/type/inference");
        resPath = resources.getAbsolutePath()+'/';
    }

    @Test
    public void simpleAssignmentWithSameTypesSucceeds() throws IOException {
        ASTCompileUnit program = generator.parseFile(resPath+"simpleConstantAssignments.myun");
        ScopeInitializer.initScopes(program, MyunCoreScope.getInstance());
        TypeInferrer typeInferrer = new TypeInferrer();
        typeInferrer.inferTypes(program);

        for (int i = 1; i <= 3; i++) {
            VariableInfo varInfo = program.getScript().getBlock().getScope().getVarInfo(new ASTVariable(new SourcePosition(), "var"+i));
            assertThat("Type of var"+i+" should be a basic type", varInfo.getType(), is(instanceOf(BasicType.class)));
            assertEquals("Type of var"+i+" should Int", PrimitiveTypes.MYUN_INT, ((BasicType) varInfo.getType()).getName());
        }
    }

    @Test(expected = TypeMismatchException.class)
    public void simpleAssignmentWithDifferentTypesFails() throws IOException {
        ASTCompileUnit program = generator.parseFile(resPath+"mistypedConstantAssignments.myun");
        ScopeInitializer.initScopes(program, MyunCoreScope.getInstance());
        TypeInferrer typeInferrer = new TypeInferrer();
        typeInferrer.inferTypes(program);
    }
}