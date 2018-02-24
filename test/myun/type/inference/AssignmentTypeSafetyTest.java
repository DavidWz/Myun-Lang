package myun.type.inference;

import myun.AST.ASTCompileUnit;
import myun.AST.ASTGenerator;
import myun.AST.ASTVariable;
import myun.AST.SourcePosition;
import myun.scope.MyunCoreScope;
import myun.scope.ScopeInitializer;
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
    public void constantAssignmentWithSameTypesSucceeds() throws IOException {
        ASTCompileUnit program = generator.parseFile(resPath+"simpleConstantAssignments.myun");
        ScopeInitializer.initScopes(program, MyunCoreScope.getInstance());
        TypeInferrer typeInferrer = new TypeInferrer();
        typeInferrer.inferTypes(program);

        for (int i = 1; i <= 3; i++) {
            ASTVariable var = program.getScript().getBlock().getScope().getActualVariable(new ASTVariable(new SourcePosition(), "var"+i));
            assertThat("Type of var"+i+" should be a basic type", var.getType(), is(instanceOf(BasicType.class)));
            assertEquals("Type of var"+i+" should Int", PrimitiveTypes.MYUN_INT_NAME, ((BasicType) var.getType()).getName());
        }
    }

    @Test(expected = TypeMismatchException.class)
    public void constantAssignmentWithDifferentTypesFails() throws IOException {
        ASTCompileUnit program = generator.parseFile(resPath+"mistypedConstantAssignments.myun");
        ScopeInitializer.initScopes(program, MyunCoreScope.getInstance());
        TypeInferrer typeInferrer = new TypeInferrer();
        typeInferrer.inferTypes(program);
    }

    @Test
    public void funcCallAssignmentWithSameTypesSucceeds() throws IOException {
        ASTCompileUnit program = generator.parseFile(resPath+"funcCallAssignments.myun");
        ScopeInitializer.initScopes(program, MyunCoreScope.getInstance());
        TypeInferrer typeInferrer = new TypeInferrer();
        typeInferrer.inferTypes(program);

        String[] expected = {PrimitiveTypes.MYUN_INT_NAME,
                PrimitiveTypes.MYUN_FLOAT_NAME,
                PrimitiveTypes.MYUN_FLOAT_NAME,
                PrimitiveTypes.MYUN_INT_NAME};

        for (int i = 0; i < 4; i++) {
            ASTVariable var = program.getScript().getBlock().getScope().getActualVariable(new ASTVariable(new SourcePosition(), "var"+i));
            assertThat("Type of var"+i+" should be a basic type", var.getType(), is(instanceOf(BasicType.class)));
            assertEquals("Type of var"+i+" should be " + expected[i], expected[i], ((BasicType) var.getType()).getName());
        }
    }

    @Test(expected = TypeMismatchException.class)
    public void funcCallAssignmentWithDifferentTypesFails() throws IOException {
        ASTCompileUnit program = generator.parseFile(resPath+"mistypedFuncCallAssignments.myun");
        ScopeInitializer.initScopes(program, MyunCoreScope.getInstance());
        TypeInferrer typeInferrer = new TypeInferrer();
        typeInferrer.inferTypes(program);
    }
}