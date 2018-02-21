package myun.compiler;

import com.sun.org.apache.bcel.internal.classfile.Code;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Tests whether the arithmetic functions from the core scope work as intended.
 */
public class ArithmeticTest {
    private MyunCompiler compiler;
    private String resPath;

    @Before
    public void setUp() {
        File resources = new File("testData/myun/compiler/arithmetic");
        resPath = resources.getAbsolutePath() + '/';
        compiler = MyunCompiler.getDefaultMyunCompiler();
    }

    private void compareResults(String[] lines, int[] intResults, double[] floatResults) {
        int numRes = intResults.length + floatResults.length;

        assertThat("There should be "+numRes+" output lines.", numRes, is(lines.length));

        for (int i = 0; i < intResults.length; i++) {
            int res = Integer.parseInt(lines[i]);
            assertEquals((i+1)+"th int result should be " + intResults[i], intResults[i], res);
        }

        for (int i = 0; i < floatResults.length; i++) {
            double res = Double.parseDouble(lines[intResults.length + i]);
            assertEquals((i+1)+"th float result should be " + floatResults[i], floatResults[i], res, 1.0e-8);
        }
    }

    @Test
    public void testAddition() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "addition.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);
        int[] intResults = {10, -3893, 356731, 15};
        double[] floatResults = {3.0, 66.8, -302.399, 3.0e-5, 3.0e7, 231.101};
        compareResults(lines, intResults, floatResults);
    }

    @Test
    public void testSubtraction() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "subtraction.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        int[] intResults = {-1, 227, -21298, 332, -102};
        double[] floatResults = {2.29, 0.01, 30935.0, -496.2, 18.1};
        compareResults(lines, intResults, floatResults);
    }

    @Test
    public void testMultiplication() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "multiplication.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        int[] intResults = {15, -80, -291, 153683, 400};
        double[] floatResults = {0.12, -61.2, 0.0, 21.78, -1.089};
        compareResults(lines, intResults, floatResults);
    }

    @Test
    public void testDivision() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "division.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        int[] intResults = {5, -5, 0, 3, 133};
        double[] floatResults = {-2.0, 7.70247933e-6, 0.0, 3.75037503e-4};
        int numRes = intResults.length + floatResults.length + 1;

        assertThat("There should be "+numRes+" output lines.", numRes, is(lines.length));

        for (int i = 0; i < intResults.length; i++) {
            int res = Integer.parseInt(lines[i]);
            assertEquals((i+1)+"th int result should be " + intResults[i], intResults[i], res);
        }

        for (int i = 0; i < floatResults.length; i++) {
            double res = Double.parseDouble(lines[intResults.length + i]);
            assertEquals((i+1)+"th float result should be " + floatResults[i], floatResults[i], res, 1.0e-8);
        }

        int last = floatResults.length;
        assertEquals((last+1)+"th float result should be inf", "inf", lines[intResults.length+last]);
    }

    @Test
    public void divideByZero() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "divideBy0.myun");
        ExecutionResult result = CodeRunner.runMyunFile(outputFile);
        assertNotEquals("Program should terminate with some error code because of erroneous Arithmetic Operation",
                0,
                result.getExitStatus());
        assertEquals("Output should be empty.", "", result.getOutput());
    }

    @Test
    public void testModulo() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "modulo.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        int[] intResults = {2, -7, 3, -92};
        double[] floatResults = {0.1, -0.2, 9e-4, 0};
        compareResults(lines, intResults, floatResults);
    }
}